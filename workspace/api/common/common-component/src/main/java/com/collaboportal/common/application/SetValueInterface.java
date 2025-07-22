package com.collaboportal.common.application;

import com.collaboportal.common.funcs.RetFunction;

public interface SetValueInterface extends GetValueInterface {
    	// --------- 需要子类实现的方法 
	
	/**
	 * 写值 
	 * @param key   名称
	 * @param value 值
	 * @return 对象自身
	 */
	SetValueInterface set(String key, Object value);
	
	/**
	 * 删值 
	 * @param key 要删除的key
	 * @return 对象自身
	 */
	SetValueInterface delete(String key);

	
	// --------- 接口提供封装的方法 

	/**
	 * 
	 * 取值 (如果值为 null，则执行 fun 函数获取值，并把函数返回值写入缓存) 
	 * @param <T> 返回值的类型 
	 * @param key key 
	 * @param fun 值为null时执行的函数 
	 * @return 值 
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
	 * 写值 (只有在此 key 原本无值的情况下才会写入) 
	 * @param key   名称
	 * @param value 值
	 * @return 对象自身
	 */
	default SetValueInterface setByNull(String key, Object value) {
		if( ! has(key)) {
			set(key, value);
		}
		return this;
	}

}
