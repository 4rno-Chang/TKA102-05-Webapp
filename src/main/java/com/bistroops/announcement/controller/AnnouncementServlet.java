package com.bistroops.announcement.controller;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

import jakarta.servlet.*;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import com.bistroops.announcement.model.*;

@WebServlet({"/ann/ann.do", "/ann/ann.img"})
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
		    forwardPath = insertAnn(req, res);
		    break;
		case "updateAnnPage":
			forwardPath = updateAnnPage(req, res); 
			break;
		case "updateAnn":
			forwardPath = updateAnn(req, res);
			break;
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
		if (annNoStr == null || annNoStr.trim().isEmpty()) {
			req.setAttribute("errorMsg", "請輸入公告編號");
			return "/announcement/index.jsp";
		}

		try {
			Integer annNo = Integer.parseInt(annNoStr.trim());
			AnnouncementVO ann = annService.getAnnNoQuery(annNo);

			if (ann == null) {
				req.setAttribute("errorMsg", "查無此公告編號：" + annNo);
				return "/announcement/index.jsp";
			}
			req.setAttribute("ann", ann);
			return "/announcement/listOneAnn.jsp";

		} catch (NumberFormatException e) {
			req.setAttribute("errorMsg", "公告編號格式錯誤");
			return "/announcement/index.jsp";
		}
	}

	private String insertAnn(HttpServletRequest req, HttpServletResponse res) {
		String annTitleStr = req.getParameter("annTitle");
		String annBeginStr = req.getParameter("annBegin");
		String annTextStr = req.getParameter("annText");

		if (annTitleStr == null || annTitleStr.trim().isEmpty()) {
			req.setAttribute("errorMsg", "請輸入公告標題");
			return "/announcement/index.jsp";
		}

		try {
			String annTitle = annTitleStr.trim();
			LocalDateTime annBegin = LocalDateTime.parse(annBeginStr);
			String annText = annTextStr;
			Part annImgPart = req.getPart("annImg");
			byte[] annImg = null;
			
			if (annImgPart != null && annImgPart.getSize() > 0) {
//			    annImg = annImgPart.getInputStream().readAllBytes();
			    BufferedInputStream bis = new BufferedInputStream(annImgPart.getInputStream());
			    annImg = bis.readAllBytes();
			    bis.close();
			}		
			
			annService.insertAnn(annTitle, annBegin, annImg, annText);

		} catch (Exception e) {
			e.printStackTrace();
			req.setAttribute("errorMsg", "新增公告失敗");
			return "/announcement/index.jsp";
		}

		return "/announcement/index.jsp";
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
			
			if (annImgPart != null && annImgPart.getSize() > 0) {
			    BufferedInputStream bis = new BufferedInputStream(annImgPart.getInputStream());
			    annImg = bis.readAllBytes();
			    bis.close();
			}		
			
			annService.updateAnn(annNo, annTitle, annBegin, annImg, annText);

		} catch (Exception e) {
			e.printStackTrace();
			req.setAttribute("errorMsg", "新增公告失敗");
			return "/announcement/index.jsp";
		}

		return "/announcement/index.jsp";
	}
	
	private String deleteAnn(HttpServletRequest req, HttpServletResponse res) {
		String annNoStr = req.getParameter("annNo");
		
		Integer annNo = Integer.parseInt(annNoStr.trim());
		annService.deleteAnn(annNo);
		
		return "/announcement/index.jsp";
	}
	private void getImage(HttpServletRequest req, HttpServletResponse res) throws IOException{
		Integer annNo = Integer.parseInt(req.getParameter("annNo"));
		AnnouncementVO ann = annService.getAnnNoQuery(annNo);
		byte[] img = ann.getAnnImg();
		
		if(img != null) {
			res.setContentType("image/jpg");
			BufferedOutputStream bos = new BufferedOutputStream(res.getOutputStream());
			
			bos.write(img);
			
			bos.flush();
		}        
	}
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		if("/ann/ann.img".equals(req.getServletPath())) {
			getImage(req, res);
			return;
		}
		doPost(req, res);
	}

}