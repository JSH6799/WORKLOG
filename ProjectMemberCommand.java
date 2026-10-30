
package service;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import model.ProjectMemberDTO;
import model.ProjectMemberDAO;

public class ProjectMemberCommand implements Command {

    @Override
    public boolean doCommand(HttpServletRequest request,
                             HttpServletResponse response)
            throws ServletException, IOException {

        // 1. 요청에서 프로젝트 ID 가져오기
        String projectId = request.getParameter("projectId");

        if (projectId == null || projectId.trim().isEmpty()) {
            request.setAttribute("memberList",
                    java.util.Collections.emptyList());
            request.setAttribute("errorMessage", "프로젝트 ID가 없습니다.");
            return true;
        }

        try {
            // 2. DAO 객체 만들기
            ProjectMemberDAO dao = new ProjectMemberDAO();

            // 3. DB에서 해당 프로젝트의 팀원 목록 조회하기
            List<ProjectMemberDTO> list = dao.search(projectId);

            // 4. 조회 결과를 JSP로 전달하기
            request.setAttribute("memberList", list);
            request.setAttribute("projectId", projectId);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("memberList",
                    java.util.Collections.emptyList());
            request.setAttribute("errorMessage",
                    "팀원 목록 조회 중 오류가 발생했습니다.");
        }

        // 5. Controller가 JSP로 이동하도록 허용
        return true;
    }
}