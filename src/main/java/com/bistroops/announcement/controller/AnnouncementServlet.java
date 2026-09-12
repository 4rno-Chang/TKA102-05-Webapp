package com.bistroops.announcement.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.util.List;

import com.bistroops.announcement.model.AnnouncementService;
import com.bistroops.announcement.model.AnnouncementVO;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet({ "/ann/ann.do", "/ann/ann.img" })
@MultipartConfig
public class AnnouncementServlet extends HttpServlet {
	AnnouncementService annService = new AnnouncementService();
	// private AnnouncementService annService;
//	@Override
//	public void init() throws ServletException {
//		annService = new AnnouncementServiceImpl();
//	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		String action = req.getParameter("action");
		String forwardPath = "";

		if ("/ann/ann.img".equals(req.getServletPath())) {
			getImage(req, res);
			return;
		}

		switch (action) {
		case "getAll":
			forwardPath = getAll(req, res);
			break;
		case "annNoQuery":
			forwardPath = annNoQuery(req, res);
			break;
		case "insertAnnPage":
			forwardPath = "/announcement/insertAnnPage.jsp";
			break;
		case "insertAnn":
//		    forwardPath = insertAnn(req, res);
//			insertAnn(req, res);
			String insertResult = insertAnn(req, res);
		    if (insertResult != null) {
		        forwardPath = insertResult;
		        break;
		    }
			return;
		case "updateAnnPage":
			forwardPath = updateAnnPage(req, res);
			break;
		case "updateAnn":
//			forwardPath = updateAnn(req, res);
//			updateAnn(req, res);

			String updateResult = updateAnn(req, res);
			if (updateResult != null) {
				forwardPath = updateResult;
				break;
			}
			return;
		case "deleteAnn":
			forwardPath = deleteAnn(req, res);
			break;
		default:
			forwardPath = "/announcement/index.jsp";
		}

		res.setContentType("text/html; charset=UTF-8");
		RequestDispatcher dispatcher = req.getRequestDispatcher(forwardPath);
		dispatcher.forward(req, res);
	}

	private String getAll(HttpServletRequest req, HttpServletResponse res) {
		List<AnnouncementVO> annList = annService.getAll();
		req.setAttribute("annList", annList);

		return "/announcement/listAllAnns.jsp";
	}

	private String annNoQuery(HttpServletRequest req, HttpServletResponse res) {
		// 防呆
		String annNoStr = req.getParameter("annNo");
		Integer annNo = null;

		if (annNoStr != null && !annNoStr.trim().isEmpty()) {
			// 原本的查詢功能
			try {
				annNo = Integer.parseInt(annNoStr.trim());
			} catch (NumberFormatException e) {
				req.setAttribute("errorMsgNoQuery", "公告編號格式錯誤");
				return "/announcement/index.jsp";
			}
		} else {
			Object newAnnNo = req.getSession().getAttribute("newAnnNo");
			if (newAnnNo != null) {
				annNo = (Integer) newAnnNo;
				req.getSession().removeAttribute("newAnnNo");
			}
		}

		// 原本的查詢功能
		if (annNo == null) {
			req.setAttribute("errorMsgNoQuery", "請輸入公告編號");
			return "/announcement/index.jsp";
		}
		AnnouncementVO ann = annService.getAnnNoQuery(annNo);

		if (ann == null) {
			req.setAttribute("errorMsgNoQuery", "查無此公告編號：" + annNo);
			return "/announcement/index.jsp";
		}
		req.setAttribute("ann", ann);
		return "/announcement/listOneAnn.jsp";
	}

	private String insertAnn(HttpServletRequest req, HttpServletResponse res) {
		String annTitleStr = req.getParameter("annTitle");
		String annBeginStr = req.getParameter("annBegin");
		String annTextStr = req.getParameter("annText");

		if (annTitleStr == null || annTitleStr.trim().isEmpty()) {
			req.setAttribute("errorMsgInsertTitle", "請輸入公告標題");
			return "/announcement/index.jsp";
		}
		if (annBeginStr == null || annBeginStr.trim().isEmpty()) {
			req.setAttribute("errorMsgInsertBeginNotInput", "請輸入公告時間");
			return "/announcement/index.jsp";
		}
		if (annTextStr == null || annTextStr.trim().isEmpty()) {
			req.setAttribute("errorMsgInsertText", "請輸入公告內文");
			return "/announcement/index.jsp";
		}

		try {
			String annTitle = annTitleStr.trim();
			LocalDateTime annBegin = LocalDateTime.parse(annBeginStr);

			if (annBegin.isBefore(LocalDateTime.now())) {
				req.setAttribute("errorMsgInsertBegin", "公告開始時間不能選擇過去的時間");
				return "/announcement/index.jsp";
			}

			String annText = annTextStr;
			Part annImgPart = req.getPart("annImg");
			byte[] annImg = null;

			if (annImgPart != null && annImgPart.getSize() > 0) {
//			    annImg = annImgPart.getInputStream().readAllBytes();
				BufferedInputStream bis = new BufferedInputStream(annImgPart.getInputStream());
				annImg = bis.readAllBytes();
				bis.close();
			}

			Integer annNo = annService.insertAnn(annTitle, annBegin, annImg, annText);

//			res.sendRedirect(req.getContextPath() + "/ann/ann.do?action=getAll");
			req.getSession().setAttribute("newAnnNo", annNo);
			res.sendRedirect(req.getContextPath() + "/ann/ann.do?action=annNoQuery");
			return null;

		} catch (Exception e) {
			e.printStackTrace();
			req.setAttribute("errorMsgInsert", "新增公告失敗");
			return "/announcement/index.jsp";
		}

//		return "/announcement/index.jsp";
	}

	private String updateAnnPage(HttpServletRequest req, HttpServletResponse res) {
		Integer annNo = Integer.parseInt(req.getParameter("annNo"));
		AnnouncementVO ann = annService.getAnnNoQuery(annNo);
		req.setAttribute("ann", ann);

		return "/announcement/updateAnnPage.jsp";
	}

	private String updateAnn(HttpServletRequest req, HttpServletResponse res) {
		String annNoStr = req.getParameter("annNo");
		String annTitleStr = req.getParameter("annTitle");
		String annBeginStr = req.getParameter("annBegin");
		String annTextStr = req.getParameter("annText");

		if (annTitleStr == null || annTitleStr.trim().isEmpty()) {
			req.setAttribute("errorMsg", "請輸入公告標題");
			return "/announcement/index.jsp";
		}

		try {
			Integer annNo = Integer.parseInt(annNoStr);
			String annTitle = annTitleStr.trim();
			LocalDateTime annBegin = LocalDateTime.parse(annBeginStr);
			String annText = annTextStr;
			Part annImgPart = req.getPart("annImg");
			byte[] annImg = null;

			if (annBegin.isBefore(LocalDateTime.now())) {
				req.setAttribute("errorMsgUpdateBegin", "公告開始時間不能選擇過去的時間");
				return "/announcement/index.jsp";
			}

			if (annImgPart != null && annImgPart.getSize() > 0) {
				BufferedInputStream bis = new BufferedInputStream(annImgPart.getInputStream());
				annImg = bis.readAllBytes();
				bis.close();
			}

			annService.updateAnn(annNo, annTitle, annBegin, annImg, annText);

			req.getSession().setAttribute("newAnnNo", annNo);
			res.sendRedirect(req.getContextPath() + "/ann/ann.do?action=annNoQuery");
			return null;

		} catch (Exception e) {
			e.printStackTrace();
			req.setAttribute("errorMsg", "更新公告失敗");
			return "/announcement/index.jsp";
		}

	}

	private String deleteAnn(HttpServletRequest req, HttpServletResponse res) {
		String annNoStr = req.getParameter("annNo");

		Integer annNo = Integer.parseInt(annNoStr.trim());
		annService.deleteAnn(annNo);

		return "/announcement/index.jsp";
	}

	private void getImage(HttpServletRequest req, HttpServletResponse res) throws IOException {
		Integer annNo = Integer.parseInt(req.getParameter("annNo"));
		AnnouncementVO ann = annService.getAnnNoQuery(annNo);
		byte[] img = ann.getAnnImg();

		if (img != null) {
//			res.setContentType("image/jpg");
			String mimeType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(img));
			if (mimeType == null) {
				mimeType = "application/octet-stream"; // 無法辨識時的保底值
			}
			res.setContentType(mimeType);
			BufferedOutputStream bos = new BufferedOutputStream(res.getOutputStream());

			bos.write(img);

			bos.flush();
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		doPost(req, res);
	}

}