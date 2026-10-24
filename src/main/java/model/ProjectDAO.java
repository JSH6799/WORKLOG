package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import util.DBUtil;

public class ProjectDAO {

	// 회원이 참여한 프로젝트 목록 조회
	public List<ProjectDTO> selectMyProjects(String memberId){
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "select p.project_id, p.team_name, p.project_name\r\n"
				+ "from project p\r\n"
				+ "join project_member pm\r\n"
				+ "on p.project_id = pm.project_id\r\n"
				+ "where pm.member_id = ?";
		
		List<ProjectDTO> list = new ArrayList<ProjectDTO>();
		
		try {
			conn = DBUtil.getInstance();
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, memberId);
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				ProjectDTO dto = new ProjectDTO();
				
				dto.setProjectId(rs.getString("project_id"));
				dto.setTeamName(rs.getString("team_name"));
				dto.setProjectName(rs.getString("project_name"));
				
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
