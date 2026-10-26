package service;

import java.io.IOException;

import org.mindrot.jbcrypt.BCrypt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.MemberDAO;
import model.MemberDTO;

public class MemberLoginCommand implements Command {

	@Override
	public boolean doCommand(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("utf-8");
		
		if("get".equalsIgnoreCase(request.getMethod())) {
			return true;
		}
		
		String memberId = request.getParameter("memberId");
		String pwd = request.getParameter("pwd");
		
		MemberDAO dao = new MemberDAO();
		MemberDTO dto = dao.login(memberId);
		
		if(dto != null && BCrypt.checkpw(pwd, dto.getPwd())) {
			HttpSession session = request.getSession();
			session.setAttribute("loginMember", dto);
			response.sendRedirect(request.getContextPath()+"/");
			return false;
		}else {
			request.setAttribute("loginFail", "IDまたはパスワードをご確認ください。");
			return true;
		}
		
	}

}
