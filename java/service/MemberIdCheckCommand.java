package service;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.MemberDAO;

public class MemberIdCheckCommand implements Command {

	@Override
	public boolean doCommand(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("utf-8");
		
		String memberId = request.getParameter("memberId");
		MemberDAO dao = new MemberDAO();
		boolean isDuplicate = dao.idCheck(memberId);
		
		response.setContentType("application/json; charset=utf-8");
		PrintWriter out = response.getWriter();
		out.print("{\"isDuplicate\":"+ isDuplicate+"}");
		
		return false;
	}

}
