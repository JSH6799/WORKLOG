package service;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.BoardCheckDAO;
import model.MemberDTO;

public class BoardCheckCommand implements Command {

	@Override
	public boolean doCommand(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("utf-8");
		
		HttpSession session = request.getSession();
		MemberDTO loginMember = (MemberDTO)session.getAttribute("loginMember");
		
		if(loginMember == null) {
			response.sendRedirect(request.getContextPath()+"/worklogMember/login");
			return false;
		}
		
		int bno = Integer.parseInt(request.getParameter("bno"));
		
		String memberId = loginMember.getMemberId();
		
		BoardCheckDAO dao = new BoardCheckDAO();
		boolean result = dao.insertCheck(bno, memberId);
		
		response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write("{\"success\":" + result + "}");
		
		return false;
	}

}
