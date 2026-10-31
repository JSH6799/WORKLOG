package controller;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.Command;
import service.ProjectMemberCommand;
import service.ProjectMemberDeleteCommmand;
import service.ProjectMemberSearch;
import service.ProjectmemberUpdateCommnand;
import service.ProjectInvitationAcceptCommand;
import service.ProjectInvitationPendingCommand;
import service.ProjectInvitationSend;
import service.ProjectInvitationRejectCommand;
import service.ProjectMemberAddCommand;

import java.io.IOException;

@WebServlet("/projectmember/*")
public class projectMemeberController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	public projectMemeberController() {
	    super();
	}

	@Override
	protected void doGet(HttpServletRequest request,
	                     HttpServletResponse response)
	        throws ServletException, IOException {

	    doAction(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request,
	                      HttpServletResponse response)
	        throws ServletException, IOException {

	    request.setCharacterEncoding("UTF-8");
	    doAction(request, response);
	}

	protected void doAction(HttpServletRequest request,
	                        HttpServletResponse response)
	        throws ServletException, IOException {

	    String pathInfo = request.getPathInfo();

	    System.out.println("요청 방식 = " + request.getMethod());
	    System.out.println("pathInfo = " + pathInfo);

	    String page = null;
	    Command command = null;

	    if (pathInfo == null || pathInfo.equals("/")) {
	        pathInfo = "/members";
	    }

	    switch (pathInfo) {

	        // 팀원 목록 조회
	        case "/members":
	            command = new ProjectMemberCommand();
	            page = "/projectJSP/projectmember.jsp";
	            break;

	        // 팀원 검색
	        case "/search":
	            command = new ProjectMemberSearch();
	            page = "/projectJSP/projectmembersearch.jsp";
	            break;

	        // 팀원 역할 수정
	        case "/update":
	            command = new ProjectmemberUpdateCommnand();
	            break;

	        // 멤버 초대 전송
	        case "/invitation/send":
	            command = new ProjectInvitationSend();
	            page = "/projectJSP/invitationpending.jsp";
	            break;

	        // 초대 수락
	        case "/invitation/accept":
	            command = new ProjectInvitationAcceptCommand();
	            break;
	        // 초대 거절
	        case "/invitation/reject":
	        	command = new ProjectInvitationRejectCommand();
	        	break;

	        // 팀원 삭제
	        case "/delete":
	        	command = new ProjectMemberDeleteCommmand();
	        	break;

	        // 받은 초대 목록 조회
	        case "/invitation/pending":
	            command = new ProjectInvitationPendingCommand();
	            page = "/projectJSP/invitationpending.jsp";
	            break;
	         // 팀원 바로 추가
	        case "/add":
	            command = new ProjectMemberAddCommand();
	            page = "/projectJSP/projectmember.jsp";
	            break;
	        
	            
	        default:
	            response.sendError(
	                HttpServletResponse.SC_NOT_FOUND,
	                "요청 경로를 찾을 수 없습니다: " + pathInfo
	            );
	            return;
	    }

	    try {
	        // 실제 실행할 Command 확인
	        System.out.println("실행할 command = "
	                + command.getClass().getName());

	        // Service 실행
	        boolean result = command.doCommand(request, response);

	        System.out.println("Command 실행 결과 = " + result);
	        System.out.println("JSP 이동 경로 = " + page);
	        System.out.println("forward 직전 memberList = "
	                + request.getAttribute("memberList"));
	        
	        
	        // 결과 페이지로 이동
	        if (result && page != null && !response.isCommitted()) {
	            request.getRequestDispatcher(page)
	                   .forward(request, response);
	        }

	    } catch (Exception e) {

	        // 오류 원인을 콘솔에 출력
	        System.out.println("요청 처리 중 오류 발생");
	        e.printStackTrace();

	        if (!response.isCommitted()) {
	            response.sendError(
	                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
	                "요청 처리 중 오류가 발생했습니다."
	            );
	        }
	    }
	}

	}
