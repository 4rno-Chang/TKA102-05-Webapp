package com.bistroops.announcement.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

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
			con = ds.getConnection();
			pstmt = con.prepareStatement(FIND_BY_ANNNO_STMT);
			pstmt.setInt(1, annNo);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				annVO = new AnnouncementVO();
				annVO.setAnnNo(rs.getInt("ann_no"));
				annVO.setAnnTitle(rs.getString("ann_title"));
				annVO.setAnnBegin(rs.getTimestamp("ann_begin").toLocalDateTime());
				annVO.setAnnImg(rs.getBytes("ann_img"));
				annVO.setAnnText(rs.getString("ann_text"));
			}

		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
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
			con = ds.getConnection();
			pstmt = con.prepareStatement(GET_ALL_STMT);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				annVO = new AnnouncementVO();
				annVO.setAnnNo(rs.getInt("ann_no"));
				annVO.setAnnTitle(rs.getString("ann_title"));
				annVO.setAnnBegin(rs.getTimestamp("ann_begin").toLocalDateTime());
				annVO.setAnnImg(rs.getBytes("ann_img"));
				annVO.setAnnText(rs.getString("ann_text"));
				list.add(annVO);
			}

		} catch (SQLException se) {
			throw new RuntimeException("Database error. " + se.getMessage());
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
			con = ds.getConnection();
			pstmt = con.prepareStatement(UPDATE_ANN);

			pstmt.setString(1, annVO.getAnnTitle());
			pstmt.setTimestamp(2, Timestamp.valueOf(annVO.getAnnBegin()));
			pstmt.setBytes(3, annVO.getAnnImg());
			pstmt.setString(4, annVO.getAnnText());

			pstmt.setInt(5, annVO.getAnnNo());

			pstmt.executeUpdate();

		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
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