package com.bistroops.announcement.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.bistroops.announcement.model.AnnouncementVO;

public class AnnouncementService {

	private AnnouncementDAO_interface dao;

	public AnnouncementService() {
		dao = new AnnouncementDAO();
	}

	public List<AnnouncementVO> getAll() {
		return dao.getAll();
	}

	public AnnouncementVO getAnnNoQuery(Integer annNo) {
		return dao.findByAnnNo(annNo);
	}

	public Integer insertAnn(String annTitle, LocalDateTime annBegin, byte[] annImg, String annText) {
		AnnouncementVO ann = new AnnouncementVO();

		ann.setAnnTitle(annTitle);
		ann.setAnnBegin(annBegin);
		ann.setAnnImg(annImg);
		ann.setAnnText(annText);
		
		dao.insert(ann);
		
		return ann.getAnnNo();
	}
	
	public void updateAnn(Integer annNo, String annTitle, LocalDateTime annBegin, byte[] annImg, String annText) {
		AnnouncementVO ann = new AnnouncementVO();
		
		ann.setAnnNo(annNo);
		ann.setAnnTitle(annTitle);
		ann.setAnnBegin(annBegin);
		ann.setAnnImg(annImg);
		ann.setAnnText(annText);
		
		if (annImg != null && annImg.length > 0)
		    dao.update(ann);
		else
		    dao.updateNoImg(ann);
	}
	public void deleteAnn(Integer annNo) {
		dao.delete(annNo);
	}
}
