package service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ProjectDAO;
import model.ProjectMemberDTO;
import java.util.List;

public class ProjectMembersCommand implements Command {
    @Override
    public boolean doCommand(HttpServletRequest request, HttpServletResponse response) {
        try {
            String projectId = request.getParameter("projectId");
            if(projectId == null) projectId = "PROJ_1";
            
            String newRole = request.getParameter("newRole");
            String targetMemberId = request.getParameter("targetMemberId");
            
            ProjectDAO dao = ProjectDAO.getInstance();
            if(newRole != null && targetMemberId != null) {
                dao.updateMemberRole(projectId, targetMemberId, newRole);
            }
            
            List<ProjectMemberDTO> members = dao.getProjectMembers(projectId);
            request.setAttribute("memberList", members);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}