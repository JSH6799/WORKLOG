package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import util.DBUtil;

public class BoardCheckDAO {

	// 확인 완료 처리
	public boolean insertCheck(int bno, String memberId) {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		String sql = "insert into board_check (bno, member_id, check_yn, check_date)\r\n"
				+ " values (?, ?, 'Y', SYSDATE)";
		
		try {
			conn = DBUtil.getInstance();
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, bno);
			pstmt.setString(2, memberId);

			int result = pstmt.executeUpdate();
			return result > 0;
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	
	// 게시글 확인 현황 조회
	public boolean isChecked(int bno, String memberId) {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "select check_yn from board_check "
				+ " where bno=? and member_id=?";
		
		try {
			conn = DBUtil.getInstance();
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, bno);
			pstmt.setString(2, memberId);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				return "Y".equals(rs.getString("check_yn"));
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
