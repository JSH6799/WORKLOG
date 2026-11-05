package service;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.BoardCheckDAO;
import model.BoardCheckDTO;
import model.BoardDAO;
import model.BoardDTO;
import model.MemberDTO;
import model.ProjectDTO;

public class MainCommand implements Command {

    @Override
    public boolean doCommand(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

    	request.setCharacterEncoding("utf-8");

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("loginMember") == null) {
            response.sendRedirect(request.getContextPath() + "/worklogMember/login");
            return false;
        }

        MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");

        String memberId = loginMember.getMemberId();
        BoardDAO dao = new BoardDAO();

        // 참여 프로젝트 목록
        List<ProjectDTO> projectList = dao.selectMyProjects(memberId);

        // 전체 미확인 게시글 건수
        int unreadPostCount = 0;

        if (projectList != null) {
            for (ProjectDTO project : projectList) {
                unreadPostCount += dao.selectUnreadCountByProject(memberId, project.getProjectId());
            }
        }
        
        List<BoardDTO> recentPosts = dao.selectRecentBoardList(memberId);
        
     // 게시글 확인 상태 및 작성자 확인 인원수 계산
        BoardCheckDAO checkDao = new BoardCheckDAO();

        if (recentPosts != null) {
            for (BoardDTO post : recentPosts) {

                // 게시글의 확인 필요 여부 가져오기
                BoardDTO view = dao.selectView(post.getBno());

                if (view != null) {
                    post.setCheckRequired(view.getCheckRequired());
                }

                // 확인이 필요한 게시글만 처리
                if ("Y".equals(post.getCheckRequired())) {

                    if (memberId.equals(post.getWriter())) {

                        // 작성자: 확인 완료 인원 / 전체 확인 대상 인원
                        List<BoardCheckDTO> checkList =
                                checkDao.selectCheckList(post.getBno());

                        int completeCount = 0;
                        int totalCountForCheck = checkList.size();

                        for (BoardCheckDTO check : checkList) {
                            if ("確認完了".equals(check.getCheckStatus())) {
                                completeCount++;
                            }
                        }

                        post.setCheckCompleteCount(completeCount);
                        post.setCheckTotalCount(totalCountForCheck);

                    } else {

                        // 일반 회원: 본인의 확인 여부
                        boolean checked =
                                checkDao.isChecked(post.getBno(), memberId);

                        post.setCheckYn(checked ? "Y" : "N");
                    }
                }
            }
        }
        
        request.setAttribute("recentPosts", recentPosts);
        request.setAttribute("unreadPostCount", unreadPostCount);
        request.setAttribute("userName", loginMember.getMemberName());

        return true;
    }
}