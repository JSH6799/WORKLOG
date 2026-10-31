package model;

public class ProjectInvitationDTO {
	
	private int invitationId;
	private String projectId;
    private String inviterId;
    private String inviteeId;
    private String invitationStatus;
    private String invitedDate;
    private String respondedDate;
    private String role;
    
    
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public int getInvitationId() {
		return invitationId;
	}
	public void setInvitationId(int invitationId) {
		this.invitationId = invitationId;
	}
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	public String getInviterId() {
		return inviterId;
	}
	public void setInviterId(String inviterId) {
		this.inviterId = inviterId;
	}
	public String getInviteeId() {
		return inviteeId;
	}
	public void setInviteeId(String inviteeId) {
		this.inviteeId = inviteeId;
	}
	public String getInvitationStatus() {
		return invitationStatus;
	}
	public void setInvitationStatus(String invitationStatus) {
		this.invitationStatus = invitationStatus;
	}
	public String getInvitedDate() {
		return invitedDate;
	}
	public void setInvitedDate(String invitedDate) {
		this.invitedDate = invitedDate;
	}
	public String getRespondedDate() {
		return respondedDate;
	}
	public void setRespondedDate(String respondedDate) {
		this.respondedDate = respondedDate;
	}
    
}
