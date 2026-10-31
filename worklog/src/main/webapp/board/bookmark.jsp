<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ include file="/include/header.jsp"%>

<main class="bookmark-wrap">
	<h2 class="list-title">ブックマーク一覧</h2>
	<div class="bookmark-project-filter">
    <button type="button" class="project-filter-btn active"
            data-project="all">
        全プロジェクト
    </button>
    <button type="button" class="project-filter-btn"
            data-project="1">
        プロジェクトA
    </button>
    <button type="button" class="project-filter-btn"
            data-project="2">
        プロジェクトB
    </button>
    <button type="button" class="project-filter-btn"
            data-project="3">
        プロジェクトC
    </button>
</div>
	<div class="bookmark-top">
		<form id="searchForm" class="search-form" action="">
			<select name="searchType" class="search-select">
				<option value="">選択</option>
				<option value="title">タイトル</option>
				<option value="content">内容</option>
				<option value="writer">投稿者</option>
			</select>
			<input type="text" id="searchText" name="keyword" placeholder="検索ワードを入力してください" class="search-input">
			<button type="submit" class="btn btn-outline">検索</button>
		</form>
		<div class="bmk-btn-group">
			<button type="button" id="bmkEditBtn" class="btn btn-outline">編集</button>
			<button type="button" id="bmkCancelBtn" class="btn btn-outline bmk-edit-cancel">キャンセル</button>
		</div>
	</div>

<!-- 북마크 목록 -->
	<ul class="board-grid">
		<li class="board-list">
			<div class="bookmark-list">
			    <article class="bookmark-card" data-project="1">
			        <span class="bookmark-project-name">プロジェクトA</span>
			        <h3>タイトル</h3>
			        <span class="bookmark-date">YYYY-MM-DD</span>
			    </article>
			
			    <article class="bookmark-card" data-project="2">
			        <span class="bookmark-project-name">プロジェクトB</span>
			        <h3>タイトル</h3>
			        <span class="bookmark-date">YYYY-MM-DD</span>
			    </article>
			
			    <article class="bookmark-card" data-project="1">
			        <span class="bookmark-project-name">プロジェクトA</span>
			        <h3>タイトル</h3>
			        <span class="bookmark-date">YYYY-MM-DD</span>
			    </article>
			</div>
		</li>
		<li class="board-list">
			<div class="bookmark-list">
			    <article class="bookmark-card" data-project="1">
			        <span class="bookmark-project-name">プロジェクトA</span>
			        <h3>タイトル</h3>
			        <span class="bookmark-date">YYYY-MM-DD</span>
			    </article>
			
			    <article class="bookmark-card" data-project="2">
			        <span class="bookmark-project-name">プロジェクトB</span>
			        <h3>タイトル</h3>
			        <span class="bookmark-date">YYYY-MM-DD</span>
			    </article>
			
			    <article class="bookmark-card" data-project="1">
			        <span class="bookmark-project-name">プロジェクトA</span>
			        <h3>タイトル</h3>
			        <span class="bookmark-date">YYYY-MM-DD</span>
			    </article>
			</div>
		</li>
		<li class="board-list">
			<div class="bookmark-list">
			    <article class="bookmark-card" data-project="1">
			        <span class="bookmark-project-name">プロジェクトA</span>
			        <h3>タイトル</h3>
			        <span class="bookmark-date">YYYY-MM-DD</span>
			    </article>
			
			    <article class="bookmark-card" data-project="2">
			        <span class="bookmark-project-name">プロジェクトB</span>
			        <h3>タイトル</h3>
			        <span class="bookmark-date">YYYY-MM-DD</span>
			    </article>
			
			    <article class="bookmark-card" data-project="1">
			        <span class="bookmark-project-name">プロジェクトA</span>
			        <h3>タイトル</h3>
			        <span class="bookmark-date">YYYY-MM-DD</span>
			    </article>
			</div>
		</li>
	</ul>
		
		<div class="pagination">
			<a href="" class="page-nav disabled">이전</a>
			<a href="" class="page-num active">1</a>
			<a href="" class="page-num">2</a>
			<a href="" class="page-num">3</a>
			<a href="" class="page-num">4</a>
			<a href="" class="page-num">5</a>
			<a href="" class="page-nav">다음</a>
		</div>
</main>

<%@ include file="/include/footer.jsp"%>

<script src="${pageContext.request.contextPath}/js/bookmark.js"></script>


