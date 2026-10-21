package service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import model.BoardDAO;
import model.BoardDTO;

public class BoardWriteCommand implements Command {

	private static final String UPLOAD_DIR="C:/WORKLOG_upload";
	
	@Override
	public boolean doCommand(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("utf-8");
		
		// 로그인 세션 기능 추가 필요
		
		
		if("get".equalsIgnoreCase(request.getMethod())) {
			return true;
		}
		
		BoardDTO dto = new BoardDTO();
		
		dto.setTitle(request.getParameter("title"));
		dto.setContent(request.getParameter("content"));
		// setWriter는 로그인 객체 추가 후 getId
		dto.setProjectId(request.getParameter("project_id"));
		dto.setCheckRequired(request.getParameter("check_required"));
		
		
		Part filePart = request.getPart("imgFile");
		if(filePart != null && filePart.getSize() > 0) {
			String saveFileName = saveFile(filePart);
			dto.setImgPath(saveFileName);
		}
		
		BoardDAO dao = new BoardDAO();
		dao.insert(dto);
		
		response.sendRedirect(request.getContextPath()+"/project_board/list");
		
		return false;
	}
	
	
	public String saveFile(Part filepPart) throws IOException {
		
		String oriFileName = filepPart.getSubmittedFileName();
		String saveFileName = UUID.randomUUID()+"_"+oriFileName;
		
		Path uploadDir = Paths.get(UPLOAD_DIR);
		
		if(!Files.exists(uploadDir)) {
			Files.createDirectories(uploadDir);
		}
		
		Path targetPath = uploadDir.resolve(saveFileName);
		try(InputStream input = filepPart.getInputStream()){
			Files.copy(input, targetPath);
		}

		return saveFileName;
	
	}
	
}
