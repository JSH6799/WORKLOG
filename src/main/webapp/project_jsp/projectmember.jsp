<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, model.ProjectMemberDTO" %>
<%@ include file="/include/header.jsp"%>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/projectmember.css">

<%
    List<ProjectMemberDTO> memberList = (List<ProjectMemberDTO>) request.getAttribute("memberList");
    String projectId = request.getParameter("projectId");
    // サーブレットがDBから判定した自分の権限 (OWNER / ADMIN / MEMBER)
    String role = (String) request.getAttribute("myRole");
    if (role == null) role = "MEMBER";
    boolean isOwner = "OWNER".equals(role);
    String msg = request.getParameter("msg");
%>

<main class="project-wrap">
<div id="project-content">

    <nav class="crumbs" aria-label="現在地">
        <a href="${pageContext.request.contextPath}/project/list">プロジェクトリスト</a>
        <a href="${pageContext.request.contextPath}/project/manage?projectId=<%= projectId %>">プロジェクト管理</a>
        <span>メンバー権限</span>
    </nav>

    <header class="page-head">
	    <div>
	        <h1 class="page-title">メンバー権限の管理</h1>
	        <p class="page-desc">プロジェクトに参加中のメンバーおよび権限を確認します。</p>
	    </div>
	
	    <div class="page-head-actions">
	        <!-- 멤버 검색 버튼 -->
	        <button type="button" class="btn member-search-trigger" data-bs-toggle="modal" data-bs-target="#memberSearchModal">
	            <i class="bi bi-search"></i>
	            メンバー検索
	        </button>
	
	        <!-- 프로젝트 관리 버튼 -->
	        <a class="btn" href="${pageContext.request.contextPath}/project/manage?projectId=<%= projectId %>">
	            プロジェクト管理へ
	        </a>
	    </div>
	</header>

    <% if ("roleChanged".equals(msg)) { %>
        <p class="banner banner-ok" role="status">権限を変更しました。</p>
    <% } %>
    <% if (!isOwner) { %>
        <p class="banner banner-info">メンバー権限はプロジェクトリーダー以外変更できません。</p>
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
                                    <form class="role-form" action="${pageContext.request.contextPath}/project/members" method="post">
                                       <input type="hidden" name="projectId" value="<%= projectId %>">
                                                                              <input type="hidden" name="targetMemberId" value="<%= m.getMemberId() %>">
                                                                              <select name="newRole">
                                           <option value="ADMIN" <%= "ADMIN".equals(m.getRole()) ? "selected" : "" %>>メンバー</option>
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
                    <dd>プロジェクト削除、メンバー権限の変更、情報変更</dd>
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

</div>

<!-- 멤버 검색 모달 -->
<div class="modal fade" id="memberSearchModal" tabindex="-1" aria-labelledby="memberSearchModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content member-search-modal">
            <div class="modal-header">
                <h5 class="modal-title" id="memberSearchModalLabel">メンバー検索</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>

            <form action="${pageContext.request.contextPath}/projectmember/search" method="get">
                <div class="modal-body">
                    <p class="member-search-desc">会員IDまたは名前を入力してください。</p>

                    <input type="hidden" name="projectId" value="<%= projectId %>">
                    <input type="text" name="keyword" class="form-control" placeholder="会員IDまたは名前で検索" required>
                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">キャンセル</button>

                    <button type="submit" class="btn btn-primary">
                        <i class="bi bi-search"></i>
                        検索
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

</main>

<%@ include file="/include/footer.jsp"%>