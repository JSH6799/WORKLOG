<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ include file="/include/header.jsp"%>

<main class="mypage-wrap">
	<div class="mypage-layout">

	<aside class="mypage-sidebar">
		<section class="mypage-info">
			<h3 class="mypage-info-title">会員情報</h3>
			<div class="mypage-info-list">
				<div class="mypage-info-item">
					<span class="mypage-info-label">名前</span>
					<span class="mypage-info-value">
						${memberInfo.memberName}
					</span>
				</div>
				<div class="mypage-info-item">
					<span class="mypage-info-label">ID</span>
					<span class="mypage-info-value">
						${memberInfo.memberId}
					</span>
				</div>
				<div class="mypage-info-item">
					<span class="mypage-info-label">Email</span>
					<span class="mypage-info-value">
						${memberInfo.email}
					</span>
				</div>
			</div>
			<button type="button" class="mypage-edit-btn" id="memberEditBtn">
				情報編集
			</button>
		</section>
		
		<section class="project-invitations">
		    <h3 class="project-invitations-title">プロジェクト招待</h3>
		    <C:choose>
		        <C:when test="${not empty invitationList}">
		            <button type="button" class="project-invitation-btn invitation-active" data-bs-toggle="modal" data-bs-target="#invitationModal">
		                プロジェクト招待あり
		            </button>
		        </C:when>
		        <C:otherwise>
		            <button type="button" class="project-invitation-btn invitation-disabled" disabled>
		                プロジェクト招待なし
		            </button>
		        </C:otherwise>
		    </C:choose>
		</section>
		
		<section class="joined-projects">
			<h3 class="joined-projects-title">参加プロジェクト</h3>
			<ul class="joined-project-list">
				<C:forEach var="pl" items="${mypageUnreadList}">
				<li>
			        <a href="${pageContext.request.contextPath}/project_board/list?projectId=${pl.projectId}" class="joined-project-item">${pl.projectName}</a>
			    </li>
				</C:forEach>
			</ul>
		</section>
	</aside>

	<div class="modal fade" id="invitationModal" tabindex="-1" aria-labelledby="invitationModalLabel" aria-hidden="true">
	    <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable">
	        <div class="modal-content invitation-modal">
	            <div class="modal-header">
	                <h5 class="modal-title" id="invitationModalLabel">プロジェクト招待一覧</h5>
	                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
	            </div>
	
	            <div class="modal-body">
	                <p class="invitation-description">受信したプロジェクト招待を確認できます。</p>
	
	                <div class="invitation-list">
					    <C:forEach var="inv" items="${invitationList}">
      						  <div class="invitation-item">
           					 <h6 class="invitation-project-name"> ${inv.projectId} </h6>
         				  	 <p class="invitation-meta"> 招待者：${inv.inviterId}</p>
           					 <p class="invitation-meta">招待日：${inv.invitedDate}</p>
           				 <div class="invitation-actions">
 						   <!-- 수락 -->
				<form method="post"action="${pageContext.request.contextPath}/projectmember/invitation/accept">
        		<input type="hidden" name="invitationId"value="${inv.invitationId}">
        		<input type="hidden" name="projectId" value="${inv.projectId}">
		        <button type="submit"class="btn btn-primary invitation-accept-btn">承認 </button>
   			 </form>
   				 <!-- 거절 -->
    			<form method="post"action="${pageContext.request.contextPath}/projectmember/invitation/reject">
        <input type="hidden" name="invitationId"value="${inv.invitationId}">
        <button type="submit"class="btn btn-outline-secondary invitation-reject-btn">
            拒否
        </button>
   		 </form>
		</div>
		</div>
    	</C:forEach>	                       
	                </div>
	            </div>
	            <div class="modal-footer">
	                <button type="button" class="btn btn-outline-secondary"data-bs-dismiss="modal">閉じる</button>
	            </div>
	        </div>
	    </div>
	</div>

	
	<section class="mypage-content">
	<div class="mypage-tabs">
	    <button type="button" class="mypage-tab active" data-tab="myPosts">自分の投稿</button>
	    <button type="button" class="mypage-tab" data-tab="unconfirmedPosts">未確認投稿</button>
	</div>
	
	<div id="myPosts" class="mypage-panel">
	    <h3 class="my-board-title">自分の投稿</h3>
	    <p class="my-board-desc">自分が作成した投稿を確認できます。</p>
	
	    <!-- 전체 / 프로젝트별 필터 -->
	    <div class="my-board-filter">
	        <button type="button" class="my-board-filter-btn active" data-project="all">全プロジェクト</button>
	
	        <C:forEach var="p" items="${mypageUnreadList}">
	            <button type="button" class="my-board-filter-btn" data-project="${p.projectId}">
	                <C:out value="${p.projectName}"/>
	            </button>
	        </C:forEach>
	    </div>
	
	    <!-- 내가 쓴 게시글 목록 -->
	    <ul class="my-board-list">
	        <C:choose>
	            <C:when test="${not empty myBoardList}">
	                <C:forEach var="b" items="${myBoardList}">
	                    <li class="my-board-item" data-project="${b.projectId}">
	                        <a href="${pageContext.request.contextPath}/project_board/view?bno=${b.bno}" class="my-board-post">
	                            <div class="post-title">${b.title}</div>
	                            <div class="post-info">${b.projectId} · ${b.writeDate}</div>
	                        </a>
	                    </li>
	                </C:forEach>
	            </C:when>
	
	            <C:otherwise>
	                <li class="my-board-empty">作成した投稿はありません。</li>
	            </C:otherwise>
	        </C:choose>
	    </ul>
	</div>
	
    <div id="unconfirmedPosts" class="mypage-panel" hidden>
	    <section class="unconfirmed-section">
	        <h3 class="unconfirmed-title">未確認投稿</h3>
	        <p class="unconfirmed-desc">確認が必要な投稿をプロジェクト別に確認できます。</p>
	
	        <!-- 프로젝트 1 -->
			<C:forEach var="pl" items="${mypageUnreadList}">
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
	    </div>
	</section>
	</div>
	
	<div class="modal fade" id="passwordConfirmModal" tabindex="-1" aria-labelledby="passwordConfirmTitle" aria-hidden="true">
	    <div class="modal-dialog modal-dialog-centered">
	        <div class="modal-content password-confirm-modal">
	        
	            <div class="modal-header">
	                <h5 class="modal-title" id="passwordConfirmTitle">本人確認</h5>
	                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
	            </div>
	
	            <form id="passwordConfirmForm">
	                <div class="modal-body">
	                    <p class="password-confirm-desc">会員情報編集のため現在のパスワードを入力してください。</p>
	
	                    <label for="currentPassword" class="form-label">現在のパスワード</label>
	                    <input type="password" class="form-control" id="currentPassword" name="currentPassword" autocomplete="current-password" required>
	
	                    <p id="passwordConfirmError" class="password-confirm-error" hidden></p>
	                </div>
	
	                <div class="modal-footer">
	                    <button type="submit" class="btn btn-primary">確認</button>
	                    <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">キャンセル</button>
	                </div>
	            </form>
	        </div>
	    </div>
	</div>
	
	<div class="modal fade" id="memberEditModal" tabindex="-1" aria-labelledby="memberEditModalTitle" aria-hidden="true">
	    <div class="modal-dialog modal-dialog-centered">
	        <div class="modal-content member-edit-modal">
	            <div class="modal-header">
	                <h5 class="modal-title" id="memberEditModalTitle">会員情報編集</h5>
	                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
	            </div>
	
	            <form id="memberEditForm">
	                <div class="modal-body">
	                    <div class="mb-3">
	                        <label for="editMemberId" class="form-label">ID</label>
	                        <input type="text" class="form-control" id="editMemberId" readonly>
	                    </div>
	
	                    <div class="mb-3">
	                        <label for="editMemberName" class="form-label">名前</label>
	                        <input type="text" class="form-control" id="editMemberName" name="memberName" maxlength="30" required>
	                    </div>
	
	                    <div class="mb-3">
	                        <label for="editMemberEmail" class="form-label">メールアドレス</label>
	                        <input type="email" class="form-control" id="editMemberEmail" name="email" required>
	                    </div>
	
	                    <hr>
	                    <p class="text-muted">パスワードを変更する場合のみ入力してください。</p>
	
	                    <div class="mb-3">
	                        <label for="editNewPassword" class="form-label">新しいパスワード</label>
	                        <input type="password" class="form-control" id="editNewPassword" name="newPassword" autocomplete="new-password">
	                    </div>
	
	                    <div class="mb-3">
	                        <label for="editConfirmPassword" class="form-label">新しいパスワード（確認）</label>
							<input type="password" class="form-control" id="editConfirmPassword" name="confirmPassword" autocomplete="new-password">
							<div id="passwordMatchMessage"></div>
	                    </div>
	
	                    <p id="memberEditError" class="text-danger" hidden></p>
	                </div>
	
	                <div class="modal-footer">
	                    <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">キャンセル</button>
	                    <button type="submit" class="btn btn-primary">保存</button>
	                </div>
	            </form>
	
	        </div>
	    </div>
	</div>
	
</main>

<%@ include file="/include/footer.jsp"%>
<script src="${pageContext.request.contextPath}/js/mypage.js"></script>