package com.bistroops.qa.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bistroops.qa.model.QaService;
import com.bistroops.qa.model.QaVO;

/*
 * 前台常見問題的資料 API（ThymeToJS 分支：畫面改由 JS 產生，這裡只回 JSON）
 *   GET /bistroops/api/qa          全部，點擊多的在前（常見問題頁）
 *   GET /bistroops/api/qa?limit=5  點擊最多的 5 題（首頁）
 * 畫面在 static/js/front/qa-render.js；點擊次數 +1 還是 FrontQaController 的 POST /bistroops/qa/{qaNo}/click
 */
@RestController
@RequestMapping("/bistroops/api/qa")
public class QaApiController {

	// 回給前端的欄位：只放畫面要用的，不把 QaVO 整個送出去（點擊次數不用給顧客看）
	public record QaItem(Integer qaNo, String qaTitle, String qaContent) {
		static QaItem of(QaVO qa) {
			return new QaItem(qa.getQaNo(), qa.getQaTitle(), qa.getQaContent());
		}
	}

	@Autowired
	private QaService qaService;

	@GetMapping
	public ResponseEntity<List<QaItem>> list(@RequestParam(required = false) Integer limit) {
		// limit 是 0 或負數：PageRequest 會丟例外，先回 400
		if (limit != null && limit < 1) {
			return ResponseEntity.badRequest().build();
		}
		List<QaVO> qaList = (limit == null) ? qaService.getAllPopularFirst() : qaService.getPopular(limit);
		return ResponseEntity.ok(qaList.stream().map(QaItem::of).toList());
	}
}
