<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>

<%
    // Controller / Servlet 에서 request.setAttribute로 넘겨주는 데이터 수신
    String userName = (String) request.getAttribute("userName");
    if (userName == null) userName = "山田一郎";

    String projectName = (String) request.getAttribute("projectName");
    if (projectName == null) projectName = "WORKLOG Web App Development";

    Integer unreadPostCount = (Integer) request.getAttribute("unreadPostCount");
    if (unreadPostCount == null) unreadPostCount = 2;

    Integer pendingTaskCount = (Integer) request.getAttribute("pendingTaskCount");
    if (pendingTaskCount == null) pendingTaskCount = 5;

    Integer projectProgress = (Integer) request.getAttribute("projectProgress");
    if (projectProgress == null) projectProgress = 78;

    Integer dDay = (Integer) request.getAttribute("dDay");
    if (dDay == null) dDay = 12;
%>

<!-- 공통 헤더 (header.jsp 안에서 <html>, <head>, <link css>, <header GNB>가 이미 출력됨) -->
<%@ include file="/include/header.jsp"%>

<!-- 대시보드 전용 CSS 파일 연결 -->
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/index.css">

<!-- Main Container -->
<main class="main-container">
    
    <!-- Page Title & Primary Action -->
    <div class="page-title-section">
        <div>
            <h1 class="page-title">ダッシュボード</h1>
            <div class="project-selector">
                現在選択中のプロジェクト: <strong><%= projectName %></strong>
            </div>
        </div>
        <div>
            <button class="btn-primary" onclick="location.href='post_write.jsp'">投稿する</button>
        </div>
    </div>

    <!-- Summary Banners (Unread / Pending) -->
    <section class="summary-banner-grid">
        <div class="summary-card">
            <div class="summary-info">
                <span class="badge badge-unread">未確認</span>
                <span class="summary-label">未確認投稿</span>
                <span class="summary-count"><%= unreadPostCount %>件</span>
            </div>
            <a href="<%= request.getContextPath() %>/board/list.jsp?projectId=1" class="btn-sm-outline">未確認投稿一覧</a>
        </div>
        <div class="summary-card">
            <div class="summary-info">
                <span class="badge badge-mid">進行中</span>
                <span class="summary-label">未完了タスク</span>
                <span class="summary-count"><%= pendingTaskCount %>件</span>
            </div>
            <a href="task_list.jsp" class="btn-sm-outline">タスク管理</a>
        </div>
    </section>

    <!-- Dashboard Grid Layout -->
    <div class="dashboard-grid">
        
        <!-- Main Content Area (Left) -->
        <div class="left-content">
            
            <!-- Widget: Project Progress -->
            <div class="widget-card">
                <div class="widget-header">
                    <span class="widget-title">📊 プロジェクト進行状況</span>
                    <span class="widget-action">D-<%= dDay %> 日</span>
                </div>
                <div class="progress-box">
                    <div class="progress-header-info">
                        <span>WORKLOG v1.0 Launch</span>
                        <span><%= projectProgress %>%</span>
                    </div>
                    <div class="progress-bar-bg">
                        <div class="progress-bar-fill" style="width: <%= projectProgress %>%;"></div>
                    </div>
                    <div class="progress-details">
                        <span>フェーズ: バックエンド統合 & UI修正</span>
                        <span>目標日: 2026/11/15</span>
                    </div>
                </div>
            </div>

            <!-- Widget: My Tasks -->
            <div class="widget-card">
                <div class="widget-header">
                    <span class="widget-title">✅ マイタスク (My Tasks)</span>
                    <a href="task_list.jsp" class="widget-action">すべて見る ›</a>
                </div>
                <ul class="task-list">
                    <li class="task-item">
                        <div class="task-left">
                            <input type="checkbox" class="task-checkbox" onchange="toggleTask(this)">
                            <span class="task-title">DBテーブル設計の最終確認 (Post & Comment)</span>
                        </div>
                        <div class="task-tags">
                            <span class="badge badge-high">高</span>
                            <span class="task-date">本日 18:00</span>
                        </div>
                    </li>
                    <li class="task-item">
                        <div class="task-left">
                            <input type="checkbox" class="task-checkbox" onchange="toggleTask(this)">
                            <span class="task-title">ダッシュボードUIのJSP移行作業</span>
                        </div>
                        <div class="task-tags">
                            <span class="badge badge-mid">中</span>
                            <span class="task-date">明日</span>
                        </div>
                    </li>
                    <li class="task-item">
                        <div class="task-left">
                            <input type="checkbox" class="task-checkbox" checked onchange="toggleTask(this)">
                            <span class="task-title done">会員登録API仕様書の作成</span>
                        </div>
                        <div class="task-tags">
                            <span class="badge badge-low">低</span>
                            <span class="task-date">完了</span>
                        </div>
                    </li>
                </ul>
            </div>

            <!-- Widget: Recent Posts -->
            <div class="widget-card">
                <div class="widget-header">
                    <span class="widget-title">📝 最近のプロジェクト投稿</span>
                    <a href="post_list.jsp" class="widget-action">投稿一覧へ ›</a>
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
                <div class="quick-actions-grid">
                    <button class="quick-btn" onclick="location.href='post_write.jsp'">
                        <span class="icon">✏️</span>
                        <span>投稿作成</span>
                    </button>
                    <button class="quick-btn" onclick="location.href='task_add.jsp'">
                        <span class="icon">➕</span>
                        <span>タスク追加</span>
                    </button>
                    <button class="quick-btn" onclick="location.href='file_upload.jsp'">
                        <span class="icon">📁</span>
                        <span>資料アップ</span>
                    </button>
                    <button class="quick-btn" onclick="location.href='member_invite.jsp'">
                        <span class="icon">👥</span>
                        <span>メンバー招待</span>
                    </button>
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

<script>
    function toggleTask(checkbox) {
        const taskTitle = checkbox.nextElementSibling;
        if (checkbox.checked) {
            taskTitle.classList.add('done');
        } else {
            taskTitle.classList.remove('done');
        }
    }
</script>

<!-- 공통 푸터 (footer.jsp 안에서 </body> 및 </html>을 닫음) -->
<%@ include file="/include/footer.jsp"%>