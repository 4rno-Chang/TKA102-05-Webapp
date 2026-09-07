package com.bistroops.announcement.model;

import java.util.*;
import com.bistroops.announcement.model.AnnouncementVO;

public interface AnnouncementDAO_interface {
	public List<AnnouncementVO> getAll();
	public AnnouncementVO findByAnnNo(Integer annNo);
	public void insert(AnnouncementVO annVO);
	public void update(AnnouncementVO annVO);
	public void updateNoImg(AnnouncementVO annVo);
	public void delete(Integer annNo);
}