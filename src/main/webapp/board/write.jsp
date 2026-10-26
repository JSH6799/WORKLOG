<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ include file="/include/header.jsp"%>

<main class="write-wrap">
	<h2 class="write-page-title">投稿する</h2>
	<form id="writeForm" class="write-form"
		  method="post"
		  enctype="multipart/form-data"
		  action="${pageContext.request.contextPath}/project_board/write">
		  
		  <div class="form-row">
		  	<label for="writer">投稿者</label>
		  	<p class="write-author">
		  		<input type="text" id="writer" class="form-input" value="${sessionScope.loginMember.memberName}" readonly>
		  	</p>
		  </div>
	
		  <div class="form-row">
		  	<label for="project-id">プロジェクト</label>
		  	<select id="project-id" name="project_id" class="project-select">
		  		<option value="pno_4"${selectedProjectId == 'pno_1' ? 'selected' : ''}>仮プロジェクト名</option>
		  	</select>
		  </div>
	
	
		  <div class=form-row>
		  	<label for="title">タイトル</label>
		  	<input type="text" id="title" name="title" placeholder="タイトルを入力してください" class="form-input">
		  </div>
		  
		  <div class=form-row>
		  	<label>確認要否<span class="form-option">(選択)</span></label>
		  	<input type="checkbox" id="confirm" name="check_required" value="Y">
		  </div>
		  
		  <div class="form-row">
		  	<label>画像添付<span class="form-option">(選択)</span></label>
		  	<div class="image-upload-box">
		  		<img id="imgPreview" src="${pageContext.request.contextPath}/images/default_img.jpg" alt="画像プレビュー" class="img-preview">
		  		<div class="image-upload-btns">
		  			<label for="imgFile" class="btn btn-outline file-label">画像選択</label>
		  			<input type="file" id="imgFile" name="imgFile" accept="image/*" class="file-input">
		  			<button type="button" id="imgRemoveBtn" class="btn btn-outline">画像削除</button>
		  		</div>
		  	</div>
		  </div>
		  
		  
		  <div class="form-row">
		  	<label for="content">内容</label>
		  	<textarea rows="12" id="content" name="content" placeholder="内容を入力してください" class="form-textarea"></textarea>
		  </div>
		  
		  <div class="write-btns">
		  	<a href="${pageContext.request.contextPath}/board/list.jsp" class="btn btn-outline">キャンセル</a>
		  	<button type="submit" class="btn btn-primary">投稿</button>
		  </div>
		  
	</form>
</main>


<%@ include file="/include/footer.jsp"%>
<script src="${pageContext.request.contextPath}/js/write.js"></script>
