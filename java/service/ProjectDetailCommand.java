package service;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.MemberDTO;
import model.ProjectDAO;
import model.ProjectDTO;
import model.ProjectMemberDTO;

public class ProjectDetailCommand implements Command {

	@Override
	public boolean doCommand(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("utf-8");
		
		HttpSession session = request.getSession();
		MemberDTO loginMember = (MemberDTO)session.getAttribute("loginMember");
		
		if(loginMember == null) {
			response.sendRedirect(request.getContextPath()+"/worklogMember/login");
			return false;
		}
		
		
		String projectId = request.getParameter("projectId");
		
		if (projectId == null || projectId.trim().isEmpty()) {
		    response.sendRedirect(request.getContextPath() + "/project/list");
		    return false;
		}
		
		
		ProjectDAO dao = ProjectDAO.getInstance();
		ProjectDTO project = dao.selectProjectDetail(projectId);
		
		if (project == null) {
		    response.sendRedirect(request.getContextPath() + "/project/list");
		    return false;
		}
		
		
		String loginId = loginMember.getMemberId();
		boolean isMember = project.getCreatorId() != null && project.getCreatorId().equals(loginId);
		
		List<ProjectMemberDTO> memberList = dao.getProjectMembers(projectId);
		for(ProjectMemberDTO member : memberList) {
			if(loginId.equals(member.getMemberId())) {
				isMember = true;
				break;
			}
		}
		
		if(!isMember) {
			response.sendRedirect(request.getContextPath()+"/project/list");
			return false;
		}
		
		request.setAttribute("proDetail", project);
		return true;
	}

}
