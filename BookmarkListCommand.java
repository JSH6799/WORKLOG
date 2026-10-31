package service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.BoardDAO;
import model.BoardDTO;
import model.BookmarkDAO;
import model.MemberDTO;
import model.ProjectDAO;
import model.ProjectDTO;

public class BookmarkListCommand implements Command {
	
	private static final int PAGE_SIZE = 10;

	@Override
	public boolean doCommand(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("utf-8");
		
		HttpSession session = request.getSession();
		MemberDTO loginMember = (MemberDTO)session.getAttribute("loginMember");
		
		if(loginMember == null) {
			response.sendRedirect(request.getContextPath()+"/worklogMember/login");
			return false;
		}
		
		String memberId = loginMember.getMemberId();
		String projectId = request.getParameter("projectId");
		
		if("all".equals(projectId)) {
			projectId = null;
		}
		
		String searchType = request.getParameter("searchType");
		String keyword = request.getParameter("keyword");

		if (keyword != null) {
		    keyword = keyword.trim();
		}

		if (keyword == null || keyword.isEmpty()
		        || searchType == null
		        || !(searchType.equals("title")
		            || searchType.equals("content")
		            || searchType.equals("writer"))) {
		    searchType = null;
		    keyword = null;
		}

		int page = 1;
		
		String pageParam = request.getParameter("page");
		
		if(pageParam != null) {
			try {
				page = Integer.parseInt(pageParam);
				if(page < 1) {
					page = 1;
				}
			}catch (NumberFormatException e) {
				page = 1;
			}
		}
		
		int startRow = (page - 1) * PAGE_SIZE + 1;
		int endRow = page * PAGE_SIZE;
		
		BookmarkDAO dao = new BookmarkDAO();
		List<BoardDTO> bmkList;

		if (searchType != null) {
		    bmkList = dao.searchBookmarkList(memberId, projectId, searchType, keyword, startRow, endRow);
		} else {
		    bmkList = dao.selectBookmarkList(memberId, projectId, startRow, endRow);
		}
		
		String ajax = request.getParameter("ajax");
		
		if("true".equals(ajax)) {
			response.setContentType("application/json; charset=utf-8");
			
			Gson gson = new Gson();
			PrintWriter out = response.getWriter();
			out.print(gson.toJson(bmkList));
			
			return false;
		}
		
		BoardDAO bdao = new BoardDAO();
		List<ProjectDTO> projectList = bdao.selectMyProjects(memberId);
		
		request.setAttribute("bmkList", bmkList);
		request.setAttribute("bmkProList", projectList);
		
		request.setAttribute("keyword", keyword);
		request.setAttribute("searchType", searchType);
		
		return true;
	}

}
