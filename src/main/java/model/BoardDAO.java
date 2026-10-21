package model;

import java.sql.Connection;
import java.sql.PreparedStatement;

import util.DBUtil;

public class BoardDAO {

	// 게시글 등록
	public void insert(BoardDTO dto) {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		String sql = "insert into board (bno, title, content, writer, img_path, project_id, check_required) "
				+ " values (seq_board.nextval,?,?,?,?,?,?)";
		
		try {
			conn = DBUtil.getInstance();
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getTitle());
			pstmt.setString(2, dto.getContent());
			pstmt.setString(3, dto.getWriter());
			pstmt.setString(4, dto.getImgPath());
			pstmt.setString(5, dto.getProjectId());
			pstmt.setString(6, dto.getCheckRequired());
			
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
	
}
