package service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ProjectDAO;

public class ProjectDeleteCommand implements Command {
    @Override
    public boolean doCommand(HttpServletRequest request, HttpServletResponse response) {
        try {
            String projectId = request.getParameter("projectId");
            if(projectId != null) {
                ProjectDAO dao = ProjectDAO.getInstance();
                dao.deleteProject(projectId);
            }
            response.sendRedirect(request.getContextPath() + "/project/list");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}