package service;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ProjectMemberDao;

public class ProjectMemberDeleteCommmand implements Command {

	@Override
	public boolean doCommand(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		  String projectId = request.getParameter("projectId");
	        String memberId = request.getParameter("memberId");
	        
	        System.out.println("삭제 요청 도착");
	        System.out.println("projectId = " + projectId);
	        System.out.println("memberId = " + memberId);

	        if (projectId == null ||projectId.trim().isEmpty()
	            || memberId == null || memberId.trim().isEmpty()) {
	        	 response.sendError(HttpServletResponse.SC_BAD_REQUEST,
	                     "プロジェクトIDまたは会員IDがありません。");
	            return false;
	        }
	        
	        ProjectMemberDao dao = new ProjectMemberDao();
	        boolean result = dao.delete(projectId, memberId);

	        response.sendRedirect(
	            request.getContextPath()
	            + "/project/members?projectId="
	            + projectId
	        );

	        return false;
	        
	}
}