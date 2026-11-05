
package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.Command;
import service.ProjectInvitationSend;
import service.ProjectMemberDeleteCommmand;
import service.ProjectMemberSearch;
import service.ProjectInvitationAcceptCommand;
import java.io.IOException;

@WebServlet("/projectmember/*")
public class ProjectMemberController extends HttpServlet {

    private static final long serialVersionUID = 1L;

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

    private void doAction(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getPathInfo();

        System.out.println("ProjectMemberController 실행");
        System.out.println("요청 경로 = " + path);

        // 다음 단계에서 초대 기능별 Command 연결
        Command command = null;
        String page = null;

        switch (path) {
        	
             case "/search":
            	 command = new ProjectMemberSearch();
            	 page = "/project_jsp/projectmembersearch.jsp";
               break;

            case "/invitation/send":
                command = new ProjectInvitationSend();
                break;
            case"/invitation/accept":
            	command = new ProjectInvitationAcceptCommand();
            	break;
            
            case "/invitation/reject":
                command = new ProjectInvitationAcceptCommand();
                break;
            	
            case "/delete":
                command = new ProjectMemberDeleteCommmand();
                break;

            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
        }
        boolean result = command.doCommand(request, response);
        
        if (result && page != null && !response.isCommitted()) {
            request.getRequestDispatcher(page).forward(request, response);
        }
    }
}
