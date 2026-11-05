
package service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.MemberDTO;
import model.ProjectMemberDTO;
import model.ProjectMemberDao;
import model.ProjectInvitationDTO;
import model.ProjectInvitationDAO;

public class ProjectInvitationSend implements Command {

    @Override
    public boolean doCommand(HttpServletRequest request,
                             HttpServletResponse response)
            throws IOException {

        request.setCharacterEncoding("UTF-8");

        String contextPath = request.getContextPath();

        String projectId = request.getParameter("projectId");
        String inviteeId = request.getParameter("inviteeId");

        String keyword = request.getParameter("keyword");
        if (keyword == null) {
            keyword = "";
        }

        // 1. 로그인 확인
        HttpSession session = request.getSession(false);

        MemberDTO loginMember = null;

        if (session != null) {
            loginMember =
                (MemberDTO) session.getAttribute("loginMember");
        }

        if (loginMember == null
                || loginMember.getMemberId() == null) {

            response.sendRedirect(
                contextPath + "/worklogMember/login"
            );
            return false;
        }

        // 2. 전달받은 값 확인
        if (projectId == null || projectId.trim().isEmpty()
                || inviteeId == null || inviteeId.trim().isEmpty()) {

            response.sendRedirect(contextPath + "/project/list");
            return false;
        }

        String inviterId = loginMember.getMemberId();
        
        System.out.println("========== 초대 전송 확인 ==========");
        System.out.println("projectId = " + projectId);
        System.out.println("inviteeId = " + inviteeId);
        System.out.println("inviterId = " + inviterId);

        ProjectMemberDao memberDAO = new ProjectMemberDao();

        // 3. 초대자가 해당 프로젝트의 OWNER인지 확인
        List<ProjectMemberDTO> memberList =
            memberDAO.search(projectId);

        boolean isOwner = false;

        if (memberList != null) {
            for (ProjectMemberDTO member : memberList) {

                if (inviterId.equals(member.getMemberId())
                        && "OWNER".equals(member.getRole())) {

                    isOwner = true;
                    break;
                }
            }
        }
        System.out.println("OWNER 확인 결과 = " + isOwner);
        
        if (!isOwner) {
            response.sendRedirect(contextPath + "/project/list");
            return false;
        }

        // 4. 이미 프로젝트에 참여 중인지 확인
        if (memberDAO.isAlreadyMember(projectId, inviteeId)) {

            response.sendRedirect(
                contextPath + "/projectmember/search?projectId="
                + URLEncoder.encode(projectId, StandardCharsets.UTF_8)
                + "&keyword="
                + URLEncoder.encode(keyword, StandardCharsets.UTF_8)
            );
            return false;
        }

        // 5. 초대 정보 생성
        ProjectInvitationDTO dto = new ProjectInvitationDTO();

        dto.setProjectId(projectId);
        dto.setInviterId(inviterId);
        dto.setInviteeId(inviteeId);

        // 기본 권한은 관람 전용
        dto.setRole("MEMBER");

        // 6. 초대 DB 저장
        ProjectInvitationDAO invitationDAO =
            new ProjectInvitationDAO();

        boolean result = invitationDAO.insert(dto);
        
        System.out.println("INSERT 결과 = " + result);
        System.out.println("==================================");

        // 7. 검색 결과 페이지로 이동
        String msg = result ? "sent" : "failed";

        response.sendRedirect(
            contextPath + "/projectmember/search?projectId="
            + URLEncoder.encode(projectId, StandardCharsets.UTF_8)
            + "&keyword="
            + URLEncoder.encode(keyword, StandardCharsets.UTF_8)
            + "&msg=" + msg
        );

        return false;
    }
}
