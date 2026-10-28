
package service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.MemberDTO;
import model.ProjectDAO;
import model.ProjectDTO;

public class ProjectDeleteCommand implements Command {

    @Override
    public boolean doCommand(HttpServletRequest request,
                             HttpServletResponse response) {
        try {
            String projectId = request.getParameter("projectId");
            String contextPath = request.getContextPath();

            // 프로젝트 ID 확인
            if (projectId == null || projectId.trim().isEmpty()) {
                response.sendRedirect(contextPath + "/project/list");
                return false;
            }

            // 로그인 상태 확인
            HttpSession session = request.getSession(false);

            MemberDTO loginMember = null;

            if (session != null) {
                loginMember = (MemberDTO) session.getAttribute("loginMember");
            }

            if (loginMember == null) {
                response.sendRedirect(contextPath + "/member/login");
                return false;
            }

            ProjectDAO dao = ProjectDAO.getInstance();
            ProjectDTO project = dao.getProjectById(projectId);

            // 존재하지 않는 프로젝트
            if (project == null) {
                response.sendRedirect(contextPath + "/project/list");
                return false;
            }

            // 프로젝트 생성자와 로그인 아이디 비교
            if (project.getCreatorId() == null
                    || !project.getCreatorId().equals(loginMember.getMemberId())) {

                response.setContentType("text/html; charset=UTF-8");

                response.getWriter().println(
                    "<script>" +
                    "alert('本人が作成したプロジェクトのみ削除可能です。');" +
                    "location.href='" + contextPath + "/project/list';" +
                    "</script>"
                );

                return false;
            }

            // 본인이 만든 프로젝트만 삭제
            dao.deleteProject(projectId);

            response.sendRedirect(contextPath + "/project/list");
            return false;

        } catch (Exception e) {
            e.printStackTrace();

            try {
                if (!response.isCommitted()) {
                    response.sendRedirect(
                        request.getContextPath() + "/project/list"
                    );
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return false;
        }
    }
}
