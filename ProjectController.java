
package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
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
@MultipartConfig(
    maxFileSize = 10 * 1024 * 1024,
    maxRequestSize = 20 * 1024 * 1024
)
public class ProjectController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public ProjectController() {
        super();
    }

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        doAction(request, response);
    }

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {
        doAction(request, response);
    }

    protected void doAction(HttpServletRequest request,
                            HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        System.out.println("pathInfo: " + pathInfo);

        String page = null;
        Command command = null;

        if (pathInfo == null) {
            response.sendRedirect(request.getContextPath() + "/project/list");
            return;
        }

        switch (pathInfo) {
        case "/list":
            command = new ProjectListCommand();
            page = "/view/projectlist.jsp";
            break;

        case "/add":
            if (request.getMethod().equalsIgnoreCase("GET")) {
                page = "/view/addProject.jsp";
                command = (req, resp) -> true;
            } else {
                command = new ProjectAddCommand();
                page = "redirect:/project/list";
            }
            break;

        case "/manage":
            command = new ProjectManageCommand();
            page = "/view/manageproject.jsp";
            break;

        case "/members":
            command = new ProjectMembersCommand();
            page = "/view/projectmember.jsp";
            break;

        case "/delete":
            command = new ProjectDeleteCommand();
            break;

        default:
            response.sendRedirect(request.getContextPath() + "/project/list");
            return;
        }

        boolean needForward = command.doCommand(request, response);

        if (needForward && page != null) {
            if (page.startsWith("redirect:")) {
                response.sendRedirect(
                    request.getContextPath() + page.substring("redirect:".length())
                );
            } else {
                request.getRequestDispatcher(page).forward(request, response);
            }
        }
    }
}
