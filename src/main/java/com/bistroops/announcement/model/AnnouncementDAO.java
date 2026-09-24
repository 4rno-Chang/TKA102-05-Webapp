package com.bistroops.announcement.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.bistroops.util.HibernateUtil;

import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaBuilder;

public class AnnouncementDAO implements AnnouncementDAO_interface {
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

	@Override
	public AnnouncementVO findByAnnNo(Integer annNo) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		AnnouncementVO annVO = null;

		try {
//			============      hql      ============
//			String FindByAnn = "FROM AnnouncementVO WHERE annNo = :annNo";			
//			return session.createQuery(FindByAnn, AnnouncementVO.class).setParameter("annNo", annNo).uniqueResult();
			
//			============Hibernate method============
			return session.find(AnnouncementVO.class, annNo);
			
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new RuntimeException("something error occured. " + he.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Database error. " + e.getMessage());
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}
		return annVO;
	}

	@Override
	public List<AnnouncementVO> getAll() {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		List<AnnouncementVO> list = new ArrayList<>();
		AnnouncementVO annVO = null;

		try {
//			============      hql      ============
//			String findAll = "FROM AnnouncementVO";
//			return session.createQuery(findAll, AnnouncementVO.class).getResultList();
			
//			============Hibernate criteria============
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<AnnouncementVO> cq = cb.createQuery(AnnouncementVO.class);

			cq.from(AnnouncementVO.class);
			return session.createQuery(cq).getResultList();
			
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new RuntimeException("something error occured. " + he.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Database error. " + e.getMessage());
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}
		return list;
	}

	@Override
	public void insert(AnnouncementVO annVO) {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			con = ds.getConnection();
			pstmt = con.prepareStatement(INSERT_ANN);

			pstmt.setString(1, annVO.getAnnTitle());
			pstmt.setTimestamp(2, Timestamp.valueOf(annVO.getAnnBegin()));
			pstmt.setBytes(3, annVO.getAnnImg());
			pstmt.setString(4, annVO.getAnnText());

			pstmt.executeUpdate();

		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update(AnnouncementVO annVO) {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			transaction = session.beginTransaction();
			
//			先找annVO.no -> find找到這個VO -> 一個一個set(Update page傳過來的資料) ->commit
//			Integer updateNo = annVO.getAnnNo();
//			AnnouncementVO ann = session.find(AnnouncementVO.class, updateNo);
//			ann.setAnnTitle(annVO.getAnnTitle());
//			ann.setAnnBegin(annVO.getAnnBegin());
//			ann.setAnnText(annVO.getAnnText());
//			ann.setAnnImg(annVO.getAnnImg());
			
//			直接用session.merge(Update page傳過來的資料)
			session.merge(annVO);
			
			transaction.commit();

		} catch(HibernateException he) {
			if(transaction != null)
				transaction.rollback();
			he.printStackTrace();
			throw new RuntimeException("something error occured. " + he.getMessage());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateNoImg(AnnouncementVO annVO) {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {

			con = ds.getConnection();

			pstmt = con.prepareStatement(UPDATE_ANN_NO_IMG);

			pstmt.setString(1, annVO.getAnnTitle());
			pstmt.setTimestamp(2, Timestamp.valueOf(annVO.getAnnBegin()));
			pstmt.setString(3, annVO.getAnnText());
			pstmt.setInt(4, annVO.getAnnNo());

			pstmt.executeUpdate();

		} catch (SQLException se) {

			se.printStackTrace();

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	@Override
	public void delete(Integer annNo) {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			con = ds.getConnection();
			pstmt = con.prepareStatement(DELETE_ANN);

			pstmt.setInt(1, annNo);

			pstmt.executeUpdate();

		} catch (SQLException se) {

			se.printStackTrace();

		} catch (Exception e) {

			e.printStackTrace();
		}
	}
}