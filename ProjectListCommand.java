
package service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.ProjectDAO;
import model.ProjectDTO;
import model.MemberDTO;

import java.util.List;

public class ProjectListCommand implements Command {

    @Override
    public boolean doCommand(HttpServletRequest request,
                             HttpServletResponse response) {
        try {
            // 로그인 여부 확인
            HttpSession session = request.getSession(false);

            MemberDTO loginMember = null;

            if (session != null) {
                loginMember =
                    (MemberDTO) session.getAttribute("loginMember");
            }

            // 로그인하지 않은 경우 로그인 화면으로 이동
            if (loginMember == null
                    || loginMember.getMemberId() == null
                    || loginMember.getMemberId().trim().isEmpty()) {

                response.sendRedirect(
                    request.getContextPath() + "/member/login.jsp"
                );

                return false;
            }

            // 로그인한 회원의 프로젝트만 조회
            ProjectDAO dao = ProjectDAO.getInstance();

            List<ProjectDTO> list =
                dao.getProjectListByMember(loginMember.getMemberId());

            request.setAttribute("projectList", list);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }
}