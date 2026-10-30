package service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.BookmarkDAO;
import model.MemberDTO;

public class BookmarkDeleteCommand implements Command {

	@Override
	public boolean doCommand(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("utf-8");
		response.setContentType("application/json; charset=utf-8");
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		HttpSession session = request.getSession();
		MemberDTO loginMember = (MemberDTO)session.getAttribute("loginMember");
		
		if(loginMember == null) {
			result.put("success", false);
			result.put("message", "ログインが必要です");
            response.getWriter().print(new Gson().toJson(result));
            return false;
		}
		
		String[] bnoParams = request.getParameterValues("bno");
		
		if(bnoParams == null || bnoParams.length == 0) {
			result.put("success", false);
            result.put("message", "削除する投稿を選択してください");
            response.getWriter().print(new Gson().toJson(result));
            return false;
		}
		
		List<Integer> bnoList = new ArrayList<Integer>();
		
		try {
			for(String bno : bnoParams) {
				bnoList.add(Integer.parseInt(bno));
			}
		}catch (NumberFormatException e) {
			 result.put("success", false);
	         result.put("message", "投稿番号が正しくありません");
	         response.getWriter().print(new Gson().toJson(result));
	         return false;
		}
		
		BookmarkDAO dao = new BookmarkDAO();
		boolean success = dao.deleteBookmarks(bnoList, loginMember.getMemberId());
		
		result.put("success", success);
		
		if(!success) {
			result.put("message", "ブックマークの削除に失敗しました");
		}
		
		response.getWriter().print(new Gson().toJson(result));
		
		return false;
	}

}
