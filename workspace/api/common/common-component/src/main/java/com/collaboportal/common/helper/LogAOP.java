package com.collaboportal.common.helper;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class LogAOP{
    //ログ出力のためのクラス
    Logger logger = LoggerFactory.getLogger(LogAOP.class);

    // AOPを行う箇所の設定
    @Pointcut("execution(* com.collaboportal.shoho.controller.*Controller.*(..))")
    public void controller() {}
    @Pointcut("execution(* com.collaboportal.shoho.repository.*Service.*(..))")
    public void service() {}
    @Pointcut("execution(* com.collaboportal.shoho.repository.*Mapper.*(..))")
    public void repository() {}
    /**
     * Beforeアノテーションにより、指定したメソッドの前に処理を追加する
     * Beforeアノテーションの引数には、Pointcut式 execution(戻り値 パッケージ.クラス.メソッド(引数))
     * を指定し、ここではControllerクラスの全メソッドの実行前にログ出力するようにしている
     *
     * @param jp 横断的な処理を挿入する場所
     */
    @Before("service() || controller() || repository()")
    public void startLog(JoinPoint jp){
        //開始ログを出力
        String signature = jp.getSignature().toString();
        logger.trace("===== START: " + signature + " =====");
    }

    /**
     * Afterアノテーションにより、指定したメソッドの後に処理を追加する
     * Afterアノテーションの引数には、Pointcut式を指定
     *
     * @param jp 横断的な処理を挿入する場所
     */
    @AfterReturning("service() || controller() || repository()")
    public void endSuccesLog(JoinPoint jp){
        //終了ログを出力
        String signature = jp.getSignature().toString();
        logger.trace("===== END SUCCESS : " + signature + " =====");
    }

    /**
     * Afterアノテーションにより、指定したメソッドの後に処理を追加する
     * Afterアノテーションの引数には、Pointcut式を指定
     *
     * @param jp 横断的な処理を挿入する場所
     */
    @AfterThrowing("service() || controller() || repository()")
    public void endErrorLog(JoinPoint jp){
        //終了ログを出力
        String signature = jp.getSignature().toString();
        logger.trace("===== END ERROR : " + signature + " =====");
    }

}
