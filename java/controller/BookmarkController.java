package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.BookmarkCommand;
import service.BookmarkDeleteCommand;
import service.BookmarkListCommand;
import service.Command;

import java.io.IOException;

@WebServlet("/bookmark/*")
public class BookmarkController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public BookmarkController() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doAction(request,response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doAction(request,response);
	}
	
	protected void doAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String path = request.getPathInfo();
		
		Command command = null;
		String page = null;
		
		if(path == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		switch(path) {
		case "/list":
			command = new BookmarkListCommand();
			page = "/board/bookmark.jsp";
			break;
		case "/toggle":
			command = new BookmarkCommand();
			break;
		case "/delete":
			command = new BookmarkDeleteCommand();
			break;
			
		default:
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		boolean needForward = command.doCommand(request, response);
		
		String ajax = request.getParameter("ajax");
		boolean isAjax = "true".equals(ajax);
		
		if(needForward && page != null && !isAjax) {
			request.getRequestDispatcher(page).forward(request, response);
		}
	}

}
