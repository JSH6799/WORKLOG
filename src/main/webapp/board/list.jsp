<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ include file="/include/header.jsp"%>
	
	<main class="list-wrap">
<!-- 검색창 및 글쓰기 버튼 -->
	<h2 class="list-title">プロジェクト投稿</h2>
	<!-- 수정 -->
	<div class="unread-notice">
		<p class="unread-title">未確認投稿</p>
		<p class="unread-count">${unreadCount}件</p>
		<button type="button" id="buttonFilter"class="button-filter" 
				data-project-id="${projectId}" data-context-path="${pageContext.request.contextPath}">未確認投稿一覧</button>
	</div>
		<div class="list-top">
			<form id="searchForm" class="search-form" action="${pageContext.request.contextPath}/project_board/list" method="get">
				<input type="hidden" name="projectId" value="${projectId}">
				<select name="searchType" class="search-select">
					<option value="">選択</option>
					<option value="title" ${searchType == 'title' ? 'selected' : ''}>タイトル</option>
					<option value="content" ${searchType == 'content' ? 'selected' : ''}>内容</option>
					<option value="writer" ${searchType == 'writer' ? 'selected' : ''}>投稿者</option>
				</select>
				<input type="text" id="searchText" name="keyword" value="${keyword}"placeholder="検索ワードを入力してください" class="search-input">
				<button type="submit" class="btn btn-outline">検索</button>
			</form>
				<a href="${pageContext.request.contextPath}/project_board/write?projectId=${param.projectId}">投稿する</a>
		</div>
		
		
	<!-- 게시글 목록 -->
		<ul class="board-grid">
		<C:forEach var="b" items="${boardList}">
			<li class="board-list">
				<div class="board-info">
					<p class="board-number">No.${b.bno}</p>
					<p class="board-title"><a href="${pageContext.request.contextPath}/project_board/view?bno=${b.bno}">${b.title}</a></p>
					<span class="check-required">未確認</span>
					<!-- 확인 여부 메서드 만든 후 수정 -->
					<p class="board-writer">${b.writerName}</p>
					<p class="board-date">${b.writeDate}</p>
				</div>
			</li>
		</C:forEach>
		</ul>
		
		<div class="pagination">
			<C:choose>
			<C:when test="${startPage > 1}">
				<C:url var="preUrl" value="/project_board/list?projectId=${param.projectId}">
					<C:param name="page" value="${startPage - 1}"/>
					<C:param name="searchType" value="${searchType}"/>
					<C:param name="keyword" value="${keyword}"/>
					<C:param name="unreadOnly" value="${unreadOnly}"/>
				</C:url>
				<a href="${preUrl}" class="page-nav">前へ</a>
			</C:when>
			<C:otherwise>
				<span class="page-nav disabled">前へ</span>
			</C:otherwise>
		</C:choose>
		<!-- 페이지 번호 -->
		<C:forEach var="p" begin="${startPage}" end="${endPage}">
			<C:url var="pageUrl" value="/project_board/list?projectId=${param.projectId}">
				<C:param name="page" value="${p}"/>
				<C:param name="searchType" value="${searchType}"/>
				<C:param name="keyword" value="${keyword}"/>
				<C:param name="unreadOnly" value="${unreadOnly}"/>
			</C:url>
			<a href="${pageUrl}" class="page-num ${p==currentPage?'active':''}">${p}</a>
		</C:forEach>
		<!-- 다음 버튼 -->
		<C:choose>
			<C:when test="${endPage < totalPage}">
				<C:url var="nextUrl" value="/project_board/list?projectId=${param.projectId}">
					<C:param name="page" value="${endPage + 1}"/>
					<C:param name="searchType" value="${searchType}"/>
					<C:param name="keyword" value="${keyword}"/>
					<C:param name="unreadOnly" value="${unreadOnly}"/>
				</C:url>
				<a href="${nextUrl}" class="page-nav">次へ</a>
			</C:when>
			<C:otherwise>
				<span class="page-nav disabled">次へ</span>
			</C:otherwise>
		</C:choose>
		</div>
	</main>


<%@ include file="/include/footer.jsp"%>
<script src="${pageContext.request.contextPath}/js/list.js"></script>
