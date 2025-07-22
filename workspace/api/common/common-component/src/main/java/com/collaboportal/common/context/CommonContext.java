package com.collaboportal.common.context;

import com.collaboportal.common.context.model.BaseRequest;
import com.collaboportal.common.context.model.BaseResponse;
import com.collaboportal.common.context.model.BaseStorage;


public interface CommonContext {

    BaseRequest getRequest();

    BaseResponse getResponse();

    BaseStorage getStorage();

    boolean matchPath(String pattern,String path);

    default boolean isValid() {
		return false;
	}
}
