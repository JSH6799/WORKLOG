<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ include file="/include/header.jsp"%>

<main class="project-wrap">
    <div id="project-content">

        <header class="page-head">
            <div>
                <h1 class="page-title">プロジェクト画面へ</h1>
                <p class="page-desc">開発中、画面を迅速に開くためのリストです。チームプロジェクトと統合する際にこのファイルは削除されます。</p>
            </div>
        </header>

        <div class="panel">
            <ul class="screen-list">
                <li>
                    <strong>プロジェクトリスト</strong>
                    <p class="page-desc">参加中のプロジェクトを閲覧し、新しいプロジェクトを作るか、管理画面へ移動します。</p>
                    <div class="links"><a class="btn btn-small" href="${pageContext.request.contextPath}/project/list">list.jsp 開ける</a></div>
                </li>
                <li>
                    <strong>新たなプロジェクトを作成</strong>
                    <p class="page-desc">プロジェクト名、詳細、期間を入力する画面です。</p>
                    <div class="links"><a class="btn btn-small" href="${pageContext.request.contextPath}/project/add">add.jsp 開ける</a></div>
                </li>
                <li>
                    <strong>プロジェクト管理</strong>
                    <p class="page-desc">情報を修正し、所有者はプロジェクトを削除します。権限によって画面が異なります。</p>
                    <div class="links">
                        <a class="btn btn-small" href="${pageContext.request.contextPath}/project/manage?role=OWNER">所有者として閲覧</a>
                        <a class="btn btn-small" href="${pageContext.request.contextPath}/project/manage?role=ADMIN">メンバーとして閲覧</a>
                        <a class="btn btn-small" href="${pageContext.request.contextPath}/project/manage?role=MEMBER">閲覧者として閲覧</a>
                    </div>
                </li>
                <li>
                    <strong>メンバー権限</strong>
                    <p class="page-desc">メンバーリストと権限を確認し、所有者は権限を変更します。</p>
                    <div class="links">
                        <a class="btn btn-small" href="${pageContext.request.contextPath}/project/members?role=OWNER">所有者として閲覧</a>
                        <a class="btn btn-small" href="${pageContext.request.contextPath}/project/members?role=ADMIN">メンバーとして閲覧</a>
                        <a class="btn btn-small" href="${pageContext.request.contextPath}/project/members?role=MEMBER">閲覧者として閲覧</a>
                    </div>
                </li>
            </ul>
        </div>

    </div>
</main>

<%@ include file="/include/footer.jsp"%>