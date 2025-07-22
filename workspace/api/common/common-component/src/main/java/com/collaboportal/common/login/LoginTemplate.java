package com.collaboportal.common.login;

import com.collaboportal.common.ConfigManager;

public class LoginTemplate {

    public String loginType; // ログインタイプ EPL -> 自前データベース　SUZUKEN -> OAuth2.0

    public LoginTemplate(String loginType) {
        setLoginType(loginType);
    }

    public String getLoginType(){
		return loginType;
	}

    public LoginTemplate setLoginType(String loginType){

		// 先清除此 StpLogic 在全局 SaManager 中的记录
		if(this.loginType != null || !this.loginType.isEmpty()) {
			ConfigManager.removeLoginTemplate(this.loginType);
		}

		// 赋值
		this.loginType = loginType;

		// 将新的 loginType -> StpLogic 映射关系 put 到 SaManager 全局集合中，以便后续根据 LoginType 进行查找此对象
		ConfigManager.putLoginTemplate(this);

		return this;
	}



}
