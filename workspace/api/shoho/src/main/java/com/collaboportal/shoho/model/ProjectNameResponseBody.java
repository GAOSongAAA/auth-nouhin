package com.collaboportal.shoho.model;

import com.collaboportal.common.model.BaseResponseBody;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ProjectNameResponseBody<T> extends BaseResponseBody {
	
	/** 企画名情報 */
	private final T lst_kkk_inf;
	
}
