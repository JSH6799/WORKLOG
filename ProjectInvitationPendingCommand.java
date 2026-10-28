package service;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.MemberDTO;
import model.ProjectInvitationDAO;
import model.ProjectInvitationDTO;

public class ProjectInvitationPendingCommand implements Command {

	@Override
	public boolean doCommand(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		

        // 1. 로그인한 회원 정보 가져오기
        HttpSession session = request.getSession();

        MemberDTO loginMember =
            (MemberDTO) session.getAttribute("loginMember");

        // 2. 로그인하지 않았다면 로그인 페이지로 이동
        if (loginMember == null) {
            response.sendRedirect(
                request.getContextPath() + "/worklogMember/login"
            );
            return false;
        }

        // 3. 로그인한 회원의 ID 가져오기
        String inviteeId = loginMember.getMemberId();

        // 4. 받은 대기 초대 목록 조회
        ProjectInvitationDAO dao = new ProjectInvitationDAO();

        List<ProjectInvitationDTO> invitationList =
            dao.selectPending(inviteeId);

        // 5. 조회 결과를 JSP에 전달
        request.setAttribute("invitationList", invitationList);

        return true;
	}
   }

