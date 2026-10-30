package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import util.DBUtil;

public class ProjectMemberDAO {
	
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
				dto.setMemberId(rs.getString("member_id"));
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
}