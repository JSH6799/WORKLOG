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
import model.ProjectDAO;
import model.ProjectDTO;

public class MypageCommand implements Command {

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
		
		BoardDAO dao = new BoardDAO();
		
		List<ProjectDTO> projectList = dao.selectMyProjects(memberId);
		
		for(ProjectDTO dto : projectList) {
			String projectId = dto.getProjectId();
			int unreadCnt = dao.selectUnreadCountByProject(memberId, projectId);
			List<BoardDTO> boardList = dao.unreadListForMyPage(projectId, memberId);
			
			dto.setUnreadCount(unreadCnt);
			dto.setUnreadBoardList(boardList);
		}
		
		request.setAttribute("proList", projectList);
		
		return true;
	}

}
