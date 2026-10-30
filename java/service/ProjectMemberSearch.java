package service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ProjectMemberDTO;
import model.ProjectMemberDao;

public class ProjectMemberSearch implements Command {

	@Override
	public boolean doCommand(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		  String projectId = request.getParameter("projectId");
	        String keyword = request.getParameter("keyword");
	        System.out.println("검색어 = [" + keyword + "]");
	        System.out.println("projectId = [" + projectId + "]");

	        if (projectId == null || projectId.trim().isEmpty()) {
	            request.setAttribute("memberList", Collections.emptyList());
	            request.setAttribute("errorMessage", "プロジェクトIDがありません。");
	            return true;
	        }

	        if (keyword == null) {
	            keyword = "";
	        }

	        ProjectMemberDao dao = new ProjectMemberDao();
	        

	        List<ProjectMemberDTO> list =
	                dao.searchAvailableMembers(projectId, keyword);


	        request.setAttribute("memberList", list);
	        request.setAttribute("projectId", projectId);
	        request.setAttribute("keyword", keyword);

	        return true;
	    }
	
}
