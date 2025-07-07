package com.collaboportal.common.jwt.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.collaboportal.common.ConfigManager;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/mr")
public class MrController {

    private String INDEX_PAGE = ConfigManager.getConfig().getIndexPage() + "/index.html";

    /** ロガー */
    Logger logger = LoggerFactory.getLogger(MrController.class);

    @GetMapping()
    public void HonbuPage(
            HttpServletRequest request,
            HttpServletResponse response) throws JsonProcessingException {
        logger.info("リクエストを受信しました");
        logger.debug("リダイレクト先: {}", INDEX_PAGE);

        try {
            response.setHeader("Location", INDEX_PAGE);
            response.setStatus(302);
            logger.info("リダイレクト処理が正常に完了しました。ステータスコード: 302");
        } catch (Exception e) {
            logger.error("リダイレクト処理中にエラーが発生しました: {}", e.getMessage());
            throw e;
        }
    }
}