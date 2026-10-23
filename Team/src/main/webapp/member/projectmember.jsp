<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<head>
<meta charset="UTF-8">
<title>팀원관리</title>
<link rel="stylesheet" href="css/member.css" type="text/css">
</head>

<%@include file = "/include/header.jsp" %>
<div class="container">
	<h1>👥チームメンバーの連絡先・管理</h1>
		
		<!-- 팀원 추가 폼 -->
	<div class="form-card">
		<div class="form-title">新規メンバー追加</div>
		<form action="" method="post" class="member-form">
			<div class="form-group">
				<input type="text"  name="name" placeholder="名前" required>
			</div>
			<div class="form-group">
				<input type="text"  name="role" placeholder="職種業務" required>
			</div>
			<div class="form-group">
				<input type="text"  name="email" placeholder="メール" required>
			</div>
			<div class="form-group">
				<input type="text"  name="phone" placeholder="連絡先" required>
			</div>
			<button type="submit" class="btn-add">メンバー追加</button>
		</form>
	</div>
	
	<!-- 팀원 목록 -->
	<div class="member-list-title">メンバーリスト</div>
	<div class="member-grid">
		<!-- jstl을 사용해 백엔드에서 전달받은 memeberlist 반복출력하기 -->
	<C:forEach var="pm" items="${memberList}">
		<div class="member-card">
		<div class="member-info">
		<div class="name">名前: <c:out value="${pm.memberName}"/></div>
		<span class="role">職種：<c:out value="${pm.role}"/></span>
		<div class="contact-M">📧 : <c:out value="${pm.email}"></c:out></div>
		<div class="contact-M">📞: 010-1234-4566</div>
		</div>
		<div class="card-actions">
		<!-- 삭제시 ID값을 전달하여 삭제요청으로 -->
		<form action="" method="post" onsubmit="return confirm('정말 삭제하시겠습니까?');">
			<input type="hidden" name="memberId" value="<c:out value='${pm.memberId}' />">
			<button type="submit" class="btn-delete">メンバー削除</button>
		</form>
		</div>
	</div>
 </C:forEach>
	</div>
</div>
