package service;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.CalendarDAO;
import model.CalendarEventDTO;
import model.MemberDTO;

/**
 * 캘린더 일정 API (JSON 응답)
 *
 * action 목록
 *  list    GET   표시 범위 일정 (start, end)          역할 1,2,3
 *  pending GET   조율 중 일정 목록                    역할 1,2,3
 *  detail  GET   일정 상세 (id)                       역할 1,2,3
 *  create  POST  생성 (JSON body)                     역할 1,2
 *  update  POST  수정 (JSON body)                     역할 1 / 2(본인 일정)
 *  delete  POST  삭제 (JSON body {eventId})           역할 1 / 2(본인 일정)
 *  members GET   프로젝트 멤버 + 내 역할               역할 1,2,3
 *  attend  POST  내 참가 여부 {eventId, status}        역할 1,2
 *  vote    POST  후보 시간 투표 토글 {eventId, slotId} 역할 1,2
 *  addSlot POST  후보 시간 추가 {eventId, time}        역할 1,2
 *  confirm POST  시간 확정 {eventId, time}             역할 1 (리더만)
 *
 * 모든 요청에 projectId 필요. 서버가 project_member 로 소속/역할을 검증한다.
 */
public class CalendarEventCommand implements Command {

    private static final Gson GSON = new Gson();
    private final CalendarDAO dao = new CalendarDAO();

    private static final Set<String> COLORS = Set.of("blue", "green", "purple", "red");
    // JS에서 { EVENT:'📅', MEETING:'📋', IDEA:'💡', DEADLINE:'🚨', DESIGN:'🎨', DISCUSS:'🤝', REPORT:'📊', TEAM:'👥' } 로 변환
    private static final Set<String> ICONS =
            Set.of("EVENT", "MEETING", "IDEA", "DEADLINE", "DESIGN", "DISCUSS", "REPORT", "TEAM");
    private static final Set<String> ATTEND_STATUS = Set.of("YES", "MAYBE", "NO");
    private static final Pattern TIME = Pattern.compile("^([01]\\d|2[0-3]):[0-5]\\d$");

    @Override
    public boolean doCommand(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        handle(request, response);
        return false;   // JSON 을 직접 응답했으므로 FrontController 는 forward/redirect 하지 않음
    }

    private void handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

        // ---- 1. 로그인 ----
        String loginId = getLoginId(request);
        if (loginId == null) { error(response, 401, "ログインが必要です"); return; }

        // ---- 2. 프로젝트 소속 / 역할 ----
        String projectId = request.getParameter("projectId");
        if (isBlank(projectId)) { error(response, 400, "projectId が必要です"); return; }

        String action = request.getParameter("action");
        if (isBlank(action)) action = "list";

        try {
            String role = dao.getRole(projectId, loginId);
            if (role == null) { error(response, 403, "このプロジェクトのメンバーではありません"); return; }

            boolean isPost = "POST".equalsIgnoreCase(request.getMethod());

            switch (action) {
                case "list":    doList(request, response, projectId); break;
                case "pending": doPending(response, projectId); break;
                case "members": doMembers(response, projectId, loginId, role); break;
                case "detail":  doDetail(request, response, projectId, loginId, role); break;
                case "create":
                    if (!isPost) { error(response, 405, "POST only"); return; }
                    doCreate(request, response, projectId, loginId, role);
                    break;
                case "update":
                    if (!isPost) { error(response, 405, "POST only"); return; }
                    doUpdate(request, response, projectId, loginId, role);
                    break;
                case "delete":
                    if (!isPost) { error(response, 405, "POST only"); return; }
                    doDelete(request, response, projectId, loginId, role);
                    break;
                case "attend":
                    if (!isPost) { error(response, 405, "POST only"); return; }
                    doAttend(request, response, projectId, loginId, role);
                    break;
                case "vote":
                    if (!isPost) { error(response, 405, "POST only"); return; }
                    doVote(request, response, projectId, loginId, role);
                    break;
                case "addSlot":
                    if (!isPost) { error(response, 405, "POST only"); return; }
                    doAddSlot(request, response, projectId, loginId, role);
                    break;
                case "confirm":
                    if (!isPost) { error(response, 405, "POST only"); return; }
                    doConfirm(request, response, projectId, loginId, role);
                    break;
                default:
                    error(response, 400, "unknown action");
            }
        } catch (IllegalArgumentException e) {
            error(response, 400, e.getMessage());          // 입력값 검증 실패
        } catch (SQLException e) {
            e.printStackTrace();
            error(response, 500, "サーバーエラーが発生しました");
        }
    }

    /* ================= 조회 ================= */

    private void doList(HttpServletRequest req, HttpServletResponse resp, String projectId)
            throws SQLException, IOException {
        // FullCalendar 의 startStr/endStr 는 'YYYY-MM-DD' 또는 'YYYY-MM-DDTHH:mm...' 이므로 앞 10자만 사용
        LocalDate start = parseDate(req.getParameter("start"));
        LocalDate endExclusive = parseDate(req.getParameter("end"));
        LocalDate endInclusive = endExclusive.minusDays(1);

        JsonArray arr = new JsonArray();
        for (CalendarEventDTO e : dao.findByRange(projectId, start, endInclusive)) {
            arr.add(toListJson(e));
        }
        write(resp, 200, arr);
    }

    private void doPending(HttpServletResponse resp, String projectId) throws SQLException, IOException {
        JsonArray arr = new JsonArray();
        for (CalendarEventDTO e : dao.findPending(projectId)) {
            arr.add(toListJson(e));
        }
        write(resp, 200, arr);
    }

    /** 팀 멤버 목록 + 내 아이디/역할 (화면의 멤버 표시, 보기전용 버튼 비활성화용) */
    private void doMembers(HttpServletResponse resp, String projectId, String loginId, String role)
            throws SQLException, IOException {
        JsonArray arr = new JsonArray();
        for (String[] m : dao.findMembers(projectId)) {
            JsonObject o = new JsonObject();
            o.addProperty("id", m[0]);
            o.addProperty("name", m[1]);
            o.addProperty("role", m[2]);
            arr.add(o);
        }
        JsonObject res = new JsonObject();
        res.addProperty("myId", loginId);
        res.addProperty("myRole", role);
        res.add("members", arr);
        write(resp, 200, res);
    }

    private void doDetail(HttpServletRequest req, HttpServletResponse resp,
                          String projectId, String loginId, String role) throws SQLException, IOException {
        int id = parseInt(req.getParameter("id"), "id");
        CalendarEventDTO e = dao.findById(id, projectId, loginId);
        if (e == null) { error(resp, 404, "予定が見つかりません"); return; }

        JsonObject o = toListJson(e);
        o.addProperty("memo", e.getMemo() == null ? "" : e.getMemo());
        o.addProperty("confirmedTime", e.getConfirmedTime());
        o.addProperty("startDate", e.getStartDate().toString());   // 포함 (수정 폼용)
        o.addProperty("endDate", e.getEndDate().toString());       // 포함
        o.add("attendees", GSON.toJsonTree(e.getAttendees()));
        o.add("slots", GSON.toJsonTree(e.getSlots()));

        // 화면의 버튼 표시용 (최종 판단은 항상 서버에서 다시 검증)
        o.addProperty("myId", loginId);
        o.addProperty("myRole", role);
        o.addProperty("canEdit", canModify(role, loginId, e.getCreatedBy()));
        o.addProperty("canConfirm", CalendarEventDTO.ROLE_LEADER.equals(role));
        o.addProperty("canParticipate", !CalendarEventDTO.ROLE_VIEWER.equals(role));
        write(resp, 200, o);
    }

    /* ================= 생성 / 수정 / 삭제 ================= */

    private void doCreate(HttpServletRequest req, HttpServletResponse resp,
                          String projectId, String loginId, String role) throws SQLException, IOException {
        if (CalendarEventDTO.ROLE_VIEWER.equals(role)) { error(resp, 403, "権限がありません"); return; }

        JsonObject b = readBody(req);
        CalendarEventDTO dto = readEvent(b, projectId);
        dto.setCreatedBy(loginId);

        List<String> slots = null;
        if (dto.isPending()) {
            slots = readSlots(b);
        } else {
            dto.setConfirmedTime(readOptionalTime(b, "confirmedTime"));
        }

        int newId = dao.insert(dto, slots);
        JsonObject o = new JsonObject();
        o.addProperty("ok", true);
        o.addProperty("eventId", newId);
        write(resp, 200, o);
    }

    private void doUpdate(HttpServletRequest req, HttpServletResponse resp,
                          String projectId, String loginId, String role) throws SQLException, IOException {
        JsonObject b = readBody(req);
        int id = parseInt(str(b, "eventId"), "eventId");

        String creator = dao.getCreator(id, projectId);
        if (creator == null) { error(resp, 404, "予定が見つかりません"); return; }
        if (!canModify(role, loginId, creator)) { error(resp, 403, "権限がありません"); return; }

        CalendarEventDTO dto = readEvent(b, projectId);
        dto.setEventId(id);
        int n = dao.update(dto);
        if (n == 0) { error(resp, 404, "予定が見つかりません"); return; }

        JsonObject o = new JsonObject();
        o.addProperty("ok", true);
        write(resp, 200, o);
    }

    private void doDelete(HttpServletRequest req, HttpServletResponse resp,
                          String projectId, String loginId, String role) throws SQLException, IOException {
        JsonObject b = readBody(req);
        int id = parseInt(str(b, "eventId"), "eventId");

        String creator = dao.getCreator(id, projectId);
        if (creator == null) { error(resp, 404, "予定が見つかりません"); return; }
        if (!canModify(role, loginId, creator)) { error(resp, 403, "権限がありません"); return; }

        dao.delete(id, projectId);
        JsonObject o = new JsonObject();
        o.addProperty("ok", true);
        write(resp, 200, o);
    }

    /* ================= 참가 / 투표 / 후보 추가 / 확정 ================= */

    /** 내 참가 여부 변경 — 리더(1), 팀원(2) */
    private void doAttend(HttpServletRequest req, HttpServletResponse resp,
                          String projectId, String loginId, String role) throws SQLException, IOException {
        if (CalendarEventDTO.ROLE_VIEWER.equals(role)) { error(resp, 403, "権限がありません"); return; }

        JsonObject b = readBody(req);
        int id = parseInt(str(b, "eventId"), "eventId");
        String st = str(b, "status");
        if (st == null || !ATTEND_STATUS.contains(st)) throw new IllegalArgumentException("参加状況が正しくありません");

        if (dao.setAttendance(id, projectId, loginId, st) == 0) { error(resp, 404, "予定が見つかりません"); return; }
        ok(resp);
    }

    /** 후보 시간 투표 토글 — 리더(1), 팀원(2) */
    private void doVote(HttpServletRequest req, HttpServletResponse resp,
                        String projectId, String loginId, String role) throws SQLException, IOException {
        if (CalendarEventDTO.ROLE_VIEWER.equals(role)) { error(resp, 403, "権限がありません"); return; }

        JsonObject b = readBody(req);
        int id = parseInt(str(b, "eventId"), "eventId");
        int slotId = parseInt(str(b, "slotId"), "slotId");

        int r = dao.toggleVote(id, slotId, projectId, loginId);
        if (r < 0) { error(resp, 409, "投票できません（調整が終了したか、候補が存在しません）"); return; }

        JsonObject o = new JsonObject();
        o.addProperty("ok", true);
        o.addProperty("voted", r == 1);
        write(resp, 200, o);
    }

    /** 후보 시간 추가 — 리더(1), 팀원(2) */
    private void doAddSlot(HttpServletRequest req, HttpServletResponse resp,
                           String projectId, String loginId, String role) throws SQLException, IOException {
        if (CalendarEventDTO.ROLE_VIEWER.equals(role)) { error(resp, 403, "権限がありません"); return; }

        JsonObject b = readBody(req);
        int id = parseInt(str(b, "eventId"), "eventId");
        String t = readOptionalTime(b, "time");
        if (t == null) throw new IllegalArgumentException("時間を入力してください");

        int r = dao.addSlot(id, projectId, t, loginId);
        if (r == 0)  { error(resp, 409, "候補を追加できません（調整中の予定ではありません）"); return; }
        if (r == -1) { error(resp, 409, "すでに同じ候補時間があります"); return; }
        if (r == -2) { error(resp, 409, "候補時間は最大10個までです"); return; }
        ok(resp);
    }

    /** 일정 확정 — 리더(1)만. 반드시 서버에서 검증 */
    private void doConfirm(HttpServletRequest req, HttpServletResponse resp,
                           String projectId, String loginId, String role) throws SQLException, IOException {
        if (!CalendarEventDTO.ROLE_LEADER.equals(role)) { error(resp, 403, "リーダーのみ確定できます"); return; }

        JsonObject b = readBody(req);
        int id = parseInt(str(b, "eventId"), "eventId");
        String t = readOptionalTime(b, "time");
        if (t == null) throw new IllegalArgumentException("確定する時間を選択してください");

        if (dao.confirm(id, projectId, t) == 0) {
            error(resp, 409, "確定できません（調整中ではない、または候補にない時間です）");
            return;
        }
        ok(resp);
    }

    /* ================= 권한 ================= */

    /** 수정/삭제: 리더(1)는 전부, 팀원(2)은 본인 일정만, 보기전용(3)은 불가 */
    private boolean canModify(String role, String loginId, String creatorId) {
        if (CalendarEventDTO.ROLE_LEADER.equals(role)) return true;
        return CalendarEventDTO.ROLE_MEMBER.equals(role) && loginId.equals(creatorId);
    }

    /* ================= 입력 파싱 / 검증 ================= */

    /** body JSON → DTO (status 는 생성 시에만 의미 있음, 수정 시 DAO 가 무시) */
    private CalendarEventDTO readEvent(JsonObject b, String projectId) {
        CalendarEventDTO dto = new CalendarEventDTO();
        dto.setProjectId(projectId);

        String title = str(b, "title");
        if (isBlank(title)) throw new IllegalArgumentException("タイトルを入力してください");
        title = title.trim();
        if (title.length() > 100) throw new IllegalArgumentException("タイトルは100文字以内です");
        dto.setTitle(title);

        String memo = str(b, "memo");
        if (memo != null && memo.length() > 2000) throw new IllegalArgumentException("メモは2000文字以内です");
        dto.setMemo(memo == null ? "" : memo);

        String icon = str(b, "iconKey");
        dto.setIconKey(icon != null && ICONS.contains(icon) ? icon : "EVENT");

        String color = str(b, "colorCls");
        dto.setColorCls(color != null && COLORS.contains(color) ? color : "blue");

        LocalDate s = parseDate(str(b, "startDate"));
        LocalDate e = isBlank(str(b, "endDate")) ? s : parseDate(str(b, "endDate"));
        if (e.isBefore(s)) throw new IllegalArgumentException("終了日は開始日以降にしてください");
        dto.setStartDate(s);
        dto.setEndDate(e);

        String status = str(b, "status");
        dto.setStatus("PENDING".equals(status) ? "PENDING" : "CONFIRMED");

        dto.setDeadline(b.has("deadline") && !b.get("deadline").isJsonNull() && b.get("deadline").getAsBoolean());
        return dto;
    }

    private List<String> readSlots(JsonObject b) {
        Set<String> set = new LinkedHashSet<>();   // 중복 제거 (uq_es_event_time 보호)
        if (b.has("slots") && b.get("slots").isJsonArray()) {
            for (JsonElement el : b.getAsJsonArray("slots")) {
                String t = el.getAsString().trim();
                if (!TIME.matcher(t).matches()) throw new IllegalArgumentException("時間は「HH:MM」形式で入力してください");
                set.add(t);
            }
        }
        if (set.size() > 10) throw new IllegalArgumentException("候補時間は最大10個までです");
        return new ArrayList<>(set);
    }

    private String readOptionalTime(JsonObject b, String key) {
        String t = str(b, key);
        if (isBlank(t)) return null;
        t = t.trim();
        if (!TIME.matcher(t).matches()) throw new IllegalArgumentException("時間は「HH:MM」形式で入力してください");
        return t;
    }

    private JsonObject readBody(HttpServletRequest req) throws IOException {
        try {
            JsonElement el = JsonParser.parseReader(req.getReader());   // Gson 2.8.6+
            if (el == null || !el.isJsonObject()) throw new IllegalArgumentException("リクエスト形式が正しくありません");
            return el.getAsJsonObject();
        } catch (JsonParseException ex) {
            throw new IllegalArgumentException("リクエスト形式が正しくありません");
        }
    }

    private LocalDate parseDate(String s) {
        if (isBlank(s) || s.length() < 10) throw new IllegalArgumentException("日付の形式が正しくありません");
        try {
            return LocalDate.parse(s.substring(0, 10));
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("日付の形式が正しくありません");
        }
    }

    private int parseInt(String s, String name) {
        try {
            return Integer.parseInt(s);
        } catch (Exception ex) {
            throw new IllegalArgumentException(name + " が正しくありません");
        }
    }

    private String str(JsonObject b, String key) {
        return b.has(key) && !b.get(key).isJsonNull() ? b.get(key).getAsString() : null;
    }

    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }

    /* ================= JSON 변환 ================= */

    /** FullCalendar 이벤트로 바로 쓸 수 있는 형태 (end 는 미포함 = +1일) */
    private JsonObject toListJson(CalendarEventDTO e) {
        JsonObject o = new JsonObject();
        o.addProperty("id", String.valueOf(e.getEventId()));
        o.addProperty("title", e.getTitle());
        o.addProperty("start", e.getStartDate().toString());
        o.addProperty("end", e.getEndDate().plusDays(1).toString());
        o.addProperty("iconKey", e.getIconKey());
        o.addProperty("colorCls", e.getColorCls());
        o.addProperty("status", e.getStatus());
        o.addProperty("deadline", e.isDeadline());
        o.addProperty("yesCount", e.getYesCount());
        o.addProperty("totalCount", e.getTotalCount());
        o.addProperty("voterCount", e.getVoterCount());
        o.addProperty("createdBy", e.getCreatedBy());
        return o;
    }

    /* ================= 공통 응답 ================= */

    private String getLoginId(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return null;
        Object m = session.getAttribute("loginMember");
        return (m instanceof MemberDTO) ? ((MemberDTO) m).getMemberId() : null;
    }

    private void ok(HttpServletResponse resp) throws IOException {
        JsonObject o = new JsonObject();
        o.addProperty("ok", true);
        write(resp, 200, o);
    }

    private void error(HttpServletResponse resp, int status, String msg) throws IOException {
        JsonObject o = new JsonObject();
        o.addProperty("error", msg);
        write(resp, status, o);
    }

    private void write(HttpServletResponse resp, int status, JsonElement body) throws IOException {
        resp.setStatus(status);
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(GSON.toJson(body));
    }
}
