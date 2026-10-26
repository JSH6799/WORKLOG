package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import util.DBUtil;

public class ProjectMemberDao {
	
	//특정 프로젝트에 참여한 팀원 목록 조회
	public List<ProjectMemberDTO> search(String ProjectId){
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT pm.project_id, pm.member_id, pm.role, \"\r\n"
				+ " m.member_name, m.email \"\r\n"
				+ " FROM project_member pm \"\r\n"
				+ " JOIN member m ON pm.member_id = m.member_id \"\r\n"
				+ " WHERE pm.project_id = ?"; 
				
		List<ProjectMemberDTO> list = new ArrayList<ProjectMemberDTO>();
		
		try {
			conn = DBUtil.getInstance();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, "projectId");
			rs = pstmt.executeQuery();
			while(rs.next()) {
				
				ProjectMemberDTO dto = new ProjectMemberDTO();
				dto.setProjectId(rs.getString("project_id"));
				dto.setMemberId(rs.getString("memberId"));
				dto.setRole(rs.getString("Role"));
				dto.setMemberName(rs.getString("member_nane"));
				dto.setEmail(rs.getString("email"));
				
				list.add(dto);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
				
		return list;
	}
	
	
	// 기존 회원을 프로젝트 팀원으로 추가
	public boolean insert(ProjectMemberDTO dto) {

	    Connection conn = null;
	    PreparedStatement pstmt = null;

	    String sql = "INSERT INTO project_member "
	               + "(project_id, member_id, role) "
	               + "VALUES (?, ?, ?)";

	    try {
	        conn = DBUtil.getInstance();
	        pstmt = conn.prepareStatement(sql);

	        pstmt.setString(1, dto.getProjectId());
	        pstmt.setString(2, dto.getMemberId());
	        pstmt.setString(3, dto.getRole());

	        int result = pstmt.executeUpdate();

	        return result == 1;

	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	// 팀원의 역할 수정
	public boolean updateRole(ProjectMemberDTO dto) {

	    String sql =
	        "UPDATE project_member " +
	        "SET role = ? " +
	        "WHERE project_id = ? AND member_id = ?";

	    try {
	        Connection conn = DBUtil.getInstance();

	        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

	            pstmt.setString(1, dto.getRole());
	            pstmt.setString(2, dto.getProjectId());
	            pstmt.setString(3, dto.getMemberId());

	            int result = pstmt.executeUpdate();

	            return result == 1;
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	// 프로젝트에서 팀원 참여 삭제
	public boolean delete(String projectId, String memberId) {

	    String sql =
	        "DELETE FROM project_member " +
	        "WHERE project_id = ? AND member_id = ?";

	    try {
	        Connection conn = DBUtil.getInstance();

	        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

	            pstmt.setString(1, projectId);
	            pstmt.setString(2, memberId);

	            int result = pstmt.executeUpdate();

	            return result == 1;
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
}
