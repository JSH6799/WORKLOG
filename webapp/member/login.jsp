<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ include file="/include/header.jsp"%>

<main class="login-wrap">

	<h2 class="login-page-title">ログイン</h2>
        
	<c:if test="${not empty loginFail}">
    	<p class="login-error-msg">${loginFail}</p>
	</c:if>
		
	<form id="loginForm" class="login-form"
              action="" method="post">

		<div class="form-row">
			<label for="id">ID</label>
			<input type="text" id="id" name="memberId" placeholder="IDを入力してください" class="form-input">
		</div>

		<div class="form-row">
			<label for="pwd">パスワード</label>
			<input type="password" id="pwd" name="pwd" placeholder="パスワードを入力してください" class="form-input">
		</div>

		<button type="submit" class="btn btn-primary login-submit-btn">ログイン</button>

		<p class="login-footer-link">
			まだアカウントをお持ちでないですか？
			<a href="${pageContext.request.contextPath}/worklogMember/join">会員登録</a>
		</p>
	</form>
	
</main>

<%@ include file="/include/footer.jsp"%>
<script src="${pageContext.request.contextPath}/js/login.js"></script>