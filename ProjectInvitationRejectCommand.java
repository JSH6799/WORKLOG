package service;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.MemberDTO;
import model.ProjectInvitationDAO;

public class ProjectInvitationRejectCommand implements Command {

	@Override
	public boolean doCommand(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		   HttpSession session = request.getSession(false);

	        if (session == null) {
	            response.sendRedirect(
	                request.getContextPath() + "/worklogMember/login");
	            return false;
	        }

	        MemberDTO loginMember =
	            (MemberDTO) session.getAttribute("loginMember");

	        if (loginMember == null) {
	            response.sendRedirect(
	                request.getContextPath() + "/worklogMember/login");
	            return false;
	        }

	        int invitationId = Integer.parseInt(
	            request.getParameter("invitationId"));

	        ProjectInvitationDAO dao = new ProjectInvitationDAO();

	        boolean result = dao.updateStatus(invitationId, "REJECTED");

	        if (result) {
	            response.sendRedirect(
	                request.getContextPath() + "/mypage");
	        } else {
	            response.sendError(
	                HttpServletResponse.SC_BAD_REQUEST, "초대 거절 실패");
	        }
		
		return false;
	}

}
