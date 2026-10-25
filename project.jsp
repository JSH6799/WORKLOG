<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/include/header.jsp"%>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/proandmem.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/project.css">

<main class="project-wrap">
    <div id="project-content">

        <header class="page-head">
            <div>
                <h1 class="page-title">프로젝트 목록</h1>
                <p class="page-desc">참가 중인 프로젝트를 확인하고 관리하거나 새로운 프로젝트를 생성할 수 있습니다.</p>
            </div>
            <a class="btn btn-primary" href="${pageContext.request.contextPath}/project/add">새로운 프로젝트 생성</a>
        </header>

        <div class="panel">
            <div class="table-wrap">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th scope="col">프로젝트명</th>
                            <th scope="col">팀명</th>
                            <th scope="col">기간</th>
                            <th scope="col">관리</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty projectList}">
                                <tr>
                                    <td colspan="4" style="text-align: center; padding: 20px;">등록된 프로젝트가 없습니다.</td>
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
                                            <a class="btn btn-small" href="${pageContext.request.contextPath}/project/manage?projectId=${p.projectId}">관리</a>
                                            <a class="btn btn-small" href="${pageContext.request.contextPath}/project/members?projectId=${p.projectId}">멤버</a>
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