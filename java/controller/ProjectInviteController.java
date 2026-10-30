package controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.Command;
import service.InviteCreateCommand;
import service.ProjectJoinCommand;

/**
 * 초대 코드 생성 / 프로젝트 참가 요청을 받아 알맞은 Command 로 연결하는 컨트롤러.
 *   /project_invite/create → InviteCreateServlet (초대 코드 생성, JSON 응답)
 *   /project_invite/join   → ProjectJoinServlet  (링크 또는 코드로 참가)
 */
@WebServlet("/project_invite/*")
public class ProjectInviteController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doAction(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doAction(request, response);
    }

    protected void doAction(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        System.out.println("pathInfo: " + pathInfo);

        String page = null;
        Command command = null;

        if (pathInfo == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        switch (pathInfo) {
        case "/create":
            command = new InviteCreateCommand();
            page = null; // JSON 을 직접 응답하므로 forward 없음
            break;
        case "/join":
            command = new ProjectJoinCommand();
            page = null; // redirect / alert 를 직접 응답하므로 forward 없음
            break;
        default:
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        boolean needForward = command.doCommand(request, response);

        if (needForward && page != null) {
            request.getRequestDispatcher(page).forward(request, response);
        }
    }
}