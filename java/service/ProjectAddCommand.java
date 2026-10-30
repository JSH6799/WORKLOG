package service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.ProjectDAO;
import model.ProjectDTO;
import model.MemberDTO; // MemberDTO 임포트 확인
import java.util.UUID;

public class ProjectAddCommand implements Command {
    @Override
    public boolean doCommand(HttpServletRequest request, HttpServletResponse response) {
        try {
            String projectName = request.getParameter("projectName");
            String description = request.getParameter("description");
            String startDate = request.getParameter("startDate");
            String endDate = request.getParameter("endDate");
            
            HttpSession session = request.getSession();
            
            // 1. "loginMember" 키로 세션에서 MemberDTO 객체 가져오기
            MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");
            
            String creatorId = null;
            if (loginMember != null) {
                creatorId = loginMember.getMemberId(); // 로그인한 유저의 진짜 아이디 추출!
            }
            
            // 2. 혹시라도 비로그인 상태이거나 세션이 만료된 경우의 방어 코드
            if (creatorId == null) {
                creatorId = "maromaro03"; // 필요시 기본값 또는 로그인 페이지 유도
            }

            ProjectDTO dto = new ProjectDTO();
            dto.setProjectId("PROJ_" + UUID.randomUUID().toString().substring(0, 8));
            dto.setTeamName("Default Team");
            dto.setProjectName(projectName);
            dto.setDescription(description);
            dto.setStartDate(startDate);
            dto.setEndDate(endDate);
            dto.setCreatorId(creatorId); // 동적으로 가져온 크리에이터 아이디 세팅

            ProjectDAO dao = ProjectDAO.getInstance();
            dao.insertProject(dto, creatorId);

            response.sendRedirect(request.getContextPath() + "/project/list");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}