package service;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.ProjectInvitationDAO;
import model.ProjectInvitationDTO;

public class ProjectInvitationSend implements Command {

	@Override
	public boolean doCommand(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		HttpSession session = request.getSession(false);
		
		if(session == null || session.getAttribute("memberId") == null) {
			 request.setAttribute("message", "ログインしてください。");
			 return false;
		}
		
		 String inviterId =
		            (String) session.getAttribute("memberId");

		        String projectId = request.getParameter("projectId");
		        String inviteeId = request.getParameter("inviteeId");
		        String role = request.getParameter("role");

		        if (projectId == null || inviteeId == null
		                || role == null
		                || projectId.isBlank()
		                || inviteeId.isBlank()
		                || !(role.equals("MEMBER")
		                    || role.equals("MANAGER"))) {

		            request.setAttribute("message", "入力内容を確認してください。");
		            return false;
		        }

		        ProjectInvitationDTO dto = new ProjectInvitationDTO();

		        dto.setProjectId(projectId);
		        dto.setInviterId(inviterId);
		        dto.setInviteeId(inviteeId);
		        dto.setRole(role);

		        ProjectInvitationDAO dao = new ProjectInvitationDAO();

		        boolean result = dao.insert(dto);

		        request.setAttribute(
		            "message",
		            result ? "招待を送信しました。" : "招待に失敗しました。"
		        );

		        return result;
	}

}
