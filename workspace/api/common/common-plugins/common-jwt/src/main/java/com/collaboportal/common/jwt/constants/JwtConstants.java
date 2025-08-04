package com.collaboportal.common.jwt.constants;

public class JwtConstants {

    // トークンタイプ
    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";


    public static final String TOKEN_TYPE_INTERNAL = "internal";
    public static final String TOKEN_TYPE_STATE = "state";
    
    // トークン生成タイプ
    public static final String GENERATE_STATE_MAP = "state-map-generator";
    public static final String GENERATE_INTERNAL_TOKEN = "internal-token-generator";
    public static final String GENERATE_DATABASE_TOKEN = "database-token-generator";



    public static final String RESOLVER_TYPE_UID = "uid";
    public static final String RESOLVER_TYPE_EMAIL = "email";
    public static final String RESOLVER_TYPE_ROLE = "role";

    // トークン検証タイプ
    public static final String VALIDATE_TYPE_ISSUER = "issuer-validator";
    public static final String VALIDATE_TYPE_EXPIRED = "exp-only";


    public static final String RETRY_TYPE_LOGIN = "login-retry";
    
}
