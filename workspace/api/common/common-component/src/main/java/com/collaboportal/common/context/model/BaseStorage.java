package com.collaboportal.common.context.model;

import com.collaboportal.common.application.SetValueInterface;

public interface BaseStorage extends SetValueInterface {

    /**
     * ラップされている元のオブジェクトを取得
     * @return 元のオブジェクト
     */
    Object getSource();

    // ---- インターフェースの値取得・設定メソッドの実装

    /** 値を取得 */
    @Override
    Object get(String key);

    /** 値を設定 */
    @Override
    BaseStorage set(String key, Object value);

    /** 値を削除 */
    @Override
    BaseStorage delete(String key);

}
