package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.Command;
import service.ProjectAddCommand;
import service.ProjectDeleteCommand;
import service.ProjectListCommand;
import service.ProjectManageCommand;
import service.ProjectMembersCommand;

import java.io.IOException;

@WebServlet("/project/*")
public class ProjectController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public ProjectController() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doAction(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doAction(request, response);
    }

    protected void doAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        System.out.println("pathInfo: " + pathInfo);
        
        String page = null;
        Command command = null;
        
        if (pathInfo == null) {
            pathInfo = "/list";
        }

        try {
            switch (pathInfo) {
                case "/list":
                    command = new ProjectListCommand();
                    page = "/project/project.jsp"; // 또는 list.jsp
                    break;
                case "/add":
                    if (request.getMethod().equalsIgnoreCase("GET")) {
                        page = "/project/addProject.jsp";
                        command = (req, resp) -> true;
                    } else {
                        command = new ProjectAddCommand();
                        page = "/project/list"; 
                    }
                    break;
                case "/manage":
                    command = new ProjectManageCommand();
                    page = "/project/manageproject.jsp";
                    break;
                case "/members":
                    command = new ProjectMembersCommand();
                    page = "/project/projectmember.jsp";
                    break;
                case "/delete":
                    command = new ProjectDeleteCommand();
                    page = "/project/list";
                    break;
                default:
                    page = "/index.jsp";
                    command = (req, resp) -> true;
                    break;
            }
            
            boolean needForward = command != null && command.doCommand(request, response);
            
            if (needForward && page != null) {
                if (page.startsWith("redirect:")) {
                    response.sendRedirect(request.getContextPath() + page.substring(9));
                } else {
                    request.getRequestDispatcher(page).forward(request, response);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}