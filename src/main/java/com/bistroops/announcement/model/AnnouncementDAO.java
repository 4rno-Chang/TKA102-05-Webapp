package com.bistroops.announcement.model;

import java.time.LocalDateTime;
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
//	Hibernate version

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
			
//			先找VO.no -> find找到這個VO -> 一個一個set annVO(Update傳過來的資料) ->commit
//			Integer updateNo = annVO.getAnnNo();
//			AnnouncementVO ann = session.find(AnnouncementVO.class, updateNo);
//			ann.setAnnTitle(annVO.getAnnTitle());
//			ann.setAnnBegin(annVO.getAnnBegin());
//			ann.setAnnText(annVO.getAnnText());
//			ann.setAnnImg(annVO.getAnnImg());
			
//			直接用session.merge(Update傳過來的資料)
			session.merge(annVO);
			
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