package service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ProjectMemberDAO;
import model.ProjectMemberDTO;

public class ProjectMemberCommand implements Command {

	@Override
	public boolean doCommand(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String projectId = request.getParameter("projectId");
		
		if(projectId == null || projectId.trim().isEmpty()) {
			request.setAttribute("memberList", Collections.emptyList());
			request.setAttribute("errorMessage", "프로젝트 ID가 없습니다");
			return true;
		}
	
	try {
		ProjectMemberDAO dao = new ProjectMemberDAO();
		
		List<ProjectMemberDTO> list = new ArrayList<ProjectMemberDTO>();
		
		request.setAttribute("memberList", list);

	}catch (Exception e) {
		e.printStackTrace();
		request.setAttribute("errorMessage", "회원 목록 조회 중 오류가 발생 ㅜㅜ");
	}
	return true;
}
}