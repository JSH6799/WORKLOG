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
		
		if(projectId == null || projectId.trim().isEmpty()) {
			request.setAttribute("memberList", Collections.emptyList());
			request.setAttribute("errorMessage", "프로젝트 ID가 없습니다..");
			return true;
		}
		
	try {
		ProjectMemberDao dao = new ProjectMemberDao();
		
		List<ProjectMemberDTO> list = dao.search(projectId);
		
		request.setAttribute("memberList", list);
		
	}catch (Exception e) {
		e.printStackTrace();
		
		request.setAttribute("memberList", Collections.emptyList());
		request.setAttribute("errorMessage", "팀원 목록을 조회하지 못했습니다.");
	}
	return true;
	}

	
}
