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
import model.BookmarkDAO;
import model.MemberDTO;

public class BoardViewCommand implements Command {

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
		
		int bno = Integer.parseInt(request.getParameter("bno"));
		
		BoardDAO dao = new BoardDAO();
		BoardDTO dto = dao.selectView(bno);
		
		request.setAttribute("view", dto);
		
		
		BookmarkDAO bdao = new BookmarkDAO();
		boolean bookmarked = bdao.isBookmarked(bno, loginMember.getMemberId());
		
		request.setAttribute("bookmarked", bookmarked);
		
		
		BoardCheckDAO checkDAO = new BoardCheckDAO();
		
		boolean checked = checkDAO.isChecked(bno, loginMember.getMemberId());
		request.setAttribute("checked", checked);
		
		
		
		if(loginMember.getMemberId().equals(dto.getWriter()) && "Y".equals(dto.getCheckRequired())) {
			List<BoardCheckDTO> checkList = checkDAO.selectCheckList(bno);
			request.setAttribute("checkList", checkList);
			int totalCount = checkList.size();
			int checkCount = 0;
			
			for (BoardCheckDTO cl : checkList) {
			    if ("確認完了".equals(cl.getCheckStatus())) {
			        checkCount++;
			    }
			}
			
			request.setAttribute("totalCount", totalCount);
			request.setAttribute("checkCount", checkCount);
		}
		
		return true;
	}

}
