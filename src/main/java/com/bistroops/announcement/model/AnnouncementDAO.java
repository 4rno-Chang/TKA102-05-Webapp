package com.bistroops.announcement.model;

import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.bistroops.util.HibernateUtil;

public class AnnouncementDAO implements AnnouncementDAO_interface {
<<<<<<< Upstream, based on branch 'master' of https://github.com/4rno-Chang/TKA102-05-Webapp
//	Hibernate version
=======
	private static DataSource ds = null;
	static {
		try {
			Context ctx = new InitialContext();
			ds = (DataSource) ctx.lookup("java:comp/env/jdbc/TestDB2");
		} catch (NamingException e) {
			e.printStackTrace();
		}
	} 
//	JDBC version 
	private static final String FIND_BY_ANNNO_STMT = "SELECT ann_no, ann_title, ann_begin, ann_img, ann_text FROM announcement WHERE ann_no = ?";
	private static final String GET_ALL_STMT = "SELECT ann_no, ann_title, ann_begin, ann_img, ann_text FROM announcement";
	private static final String INSERT_ANN = "INSERT INTO project.announcement(ann_title, ann_begin, ann_img, ann_text) VALUES (?, ?, ?, ?)";
	private static final String UPDATE_ANN = "UPDATE project.announcement SET ann_title = ?, ann_begin = ?, ann_img = ?, ann_text = ? WHERE ann_no = ?";
	private static final String UPDATE_ANN_NO_IMG = "UPDATE project.announcement SET ann_title = ?, ann_begin = ?, ann_text = ? WHERE ann_no = ?";
	private static final String DELETE_ANN = "DELETE FROM project.announcement WHERE ann_no = ?";
>>>>>>> 9843f06 JDBC Version

	@Override
	public AnnouncementVO findByAnnNo(Integer annNo) {
		Session session = HibernateUtil.getSessionFactory().openSession();

		try {
			String FindByAnn = "FROM AnnouncementVO WHERE annNo = :annNo";
			return session.createQuery(FindByAnn, AnnouncementVO.class).setParameter("annNo", annNo).uniqueResult();
			
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new RuntimeException("something error occured. " + he.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Database error. " + e.getMessage());
		} finally {
			session.close();
		}
	}

	@Override
	public List<AnnouncementVO> getAll() {
		Session session = HibernateUtil.getSessionFactory().openSession();

		try {
			String findAll = "FROM AnnouncementVO";
			return session.createQuery(findAll, AnnouncementVO.class).getResultList();
			
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new RuntimeException("something error occured. " + he.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Database error. " + e.getMessage());
		} finally {
			session.close();
		}
	}

	@Override
	public void insert(AnnouncementVO annVO) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;

		try {
			transaction = session.beginTransaction();
			session.persist(annVO);
			transaction.commit();

		} catch (HibernateException he) {
			if(transaction != null)
				transaction.rollback();
			throw new RuntimeException("something error occured. " + he.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Database error. " + e.getMessage());
		} finally {
			session.close();
		}
	}

	@Override
	public void update(AnnouncementVO annVO) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		try {
			transaction = session.beginTransaction();
			Integer updateNo = annVO.getAnnNo();
			AnnouncementVO ann = session.find(AnnouncementVO.class, updateNo);
			
			ann.setAnnTitle(annVO.getAnnTitle());
			ann.setAnnBegin(annVO.getAnnBegin());
			ann.setAnnText(annVO.getAnnText());
			ann.setAnnImg(annVO.getAnnImg());
			
			transaction.commit();

		} catch(HibernateException he) {
			if(transaction != null)
				transaction.rollback();
			he.printStackTrace();
			throw new RuntimeException("something error occured. " + he.getMessage());
		} catch(Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Database error" + e.getMessage());
		} finally {
			session.close();
		}
	}

	@Override
	public void updateNoImg(AnnouncementVO annVO) {		
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;

		try {
			transaction = session.beginTransaction();
			Integer updateNo = annVO.getAnnNo();
			AnnouncementVO ann = session.find(AnnouncementVO.class, updateNo);
			
			ann.setAnnTitle(annVO.getAnnTitle());
			ann.setAnnBegin(annVO.getAnnBegin());
			ann.setAnnText(annVO.getAnnText());
			
			transaction.commit();

		} catch (HibernateException he) {
			if(transaction != null)
				transaction.rollback();
			he.printStackTrace();
			throw new RuntimeException("something error occured. " + he.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Database error. " + e.getMessage());
		} finally {
			session.close();
		}
	}

	@Override
	public void delete(Integer annNo) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		
		try {
			transaction = session.beginTransaction();
			AnnouncementVO ann = session.find(AnnouncementVO.class, annNo);
			session.remove(ann);
			transaction.commit();

		} catch (HibernateException he) {
			if(transaction != null)
				transaction.rollback();
			he.printStackTrace();
			throw new RuntimeException("something error occured. " + he.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Database error. " + e.getMessage());
		} finally {
			session.close();
		}
	}
}