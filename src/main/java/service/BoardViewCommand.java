package service;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.BoardDAO;
import model.BoardDTO;
import model.MemberDTO;

public class BoardViewCommand implements Command {

	@Override
	public boolean doCommand(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("utf-8");
		
		HttpSession session = request.getSession();
		MemberDTO loginMember = (MemberDTO)session.getAttribute("loginMember");
		
		if (loginMember == null) {
		    response.sendRedirect(
		        request.getContextPath() + "/worklogMember/login"
		    );
		    return false;
		}
		
		int bno = Integer.parseInt(request.getParameter("bno"));
		
		BoardDAO dao = new BoardDAO();
		BoardDTO dto = dao.selectView(bno);
		
		request.setAttribute("view", dto);
		
		return true;
	}

}
