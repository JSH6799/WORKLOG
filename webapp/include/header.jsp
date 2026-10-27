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
		<link href="${pageContext.request.contextPath}/css/project-dropdown.css" rel="stylesheet">
	</head>
	<body>
<header class="site-header">
	<div class="header-inline">
		<h1 class="logo"><a href="${pageContext.request.contextPath}">WORKLOG</a></h1>
		<nav class="gnb">
			<ul>
				<li><a href="${pageContext.request.contextPath}/calendar/calendar.jsp">カレンダー</a></li>
				
				<li class="dropdown project-dropdown">
				    <a class="nav-link project-dropdown-toggle" href="${pageContext.request.contextPath}/">プロジェクト</a>
				
				    <div class="project-mega-menu">
				        <div class="project-menu-left">
				            <h6 class="project-menu-heading">参加中のプロジェクト</h6>
				            <C:choose>
				                <C:when test="${empty sessionScope.loginMember}">
				                    <p class="project-menu-message">ログインが必要です</p>
				                    <a class="project-menu-login" href="${pageContext.request.contextPath}/worklogMember/login">ログイン</a>
				                </C:when>
				
				                <C:otherwise>
				                    <C:if test="${empty proList}">
				                        <p class="project-menu-message">参加中のプロジェクトがありません</p>
				                    </C:if>
				                    <C:forEach var="p" items="${proList}" varStatus="status">
				                        <button type="button" class="project-select-btn ${status.first ? 'active' : ''}" data-project-id="${p.projectId}">
				                            <span>${p.projectName}</span>
				                            <span class="project-menu-arrow">›</span>
				                        </button>
				                    </C:forEach>
				                </C:otherwise>
				            </C:choose>
				        </div>
				
				        <C:if test="${not empty sessionScope.loginMember and not empty proList}">
				            <div class="project-menu-right">
				                <C:forEach var="p" items="${proList}" varStatus="status">
				                    <div class="project-menu-panel ${status.first ? 'active' : ''}" data-project-panel="${p.projectId}">
				                        <h6 class="project-menu-heading">${p.projectName}</h6>
				
				                        <a class="project-feature-link" href="${pageContext.request.contextPath}/project/manage">
				                            <span class="project-feature-icon">▣</span>
				                            <span>プロジェクト詳細</span>
				                        </a>
				
				                        <a class="project-feature-link" href="${pageContext.request.contextPath}/main.jsp">
				                            <span class="project-feature-icon">▦</span>
				                            <span>ダッシュボード</span>
				                        </a>
				
				                        <a class="project-feature-link" href="${pageContext.request.contextPath}/project_board/list?projectId=${p.projectId}">
				                            <span class="project-feature-icon">▤</span>
				                            <span>掲示板</span>
				                        </a>
				
				                        <a class="project-feature-link" href="${pageContext.request.contextPath}/calendar/calendar.jsp">
				                            <span class="project-feature-icon">▦</span>
				                            <span>プロジェクトカレンダー</span>
				                        </a>
				                    </div>
				                </C:forEach>
				            </div>
				        </C:if>
				    </div>
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

<script src="${pageContext.request.contextPath}/js/project-dropdown.js"></script>