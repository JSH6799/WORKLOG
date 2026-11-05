package service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;

import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.MemberDAO;
import model.MemberDTO;

public class MemberUpdateCommand implements Command {

    private final Gson gson = new Gson();

    @Override
    public boolean doCommand(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    	response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("loginMember") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            sendJson(response, false, "再度ログインしてください。");
            return false;
        }

        // 비밀번호 확인을 통과했는지 검사 (5분 이내)
        Object verifiedAt = session.getAttribute("memberEditVerifiedAt");

        if (!(verifiedAt instanceof Long) 
        		|| System.currentTimeMillis() - (Long) verifiedAt > 5 * 60 * 1000L
                || (Long) verifiedAt > System.currentTimeMillis()) {

            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            sendJson(response, false, "本人確認をやり直してください。");
            return false;
        }

        MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");

        String memberName = request.getParameter("memberName");
        String email = request.getParameter("email");
        String newPassword = request.getParameter("newPassword");

        if (memberName == null || memberName.isBlank() || email == null || email.isBlank()) {
            sendJson(response, false, "名前とメールアドレスを入力してください。");
            return false;
        }

        memberName = memberName.trim();
        email = email.trim();

        if (memberName.length() > 30) {
            sendJson(response, false, "名前は30文字以内で入力してください。");
            return false;
        }

        MemberDTO dto = new MemberDTO();
        dto.setMemberId(loginMember.getMemberId());
        dto.setMemberName(memberName);
        dto.setEmail(email);

        if (newPassword != null && !newPassword.isEmpty()) {
            dto.setPwd(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        }

        MemberDAO dao = new MemberDAO();
        boolean success = dao.update(dto);

        if (success) {
            loginMember.setMemberName(memberName);
            loginMember.setEmail(email);

            if (newPassword != null && !newPassword.isEmpty()) {
                loginMember.setPwd(dto.getPwd());
            }

            session.setAttribute("loginMember", loginMember);
            session.removeAttribute("memberEditVerifiedAt");
            sendJson(response, true, "会員情報を更新しました。");
        } else {
            sendJson(response, false, "会員情報の更新に失敗しました。");
        }
        return false;
    }

    private void sendJson(HttpServletResponse response, boolean success, String message) throws IOException {

        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("message", message);

        response.getWriter().write(gson.toJson(result));
    }
}