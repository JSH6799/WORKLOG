package service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.ProjectDAO;
import model.ProjectDTO;
import model.MemberDTO;

import java.io.IOException;
import java.util.UUID;

public class ProjectAddCommand implements Command {

    @Override
    public boolean doCommand(HttpServletRequest request,
                             HttpServletResponse response) {

        try {
        
            HttpSession session = request.getSession(false);

            MemberDTO loginMember = null;

            if (session != null) {
                loginMember =
                    (MemberDTO) session.getAttribute("loginMember");
            }

            // 未ログインの場合はログイン画面へ移動
            if (loginMember == null
                    || loginMember.getMemberId() == null
                    || loginMember.getMemberId().trim().isEmpty()) {

                response.sendRedirect(
                    request.getContextPath() + "/member/login.jsp"
                );
                return false;
            }
            
            if (!"POST".equalsIgnoreCase(request.getMethod())) {
                response.sendRedirect(
                    request.getContextPath() + "/project/list"
                );
                return false;
            }

          
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
                startDate == null || startDate.isEmpty() ? null : startDate
            );
            dto.setEndDate(
                endDate == null || endDate.isEmpty() ? null : endDate
            );
            dto.setCreatorId(creatorId);

            ProjectDAO dao = ProjectDAO.getInstance();
            dao.insertProject(dto, creatorId);

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
                        "alert('プロジェクトの作成に失敗しました。');" +
                        "location.href='" +
                        request.getContextPath() +
                        "/project/list';" +
                        "</script>"
                    );
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            return false;
        }
    }
}
