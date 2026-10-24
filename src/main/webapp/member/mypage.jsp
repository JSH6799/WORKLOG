<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ include file="/include/header.jsp"%>

<main class="mypage-wrap">
    <section class="unconfirmed-section">
        <h3 class="unconfirmed-title">未確認投稿</h3>
        <p class="unconfirmed-desc">確認が必要な投稿をプロジェクト別に確認できます。</p>

        <!-- 프로젝트 1 -->
		<C:forEach var="pl" items="${projectList}">
        <div class="unconfirmed-project">
        <C:choose>
	        <C:when test="${pl.unreadCount gt 0}">
	            <button type="button" class="project-toggle"
	                    data-bs-toggle="collapse" data-bs-target="#${pl.projectId}"
	                    aria-expanded="false" aria-controls="${pl.projectId}">
	
	                <span class="project-info">
	                    <span class="project-name">${pl.projectName}</span>
	                    <span class="project-count">未確認 ${pl.unreadCount}件</span>
	                </span>
	
	                <span class="toggle-label">▶ 開く</span>
	            </button>
	        </C:when>
	        <C:otherwise>
	        	<button type="button" class="project-toggle" disabled>
	        		<span class="project-info">
	                    <span class="project-name">${pl.projectName}</span>
	                    <span class="project-count">未確認 ${pl.unreadCount}件</span>
	                </span>
	        	</button>
	        </C:otherwise>
        </C:choose>

            <div class="collapse" id="${pl.projectId}">
                <ul class="unconfirmed-list">
                <C:forEach var="ubl" items="${pl.unreadBoardList}">
                    <li>
                        <a href="${pageContext.request.contextPath}/project_board/view?bno=${ubl.bno}" class="unconfirmed-post">
                            <div class="post-title">${ubl.title}</div>
                            <div class="post-info">${ubl.writer} · ${ubl.writeDate}</div>
                        </a>
                    </li>
                </C:forEach>
                </ul>
            </div>
		</div>
		</C:forEach>

    </section>
</main>

<%@ include file="/include/footer.jsp"%>
<script src="${pageContext.request.contextPath}/js/mypage.js"></script>