package com.collaboportal.common.utils;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Function;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 敏感信息掩碼工具類
 * 用於在日誌輸出中對敏感信息進行掩碼處理，保護用戶隱私和滿足合規要求
 */
public class SensitiveDataMaskUtil {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveDataMaskUtil.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 掩碼字符
    private static final String REPLACEMENT_PATTERN = "****";

    /**
     * 敏感信息模式枚舉
     */
    public enum SensitiveDataPattern {
        // 密碼相關
        PASSWORD("(?i)(password|pwd|pass|secret|token|key)\\s*[:=]\\s*[\"']?([^\\s\"',}]+)",
                "密碼信息", SensitiveDataMaskUtil::maskPassword),

        // 郵箱地址
        EMAIL("([a-zA-Z0-9._%+-]+)@([a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})",
                "郵箱地址", SensitiveDataMaskUtil::maskEmail),

        // JWT Token
        JWT_TOKEN("\\b[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]*\\b",
                "JWT令牌", SensitiveDataMaskUtil::maskJwtToken);

        private final Pattern pattern;
        private final String description;
        private final Function<String, String> maskFunction;

        SensitiveDataPattern(String regex, String description, Function<String, String> maskFunction) {
            this.pattern = Pattern.compile(regex);
            this.description = description;
            this.maskFunction = maskFunction;
        }

        public Pattern getPattern() {
            return pattern;
        }

        public String getDescription() {
            return description;
        }

        public Function<String, String> getMaskFunction() {
            return maskFunction;
        }
    }

    /**
     * 對文本進行全面的敏感信息掩碼處理
     * 
     * @param text 原始文本
     * @return 掩碼後的文本
     */
    public static String maskSensitiveData(String text) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }

        String maskedText = text;

        try {
            // 依次應用所有掩碼模式
            for (SensitiveDataPattern pattern : SensitiveDataPattern.values()) {
                maskedText = applyMaskPattern(maskedText, pattern);
            }

            // 特殊處理JSON格式
            if (isJsonFormat(text)) {
                maskedText = maskJsonSensitiveData(maskedText);
            }

        } catch (Exception e) {
            logger.warn("敏感信息掩碼處理失敗: {}", e.getMessage());
            // 如果掩碼失敗，返回原文本
            return text;
        }

        return maskedText;
    }

    /**
     * 應用特定的掩碼模式
     */
    private static String applyMaskPattern(String text, SensitiveDataPattern sensitivePattern) {
        try {
            Matcher matcher = sensitivePattern.getPattern().matcher(text);
            if (matcher.find()) {
                logger.debug("檢測到敏感信息類型: {}", sensitivePattern.getDescription());
            }
            return matcher.replaceAll(match -> sensitivePattern.getMaskFunction().apply(match.group()));
        } catch (Exception e) {
            logger.warn("應用掩碼模式失敗 [{}]: {}", sensitivePattern.getDescription(), e.getMessage());
            return text;
        }
    }

    /**
     * 檢查是否為JSON格式
     */
    private static boolean isJsonFormat(String text) {
        text = text.trim();
        return (text.startsWith("{") && text.endsWith("}")) ||
                (text.startsWith("[") && text.endsWith("]"));
    }

    /**
     * 對JSON格式的敏感數據進行掩碼
     */
    private static String maskJsonSensitiveData(String jsonText) {
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonText);
            String maskedJson = jsonNode.toString();

            // 對JSON中的敏感字段進行掩碼
            Map<String, String> sensitiveFields = new HashMap<>();
            sensitiveFields.put("password", "****");
            sensitiveFields.put("pwd", "****");
            sensitiveFields.put("token", "****");
            sensitiveFields.put("secret", "****");
            sensitiveFields.put("key", "****");

            for (Map.Entry<String, String> entry : sensitiveFields.entrySet()) {
                String fieldPattern = "\"" + entry.getKey() + "\"\\s*:\\s*\"[^\"]*\"";
                maskedJson = maskedJson.replaceAll(fieldPattern,
                        "\"" + entry.getKey() + "\":\"" + entry.getValue() + "\"");
            }

            return maskedJson;
        } catch (Exception e) {
            logger.debug("JSON掩碼處理失敗，使用原始文本: {}", e.getMessage());
            return jsonText;
        }
    }

    // ==================== 具體的掩碼方法 ====================

    /**
     * 密碼掩碼處理
     */
    private static String maskPassword(String match) {
        // 保留字段名，完全隱藏值
        if (match.contains(":") || match.contains("=")) {
            int separatorIndex = Math.max(match.indexOf(":"), match.indexOf("="));
            return match.substring(0, separatorIndex + 1) + " \"****\"";
        }
        return REPLACEMENT_PATTERN;
    }

    /**
     * 郵箱掩碼處理 - 保留前2位和域名
     */
    private static String maskEmail(String email) {
        int atIndex = email.indexOf("@");
        if (atIndex > 2) {
            String prefix = email.substring(0, 2);
            String suffix = email.substring(atIndex);
            return prefix + "****" + suffix;
        }
        return "****@****.com";
    }

    /**
     * JWT Token掩碼處理 - 保留前後各8位
     */
    private static String maskJwtToken(String token) {
        if (token.length() > 16) {
            String prefix = token.substring(0, 8);
            String suffix = token.substring(token.length() - 8);
            return prefix + "......" + suffix;
        }
        return "******.******.******";
    }

    /**
     * 自定義掩碼處理
     * 
     * @param text        原始文本
     * @param regex       正則表達式
     * @param replacement 替換字符串
     * @return 掩碼後的文本
     */
    public static String maskCustomPattern(String text, String regex, String replacement) {
        if (text == null || regex == null || replacement == null) {
            return text;
        }

        try {
            return text.replaceAll(regex, replacement);
        } catch (Exception e) {
            logger.warn("自定義掩碼處理失敗: {}", e.getMessage());
            return text;
        }
    }

    /**
     * 檢查文本是否包含敏感信息
     * 
     * @param text 待檢查的文本
     * @return 是否包含敏感信息
     */
    public static boolean containsSensitiveData(String text) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }

        for (SensitiveDataPattern pattern : SensitiveDataPattern.values()) {
            if (pattern.getPattern().matcher(text).find()) {
                logger.debug("檢測到敏感信息: {}", pattern.getDescription());
                return true;
            }
        }

        return false;
    }

    /**
     * 獲取文本中包含的敏感信息類型
     * 
     * @param text 待檢查的文本
     * @return 敏感信息類型列表
     */
    public static Map<String, Boolean> getSensitiveDataTypes(String text) {
        Map<String, Boolean> result = new HashMap<>();

        if (text == null || text.trim().isEmpty()) {
            return result;
        }

        for (SensitiveDataPattern pattern : SensitiveDataPattern.values()) {
            boolean found = pattern.getPattern().matcher(text).find();
            result.put(pattern.getDescription(), found);
            if (found) {
                logger.debug("發現敏感信息類型: {}", pattern.getDescription());
            }
        }

        return result;
    }
}