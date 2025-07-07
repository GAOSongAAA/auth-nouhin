package com.collaboportal.common.filter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.model.MaintenanceBody;
import com.collaboportal.common.config.CommonConfig;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * メンテナンスモードフィルタークラス
 * メンテナンスモードが有効な場合、インデックスページのURLを変更する
 */
public class MaintenanceModeFilter extends OncePerRequestFilter {

    // ロガー
    private static final Logger logger = LoggerFactory.getLogger(MaintenanceModeFilter.class);

    /**
     * コンストラクタ
     * 初期化時にログを出力
     */
    public MaintenanceModeFilter() {
        logger.debug("メンテナンスモードフィルターが初期化されました");
    }

    /**
     * フィルターロジックを実行するメソッド
     * @param request HTTPリクエストオブジェクト
     * @param response HTTPレスポンスオブジェクト
     * @param filterChain フィルターチェーン
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 設定を取得
        CommonConfig config = ConfigManager.getConfig();
        if (config != null) {
            String indexPage = config.getIndexPage();
            
            // メンテナンスモードが有効な場合
            if ("1".equals(MaintenanceBody.getMentFlg())) {
                logger.info("メンテナンスモードが有効です。インデックスページを変更します");
                config.setIndexPage(indexPage.replaceAll("com$", "com:442"));
            } 
            // メンテナンスモードが無効な場合
            else {
                logger.info("メンテナンスモードが無効です。インデックスページを元に戻します");
                config.setIndexPage(indexPage.replaceAll(":442", ""));
            }
        }

        // 次のフィルターに処理を渡す
        filterChain.doFilter(request, response);
    }
}
