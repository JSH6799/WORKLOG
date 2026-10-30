package model;

public class BoardDTO {

	private int bno;
    private String title;
    private String content;
    private String writer;
    private String imgPath;
    private String writeDate;
    private String projectId;
    private String checkRequired;
    
    private String writerName;
    
    private String projectName;
    private String bookmarkDate;
    
    private String checkYn;
    private int checkCompleteCount;
    private int checkTotalCount;
    
	public int getBno() {
		return bno;
	}
	public void setBno(int bno) {
		this.bno = bno;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getWriter() {
		return writer;
	}
	public void setWriter(String writer) {
		this.writer = writer;
	}
	public String getImgPath() {
		return imgPath;
	}
	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}
	public String getWriteDate() {
		return writeDate;
	}
	public void setWriteDate(String writeDate) {
		this.writeDate = writeDate;
	}
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	public String getCheckRequired() {
		return checkRequired;
	}
	public void setCheckRequired(String checkRequired) {
		this.checkRequired = checkRequired;
	}
	public String getWriterName() {
		return writerName;
	}
	public void setWriterName(String writerName) {
		this.writerName = writerName;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getBookmarkDate() {
		return bookmarkDate;
	}
	public void setBookmarkDate(String bookmarkDate) {
		this.bookmarkDate = bookmarkDate;
	}
	public String getCheckYn() {
		return checkYn;
	}
	public void setCheckYn(String checkYn) {
		this.checkYn = checkYn;
	}
	public int getCheckCompleteCount() {
		return checkCompleteCount;
	}
	public void setCheckCompleteCount(int checkCompleteCount) {
		this.checkCompleteCount = checkCompleteCount;
	}
	public int getCheckTotalCount() {
		return checkTotalCount;
	}
	public void setCheckTotalCount(int checkTotalCount) {
		this.checkTotalCount = checkTotalCount;
	}
	
	
}