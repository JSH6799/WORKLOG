package model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import util.DBUtil;

public class CalendarDAO {

    /* =========================================================
     * 권한: 프로젝트 내 내 역할 ('1','2','3'), 소속 아니면 null
     * ========================================================= */
    public String getRole(String projectId, String memberId) throws SQLException {
        String sql = "select role from project_member where project_id = ? and member_id = ?";
        try (Connection con = DBUtil.getInstance();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, projectId);
            ps.setString(2, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        }
    }

    /* =========================================================
     * 프로젝트 멤버 목록: {member_id, member_name, role}
     * ========================================================= */
    public List<String[]> findMembers(String projectId) throws SQLException {
        String sql =
            "select pm.member_id, m.member_name, pm.role " +
            "from project_member pm join member m on m.member_id = pm.member_id " +
            "where pm.project_id = ? order by pm.role, pm.member_id";
        List<String[]> list = new ArrayList<>();
        try (Connection con = DBUtil.getInstance();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, projectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new String[] { rs.getString(1), rs.getString(2), rs.getString(3) });
                }
            }
        }
        return list;
    }

    /* =========================================================
     * 목록 조회 (FullCalendar 표시 범위)
     * rangeStart / rangeEnd 모두 "포함" 기준 날짜
     * total_cnt : 보기전용(role 3)을 제외한 프로젝트 멤버 수 (상세 화면과 동일 기준)
     * ========================================================= */
    public List<CalendarEventDTO> findByRange(String projectId, LocalDate rangeStart, LocalDate rangeEnd)
            throws SQLException {
        String sql =
            "select e.*, " +
            "  (select count(*) from event_attendee a where a.event_id = e.event_id and a.attend_status = 'YES') yes_cnt, " +
            "  (select count(*) from project_member pm where pm.project_id = e.project_id and pm.role <> '3') total_cnt, " +
            "  (select count(distinct v.member_id) from event_slot s " +
            "     join slot_vote v on v.slot_id = s.slot_id where s.event_id = e.event_id) voter_cnt " +
            "from calendar_event e " +
            "where e.project_id = ? and e.start_date <= ? and e.end_date >= ? " +
            "order by e.start_date, e.event_id";

        List<CalendarEventDTO> list = new ArrayList<>();
        try (Connection con = DBUtil.getInstance();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, projectId);
            ps.setDate(2, Date.valueOf(rangeEnd));
            ps.setDate(3, Date.valueOf(rangeStart));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CalendarEventDTO dto = mapEvent(rs);
                    dto.setYesCount(rs.getInt("yes_cnt"));
                    dto.setTotalCount(rs.getInt("total_cnt"));
                    dto.setVoterCount(rs.getInt("voter_cnt"));
                    list.add(dto);
                }
            }
        }
        return list;
    }

    /** 조율 중(PENDING) 일정 목록: 왼쪽 "日程調整チャット" 영역용 */
    public List<CalendarEventDTO> findPending(String projectId) throws SQLException {
        String sql = "select e.* from calendar_event e " +
                     "where e.project_id = ? and e.status = 'PENDING' " +
                     "order by e.start_date, e.event_id";
        List<CalendarEventDTO> list = new ArrayList<>();
        try (Connection con = DBUtil.getInstance();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, projectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapEvent(rs));
            }
        }
        return list;
    }

    /* =========================================================
     * 상세 조회 (참가자 + 후보 시간 + 득표 + 내 투표 여부)
     * projectId 를 같이 받아 다른 프로젝트 일정 접근을 막는다
     * ========================================================= */
    public CalendarEventDTO findById(int eventId, String projectId, String loginId) throws SQLException {
        try (Connection con = DBUtil.getInstance()) {
            CalendarEventDTO dto = null;

            String sqlEvent = "select * from calendar_event where event_id = ? and project_id = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlEvent)) {
                ps.setInt(1, eventId);
                ps.setString(2, projectId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) dto = mapEvent(rs);
                }
            }
            if (dto == null) return null;

            // 참가자: 프로젝트 멤버 기준 (나중에 합류해 응답 행이 없는 멤버는 MAYBE)
            //         보기전용(role 3)은 참가 대상에서 제외
            String sqlAtt =
                "select pm.member_id, m.member_name, nvl(a.attend_status, 'MAYBE') attend_status " +
                "from project_member pm " +
                "join member m on m.member_id = pm.member_id " +
                "left join event_attendee a on a.event_id = ? and a.member_id = pm.member_id " +
                "where pm.project_id = ? and pm.role <> '3' " +
                "order by pm.member_id";

            try (PreparedStatement ps = con.prepareStatement(sqlAtt)) {
                ps.setInt(1, eventId);
                ps.setString(2, projectId);
                try (ResultSet rs = ps.executeQuery()) {
                    int yes = 0;
                    while (rs.next()) {
                        CalendarEventDTO.Attendee a = new CalendarEventDTO.Attendee();
                        a.setMemberId(rs.getString("member_id"));
                        a.setMemberName(rs.getString("member_name"));
                        a.setStatus(rs.getString("attend_status"));
                        if ("YES".equals(a.getStatus())) yes++;
                        dto.getAttendees().add(a);
                    }
                    dto.setTotalCount(dto.getAttendees().size());
                    dto.setYesCount(yes);
                }
            }

            // 후보 시간 + 득표
            String sqlSlot = "select s.slot_id, s.slot_time, count(v.member_id) votes, " +
                "max(case when v.member_id = ? then 1 else 0 end) voted_me " +
                "from event_slot s left join slot_vote v on v.slot_id = s.slot_id " +
                "where s.event_id = ? " +
                "group by s.slot_id, s.slot_time order by s.slot_time";

            try (PreparedStatement ps = con.prepareStatement(sqlSlot)) {
                ps.setString(1, loginId);
                ps.setInt(2, eventId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        CalendarEventDTO.Slot s = new CalendarEventDTO.Slot();
                        s.setSlotId(rs.getInt("slot_id"));
                        s.setTime(rs.getString("slot_time"));
                        s.setVotes(rs.getInt("votes"));
                        s.setVotedByMe(rs.getInt("voted_me") == 1);
                        dto.getSlots().add(s);
                    }
                }
            }
            return dto;
        }
    }

    /* =========================================================
     * 생성: 일정 + 참가자(프로젝트 전원) + 후보 시간 을 한 트랜잭션으로
     * slotTimes 는 PENDING 일 때만 사용 (null 가능)
     * 반환: 생성된 event_id
     * ========================================================= */
    public int insert(CalendarEventDTO dto, List<String> slotTimes) throws SQLException {
        try (Connection con = DBUtil.getInstance()) {
            con.setAutoCommit(false);
            try {
                int eventId;
                try (PreparedStatement ps = con.prepareStatement("select seq_cal_event.nextval from dual");
                     ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    eventId = rs.getInt(1);
                }

                String sqlEvent =
                    "insert into calendar_event " +
                    "(event_id, project_id, title, memo, icon_key, color_cls, start_date, end_date, " +
                    " status, is_deadline, confirmed_time, created_by) " +
                    "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = con.prepareStatement(sqlEvent)) {
                    ps.setInt(1, eventId);
                    ps.setString(2, dto.getProjectId());
                    ps.setString(3, dto.getTitle());
                    ps.setString(4, dto.getMemo());
                    ps.setString(5, dto.getIconKey());
                    ps.setString(6, dto.getColorCls());
                    ps.setDate(7, Date.valueOf(dto.getStartDate()));
                    ps.setDate(8, Date.valueOf(dto.getEndDate()));
                    ps.setString(9, dto.getStatus());
                    ps.setString(10, dto.isDeadline() ? "Y" : "N");
                    ps.setString(11, dto.getConfirmedTime());
                    ps.setString(12, dto.getCreatedBy());
                    ps.executeUpdate();
                }

                // 프로젝트 멤버 전원 등록 (작성자는 YES, 나머지는 MAYBE)
                String sqlAtt =
                    "insert into event_attendee (event_id, member_id, attend_status) " +
                    "select ?, member_id, case when member_id = ? then 'YES' else 'MAYBE' end " +
                    "from project_member where project_id = ?";
                try (PreparedStatement ps = con.prepareStatement(sqlAtt)) {
                    ps.setInt(1, eventId);
                    ps.setString(2, dto.getCreatedBy());
                    ps.setString(3, dto.getProjectId());
                    ps.executeUpdate();
                }

                // 후보 시간
                if (dto.isPending() && slotTimes != null && !slotTimes.isEmpty()) {
                    String sqlSlot =
                        "insert into event_slot (slot_id, event_id, slot_time, created_by) " +
                        "values (seq_event_slot.nextval, ?, ?, ?)";
                    try (PreparedStatement ps = con.prepareStatement(sqlSlot)) {
                        for (String t : slotTimes) {
                            ps.setInt(1, eventId);
                            ps.setString(2, t);
                            ps.setString(3, dto.getCreatedBy());
                            ps.addBatch();
                        }
                        ps.executeBatch();
                    }
                }

                con.commit();
                return eventId;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    /* =========================================================
     * 수정 (status / confirmed_time 은 확정 API에서만 변경)
     * 반환: 수정된 행 수 (0이면 없는 일정)
     * ========================================================= */
    public int update(CalendarEventDTO dto) throws SQLException {
        String sql =
            "update calendar_event set title = ?, memo = ?, icon_key = ?, color_cls = ?, " +
            "  start_date = ?, end_date = ?, is_deadline = ? " +
            "where event_id = ? and project_id = ?";
        try (Connection con = DBUtil.getInstance();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dto.getTitle());
            ps.setString(2, dto.getMemo());
            ps.setString(3, dto.getIconKey());
            ps.setString(4, dto.getColorCls());
            ps.setDate(5, Date.valueOf(dto.getStartDate()));
            ps.setDate(6, Date.valueOf(dto.getEndDate()));
            ps.setString(7, dto.isDeadline() ? "Y" : "N");
            ps.setInt(8, dto.getEventId());
            ps.setString(9, dto.getProjectId());
            return ps.executeUpdate();
        }
    }

    /* =========================================================
     * 삭제 (참가자/후보 시간/투표는 on delete cascade 로 같이 삭제됨)
     * ========================================================= */
    public int delete(int eventId, String projectId) throws SQLException {
        String sql = "delete from calendar_event where event_id = ? and project_id = ?";
        try (Connection con = DBUtil.getInstance();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ps.setString(2, projectId);
            return ps.executeUpdate();
        }
    }

    /* 수정/삭제 권한 체크용: 작성자 조회 (없으면 null) */
    public String getCreator(int eventId, String projectId) throws SQLException {
        String sql = "select created_by from calendar_event where event_id = ? and project_id = ?";
        try (Connection con = DBUtil.getInstance();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ps.setString(2, projectId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        }
    }

    /* =========================================================
     * 참가 여부 변경: 행이 없으면 insert, 있으면 update
     * (프로젝트에 나중에 합류한 멤버도 처리)
     * 반환: 처리된 행 수 (0 이면 이 프로젝트의 일정이 아님)
     * ========================================================= */
    public int setAttendance(int eventId, String projectId, String memberId, String status)
            throws SQLException {
        String sql =
            "merge into event_attendee a " +
            "using (select e.event_id, cast(? as varchar2(20)) member_id " +
            "       from calendar_event e where e.event_id = ? and e.project_id = ?) s " +
            "on (a.event_id = s.event_id and a.member_id = s.member_id) " +
            "when matched then update set a.attend_status = ? " +
            "when not matched then insert (event_id, member_id, attend_status) " +
            "     values (s.event_id, s.member_id, ?)";
        try (Connection con = DBUtil.getInstance();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, memberId);
            ps.setInt(2, eventId);
            ps.setString(3, projectId);
            ps.setString(4, status);
            ps.setString(5, status);
            return ps.executeUpdate();
        }
    }

    /* =========================================================
     * 후보 시간 투표 토글 (투표 중이면 취소, 아니면 투표)
     * 반환: 1 = 투표함, 0 = 투표 취소,
     *      -1 = 투표 불가 (없는 후보 / 조율 중이 아님 / 다른 프로젝트)
     * ========================================================= */
    public int toggleVote(int eventId, int slotId, String projectId, String memberId)
            throws SQLException {
        try (Connection con = DBUtil.getInstance()) {
            con.setAutoCommit(false);
            try {
                String chk =
                    "select count(*) from event_slot s " +
                    "join calendar_event e on e.event_id = s.event_id " +
                    "where s.slot_id = ? and s.event_id = ? and e.project_id = ? and e.status = 'PENDING'";
                try (PreparedStatement ps = con.prepareStatement(chk)) {
                    ps.setInt(1, slotId);
                    ps.setInt(2, eventId);
                    ps.setString(3, projectId);
                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        if (rs.getInt(1) == 0) { con.rollback(); return -1; }
                    }
                }

                int deleted;
                try (PreparedStatement ps = con.prepareStatement(
                        "delete from slot_vote where slot_id = ? and member_id = ?")) {
                    ps.setInt(1, slotId);
                    ps.setString(2, memberId);
                    deleted = ps.executeUpdate();
                }

                int result = 0;
                if (deleted == 0) {
                    try (PreparedStatement ps = con.prepareStatement(
                            "insert into slot_vote (slot_id, member_id) values (?, ?)")) {
                        ps.setInt(1, slotId);
                        ps.setString(2, memberId);
                        ps.executeUpdate();
                    } catch (SQLException e) {
                        if (e.getErrorCode() != 1) throw e;   // ORA-00001: 동시 클릭으로 이미 투표됨
                    }
                    result = 1;
                }
                con.commit();
                return result;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    /* =========================================================
     * 후보 시간 추가
     * 반환: 1 = 추가, 0 = 없는 일정 / 조율 중이 아님,
     *      -1 = 이미 있는 시간, -2 = 최대 10개 초과
     * ========================================================= */
    public int addSlot(int eventId, String projectId, String time, String memberId)
            throws SQLException {
        try (Connection con = DBUtil.getInstance()) {
            String chk =
                "select (select count(*) from event_slot where event_id = e.event_id) cnt, " +
                "       (select count(*) from event_slot where event_id = e.event_id and slot_time = ?) dup " +
                "from calendar_event e " +
                "where e.event_id = ? and e.project_id = ? and e.status = 'PENDING'";
            try (PreparedStatement ps = con.prepareStatement(chk)) {
                ps.setString(1, time);
                ps.setInt(2, eventId);
                ps.setString(3, projectId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return 0;
                    if (rs.getInt("dup") > 0) return -1;
                    if (rs.getInt("cnt") >= 10) return -2;
                }
            }

            String ins = "insert into event_slot (slot_id, event_id, slot_time, created_by) " +
                         "values (seq_event_slot.nextval, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(ins)) {
                ps.setInt(1, eventId);
                ps.setString(2, time);
                ps.setString(3, memberId);
                ps.executeUpdate();
                return 1;
            } catch (SQLException e) {
                if (e.getErrorCode() == 1) return -1;       // 동시 추가로 중복
                throw e;
            }
        }
    }

    /* =========================================================
     * 확정: PENDING → CONFIRMED, 선택한 시간 저장, 후보/투표 삭제
     * 반환: 1 = 확정, 0 = 확정 불가
     *      (없는 일정 / 이미 확정 / 후보에 없는 시간)
     * 리더 권한 검증은 Command 에서 한다
     * ========================================================= */
    public int confirm(int eventId, String projectId, String time) throws SQLException {
        try (Connection con = DBUtil.getInstance()) {
            con.setAutoCommit(false);
            try {
                String upd =
                    "update calendar_event e set e.status = 'CONFIRMED', e.confirmed_time = ? " +
                    "where e.event_id = ? and e.project_id = ? and e.status = 'PENDING' " +
                    "and exists (select 1 from event_slot s where s.event_id = e.event_id and s.slot_time = ?)";
                int n;
                try (PreparedStatement ps = con.prepareStatement(upd)) {
                    ps.setString(1, time);
                    ps.setInt(2, eventId);
                    ps.setString(3, projectId);
                    ps.setString(4, time);
                    n = ps.executeUpdate();
                }
                if (n == 1) {
                    // slot_vote 는 on delete cascade 로 같이 삭제됨
                    try (PreparedStatement ps = con.prepareStatement(
                            "delete from event_slot where event_id = ?")) {
                        ps.setInt(1, eventId);
                        ps.executeUpdate();
                    }
                }
                con.commit();
                return n;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    /* ---- ResultSet → DTO (공통) ---- */
    private CalendarEventDTO mapEvent(ResultSet rs) throws SQLException {
        CalendarEventDTO dto = new CalendarEventDTO();
        dto.setEventId(rs.getInt("event_id"));
        dto.setProjectId(rs.getString("project_id"));
        dto.setTitle(rs.getString("title"));
        dto.setMemo(rs.getString("memo"));
        dto.setIconKey(rs.getString("icon_key"));
        dto.setColorCls(rs.getString("color_cls"));
        dto.setStartDate(rs.getDate("start_date").toLocalDate());
        dto.setEndDate(rs.getDate("end_date").toLocalDate());
        dto.setStatus(rs.getString("status"));
        dto.setDeadline("Y".equals(rs.getString("is_deadline")));
        dto.setConfirmedTime(rs.getString("confirmed_time"));
        dto.setCreatedBy(rs.getString("created_by"));
        return dto;
    }
}
