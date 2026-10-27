package service;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ProjectMemberDTO;
import model.ProjectMemberDao;

public class ProjectmemberUpdateCommnand implements Command {

	public boolean doCommand(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		        String projectId = request.getParameter("projectId");
		        String memberId = request.getParameter("memberId");
		        String role = request.getParameter("role");

		        if (projectId == null || memberId == null
		                || role == null || role.isBlank()) {
		            return false;
		        }

		        ProjectMemberDTO dto = new ProjectMemberDTO();
		        dto.setProjectId(projectId);
		        dto.setMemberId(memberId);
		        dto.setRole(role);

		        ProjectMemberDao dao = new ProjectMemberDao();
		        boolean result = dao.updateRole(dto);

		        return result;
		    }
		}

		
		
		
		
	

