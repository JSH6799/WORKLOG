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
    maxFileSize = 10 * 1024 * 1024,      // 파일 1개 최대 10MB
    maxRequestSize = 20 * 1024 * 1024    // 전체 요청 최대 20MB
)
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
        
        // pathInfo가 null일 때 기본값 설정 (에러 방어)
        if (pathInfo == null) {
            pathInfo = "/list";
        }
        
        String page = null;
        Command command = null;

        try {
            switch (pathInfo) {
                case "/list":
                    command = new ProjectListCommand();
                    page = "/projectJSP/project.jsp";
                    break;
                case "/add":
                    if (request.getMethod().equalsIgnoreCase("GET")) {
                        page = "/projectJSP/addProject.jsp";
                        command = (req, resp) -> true;
                    } else {
                        command = new ProjectAddCommand();
                        // POST 처리 후 새로고침 중복 방지를 위해 redirect: 추가
                        page = "redirect:/projectJSP/list"; 
                    }
                    break;
                case "/manage":
                    command = new ProjectManageCommand();
                    page = "/projectJSP/manageproject.jsp";
                    break;
                case "/members":
                    command = new ProjectMembersCommand();
                    page = "/projectJSP/projectmember.jsp";
                    break;
                case "/delete":
                    command = new ProjectDeleteCommand();
                    page = "redirect:/projectJSP/list";
                    break;
                default:
                    page = "/index.jsp";
                    command = (req, resp) -> true;
                    break;
            }
            
            // command가 null이 아닐 때만 안전하게 실행
            boolean needForward = (command != null) && command.doCommand(request, response);
            
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