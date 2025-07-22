
package com.collaboportal.common.context;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.context.model.BaseRequest;
import com.collaboportal.common.context.model.BaseResponse;
import com.collaboportal.common.context.model.BaseStorage;

/**
 * Sa-Token 上下文持有类，你可以通过此类快速获取当前环境下的 SaRequest、SaResponse、SaStorage、SaApplication 对象。
 *
 * @author click33
 * @since 1.18.0
 */
public class CommonHolder {
	
	/**
	 * 获取当前请求的 SaTokenContext 上下文对象
	 * @see SaTokenContext
	 * 
	 * @return /
	 */
	public static CommonContext getContext() {
		return ConfigManager.getCommonContext();
	}

	/**
	 * 获取当前请求的 Request 包装对象
	 * @see SaRequest
	 * 
	 * @return /
	 */
	public static BaseRequest getRequest() {
		return ConfigManager.getCommonContext().getRequest();
	}

	/**
	 * 获取当前请求的 Response 包装对象
	 * @see SaResponse
	 * 
	 * @return /
	 */
	public static BaseResponse getResponse() {
		return ConfigManager.getCommonContext().getResponse();
	}

	/**
	 * 获取当前请求的 Storage 包装对象
	 * @see SaStorage
	 *
	 * @return /
	 */
	public static BaseStorage getStorage() {
		return ConfigManager.getCommonContext().getStorage();
	}


}
