package utill;

import java.sql.Connection;

public class DBtest {



	public static void main(String[] args) {
		
		try {
			Connection conn = DButil.getInstance();
			if(conn != null) {
				System.out.println("접속에 성공~");
			}else {
				System.out.println("접속이 안되는데 ㅜㅜㅜ?");
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}
