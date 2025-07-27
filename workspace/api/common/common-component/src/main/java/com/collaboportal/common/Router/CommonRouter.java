package com.collaboportal.common.Router;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.context.CommonHolder;
import com.collaboportal.common.exception.BackResultException;
import com.collaboportal.common.exception.StopMatchException;
import com.collaboportal.common.funcs.Function;
import com.collaboportal.common.funcs.ParamFunction;
import com.collaboportal.common.funcs.ParamRetFunction;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 共通ルータークラス
 * ルーティングのマッチングや認証処理を提供します。
 */
public class CommonRouter {

    // ロガーの初期化
    private static final Logger logger = LoggerFactory.getLogger(CommonRouter.class);

    private CommonRouter() {};

    /**
     * パターンとパスのマッチングを行います。
     * @param pattern ルートパターン
     * @param path リクエストパス
     * @return マッチした場合はtrue
     */
    public static boolean isMatch(String pattern, String path) {
        logger.debug("isMatch: パターン[{}]、パス[{}] でマッチングを実行します。", pattern, path);
        boolean result = ConfigManager.getCommonContext().matchPath(pattern, path);
        logger.debug("isMatch: 結果 = {}", result);
        return result;
    }

    /**
     * パターンリストとパスのマッチングを行います。
     * @param patterns ルートパターンリスト
     * @param path リクエストパス
     * @return マッチした場合はtrue
     */
    public static boolean isMatch(List<String> patterns, String path) {
        logger.debug("isMatch(List): パターンリスト[{}]、パス[{}] でマッチングを実行します。", patterns, path);
        if(patterns == null) {
            logger.warn("isMatch(List): パターンリストがnullです。");
            return false;
        }
        for (String pattern : patterns) {
            if(isMatch(pattern, path)) {
                logger.debug("isMatch(List): パターン[{}]がマッチしました。", pattern);
                return true;
            }
        }
        logger.debug("isMatch(List): いずれのパターンもマッチしませんでした。");
        return false;
    }

    /**
     * パターン配列とパスのマッチングを行います。
     * @param patterns ルートパターン配列
     * @param path リクエストパス
     * @return マッチした場合はtrue
     */
    public static boolean isMatch(String[] patterns, String path) {
        logger.debug("isMatch(Array): パターン配列[{}]、パス[{}] でマッチングを実行します。", (patterns == null ? "null" : patterns.length), path);
        if(patterns == null) {
            logger.warn("isMatch(Array): パターン配列がnullです。");
            return false;
        }
        for (String pattern : patterns) {
            if(isMatch(pattern, path)) {
                logger.debug("isMatch(Array): パターン[{}]がマッチしました。", pattern);
                return true;
            }
        }
        logger.debug("isMatch(Array): いずれのパターンもマッチしませんでした。");
        return false;
    }

    /**
     * 現在のリクエストURIとパターンのマッチングを行います。
     * @param pattern ルートパターン
     * @return マッチした場合はtrue
     */
    public static boolean isMatchCurrURI(String pattern) {
        String currPath = CommonHolder.getRequest().getRequestPath();
        logger.debug("isMatchCurrURI: 現在のURI[{}]、パターン[{}] でマッチングを実行します。", currPath, pattern);
        return isMatch(pattern, currPath);
    }  

    /**
     * 現在のリクエストURIとパターンリストのマッチングを行います。
     * @param patterns ルートパターンリスト
     * @return マッチした場合はtrue
     */
    public static boolean isMatchCurrURI(List<String> patterns) {
        String currPath = CommonHolder.getRequest().getRequestPath();
        logger.debug("isMatchCurrURI(List): 現在のURI[{}]、パターンリスト[{}] でマッチングを実行します。", currPath, patterns);
        return isMatch(patterns, currPath);
    }

    /**
     * 現在のリクエストURIとパターン配列のマッチングを行います。
     * @param patterns ルートパターン配列
     * @return マッチした場合はtrue
     */
    public static boolean isMatchCurrURI(String[] patterns) {
        String currPath = CommonHolder.getRequest().getRequestPath();
        logger.debug("isMatchCurrURI(Array): 現在のURI[{}]、パターン配列[{}] でマッチングを実行します。", currPath, (patterns == null ? "null" : patterns.length));
        return isMatch(patterns, currPath);
    }

    /**
     * 新しいマッチングスタッフを生成します。
     * @return CommonRouterStaffインスタンス
     */
    public static CommonRouterStaff newMatch() {
        logger.debug("newMatch: 新しいCommonRouterStaffを生成します。");
        return new CommonRouterStaff();
    }

    /**
     * パターン配列でマッチングを行うスタッフを生成します。
     * @param patterns ルートパターン配列
     * @return CommonRouterStaffインスタンス
     */
    public static CommonRouterStaff match(String... patterns) {
        logger.debug("match(String...): パターン配列でマッチングスタッフを生成します。");
        return new CommonRouterStaff().match(patterns);
    }

    /**
     * パターン配列でマッチしないスタッフを生成します。
     * @param patterns ルートパターン配列
     * @return CommonRouterStaffインスタンス
     */
    public static CommonRouterStaff notMatch(String... patterns) {
        logger.debug("notMatch(String...): パターン配列でnotMatchスタッフを生成します。");
        return new CommonRouterStaff().notMatch(patterns);
    }

    /**
     * パターンリストでマッチングを行うスタッフを生成します。
     * @param patterns ルートパターンリスト
     * @return CommonRouterStaffインスタンス
     */
    public static CommonRouterStaff match(List<String> patterns) {
        logger.debug("match(List): パターンリストでマッチングスタッフを生成します。");
        return new CommonRouterStaff().match(patterns);
    }

    /**
     * パターンリストでマッチしないスタッフを生成します。
     * @param patterns ルートパターンリスト
     * @return CommonRouterStaffインスタンス
     */
    public static CommonRouterStaff notMatch(List<String> patterns) {
        logger.debug("notMatch(List): パターンリストでnotMatchスタッフを生成します。");
        return new CommonRouterStaff().notMatch(patterns);
    }

    /**
     * boolean値でマッチングを行うスタッフを生成します。
     * @param flag マッチフラグ
     * @return CommonRouterStaffインスタンス
     */
    public static CommonRouterStaff match(boolean flag) {
        logger.debug("match(boolean): フラグ[{}]でマッチングスタッフを生成します。", flag);
        return new CommonRouterStaff().match(flag);
    }

    /**
     * boolean値でマッチしないスタッフを生成します。
     * @param flag マッチフラグ
     * @return CommonRouterStaffインスタンス
     */
    public static CommonRouterStaff notMatch(boolean flag) {
        logger.debug("notMatch(boolean): フラグ[{}]でnotMatchスタッフを生成します。", flag);
        return new CommonRouterStaff().notMatch(flag);
    }

    /**
     * 関数でマッチングを行うスタッフを生成します。
     * @param fun 判定関数
     * @return CommonRouterStaffインスタンス
     */
    public static CommonRouterStaff match(ParamRetFunction<Object, Boolean> fun) {
        logger.debug("match(ParamRetFunction): 関数でマッチングスタッフを生成します。");
        return new CommonRouterStaff().match(fun);
    }

    /**
     * 関数でマッチしないスタッフを生成します。
     * @param fun 判定関数
     * @return CommonRouterStaffインスタンス
     */
    public static CommonRouterStaff notMatch(ParamRetFunction<Object, Boolean> fun) {
        logger.debug("notMatch(ParamRetFunction): 関数でnotMatchスタッフを生成します。");
        return new CommonRouterStaff().notMatch(fun);
    }

    /**
     * パターンと関数でマッチングを行うスタッフを生成します。
     * @param pattern ルートパターン
     * @param fun 実行関数
     * @return CommonRouterStaffインスタンス
     */
    public static CommonRouterStaff match(String pattern, Function fun) {
        logger.debug("match(String, Function): パターン[{}]で関数付きマッチングスタッフを生成します。", pattern);
        return new CommonRouterStaff().match(pattern, fun);
    }

    /**
     * パターンと関数でマッチングを行うスタッフを生成します。
     * @param pattern ルートパターン
     * @param fun 実行関数
     * @return CommonRouterStaffインスタンス
     */
    public static CommonRouterStaff match(String pattern, ParamFunction<CommonRouterStaff> fun) {
        logger.debug("match(String, ParamFunction): パターン[{}]で関数付きマッチングスタッフを生成します。", pattern);
        return new CommonRouterStaff().match(pattern, fun);
    }

    /**
     * パターンと除外パターン、関数でマッチングを行うスタッフを生成します。
     * @param pattern ルートパターン
     * @param excludePattern 除外パターン
     * @param fun 実行関数
     * @return CommonRouterStaffインスタンス
     */
    public static CommonRouterStaff match(String pattern, String excludePattern, Function fun) {
        logger.debug("match(String, String, Function): パターン[{}]、除外パターン[{}]で関数付きマッチングスタッフを生成します。", pattern, excludePattern);
        return new CommonRouterStaff().match(pattern, excludePattern, fun);
    }

    /**
     * パターンと除外パターン、関数でマッチングを行うスタッフを生成します。
     * @param pattern ルートパターン
     * @param excludePattern 除外パターン
     * @param fun 実行関数
     * @return CommonRouterStaffインスタンス
     */
    public static CommonRouterStaff match(String pattern, String excludePattern, ParamFunction<CommonRouterStaff> fun) {
        logger.debug("match(String, String, ParamFunction): パターン[{}]、除外パターン[{}]で関数付きマッチングスタッフを生成します。", pattern, excludePattern);
        return new CommonRouterStaff().match(pattern, excludePattern, fun);
    }

    /**
     * マッチングを停止します（例外をスロー）。
     * @return なし
     */
    public static CommonRouterStaff stop() {
        logger.warn("stop: マッチングを停止します。StopMatchExceptionをスローします。");
        throw new StopMatchException();
    }

    /**
     * マッチングを停止し、結果を返します（例外をスロー）。
     * @return なし
     */
    public static CommonRouterStaff back() {
        logger.warn("back: マッチングを停止し、空の結果を返します。BackResultExceptionをスローします。");
        throw new BackResultException("");
    }

    /**
     * マッチングを停止し、指定した結果を返します（例外をスロー）。
     * @param result 返却する結果
     * @return なし
     */
    public static CommonRouterStaff back(Object result) {
        logger.warn("back(Object): マッチングを停止し、結果[{}]を返します。BackResultExceptionをスローします。", result);
        throw new BackResultException(result);
    }

}
