
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
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/project/*")
@MultipartConfig(
    maxFileSize = 10 * 1024 * 1024,
    maxRequestSize = 20 * 1024 * 1024
)
public class ProjectController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        doAction(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {
        doAction(request, response);
    }

    protected void doAction(HttpServletRequest request,
                            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || "/".equals(pathInfo)) {
            response.sendRedirect(
                request.getContextPath() + "/project/list"
            );
            return;
        }

        String page = null;
        Command command = null;

        switch (pathInfo) {

        case "/list":
            HttpSession session = request.getSession(false);

            model.MemberDTO loginMember = null;

            if (session != null) {
                loginMember =
                    (model.MemberDTO) session.getAttribute("loginMember");
            }

            if (loginMember == null
                    || loginMember.getMemberId() == null
                    || loginMember.getMemberId().trim().isEmpty()) {

                response.sendRedirect(
                    request.getContextPath() + "/member/login.jsp"
                );
                return;
            }

            command = new ProjectListCommand();
            page = "/view/projectlist.jsp";
            break;
        case "/add":
            if ("GET".equalsIgnoreCase(request.getMethod())) {

                HttpSession addSession = request.getSession(false);

                model.MemberDTO addLoginMember = null;

                if (addSession != null) {
                    addLoginMember =
                        (model.MemberDTO) addSession.getAttribute("loginMember");
                }

                if (addLoginMember == null
                        || addLoginMember.getMemberId() == null
                        || addLoginMember.getMemberId().trim().isEmpty()) {

                    response.sendRedirect(
                        request.getContextPath() + "/member/login.jsp"
                    );
                    return;
                }

                page = "/view/addProject.jsp";
                command = (req, resp) -> true;

            } else if ("POST".equalsIgnoreCase(request.getMethod())) {
                command = new ProjectAddCommand();
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
                response.sendRedirect(
                    request.getContextPath() + "/project/list"
                );
                return;
        }

        if (command == null) {
            response.sendRedirect(
                request.getContextPath() + "/project/list"
            );
            return;
        }

        boolean needForward = command.doCommand(request, response);

        if (needForward && page != null && !response.isCommitted()) {
            request.getRequestDispatcher(page)
                   .forward(request, response);
        }

    }
}