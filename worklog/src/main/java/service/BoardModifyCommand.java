package service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import model.BoardDAO;
import model.BoardDTO;
import model.MemberDTO;

public class BoardModifyCommand implements Command {

	private static final String UPLOAD_DIR="C:/WORKLOG_upload";
	
	@Override
	public boolean doCommand(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("utf-8");
		
		HttpSession session = request.getSession();
		MemberDTO loginMember = (MemberDTO)session.getAttribute("loginMember");
		
		if(loginMember == null) {
			response.sendRedirect(request.getContextPath()+"/worklogMember/login");
			return false;
		}
		
		int bno = Integer.parseInt(request.getParameter("bno"));
		
		BoardDAO dao = new BoardDAO();
		BoardDTO board = dao.selectView(bno);
		
		if(board == null) {
			response.sendRedirect(request.getContextPath()+"/project_board/list");
			return false;
		}
		if(!board.getWriter().equals(loginMember.getMemberId())) {
			request.setAttribute("board", board);
			request.setAttribute("errMsg", "投稿者のみ修正可能です。");
			return true;
		}
		if("get".equalsIgnoreCase(request.getMethod())) {
			request.setAttribute("board", board);
			return true;
		}
		
		
		String title = request.getParameter("title");
		String content = request.getParameter("content");
		String checkRequired = request.getParameter("checkRequired");
		
		if (checkRequired == null) {
		    checkRequired = "N";
		}
		
		String removeImg = request.getParameter("removeImg");
		
		String oldImgPath = board.getImgPath();
		String finalImgPath = oldImgPath;
		
		Part filePart = request.getPart("imgFile");
		if(filePart != null && filePart.getSize() > 0) {
			deleteOldFile(oldImgPath);
			finalImgPath = saveFile(filePart);
		}else if("true".equals(removeImg)) {
			deleteOldFile(oldImgPath);
			finalImgPath = null;
		}
		
		BoardDTO updateDTO = new BoardDTO();
		
		updateDTO.setBno(bno);
		updateDTO.setTitle(title);
		updateDTO.setContent(content);
		updateDTO.setImgPath(finalImgPath);
		updateDTO.setCheckRequired(checkRequired);
		updateDTO.setWriter(board.getWriter());
		updateDTO.setProjectId(board.getProjectId());
		
		dao.update(updateDTO);
		response.sendRedirect(request.getContextPath()+"/project_board/view?bno=" + bno);
		
		return false;
	}
	
	
	public String saveFile(Part filepPart) throws IOException {
		String oriFileName = filepPart.getSubmittedFileName();
		String saveFileName = System.currentTimeMillis()+"-"+oriFileName;
		Path uploadDir = Paths.get(UPLOAD_DIR);
		if(!Files.exists(uploadDir)) {
			Files.createDirectories(uploadDir);
		}
		Path targetPath = uploadDir.resolve(saveFileName);
		try {
			InputStream input = filepPart.getInputStream();
			Files.copy(input, targetPath);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return saveFileName;
	}
	
	
	private void deleteOldFile(String fileName) {
		if(fileName == null) return;
		try {
			Files.deleteIfExists(Paths.get(UPLOAD_DIR, fileName));
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}
