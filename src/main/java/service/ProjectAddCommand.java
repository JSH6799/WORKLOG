package service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.ProjectDAO;
import model.ProjectDTO;
import java.util.UUID;

public class ProjectAddCommand implements Command {
    @Override
    public boolean doCommand(HttpServletRequest request, HttpServletResponse response) {
        try {
            String projectName = request.getParameter("projectName");
            String description = request.getParameter("description");
            String startDate = request.getParameter("startDate");
            String endDate = request.getParameter("endDate");
            
            HttpSession session = request.getSession();
            String creatorId = "java.kim"; 
            if(session.getAttribute("memberId") != null) {
                creatorId = (String) session.getAttribute("memberId");
            }

            ProjectDTO dto = new ProjectDTO();
            dto.setProjectId("PROJ_" + UUID.randomUUID().toString().substring(0, 8));
            dto.setTeamName("Default Team");
            dto.setProjectName(projectName);
            dto.setDescription(description);
            dto.setStartDate(startDate);
            dto.setEndDate(endDate);
            dto.setCreatorId(creatorId);

            ProjectDAO dao = ProjectDAO.getInstance();
            dao.insertProject(dto, creatorId);

            response.sendRedirect(request.getContextPath() + "/project/list");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}