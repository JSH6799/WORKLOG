package service;

import java.io.IOException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import util.DBUtil;

/**
 * メンバー招待 모달의 「コード生成」 버튼 처리 (초대 코드 생성).
 * 결과를 JSON 으로 직접 응답에 써서 돌려준다. (JSP 로 forward 하지 않음)
 */
public class InviteCreateCommand implements Command {

    // doCommand 의 반환값: true = 컨트롤러가 JSP 로 forward / false = forward 안 함
    // 이 클래스는 JSON 을 응답에 직접 써서 보내므로 항상 false
    private static final boolean RESULT_AFTER_DIRECT_RESPONSE = false;

    private static final int VALID_DAYS = 7; // 코드 유효기간(일)
    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // 헷갈리는 문자(0,O,1,I) 제외
    private static final SecureRandom RND = new SecureRandom();

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

        String memberId = getLoginId(request);
        if (memberId == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return RESULT_AFTER_DIRECT_RESPONSE;
        }

        String projectId = request.getParameter("projectId");
        if (projectId == null || projectId.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return RESULT_AFTER_DIRECT_RESPONSE;
        }

        try (Connection conn = DBUtil.getInstance()) {

            // 1) 이 프로젝트의 멤버인지 확인 (남의 프로젝트 초대코드 생성 방지)
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT 1 FROM project_member WHERE project_id = ? AND member_id = ?")) {
                ps.setString(1, projectId);
                ps.setString(2, memberId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        response.sendError(HttpServletResponse.SC_FORBIDDEN);
                        return RESULT_AFTER_DIRECT_RESPONSE;
                    }
                }
            }

            // 2) 시퀀스 번호 받기
            long seq;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT project_invitation_seq.NEXTVAL FROM DUAL");
                 ResultSet rs = ps.executeQuery()) {
                rs.next();
                seq = rs.getLong(1);
            }

            // 3) 코드 만들기: WL- + 시퀀스(36진수) + 랜덤 4글자
            //    시퀀스만 쓰면 추측하기 쉬워서 랜덤 문자를 뒤에 붙임
            StringBuilder suffix = new StringBuilder();
            for (int i = 0; i < 4; i++) {
                suffix.append(CHARS.charAt(RND.nextInt(CHARS.length())));
            }
            String code = "WL-" + Long.toString(seq, 36).toUpperCase() + suffix;

            // 4) 저장
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO project_invitation " +
                    "(invitation_id, project_id, inviter_id, invitee_id, invitation_status, " +
                    " invited_date, invite_code, expires_date) " +
                    "VALUES (?, ?, ?, NULL, 'PENDING', SYSDATE, ?, SYSDATE + ?)")) {
                ps.setLong(1, seq);
                ps.setString(2, projectId);
                ps.setString(3, memberId);
                ps.setString(4, code);
                ps.setInt(5, VALID_DAYS);
                ps.executeUpdate();
            }

            // 5) JSON 으로 응답 (JSP 의 fetch 가 받음)
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write("{\"code\":\"" + code + "\",\"expiresAt\":\""
                    + LocalDate.now().plusDays(VALID_DAYS) + "\"}");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        return RESULT_AFTER_DIRECT_RESPONSE;
    }
}