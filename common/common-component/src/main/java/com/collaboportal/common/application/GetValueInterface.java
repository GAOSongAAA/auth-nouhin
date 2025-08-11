package com.collaboportal.common.application;

import com.collaboportal.common.utils.ObjectUtil;

public interface GetValueInterface {
    Object get(String key);
	
	
	// --------- インターフェースが提供するラップメソッド 

	/**
	 * 値を取得（デフォルト値を指定）
	 *
	 * @param <T> デフォルト値の型
	 * @param key キー 
	 * @param defaultValue 値が取得できない場合に返すデフォルト値 
	 * @return 値 
	 */
	default <T> T get(String key, T defaultValue) {
		return getValueByDefaultValue(get(key), defaultValue);
	}

	/**
	 * 値を取得（String型に変換） 
	 * @param key キー 
	 * @return 値 
	 */
	default String getString(String key) {
		Object value = get(key);
		if(value == null) {
			return null;
		}
		return String.valueOf(value);
	}

	/**
	 * 値を取得（int型に変換） 
	 * @param key キー 
	 * @return 値 
	 */
	default int getInt(String key) {
		return getValueByDefaultValue(get(key), 0);
	}

	/**
	 * 値を取得（long型に変換） 
	 * @param key キー 
	 * @return 値 
	 */
	default long getLong(String key) {
		return getValueByDefaultValue(get(key), 0L);
	}

	/**
	 * 値を取得（double型に変換） 
	 * @param key キー 
	 * @return 値 
	 */
	default double getDouble(String key) {
		return getValueByDefaultValue(get(key), 0.0);
	}

	/**
	 * 値を取得（float型に変換） 
	 * @param key キー 
	 * @return 値 
	 */
	default float getFloat(String key) {
		return getValueByDefaultValue(get(key), 0.0f);
	}

	/**
	 * 値を取得（指定した型に変換）
	 * @param <T> ジェネリクス
	 * @param key キー 
	 * @param cs 指定する型 
	 * @return 値 
	 */
	default <T> T getModel(String key, Class<T> cs) {
		return ObjectUtil.getValueByType(get(key), cs);
	}

	/**
	 * 値を取得（指定した型に変換し、値がnullの場合はデフォルト値を返す）
	 * @param <T> ジェネリクス
	 * @param key キー 
	 * @param cs 指定する型 
	 * @param defaultValue 値がnullの場合に返すデフォルト値
	 * @return 値 
	 */
	@SuppressWarnings("unchecked")
	default <T> T getModel(String key, Class<T> cs, Object defaultValue) {
		T model = getModel(key, cs);
		return valueIsNull(model) ? (T)defaultValue : model;
	}

	/**
	 * 指定したキーが存在するかどうか
	 * @param key 指定するキー
	 * @return 存在するかどうか
	 */
	default boolean has(String key) {
		return !valueIsNull(get(key));
	}

	
	// --------- 内部ユーティリティメソッド 

	/**
	 * 値がnullかどうかを判定
	 * @param value 指定する値 
	 * @return このvalueがnullかどうか 
	 */
	default boolean valueIsNull(Object value) {
		return value == null || value.equals("");
	}

	/**
	 * デフォルト値に基づいて値を取得
	 * @param <T> ジェネリクス
	 * @param value 値 
	 * @param defaultValue デフォルト値
	 * @return 変換後の値 
	 */
	@SuppressWarnings("unchecked")
	default <T> T getValueByDefaultValue(Object value, T defaultValue) {
		
		// objがnullの場合はデフォルト値を返す 
		if(valueIsNull(value)) {
			return defaultValue;
		}
		
		// 型変換を開始
		Class<T> cs = (Class<T>) defaultValue.getClass();
		return ObjectUtil.getValueByType(value, cs);
	}


}
