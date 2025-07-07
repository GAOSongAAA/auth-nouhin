package com.collaboportal.shoho.model;

import com.collaboportal.common.model.BaseResponseBody;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class DcfCodeResponseBody extends BaseResponseBody {

	/** DCFコード */
	private final List<String> dcf_cod;
}
