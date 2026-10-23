package service;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.BoardDAO;
import model.BoardDTO;
import model.MemberDTO;

public class BoardListCommand implements Command {
	
	private static final int PAGE_SIZE = 10;
	private static final int PAGE_BLOCK = 5;

	@Override
	public boolean doCommand(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("utf-8");
		
		HttpSession session = request.getSession();
		MemberDTO loginMember = (MemberDTO)session.getAttribute("loginMember");
		
		if (loginMember == null) {
		    response.sendRedirect(
		        request.getContextPath() + "/worklogMember/login"
		    );
		    return false;
		}
		
		String searchType = request.getParameter("searchType");
		String keyword = request.getParameter("keyword");
		
		// 現在ページno
		int currentPage = 1;
		String pageParam = request.getParameter("page");
		if(pageParam != null) {
			try {
				currentPage = Integer.parseInt(pageParam);
			}catch (NumberFormatException e) {
				currentPage = 1;
			}
		}
		
		BoardDAO dao = new BoardDAO();
		
		// 全体ページ数計算
		String projectId = request.getParameter("projectId");
		int totalCount = dao.selectCount(projectId, searchType, keyword);
		int totalPage = (int)Math.ceil((double)totalCount / PAGE_SIZE);
		if(totalPage == 0) totalPage = 1;
		if(currentPage < 1) currentPage = 1;
		if(currentPage > totalPage) currentPage = totalPage;
		
		int startRow = (currentPage - 1) * PAGE_SIZE + 1;
		int endRow = currentPage * PAGE_SIZE;
		
		List<BoardDTO> list = dao.selectList(searchType, keyword, projectId, startRow, endRow);
		
		// ページno計算
		int startPage = ((currentPage - 1) / PAGE_BLOCK) * PAGE_BLOCK + 1;
		int endPage = Math.min(startPage+PAGE_BLOCK - 1, totalPage);
		
		request.setAttribute("boardList", list);
		request.setAttribute("searchType", searchType);
		request.setAttribute("keyword", keyword);
		request.setAttribute("projectId", projectId);
		request.setAttribute("currentPage", currentPage);
		request.setAttribute("totalPage", totalPage);
		request.setAttribute("startPage", startPage);
		request.setAttribute("endPage", endPage);
		
		return true;
	}

}
