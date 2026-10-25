<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, model.ProjectMemberDTO" %>
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

    <nav class="crumbs" aria-label="현재 위치">
        <a href="${pageContext.request.contextPath}/project/list">프로젝트 목록</a>
        <a href="${pageContext.request.contextPath}/project/manage?projectId=<%= projectId %>">프로젝트 관리</a>
        <span>멤버 권한</span>
    </nav>

    <header class="page-head">
        <div>
            <h1 class="page-title">멤버 권한 관리</h1>
            <p class="page-desc">프로젝트에 참여 중인 멤버와 권한을 확인합니다.</p>
        </div>
        <a class="btn" href="${pageContext.request.contextPath}/project/manage?projectId=<%= projectId %>">프로젝트 관리로</a>
    </header>

    <% if ("roleChanged".equals(msg)) { %>
        <p class="banner banner-ok" role="status">권한을 변경했습니다.</p>
    <% } %>
    <% if (!isOwner) { %>
        <p class="banner banner-info">권한 변경은 프로젝트 리더(OWNER)만 가능합니다.</p>
    <% } %>

    <div class="layout-2">
        <div class="panel">
            <div class="table-wrap">
                <table class="data-table">
                    <thead>
                         <tr>
                            <th scope="col">멤버</th>
                            <th scope="col">권한</th>
                            <th scope="col">이메일</th>
                            <% if (isOwner) { %><th scope="col">권한 변경</th><% } %>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (memberList == null || memberList.isEmpty()) { %>
                        <tr>
                            <td colspan="4" style="text-align: center; padding: 20px;">등록된 멤버가 없습니다.</td>
                        </tr>
                        <% } else { 
                            for (int i = 0; i < memberList.size(); i++) {
                                ProjectMemberDTO m = memberList.get(i);
                                boolean rowOwner = "OWNER".equals(m.getRole());
                                String label = rowOwner ? "리더" : ("ADMIN".equals(m.getRole()) ? "멤버" : "열람자");
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
                                    <span class="muted">변경 불가</span>
                                <% } else { %>
                                    <form class="role-form" action="${pageContext.request.contextPath}/project/members" method="get">
                                       <input type="hidden" name="projectId" value="<%= projectId %>">
                                       <input type="hidden" name="role" value="<%= role %>">
                                       <input type="hidden" name="targetMemberId" value="<%= m.getMemberId() %>">
                                       <input type="hidden" name="msg" value="roleChanged">
                                       <select name="newRole">
                                           <option value="ADMIN" <%= "ADMIN".equals(m.getRole()) ? "selected" : "" %>>멤버</option>
                                           <option value="MEMBER" <%= "MEMBER".equals(m.getRole()) ? "selected" : "" %>>열람자</option>
                                       </select>
                                       <button type="submit" class="btn btn-small">변경</button>
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
            <p class="table-foot">전체 <%= memberList != null ? memberList.size() : 0 %>명</p>
        </div>

        <aside class="panel side-panel">
            <h2 class="side-title">권한 안내</h2>
            <dl class="role-legend">
                <div>
                    <dt><span class="badge badge-owner">리더</span></dt>
                    <dd>프로젝트 삭제, 멤버 권한 변경, 정보 수정</dd>
                </div>
                <div>
                    <dt><span class="badge badge-admin">멤버</span></dt>
                    <dd>프로젝트 정보 수정</dd>
                </div>
                <div>
                    <dt><span class="badge badge-member">열람자</span></dt>
                    <dd>프로젝트 정보 조회</dd>
                </div>
            </dl>
        </aside>
    </div>

</div>
</main>

<%@ include file="/include/footer.jsp"%>