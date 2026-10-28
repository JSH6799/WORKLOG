
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>받은 프로젝트 초대</title>
<style>
    body {
        font-family: sans-serif;
        margin: 30px;
    }

    table {
        width: 100%;
        border-collapse: collapse;
        margin-top: 20px;
    }

    th, td {
        border: 1px solid #ddd;
        padding: 12px;
        text-align: center;
    }

    th {
        background-color: #f2f2f2;
    }

    button {
        padding: 7px 14px;
        margin: 2px;
        cursor: pointer;
    }

    .empty {
        margin-top: 20px;
        color: #777;
    }

    .error {
        color: #d00;
    }
</style>
</head>
<body>

<h2>受けたプロジェクト招待</h2>

<c:if test="${not empty errorMessage}">
    <p class="error"><c:out value="${errorMessage}" /></p>
</c:if>

<c:choose>
    <c:when test="${empty invitationList}">
        <p class="empty">受け取った待機招待はありません</p>
    </c:when>

    <c:otherwise>
        <table>
            <thead>
                <tr>
                    <th>プロジェクトID</th>
                    <th>招待した会員</th>
                    <th>役割</th>
                    <th>招待日</th>
                    <th>処理</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="inv" items="${invitationList}">
                    <tr>
                        <td><c:out value="${inv.projectId}" /></td>
                        <td><c:out value="${inv.inviterId}" /></td>
                        <td><c:out value="${inv.role}" /></td>
                        <td><c:out value="${inv.invitedDate}" /></td>
                        <td>
                            <form method="post"
                                  action="${pageContext.request.contextPath}/projectmember/invitation/accept"
                                  style="display:inline;">
                                <input type="hidden" name="invitationId"
                                       value="<c:out value='${inv.invitationId}' />">
                                <button type="submit">受諾</button>
                            </form>

                            <form method="post"
                                  action="${pageContext.request.contextPath}/projectmember/invitation/reject"
                                  style="display:inline;">
                                <input type="hidden" name="invitationId"
                                       value="<c:out value='${inv.invitationId}' />">
                                <button type="submit">拒否</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:otherwise>
</c:choose>

</body>
</html>
