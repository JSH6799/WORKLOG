
package service;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ProjectMemberDTO;
import model.ProjectMemberDao;

public class ProjectMemberCommand implements Command {

    @Override
    public boolean doCommand(HttpServletRequest request,
                             HttpServletResponse response)
            throws ServletException, IOException {
    	
    	//로그인 세션 기능 속성 
    	request.setCharacterEncoding("utf-8");
		
//		HttpSession session = request.getSession();
//		MemberDTO loginMember = (MemberDTO)session.getAttribute("loginMember");
		
//		if(loginMember == null) {
//			response.sendRedirect(request.getContextPath()+"/worklogMember/login");
//			return false;
//		}

        // 1. 요청에서 프로젝트 ID 가져오기
        String projectId = request.getParameter("projectId");
        
        System.out.println("projectId = " + projectId);

        if (projectId == null || projectId.trim().isEmpty()) {
            request.setAttribute("memberList",
                    java.util.Collections.emptyList());
            request.setAttribute("errorMessage", "프로젝트 ID가 없습니다.");
            return true;
        }

        try {
            // 2. DAO 객체 만들기
            ProjectMemberDao dao = new ProjectMemberDao();

            // 3. DB에서 해당 프로젝트의 팀원 목록 조회하기
            List<ProjectMemberDTO> list = dao.search(projectId);
            
            System.out.println("전달받은 projectId = " + projectId);
            System.out.println("조회된 팀원 수 = " + list.size());
            
         // 조회된 DTO 값 확인
            for (ProjectMemberDTO pm : list) {
            	 System.out.println("--------------------");
            	    System.out.println("memberId = " + pm.getMemberId());
            	    System.out.println("memberName = " + pm.getMemberName());
            	    System.out.println("role = " + pm.getRole());
            	    System.out.println("email = " + pm.getEmail());
            	}

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