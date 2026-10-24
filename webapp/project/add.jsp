<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ include file="/include/header.jsp"%>

<main class="project-wrap">
    <div id="project-content">

        <nav class="crumbs" aria-label="현재 위치">
            <a href="${pageContext.request.contextPath}/project/list">プロジェクト</a>
            <span>新しいプロジェクト</span>
        </nav>

        <header class="page-head">
            <div>
                <h1 class="page-title">新たなプロジェクト作成</h1>
                <p class="page-desc">プロジェクトの情報を入力してください。 作成者がリーダーになります。</p>
            </div>
        </header>

        <div class="layout-2">

            <%-- 서블릿 컨트롤러 연동: POST 방식으로 /project/add 에 제출합니다. --%>
            <form class="panel form-panel" action="${pageContext.request.contextPath}/project/add" method="post">

                <div class="field">
                    <label for="projectName">プロジェクト名<span class="req" aria-hidden="true">*</span></label>
                    <input type="text" id="projectName" name="projectName" maxlength="30" required
                           placeholder="예: 2026 하반기 신규 서비스 런칭">
                    <p class="field-hint">30文字以内で入力。</p>
                </div>

                <div class="field">
                    <label for="description">詳細</label>
                    <textarea id="description" name="description" maxlength="300"
                              placeholder="프로젝트의 목적과 범위를 간단히 적어 주세요."></textarea>
                    <p class="field-hint">300文字以内で入力。</p>
                </div>

                <div class="field-row">
                    <div class="field">
                        <label for="startDate">スタート日</label>
                        <input type="date" id="startDate" name="startDate">
                    </div>
                    <div class="field">
                        <label for="endDate">終了日</label>
                        <input type="date" id="endDate" name="endDate">
                        <p class="field-hint">スタート日以降の日付を選択します。</p>
                    </div>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn btn-primary">プロジェクト作成</button>
                    <a class="btn" href="${pageContext.request.contextPath}/project/list">キャンセル</a>
                </div>
            </form>

            <aside class="panel side-panel">
                <h2 class="side-title">権限案内</h2>
                <dl class="role-legend">
                    <div>
                        <dt><span class="badge badge-owner">リーダー</span></dt>
                        <dd>プロジェクト削除、メンバー権限の変更、情報変更</dd>
                    </div>
                    <div>
                        <dt><span class="badge badge-admin">メンバー</span></dt>
                        <dd>プロジェクト情報変更</dd>
                    </div>
                    <div>
                        <dt><span class="badge badge-member">閲覧者</span></dt>
                        <dd>プロジェクト情報閲覧</dd>
                    </div>
                </dl>
            </aside>

        </div>

    </div>
</main>

<%@ include file="/include/footer.jsp"%>