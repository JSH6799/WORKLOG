package model;

public class BoardCheckDTO {

	private int bno;
    private String memberId;
    private String checkYN;
    private String checkDate;
    
    
	public int getBno() {
		return bno;
	}
	public void setBno(int bno) {
		this.bno = bno;
	}
	public String getMemberId() {
		return memberId;
	}
	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}
	public String getCheckYN() {
		return checkYN;
	}
	public void setCheckYN(String checkYN) {
		this.checkYN = checkYN;
	}
	public String getCheckDate() {
		return checkDate;
	}
	public void setCheckDate(String checkDate) {
		this.checkDate = checkDate;
	}
    
	
}
