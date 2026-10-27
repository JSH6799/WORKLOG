<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.ProjectDTO" %>
<%@ include file="/include/header.jsp"%>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/proandmem.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/manageproject.css">

<%
    ProjectDTO project = (ProjectDTO) request.getAttribute("project");
    if(project == null) {
        project = new ProjectDTO();
        project.setProjectId("");
        project.setProjectName("");
        project.setDescription("");
        project.setStartDate("");
        project.setEndDate("");
    }
    
    // 세션 혹은 권한 체크 값 (임시로 OWNER 부여, 필요시 로그인 세션 연동)
    String role = request.getParameter("role");
    if (role == null) role = "OWNER";
    boolean isOwner = "OWNER".equals(role);
    boolean canEdit = !"MEMBER".equals(role);
    String msg = request.getParameter("msg");
%>

<main class="project-wrap">
<div id="project-content">

    <nav class="crumbs" aria-label="現在地">
        <a href="${pageContext.request.contextPath}/project/list">プロジェクトリスト</a>
        <span><%= project.getProjectName() %></span>
    </nav>

    <header class="page-head">
        <div>
            <h1 class="page-title">プロジェクト管理</h1>
            <p class="page-desc"><%= project.getProjectName() %></p>
        </div>
        <a class="btn" href="${pageContext.request.contextPath}/project/list">リストへ</a>
    </header>

    <% if ("saved".equals(msg)) { %>
        <p class="banner banner-ok" role="status">変更内容を保存しました。</p>
    <% } %>

    <div class="layout-2">
        <div class="stack">
            <form class="panel form-panel" action="${pageContext.request.contextPath}/project/manage" method="post">
                <input type="hidden" name="projectId" value="<%= project.getProjectId() %>">
                <input type="hidden" name="role" value="<%= role %>">

                <div class="field">
                    <label for="projectName">プロジェクト名<% if (canEdit) { %><span class="req" aria-hidden="true">*</span><% } %></label>
                    <input type="text" id="projectName" name="projectName" maxlength="30" required
                           value="<%= project.getProjectName() %>" <%= canEdit ? "" : "readonly" %>>
                </div>

                <div class="field">
                    <label for="description">詳細</label>
                    <textarea id="description" name="description" maxlength="2000"
                              <%= canEdit ? "" : "readonly" %>><%= project.getDescription() %></textarea>
                </div>

                <div class="field-row">
                    <div class="field">
                        <label for="startDate">開始日</label>
                        <input type="date" id="startDate" name="startDate" value="<%= project.getStartDate() %>" <%= canEdit ? "" : "readonly" %>>
                    </div>
                    <div class="field">
                        <label for="endDate">終了日</label>
                        <input type="date" id="endDate" name="endDate" value="<%= project.getEndDate() %>" <%= canEdit ? "" : "readonly" %>>
                    </div>
                </div>

                <% if (canEdit) { %>
                <div class="form-actions">
                    <button type="submit" class="btn btn-primary">変更内容を保存</button>
                </div>
                <% } %>
            </form>

            <% if (isOwner) { %>
            <section class="panel danger-zone">
                <div>
                    <h2>プロジェクト削除</h2>
                    <p>プロジェクトを削除すると、全てのプロジェクトメンバー情報が消え、復旧出来ません</p>
                </div>
                <button type="button" class="btn btn-danger-outline" id="open-delete">プロジェクト削除</button>
            </section>
            <% } %>
        </div>

        <aside class="panel side-panel">
            <h2 class="side-title">プロジェクト情報</h2>
            <dl class="summary-list">
                <div><dt>作成者 ID</dt><dd><%= project.getCreatorId() != null ? project.getCreatorId() : "-" %></dd></div>
                <div><dt>自分の権限</dt><dd><span class="badge badge-<%= role.toLowerCase() %>"><%= role %></span></dd></div>
            </dl>
            <div class="side-actions">
                <a class="btn" href="${pageContext.request.contextPath}/project/members?projectId=<%= project.getProjectId() %>">メンバー権限管理</a>
            </div>
        </aside>
    </div>

<% if (isOwner) { %>
<dialog id="delete-dialog" aria-labelledby="delete-title">
    <form action="${pageContext.request.contextPath}/project/delete" method="post">
        <input type="hidden" name="projectId" value="<%= project.getProjectId() %>">
        <h2 id="delete-title">本当にプロジェクトを削除しますか?</h2>
        <p>"<%= project.getProjectName() %>" プロジェクトおよび全てのメンバー情報が消えます。</p>
        <label for="confirm-name">確認のため、プロジェクト名を正確に入力してください。</label>
        <input type="text" id="confirm-name" autocomplete="off">
        <div class="dialog-actions">
            <button type="button" class="btn" id="cancel-delete">キャンセル</button>
            <button type="submit" class="btn btn-danger" id="confirm-delete" disabled>削除</button>
        </div>
    </form>
</dialog>

<script>
    (function () {
        var dialog = document.getElementById('delete-dialog');
        var input = document.getElementById('confirm-name');
        var confirmBtn = document.getElementById('confirm-delete');
        var projectName = '<%= project.getProjectName() %>';

        var openBtn = document.getElementById('open-delete');
        if(openBtn) {
            openBtn.addEventListener('click', function () {
                input.value = '';
                confirmBtn.disabled = true;
                dialog.showModal();
            });
        }
        var cancelBtn = document.getElementById('cancel-delete');
        if(cancelBtn) {
            cancelBtn.addEventListener('click', function () {
                dialog.close();
            });
        }
        if(input) {
            input.addEventListener('input', function () {
                confirmBtn.disabled = (input.value.trim() !== projectName);
            });
        }
    })();
</script>
<% } %>

</div>
</main>

<%@ include file="/include/footer.jsp"%>