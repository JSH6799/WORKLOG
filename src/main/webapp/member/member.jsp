<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
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
		<div class="member-card">
		<div class="member-info">
		<div class="name">名前: 김철수</div>
		<span class="role">職種：開発者</span>
		<div class="contact-M">📧 : heodksf@exsample.com</div>
		<div class="contact-M">📞: 010-1234-4566</div>
		</div>
		<div class="card-actions">
		<!-- 삭제시 ID값을 전달하여 삭제요청으로 -->
		<form action="" method="post" onsubmit="return confirm('정말 삭제하시겠습니까?');">
			<input type="hidden" name="id" value="1">
			<button type="submit" class="btn-delete">メンバー削除</button>
		</form>
		</div>
	</div>
 
 
 <!-- 수기입력 샘플링 테이터 2 -->
 	<div class="member-card">
		<div class="member-info">
			<div class="name">名前:박동길</div>
			<span class="role">職種: ui/ux　デザイナー</span>
			<div class="contact-M">📧 : nanundizaf@exsample.com</div>
			<div class="contact-M">📞: 010-1234-4366</div>
		</div>
		<div class="card-actions">
		<!-- 삭제시 ID값을 전달하여 삭제요청으로 -->
			<form action="" method="post" onsubmit="return confirm('정말 삭제하시겠습니까?');">
				<input type="hidden" name="id" value="2">
				<button type="submit" class="btn-delete">メンバー削除</button>
			</form>
		</div>
	</div>
 <!-- 수기입력 샘플링 테이터 3 -->
	
	<div class="member-card">
		<div class="member-info">
		<div class="name">名前:함영희</div>
		<span class="role">職種：:エンジニア</span>
		<div class="contact-M">📧 : catmomeda@exsample.com</div>
		<div class="contact-M">📞 : 010-3434-4566</div>
		</div>
		<div class="card-actions">
		<!-- 삭제시 ID값을 전달하여 삭제요청으로 -->
		<form action="" method="post" onsubmit="return confirm('정말 삭제하시겠습니까?');">
			<input type="hidden" name="id" value="3">
			<button type="submit" class="btn-delete">メンバー削除</button>
		</form>
		</div>
	</div>
	 <!-- 수기입력 샘플링 테이터4 -->
	
	<div class="member-card">
		<div class="member-info">
		<div class="name"> 名前: 임성현</div>
		<span class="role">職種：:可愛い　トマト</span>
		<div class="contact-M">📧: cuteboyseyof@exsample.com</div>
		<div class="contact-M">📞: 010-5647-4566</div>
		</div>
		<div class="card-actions">
		<!-- 삭제시 ID값을 전달하여 삭제요청으로 -->
		<form action="" method="post" onsubmit="return confirm('정말 삭제하시겠습니까?');">
			<input type="hidden" name="id" value="4">
			<button type="submit" class="btn-delete">メンバー削除</button>
		</form>
		</div>
	</div>
	
	</div>
</div>
