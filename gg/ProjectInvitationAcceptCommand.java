package service;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.MemberDTO;
import model.ProjectInvitationDAO;

public class ProjectInvitationAcceptCommand implements Command {

	@Override
	public boolean doCommand(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {

	    request.setCharacterEncoding("UTF-8");

	    HttpSession session = request.getSession(false);
	    MemberDTO loginMember = null;

	    if (session != null) {
	        loginMember = (MemberDTO) session.getAttribute("loginMember");
	    }

	    if (loginMember == null) {
	        response.sendRedirect(request.getContextPath() + "/worklogMember/login");
	        return false;
	    }

	    String memberId = loginMember.getMemberId();
	    String invitationId = request.getParameter("invitationId");
	    	

	    if (invitationId == null || invitationId.trim().isEmpty()) {
	        System.out.println("초대 번호가 전달되지 않았습니다.");
	        response.sendRedirect(request.getContextPath() + "/worklogMember/mypage");
	        return false;
	    }
	    int id = Integer.parseInt(invitationId);

	    try {
	        ProjectInvitationDAO dao = new ProjectInvitationDAO();

	        String path = request.getPathInfo();

	        boolean result;

	        if ("/invitation/accept".equals(path)) {
	            result = dao.acceptInvitation(id, memberId);
	            System.out.println("초대 수락 결과 = " + result);

	        } else if ("/invitation/reject".equals(path)) {
	            result = dao.updateStatus(id, "REJECTED");
	            System.out.println("초대 거절 결과 = " + result);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    response.sendRedirect(request.getContextPath() + "/worklogMember/mypage");
	    return false;
	}
}