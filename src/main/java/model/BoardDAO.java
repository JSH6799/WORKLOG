package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

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
	
	
	// 목록 조회. 검색 조건에 맞는 where 절
	private String buildWhere(String searchType, String keyword) {
		if(keyword == null || keyword.trim().isEmpty()) {
			return "";
		}
		
		if("title".equals(searchType)) {
			return " where b.title like ? ";
		}else if("content".equals(searchType)) {
			return " where b.content like ? ";
		}else if("writer".equals(searchType)) {
			return " where m.member_name like ? ";
		}
		return "";
	}
	
	

	// 해당 프로젝트에서 검색 조건에 맞는 게시글 개수 조회
	public int selectCount(String projectId, String searchType, String keyword) {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String where = buildWhere(searchType, keyword);
		String sql = "SELECT COUNT(*) "
				+ " FROM board b "
				+ " JOIN member m ON b.writer = m.member_id "
				+ " WHERE b.project_id = ? "
				+ (where.isEmpty() ? "" : " AND " + where.trim().substring(5));
		
		try {
			conn = DBUtil.getInstance();
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, projectId);
			if(!where.isEmpty()) {
				pstmt.setString(2, "%"+keyword+"%");
			}
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				return rs.getInt(1);
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
		return 0;
	}
		
		
	// 게시글 목록 조회
	public List<BoardDTO> selectList(String searchType, String keyword, String projectId, int startRow, int endRow){
		
		String where = buildWhere(searchType, keyword);
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "select * from (\r\n"
				+ "    select rownum as rn, t.* from(\r\n"
				+ "        select b.bno, b.title, b.writer, m.member_name as writer_name,\r\n"
				+ "               to_char(b.write_date, 'YYYY-MM-DD') as write_date\r\n"
				+ "        from board b\r\n"
				+ "        join member m\r\n"
				+ "        on b.writer = m.member_id\r\n"
				+ "		   where b.project_id = ? \r\n"
				+		   (where.isEmpty() ? "" : " AND " + where.trim().substring(5) + "\r\n")
				+ "        order by b.bno desc\r\n"
				+ "        ) t where rownum <= ?\r\n"
				+ "    ) where rn >= ?\r\n";
		
		List<BoardDTO> list = new ArrayList<BoardDTO>();
		
		try {
			conn = DBUtil.getInstance();
			pstmt = conn.prepareStatement(sql);
			
			int idx = 1;
			
			pstmt.setString(idx++, projectId);

			if(!where.isEmpty()) {
				pstmt.setString(idx++, "%"+keyword+"%");
			}
			pstmt.setInt(idx++, endRow);
			pstmt.setInt(idx, startRow);
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				BoardDTO dto = new BoardDTO();
				
				dto.setBno(rs.getInt("bno"));
				dto.setTitle(rs.getString("title"));
				dto.setWriter(rs.getString("writer"));
				dto.setWriterName(rs.getString("writer_name"));
				dto.setWriteDate(rs.getString("write_date"));
				
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
