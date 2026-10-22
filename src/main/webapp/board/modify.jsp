<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ include file="/include/header.jsp"%>

<main class="write-wrap">
	<h2 class="write-page-title">修正する</h2>
	<form id="modifyForm" class="write-form"
		  method="post"
		  enctype="multipart/form-data"
		  action="">
		  <input type="hidden" name="bno" value="">
		  
		  <div class="form-row">
		  	<label for="writer">投稿者</label>
		  	<p class="write-author">
		  		<input type="text" id="writer" class="form-input" value="" readonly>
		  	</p>
		  </div>
	
	
		  <div class=form-row>
		  	<label for="title">タイトル</label>
		  	<input type="text" id="title" name="title" class="form-input" value="">
		  </div>
		  
		  <div class=form-row>
		  	<label>確認要否<span class="form-option">(選択)</span></label>
		  	<input type="checkbox" id="confirm" name="confirm" value="Y">
		  </div>
		  
		  <div class="form-row">
		  	<label>画像添付<span class="form-option">(選択)</span></label>
		  	<div class="image-upload-box">
		  		<img id="imgPreview" src="${pageContext.request.contextPath}/images/defalut_img.jpg" alt="画像プレビュー" class="img-preview">
		  		<div class="image-upload-btns">
		  			<label for="imgFile" class="btn btn-outline file-label">画像選択</label>
		  			<input type="file" id="imgFile" name="imgFile" accept="image/*" class="file-input">
		  			<button type="button" id="imgRemoveBtn" class="btn btn-outline">画像削除</button>
		  		</div>
		  	</div>
		  </div>
		  
		  
		  <div class="form-row">
		  	<label for="content">内容</label>
		  	<textarea rows="12" id="content" name="content" class="form-textarea"></textarea>
		  </div>
		  
		  <div class="write-btns">
		  	<a href="${pageContext.request.contextPath}/board/list.jsp" class="btn btn-outline">キャンセル</a>
		  	<button type="submit" class="btn btn-primary">修正完了</button>
		  </div>
		  
	</form>
</main>


<%@ include file="/include/footer.jsp"%>
<script src="${pageContext.request.contextPath}/js/write.js"></script>
