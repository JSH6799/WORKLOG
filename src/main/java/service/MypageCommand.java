
package service;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.BoardDAO;
import model.BoardDTO;
import model.MemberDAO;
import model.MemberDTO;
import model.ProjectDTO;

public class MypageCommand implements Command {

	@Override
	public boolean doCommand(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("utf-8");

		HttpSession session = request.getSession();
		MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");

		if(loginMember == null) {
			response.sendRedirect(request.getContextPath() + "/worklogMember/login");
			return false;
		}

		String memberId = loginMember.getMemberId();

		BoardDAO bdao = new BoardDAO();

		// 회원이 참여한 프로젝트 목록
		List<ProjectDTO> projectList = bdao.selectMyProjects(memberId);
		
		// 회원 정보 조회
		MemberDAO mdao = new MemberDAO();
		MemberDTO memberInfo = mdao.selectMember(memberId);
		
		request.setAttribute("memberInfo", memberInfo);

		// 프로젝트별 미확인 게시글 정보
		for(ProjectDTO dto : projectList) {
			String projectId = dto.getProjectId();

			int unreadCnt = bdao.selectUnreadCountByProject(memberId, projectId);
			List<BoardDTO> boardList = bdao.unreadListForMyPage(projectId, memberId);

			dto.setUnreadCount(unreadCnt);
			dto.setUnreadBoardList(boardList);
		}

		// 내가 쓴 게시글 전체 목록
		List<BoardDTO> myBoardList = bdao.selectMyBoardList(memberId);

		// JSP로 전달
		request.setAttribute("mypageUnreadList", projectList);
		request.setAttribute("myBoardList", myBoardList);

		return true;
	}
}
