
package service;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.MemberDTO;
import model.ProjectInvitationDAO;
import model.ProjectInvitationDTO;
import model.ProjectMemberDao;

public class ProjectInvitationSend implements Command {

    @Override
    public boolean doCommand(HttpServletRequest request,
                             HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("========== 초대 전송 시작 ==========");

        // 1. 로그인 확인
        
        HttpSession session = request.getSession(false);
		
		if (session == null) {
		    response.sendRedirect(request.getContextPath() + "/login");
		    return false;
		}
		
		MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");
		
		if(loginMember == null) {
			response.sendRedirect(request.getContextPath()+"/worklogMember/login");
			return false;
		}

        String inviterId = loginMember.getMemberId();
  

        // 2. 요청 파라미터 받기
        String projectId = request.getParameter("projectId");
        String inviteeId = request.getParameter("inviteeId");
        String inviteeName = request.getParameter("inviteeName");
        String email = request.getParameter("email");
        String role = request.getParameter("role");

        System.out.println("inviterId = " + inviterId);
        System.out.println("projectId = " + projectId);
        System.out.println("inviteeId = " + inviteeId);
        System.out.println("role = " + role);

        // 3. 입력값 확인
        if (projectId == null || projectId.isBlank()
                || inviteeId == null || inviteeId.isBlank()
                || inviteeName == null || inviteeName.isBlank()
                || email == null || email.isBlank()
                || role == null || role.isBlank()) {

            System.out.println("실패: 필수 입력값이 없습니다.");

            request.setAttribute(
                "message",
                "プロジェクトまたは招待するメンバーを確認してください。"
            );

            return false;
        }

        projectId = projectId.trim();
        inviteeId = inviteeId.trim();
        inviteeName = inviteeName.trim();
        email = email.trim();
        role = role.trim();

        if (!role.equals("MEMBER") && !role.equals("MANAGER")) {

            System.out.println("실패: 올바르지 않은 역할입니다.");

            request.setAttribute(
                "message", "メンバーの役割を確認してください。"
            );

            return false;
        }

        // 자기 자신에게 초대하는 것 방지
        if (inviterId.equals(inviteeId)) {

            System.out.println("실패: 본인에게 초대할 수 없습니다.");

            request.setAttribute(
                "message", "自分自身を招待することはできません。"
            );

            return false;
        }
        
        // 입력한 회원 정보가 실제 DB 정보와 일치하는지 확인
        ProjectMemberDao memberDao = new ProjectMemberDao();

        boolean memberExists =
            memberDao.checkMemberInfo(inviteeId, inviteeName, email);

        if (!memberExists) {
            request.setAttribute(
                "message",
                "入力したID・名前・メールアドレスに一致する会員がいません。"
            );

            return false;
        }
        
        // 4. 초대 정보 DTO 생성
        ProjectInvitationDTO dto = new ProjectInvitationDTO();

        dto.setProjectId(projectId);
        dto.setInviterId(inviterId);
        dto.setInviteeId(inviteeId);
        dto.setRole(role);

        // 5. DB에 초대 정보 저장
        ProjectInvitationDAO dao = new ProjectInvitationDAO();

        try {
        	
        	System.out.println("초대자 ID = [" + inviterId + "]");
        	System.out.println("프로젝트 ID = [" + projectId + "]");
        	System.out.println("초대받는 ID = [" + inviteeId + "]");

            boolean result = dao.insert(dto);

            System.out.println("DB 저장 결과 = " + result);

            if (!result) {
                request.setAttribute(
                    "message",
                    "招待の送信に失敗しました。入力内容とデータベースを確認してください。"
                );

                return false;
            }

            // 6. 초대 전송 성공
            request.setAttribute(
                "message", "招待を送信しました。"
            );

            System.out.println("초대 전송 성공");
            System.out.println("===================================");

            return true;

        } catch (Exception e) {

            System.out.println("초대 전송 중 예외 발생");
            e.printStackTrace();

            request.setAttribute(
                "message",
                "招待の送信中にエラーが発生しました。"
            );

            return false;
        }
    }
}
