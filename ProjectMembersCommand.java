package service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.MemberDTO;
import model.ProjectDAO;
import model.ProjectMemberDTO;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ProjectMembersCommand implements Command {

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

            String loginId = loginMember.getMemberId();
            ProjectDAO dao = ProjectDAO.getInstance();

            List<ProjectMemberDTO> list = dao.getProjectMembers(projectId);

            // 自分の権限をDBのメンバー一覧から判定 (パラメータは信用しない)
            // OWNER=リーダー, ADMIN=メンバー, MEMBER=閲覧者
            String myRole = "MEMBER";

            if (list != null) {
                for (ProjectMemberDTO m : list) {
                    if (loginId.equals(m.getMemberId())) {
                        myRole = m.getRole();
                        break;
                    }
                }
            }

            String newRole = request.getParameter("newRole");
            String targetMemberId = request.getParameter("targetMemberId");

            // 権限の変更 (POSTのみ、リーダーのみ)
            if (newRole != null && targetMemberId != null) {

                boolean isPost = "POST".equalsIgnoreCase(request.getMethod());
                boolean validRole =
                    "ADMIN".equals(newRole) || "MEMBER".equals(newRole);

                boolean targetIsOwner = false;
                boolean targetExists = false;

                if (list != null) {
                    for (ProjectMemberDTO m : list) {
                        if (targetMemberId.equals(m.getMemberId())) {
                            targetExists = true;
                            targetIsOwner = "OWNER".equals(m.getRole());
                            break;
                        }
                    }
                }

                if (!isPost
                        || !"OWNER".equals(myRole)
                        || !validRole
                        || !targetExists
                        || targetIsOwner) {

                    response.setContentType("text/html; charset=UTF-8");
                    response.getWriter().println(
                        "<script>" +
                        "alert('メンバー権限を変更できるのはプロジェクトリーダーのみです。');" +
                        "history.back();" +
                        "</script>"
                    );
                    return false;
                }

                dao.updateMemberRole(projectId, targetMemberId, newRole);

                // 再読み込みによる二重送信を防ぐためリダイレクト
                response.sendRedirect(
                    contextPath + "/project/members?projectId="
                    + URLEncoder.encode(projectId, StandardCharsets.UTF_8)
                    + "&msg=roleChanged"
                );
                return false;
            }

            request.setAttribute("memberList", list);
            request.setAttribute("projectId", projectId);
            request.setAttribute("myRole", myRole);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}