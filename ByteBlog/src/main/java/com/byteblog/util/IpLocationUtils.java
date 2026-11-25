package com.byteblog.util;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.lionsoul.ip2region.service.Ip2Region;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * ip工具方法
 */
@Component
public class IpLocationUtils {

    @Resource
    private Ip2Region ip2Region;

    public String getRealLocation(String ip) {
        // 本地/内网 IP 直接拦截
        if (ip == null || ip.startsWith("127.") || ip.startsWith("192.168.")
                || ip.equals("0:0:0:0:0:0:0:1") || ip.equals("localhost")) {
            return "本地ip";
        }

        try {
            // 使用 ip2region 搜索
            String region = ip2Region.search(ip);

            //
            if (region != null && !region.isEmpty()) {
                String[] parts = region.split("\\|");
                if (parts.length >= 4 && !"0".equals(parts[2])) {
                    return parts[2] + parts[3];
                }
            }
            return "未知星球";
        } catch (Exception e) {
            return "未知星球";
        }
    }
    public static String getRealIp() {
        // 获取当前线程绑定的 Request 对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return "127.0.0.1"; // 如果拿不到（比如在异步线程里），默认返回本地
        }
        HttpServletRequest request = attributes.getRequest();
        // 尝试从各种代理请求头里获取
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多级代理时取第一个 IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }
}
