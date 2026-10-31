<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ include file="/include/header.jsp"%>

<main class="join-wrap">

	<h2 class="join-page-title">会員登録</h2>
	<form id="joinForm" class="join-form"
		  action="${pageContext.request.contextPath}/worklogMember/join"
		  method="post">
		  
		  <div class="form-row">
                <label for="id">ID</label>
                <div class="id-check-group">
                    <input type="text" id="id" name="memberId" placeholder="英文/数字 4~12자" class="form-input">
                    <button type="button" id="idCheckBtn" class="btn btn-outline id-check-btn">重複チェック</button>
                </div>
                <p id="idCheckMsg" class="field-msg"></p>
            </div>

            <div class="form-row">
                <label for="pwd">パスワード</label>
                <input type="password" id="pwd" name="pwd" placeholder="パスワードを入力してください" class="form-input">
            </div>

            <div class="form-row">
                <label for="pwdCheck">パスワード確認</label>
                <input type="password" id="pwdCheck" placeholder="もう一度パスワードを入力してください" class="form-input">
                <p id="pwdCheckMsg" class="field-msg"></p>
            </div>

            <div class="form-row">
                <label for="nickname">お名前</label>
                <input type="text" id="nickname" name="memberName" placeholder="お名前を入力してください" class="form-input">
            </div>

            <div class="form-row">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" placeholder="example@email.com" class="form-input">
            </div>

            <div class="join-btns">
                <a href="${pageContext.request.contextPath}/" class="btn btn-outline">キャンセル</a>
                <button type="submit" class="btn btn-primary">登録する</button>
            </div>
		  
	</form>

</main>

<%@ include file="/include/footer.jsp"%>
<script>
    const contextPath = "${pageContext.request.contextPath}";
</script>
<script src="${pageContext.request.contextPath}/js/join.js"></script>