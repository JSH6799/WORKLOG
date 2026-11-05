package service;

import java.io.IOException;

import org.mindrot.jbcrypt.BCrypt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.MemberDAO;
import model.MemberDTO;

public class MemberJoinCommand implements Command {

	@Override
	public boolean doCommand(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("utf-8");
		
		if("get".equalsIgnoreCase(request.getMethod())) {
			return true;
		}
		
		String memberId = request.getParameter("memberId");
		String pwd = request.getParameter("pwd");
		String hashedPwd = BCrypt.hashpw(pwd, BCrypt.gensalt());
		String memberName = request.getParameter("memberName");
		String email = request.getParameter("email");
		
		MemberDTO dto = new MemberDTO();
		
		dto.setMemberId(memberId);
		dto.setPwd(hashedPwd);
		dto.setMemberName(memberName);
		dto.setEmail(email);
		
		MemberDAO dao = new MemberDAO();
		dao.memberJoin(dto);
		
		response.sendRedirect(request.getContextPath()+"/worklogMember/login");
		
		return false;
	}

}
