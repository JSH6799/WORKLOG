package controller;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.Command;
import service.ProjectMemberCommand;

import java.io.IOException;

@WebServlet("/promember/*")
public class projectMemeber extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    
    public projectMemeber() {
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
        
		if("/projectmember".equals(pathInfo)) {
			System.out.println("조건문 진입.");
        		command = new ProjectMemberCommand();
        		page = "/member/projectmember.jsp";
        }
        
		System.out.println("command="+command);
		System.out.println("page="+page);
		
        if(command ==null) {
        	System.out.println("command가 null이라 404발생함!");
        	
        	response.sendError(HttpServletResponse.SC_NOT_FOUND);
        	return;
        }
        
        System.out.println("서비스 실행직전");
        
        boolean needForward = command.doCommand(request, response);
        System.out.println("서비스 실행완료, needForward="+needForward);
      
        
        if(needForward && page != null) {
        	System.out.println("jsp 포워딩:"+page);
           request.getRequestDispatcher(page).forward(request, response);
        }

     }
}
