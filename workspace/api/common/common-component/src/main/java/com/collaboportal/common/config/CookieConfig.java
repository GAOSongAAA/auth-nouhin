package com.collaboportal.common.config;

import java.util.LinkedHashMap;
import java.util.Map;

public class CookieConfig implements BaseConfig {

    private static final String CONFIG_PREFIX = "common.cookie";

    private String domain;
    private String path;
    private Boolean secure = false;
    private Boolean httpOnly = false;
    private String sameSite;
    private Map<String, String> extraAttrs = new LinkedHashMap<>();

    @Override
    public String getConfigPrefix() {
        return CONFIG_PREFIX;
    }

    public String getDomain() {
        return domain;
    }

    public CookieConfig setDomain(String domain) {
        this.domain = domain;
        return this;
    }

    public String getPath() {
        return path;
    }

    public CookieConfig setPath(String path) {
        this.path = path;
        return this;
    }

    public boolean isSecure() {
        return secure;
    }

    public CookieConfig setSecure(boolean secure) {
        this.secure = secure;
        return this;
    }

    public boolean isHttpOnly() {
        return httpOnly;
    }

    public CookieConfig setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
        return this;
    }

    public String getSameSite() {
        return sameSite;
    }
    public CookieConfig setSameSite(String sameSite) {
        this.sameSite = sameSite;
        return this;
    }
    public Map<String, String> getExtraAttrs() {
        return extraAttrs;
    }
    public CookieConfig setExtraAttrs(Map<String, String> extraAttrs) {
        this.extraAttrs = extraAttrs;
        return this;
    }
    public CookieConfig addExtraAttr(String key, String value) {
        this.extraAttrs.put(key, value);
        return this;
    }
    public CookieConfig removeExtraAttr(String key) {
        this.extraAttrs.remove(key);
        return this;
    }
    public CookieConfig clearExtraAttrs() {
        this.extraAttrs.clear();
        return this;
    }
    @Override
    public String toString() {
        return "CookieConfig{" +
                "domain='" + domain + '\'' +
                ", path='" + path + '\'' +
                ", secure=" + secure +
                ", httpOnly=" + httpOnly +
                ", sameSite='" + sameSite + '\'' +
                ", extraAttrs=" + extraAttrs +
                '}';
    }
    

}
