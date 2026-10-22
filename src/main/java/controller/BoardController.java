package controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.BoardListCommand;
import service.BoardWriteCommand;
import service.Command;

@WebServlet("/project_board/*")
@MultipartConfig (
		maxFileSize = 10 * 1024 * 1024,		// 파일 1개 최대 10MB
		maxRequestSize = 20 * 1024 * 1024	// 전체 요청 최대 20MB
		)
public class BoardController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public BoardController() {
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
		case "/write":
			command = new BoardWriteCommand();
			page = "/board/write.jsp";
			break;
		case "/list":
			command = new BoardListCommand();
			page = "/board/list.jsp";
			break;
		}
		
		
		boolean needForward = command.doCommand(request, response);
		
		if(needForward && page != null) {
			request.getRequestDispatcher(page).forward(request, response);
		}
	}
	

}
