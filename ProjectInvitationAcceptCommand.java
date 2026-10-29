package service;

import java.io.IOException;
import java.sql.SQLException;

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
		
		 // 1. 초대 번호 가져오기
        String invitationIdStr = request.getParameter("invitationId");

        HttpSession session = request.getSession();
		MemberDTO loginMember = (MemberDTO)session.getAttribute("loginMember");
		
		if(loginMember == null) {
			response.sendRedirect(request.getContextPath()+"/worklogMember/login");
			return false;
		}
		
		String inviteeId = loginMember.getMemberId();

        if (inviteeId == null || inviteeId.trim().isEmpty()) {
            response.sendError(
                HttpServletResponse.SC_UNAUTHORIZED,
                "로그인이 필요합니다."
            );
        	return false;
    	}
    
        if (invitationIdStr == null || invitationIdStr.trim().isEmpty()) {
             response.sendError(
        	 HttpServletResponse.SC_BAD_REQUEST,
             "초대 번호가 없습니다."
            		 );
            return false;
        }
        try {
            int invitationId = Integer.parseInt(invitationIdStr);

            // 3. 초대 수락 처리
            ProjectInvitationDAO dao = new ProjectInvitationDAO();

            boolean result =
                dao.acceptInvitation(invitationId, inviteeId);

            // 4. 처리 결과에 따라 이동
            if (result) {
                response.sendRedirect(
                    request.getContextPath()
                    + "/projectmember/members?projectId="
                    + java.net.URLEncoder.encode(
                        request.getParameter("projectId") == null
                            ? "" : request.getParameter("projectId"),
                        "UTF-8"
                    )
                );
                return false;
            } else {
                request.setAttribute(
                    "errorMessage",
                    "초대 수락에 실패했습니다. 이미 처리된 초대인지 확인해 주세요."
                );
                return true;
            }

        } catch (NumberFormatException e) {
            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "초대 번호가 올바르지 않습니다."
            );
            return false;

        } catch (SQLException e) {
            throw new ServletException("초대 수락 처리 중 오류가 발생했습니다.", e);
        }
	}
}
	
