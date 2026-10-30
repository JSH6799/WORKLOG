<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.time.LocalDate"%>
<%@ include file="/include/header.jsp"%>
<%
    // 오늘 날짜 (yyyy-MM-dd 형식, input type="date"의 value 형식과 동일)
    String today = LocalDate.now().toString();
%>

<link rel="stylesheet"
    href="${pageContext.request.contextPath}/css/proandmem.css">
<link rel="stylesheet"
    href="${pageContext.request.contextPath}/css/project/addproject.css">

<main class="project-wrap">
    <div id="project-content">

        <nav class="crumbs" aria-label="現在位置">
            <a href="${pageContext.request.contextPath}/project/list">
                プロジェクトリスト
            </a>
            <span>新しいプロジェクト</span>
        </nav>

        <header class="page-head">
            <div>
                <h1 class="page-title">新しいプロジェクトを作成</h1>
                <p class="page-desc">
                    プロジェクト情報を入力してください。
                    作成者にはリーダー権限が付与されます。
                </p>
            </div>
        </header>

        <div class="layout-2">

            <form class="panel form-panel"
                action="${pageContext.request.contextPath}/project/add"
                method="post">

                <div class="field">
                    <label for="projectName">
                        プロジェクト名
                        <span class="req" aria-hidden="true">*</span>
                    </label>

                    <input type="text" id="projectName"
                        name="projectName" maxlength="30" required
                        placeholder="例：2026年度新規サービス開発">

                    <p class="field-hint">
                        30文字以内で入力してください。
                    </p>
                </div>

                <!-- チーム名 -->
                <div class="field">
                    <label for="teamName">
                        チーム名
                        <span class="req" aria-hidden="true">*</span>
                    </label>

                    <input type="text" id="teamName"
                        name="teamName" maxlength="100" required
                        placeholder="例：開発チーム">

                    <p class="field-hint">
                        所属するチーム名を入力してください。
                    </p>
                </div>

                <div class="field">
                    <label for="description">詳細</label>

                    <textarea id="description" name="description"
                        maxlength="2000"
                        placeholder="プロジェクトの目的と範囲を入力してください。"></textarea>

                    <p class="field-hint">
                        2000文字以内で入力してください。
                    </p>
                </div>

                <div class="field-row">
                    <div class="field">
                        <label for="startDate">開始日</label>
                        <input type="date" id="startDate"
                            name="startDate" value="<%= today %>">
                    </div>

                    <div class="field">
                        <label for="endDate">終了日</label>
                        <input type="date" id="endDate"
                            name="endDate" value="<%= today %>"
                            min="<%= today %>">
                    </div>
                </div>

                <div class="form-actions">
                    <button type="submit"
                        class="btn btn-primary">
                        プロジェクトを作成
                    </button>

                    <a class="btn"
                        href="${pageContext.request.contextPath}/project/list">
                        キャンセル
                    </a>
                </div>

            </form>

            <aside class="panel side-panel">
                <h2 class="side-title">権限の説明</h2>

                <dl class="role-legend">
                    <div>
                        <dt>
                            <span class="badge badge-owner">リーダー</span>
                        </dt>
                        <dd>
                            プロジェクトの編集・削除、
                            メンバー権限の変更
                        </dd>
                    </div>

                    <div>
                        <dt>
                            <span class="badge badge-admin">メンバー</span>
                        </dt>
                        <dd>プロジェクト情報の編集</dd>
                    </div>

                    <div>
                        <dt>
                            <span class="badge badge-member">閲覧者</span>
                        </dt>
                        <dd>プロジェクト情報の閲覧</dd>
                    </div>
                </dl>
            </aside>

        </div>
    </div>
</main>

<%@ include file="/include/footer.jsp"%>
