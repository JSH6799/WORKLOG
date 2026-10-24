package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import util.DBUtil;

public class BoardDAO {

	// 게시글 등록
	public int insert(BoardDTO dto) {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 = null;
		ResultSet rs = null;
		
		String sql = "insert into board (bno, title, content, writer, img_path, project_id, check_required) "
				+ " values (?,?,?,?,?,?,?)";
		String sql2 = "select seq_board.nextval from dual";
		
		int bno = 0;
		int result = 0;
		
		try {
			conn = DBUtil.getInstance();
			pstmt2 = conn.prepareStatement(sql2);
			rs = pstmt2.executeQuery();
			
			if(rs.next()) {
				bno = rs.getInt(1);
			}
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, bno);
			pstmt.setString(2, dto.getTitle());
			pstmt.setString(3, dto.getContent());
			pstmt.setString(4, dto.getWriter());
			pstmt.setString(5, dto.getImgPath());
			pstmt.setString(6, dto.getProjectId());
			pstmt.setString(7, dto.getCheckRequired());

			result = pstmt.executeUpdate();
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(rs != null) rs.close();
				if(pstmt2 != null) pstmt2.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(result == 1) {
			return bno;
		}
		return 0;
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


	// 게시글
	public BoardDTO selectView(int bno) {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "select b.bno, b.title, b.content, b.writer, m.member_name as writer_name,\r\n"
				+ "       to_char(b.write_date, 'YYYY-MM-DD') as write_date, b.img_path,\r\n"
				+ "       b.project_id, b.check_required\r\n"
				+ "from board b\r\n"
				+ "join member m\r\n"
				+ "on b.writer = m.member_id\r\n"
				+ "where b.bno = ?";
		
		BoardDTO dto = null;
		
		try {
			conn = DBUtil.getInstance();
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, bno);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dto = new BoardDTO();
				
				dto.setBno(rs.getInt("bno"));
				dto.setTitle(rs.getString("title"));
				dto.setContent(rs.getString("content"));
				dto.setWriter(rs.getString("writer"));
				dto.setWriterName(rs.getString("writer_name"));
				dto.setWriteDate(rs.getString("write_date"));
				dto.setImgPath(rs.getString("img_path"));
				dto.setProjectId(rs.getString("project_id"));
				dto.setCheckRequired(rs.getString("check_required"));
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


	// 수정
	public void update(BoardDTO dto) {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		String sql = "update board set title=?, content=?, img_path=?, check_required=?\r\n"
				+ "where bno=? and writer=? and project_id=?";
		
		try {
			conn = DBUtil.getInstance();
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getTitle());
			pstmt.setString(2, dto.getContent());
			pstmt.setString(3, dto.getImgPath());
			pstmt.setString(4, dto.getCheckRequired());
			pstmt.setInt(5, dto.getBno());
			pstmt.setString(6, dto.getWriter());
			pstmt.setString(7, dto.getProjectId());
			
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


	// 삭제
	public String delete(int bno, String writer, String projectId) {
		
		String sql = "delete from board where bno=? and writer=? and project_id=?";
		String sql2 = "select img_path from board where bno=? and writer=? and project_id=?";
		String imgPath = null;
		
		try(Connection conn = DBUtil.getInstance()) {
			try(PreparedStatement pstmt = conn.prepareStatement(sql2)){
				
				pstmt.setInt(1, bno);
				pstmt.setString(2, writer);
				pstmt.setString(3, projectId);
				
				try(ResultSet rs = pstmt.executeQuery()){
					if(rs.next()) {
						imgPath = rs.getString("img_path");
					}else {
						return null;
					}
				}
			}
			try(PreparedStatement pstmt = conn.prepareStatement(sql)){
				
				pstmt.setInt(1, bno);
				pstmt.setString(2, writer);
				pstmt.setString(3, projectId);
				
				pstmt.executeUpdate();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return imgPath;
	}


	// 프로젝트별 미확인 게시물 건수 조회
	public int selectUnreadCountByProject(String memberId, String projectId) {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "select count(*)\r\n"
				+ "from board b\r\n"
				+ "join board_check c\r\n"
				+ "on b.bno = c.bno\r\n"
				+ "where b.project_id = ? and c.member_id = ? and b.check_required = 'Y'\r\n"
				+ "      and c.check_yn = 'N' and b.writer <> ?";
		
		try {
			conn = DBUtil.getInstance();
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, projectId);
			pstmt.setString(2, memberId);
			pstmt.setString(3, memberId);
			
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

	
	// 프로젝트별 미확인 게시물 목록 조회
	public List<BoardDTO> selectUnreadListByProject(String memberId, String projectId, int startRow, int endRow){
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "select b.bno, b.title, b.writer, b.project_id, b.write_date\r\n"
				+ "from board b\r\n"
				+ "join board_check c\r\n"
				+ "on b.bno = c.bno\r\n"
				+ "where b.project_id = ? and c.member_id = ? and b.check_required = 'Y'\r\n"
				+ "      and c.check_yn = 'N' and b.writer <> ?\r\n"
				+ "order by b.write_date desc";
		
		List<BoardDTO> list = new ArrayList<BoardDTO>();
		
		try {
			conn = DBUtil.getInstance();
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, projectId);
			pstmt.setString(2, memberId);
			pstmt.setString(3, memberId);
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				BoardDTO dto = new BoardDTO();
				
				dto.setBno(rs.getInt("bno"));
				dto.setTitle(rs.getString("title"));
				dto.setWriter(rs.getString("writer"));
				dto.setProjectId(rs.getString("project_id"));
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
	
	
	// 마이페이지용 미확인 게시물 목록
	public List<BoardDTO> unreadListForMyPage(String projectId, String memberId){
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "select b.bno, b.title, b.writer, b.project_id,\r\n"
				+ "       to_char(b.write_date, 'YYYY-MM-DD') as write_date\r\n"
				+ "from board b\r\n"
				+ "join board_check c\r\n"
				+ "on b.bno = c.bno\r\n"
				+ "where b.project_id = ? and c.member_id = ? and b.check_required = 'Y'\r\n"
				+ "      and c.check_yn = 'N' and b.writer <> ?\r\n"
				+ "order by b.write_date desc";
		
		List<BoardDTO> list = new ArrayList<BoardDTO>();
		
		try {
			conn = DBUtil.getInstance();
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, projectId);
			pstmt.setString(2, memberId);
			pstmt.setString(3, memberId);
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				BoardDTO dto = new BoardDTO();
				
				dto.setBno(rs.getInt("bno"));
				dto.setTitle(rs.getString("title"));
				dto.setWriter(rs.getString("writer"));
				dto.setProjectId(rs.getString("project_id"));
				dto.setWriteDate(rs.getString("write_date"));
				
				list.add(dto);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		finally {
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
