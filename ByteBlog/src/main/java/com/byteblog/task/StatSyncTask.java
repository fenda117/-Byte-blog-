package com.byteblog.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.byteblog.model.entity.Article;
import com.byteblog.model.entity.Project;
import com.byteblog.model.entity.SiteStat;
import com.byteblog.model.mapper.ArticleMapper;
import com.byteblog.model.mapper.ProjectMapper;
import com.byteblog.model.mapper.SiteStatMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 定时异步任务
 * 为防止Redis数据丢失，需要定时把 Redis里记录的所有增量，刷回 MySQL。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StatSyncTask {
    // 注入
    private final StringRedisTemplate redisTemplate;
    private final ProjectMapper projectMapper;
    private final ArticleMapper articleMapper;
    private final SiteStatMapper siteStatMapper;
    // 声明Redis 脚本对象，泛型 为<List>
    private DefaultRedisScript<List> syncViewsScript;

    @PostConstruct
    public void init() {
        // 实例化
        syncViewsScript = new DefaultRedisScript<>();
        //指定脚本位置
        syncViewsScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/sync_views.lua")));
        //脚本执行完整后按List解析
        syncViewsScript.setResultType(List.class);
    }

    // 每5分钟执行一次刷新(项目浏览量刷新)
    @Scheduled(cron = "0 */5 * * * ?")
    public void execute() {
        String hashKey = "view:project";

        List<String> result = redisTemplate.execute(
                syncViewsScript,  // 要执行的脚本对象
                Collections.singletonList(hashKey)); // 传给脚本的 KEYS[1]
        // 如果浏览量为空直接return，不占内存
        if (result == null || result.isEmpty()) {
            return;
        }
        // 打印日志
        log.info("开始同步项目浏览量数据，共 {} 个字段", result.size() / 2);
        // 遍历Lua脚本，每次步长为2
        for (int i = 0; i < result.size(); i += 2) {
            String projectId = String.valueOf(result.get(i));
            String increment = String.valueOf(result.get(i + 1));
            UpdateWrapper<Project> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", projectId)
                    .setSql("view_count = view_count + " + increment);
            projectMapper.update(null, updateWrapper);
        }

        // 打印完成日志
        log.info("项目浏览量同步完成！");
    }

    // 每五分钟执行一次刷新(文章浏览量刷新)
    @Scheduled(cron = "0 */5 * * * ?")
    public void syncArticleViews() {
        String hashKey = "view:article";
        List<String> result = redisTemplate.execute(
                syncViewsScript,
                Collections.singletonList(hashKey)
        );
        if (result == null || result.isEmpty()) return;

        log.info("开始同步文章浏览量，共 {} 个字段", result.size() / 2);
        for (int i = 0; i < result.size(); i += 2) {
            String articleId = String.valueOf(result.get(i));
            String increment = String.valueOf(result.get(i + 1));
            UpdateWrapper<Article> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", articleId)
                    .setSql("view_count = view_count + " + increment);
            articleMapper.update(null, updateWrapper);
        }

        // 打印完成日志
        log.info("文章浏览量同步完成！");
    }
    // 每五分钟执行一次刷新(Redis 读取到的 pv/uv 同步到 MySql)
    @Scheduled(cron = "0 */5 * * * ?")
    public void syncSiteVisitStats() {
        String today = LocalDate.now().toString();
        String pvKey = "visit:pv:" + today;
        String uvKey = "visit:uv:" + today;

        String pvStr = redisTemplate.opsForValue().get(pvKey);
        Long uv = redisTemplate.opsForSet().size(uvKey);

        int pv = pvStr != null ? Integer.parseInt(pvStr) : 0;
        int uvCount = uv != null ? uv.intValue() : 0;

        // 如果今天有访问，才去更新数据库
        if (pv > 0 || uvCount > 0) {
            // 查一下今天是不是已经有记录了
            SiteStat stat = siteStatMapper.selectOne(
                    new LambdaQueryWrapper<SiteStat>().eq(SiteStat::getStatDate, LocalDate.now())
            );

            if (stat == null) {
                // 没记录，插入新数据
                stat = new SiteStat();
                stat.setStatDate(LocalDate.now());
                stat.setPv(pv);
                stat.setUv(uvCount);
                siteStatMapper.insert(stat);
            } else {
                // 有记录了，直接覆盖更新
                stat.setPv(pv);
                stat.setUv(uvCount);
                siteStatMapper.updateById(stat);
            }
        }
    }
}
