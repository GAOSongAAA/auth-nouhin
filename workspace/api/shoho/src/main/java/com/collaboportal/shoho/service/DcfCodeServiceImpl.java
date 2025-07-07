package com.collaboportal.shoho.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.collaboportal.common.error.InternalErrorCode;
import com.collaboportal.common.exception.CommonException;
import com.collaboportal.shoho.entity.DcfCode;
import com.collaboportal.shoho.model.DcfCodeRequestParameter;
import com.collaboportal.shoho.repository.DcfCodeMapper;

@Service
public class DcfCodeServiceImpl implements DcfCodeService {

	private final DcfCodeMapper dcfCodeMapper;

	public DcfCodeServiceImpl(DcfCodeMapper dcfCodeMapper) {
		this.dcfCodeMapper = dcfCodeMapper;
	}

	/**
	 * DCFコード一覧取得メソッド
	 * 
	 * @param param DCFコード一覧取得パラメータ
	 * @return DCFコード一覧のリスト
	 */
	@Override
	public List<String> getDcfCode(DcfCodeRequestParameter param) {
		List<DcfCode> dcfCode = dcfCodeMapper.getDcfCode(param);

		if (dcfCode == null || dcfCode.size() == 0) {
			throw new CommonException(InternalErrorCode.RECORD_NOT_FOUND_ERROR);
		}

		// List<DcfCode>をList<String>に変換
		return dcfCode.stream()
				.map(DcfCode::getDcf_cod)
				.collect(Collectors.toList());
	}
}
