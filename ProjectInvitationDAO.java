
package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProjectInvitationDAO {

    // 1. 초대 전송
    public boolean insert(ProjectInvitationDTO dto) {

        String sql =
            "INSERT INTO WORKLOG.project_invitation "
          + "(invitation_id, project_id, inviter_id, "
          + "invitee_id, role) "
          + "VALUES (project_invitation_seq.NEXTVAL, ?, ?, ?, ?)";

        try (
            Connection conn = util.DBUtil.getInstance();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setString(1, dto.getProjectId());
            pstmt.setString(2, dto.getInviterId());
            pstmt.setString(3, dto.getInviteeId());
            pstmt.setString(4, dto.getRole());

            return pstmt.executeUpdate() == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 2. 받은 대기 초대 목록 조회
    public List<ProjectInvitationDTO> selectPending(
            String inviteeId) {

        List<ProjectInvitationDTO> list = new ArrayList<>();

        String sql =
            "SELECT invitation_id, project_id, inviter_id, "
          + "invitee_id, role, invitation_status, to_char(invited_date, 'yyyy-mm-dd') as invited_date "
          + "FROM project_invitation "
          + "WHERE invitee_id = ? "
          + "AND invitation_status = 'PENDING' "
          + "ORDER BY invited_date DESC";

        try (
            Connection conn = util.DBUtil.getInstance();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setString(1, inviteeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ProjectInvitationDTO dto =
                        new ProjectInvitationDTO();

                    dto.setInvitationId(
                        rs.getInt("invitation_id"));
                    dto.setProjectId(
                        rs.getString("project_id"));
                    dto.setInviterId(
                        rs.getString("inviter_id"));
                    dto.setInviteeId(
                        rs.getString("invitee_id"));
                    dto.setRole(
                        rs.getString("role"));
                    dto.setInvitationStatus(
                        rs.getString("invitation_status"));
                    dto.setInvitedDate(rs.getString("invited_date"));

                    list.add(dto);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
    
 // 3. 초대 수락 또는 거절
    public boolean updateStatus(int invitationId, String status) {
        String sql =
            "UPDATE project_invitation "
          + "SET invitation_status = ?, "
          + "responded_date = SYSDATE "
          + "WHERE invitation_id = ? "
          + "AND invitation_status = 'PENDING'";

        try (
            Connection conn = util.DBUtil.getInstance();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setString(1, status);
            pstmt.setInt(2, invitationId);

            return pstmt.executeUpdate() == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
 // 초대 수락 + 프로젝트 멤버 등록
    public boolean acceptInvitation(int invitationId, String inviteeId)
            throws SQLException {

        Connection conn = null;

        try {
            conn = util.DBUtil.getInstance();

            // 트랜잭션 시작
            conn.setAutoCommit(false);

            // 1. 수락할 초대 정보 조회 및 잠금
            String selectSql =
                "SELECT project_id, role "
              + "FROM project_invitation "
              + "WHERE invitation_id = ? "
              + "AND invitee_id = ? "
              + "AND invitation_status = 'PENDING' "
              + "FOR UPDATE";

            String projectId;
            String role;

            try (PreparedStatement pstmt = conn.prepareStatement(selectSql)) {
                pstmt.setInt(1, invitationId);
                pstmt.setString(2, inviteeId);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }

                    projectId = rs.getString("project_id");
                    role = rs.getString("role");
                }
            }

            // 2. 프로젝트 멤버 등록
            ProjectMemberDTO member = new ProjectMemberDTO();
            member.setProjectId(projectId);
            member.setMemberId(inviteeId);
            member.setRole(role);

            ProjectMemberDao memberDAO = new ProjectMemberDao();

            if (!memberDAO.insert(member, conn)) {
                conn.rollback();
                return false;
            }

            // 3. 초대 상태를 ACCEPTED로 변경
            String updateSql =
                "UPDATE project_invitation "
              + "SET invitation_status = 'ACCEPTED', "
              + "responded_date = SYSDATE "
              + "WHERE invitation_id = ? "
              + "AND invitee_id = ? "
              + "AND invitation_status = 'PENDING'";

            try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                pstmt.setInt(1, invitationId);
                pstmt.setString(2, inviteeId);

                if (pstmt.executeUpdate() != 1) {
                    conn.rollback();
                    return false;
                }
            }

            // 4. 두 작업 모두 성공하면 확정
            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
 // 초대 거절
    public boolean rejectInvitation(int invitationId, String inviteeId) {

        String sql =
            "UPDATE WORKLOG.PROJECT_INVITATION "
          + "SET invitation_status = 'REJECTED', "
          + "responded_date = SYSDATE "
          + "WHERE invitation_id = ? "
          + "AND invitee_id = ? "
          + "AND invitation_status = 'PENDING'";

        try (
            Connection conn = util.DBUtil.getInstance();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, invitationId);
            pstmt.setString(2, inviteeId);

            return pstmt.executeUpdate() == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
