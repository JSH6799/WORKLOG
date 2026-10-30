package service;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import util.DBUtil;

/**
 * プロジェクト参加 모달의 「参加」 버튼 처리.
 * 입력값(projectLink)이 ① 프로젝트 링크  ② 초대 코드(WL-...)  둘 다 처리한다.
 * 성공하면 해당 프로젝트 화면으로 redirect, 실패하면 alert 후 이전 화면으로 되돌린다.
 */
public class ProjectJoinCommand implements Command {

    // doCommand 의 반환값: true = 컨트롤러가 JSP 로 forward / false = forward 안 함
    // 이 클래스는 redirect / alert 를 직접 응답하므로 항상 false
    private static final boolean RESULT_AFTER_DIRECT_RESPONSE = false;

    private static final String DEFAULT_ROLE = "MEMBER"; // project_member.role 은 NOT NULL 이라 기본값 필요

    /**
     * 세션 "loginMember" 에서 로그인한 회원의 아이디를 꺼낸다.
     *  - 아이디 문자열(String)을 저장했다면 그대로 사용
     *  - 회원 객체(VO)를 저장했다면 getMemberId() 를 호출
     *    (VO 의 getter 이름이 다르면 아래 "getMemberId" 를 그 이름으로 변경)
     */
    private String getLoginId(HttpServletRequest request) {
        Object o = request.getSession().getAttribute("loginMember");
        if (o == null) return null;
        if (o instanceof String) return (String) o;
        try {
            return String.valueOf(o.getClass().getMethod("getMemberId").invoke(o));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean doCommand(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String memberId = getLoginId(request);
        if (memberId == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return RESULT_AFTER_DIRECT_RESPONSE;
        }

        String input = request.getParameter("projectLink");
        if (input == null || input.trim().isEmpty()) {
            alertAndBack(response, "入力してください。");
            return RESULT_AFTER_DIRECT_RESPONSE;
        }
        input = input.trim();

        try (Connection conn = DBUtil.getInstance()) {
            conn.setAutoCommit(false);
            try {
                String projectId;
                Long invitationId = null; // 코드로 참가한 경우에만 값이 들어감

                if (input.contains("projectId=")) {
                    // ---- ① 링크로 참가 ----
                    projectId = extractProjectId(input);
                    if (projectId == null || !projectExists(conn, projectId)) {
                        alertAndBack(response, "プロジェクトが見つかりません。");
                        return RESULT_AFTER_DIRECT_RESPONSE;
                    }
                } else {
                    // ---- ② 초대 코드로 참가 ----
                    try (PreparedStatement ps = conn.prepareStatement(
                            "SELECT invitation_id, project_id FROM project_invitation " +
                            "WHERE invite_code = ? AND invitation_status = 'PENDING' " +
                            "AND expires_date > SYSDATE")) {
                        ps.setString(1, input.toUpperCase());
                        try (ResultSet rs = ps.executeQuery()) {
                            if (!rs.next()) {
                                alertAndBack(response, "無効または期限切れの招待コードです。");
                                return RESULT_AFTER_DIRECT_RESPONSE;
                            }
                            invitationId = rs.getLong(1);
                            projectId = rs.getString(2);
                        }
                    }
                }

                // 이미 참가 중인지 확인
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT 1 FROM project_member WHERE project_id = ? AND member_id = ?")) {
                    ps.setString(1, projectId);
                    ps.setString(2, memberId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            alertAndBack(response, "すでに参加しているプロジェクトです。");
                            return RESULT_AFTER_DIRECT_RESPONSE;
                        }
                    }
                }

                // 멤버 등록
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO project_member (project_id, member_id, role) VALUES (?, ?, ?)")) {
                    ps.setString(1, projectId);
                    ps.setString(2, memberId);
                    ps.setString(3, DEFAULT_ROLE);
                    ps.executeUpdate();
                }

                // 코드로 참가했다면 초대 상태를 ACCEPTED 로 (1회용 코드)
                if (invitationId != null) {
                    try (PreparedStatement ps = conn.prepareStatement(
                            "UPDATE project_invitation SET invitee_id = ?, " +
                            "invitation_status = 'ACCEPTED', responded_date = SYSDATE " +
                            "WHERE invitation_id = ?")) {
                        ps.setString(1, memberId);
                        ps.setLong(2, invitationId);
                        ps.executeUpdate();
                    }
                }

                conn.commit();
                response.sendRedirect(request.getContextPath()
                        + "/project/project.jsp?projectId=" + projectId);

            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
            alertAndBack(response, "エラーが発生しました。");
        }

        return RESULT_AFTER_DIRECT_RESPONSE;
    }

    private boolean projectExists(Connection conn, String projectId) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT 1 FROM project WHERE project_id = ?")) {
            ps.setString(1, projectId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /** 링크의 ?projectId=123 부분만 꺼냄 */
    private String extractProjectId(String link) {
        try {
            String q = new URI(link).getQuery();
            if (q == null) return null;
            for (String kv : q.split("&")) {
                if (kv.startsWith("projectId=")) return kv.substring("projectId=".length());
            }
        } catch (Exception ignore) { }
        return null;
    }

    private void alertAndBack(HttpServletResponse response, String msg) throws IOException {
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<script>alert('" + msg + "'); history.back();</script>");
    }
}