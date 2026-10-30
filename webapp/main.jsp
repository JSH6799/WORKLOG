<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%-- JSTL 태그라이브러리 선언 (Tomcat 10+ : jakarta.tags.core) --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<%!
    // XSS / 문자열 깨짐 방지용 이스케이프 헬퍼
    private static String escHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
    private static String escJs(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("'", "\\'").replace("\"", "\\\"")
                .replace("<", "\\x3C").replace("\n", "\\n").replace("\r", "");
    }

    // D-day 계산 (프로젝트별)
    private static String stageOf(long r) {
        if (r < 0)   return "over";
        if (r == 0)  return "today";
        if (r <= 7)  return "danger";
        if (r <= 14) return "warn";
        return "safe";
    }
    private static String ddayOf(long r) {
        if (r < 0)  return "D+" + (-r);
        if (r == 0) return "D-DAY";
        return "D-" + r;
    }
%>

<%
    // Controller / Servlet 에서 request.setAttribute로 넘겨주는 데이터 수신
    String userName = (String) request.getAttribute("userName");
    if (userName == null) userName = "geust";

    Integer unreadPostCount = (Integer) request.getAttribute("unreadPostCount");
    if (unreadPostCount == null) unreadPostCount = 4;

    java.time.LocalDate today = java.time.LocalDate.now(java.time.ZoneId.of("Asia/Tokyo"));

    String ctx = request.getContextPath();
%>
<%-- 서블릿/필터가 "projects" 또는 "projectList" 어느 이름으로 넘겨도 동작하도록 통일 --%>
<c:set var="proList" value="${empty proList ? projects : proList}" />

<!-- 공통 헤더 (header.jsp 안에서 <html>, <head>, <link css>, <header GNB>가 이미 출력됨) -->
<%@ include file="/include/header.jsp"%>

<%-- 가능하면 이 <link>는 header.jsp의 <head> 안으로 옮기는 것을 권장 --%>
<link rel="stylesheet" href="<%= ctx %>/css/main.css">

<!-- Main Container -->
<main class="main-container">

    <div class="page-title-section">
        <div>
            <h1 class="page-title">ダッシュボード</h1>
        </div>
    </div>

    <section class="summary-banner-single">
        <div class="summary-card" style="width: 100%;">
            <div class="summary-info">
                <span class="badge badge-unread">未確認</span>
                <span class="summary-label">未確認投稿</span>
                <span class="summary-count"><%= unreadPostCount %>件</span>
            </div>
            <a href="<%= ctx %>/member/mypage.jsp" class="btn-sm-outline">未確認投稿一覧</a>
        </div>
    </section>
    
    <div class="dashboard-grid">

        <!-- Left -->
        <div class="left-content">

            <!-- Widget: 現在参加中のプロジェクト -->
            <div class="widget-card">
                <div class="widget-header">
                    <span class="widget-title">📊 現在参加中のプロジェクト</span>
                    <a href="${pageContext.request.contextPath}/project/projectList" class="widget-action">プロジェクト一覧へ ›</a>
                </div>

                <table class="project-table">
                    <thead>
					    <tr>
					        <th style="text-align: left;">プロジェクト名</th>
					        <th style="text-align: left; width: 120px;">D-Day</th>
					        <th style="text-align: right; width: 110px;">DL</th>
					    </tr>
					</thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty proList}">
                                <c:forEach var="p" items="${proList}">
                                    <c:set var="endStr" value="${p.endDate}" />
                                    <%
                                        // 프로젝트별 D-day 계산 (endDate.toString() 이 yyyy-MM-dd 로 시작한다고 가정:
                                        // LocalDate / java.sql.Date / "yyyy-MM-dd" 문자열 OK)
                                        String rowStage = "none", rowDday = "-";
                                        try {
                                            String es = String.valueOf(pageContext.getAttribute("endStr"));
                                            long rd = java.time.temporal.ChronoUnit.DAYS.between(
                                                    today, java.time.LocalDate.parse(es.substring(0, 10)));
                                            rowStage = stageOf(rd);
                                            rowDday  = ddayOf(rd);
                                        } catch (Exception ignore) { }
                                    %>
                                    <tr class="project-row stage-<%= rowStage %>"
									    onclick="location.href='${pageContext.request.contextPath}/project/project.jsp?projectId=${p.projectId}'">
									    <td style="font-weight: 600; color: #212529;">
									        <a class="row-link" href="${pageContext.request.contextPath}/project/project.jsp?projectId=${p.projectId}"
									           onclick="event.stopPropagation();"><c:out value="${p.projectName}" /></a>
									    </td>
									    <td><span class="dday-chip"><%= rowDday %></span></td>
									    <td style="text-align: right; color: #6c757d; font-size: 12px;"><c:out value="${p.endDate}" /></td>
									</tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="4" style="padding: 32px; text-align: center; color: #adb5bd;">
                                        現在参加中のプロジェクトはありません。
                                    </td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>


		<!-- Widget: Recent Posts -->
		<div class="widget-card">
		    <div class="widget-header">
		        <span class="widget-title">📝 最近のプロジェクト投稿</span>
		        <a href="<%= ctx %>/board/list.jsp" class="widget-action">投稿一覧へ ›</a>
		    </div>
		    <table class="recent-posts-table">
		        <thead>
		            <tr>
		                <th class="col-status">状態</th>
		                <th class="col-title">タイトル</th>
		                <th class="col-writer">投稿者</th>
		                <th class="col-date">日付</th>
		            </tr>
		        </thead>
		        <tbody>
		            <%-- recentPosts 데이터가 비어있지 않은 경우 반복 출력 --%>
		            <c:choose>
		                <c:when test="${not empty recentPosts}">
		                    <c:forEach var="post" items="${recentPosts}">
		                        <tr>
		                            <%-- 상태 (예: 읽음/안읽음 등에 따라 동적으로 바뀔 수 있음) --%>
		                            <td>
		                                <span class="badge ${post.unread ? 'badge-unread' : ''}">
		                                    ${post.checkRequired}
		                                </span>
		                            </td>
		                            <%-- 제목 및 상세페이지 링크 (id 값 바인딩) --%>
		                            <td>
		                                <a href="<%= ctx %>/board/post_detail.jsp?id=${post.id}" class="post-link">
		                                    ${post.title}
		                                </a>
		                            </td>
		                            <%-- 작성자 --%>
		                            <td>${post.writer}</td>
		                            <%-- 날짜 (필요에 따라 날짜 포맷 적용) --%>
		                            <td>${post.writeDate}</td>
		                        </tr>
		                    </c:forEach>
		                </c:when>
		                <c:otherwise>
		                    <%-- 게시물이 없을 경우의 대체 메시지 --%>
		                    <tr>
		                        <td colspan="4" style="text-align: center; color: #888;">登録された投稿がありません。</td>
		                    </tr>
		                </c:otherwise>
		            </c:choose>
		        </tbody>
		    </table>
		</div>

        </div>

        <!-- Right -->
        <div class="right-content">

            <div class="widget-card">
                <div class="widget-header">
                    <span class="widget-title">⚡ クイックアクション</span>
                </div>
                <div class="quick-actions-grid">
                    <button type="button" class="quick-btn" onclick="location.href='<%= ctx %>/board/write.jsp'">
                        <span class="icon">✏️</span>
                        <span>投稿作成</span>
                    </button>
                    <button type="button" class="quick-btn" onclick="openModal('joinModal')">
                        <span class="icon">🚪</span>
                        <span>プロジェクト参加</span>
                    </button>
                    <button type="button" class="quick-btn" onclick="openModal('inviteModal')">
                        <span class="icon">👥</span>
                        <span>メンバー招待</span>
                    </button>
                </div>
            </div>

            <div class="widget-card">
                <div class="widget-header">
                    <span class="widget-title">📌 クイックメモ</span>
                    <div style="display: flex; gap: 6px;">
                        <button type="button" class="btn-sm-outline btn-xs" onclick="clearMemo()">クリア</button>
                        <button type="button" class="btn-sm-outline btn-xs" onclick="saveMemo()">保存</button>
                    </div>
                </div>
                <div class="memo-box">
                    <textarea id="dashboardMemo" class="memo-textarea" placeholder="メモ" rows="4"></textarea>
                </div>
            </div>

            <div class="widget-card">
                <div class="widget-header">
                    <%-- 접속 중 3명 / 전체 4명. 실제로는 DB 값으로 계산 권장 --%>
                    <span class="widget-title">👥 チームメンバー (3/4)</span>
                </div>
                <ul class="member-list">
                    <li class="member-item">
                        <div class="member-info">
                            <span class="status-dot online"></span>
                            <span><%= escHtml(userName) %> (自分)</span>
                        </div>
                        <span class="role-text">開発</span>
                    </li>
                    <li class="member-item">
                        <div class="member-info">
                            <span class="status-dot online"></span>
                            <span>山田二郎</span>
                        </div>
                        <span class="role-text">デザイン</span>
                    </li>
                    <li class="member-item">
                        <div class="member-info">
                            <span class="status-dot online"></span>
                            <span>山田三郎</span>
                        </div>
                        <span class="role-text">PL</span>
                    </li>
                    <li class="member-item">
                        <div class="member-info">
                            <span class="status-dot"></span>
                            <span>山田四郎</span>
                        </div>
                        <span class="role-text offline">オフライン</span>
                    </li>
                </ul>
            </div>

        </div>

    </div>

</main>

<!-- プロジェクト参加モーダル -->
<div id="joinModal" class="modal-overlay" aria-hidden="true">
    <div class="modal-box" role="dialog" aria-modal="true" aria-labelledby="joinModalTitle">
        <div class="modal-header">
            <h2 id="joinModalTitle">🚪 プロジェクト参加</h2>
            <button type="button" class="modal-close" onclick="closeModal('joinModal')" aria-label="閉じる">&times;</button>
        </div>
        <form id="joinForm" action="<%= ctx %>/project_invite/join" method="post">
            <div class="modal-body">
                <div class="form-group">
                    <label for="projectLink">プロジェクトリンク または 招待コード</label>
                    <input type="text" id="projectLink" name="projectLink"
                           placeholder="リンク または WL-XXXXXX" required>
                    <small id="joinError" style="color:#e03131; display:none;">
                        正しいリンクまたは招待コードを入力してください。
                    </small>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn-sm-outline" onclick="closeModal('joinModal')">キャンセル</button>
                <button type="submit" class="btn-primary-sm">参加</button>
            </div>
        </form>
    </div>
</div>

<!-- メンバー招待モーダル -->
<div id="inviteModal" class="modal-overlay" aria-hidden="true">
    <div class="modal-box" role="dialog" aria-modal="true" aria-labelledby="inviteModalTitle">
        <div class="modal-header">
            <h2 id="inviteModalTitle">👥 メンバー招待</h2>
            <button type="button" class="modal-close" onclick="closeModal('inviteModal')" aria-label="閉じる">&times;</button>
        </div>
        <div class="modal-body">
            <div class="form-group">
                <label for="inviteProject">招待するプロジェクト</label>
                <select id="inviteProject">
                    <c:forEach var="p" items="${proList}">
                        <option value="${p.projectId}"><c:out value="${p.projectName}" /></option>
                    </c:forEach>
                </select>
            </div>
            <div class="form-group">
                <label for="inviteCode">招待コード</label>
                <div style="display:flex; gap:6px;">
                    <input type="text" id="inviteCode" readonly placeholder="「コード生成」を押してください" style="flex:1;">
                    <button type="button" class="btn-sm-outline" id="copyCodeBtn" onclick="copyInviteCode()" disabled>コピー</button>
                </div>
                <small id="inviteMsg" style="color:#6c757d;"></small>
            </div>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn-sm-outline" onclick="closeModal('inviteModal')">閉じる</button>
            <button type="button" class="btn-primary-sm" id="genCodeBtn" onclick="generateInviteCode()">コード生成</button>
        </div>
    </div>
</div>

<script>
    // 사용자명을 JS 문자열에 넣을 때 escJs 사용 (이름에 ' 가 있으면 스크립트 전체가 깨짐)
    const MEMO_KEY = "dashboard_memo_<%= escJs(userName) %>";

    // ===== クイックメモ =====
    document.addEventListener("DOMContentLoaded", function() {
        const savedMemo = localStorage.getItem(MEMO_KEY);
        if (savedMemo) {
            document.getElementById("dashboardMemo").value = savedMemo;
        }
    });

    function saveMemo() {
        localStorage.setItem(MEMO_KEY, document.getElementById("dashboardMemo").value);
        alert("メモを保存しました。");
    }

    function clearMemo() {
        const memo = document.getElementById("dashboardMemo");
        if (memo.value === "") return;
        if (!confirm("メモの内容をすべて削除しますか?")) return;
        memo.value = "";
        localStorage.setItem(MEMO_KEY, "");
        memo.focus();
    }

    // ===== 공통 모달 =====
    function openModal(id) {
        const m = document.getElementById(id);
        m.classList.add("open");
        m.setAttribute("aria-hidden", "false");
    }
    function closeModal(id) {
        const m = document.getElementById(id);
        m.classList.remove("open");
        m.setAttribute("aria-hidden", "true");
    }
    document.querySelectorAll(".modal-overlay").forEach(function(m) {
        m.addEventListener("click", function(e) {
            if (e.target === m) closeModal(m.id);
        });
    });
    document.addEventListener("keydown", function(e) {
        if (e.key !== "Escape") return;
        document.querySelectorAll(".modal-overlay.open").forEach(function(m) {
            closeModal(m.id);
        });
    });

    // ===== プロジェクト参加: 링크 / 초대코드 검증 =====
    document.getElementById("joinForm").addEventListener("submit", function(e) {
        const err = document.getElementById("joinError");
        const v = document.getElementById("projectLink").value.trim();
        const isCode = /^WL-[A-Z0-9]+$/i.test(v);
        const isLink = v.indexOf("projectId=") !== -1;
        if (!isCode && !isLink) {
            e.preventDefault();
            err.style.display = "block";
        } else {
            err.style.display = "none";
        }
    });

    // ===== メンバー招待: コード生成 / コピー =====
    function generateInviteCode() {
        const projectId = document.getElementById("inviteProject").value;
        const msg = document.getElementById("inviteMsg");
        if (!projectId) {
            msg.textContent = "プロジェクトを選択してください。";
            return;
        }

        fetch("<%= ctx %>/project_invite/create", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: "projectId=" + encodeURIComponent(projectId)
        })
        .then(function(r) {
            if (!r.ok) throw new Error();
            return r.json();
        })
        .then(function(data) {
            document.getElementById("inviteCode").value = data.code;
            document.getElementById("copyCodeBtn").disabled = false;
            msg.textContent = "有効期限: " + data.expiresAt;
        })
        .catch(function() {
            msg.textContent = "コード生成に失敗しました。";
        });
    }

    function copyInviteCode() {
        const input = document.getElementById("inviteCode");
        const done = function() {
            document.getElementById("inviteMsg").textContent = "コピーしました。";
        };
        if (navigator.clipboard && window.isSecureContext) {
            navigator.clipboard.writeText(input.value).then(done);
        } else { // http 환경 대비 fallback
            input.select();
            document.execCommand("copy");
            done();
        }
    }
</script>

<!-- 공통 푸터 -->
<%@ include file="/include/footer.jsp"%>
