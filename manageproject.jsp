<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/include/header.jsp"%>

<link rel="stylesheet"
    href="${pageContext.request.contextPath}/css/proandmem.css">
<link rel="stylesheet"
    href="${pageContext.request.contextPath}/css/manageproject.css">

<%
    model.ProjectDTO project =
        (model.ProjectDTO) request.getAttribute("project");

    Boolean ownerValue = (Boolean) request.getAttribute("isOwner");
    boolean isOwner = Boolean.TRUE.equals(ownerValue);

    // サーブレットから渡された自分の権限 (OWNER / ADMIN / MEMBER)
    // OWNER=リーダー, ADMIN=メンバー, MEMBER=閲覧者
    // 値がない場合は、作成者以外を閲覧者(MEMBER)として扱う
    String role = (String) request.getAttribute("myRole");
    if (role == null) {
        role = isOwner ? "OWNER" : "MEMBER";
    }

    boolean isLeader = "OWNER".equals(role);                 // 修正・削除が可能
    boolean canEdit  = isLeader || "ADMIN".equals(role);     // 修正が可能
    boolean isViewer = !canEdit;                             // 閲覧のみ

    String msg = request.getParameter("msg");

    // 日付形式の変換: yyyy-MM-dd 形式に合わせる
    String startDateValue = project.getStartDate();
    String endDateValue = project.getEndDate();

    // 今日の日付 (保存された値がない場合の初期値)
    String today = java.time.LocalDate.now().toString();

    if (startDateValue != null && startDateValue.length() >= 10) {
        startDateValue = startDateValue.substring(0, 10);
    } else {
        startDateValue = today;
    }

    if (endDateValue != null && endDateValue.length() >= 10) {
        endDateValue = endDateValue.substring(0, 10);
    } else {
        endDateValue = today;
    }
%>

<main class="project-wrap">
    <div id="project-content">

        <nav class="crumbs" aria-label="現在地">
            <a href="${pageContext.request.contextPath}/project/list">
                プロジェクトリスト
            </a>
            <span><c:out value="<%= project.getProjectName() %>"/></span>
        </nav>

        <header class="page-head">
            <div>
                <h1 class="page-title">
                    <% if (isViewer) { %>
                        プロジェクト詳細
                    <% } else { %>
                        プロジェクト管理
                    <% } %>
                </h1>
                <p class="page-desc">
                    <c:out value="<%= project.getProjectName() %>"/>
                </p>
            </div>

            <a class="btn"
                href="${pageContext.request.contextPath}/project/list">
                リストへ戻る
            </a>
        </header>

        <% if ("saved".equals(msg) && canEdit) { %>
            <p class="banner banner-ok" role="status">
                変更内容を保存しました。
            </p>
        <% } %>

        <div class="layout-2">

            <div class="stack">

                <!-- プロジェクト編集フォーム -->
                <form class="panel form-panel"
                    action="${pageContext.request.contextPath}/project/manage"
                    method="post">

                    <input type="hidden" name="projectId"
                        value="<c:out value='<%= project.getProjectId() %>'/>">

                    <div class="field">
                        <label for="projectName">
                            プロジェクト名
                            <% if (canEdit) { %>
                                <span class="req" aria-hidden="true">*</span>
                            <% } %>
                        </label>

                        <input type="text" id="projectName"
                            name="projectName" maxlength="30"
                            value="<c:out value='<%= project.getProjectName() %>'/>"
                            <%= canEdit ? "required" : "readonly" %>>
                    </div>

                    <!-- チーム名 -->
                    <div class="field">
                        <label for="teamName">
                            チーム名
                            <% if (canEdit) { %>
                                <span class="req" aria-hidden="true">*</span>
                            <% } %>
                        </label>

                        <input type="text"
                            id="teamName"
                            name="teamName"
                            maxlength="100"
                            value="<c:out value='<%= project.getTeamName() != null ? project.getTeamName() : "" %>'/>"
                            <%= canEdit ? "required" : "readonly" %>>

                        <% if (canEdit) { %>
                            <p class="field-hint">チーム名を入力してください。</p>
                        <% } %>
                    </div>

                    <div class="field">
                        <label for="description">詳細</label>

                        <textarea id="description" name="description"
                            maxlength="2000"
                            <%= canEdit ? "" : "readonly" %>><c:out value="<%= project.getDescription() %>"/></textarea>
                    </div>

                    <div class="field-row">

                        <div class="field">
                            <label for="startDate">開始日</label>

                            <input type="date" id="startDate"
                                name="startDate"
                                value="<%= startDateValue %>"
                                <%= canEdit ? "" : "readonly" %>>
                        </div>

                        <div class="field">
                            <label for="endDate">終了日</label>

                            <input type="date" id="endDate"
                                name="endDate"
                                value="<%= endDateValue %>"
                                <%= canEdit ? "" : "readonly" %>>
                        </div>

                    </div>

                    <% if (canEdit) { %>
                        <div class="form-actions">
                            <button type="submit"
                                class="btn btn-primary">
                                変更内容を保存
                            </button>
                        </div>
                    <% } else { %>
                        <p class="field-hint">
                            このプロジェクトは閲覧のみ可能です。
                        </p>
                    <% } %>

                </form>

                <!-- リーダーだけに削除機能を表示 -->
                <% if (isLeader) { %>
                    <section class="panel danger-zone">
                        <div>
                            <h2>プロジェクト削除</h2>
                            <p>
                                プロジェクトを削除すると、すべての
                                プロジェクトメンバー情報が削除され、
                                復元できません。
                            </p>
                        </div>

                        <button type="button"
                            class="btn btn-danger-outline"
                            id="open-delete">
                            プロジェクト削除
                        </button>
                    </section>
                <% } %>

            </div>

            <!-- プロジェクト情報 -->
            <aside class="panel side-panel">

                <h2 class="side-title">プロジェクト情報</h2>

                <dl class="summary-list">

                    <div>
                        <dt>作成者</dt>
                        <dd>
                            <c:out value="${creatorName}"
                                default="-"/>
                        </dd>
                    </div>

                    <div>
                        <dt>自分の権限</dt>
                        <dd>
                            <% if (isLeader) { %>
                                <span class="badge badge-owner">リーダー</span>
                            <% } else if (canEdit) { %>
                                <span class="badge badge-admin">メンバー</span>
                            <% } else { %>
                                <span class="badge badge-member">閲覧者</span>
                            <% } %>
                        </dd>
                    </div>

                </dl>

                <div class="side-actions">
                    <a class="btn"
                        href="${pageContext.request.contextPath}/project/members?projectId=<c:out value='<%= project.getProjectId() %>'/>">
                        メンバー権限管理
                    </a>
                </div>

            </aside>

        </div>

        <% if (isLeader) { %>

            <dialog id="delete-dialog"
                aria-labelledby="delete-title">

                <form
                    action="${pageContext.request.contextPath}/project/delete"
                    method="post">

                    <input type="hidden" name="projectId"
                        value="<c:out value='<%= project.getProjectId() %>'/>">

                    <h2 id="delete-title">
                        プロジェクトを削除しますか？
                    </h2>

                    <p>
                        「<c:out value="<%= project.getProjectName() %>"/>」
                        およびすべてのメンバー情報が削除されます。
                    </p>

                    <label for="confirm-name">
                        確認のため、プロジェクト名を正確に入力してください。
                    </label>

                    <input type="text" id="confirm-name"
                        autocomplete="off"
                        data-project-name="<c:out value='<%= project.getProjectName() %>'/>">

                    <div class="dialog-actions">
                        <button type="button" class="btn"
                            id="cancel-delete">
                            キャンセル
                        </button>

                        <button type="submit"
                            class="btn btn-danger"
                            id="confirm-delete" disabled>
                            削除
                        </button>
                    </div>

                </form>
            </dialog>

            <script>
                (function () {
                    const dialog = document.getElementById('delete-dialog');
                    const input = document.getElementById('confirm-name');
                    const confirmBtn = document.getElementById('confirm-delete');
                    const openBtn = document.getElementById('open-delete');
                    const cancelBtn = document.getElementById('cancel-delete');

                    const projectName = input.dataset.projectName;

                    openBtn.addEventListener('click', function () {
                        input.value = '';
                        confirmBtn.disabled = true;
                        dialog.showModal();
                    });

                    cancelBtn.addEventListener('click', function () {
                        dialog.close();
                    });

                    input.addEventListener('input', function () {
                        confirmBtn.disabled =
                            input.value.trim() !== projectName;
                    });
                })();
            </script>

        <% } %>

    </div>
</main>

<%@ include file="/include/footer.jsp"%>
