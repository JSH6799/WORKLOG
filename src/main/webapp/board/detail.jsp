<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ include file="/include/header.jsp"%>
	
	<main class="detail-wrap">
	<div class="detail-body">
	<div class="detail-main-content">
		<div class="detail-header">
			<h2 class="detail-title">${view.title}</h2>
			<p class="detail-meta">作成者 ${view.writerName} | ${view.writeDate}</p>
			
			<div class="detail-actions">
				<button type="button" id="bookmarkBtn" class="btn btn-outline bookmark-btn">📑ブックマーク</button>
			</div>
		</div>
		
		<C:if test="${not empty view.imgPath}">
			<div class="detail-image">
				<img src="/imgs/${view.imgPath}" alt="" class="detail-thumb">
			</div>
		</C:if>
		
		<div class="detail-content">
			${view.content}
		</div>
		
		    <div class="confirm-btn-wrap">
			<C:if test="${view.checkRequired eq 'Y' and sessionScope.loginMember.memberId ne view.writer}">
		        <button type="button" id="confirmBtn" class="btn btn-outline confirm-btn ${checked ? 'cfm' : ''}"
				        ${checked ? 'disabled' : ''}>${checked ? '✅ 確認完了' : '確認'}</button>
			</C:if>
		    </div>

		<div class="detail-actions-right">
			<C:if test="${sessionScope.loginMember.memberId eq view.writer}">
				<a href="${pageContext.request.contextPath}/project_board/update?bno=${view.bno}" class="btn btn-outline">修正</a>
				<a href="${pageContext.request.contextPath}/project_board/delete?bno=${view.bno}&projectId=${view.projectId}"
					 class="btn btn-outline" onclick="return confirm('削除しますか?')">削除</a>
			</C:if>
			<a href="${pageContext.request.contextPath}" class="btn btn-primary">一覧</a>
		</div>
	</div>
		
		<!-- 確認状況(投稿者のみ確認可能) -->
		<C:if test="${sessionScope.loginMember.memberId eq view.writer and view.checkRequired eq 'Y'}">
		<div class="confirm-status">
			<h3>確認状況</h3>
			<p>${checkCount} / ${totalCount}人確認</p>
			<ul>
			<C:forEach var="cl" items="${checkList}">
			    <li><span>${cl.memberName}</span><span class="${cl.checkStatus eq '未確認' ? 'unconfirmed' : 'confirmed'}">${cl.checkStatus}</span></li>
			</C:forEach>
			</ul>
		</div>
		</C:if>
		
	</div>
		
	</main>

<%@ include file="/include/footer.jsp"%>
<script src="${pageContext.request.contextPath}/js/detail.js"></script>
