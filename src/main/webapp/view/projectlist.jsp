<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/include/header.jsp"%>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/proandmem.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/project.css">

<main class="project-wrap">
    <div id="project-content">

        <header class="page-head">
            <div>
                <h1 class="page-title">プロジェクトリスト</h1>
                <p class="page-desc">参加中のプロジェクトを確認、管理し、新たなプロジェクトを作ります。</p>
            </div>
            <a class="btn btn-primary" href="${pageContext.request.contextPath}/project/add">新しいプロジェクトを作る</a>
        </header>

        <div class="panel">
            <div class="table-wrap">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th scope="col">プロジェクト名</th>
                            <th scope="col">チーム名</th>
                            <th scope="col">期間</th>
                            <th scope="col">管理</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty projectList}">
                                <tr>
                                    <td colspan="4" style="text-align: center; padding: 20px;">登録されたプロジェクトが存在しません。</td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="p" items="${projectList}">
                                    <tr>
                                        <td>
                                            <strong><a href="${pageContext.request.contextPath}/project/manage?projectId=${p.projectId}">${p.projectName}</a></strong>
                                            <p class="field-hint" style="margin: 4px 0 0 0;">${p.description}</p>
                                        </td>
                                        <td>${p.teamName}</td>
                                        <td class="nowrap">${p.startDate} ~ ${p.endDate}</td>
                                        <td>
                                            <a class="btn btn-small" href="${pageContext.request.contextPath}/project/manage?projectId=${p.projectId}">管理</a>
                                            <a class="btn btn-small" href="${pageContext.request.contextPath}/project/members?projectId=${p.projectId}">メンバー</a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>

    </div>
</main>

<%@ include file="/include/footer.jsp"%>