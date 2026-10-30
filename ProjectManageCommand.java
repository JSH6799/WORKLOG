package service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.MemberDTO;
import model.ProjectDAO;
import model.ProjectDTO;
import model.ProjectMemberDTO;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ProjectManageCommand implements Command {

    @Override
    public boolean doCommand(HttpServletRequest request,
                             HttpServletResponse response) {

        String contextPath = request.getContextPath();

        try {
            String projectId = request.getParameter("projectId");

            if (projectId == null || projectId.trim().isEmpty()) {
                response.sendRedirect(contextPath + "/project/list");
                return false;
            }

            HttpSession session = request.getSession(false);
            MemberDTO loginMember = null;

            if (session != null) {
                loginMember =
                    (MemberDTO) session.getAttribute("loginMember");
            }

            if (loginMember == null
                    || loginMember.getMemberId() == null) {
                response.sendRedirect(contextPath + "/member/login.jsp");
                return false;
            }

            ProjectDAO dao = ProjectDAO.getInstance();
            ProjectDTO project = dao.getProjectById(projectId);

            if (project == null) {
                response.sendRedirect(contextPath + "/project/list");
                return false;
            }

            String loginId = loginMember.getMemberId();

            boolean isOwner =
                project.getCreatorId() != null
                && project.getCreatorId().equals(loginId);

            // 自分の権限と作成者名を、プロジェクトメンバー一覧から取得
            // OWNER=リーダー, ADMIN=メンバー, MEMBER=閲覧者
            String myRole = isOwner ? "OWNER" : "MEMBER";
            String creatorName = null;

            List<ProjectMemberDTO> members = dao.getProjectMembers(projectId);

            if (members != null) {
                for (ProjectMemberDTO m : members) {
                    if (!isOwner && loginId.equals(m.getMemberId())) {
                        myRole = m.getRole();
                    }
                    if (project.getCreatorId() != null
                            && project.getCreatorId().equals(m.getMemberId())) {
                        creatorName = m.getMemberName();
                    }
                }
            }

            // 修正できるのは リーダー(OWNER) と メンバー(ADMIN)
            boolean canEdit =
                "OWNER".equals(myRole) || "ADMIN".equals(myRole);

            // POST：プロジェクト情報の更新
            if ("POST".equalsIgnoreCase(request.getMethod())) {

                if (!canEdit) {
                    response.setContentType("text/html; charset=UTF-8");
                    response.getWriter().println(
                        "<script>" +
                        "alert('このプロジェクトを編集する権限がありません。');" +
                        "location.href='" + contextPath + "/project/list';" +
                        "</script>"
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

                project.setProjectName(projectName.trim());
                project.setTeamName(teamName.trim());
                project.setDescription(description);
                project.setStartDate(
                    startDate == null || startDate.isEmpty() ? null : startDate
                );
                project.setEndDate(
                    endDate == null || endDate.isEmpty() ? null : endDate
                );

                dao.updateProject(project);

                response.sendRedirect(
                    contextPath + "/project/manage?projectId="
                    + URLEncoder.encode(projectId, StandardCharsets.UTF_8)
                    + "&msg=saved"
                );
                return false;
            }

            // GET：画面表示用データ
            request.setAttribute("project", project);
            request.setAttribute("isOwner", isOwner);
            request.setAttribute("myRole", myRole);
            request.setAttribute("role", myRole);
            request.setAttribute("creatorName", creatorName);

            return true;

        } catch (Exception e) {
            e.printStackTrace();

            try {
                if (!response.isCommitted()) {
                    response.sendRedirect(contextPath + "/project/list");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return false;
        }
    }
}