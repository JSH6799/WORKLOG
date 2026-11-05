<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>プロジェクト詳細</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
</head>

    <%@ include file="/include/header.jsp" %>

    <main id="project-content">
        <div class="project-detail">

            <h2>プロジェクト詳細</h2>

            <section class="project-info">
                <div class="info-row">
                    <strong>プロジェクト名</strong>
                    <span><c:out value="${proDetail.projectName}" /></span>
                </div>

                <div class="info-row">
                    <strong>チーム名</strong>
                    <span><c:out value="${proDetail.teamName}" /></span>
                </div>

                <div class="info-row">
                    <strong>プロジェクト内容</strong>
                    <p><c:out value="${proDetail.description}" /></p>
                </div>

                <div class="info-row">
                    <strong>開始日</strong>
                    <span><c:out value="${proDetail.startDate}" /></span>
                </div>

                <div class="info-row">
                    <strong>終了日</strong>
                    <span><c:out value="${proDetail.endDate}" /></span>
                </div>
                
                <div class="info-row">
                	<strong>作成者</strong>
                	<span><c:out value="${proDetail.creatorName}"/></span>
                </div>
            </section>

            <div class="detail-actions">
                <a href="${pageContext.request.contextPath}/project/list" class="btn btn-outline">プロジェクト一覧へ戻る</a>
                <a href="${pageContext.request.contextPath}/project/manage?projectId=${proDetail.projectId}" class="btn btn-primary">プロジェクト管理</a>
            </div>

        </div>
    </main>

    <%@ include file="/include/footer.jsp" %>
