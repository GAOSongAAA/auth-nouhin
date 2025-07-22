package com.collaboportal.common.Router;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.context.CommonHolder;
import com.collaboportal.common.exception.BackResultException;
import com.collaboportal.common.exception.StopMatchException;
import com.collaboportal.common.funcs.Function;
import com.collaboportal.common.funcs.ParamFunction;
import com.collaboportal.common.funcs.ParamRetFunction;

import java.util.List;
import java.util.logging.Logger;

/**
 * 共通ルータークラス
 * ルーティングのマッチングや認証処理を提供します。
 */
public class CommonRouter {

    // ロガーの初期化
    private static final Logger logger = Logger.getLogger(CommonRouter.class.getName());

    private CommonRouter() {};

    /**
     * パターンとパスのマッチングを行います。
     * @param pattern ルートパターン
     * @param path リクエストパス
     * @return マッチした場合はtrue
     */
    public static boolean isMatch(String pattern, String path) {
        logger.info("isMatch: パターン[" + pattern + "]、パス[" + path + "] でマッチングを実行します。");
        boolean result = ConfigManager.getCommonContext().matchPath(pattern, path);
        logger.info("isMatch: 結果 = " + result);
        return result;
    }

    /**
     * パターンリストとパスのマッチングを行います。
     * @param patterns ルートパターンリスト
     * @param path リクエストパス
     * @return マッチした場合はtrue
     */
    public static boolean isMatch(List<String> patterns, String path) {
        logger.info("isMatch(List): パターンリスト[" + patterns + "]、パス[" + path + "] でマッチングを実行します。");
        if(patterns == null) {
            logger.warning("isMatch(List): パターンリストがnullです。");
            return false;
        }
        for (String pattern : patterns) {
            if(isMatch(pattern, path)) {
                logger.info("isMatch(List): パターン[" + pattern + "]がマッチしました。");
                return true;
            }
        }
        logger.info("isMatch(List): いずれのパターンもマッチしませんでした。");
        return false;
    }

    /**
     * パターン配列とパスのマッチングを行います。
     * @param patterns ルートパターン配列
     * @param path リクエストパス
     * @return マッチした場合はtrue
     */
    public static boolean isMatch(String[] patterns, String path) {
        logger.info("isMatch(Array): パターン配列[" + (patterns == null ? "null" : patterns.length) + "]、パス[" + path + "] でマッチングを実行します。");
        if(patterns == null) {
            logger.warning("isMatch(Array): パターン配列がnullです。");
            return false;
        }
        for (String pattern : patterns) {
            if(isMatch(pattern, path)) {
                logger.info("isMatch(Array): パターン[" + pattern + "]がマッチしました。");
                return true;
            }
        }
        logger.info("isMatch(Array): いずれのパターンもマッチしませんでした。");
        return false;
    }

    /**
     * 現在のリクエストURIとパターンのマッチングを行います。
     * @param pattern ルートパターン
     * @return マッチした場合はtrue
     */
    public static boolean isMatchCurrURI(String pattern) {
        String currPath = CommonHolder.getRequest().getRequestPath();
        logger.info("isMatchCurrURI: 現在のURI[" + currPath + "]、パターン[" + pattern + "] でマッチングを実行します。");
        return isMatch(pattern, currPath);
    }  

    /**
     * 現在のリクエストURIとパターンリストのマッチングを行います。
     * @param patterns ルートパターンリスト
     * @return マッチした場合はtrue
     */
    public static boolean isMatchCurrURI(List<String> patterns) {
        String currPath = CommonHolder.getRequest().getRequestPath();
        logger.info("isMatchCurrURI(List): 現在のURI[" + currPath + "]、パターンリスト[" + patterns + "] でマッチングを実行します。");
        return isMatch(patterns, currPath);
    }

    /**
     * 現在のリクエストURIとパターン配列のマッチングを行います。
     * @param patterns ルートパターン配列
     * @return マッチした場合はtrue
     */
    public static boolean isMatchCurrURI(String[] patterns) {
        String currPath = CommonHolder.getRequest().getRequestPath();
        logger.info("isMatchCurrURI(Array): 現在のURI[" + currPath + "]、パターン配列[" + (patterns == null ? "null" : patterns.length) + "] でマッチングを実行します。");
        return isMatch(patterns, currPath);
    }

    /**
     * 新しいマッチングスタッフを生成します。
     * @return CommonRouterStaffインスタンス
     */
    public static CommonRouterStaff newMatch() {
        logger.info("newMatch: 新しいCommonRouterStaffを生成します。");
        return new CommonRouterStaff();
    }

    /**
     * パターン配列でマッチングを行うスタッフを生成します。
     * @param patterns ルートパターン配列
     * @return CommonRouterStaffインスタンス
     */
    public static CommonRouterStaff match(String... patterns) {
        logger.info("match(String...): パターン配列でマッチングスタッフを生成します。");
        return new CommonRouterStaff().match(patterns);
    }

    /**
     * パターン配列でマッチしないスタッフを生成します。
     * @param patterns ルートパターン配列
     * @return CommonRouterStaffインスタンス
     */
    public static CommonRouterStaff notMatch(String... patterns) {
        logger.info("notMatch(String...): パターン配列でnotMatchスタッフを生成します。");
        return new CommonRouterStaff().notMatch(patterns);
    }

    /**
     * パターンリストでマッチングを行うスタッフを生成します。
     * @param patterns ルートパターンリスト
     * @return CommonRouterStaffインスタンス
     */
    public static CommonRouterStaff match(List<String> patterns) {
        logger.info("match(List): パターンリストでマッチングスタッフを生成します。");
        return new CommonRouterStaff().match(patterns);
    }

    /**
     * パターンリストでマッチしないスタッフを生成します。
     * @param patterns ルートパターンリスト
     * @return CommonRouterStaffインスタンス
     */
    public static CommonRouterStaff notMatch(List<String> patterns) {
        logger.info("notMatch(List): パターンリストでnotMatchスタッフを生成します。");
        return new CommonRouterStaff().notMatch(patterns);
    }

    /**
     * boolean値でマッチングを行うスタッフを生成します。
     * @param flag マッチフラグ
     * @return CommonRouterStaffインスタンス
     */
    public static CommonRouterStaff match(boolean flag) {
        logger.info("match(boolean): フラグ[" + flag + "]でマッチングスタッフを生成します。");
        return new CommonRouterStaff().match(flag);
    }

    /**
     * boolean値でマッチしないスタッフを生成します。
     * @param flag マッチフラグ
     * @return CommonRouterStaffインスタンス
     */
    public static CommonRouterStaff notMatch(boolean flag) {
        logger.info("notMatch(boolean): フラグ[" + flag + "]でnotMatchスタッフを生成します。");
        return new CommonRouterStaff().notMatch(flag);
    }

    /**
     * 関数でマッチングを行うスタッフを生成します。
     * @param fun 判定関数
     * @return CommonRouterStaffインスタンス
     */
    public static CommonRouterStaff match(ParamRetFunction<Object, Boolean> fun) {
        logger.info("match(ParamRetFunction): 関数でマッチングスタッフを生成します。");
        return new CommonRouterStaff().match(fun);
    }

    /**
     * 関数でマッチしないスタッフを生成します。
     * @param fun 判定関数
     * @return CommonRouterStaffインスタンス
     */
    public static CommonRouterStaff notMatch(ParamRetFunction<Object, Boolean> fun) {
        logger.info("notMatch(ParamRetFunction): 関数でnotMatchスタッフを生成します。");
        return new CommonRouterStaff().notMatch(fun);
    }

    /**
     * パターンと関数でマッチングを行うスタッフを生成します。
     * @param pattern ルートパターン
     * @param fun 実行関数
     * @return CommonRouterStaffインスタンス
     */
    public static CommonRouterStaff match(String pattern, Function fun) {
        logger.info("match(String, Function): パターン[" + pattern + "]で関数付きマッチングスタッフを生成します。");
        return new CommonRouterStaff().match(pattern, fun);
    }

    /**
     * パターンと関数でマッチングを行うスタッフを生成します。
     * @param pattern ルートパターン
     * @param fun 実行関数
     * @return CommonRouterStaffインスタンス
     */
    public static CommonRouterStaff match(String pattern, ParamFunction<CommonRouterStaff> fun) {
        logger.info("match(String, ParamFunction): パターン[" + pattern + "]で関数付きマッチングスタッフを生成します。");
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
        logger.info("match(String, String, Function): パターン[" + pattern + "]、除外パターン[" + excludePattern + "]で関数付きマッチングスタッフを生成します。");
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
        logger.info("match(String, String, ParamFunction): パターン[" + pattern + "]、除外パターン[" + excludePattern + "]で関数付きマッチングスタッフを生成します。");
        return new CommonRouterStaff().match(pattern, excludePattern, fun);
    }

    /**
     * マッチングを停止します（例外をスロー）。
     * @return なし
     */
    public static CommonRouterStaff stop() {
        logger.warning("stop: マッチングを停止します。StopMatchExceptionをスローします。");
        throw new StopMatchException();
    }

    /**
     * マッチングを停止し、結果を返します（例外をスロー）。
     * @return なし
     */
    public static CommonRouterStaff back() {
        logger.warning("back: マッチングを停止し、空の結果を返します。BackResultExceptionをスローします。");
        throw new BackResultException("");
    }

    /**
     * マッチングを停止し、指定した結果を返します（例外をスロー）。
     * @param result 返却する結果
     * @return なし
     */
    public static CommonRouterStaff back(Object result) {
        logger.warning("back(Object): マッチングを停止し、結果[" + result + "]を返します。BackResultExceptionをスローします。");
        throw new BackResultException(result);
    }

}
