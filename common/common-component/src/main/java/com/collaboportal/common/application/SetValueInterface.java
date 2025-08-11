package com.collaboportal.common.application;

import com.collaboportal.common.funcs.RetFunction;

public interface SetValueInterface extends GetValueInterface {
    	// --------- サブクラスが実装する必要があるメソッド 
	
	/**
	 * 値を設定 
	 * @param key   名称
	 * @param value 値
	 * @return オブジェクト自身
	 */
	SetValueInterface set(String key, Object value);
	
	/**
	 * 値を削除 
	 * @param key 削除するキー
	 * @return オブジェクト自身
	 */
	SetValueInterface delete(String key);

	
	// --------- インターフェースが提供するラップメソッド 

	/**
	 * 
	 * 値を取得（値が null の場合、fun 関数を実行して値を取得し、その戻り値をキャッシュに書き込む） 
	 * @param <T> 戻り値の型 
	 * @param key キー 
	 * @param fun 値がnullの時に実行する関数 
	 * @return 値 
	 */
	@SuppressWarnings("unchecked")
	default <T> T get(String key, RetFunction fun) {
		Object value = get(key);
		if(value == null) {
			value = fun.run();
			set(key, value);
		}
		return (T) value;
	}
	
	/**
	 * 値を設定（この key に元々値がない場合のみ書き込む） 
	 * @param key   名称
	 * @param value 値
	 * @return オブジェクト自身
	 */
	default SetValueInterface setByNull(String key, Object value) {
		if( ! has(key)) {
			set(key, value);
		}
		return this;
	}

}
