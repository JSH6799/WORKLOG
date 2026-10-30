package service;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.BoardCheckDAO;
import model.BoardCheckDTO;
import model.BoardDAO;
import model.BoardDTO;
import model.MemberDTO;
import model.ProjectDTO;

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
		
		BoardDAO dao = new BoardDAO();
		
		String searchType = request.getParameter("searchType");
		String keyword = request.getParameter("keyword");
		String projectId = request.getParameter("projectId");
		String unreadOnly = request.getParameter("unreadOnly");

		// 未確認投稿
		String memberId = loginMember.getMemberId();
		int unreadCount = dao.selectUnreadCountByProject(memberId, projectId);

		int totalCount;
		
		if("Y".equals(unreadOnly)) {
			totalCount = unreadCount;
		}else {
			totalCount = dao.selectCount(projectId, searchType, keyword);
		}
		
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
		
		// 全体ページ数計算
		int totalPage = (int)Math.ceil((double)totalCount / PAGE_SIZE);
		if(totalPage == 0) totalPage = 1;
		if(currentPage < 1) currentPage = 1;
		if(currentPage > totalPage) currentPage = totalPage;
		
		int startRow = (currentPage - 1) * PAGE_SIZE + 1;
		int endRow = currentPage * PAGE_SIZE;
		
		// ページno計算
		int startPage = ((currentPage - 1) / PAGE_BLOCK) * PAGE_BLOCK + 1;
		int endPage = Math.min(startPage+PAGE_BLOCK - 1, totalPage);
		
		// 一覧
		List<BoardDTO> list;
		
		if ("Y".equals(unreadOnly)) {
			list = dao.selectUnreadListByProject(
					loginMember.getMemberId(), projectId, startRow, endRow
					);
		} else {
			list = dao.selectList(
					searchType, keyword, projectId, memberId, startRow, endRow
					);
		}
		
		// 작성자가 확인 현황을 볼 수 있도록 인원수 계산
		BoardCheckDAO checkDao = new BoardCheckDAO();

		for (BoardDTO board : list) {
		    // 확인이 필요한 게시글이면서 로그인한 회원이 작성자인 경우
		    if ("Y".equals(board.getCheckRequired()) && loginMember.getMemberId().equals(board.getWriter())) {

		        List<BoardCheckDTO> checkList = checkDao.selectCheckList(board.getBno());

		        int completeCount = 0;
		        int totalCountForCheck = checkList.size();

		        for (BoardCheckDTO check : checkList) {
		            if ("確認完了".equals(check.getCheckStatus())) {
		                completeCount++;
		            }
		        }
		        board.setCheckCompleteCount(completeCount);
		        board.setCheckTotalCount(totalCountForCheck);
		    }
		}
		
		List<ProjectDTO> projectList = dao.selectMyProjects(loginMember.getMemberId());

		String projectName = "";

		for (ProjectDTO project : projectList) {
		    if (project.getProjectId().equals(projectId)) {
		        projectName = project.getProjectName();
		        break;
		    }
		}

		request.setAttribute("projectName", projectName);
		
		
		request.setAttribute("boardList", list);
		request.setAttribute("searchType", searchType);
		request.setAttribute("keyword", keyword);
		request.setAttribute("projectId", projectId);
		request.setAttribute("currentPage", currentPage);
		request.setAttribute("totalPage", totalPage);
		request.setAttribute("startPage", startPage);
		request.setAttribute("endPage", endPage);
		request.setAttribute("unreadCount", unreadCount);
		request.setAttribute("unreadOnly", unreadOnly);
		
		return true;
	}

}