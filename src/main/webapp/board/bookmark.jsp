<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ include file="/include/header.jsp"%>

<main class="bookmark-wrap">
	<h2 class="list-title">ブックマーク一覧</h2>
	<div class="bookmark-project-filter">
    <button type="button" class="project-filter-btn active" data-project="all">
        全プロジェクト
    </button>
    <C:forEach var="p" items="${bmkProList}">
	    <button type="button" class="project-filter-btn" data-project="${p.projectId}">
	        <C:out value="${p.projectName}"/>
	    </button>
    </C:forEach>
</div>
	<div class="bookmark-top">
		<form id="searchForm" class="search-form" action="">
		<input type="hidden" name="projectId" id="searchProjectId" value="all">
			<select name="searchType" class="search-select">
				<option value="">選択</option>
				<option value="title">タイトル</option>
				<option value="content">内容</option>
				<option value="writer">投稿者</option>
			</select>
			<input type="text" id="searchText" name="keyword" value="${keyword}" placeholder="検索ワードを入力してください" class="search-input">
			<button type="submit" class="btn btn-outline">検索</button>
		</form>
		<div class="bmk-btn-group">
			<button type="button" id="bmkEditBtn" class="btn btn-outline">編集</button>
			<button type="button" id="bmkCancelBtn" class="btn btn-outline bmk-edit-cancel">キャンセル</button>
		</div>
	</div>

<!-- 북마크 목록 -->
	<ul class="board-grid">
		<C:forEach var="b" items="${bmkList}">
			<li class="board-list">
				<div class="bookmark-list">
				    <article class="bookmark-card" data-project="${b.projectId}">
				    <input type="checkbox" class="bmk-check" value="${b.bno}" style="display:none;">
				        <span class="bookmark-project-name">${b.projectName}</span>
				        <h3>${b.title}</h3>
				        <span class="bookmark-date">${b.bookmarkDate}</span>
				    </article>
				</div>
			</li>
		</C:forEach>
	</ul>
	<input type="hidden" id="contextPath" value="${pageContext.request.contextPath}">
	<div id="bookmarkScrollTrigger"></div>

</main>

<%@ include file="/include/footer.jsp"%>

<script src="${pageContext.request.contextPath}/js/bookmark.js"></script>


