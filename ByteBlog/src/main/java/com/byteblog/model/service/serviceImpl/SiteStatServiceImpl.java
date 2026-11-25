package com.byteblog.model.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.byteblog.model.entity.SiteStat;
import com.byteblog.model.mapper.SiteStatMapper;
import com.byteblog.model.service.SiteStatService;
import com.byteblog.model.vo.SiteStatVo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 统计网站业务实体类
 */
@Service
@RequiredArgsConstructor
public class SiteStatServiceImpl implements SiteStatService {

    private final SiteStatMapper siteStatMapper;
    private final StringRedisTemplate redisTemplate;

    @Override
    public SiteStatVo getSiteStats() {
        SiteStatVo vo = new SiteStatVo();

        // 基础数据
        vo.setArticleCount(siteStatMapper.countArticles());
        vo.setProjectCount(siteStatMapper.countProjects());
        vo.setMessageCount(siteStatMapper.countMessages());
        // 运行天数：以站点首次初始化时间（blog_info.create_time）为起点
        LocalDateTime launch = siteStatMapper.selectLaunchTime();
        if (launch == null) {
            // blog_info 还没初始化时兜底，避免 NPE，也不要凭空造一个历史日期
            launch = LocalDateTime.now();
        }
        // +1 表示建站当天算第 1 天；用 max 兜住服务器时间早于建站时间（时区/时钟回拨）导致的负数
        long runDays = ChronoUnit.DAYS.between(launch.toLocalDate(), LocalDate.now()) + 1;
        vo.setRunDays(Math.max(runDays, 1));
        // 访客总数 = 数据库历史 UV + Redis 今日 UV
        Long historyUv = siteStatMapper.selectTotalUv();
        if (historyUv == null) historyUv = 0L;
        String today = LocalDate.now().toString();
        Long todayUv = redisTemplate.opsForSet().size("visit:uv:" + today);
        vo.setVisitorCount(historyUv + (todayUv != null ? todayUv : 0));
        // 近 7 天趋势
        LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);
        List<SiteStat> trendList = siteStatMapper.selectList(
                new LambdaQueryWrapper<SiteStat>()
                        .ge(SiteStat::getStatDate, sevenDaysAgo)
                        .orderByAsc(SiteStat::getStatDate)
        );
        vo.setTrendList(trendList);

        return vo;
    }
}
