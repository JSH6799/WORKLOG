package service;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ProjectMemberDTO;
import model.ProjectMemberDao;

public class ProjectMemberAddCommand implements Command {

	@Override
	public boolean doCommand(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		
 request.setCharacterEncoding("UTF-8");

        String projectId = request.getParameter("projectId");
        String memberId = request.getParameter("memberId");

        if (projectId == null || projectId.trim().isEmpty()
                || memberId == null || memberId.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "프로젝트 ID 또는 회원 ID가 없습니다.");
            return false;
        }

        try {
            ProjectMemberDTO dto = new ProjectMemberDTO();

            dto.setProjectId(projectId);
            dto.setMemberId(memberId);
            dto.setRole("MEMBER");

            ProjectMemberDao dao = new ProjectMemberDao();
            boolean result = dao.insert(dto);

            // 추가 후 프로젝트 팀원 목록으로 이동
            if (result) {
            response.sendRedirect(
                request.getContextPath()
                + "/projectmember/members?projectId=" + projectId
            );

            return false;
            } else {
                request.setAttribute("errorMessage", "팀원 추가에 실패했습니다.");
                return true;
            } 

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "팀원 추가에 실패했습니다. 이미 등록된 팀원인지 확인해 주세요."
            );
            return false;
        }
        
	}
	
}