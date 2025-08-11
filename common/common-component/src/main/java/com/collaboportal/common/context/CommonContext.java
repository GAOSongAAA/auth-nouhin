package com.collaboportal.common.context;

import com.collaboportal.common.context.web.BaseRequest;
import com.collaboportal.common.context.web.BaseResponse;
import com.collaboportal.common.context.web.BaseStorage;

public interface CommonContext {

    BaseRequest getRequest();

    BaseResponse getResponse();

    BaseStorage getStorage();

    boolean matchPath(String pattern,String path);

    default boolean isValid() {
		return false;
	}
}
