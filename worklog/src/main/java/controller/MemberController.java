package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.BoardWriteCommand;
import service.Command;
import service.MemberIdCheckCommand;
import service.MemberJoinCommand;
import service.MemberLoginCommand;
import service.MemberLogoutCommand;

import java.io.IOException;

@WebServlet("/worklogMember/*")
public class MemberController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public MemberController() {
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
		
		switch (pathInfo) {
		case "/idCheck":
			command = new MemberIdCheckCommand();
			page = null;
			break;
		case "/join":
			command = new MemberJoinCommand();
			page = "/member/join.jsp";
			break;
		case "/login":
			command = new MemberLoginCommand();
			page = "/member/login.jsp";
			break;
		case "/logout":
			command = new MemberLogoutCommand();
			page = null;
			break;
		}
		
		
		boolean needForward = command.doCommand(request, response);
		
		if(needForward && page != null) {
			request.getRequestDispatcher(page).forward(request, response);
		}
	}

}
