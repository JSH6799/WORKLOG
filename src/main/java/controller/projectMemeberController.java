package controller;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.Command;
import service.ProjectDeleteCommand;
import service.ProjectMembersCommand;
import service.ProjectmemberUpdateCommnand;

import java.io.IOException;

@WebServlet("/projectmember/*")
public class projectMemeberController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    
    public projectMemeberController() {
        super();
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doAction(request,response);
     }

     protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doAction(request,response);
     }
     
     protected void doAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        System.out.println("pathInfo: "+pathInfo);
        
        String page = null;
        Command command = null;
        
        if (pathInfo == null) {
            pathInfo = "/list";
        }

        try {
            switch (pathInfo) {
            	// 팀원 목록 조회
                case "/members":
                    command = new ProjectMembersCommand();
                    page = "/member/projectmember.jsp"; 
                    break;
                    
               // case "/search":
                //	command = new ProjectMemberSearch();
                //	break;
                 // 팀원 역할 수정
                case "/update":
                	command = new ProjectmemberUpdateCommnand();
                	break;
                case "/delete":
                	 command = new ProjectDeleteCommand();
                	 page = null;
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