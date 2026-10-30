package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class CalendarEventDTO {

    // ---- 역할 상수 (project_member.role) ----
    public static final String ROLE_LEADER = "1";
    public static final String ROLE_MEMBER = "2";
    public static final String ROLE_VIEWER = "3";

    private int eventId;
    private String projectId;
    private String title;
    private String memo;
    private String iconKey;        // MEETING, DEADLINE, ... (이모지 변환은 JS에서)
    private String colorCls;       // blue / green / purple / red
    private LocalDate startDate;   // 포함
    private LocalDate endDate;     // 포함
    private String status;         // CONFIRMED / PENDING
    private boolean deadline;
    private String confirmedTime;  // 'HH24:MI'
    private String createdBy;

    // 목록용 집계값
    private int yesCount;
    private int totalCount;
    private int voterCount;

    // 상세용
    private List<Attendee> attendees = new ArrayList<>();
    private List<Slot> slots = new ArrayList<>();

    /** 참가자 한 명 */
    public static class Attendee {
        private String memberId;
        private String memberName;
        private String status;     // YES / MAYBE / NO
        public String getMemberId() { return memberId; }
        public void setMemberId(String v) { this.memberId = v; }
        public String getMemberName() { return memberName; }
        public void setMemberName(String v) { this.memberName = v; }
        public String getStatus() { return status; }
        public void setStatus(String v) { this.status = v; }
    }

    /** 후보 시간 한 개 */
    public static class Slot {
        private int slotId;
        private String time;       // 'HH24:MI'
        private int votes;
        private boolean votedByMe;
        public int getSlotId() { return slotId; }
        public void setSlotId(int v) { this.slotId = v; }
        public String getTime() { return time; }
        public void setTime(String v) { this.time = v; }
        public int getVotes() { return votes; }
        public void setVotes(int v) { this.votes = v; }
        public boolean isVotedByMe() { return votedByMe; }
        public void setVotedByMe(boolean v) { this.votedByMe = v; }
    }

    public boolean isPending() { return "PENDING".equals(status); }

    public int getEventId() { return eventId; }
    public void setEventId(int v) { this.eventId = v; }
    public String getProjectId() { return projectId; }
    public void setProjectId(String v) { this.projectId = v; }
    public String getTitle() { return title; }
    public void setTitle(String v) { this.title = v; }
    public String getMemo() { return memo; }
    public void setMemo(String v) { this.memo = v; }
    public String getIconKey() { return iconKey; }
    public void setIconKey(String v) { this.iconKey = v; }
    public String getColorCls() { return colorCls; }
    public void setColorCls(String v) { this.colorCls = v; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate v) { this.startDate = v; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate v) { this.endDate = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }
    public boolean isDeadline() { return deadline; }
    public void setDeadline(boolean v) { this.deadline = v; }
    public String getConfirmedTime() { return confirmedTime; }
    public void setConfirmedTime(String v) { this.confirmedTime = v; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String v) { this.createdBy = v; }
    public int getYesCount() { return yesCount; }
    public void setYesCount(int v) { this.yesCount = v; }
    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int v) { this.totalCount = v; }
    public int getVoterCount() { return voterCount; }
    public void setVoterCount(int v) { this.voterCount = v; }
    public List<Attendee> getAttendees() { return attendees; }
    public void setAttendees(List<Attendee> v) { this.attendees = v; }
    public List<Slot> getSlots() { return slots; }
    public void setSlots(List<Slot> v) { this.slots = v; }
}