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
		
		String sql = "update board_check set check_yn='Y', check_date=sysdate "
				+ " where bno=? and member_id=?";
		
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
		
		String sql = "select \r\n"
				+ "    p.member_id,\r\n"
				+ "    m.member_name,\r\n"
				+ "    case\r\n"
				+ "        when c.check_yn = 'Y' then '確認完了'\r\n"
				+ "        else '未確認'\r\n"
				+ "    end as check_status\r\n"
				+ "from board b\r\n"
				+ "join project_member p\r\n"
				+ "    on b.project_id = p.project_id\r\n"
				+ "join member m\r\n"
				+ "    on p.member_id = m.member_id\r\n"
				+ "left outer join board_check c\r\n"
				+ "    on p.member_id = c.member_id\r\n"
				+ "    and b.bno = c.bno\r\n"
				+ "where b.bno = ?\r\n"
				+ "    AND p.member_id <> b.writer\r\n"
				+ "order by m.member_name";
		
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
	
	
	// 회원별 게시물 확인 기록
	public int insertUnreadChecks(int bno) {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		String sql = "insert into board_check (bno, member_id, check_yn)\r\n"
				+ "select b.bno, pm.member_id, 'N'\r\n"
				+ "from board b\r\n"
				+ "join project_member pm\r\n"
				+ "on b.project_id = pm.project_id\r\n"
				+ "where b.bno =? and b.check_required='Y' and pm.member_id <> b.writer";
		
		int result = 0;
		try {
			conn = DBUtil.getInstance();
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, bno);
			
			result = pstmt.executeUpdate();
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
		return result;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
