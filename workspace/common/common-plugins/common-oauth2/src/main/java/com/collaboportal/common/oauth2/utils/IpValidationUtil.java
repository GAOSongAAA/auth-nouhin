package com.collaboportal.common.oauth2.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * IP地址驗證工具類
 * 提供IP地址匹配、範圍檢查和CIDR格式驗證功能
 */
public class IpValidationUtil {

    private static final Logger logger = LoggerFactory.getLogger(IpValidationUtil.class);

    private static final String[] IP_HEADER_CANDIDATES = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
    };

    /**
     * 從HttpServletRequest中獲取真實的客戶端IP地址
     * 考慮代理服務器和負載均衡器的情況
     * 
     * @param request HTTP請求對象
     * @return 客戶端IP地址
     */
    public static String getClientIpAddress(HttpServletRequest request) {
        for (String header : IP_HEADER_CANDIDATES) {
            String ipList = request.getHeader(header);
            if (ipList != null && !ipList.isEmpty() && !"unknown".equalsIgnoreCase(ipList)) {
                // X-Forwarded-For可能包含多個IP，取第一個
                String ip = ipList.split(",")[0].trim();
                if (isValidIpAddress(ip)) {
                    logger.debug("從header [{}] 取得客戶端IP: {}", header, ip);
                    return ip;
                }
            }
        }

        String remoteAddr = request.getRemoteAddr();
        logger.debug("使用RemoteAddr取得客戶端IP: {}", remoteAddr);
        return remoteAddr;
    }

    /**
     * 驗證IP地址是否在允許列表中
     * 
     * @param clientIp   客戶端IP地址
     * @param allowedIps 允許的IP地址列表
     * @return 是否通過驗證
     */
    public static boolean isIpAllowed(String clientIp, String[] allowedIps) {
        if (allowedIps == null || allowedIps.length == 0) {
            logger.debug("未設定IP限制，允許所有IP存取");
            return true;
        }

        if (clientIp == null || clientIp.isEmpty()) {
            logger.warn("無法取得客戶端IP地址");
            return false;
        }

        logger.debug("驗證客戶端IP [{}] 是否在允許列表中: {}", clientIp, Arrays.toString(allowedIps));

        for (String allowedIp : allowedIps) {
            if (allowedIp == null || allowedIp.trim().isEmpty()) {
                continue;
            }

            allowedIp = allowedIp.trim();

            try {
                if (matchesIpPattern(clientIp, allowedIp)) {
                    logger.info("客戶端IP [{}] 匹配允許的IP模式 [{}]", clientIp, allowedIp);
                    return true;
                }
            } catch (Exception e) {
                logger.error("驗證IP模式 [{}] 時發生錯誤: {}", allowedIp, e.getMessage());
            }
        }

        logger.warn("客戶端IP [{}] 不在允許列表中: {}", clientIp, Arrays.toString(allowedIps));
        return false;
    }

    /**
     * 檢查IP地址是否匹配指定的模式
     * 支持單個IP、IP範圍和CIDR格式
     * 
     * @param clientIp 客戶端IP
     * @param pattern  IP模式
     * @return 是否匹配
     */
    private static boolean matchesIpPattern(String clientIp, String pattern) {
        // 單個IP直接比較
        if (pattern.equals(clientIp)) {
            return true;
        }

        // CIDR格式 (例如: 192.168.1.0/24)
        if (pattern.contains("/")) {
            return matchesCidr(clientIp, pattern);
        }

        // IP範圍格式 (例如: 192.168.1.1-192.168.1.100)
        if (pattern.contains("-")) {
            return matchesIpRange(clientIp, pattern);
        }

        // 萬用字符模式 (例如: 192.168.1.*)
        if (pattern.contains("*")) {
            return matchesWildcard(clientIp, pattern);
        }

        return false;
    }

    /**
     * 檢查IP是否在CIDR範圍內
     * 
     * @param clientIp 客戶端IP
     * @param cidr     CIDR格式的網路地址
     * @return 是否在範圍內
     */
    private static boolean matchesCidr(String clientIp, String cidr) {
        try {
            String[] parts = cidr.split("/");
            if (parts.length != 2) {
                return false;
            }

            InetAddress targetAddr = InetAddress.getByName(clientIp);
            InetAddress networkAddr = InetAddress.getByName(parts[0]);
            int prefixLength = Integer.parseInt(parts[1]);

            byte[] targetBytes = targetAddr.getAddress();
            byte[] networkBytes = networkAddr.getAddress();

            if (targetBytes.length != networkBytes.length) {
                return false;
            }

            int bytesToCheck = prefixLength / 8;
            int bitsToCheck = prefixLength % 8;

            // 檢查完整的字節
            for (int i = 0; i < bytesToCheck; i++) {
                if (targetBytes[i] != networkBytes[i]) {
                    return false;
                }
            }

            // 檢查剩餘的位
            if (bitsToCheck > 0 && bytesToCheck < targetBytes.length) {
                int mask = 0xFF << (8 - bitsToCheck);
                if ((targetBytes[bytesToCheck] & mask) != (networkBytes[bytesToCheck] & mask)) {
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            logger.error("CIDR格式驗證錯誤: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 檢查IP是否在指定範圍內
     * 
     * @param clientIp 客戶端IP
     * @param range    IP範圍 (格式: start-end)
     * @return 是否在範圍內
     */
    private static boolean matchesIpRange(String clientIp, String range) {
        try {
            String[] parts = range.split("-");
            if (parts.length != 2) {
                return false;
            }

            long clientIpLong = ipToLong(clientIp);
            long startIpLong = ipToLong(parts[0].trim());
            long endIpLong = ipToLong(parts[1].trim());

            return clientIpLong >= startIpLong && clientIpLong <= endIpLong;
        } catch (Exception e) {
            logger.error("IP範圍驗證錯誤: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 檢查IP是否匹配萬用字符模式
     * 
     * @param clientIp 客戶端IP
     * @param pattern  包含萬用字符的模式
     * @return 是否匹配
     */
    private static boolean matchesWildcard(String clientIp, String pattern) {
        String[] clientParts = clientIp.split("\\.");
        String[] patternParts = pattern.split("\\.");

        if (clientParts.length != 4 || patternParts.length != 4) {
            return false;
        }

        for (int i = 0; i < 4; i++) {
            if (!"*".equals(patternParts[i]) && !clientParts[i].equals(patternParts[i])) {
                return false;
            }
        }

        return true;
    }

    /**
     * 將IP地址轉換為長整數
     * 
     * @param ip IP地址字符串
     * @return 長整數表示
     */
    private static long ipToLong(String ip) {
        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            throw new IllegalArgumentException("無效的IP地址格式: " + ip);
        }

        long result = 0;
        for (int i = 0; i < 4; i++) {
            result = (result << 8) + Integer.parseInt(parts[i]);
        }
        return result;
    }

    /**
     * 驗證IP地址格式是否正確
     * 
     * @param ip IP地址字符串
     * @return 是否為有效IP地址
     */
    private static boolean isValidIpAddress(String ip) {
        try {
            InetAddress.getByName(ip);
            return true;
        } catch (UnknownHostException e) {
            return false;
        }
    }
}