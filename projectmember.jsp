	<%@ page language="java" contentType="text/html; charset=UTF-8"
	    pageEncoding="UTF-8"%>
	<%@ taglib prefix="c" uri="jakarta.tags.core" %>
	<!DOCTYPE html>
	<head>
	<meta charset="UTF-8">
	<title>팀원관리</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/projectmember.css">
	</head>
	
	<%@include file = "/include/header.jsp" %>
	<div class="container">
		<h1>👥メンバーの連絡先・管理</h1>
			
	
<!-- 팀원 추가 폼 -->
<div class="form-card">

    <div class="form-title">新規メンバー追加</div>
    <p class="form-description">
        新しいメンバーの情報を入力してください。
    </p>

	<form action="${pageContext.request.contextPath}/projectmember/add"
      method="post" class="member-form">
    
    	<input type="hidden" name="projectId"
               value="${param.projectId}">

		<!-- ① 프로젝트 ID -->
  		  <input type="hidden" name="projectId"
           value="${param.projectId}">

        <div class="form-group">
            <label for="name">名前</label>
            <input type="text"
                   id="name"
                   name="name"
                   placeholder="名前を入力"
                   required>
        </div>

        <div class="form-group">
            <label for="role">職種・役割</label>
            <input type="text"
                   id="role"
                   name="role"
                   placeholder="例：開発者"
                   required>
        </div>

        <div class="form-group">
            <label for="email">メールアドレス</label>
            <input type="email"
                   id="email"
                   name="email"
                   placeholder="example@email.com"
                   required>
        </div>
       
        <button type="submit" class="btn-add">
            ＋ メンバー追加
        </button>

    </form>
</div>
	
		<!-- 팀원 검색창 -->
		<form action="${pageContext.request.contextPath}/projectmember/search" 
			method="get"  class="member-search-form">
    	<input type="hidden" name="projectId" value="${param.projectId}">
   		 <input type="text" name="keyword" value="${param.keyword}"
          		 placeholder="メンバー名またはID検索"  class="member-search-input">
   			<button type="submit"  class="member-search-button">検索</button>
	</form>
		
<!-- 팀원 목록 -->
<div class="member-list-title">メンバーリスト</div>

<div class="member-grid">

    <!-- 멤버가 있을 때만 반복 출력 -->
    <c:if test="${not empty memberList}">
        <c:forEach var="pm" items="${memberList}">

            <div class="member-card">

                <!-- 멤버 정보 -->
                <div class="member-info">
                    <div class="name">
                        名前: <c:out value="${pm.memberName}"/>
                    </div>

                    <span class="role">
                        職種：<c:out value="${pm.role}"/>
                    </span>

                    <div class="contact-M">
                        📧 <c:out value="${pm.email}"/>
                    </div>

                    <div class="contact-M">
                        📞 010-1234-4566
                    </div>
                </div>

                <!-- 역할 수정 버튼 및 폼 -->
                <details class="member-edit">
                    <summary class="member-edit-button">
                        ✏️ 情報を変更
                    </summary>

                    <form action="${pageContext.request.contextPath}/projectmember/update"
                          method="post"
                          class="member-edit-form">

                        <input type="hidden" name="projectId"
                               value="<c:out value='${pm.projectId}'/>">

                        <input type="hidden" name="memberId"
                               value="<c:out value='${pm.memberId}'/>">

                        <div class="form-group">
                            <label>職種・役割</label>

                            <input type="text"
                                   name="role"
                                   value="<c:out value='${pm.role}'/>"
                                   required>
                        </div>

                        <div class="member-edit-email">
                            📧 <c:out value="${pm.email}"/>
                        </div>

                        <button type="submit" class="btn-add">
                            役割を変更
                        </button>
                    </form>
                </details>

                <!-- 프로젝트에서 팀원 삭제 -->
                <div class="card-actions">
                    <form action="${pageContext.request.contextPath}/projectmember/delete"
                          method="post"
                          onsubmit="return confirm('このプロジェクトで該当のチームメンバーを削除しますか？');">

                        <input type="hidden" name="projectId"
                               value="<c:out value='${pm.projectId}'/>">

                        <input type="hidden" name="memberId"
                               value="<c:out value='${pm.memberId}'/>">

                        <button type="submit" class="btn-delete">
                            メンバー削除
                        </button>
                    </form>
                </div>

            </div>
            <!-- member-card 끝 -->

        </c:forEach>
    </c:if>

    <!-- 멤버가 없을 때 -->
    <c:if test="${empty memberList}">
        <p class="empty-message">登録されたメンバーはいません。</p>
    </c:if>

</div>
<!-- member-grid 끝 -->
	       
	
	
	
	
