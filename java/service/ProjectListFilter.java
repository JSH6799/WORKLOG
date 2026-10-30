package service;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.BoardDAO;
import model.MemberDTO;
import model.ProjectDAO;
import model.ProjectDTO;

@WebFilter("/*")
public class ProjectListFilter implements Filter {

    @Override
    public void doFilter(jakarta.servlet.ServletRequest request,
                         jakarta.servlet.ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String uri = req.getRequestURI();

        // CSS, JS, 이미지 등 정적 파일은 건너뛰기
        if (uri.matches("(?i).*\\.(css|js|png|jpg|jpeg|gif|svg|ico|woff2?)$")) {
            chain.doFilter(request, response);
            return;
        }
        
        if (uri.matches("(?i).*\\.(css|js|png|jpg|jpeg|gif|svg|ico|woff2?)$")) {
            chain.doFilter(request, response);
            return;
        }
        if (uri.startsWith(req.getContextPath() + "/worklogCalendar/event")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        MemberDTO loginMember = null;

        if (session != null) {
            loginMember = (MemberDTO) session.getAttribute("loginMember");
        }

        if (loginMember != null) {
            BoardDAO dao = new BoardDAO();

            List<ProjectDTO> projectList =
                    dao.selectMyProjects(loginMember.getMemberId());

            req.setAttribute("proList", projectList);
        }

        // 다음 필터 또는 컨트롤러로 요청 전달
        chain.doFilter(request, response);
    }
}
