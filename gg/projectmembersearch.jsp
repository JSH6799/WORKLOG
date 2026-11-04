
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ include file="/include/header.jsp"%>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>팀원 검색 결과</title>

<link rel="stylesheet"
    href="${pageContext.request.contextPath}/css/projectmembersearch.css">
</head>

<body>

<div class="container">

    <h2>ユーザー検索結果</h2>

    <!-- 검색어 표시 -->
    <p>
        検索語: <strong><c:out value="${keyword}" /></strong>
    </p>

    <!-- 검색창 -->
    <form action="${pageContext.request.contextPath}/projectmember/search"
          method="get">

        <input type="hidden" name="projectId"
               value="${projectId}">

        <input type="text" name="keyword"
               placeholder="ユーザー名またはID検索">

        <button type="submit">検索</button>
    </form>

    <!-- 검색 결과가 있을 때 -->
    <c:if test="${not empty memberList}">

        <div class="member-grid">

            <c:forEach var="pm" items="${memberList}">

                <div class="member-card">

                    <p>
                        会員ID:
                        <c:out value="${pm.memberId}" />
                    </p>

                    <p>
                        名前:
                        <c:out value="${pm.memberName}" />

                        <c:if test="${pm.participating eq 'Y'}">
                            (参加中)
                        </c:if>
                    </p>

                    <p>
                        メールアドレス:
                        <c:out value="${pm.email}" />
                    </p>

                    <p>
                        役割:
                        <c:out value="${pm.role}" />
                    </p>
           				<c:choose>
    <c:when test="${fn:trim(pm.invitationStatus) eq 'PENDING'}">
   		<button type="button" disabled
        style="background-color: #888888; color: white;">
    招待済み
</button>
    </c:when>
  <c:otherwise>
    <form action="${pageContext.request.contextPath}/projectmember/invitation/send"
          method="post"
          onsubmit="return confirm('このユーザーを招待しますか？');">

        <input type="hidden" name="projectId" value="${projectId}">
        <input type="hidden" name="inviteeId" value="${pm.memberId}">

        <button type="submit" class="add-btn">追加</button>
    </form>
</c:otherwise>
</c:choose>

                    <!-- 삭제 버튼 -->
                    <form action="${pageContext.request.contextPath}/projectmember/delete"
                          method="post"
                          onsubmit="return confirm('このメンバーをプロジェクトから除外しますか？');">

                        <input type="hidden" name="projectId"
                               value="${projectId}">

                        <input type="hidden" name="memberId"
                               value="${pm.memberId}">

                        <button type="submit">削除</button>

                    </form>

                </div>

            </c:forEach>

        </div>

    </c:if>

    <!-- 검색 결과가 없을 때 -->
    <c:if test="${empty memberList}">
        <p>検索結果がありません。</p>
    </c:if>

    <!-- 이전 페이지로 돌아가기 -->
    <button type="button"
            onclick="location.href='${pageContext.request.contextPath}/project/members?projectId=${projectId}'">
        チームメンバーリストに戻る
    </button>

</div>

</body>
</html>
