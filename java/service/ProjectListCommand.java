package service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ProjectDAO;
import model.ProjectDTO;
import java.util.List;

public class ProjectListCommand implements Command {
    @Override
    public boolean doCommand(HttpServletRequest request, HttpServletResponse response) {
        try {
            ProjectDAO dao = ProjectDAO.getInstance();
            List<ProjectDTO> list = dao.getProjectList();
            request.setAttribute("projectList", list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}