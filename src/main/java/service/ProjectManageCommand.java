package service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ProjectDAO;
import model.ProjectDTO;

public class ProjectManageCommand implements Command {
    @Override
    public boolean doCommand(HttpServletRequest request, HttpServletResponse response) {
        try {
            String projectId = request.getParameter("projectId");
            if(projectId == null || projectId.isEmpty()) {
                projectId = "PROJ_1"; 
            }
            
            ProjectDAO dao = ProjectDAO.getInstance();
            
            if(request.getMethod().equalsIgnoreCase("POST")) {
                String projectName = request.getParameter("projectName");
                String description = request.getParameter("description");
                String startDate = request.getParameter("startDate");
                String endDate = request.getParameter("endDate");
                
                ProjectDTO dto = new ProjectDTO();
                dto.setProjectId(projectId);
                dto.setProjectName(projectName);
                dto.setDescription(description);
                dto.setStartDate(startDate);
                dto.setEndDate(endDate);
                
                dao.updateProject(dto);
                
                response.sendRedirect(request.getContextPath() + "/project/manage?projectId=" + projectId + "&msg=saved");
                return false;
            }
            
            ProjectDTO project = dao.getProjectById(projectId);
            request.setAttribute("project", project);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}