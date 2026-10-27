<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri="jakarta.tags.core" prefix="C"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<html>
	<head>
		<title>WORKLOG ワークログ</title>
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width, initial-scale=1">
		
		<!-- Bootstrap CSS -->
		<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css"
      		  rel="stylesheet"
    		  integrity="sha384-sRIl4kxILFvY47J16cr9ZwB07vP4J8+LH7qKQnuqkuIAvNWLzeN8tE5YBujZqJLB"
     		  crossorigin="anonymous">
		<link href="${pageContext.request.contextPath}/css/common.css" rel="stylesheet">
		<link href="${pageContext.request.contextPath}/css/mypage.css" rel="stylesheet">
		<link href="${pageContext.request.contextPath}/css/list.css" rel="stylesheet">
		<link href="${pageContext.request.contextPath}/css/write.css" rel="stylesheet">
		<link href="${pageContext.request.contextPath}/css/detail.css" rel="stylesheet">
		<link href="${pageContext.request.contextPath}/css/bookmark.css" rel="stylesheet">
		<link href="${pageContext.request.contextPath}/css/calendarMy.css" rel="stylesheet">
		<link href="${pageContext.request.contextPath}/css/member.css" rel="stylesheet">
		<link href="${pageContext.request.contextPath}/css/join.css" rel="stylesheet">
		<link href="${pageContext.request.contextPath}/css/login.css" rel="stylesheet">
		<link href="${pageContext.request.contextPath}/css/addproject.css" rel="stylesheet">
		<link href="${pageContext.request.contextPath}/css/index.css" rel="stylesheet">
		<link href="${pageContext.request.contextPath}/css/main.css" rel="stylesheet">
		<link href="${pageContext.request.contextPath}/css/manageproject.css" rel="stylesheet">
		<link href="${pageContext.request.contextPath}/css/proandmem.css" rel="stylesheet">
		<link href="${pageContext.request.contextPath}/css/project.css" rel="stylesheet">
		<link href="${pageContext.request.contextPath}/css/projectmember.css" rel="stylesheet">
		<link href="${pageContext.request.contextPath}/css/TeamProjec.css" rel="stylesheet">
	</head>
	<body>
<header class="site-header">
	<div class="header-inline">
		<h1 class="logo"><a href="${pageContext.request.contextPath}">WORKLOG</a></h1>
		<nav class="gnb">
			<ul>
				<li><a href="${pageContext.request.contextPath}/calendar/calendar.jsp">カレンダー</a></li>
				<li class="dropdown project-dropdown">
				    <a class="nav-link dropdown-toggle project-dropdown-toggle" 
				       href="${pageContext.request.contextPath}/project/list" role="button" aria-expanded="false">
				        プロジェクト
				    </a>
				    <ul class="dropdown-menu">
				    <C:choose>
				    <C:when test="${empty sessionScope.loginMember}">
				    	<li>
				    		<span class="dropdown-item-text">
				    			ログインが必要です
				    		</span>
				    	</li>
				    	<li>
				    		<a class="dropdown-item" href="${pageContext.request.contextPath}/worklogMember/login">
				    			ログイン
				    		</a>
				    	</li>
				    </C:when>
				    <C:otherwise>
					<C:if test="${empty proList}">
						<li>
							<span class="dropdown-item-text">
								参加中のプロジェクトがありません
							</span>
						</li>
					</C:if>
					<C:forEach var="p" items="${proList}">
				        <li>
				            <a class="dropdown-item"
				               href="${pageContext.request.contextPath}/project_board/list?projectId=${p.projectId}">
				                ${p.projectName}
				            </a>
				        </li>
					</C:forEach>
				    </C:otherwise>
				    </C:choose>
				    </ul>
				</li>
				<li><a href="${pageContext.request.contextPath}/bookmark/list">ブックマーク</a></li>
			</ul>
		</nav>
		<div class="member-menu">
			<C:choose>
				<C:when test="${empty sessionScope.loginMember}">
					<a href="${pageContext.request.contextPath}/worklogMember/login">ログイン</a>
					<a href="${pageContext.request.contextPath}/worklogMember/join">会員登録</a>
				</C:when>
				<C:otherwise>
					<span>${sessionScope.loginMember.memberName} 様</span>
					<a href="${pageContext.request.contextPath}/worklogMember/logout">ログアウト</a>
					<a href="${pageContext.request.contextPath}/worklogMember/mypage">マイページ</a>
				</C:otherwise>
			</C:choose>
		</div>
	</div>
	
	
</header>