package com.collaboportal.common.utils;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.login.LoginTemplate;

public class LoginUtil {

    private LoginUtil() {
    }
    public static final String TYPE = "login";

	public static LoginTemplate loginTemplate = new LoginTemplate(TYPE);

    public static String getLoginType(){
		return loginTemplate.getLoginType();
	}

	public static void setLoginTemplate(LoginTemplate newLoginTemplate) {
		// 1、重置此账户的 StpLogic 对象
		loginTemplate = newLoginTemplate;
		
		// 2、添加到全局 StpLogic 集合中
		//    以便可以通过 SaManager.getStpLogic(type) 的方式来全局获取到这个 StpLogic
		ConfigManager.putLoginTemplate(newLoginTemplate);

	}
    
    public static LoginTemplate getLoginTemplate() {
		return loginTemplate;
	}

    

}
