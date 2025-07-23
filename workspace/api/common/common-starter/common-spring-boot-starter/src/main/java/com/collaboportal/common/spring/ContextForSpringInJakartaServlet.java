package com.collaboportal.common.spring;

import com.collaboportal.common.context.CommonContext;
import com.collaboportal.common.context.model.BaseRequest;
import com.collaboportal.common.context.model.BaseResponse;
import com.collaboportal.common.context.model.BaseStorage;
import com.collaboportal.common.servlet.RequestForServlet;
import com.collaboportal.common.servlet.ResponseForServlet;
import com.collaboportal.common.servlet.StorageForServlet;
import com.collaboportal.common.utils.PathPatternParserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContextForSpringInJakartaServlet implements CommonContext {

	private static final Logger logger = LoggerFactory.getLogger(ContextForSpringInJakartaServlet.class);

	/**
	 * 現在のリクエストのRequestラッパーオブジェクトを取得します
	 */
	@Override
	public BaseRequest getRequest() {
		logger.info("リクエストラッパーオブジェクトを取得します");
		return new RequestForServlet(SpringMVCUtil.getRequest());
	}

	/**
	 * 現在のリクエストのResponseラッパーオブジェクトを取得します
	 */
	@Override
	public BaseResponse getResponse() {
		logger.info("レスポンスラッパーオブジェクトを取得します");
		return new ResponseForServlet(SpringMVCUtil.getResponse());
	}

	/**
	 * 現在のリクエストのStorageラッパーオブジェクトを取得します
	 */
	@Override
	public BaseStorage getStorage() {
		logger.info("ストレージラッパーオブジェクトを取得します");
		return new StorageForServlet(SpringMVCUtil.getRequest());
	}
	
	/**
	 * 指定したルーティングパターンが指定したパスにマッチするかどうかを判定します
	 */
	@Override
	public boolean matchPath(String pattern, String path) {
		logger.info("ルーティングパターン「{}」とパス「{}」のマッチ判定を行います", pattern, path);
		return PathPatternParserUtil.match(pattern, path);
	}

	/**
	 * 今回のリクエストでこのコンテキストが有効かどうかを判定します
	 */
	@Override
	public boolean isValid() {
		logger.info("このコンテキストが有効かどうかを判定します");
		return SpringMVCUtil.isWeb();
	}

}
