<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>

<%
    // Controller / Servlet 에서 request.setAttribute로 넘겨주는 데이터 수신
    String userName = (String) request.getAttribute("userName");
    if (userName == null) userName = "山田一郎";

    String projectName = (String) request.getAttribute("projectName");
    if (projectName == null) projectName = "WORKLOG Web";

    Integer unreadPostCount = (Integer) request.getAttribute("unreadPostCount");
    if (unreadPostCount == null) unreadPostCount = 2;

    Integer projectProgress = (Integer) request.getAttribute("projectProgress");
    if (projectProgress == null) projectProgress = 40;

    // ===== 목표 설정 모달에서 사용하는 값 =====
    String goalTitle = (String) request.getAttribute("goalTitle");
    if (goalTitle == null) goalTitle = "WORKLOG v1.0 Launch";

    String phaseName = (String) request.getAttribute("phaseName");
    if (phaseName == null) phaseName = "バックエンド統合";

    // ===== D-day 계산 (날짜 기준으로 매일 자동 변경) =====
    java.time.LocalDate today  = java.time.LocalDate.now(java.time.ZoneId.of("Asia/Seoul")); // 서버 시간대에 맞게 변경
    java.time.LocalDate target = (java.time.LocalDate) request.getAttribute("targetDate");
    if (target == null) target = java.time.LocalDate.of(2026, 10, 30);

    long remainDays = java.time.temporal.ChronoUnit.DAYS.between(today, target);

    String stage, stageLabel, ddayText;
    if (remainDays < 0) {
        stage = "over";   stageLabel = "期限超過 +" + (-remainDays) + "日"; ddayText = "D+" + (-remainDays);
    } else if (remainDays == 0) {
        stage = "today";  
        stageLabel = "本日リリース!";                    
        ddayText = "D-DAY";
    } else if (remainDays <= 7) {
        stage = "danger"; stageLabel = "最終週";
        ddayText = "D-" + remainDays;
    } else if (remainDays <= 14) {
        stage = "warn";   stageLabel = "スパート期間";
		ddayText = "D-" + remainDays;
    } else {
        stage = "safe";   stageLabel = "余裕あり";
        ddayText = "D-" + remainDays;
    }

    java.time.format.DateTimeFormatter dateFmt = java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd");
%>

<!-- 공통 헤더 (header.jsp 안에서 <html>, <head>, <link css>, <header GNB>가 이미 출력됨) -->
<%@ include file="/include/header.jsp"%>

<!-- 대시보드 전용 CSS 파일 연결 -->
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/main.css">

<!-- Main Container -->
<main class="main-container">

    <!-- Page Title & Primary Action (상단 버튼 제거 및 타이틀 영역 정돈) -->
    <div class="page-title-section" style="align-items: center !important;">
        <div>
            <h1 class="page-title">ダッシュボード</h1>
            <div class="project-selector">
                現在選択中のプロジェクト: <strong><%= projectName %></strong>
            </div>
        </div>
    </div>

    <!-- Summary Banners (Unread Only - Full Width) -->
    <section class="summary-banner-single">
        <div class="summary-card" style="width: 100%;">
            <div class="summary-info">
                <span class="badge badge-unread">未確認</span>
                <span class="summary-label">未確認投稿</span>
                <span class="summary-count"><%= unreadPostCount %>件</span>
            </div>
            <a href="<%= request.getContextPath() %>/member/mypage.jsp" class="btn-sm-outline">未確認投稿一覧</a>
        </div>
    </section>

    <!-- Dashboard Grid Layout -->
    <div class="dashboard-grid">

        <!-- Main Content Area (Left) -->
        <div class="left-content">

            <!-- Widget: Project Progress (D-day 단계별 변화) -->
            <div class="widget-card stage-<%= stage %>">
                <div class="widget-header">
                    <span class="widget-title">📊 プロジェクト進行状況</span>
                    <span class="dday-chip"><%= ddayText %></span>
                </div>
                <div class="progress-box">
                    <div class="stage-row">
                        <span class="stage-badge"><%= stageLabel %></span>
                        <span class="stage-sub">目標日 <%= target.format(dateFmt) %><%= remainDays >= 0 ? " まで あと" + remainDays + "日" : "" %></span>
                    </div>
                    <div class="progress-header-info">
                        <span><%= goalTitle %></span>
                        <span><%= projectProgress %>%</span>
                    </div>
                    <div class="progress-bar-bg">
                        <div class="progress-bar-fill" style="width: <%= projectProgress %>%;"></div>
                    </div>
                    <div class="progress-details">
                        <span>フェーズ: <%= phaseName %></span>
                        <span>目標日: <%= target.format(dateFmt) %></span>
                    </div>
                </div>
            </div>

            <!-- Widget: Recent Posts -->
            <div class="widget-card">
                <div class="widget-header">
                    <span class="widget-title">📝 最近のプロジェクト投稿</span>
                    <a href="<%= request.getContextPath() %>/board/list.jsp" class="widget-action">投稿一覧へ ›</a>
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
                        <tr>
                            <td><span class="badge badge-unread">未確認</span></td>
                            <td><a href="post_detail.jsp?id=102" class="post-link unread-link">[重要] 第3回仕様変更に関する共有</a></td>
                            <td>管理者</td>
                            <td>2026/10/22</td>
                        </tr>
                        <tr>
                            <td><span class="badge badge-unread">未確認</span></td>
                            <td><a href="post_detail.jsp?id=101" class="post-link">フロントエンドデザインコンポーネント追加</a></td>
                            <td>佐藤</td>
                            <td>2026/10/21</td>
                        </tr>
                        <tr>
                            <td><span class="badge badge-confirmed">確認済み</span></td>
                            <td><a href="post_detail.jsp?id=100" class="post-link">10月第3週のスプリント振り返り</a></td>
                            <td><%= userName %></td>
                            <td>2026/10/19</td>
                        </tr>
                    </tbody>
                </table>
            </div>

        </div>

        <!-- Sidebar Content Area (Right) -->
        <div class="right-content">

            <!-- Widget: Quick Actions -->
            <div class="widget-card">
                <div class="widget-header">
                    <span class="widget-title">⚡ クイックアクション</span>
                </div>
                <div class="quick-actions-grid" style="grid-template-columns: repeat(3, minmax(0, 1fr));">
                    <button type="button" class="quick-btn" onclick="openPhaseModal()">
                        <span class="icon">✔️</span>
                        <span>フェーズ</span>
                    </button>
                    <button class="quick-btn" onclick="location.href='<%= request.getContextPath() %>/board/write.jsp'">
                        <span class="icon">✏️</span>
                        <span>投稿作成</span>
                    </button>
                    <button class="quick-btn" onclick="location.href='member_invite.jsp'">
                        <span class="icon">👥</span>
                        <span>メンバー招待</span>
                    </button>
                </div>
            </div>

            <!-- Widget: Quick Memo -->
            <div class="widget-card">
                <div class="widget-header">
                    <span class="widget-title">📌 クイックメモ</span>
                    <div style="display: flex; gap: 6px;">
                        <button type="button" class="btn-sm-outline" onclick="clearMemo()" style="padding: 2px 8px; font-size: 11px;">クリア</button>
                        <button type="button" class="btn-sm-outline" onclick="saveMemo()" style="padding: 2px 8px; font-size: 11px;">保存</button>
                    </div>
                </div>
                <div class="memo-box">
                    <textarea id="dashboardMemo" class="memo-textarea" placeholder="メモ" rows="4" style="width: 100%; border: 1px solid #e9ecef; border-radius: 4px; padding: 10px; box-sizing: border-box; resize: vertical; font-size: 13px; font-family: inherit;"></textarea>
                </div>
            </div>

            <!-- Widget: Active Members -->
            <div class="widget-card">
                <div class="widget-header">
                    <span class="widget-title">👥 チームメンバー (4/5)</span>
                </div>
                <ul class="member-list">
                    <li class="member-item">
                        <div class="member-info">
                            <span class="status-dot online"></span>
                            <span><%= userName %> (自分)</span>
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

<!-- フェーズ / 目標設定 モーダル -->
<div id="phaseModal" class="modal-overlay" aria-hidden="true">
    <div class="modal-box" role="dialog" aria-modal="true" aria-labelledby="phaseModalTitle">
        <div class="modal-header">
            <h2 id="phaseModalTitle">🎯 プロジェクト目標設定</h2>
            <button type="button" class="modal-close" onclick="closePhaseModal()" aria-label="閉じる">&times;</button>
        </div>

        <form id="phaseForm" action="<%= request.getContextPath() %>/project/updateGoal" method="post">
            <div class="modal-body">
                <div class="form-group">
                    <label for="goalTitle">目標名</label>
                    <input type="text" id="goalTitle" name="goalTitle" value="<%= goalTitle %>" maxlength="50" required>
                </div>

                <div class="form-group">
                    <label for="phaseName">現在のフェーズ</label>
                    <select id="phaseName" name="phaseName">
                        <%
                            String[] phases = {"要件定義", "設計", "フロントエンド開発", "バックエンド統合", "テスト", "リリース準備"};
                            for (String p : phases) {
                        %>
                        <option value="<%= p %>" <%= p.equals(phaseName) ? "selected" : "" %>><%= p %></option>
                        <% } %>
                    </select>
                </div>

                <div class="form-group">
                    <label for="targetDate">目標日</label>
                    <input type="date" id="targetDate" name="targetDate" value="<%= target %>" required>
                </div>

                <div class="form-group">
                    <label for="progressRange">進捗率: <strong id="progressValue"><%= projectProgress %></strong>%</label>
                    <input type="range" id="progressRange" name="progress" min="0" max="100" step="5"
                           value="<%= projectProgress %>">
                </div>
            </div>

            <div class="modal-footer">
                <button type="button" class="btn-sm-outline" onclick="closePhaseModal()">キャンセル</button>
                <button type="submit" class="btn-primary-sm">保存</button>
            </div>
        </form>
    </div>
</div>

<script>
    // 페이지 로드 시 로컬 스토리지에서 메모 불러오기
    document.addEventListener("DOMContentLoaded", function() {
        const savedMemo = localStorage.getItem("dashboard_memo_<%= userName %>");
        if (savedMemo) {
            document.getElementById("dashboardMemo").value = savedMemo;
        }
    });

    // 메모 저장 함수
    function saveMemo() {
        const memoContent = document.getElementById("dashboardMemo").value;
        localStorage.setItem("dashboard_memo_<%= userName %>", memoContent);
        alert("메모가 저장되었습니다.");
    }

    // 메모 클리어 함수 (내용을 비우고 자동 저장)
    function clearMemo() {
        const memo = document.getElementById("dashboardMemo");
        if (memo.value === "") return; // 이미 비어 있으면 아무것도 하지 않음
        if (!confirm("メモの内容をすべて削除しますか?")) return; // 확인창이 필요 없으면 이 줄 삭제
        memo.value = "";
        localStorage.setItem("dashboard_memo_<%= userName %>", "");
        memo.focus();
    }

    // ===== フェーズ / 目標設定 モーダル =====
    const phaseModal = document.getElementById("phaseModal");

    function openPhaseModal() {
        phaseModal.classList.add("open");
        phaseModal.setAttribute("aria-hidden", "false");
        document.getElementById("goalTitle").focus();
    }
    function closePhaseModal() {
        phaseModal.classList.remove("open");
        phaseModal.setAttribute("aria-hidden", "true");
    }

    // 배경 클릭 / ESC로 닫기
    phaseModal.addEventListener("click", function(e) {
        if (e.target === phaseModal) closePhaseModal();
    });
    document.addEventListener("keydown", function(e) {
        if (e.key === "Escape" && phaseModal.classList.contains("open")) closePhaseModal();
    });

    // 슬라이더 값 실시간 표시
    document.getElementById("progressRange").addEventListener("input", function() {
        document.getElementById("progressValue").textContent = this.value;
    });
</script>

<!-- 공통 푸터 (footer.jsp 안에서 </body> 및 </html>을 닫음) -->
<%@ include file="/include/footer.jsp"%>