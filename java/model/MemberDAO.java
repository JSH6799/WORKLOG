package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import util.DBUtil;

public class MemberDAO {

	// ID重複チェック
	public boolean idCheck(String memberId) {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "select member_id from member where member_id = ?";
				
		boolean isDuplicate = false;
		
		try {
			conn = DBUtil.getInstance();
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, memberId);
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				return true;
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
		return isDuplicate;
	}
	
	
	// 会員登録
	public void memberJoin(MemberDTO dto) {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		String sql = "insert into member (member_id, pwd, member_name, email) "
				+ " values (?,?,?,?)";
		
		try {
			conn = DBUtil.getInstance();
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getMemberId());
			pstmt.setString(2, dto.getPwd());
			pstmt.setString(3, dto.getMemberName());
			pstmt.setString(4, dto.getEmail());
			
			pstmt.executeUpdate();
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
	}
	
	
	// ログイン
	public MemberDTO login(String memberId) {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "select member_id, pwd, member_name, email "
				+ " from member where member_id = ?";
		
		MemberDTO dto = null;
		
		try {
			conn = DBUtil.getInstance();
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, memberId);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				dto = new MemberDTO();
				
				dto.setMemberId(rs.getString("member_id"));
				dto.setPwd(rs.getString("pwd"));
				dto.setMemberName(rs.getString("member_name"));
				dto.setEmail(rs.getString("email"));
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
		return dto;
	}

	// マイページ会員情報取得
	public MemberDTO selectMember(String memberId) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "select member_id, member_name, email "
				+ "from member where member_id = ?";

		MemberDTO dto = null;

		try {
			conn = DBUtil.getInstance();
			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, memberId);

			rs = pstmt.executeQuery();

			if(rs.next()) {
				dto = new MemberDTO();

				dto.setMemberId(rs.getString("member_id"));
				dto.setMemberName(rs.getString("member_name"));
				dto.setEmail(rs.getString("email"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return dto;
	}

	// 会員情報修正
	public boolean update(MemberDTO dto) {

	    Connection conn = null;
	    PreparedStatement pstmt = null;

	    String sql = "update member set member_name = ?, email = ?, pwd = NVL(?, pwd) "
	            + "where member_id = ?";

	    boolean result = false;

	    try {
	        conn = DBUtil.getInstance();
	        pstmt = conn.prepareStatement(sql);

	        pstmt.setString(1, dto.getMemberName());
	        pstmt.setString(2, dto.getEmail());
	        pstmt.setString(3, dto.getPwd());
	        pstmt.setString(4, dto.getMemberId());

	        int count = pstmt.executeUpdate();

	        if (count > 0) {
	            result = true;
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            if (pstmt != null) pstmt.close();
	            if (conn != null) conn.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	    return result;
	}
	
}
