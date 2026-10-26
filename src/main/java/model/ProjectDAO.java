package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import util.DBUtil;

public class ProjectDAO {

	
	
	 private static ProjectDAO instance = new ProjectDAO();
	    public static ProjectDAO getInstance() { return instance; }
	    private ProjectDAO() {}
	
	// 프로젝트 목록 조회
    public List<ProjectDTO> getProjectList() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<ProjectDTO> list = new ArrayList<>();
        String sql = "SELECT project_id, team_name, project_name, start_date, end_date, description, creator_id FROM project ORDER BY start_date DESC";
        
        try {
            conn = DBUtil.getInstance();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while(rs.next()) {
                ProjectDTO dto = new ProjectDTO();
                dto.setProjectId(rs.getString("project_id"));
                dto.setTeamName(rs.getString("team_name"));
                dto.setProjectName(rs.getString("project_name"));
                dto.setStartDate(rs.getString("start_date"));
                dto.setEndDate(rs.getString("end_date"));
                dto.setDescription(rs.getString("description"));
                dto.setCreatorId(rs.getString("creator_id"));
                list.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(rs != null) rs.close();
                if(pstmt != null) pstmt.close();
                if(conn != null) conn.close();
            } catch (Exception e) { e.printStackTrace(); }
        }
        return list;
    }

    // 프로젝트 상세 조회
    public ProjectDTO getProjectById(String projectId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ProjectDTO dto = null;
        String sql = "SELECT project_id, team_name, project_name, start_date, end_date, description, creator_id FROM project WHERE project_id = ?";
        
        try {
            conn = DBUtil.getInstance();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, projectId);
            rs = pstmt.executeQuery();
            if(rs.next()) {
                dto = new ProjectDTO();
                dto.setProjectId(rs.getString("project_id"));
                dto.setTeamName(rs.getString("team_name"));
                dto.setProjectName(rs.getString("project_name"));
                dto.setStartDate(rs.getString("start_date"));
                dto.setEndDate(rs.getString("end_date"));
                dto.setDescription(rs.getString("description"));
                dto.setCreatorId(rs.getString("creator_id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(rs != null) rs.close();
                if(pstmt != null) pstmt.close();
                if(conn != null) conn.close();
            } catch (Exception e) { e.printStackTrace(); }
        }
        return dto;
    }

    // 프로젝트 생성 (트랜잭션 적용)
    public void insertProject(ProjectDTO dto, String creatorId) {
        Connection conn = null;
        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;
        
        String sql1 = "INSERT INTO project (project_id, team_name, project_name, start_date, end_date, description, creator_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sql2 = "INSERT INTO project_member (project_id, member_id, role) VALUES (?, ?, 'OWNER')";
        
        try {
            conn = DBUtil.getInstance();
            conn.setAutoCommit(false);
            
            pstmt1 = conn.prepareStatement(sql1);
            pstmt1.setString(1, dto.getProjectId());
            pstmt1.setString(2, dto.getTeamName() != null ? dto.getTeamName() : "Default Team");
            pstmt1.setString(3, dto.getProjectName());
            pstmt1.setString(4, dto.getStartDate());
            pstmt1.setString(5, dto.getEndDate());
            pstmt1.setString(6, dto.getDescription());
            pstmt1.setString(7, creatorId);
            pstmt1.executeUpdate();
            
            pstmt2 = conn.prepareStatement(sql2);
            pstmt2.setString(1, dto.getProjectId());
            pstmt2.setString(2, creatorId);
            pstmt2.executeUpdate();
            
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            try { if(conn != null) conn.rollback(); } catch(Exception ex) { ex.printStackTrace(); }
        } finally {
            try {
                if(pstmt1 != null) pstmt1.close();
                if(pstmt2 != null) pstmt2.close();
                if(conn != null) { conn.setAutoCommit(true); conn.close(); }
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    // 프로젝트 수정
    public void updateProject(ProjectDTO dto) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "UPDATE project SET project_name = ?, description = ?, start_date = ?, end_date = ? WHERE project_id = ?";
        
        try {
            conn = DBUtil.getInstance();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, dto.getProjectName());
            pstmt.setString(2, dto.getDescription());
            pstmt.setString(3, dto.getStartDate());
            pstmt.setString(4, dto.getEndDate());
            pstmt.setString(5, dto.getProjectId());
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(pstmt != null) pstmt.close();
                if(conn != null) conn.close();
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    // 프로젝트 삭제
    public void deleteProject(String projectId) {
        Connection conn = null;
        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;
        String sql1 = "DELETE FROM project_member WHERE project_id = ?";
        String sql2 = "DELETE FROM project WHERE project_id = ?";
        
        try {
            conn = DBUtil.getInstance();
            conn.setAutoCommit(false);
            
            pstmt1 = conn.prepareStatement(sql1);
            pstmt1.setString(1, projectId);
            pstmt1.executeUpdate();
            
            pstmt2 = conn.prepareStatement(sql2);
            pstmt2.setString(1, projectId);
            pstmt2.executeUpdate();
            
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            try { if(conn != null) conn.rollback(); } catch(Exception ex) { ex.printStackTrace(); }
        } finally {
            try {
                if(pstmt1 != null) pstmt1.close();
                if(pstmt2 != null) pstmt2.close();
                if(conn != null) { conn.setAutoCommit(true); conn.close(); }
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    // 프로젝트 멤버 목록 조회
    public List<ProjectMemberDTO> getProjectMembers(String projectId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<ProjectMemberDTO> list = new ArrayList<>();
        String sql = "SELECT pm.project_id, pm.member_id, pm.role, m.member_name, m.email " +
                     "FROM project_member pm JOIN member m ON pm.member_id = m.member_id " +
                     "WHERE pm.project_id = ?";
        
        try {
            conn = DBUtil.getInstance();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, projectId);
            rs = pstmt.executeQuery();
            while(rs.next()) {
                ProjectMemberDTO dto = new ProjectMemberDTO();
                dto.setProjectId(rs.getString("project_id"));
                dto.setMemberId(rs.getString("member_id"));
                dto.setRole(rs.getString("role"));
                dto.setMemberName(rs.getString("member_name"));
                dto.setEmail(rs.getString("email"));
                list.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(rs != null) rs.close();
                if(pstmt != null) pstmt.close();
                if(conn != null) conn.close();
            } catch (Exception e) { e.printStackTrace(); }
        }
        return list;
    }

    // 멤버 권한 변경
    public void updateMemberRole(String projectId, String memberId, String newRole) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "UPDATE project_member SET role = ? WHERE project_id = ? AND member_id = ?";
        
        try {
            conn = DBUtil.getInstance();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newRole);
            pstmt.setString(2, projectId);
            pstmt.setString(3, memberId);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(pstmt != null) pstmt.close();
                if(conn != null) conn.close();
            } catch (Exception e) { e.printStackTrace(); }
        }
    }
	
}
