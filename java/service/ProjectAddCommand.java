package service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.ProjectDAO;
import model.ProjectDTO;
import model.MemberDTO;

import java.util.UUID;

public class ProjectAddCommand implements Command {

    @Override
    public boolean doCommand(HttpServletRequest request,
                             HttpServletResponse response) {

        try {
            // POST 요청만 처리
            if (!"POST".equalsIgnoreCase(request.getMethod())) {
                response.sendRedirect(
                    request.getContextPath() + "/project/list"
                );
                return false;
            }

            // 로그인 회원 확인
            HttpSession session = request.getSession(false);

            MemberDTO loginMember = null;

            if (session != null) {
                loginMember =
                    (MemberDTO) session.getAttribute("loginMember");
            }

            if (loginMember == null
                    || loginMember.getMemberId() == null
                    || loginMember.getMemberId().trim().isEmpty()) {

                response.sendRedirect(
                    request.getContextPath() + "/member/login.jsp"
                );
                return false;
            }

            // 입력값 확인
            String projectName = request.getParameter("projectName");
            String teamName = request.getParameter("teamName");
            String description = request.getParameter("description");
            String startDate = request.getParameter("startDate");
            String endDate = request.getParameter("endDate");

            if (projectName == null || projectName.trim().isEmpty()
                    || teamName == null || teamName.trim().isEmpty()) {

                response.setContentType("text/html; charset=UTF-8");
                response.getWriter().println(
                    "<script>" +
                    "alert('プロジェクト名とチーム名を入力してください。');" +
                    "history.back();" +
                    "</script>"
                );
                return false;
            }

            String creatorId = loginMember.getMemberId();

            ProjectDTO dto = new ProjectDTO();
            dto.setProjectId(
                "PROJ_" + UUID.randomUUID().toString().substring(0, 8)
            );
            dto.setTeamName(teamName.trim());
            dto.setProjectName(projectName.trim());
            dto.setDescription(description);
            dto.setStartDate(
                startDate == null || startDate.trim().isEmpty()
                    ? null : startDate
            );
            dto.setEndDate(
                endDate == null || endDate.trim().isEmpty()
                    ? null : endDate
            );
            dto.setCreatorId(creatorId);

            // DB 저장
            ProjectDAO dao = ProjectDAO.getInstance();
            dao.insertProject(dto, creatorId);

            // 저장 요청이 정상 처리된 경우 목록으로 이동
            response.sendRedirect(
                request.getContextPath() + "/project/list"
            );
            return false;

        } catch (Exception e) {
            e.printStackTrace();

            try {
                if (!response.isCommitted()) {
                    response.setContentType("text/html; charset=UTF-8");
                    response.getWriter().println(
                        "<script>" +
                        "alert('プロジェクトの作成に失敗しました。サーバーログを確認してください。');" +
                        "history.back();" +
                        "</script>"
                    );
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return false;
        }
    }
}