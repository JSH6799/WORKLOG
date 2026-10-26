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
		<C:forEach var="pm" items="${memberList}">
		<div class="member-grid">
			<!-- jstl을 사용해 백엔드에서 전달받은 memeberlist 반복출력하기 -->
			<div class="member-card">
			<div class="member-info">
			<div class="name">名前: <c:out value="${pm.memberName}"/></div>
			<span class="role">職種：<c:out value="${pm.role}"/></span>
			<div class="contact-M">📧 : <c:out value="${pm.email}"></c:out></div>
			<div class="contact-M">📞: 010-1234-4566</div>
			</div>
			</div>
	 
			  <!-- 역할 수정 폼 -->
	                <form action="${pageContext.request.contextPath}/projectmember/update"
	                      method="post"
	                      class="member-form">
	
	              <input type="hidden" name="projectId" value="<c:out value='${pm.projectId}'/>">
	              <input type="hidden" name="memberId" value="<c:out value='${pm.memberId}'/>">
	
	                    <div class="form-group">
	                        <label>職種：</label>
	                        <input type="text"
	                               name="role"
	                               value="<c:out value='${pm.role}'/>"
	                               required>
	                    </div>
	
	                    <div class="contact-M">
	                        📧 :
	                        <c:out value="${pm.email}"/>
	                    <button type="submit" class="btn-add">
	                        役割を変更
	                    </button>
	                </form>
	            </div>
		
	            <div class="card-actions">
	                <!-- 프로젝트에서 팀원 삭제 -->
	                <form action="${pageContext.request.contextPath}/projectmember/delete"
	                      method="post"
	                      onsubmit="return confirm('이 프로젝트에서 해당 팀원을 삭제하시겠습니까?');">
	
	                    <input type="hidden"
	                           name="projectId"
	                           value="<c:out value='${pm.projectId}'/>">
	
	                    <input type="hidden"
	                           name="memberId"
	                           value="<c:out value='${pm.memberId}'/>">
	
	                    <button type="submit" class="btn-delete">
	                        メンバー削除
	                    </button>
	                </form>
	            </div>
	        </div>
	 </C:forEach>
	  <!-- 팀원이 없을 때 -->
	    <c:if test="${empty memberList}">
	        <p>登録されたメンバーはいません。</p>
	    </c:if>
	
			<div class="card-actions">
			<!-- 삭제시 ID값을 전달하여 삭제요청으로 -->
			<form action="" method="post" onsubmit="return confirm('정말 삭제하시겠습니까?');">
				<input type="hidden" name="memberId" value="<c:out value='${pm.memberId}' />">
				<button type="submit" class="btn-delete">メンバー削除</button>
			</form>
			</div>
	
	
	
