package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

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
	
	
	// 게시글 확인 유지?
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
	
	
	// 작성자용 게시글 확인 현황 조회
	public List<BoardCheckDTO> selectCheckList(int bno){
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT\r\n"
				+ "    p.member_id,\r\n"
				+ "    m.member_name,\r\n"
				+ "    CASE\r\n"
				+ "        WHEN c.check_yn = 'Y' THEN '確認完了'\r\n"
				+ "        ELSE '未確認'\r\n"
				+ "    END AS check_status\r\n"
				+ "FROM board b\r\n"
				+ "JOIN project_member p\r\n"
				+ "    ON b.project_id = p.project_id\r\n"
				+ "JOIN member m\r\n"
				+ "    ON p.member_id = m.member_id\r\n"
				+ "LEFT OUTER JOIN board_check c\r\n"
				+ "    ON p.member_id = c.member_id\r\n"
				+ "    AND b.bno = c.bno\r\n"
				+ "WHERE b.bno = ?\r\n"
				+ "ORDER BY m.member_name";
		
		List<BoardCheckDTO> list = new ArrayList<BoardCheckDTO>();
		
		try {
			conn = DBUtil.getInstance();
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, bno);
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				BoardCheckDTO dto = new BoardCheckDTO();
				
				dto.setMemberId(rs.getString("member_id"));
				dto.setMemberName(rs.getString("member_name"));
				dto.setCheckStatus(rs.getString("check_status"));
				
				list.add(dto);
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
		return list;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
