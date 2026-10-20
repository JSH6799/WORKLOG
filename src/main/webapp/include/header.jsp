<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri="jakarta.tags.core" prefix="C"%>

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
		<link href="${pageContext.request.contextPath}/css/mystyle.css" rel="stylesheet">
		<link href="${pageContext.request.contextPath}/css/list.css" rel="stylesheet">
		<link href="${pageContext.request.contextPath}/css/write.css" rel="stylesheet">
		<link href="${pageContext.request.contextPath}/css/detail.css" rel="stylesheet">
		<link href="${pageContext.request.contextPath}/css/bookmark.css" rel="stylesheet">
		<link href="${pageContext.request.contextPath}/css/calenderMy.css" rel="stylesheet">
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
				       href="#"
				       role="button"
				       aria-expanded="false">
				        プロジェクト
				    </a>
				    <ul class="dropdown-menu">
						<!-- 아래는 DB 연동 후 c:foreach 태그 활용하여 변경 -->
				        <li>
				            <a class="dropdown-item"
				               href="${pageContext.request.contextPath}/board/list.jsp?projectId=1">
				                プロジェクト A
				            </a>
				        </li>
				        <li>
				            <a class="dropdown-item"
				               href="${pageContext.request.contextPath}/board/list.jsp?projectId=2">
				                プロジェクト B
				            </a>
				        </li>
				        <li>
				            <a class="dropdown-item"
				               href="${pageContext.request.contextPath}/board/list.jsp?projectId=3">
				                プロジェクト C
				            </a>
				        </li>
				    </ul>
				</li>
				<li><a href="${pageContext.request.contextPath}/board/bookmark.jsp">ブックマーク</a></li>
			</ul>
		</nav>
		<div class="member-menu">
			<a href="">ログイン</a>
			<a href="">会員登録</a>
			<!-- 로그인 세션 생성 후 수정 -->
		</div>
	</div>
	
	
</header>