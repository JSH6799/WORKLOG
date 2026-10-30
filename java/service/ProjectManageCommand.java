
package service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.MemberDTO;
import model.ProjectDAO;
import model.ProjectDTO;

public class ProjectManageCommand implements Command {

    @Override
    public boolean doCommand(HttpServletRequest request,
                             HttpServletResponse response) {

        String contextPath = request.getContextPath();

        try {
            String projectId = request.getParameter("projectId");

            // プロジェクトIDの確認
            if (projectId == null || projectId.trim().isEmpty()) {
                response.sendRedirect(contextPath + "/project/list");
                return false;
            }

            // ログイン状態の確認
            HttpSession session = request.getSession(false);
            MemberDTO loginMember = null;

            if (session != null) {
                loginMember =
                    (MemberDTO) session.getAttribute("loginMember");
            }

            if (loginMember == null) {
                response.sendRedirect(contextPath + "/member/login");
                return false;
            }

            ProjectDAO dao = ProjectDAO.getInstance();

            // プロジェクト情報の取得
            ProjectDTO project = dao.getProjectById(projectId);

            if (project == null) {
                response.sendRedirect(contextPath + "/project/list");
                return false;
            }

            // 作成者とログインユーザーを比較
            boolean isOwner =
                project.getCreatorId() != null
                && project.getCreatorId().equals(loginMember.getMemberId());

            // POST：更新処理
            if ("POST".equalsIgnoreCase(request.getMethod())) {

                // 作成者以外の更新を禁止
                if (!isOwner) {
                    response.setContentType("text/html; charset=UTF-8");
                    response.getWriter().println(
                        "<script>" +
                        "alert('自分が作成したプロジェクトのみ編集できます。');" +
                        "location.href='" + contextPath + "/project/list';" +
                        "</script>"
                    );
                    return false;
                }

                String projectName = request.getParameter("projectName");
                String description = request.getParameter("description");
                String startDate = request.getParameter("startDate");
                String endDate = request.getParameter("endDate");

                project.setProjectName(projectName);
                project.setDescription(description);
                project.setStartDate(startDate);
                project.setEndDate(endDate);

                dao.updateProject(project);

                response.sendRedirect(
                    contextPath + "/project/manage?projectId="
                    + java.net.URLEncoder.encode(
                        projectId, java.nio.charset.StandardCharsets.UTF_8
                    )
                    + "&msg=saved"
                );

                return false;
            }

            // GET：画面表示用データを設定
            request.setAttribute("project", project);
            request.setAttribute("isOwner", isOwner);
            request.setAttribute("role", isOwner ? "OWNER" : "VIEWER");

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
