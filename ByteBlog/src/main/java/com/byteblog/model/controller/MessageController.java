package com.byteblog.model.controller;

import com.byteblog.common.Result;
import com.byteblog.model.dto.MessageDTO;
import com.byteblog.model.service.MessageService;
import com.byteblog.model.vo.MessageVo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 留言板控制器
 */
@RequiredArgsConstructor
@RequestMapping("/api/messages")
@RestController
public class MessageController {
    // 注入
    private final MessageService messageService;

    @PostMapping
    public Result<Void> insert(@Valid @RequestBody MessageDTO dto, HttpServletRequest request){
        String realIp = getRealIp(request);
        messageService.insertMessage(dto,realIp);
        return Result.success("留言已收到，审核通过后就会显示出来~",null);
    }
    @GetMapping
    public Result<List<MessageVo>> ListAll(){
        return Result.success(messageService.listMessages());
    }

    @GetMapping("/admin/all")
    public Result<List<MessageVo>> adminList(){

        return Result.success(messageService.listAllMessages());
    }

    /**
     * 只查「AI 判不了、等人工」的留言。
     * 后台可以拿它做红点提醒：数量大于 0 就说明有东西等着人决定。
     */
    @GetMapping("/admin/pending")
    public Result<List<MessageVo>> pendingList(){
        return Result.success(messageService.listPendingMessages());
    }
    @PutMapping("/{id}/top")
    public Result<Void> top(@PathVariable Long id,@RequestParam Boolean isTop){
        messageService.topMessage(id,isTop);
        return Result.success("🔝操作成功！",null);
    }

    @PutMapping("/{id}/audit")
    public Result<Void> audit(@PathVariable Long id,@RequestParam Integer status){
        messageService.auditMessage(id,status);
        return Result.success("审核成功！",null);
    }
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id){
        messageService.deleteMessage(id);
        return Result.success("操作成功！",null);
    }

    // 自定义方法 防止Nginx反向到底找不到IP地址
    private String getRealIp(HttpServletRequest request) {
        // 优先从代理请求头里获取（应对 Nginx 等反向代理）
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        // 如果没代理，直接获取客户端 IP
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 如果是多级代理，X-Forwarded-For 会是一串 IP，用逗号隔开，取第一个就是真实 IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
