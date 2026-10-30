package controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CalendarEventCommand;
import service.Command;

@WebServlet("/worklogCalendar/*")
public class CalendarController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doAction(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doAction(request, response);
	}

	protected void doAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String pathInfo = request.getPathInfo();
		if (pathInfo == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		String page = null;
		Command command = null;

		switch (pathInfo) {
		case "/calendar":
			command = null;
			page = "calendar/calendar.jsp";
			break;
		case "/view":
			// Command 없이 JSP로 바로 forward
			request.getRequestDispatcher("/calendar/calendar.jsp").forward(request, response);
			return;
		case "/event":                      // JSON API
			command = new CalendarEventCommand();
			page = null;
			break;
		}

		if (command == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		boolean needForward = command.doCommand(request, response);

		if (needForward && page != null) {
			request.getRequestDispatcher(page).forward(request, response);
		}
	}
}