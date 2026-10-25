<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ include file="/include/header.jsp"%>

<%
    // 화면 확인용: 주소 끝의 ?role=OWNER / ADMIN / MEMBER 로 권한별 화면을 미리 볼 수 있습니다.
    // 나중에는 서블릿이 DB에서 확인한 내 권한을 이 자리에 담아줍니다.
    String role = request.getParameter("role");
    if (!"ADMIN".equals(role) && !"MEMBER".equals(role)) {
        role = "OWNER";
    }
    boolean isOwner = "OWNER".equals(role);
    boolean canEdit = !"MEMBER".equals(role);           // 소유자, 관리자만 수정 가능
    String roleLabel = isOwner ? "所有者" : ("ADMIN".equals(role) ? "관리자" : "멤버");
    String roleClass = role.toLowerCase();              // badge-owner / badge-admin / badge-member
    String msg = request.getParameter("msg");
    String projectName = "クライアント会社ポータルリニュアル";          // 샘플 값
%>

<main class="project-wrap">
<div id="project-content">

    <%-- 개발용: 권한별 화면 미리보기. 서블릿 연결 후 이 블록은 삭제합니다. --%>
    <div class="dev-bar">
        <span>권한별 미리보기</span>
        <a href="${pageContext.request.contextPath}/project/manage?role=OWNER" class="<%= isOwner ? "on" : "" %>">所有者</a>
        <a href="${pageContext.request.contextPath}/project/manage?role=ADMIN" class="<%= "ADMIN".equals(role) ? "on" : "" %>">メンバー</a>
        <a href="${pageContext.request.contextPath}/project/manage?role=MEMBER" class="<%= "MEMBER".equals(role) ? "on" : "" %>">閲覧者</a>
    </div>

    <nav class="crumbs" aria-label="現在地">
        <a href="${pageContext.request.contextPath}/project/list">プロジェクト</a>
        <span><%= projectName %></span>
    </nav>

    <header class="page-head">
        <div>
            <h1 class="page-title">プロジェクト管理</h1>
            <p class="page-desc"><%= projectName %></p>
        </div>
        <a class="btn" href="${pageContext.request.contextPath}/project/list">リストへ</a>
    </header>

    <% if ("saved".equals(msg)) { %>
        <p class="banner banner-ok" role="status">変更内容を保存しました</p>
    <% } %>
    <% if (!canEdit) { %>
        <p class="banner banner-info">閲覧者の権限はプロジェクトの閲覧のみです。修正の必要な場合、所有者やメンバー等へ問い合わせください。</p>
    <% } %>

    <div class="layout-2">

        <div class="stack">

            <%-- 화면 확인용: 제출하면 같은 화면에 저장 메시지가 나옵니다. 서블릿 연결 후 action과 method를 바꿉니다. --%>
            <form class="panel form-panel" action="${pageContext.request.contextPath}/project/manage" method="get">
                <input type="hidden" name="role" value="<%= role %>">
                <input type="hidden" name="msg" value="saved">

                <div class="field">
                    <label for="projectName">プロジェクト名<% if (canEdit) { %><span class="req" aria-hidden="true">*</span><% } %></label>
                    <input type="text" id="projectName" name="projectName" maxlength="30" required
                           value="<%= projectName %>" <%= canEdit ? "" : "readonly" %>>
                </div>

                <div class="field">
                    <label for="description">詳細</label>
                    <textarea id="description" name="description" maxlength="300"
                              <%= canEdit ? "" : "readonly" %>>クライアント会社ポータルの画面と承認構造を新たに整備します。</textarea>
                </div>

                <div class="field-row">
                    <div class="field">
                        <label for="startDate">スタート日</label>
                        <input type="date" id="startDate" name="startDate" value="2026-09-15" <%= canEdit ? "" : "readonly" %>>
                    </div>
                    <div class="field">
                        <label for="endDate">終了日</label>
                        <input type="date" id="endDate" name="endDate" value="2027-02-28" <%= canEdit ? "" : "readonly" %>>
                    </div>
                </div>

                <% if (canEdit) { %>
                <div class="form-actions">
                    <button type="submit" class="btn btn-primary">変更内容を保存</button>
                </div>
                <% } %>
            </form>

            <%-- 삭제는 소유자에게만 보입니다 (서블릿에서도 한 번 더 확인할 예정) --%>
            <% if (isOwner) { %>
            <section class="panel danger-zone">
                <div>
                    <h2>プロジェクト削除</h2>
                    <p>削除すればプロジェクトとメンバーの情報がすべて消え、取り消しは不可能です。</p>
                </div>
                <button type="button" class="btn btn-danger-outline" id="open-delete">プロジェクト削除</button>
            </section>
            <% } %>

        </div>

        <aside class="panel side-panel">
            <h2 class="side-title">プロジェクト情報</h2>
            <dl class="summary-list">
                <div><dt>所有者</dt><dd>ジャバ</dd></div>
                <div><dt>生成日</dt><dd>2026-09-10</dd></div>
                <div><dt>メンバー</dt><dd>5名</dd></div>
                <div><dt>あなたの権限</dt><dd><span class="badge badge-<%= roleClass %>"><%= roleLabel %></span></dd></div>
            </dl>
            <div class="side-actions">
                <a class="btn" href="${pageContext.request.contextPath}/project/members?role=<%= role %>">メンバー権限と閲覧</a>
            </div>
        </aside>

    </div>

<% if (isOwner) { %>
<%-- 삭제 확인 창: 프로젝트명을 똑같이 입력해야 삭제 버튼이 켜집니다. --%>
<dialog id="delete-dialog" aria-labelledby="delete-title">
    <form action="${pageContext.request.contextPath}/project/list" method="get">
        <input type="hidden" name="msg" value="deleted">
        <h2 id="delete-title">プロジェクトを削除しますか？</h2>
        <p>"<%= projectName %>"プロジェクトおよび全てのメンバー情報が消えますこの作業は取り消しは不可能です。</p>
        <label for="confirm-name">確認のため、プロジェクト名を入力してください。</label>
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
        var projectName = '<%= projectName %>';

        document.getElementById('open-delete').addEventListener('click', function () {
            input.value = '';
            confirmBtn.disabled = true;
            dialog.showModal();
        });
        document.getElementById('cancel-delete').addEventListener('click', function () {
            dialog.close();
        });
        input.addEventListener('input', function () {
            confirmBtn.disabled = (input.value.trim() !== projectName);
        });
    })();
</script>
<% } %>

</div>
</main>

<%@ include file="/include/footer.jsp"%>                                                                             