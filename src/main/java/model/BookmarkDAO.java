package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import util.DBUtil;

public class BookmarkDAO {

	// 해당 회원이 북마크 했는지 확인
	public boolean isBookmarked(int bno, String memberId) {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "select 1 from bookmark where bno = ? and member_id = ?";
		
		try {
			conn = DBUtil.getInstance();
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, bno);
			pstmt.setString(2, memberId);
			
			rs = pstmt.executeQuery();

			return rs.next();
			
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
	
	public boolean toggle(int bno, String memberId) {
		
		String checkSql = "select 1 from bookmark where bno = ? and member_id = ?";
		String insertSql = "insert into bookmark (bno, member_id) values (?,?)";
		String deleteSql = "delete from bookmark where bno = ? and member_id = ?";
		
		boolean exists;
		
		try(Connection conn = DBUtil.getInstance()){
			conn.setAutoCommit(false);
			try {
				try(PreparedStatement pstmt = conn.prepareStatement(checkSql)){
					pstmt.setInt(1, bno);
					pstmt.setString(2, memberId);
					try(ResultSet rs = pstmt.executeQuery()){
						exists = rs.next();
					}
				}
				
				try(PreparedStatement pstmt = conn.prepareStatement(exists ? deleteSql : insertSql)){
					pstmt.setInt(1, bno);
					pstmt.setString(2, memberId);
					pstmt.executeUpdate();
				}
				conn.commit();
				return !exists;
			}catch (SQLException e) {
				conn.rollback();
				throw e;
			}
		}catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("ブックマーク処理に失敗しました", e);
		}
	}
	
	
	// 북마크 목록
	public List<BoardDTO> selectBookmarkList(String memberId, String projectId, int startRow, int endRow){
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "select * from (\r\n"
				+ "    select b.bno, b.title, p.project_name, p.project_id,\r\n"
				+ "           to_char(bmk.bookmark_date, 'YYYY-MM-DD') as bookmark_date,\r\n"
				+ "           row_number() over (\r\n"
				+ "               order by bmk.bookmark_date desc, bmk.bno desc\r\n"
				+ "           ) as rn \r\n"
				+ "    from bookmark bmk\r\n"
				+ "    join board b\r\n"
				+ "    on bmk.bno = b.bno\r\n"
				+ "    join project p\r\n"
				+ "    on b.project_id = p.project_id\r\n"
				+ "    where bmk.member_id = ? and (? is null or p.project_id = ?)\r\n"
				+ ")\r\n"
				+ "where rn between ? and ?\r\n"
				+ "order by rn";
		
		List<BoardDTO> list = new ArrayList<BoardDTO>();
		
		try {
			conn = DBUtil.getInstance();
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, memberId);
			pstmt.setString(2, projectId);
			pstmt.setString(3, projectId);
			pstmt.setInt(4, startRow);
			pstmt.setInt(5, endRow);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				BoardDTO dto = new BoardDTO();
				
				dto.setBno(rs.getInt("bno"));
				dto.setTitle(rs.getString("title"));
				dto.setProjectName(rs.getString("project_name"));
				dto.setProjectId(rs.getString("project_id"));
				dto.setBookmarkDate(rs.getString("bookmark_date"));
				
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
	
	
	// 북마크 삭제
	public boolean deleteBookmarks(List<Integer> bnoList, String memberId) {
		
		if(bnoList == null || bnoList.isEmpty()) {
			return false;
		}
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		String sql = "delete from bookmark where bno = ? and member_id = ?";
		
		try {
			conn = DBUtil.getInstance();
			pstmt = conn.prepareStatement(sql);
			conn.setAutoCommit(false);
			
			for(int bno : bnoList) {
				pstmt.setInt(1, bno);
				pstmt.setString(2, memberId);
				pstmt.addBatch();
			}
			
			pstmt.executeBatch();
			conn.commit();
			
			return true;
			
		}catch (Exception e) {
			try {
				if(conn != null) conn.rollback();
			}catch (Exception ex) {
				ex.printStackTrace();
			}
			e.printStackTrace();
			return false;
		}finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	

	// 북마크 목록 검색
	public List<BoardDTO> searchBookmarkList(
			String memberId, String projectId, String searchType,
			String keyword, int startRow, int endRow) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		List<BoardDTO> list = new ArrayList<BoardDTO>();

		StringBuilder sql = new StringBuilder(
				"select * from ( "
				+ " select b.bno, b.title, p.project_name, p.project_id, "
				+ " to_char(bmk.bookmark_date, 'YYYY-MM-DD') as bookmark_date, "
				+ " row_number() over (order by bmk.bookmark_date desc, bmk.bno desc) as rn "
				+ " from bookmark bmk "
				+ " join board b on bmk.bno = b.bno "
				+ " join project p on b.project_id = p.project_id "
				+ " left join member m on b.writer = m.member_id "
				+ " where bmk.member_id = ? "
				+ " and (? is null or p.project_id = ?) "
		);

		if ("title".equals(searchType)) {
			sql.append(" and lower(b.title) like '%' || lower(?) || '%' ");
		} else if ("content".equals(searchType)) {
			sql.append(" and dbms_lob.instr(b.content, ?) > 0 ");
		} else if ("writer".equals(searchType)) {
			sql.append(" and lower(m.member_name) like '%' || lower(?) || '%' ");
		} else {
			searchType = null;
		}

		if (searchType == null) {
			sql.setLength(0);
			sql.append(
					"select * from ( "
					+ " select b.bno, b.title, p.project_name, p.project_id, "
					+ " to_char(bmk.bookmark_date, 'YYYY-MM-DD') as bookmark_date, "
					+ " row_number() over (order by bmk.bookmark_date desc, bmk.bno desc) as rn "
					+ " from bookmark bmk "
					+ " join board b on bmk.bno = b.bno "
					+ " join project p on b.project_id = p.project_id "
					+ " where bmk.member_id = ? "
					+ " and (? is null or p.project_id = ?) "
			);
		}

		sql.append(") where rn between ? and ? order by rn");

		try {
			conn = DBUtil.getInstance();
			pstmt = conn.prepareStatement(sql.toString());

			int index = 1;
			pstmt.setString(index++, memberId);
			pstmt.setString(index++, projectId);
			pstmt.setString(index++, projectId);

			if (searchType != null) {
				pstmt.setString(index++, keyword.trim());
			}

			pstmt.setInt(index++, startRow);
			pstmt.setInt(index++, endRow);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				BoardDTO dto = new BoardDTO();

				dto.setBno(rs.getInt("bno"));
				dto.setTitle(rs.getString("title"));
				dto.setProjectName(rs.getString("project_name"));
				dto.setProjectId(rs.getString("project_id"));
				dto.setBookmarkDate(rs.getString("bookmark_date"));

				list.add(dto);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) rs.close();
				if (pstmt != null) pstmt.close();
				if (conn != null) conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return list;
	}

	
	
	
	
	
	
	
}
