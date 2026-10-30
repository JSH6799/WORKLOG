<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" isELIgnored="false"%>
    
<%@ page import="java.util.List, model.ProjectMemberDTO" %>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ include file="/include/header.jsp"%>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/proandmem.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/projectmember.css">

<%
    List<ProjectMemberDTO> memberList = (List<ProjectMemberDTO>) request.getAttribute("memberList");
    String projectId = request.getParameter("projectId");
    String role = request.getParameter("role");
    if (role == null) role = "OWNER";
    boolean isOwner = "OWNER".equals(role);
    String msg = request.getParameter("msg");
%>

<main class="project-wrap">
<div id="project-content">

    <nav class="crumbs" aria-label="">現在地
        <a href="${pageContext.request.contextPath}/project/list">プロジェクトリスト</a>
        <a href="${pageContext.request.contextPath}/project/manage?projectId=<%= projectId %>">プロジェクト管理</a>
        <span>멤버 권한</span>
    </nav>

    <header class="page-head">
        <div>
            <h1 class="page-title">メンバー権限の管理</h1>
            <p class="page-desc">プロジェクトに参加中のメンバーおよび権限を確認します。</p>
        </div>
        <a class="btn" href="${pageContext.request.contextPath}/project/manage?projectId=<%= projectId %>">プロジェクト管理へ</a>
    </header>

    <% if ("roleChanged".equals(msg)) { %>
        <p class="banner banner-ok" role="status">権限を変更しました。</p>
    <% } %>
    <% if (!isOwner) { %>
        <p class="banner banner-info">メンバー権限はプロジェクトリーダーにしか変更できません。</p>
    <% } %>

    <div class="layout-2">
        <div class="panel">
            <div class="table-wrap">
                <table class="data-table">
                    <thead>
                         <tr>
                            <th scope="col">メンバー</th>
                            <th scope="col">権限</th>
                            <th scope="col">e-mail</th>
                            <% if (isOwner) { %><th scope="col">権限を変更する</th><% } %>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (memberList == null || memberList.isEmpty()) { %>
                        <tr>
                            <td colspan="4" style="text-align: center; padding: 20px;">登録されたメンバーが存在しません。</td>
                        </tr>
                        <% } else { 
                            for (int i = 0; i < memberList.size(); i++) {
                                ProjectMemberDTO m = memberList.get(i);
                                boolean rowOwner = "OWNER".equals(m.getRole());
                                String label = rowOwner ? "リーダー" : ("ADMIN".equals(m.getRole()) ? "メンバー" : "閲覧者");
                        %>
                        <tr>
                            <td>
                                <div class="member">
                                    <span class="avatar" aria-hidden="true"><%= m.getMemberName().substring(0, 1) %></span>
                                    <span class="member-text">
                                       <strong><%= m.getMemberName() %></strong>
                                       <span class="member-id"><%= m.getMemberId() %></span>
                                   </span>
                                </div>
                            </td>
                            <td><span class="badge badge-<%= m.getRole().toLowerCase() %>"><%= label %></span></td>
                            <td class="nowrap"><%= m.getEmail() %></td>
                            <% if (isOwner) { %>
                            <td>
                                <% if (rowOwner) { %>
                                    <span class="muted">変更不可能</span>
                                <% } else { %>
                                    <form class="role-form" action="${pageContext.request.contextPath}/project/members" method="get">
                                       <input type="hidden" name="projectId" value="<%= projectId %>">
                                       <input type="hidden" name="role" value="<%= role %>">
                                       <input type="hidden" name="targetMemberId" value="<%= m.getMemberId() %>">
                                       <input type="hidden" name="msg" value="roleChanged">
                                       <select name="newRole">
                                           <option value="ADMIN" <%= "ADMIN".equals(m.getRole()) ? "selected" : "" %>>メンバ</option>
                                           <option value="MEMBER" <%= "MEMBER".equals(m.getRole()) ? "selected" : "" %>>閲覧者</option>
                                       </select>
                                       <button type="submit" class="btn btn-small">変更</button>
                                    </form>
                                <% } %>
                            </td>
                            <% } %>
                        </tr>
                        <% } 
                           } %>
                    </tbody>
                </table>
            </div>
            <p class="table-foot">全体 <%= memberList != null ? memberList.size() : 0 %>名</p>
        </div>

        <aside class="panel side-panel">
            <h2 class="side-title">権限案内</h2>
            <dl class="role-legend">
                <div>
                    <dt><span class="badge badge-owner">リーダー</span></dt>
                    <dd>プロジェクト削除、メンバー権限の変更、、情報変更</dd>
                </div>
                <div>
                    <dt><span class="badge badge-admin">メンバー</span></dt>
                    <dd>プロジェクト情報変更</dd>
                </div>
                <div>
                    <dt><span class="badge badge-member">閲覧者</span></dt>
                    <dd>プロジェクト情報閲覧</dd>
                </div>
            </dl>
        </aside>
    </div>


<div class="container">
		

<!-- 프로젝트 멤버 초대 폼 -->
<div class="form-card">
    <div class="form-title">メンバー招待</div>
    <p class="form-description">
        招待したいメンバーのIDと役割を入力してください。
    </p>

    <form action="${pageContext.request.contextPath}/projectmember/invitation/send"
          method="post" class="member-form">

        <input type="hidden" name="projectId"
               value="${param.projectId}">

        <div class="form-group">
            <label for="inviteeId">招待するメンバーID</label>
            <input type="text"
                   id="inviteeId"
                   name="inviteeId"
                   placeholder="メンバーIDを入力"
                   required>
        </div>
		<div class="form-group">
            <label for="inviteeName">名前</label>
            <input type="text" id="inviteeName"
                   name="inviteeName"
                   placeholder="名前を入力"
                   required>
        </div>
      
        <div class="form-group">
            <label for="role">職種・役割</label>
            <select id="role" name="role" required>
                <option value="">役割を選択してください</option>
                <option value="MEMBER">MEMBER</option>
                <option value="MANAGER">MANAGER</option>
            </select>
        </div>

        <button type="submit" class="btn-add">
            ＋ 招待状を送る
        </button>
    </form>
</div>
	
		
<!-- 팀원 검색창 -->
<form action="${pageContext.request.contextPath}/projectmember/search"
      method="get" class="member-search-form">

    <input type="hidden" name="projectId"
           value="${param.projectId}">

    <input type="text" name="keyword"
           value="${param.keyword}"
           placeholder="メンバー名またはID検索"
           class="member-search-input">

    <button type="submit" class="member-search-button">検索</button>
</form>
	
		
<!-- 팀원 목록 -->

<div class="member-grid">

    <!-- 멤버가 있을 때만 반복 출력 -->
  			<c:if test="${not empty memberList}">
    	<c:forEach var="pm" items="${memberList}">
        <div class="member-card">
            <div class="member-info">
                <div class="name">
                    名前: <c:out value="${pm.memberName}"/>
                </div>
                <span class="role">
                    職種：<c:out value="${pm.role}"/>
                </span>
                <div class="contact-M">
                    📧 <c:out value="${pm.email}"/>
                </div>
            </div>
			
                <!-- 역할 수정 버튼 및 폼 -->
                <details class="member-edit">
                    <summary class="member-edit-button">
                        ✏️ 情報を変更
                    </summary>

                    <form action="${pageContext.request.contextPath}/projectmember/update"
                          method="post"
                          class="member-edit-form">

                        <input type="hidden" name="projectId"
                               value="<c:out value='${pm.projectId}'/>">

                        <input type="hidden" name="memberId"
                               value="<c:out value='${pm.memberId}'/>">

                        <div class="form-group">
                            <label>職種・役割</label>

                            <input type="text"
                                   name="role"
                                   value="<c:out value='${pm.role}'/>"
                                   required>
                        </div>

                        <div class="member-edit-email">
                            📧 <c:out value="${pm.email}"/>
                        </div>

                        <button type="submit" class="btn-add">
                            役割を変更
                        </button>
                    </form>
                </details>

         <!-- 프로젝트에서 팀원 삭제 -->
                <div class="card-actions">
                    <form action="${pageContext.request.contextPath}/projectmember/delete"
                          method="post"
                          onsubmit="return confirm('このプロジェクトで該当のチームメンバーを削除しますか？');">

                        <input type="hidden" name="projectId"
                               value="<c:out value='${pm.projectId}'/>">

                        <input type="hidden" name="memberId"
                               value="<c:out value='${pm.memberId}'/>">

                        <button type="submit" class="btn-delete">
                            メンバー削除
                        </button>
                    </form>
                </div>

            </div>
            <!-- member-card 끝 -->

        </c:forEach>
    </c:if>
        </div>



</div>
<!-- member-grid 끝 -->
	
</div>
</main>       
	
	
	
	