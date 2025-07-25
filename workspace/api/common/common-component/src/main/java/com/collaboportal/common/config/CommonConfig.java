package com.collaboportal.common.config;


public class CommonConfig implements BaseConfig {

    private String noAuthUrl;
    private String indexPage;
    private String secretKey;
    private String collaboidBaseurl;
    private String envFlag = "0";
    private String collaboportalIssuer;
    private String collaboportalClientIdWeb;
    private String collaboportalClientSecretWeb;
    private String collaboportalAudience;
    private String collaboportalBaseurl;
    private String noAuthorization;
    private boolean isCookieSecure = true;
    private String callback;
    private int cookieExpiration;
    private String currDomain;
    private String tokenName;
    private int maxLoginCount;
    private int maxTryTimes;
    private boolean isLastingCookie;
    private boolean isWriteHeader;

    @Override
    public String getConfigPrefix() {
        return "common.util";
    }

    public String getNoAuthUrl() {
        return noAuthUrl;
    }

    public CommonConfig setNoAuthUrl(String noAuthUrl) {
        this.noAuthUrl = noAuthUrl;
        return this;
    }

    public String getIndexPage() {
        return indexPage;
    }

    public CommonConfig setIndexPage(String indexPage) {
        this.indexPage = indexPage;
        return this;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public CommonConfig setSecretKey(String secretKey) {
        this.secretKey = secretKey;
        return this;
    }

    public String getCollaboidBaseurl() {
        return collaboidBaseurl;
    }

    public CommonConfig setCollaboidBaseurl(String collaboidBaseurl) {
        this.collaboidBaseurl = collaboidBaseurl;
        return this;
    }

    public String getEnvFlag() {
        return envFlag;
    }

    public CommonConfig setEnvFlag(String envFlag) {
        this.envFlag = envFlag;
        return this;
    }

    public String getCollaboportalIssuer() {
        return collaboportalIssuer;
    }

    public CommonConfig setCollaboportalIssuer(String collaboportalIssuer) {
        this.collaboportalIssuer = collaboportalIssuer;
        return this;
    }

    public String getCollaboportalClientIdWeb() {
        return collaboportalClientIdWeb;
    }

    public CommonConfig setCollaboportalClientIdWeb(String collaboportalClientIdWeb) {
        this.collaboportalClientIdWeb = collaboportalClientIdWeb;
        return this;
    }

    public String getCollaboportalClientSecretWeb() {
        return collaboportalClientSecretWeb;
    }

    public CommonConfig setCollaboportalClientSecretWeb(String collaboportalClientSecretWeb) {
        this.collaboportalClientSecretWeb = collaboportalClientSecretWeb;
        return this;
    }

    public String getCollaboportalAudience() {
        return collaboportalAudience;
    }

    public CommonConfig setCollaboportalAudience(String collaboportalAudience) {
        this.collaboportalAudience = collaboportalAudience;
        return this;
    }

    public String getCollaboportalBaseurl() {
        return collaboportalBaseurl;
    }

    public CommonConfig setCollaboportalBaseurl(String collaboportalBaseurl) {
        this.collaboportalBaseurl = collaboportalBaseurl;
        return this;
    }

    public String getNoAuthorization() {
        return noAuthorization;
    }

    public CommonConfig setNoAuthorization(String noAuthorization) {
        this.noAuthorization = noAuthorization;
        return this;
    }

    public boolean isCookieSecure() {
        return isCookieSecure;
    }

    public CommonConfig setCookieSecure(boolean isCookieSecure) {
        this.isCookieSecure = isCookieSecure;
        return this;
    }

    public String getCallback() {
        return callback;
    }

    public CommonConfig setCallback(String callback) {
        this.callback = callback;
        return this;
    }

    public int getCookieExpiration() {
        return cookieExpiration;
    }

    public CommonConfig setCookieExpiration(int cookieExpiration) {
        this.cookieExpiration = cookieExpiration;
        return this;
    }

    public String getCurrDomain() {
        return currDomain;
    }

    public CommonConfig setCurrDomain(String currDomain) {
        this.currDomain = currDomain;
        return this;
    }

    public String getTokenName() {
        return tokenName;
    }

    public CommonConfig setTokenName(String tokenName) {
        this.tokenName = tokenName;
        return this;
    }

    public int getMaxLoginCount() {
		return maxLoginCount;
	}

    public int getMaxTryTimes() {
        return maxTryTimes;
    }

    public boolean getIsLastingCookie() {
        return isLastingCookie;
    }

    public boolean getIsWriteHeader() {
        return isWriteHeader;
    }

    @Override
    public String toString() {
        return "CommonConfig{" +
                "noAuthUrl='" + noAuthUrl + '\'' +
                ", indexPage='" + indexPage + '\'' +
                ", secretKey='" + secretKey + '\'' +
                ", collaboidBaseurl='" + collaboidBaseurl + '\'' +
                ", envFlag='" + envFlag + '\'' +
                ", collaboportalIssuer='" + collaboportalIssuer + '\'' +
                ", collaboportalClientIdWeb='" + collaboportalClientIdWeb + '\'' +
                ", collaboportalClientSecretWeb='" + collaboportalClientSecretWeb + '\'' +
                ", collaboportalAudience='" + collaboportalAudience + '\'' +
                ", collaboportalBaseurl='" + collaboportalBaseurl + '\'' +
                ", noAuthorization='" + noAuthorization + '\'' +
                ", isCookieSecure=" + isCookieSecure +
                ", callback='" + callback + '\'' +
                ", cookieExpiration=" + cookieExpiration +
                ", currDomain='" + currDomain + '\'' +
                ", tokenName='" + tokenName + '\'' +
                '}';
    }
}
