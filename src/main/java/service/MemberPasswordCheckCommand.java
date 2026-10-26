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

public class MemberPasswordCheckCommand implements Command {

    private final Gson gson = new Gson();

    @Override
    public boolean doCommand(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    	response.setCharacterEncoding("utf-8");

    	response.setContentType("application/json");
        response.setHeader("Cache-Control", "no-store");

        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            sendJson(response, false, "不正なリクエストです。");
            return false;
        }

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("loginMember") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            sendJson(response, false, "再度ログインしてください。");
            return false;
        }

        MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");

        String currentPassword = request.getParameter("currentPassword");

        if (currentPassword == null || currentPassword.isBlank()) {
            sendJson(response, false, "パスワードを入力してください。");
            return false;
        }

        MemberDAO dao = new MemberDAO();
        MemberDTO member = dao.login(loginMember.getMemberId());

        boolean passwordMatches = false;

        if (member != null && member.getPwd() != null) {
            try {
                passwordMatches = BCrypt.checkpw(currentPassword, member.getPwd());
            } catch (IllegalArgumentException e) {
                passwordMatches = false;
            }
        }

        if (!passwordMatches) {
            sendJson(response, false, "パスワードが一致しません。");
            return false;
        }

        session.setAttribute("memberEditVerifiedAt", System.currentTimeMillis());

        sendJson(response, true, null);
        return false;
    }

    
    private void sendJson(HttpServletResponse response, boolean success, String message) throws IOException {

        Map<String, Object> result = new HashMap<>();
        result.put("success", success);

        if (message != null) {
            result.put("message", message);
        }

        response.getWriter().write(gson.toJson(result));
    }
}
