<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ include file="/include/header.jsp"%>
	
	<main class="detail-wrap">
	<div class="detail-body">
	<div class="detail-main-content">
		<div class="detail-header">
			<h2 class="detail-title">サンプルタイトル</h2>
			<p class="detail-meta">作成者 名前入る所 | YYYY-MM-DD</p>
			
			<div class="detail-actions">
				<button type="button" id="bookmarkBtn" class="btn btn-outline bookmark-btn">📑ブックマーク</button>
			</div>
		</div>
		
		<div class="detail-image">
			<img src="${pageContext.request.contextPath}/images/detail_sample_img.png" alt="" class="detail-thumb">
		</div>
		
		<div class="detail-content">
			샘플 게시글 내용😉
		</div>
		
		<div class="confirm-btn-wrap">
			<button type="button" id="confirmBtn" class="btn btn-outline confirm-btn">確認</button>
		</div>

		<div class="detail-actions-right">
			<a href="" class="btn btn-outline">修正</a>
			<a href="" class="btn btn-outline" onclick="return confirm('삭제 하시겠습니까?')">削除</a>
			<a href="list.jsp" class="btn btn-primary">一覧</a>
		</div>
	</div>
		
		<!-- 確認状況(投稿者のみ確認可能) -->
		<div class="confirm-status">
			<h3>確認状況</h3>
			<p>2 / 4명 확인</p>
			
			<ul>
			    <li><span>山田一郎</span><span class="confirmed">確認済み</span></li>
			    <li><span>前田次郎</span><span class="confirmed">確認済み</span></li>
			    <li><span>鈴木三郎</span><span class="unconfirmed">未確認</span></li>
			    <li><span>小野四郎</span><span class="unconfirmed">未確認</span></li>
			</ul>
		</div>
		
	</div>
		
	</main>



<%@ include file="/include/footer.jsp"%>
<script src="${pageContext.request.contextPath}/js/detail.js"></script>
