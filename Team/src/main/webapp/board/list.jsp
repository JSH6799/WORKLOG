<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ include file="/include/header.jsp"%>
	
	<main class="list-wrap">
<!-- 검색창 및 글쓰기 버튼 -->
	<h2 class="list-title">プロジェクト投稿</h2>
	<div class="unread-notice">
		<p class="unread-title">未確認投稿</p>
		<p class="unread-count">2件</p>
		<button type="button" id="buttonFilter"class="button-filter">未確認投稿一覧</button>
	</div>
		<div class="list-top">
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
				<a href="${pageContext.request.contextPath}/board/write.jsp">投稿する</a>
		</div>
		
		
<!-- 게시글 목록 -->
		<ul class="board-grid">
			<!-- command 및 dao 작성 완료 후 c:forEach 추가 -->
			<li class="board-list">
				<div class="board-info">
					<p class="board-number">No.</p>
						<!-- 시퀀스 활용해서 게시글 번호 넣기 -->
					<p class="board-title"><a href="${pageContext.request.contextPath}/board/detail.jsp">タイトル</a></p>
					<span class="check-required">未確認</span>
					<p class="board-writer">投稿者</p>
					<p class="board-date">YYYY/MM/DD</p>
				</div>
			</li>
			<li class="board-list">
				<div class="board-info">
					<p class="board-number">No.</p>
					<p class="board-title"><a href="">タイトル</a></p>
					<span class="check-required">未確認</span>
					<p class="board-writer">投稿者</p>
					<p class="board-date">YYYY/MM/DD</p>
				</div>
			</li>
			<li class="board-list">
				<div class="board-info">
					<p class="board-number">No.</p>
					<p class="board-title"><a href="">タイトル</a></p>
					<p class="board-writer">投稿者</p>
					<p class="board-date">YYYY/MM/DD</p>
				</div>
			</li>
			<li class="board-list">
				<div class="board-info">
					<p class="board-number">No.</p>
					<p class="board-title"><a href="">タイトル</a></p>
					<span class="check-complete">確認済み</span>
					<p class="board-writer">投稿者</p>
					<p class="board-date">YYYY/MM/DD</p>
				</div>
			</li>
			<li class="board-list">
				<div class="board-info">
					<p class="board-number">No.</p>
					<p class="board-title"><a href="">タイトル</a></p>
					<p class="board-writer">投稿者</p>
					<p class="board-date">YYYY/MM/DD</p>
				</div>
			</li>
			<li class="board-list">
				<div class="board-info">
					<p class="board-number">No.</p>
					<p class="board-title"><a href="">タイトル</a></p>
					<span class="check-complete">確認済み</span>
					<p class="board-writer">投稿者</p>
					<p class="board-date">YYYY/MM/DD</p>
				</div>
			</li>
		</ul>
		
		<div class="pagination">
			<a href="" class="page-nav disabled">前</a>
			<a href="" class="page-num active">1</a>
			<a href="" class="page-num">2</a>
			<a href="" class="page-num">3</a>
			<a href="" class="page-num">4</a>
			<a href="" class="page-num">5</a>
			<a href="" class="page-nav">次</a>
		</div>
	</main>


<%@ include file="/include/footer.jsp"%>
<script src="${pageContext.request.contextPath}/js/list.js"></script>
