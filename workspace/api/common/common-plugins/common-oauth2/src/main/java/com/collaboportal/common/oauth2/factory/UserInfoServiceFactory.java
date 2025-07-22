package com.collaboportal.common.oauth2.factory;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ServiceLoader;
import java.util.Set;

import com.collaboportal.common.oauth2.entity.DTO.IUserInfoDto;
import com.collaboportal.common.oauth2.service.IUserInfoService;

public class UserInfoServiceFactory {
    private static final Map<String, IUserInfoService<?>> services = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(UserInfoServiceFactory.class);

    public static void registerService(String appName, IUserInfoService<?> service) {
        services.put(appName, service);
        logger.info("サービス登録: {} : アプリ名: {}", service.getClass().getSimpleName(), appName);
    }

    // 获取服务
    @SuppressWarnings("unchecked")
    public static <T extends IUserInfoDto> IUserInfoService<T> getService(String appName) {
        IUserInfoService<T> service = (IUserInfoService<T>) services.get(appName);
        if (service == null) {
            logger.warn("アプリ名: {} のユーザー情報サービスが見つかりませんでした。", appName);
        }
        return service;
    }

    public static <T extends IUserInfoDto> T loadUserByEmail(String appName, String email) {
        IUserInfoService<T> service = getService(appName);
        if (service != null) {
            return service.loadByEmail(email);
        }
        logger.warn("アプリ名: {} のユーザー情報が見つかりませんでした。メールアドレス: {}", appName, email);
        return null;
    }

    public static <T extends IUserInfoDto> T loadUserById(String appName, String userId) {
        IUserInfoService<T> service = getService(appName);
        if (service != null) {
            return service.loadById(userId);
        }
        logger.warn("アプリ名: {} のユーザー情報が見つかりませんでした。ユーザーID: {}", appName, userId);
        return null;
    }

    public static Set<String> getRegisteredApps() {
        return new HashSet<>(services.keySet());
    }

    public static boolean hasService(String appName) {
        return services.containsKey(appName);
    }

    static {
        ServiceLoader<IUserInfoService> loader = ServiceLoader.load(IUserInfoService.class);
        for (IUserInfoService service : loader) {
            String appName = getAppName(service);
            registerService(appName, service);
        }
    }

    // 通过注解或命名约定获取app名称
    private static String getAppName(IUserInfoService service) {

        // 方式1：通过类名约定 (App1UserInfoService -> app1)
        String className = service.getClass().getSimpleName();
        if (className.endsWith("UserInfoService")) {
            String appPart = className.substring(0, className.length() - "UserInfoService".length());
            return appPart.toLowerCase();
        }

        // 方式2：通过包名约定
        String packageName = service.getClass().getPackage().getName();
        String[] parts = packageName.split("\\.");
        for (String part : parts) {
            if (part.startsWith("app")) {
                return part;
            }
        }

        return "unknown";
    }
}