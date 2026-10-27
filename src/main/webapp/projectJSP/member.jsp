<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ include file="/include/header.jsp"%>

<%
    // 화면 확인용: 주소 끝의 ?role=OWNER / ADMIN / MEMBER 로 권한별 화면을 미리 볼 수 있습니다.
    String role = request.getParameter("role");
    if (!"ADMIN".equals(role) && !"MEMBER".equals(role)) {
        role = "OWNER";
    }
    boolean isOwner = "OWNER".equals(role);           // 권한 변경은 소유자만 가능
    String msg = request.getParameter("msg");
    String projectName = "クライアント会社ポータルリニューアル";          // 샘플 값
 
    // 샘플 멤버 5명 {이름, 사용자 ID, 권한, 가입일}
    String[][] members = {
        {"山田一郎", "java.kim",  "OWNER",  "2026-09-10"},
        {"前田次郎", "java.park", "ADMIN",  "2026-09-12"},
        {"鈴木三郎", "java.ryu",   "MEMBER", "2026-09-20"},
        {"小野四郎", "java.ham",   "MEMBER", "2026-10-02"},
        {"蛇腹邪悪", "java.ja",  "MEMBER", "2026-10-04"}
    };
    int meIndex = isOwner ? 0 : ("ADMIN".equals(role) ? 1 : 3);
%>

<main class="project-wrap">
<div id="project-content">
 
    <%-- 개발용: 권한별 화면 미리보기. 서블릿 연결 후 이 블록은 삭제합니다. --%>
    <div class="dev-bar">
        <span>権限別サンプルを閲覧</span>
        <a href="${pageContext.request.contextPath}/project/members?role=OWNER" class="<%= isOwner ? "on" : "" %>">所有者</a>
        <a href="${pageContext.request.contextPath}/project/members?role=ADMIN" class="<%= "ADMIN".equals(role) ? "on" : "" %>">メンバー</a>
        <a href="${pageContext.request.contextPath}/project/members?role=MEMBER" class="<%= "MEMBER".equals(role) ? "on" : "" %>">閲覧者</a>
    </div>
 
    <nav class="crumbs" aria-label="現在地">
        <a href="${pageContext.request.contextPath}/project/list">プロジェクト</a>
        <a href="${pageContext.request.contextPath}/project/manage?role=<%= role %>"><%= projectName %></a>
        <span>メンバー権限</span>
    </nav>
 
    <header class="page-head">
        <div>
            <h1 class="page-title">メンバー権限</h1>
            <p class="page-desc"><%= projectName %></p>
        </div>
        <a class="btn" href="${pageContext.request.contextPath}/project/manage?role=<%= role %>">プロジェクト管理へ</a>
    </header>
 
    <% if ("roleChanged".equals(msg)) { %>
        <p class="banner banner-ok" role="status">権限を変更しました。</p>
    <% } %>
    <% if (!isOwner) { %>
        <p class="banner banner-info">権限の変更はプロジェクト所有者のみ可能です。</p>
    <% } %>
 
    <div class="layout-2">
 
        <div class="panel">
            <div class="table-wrap">
                <table class="data-table">
                    <caption class="sr-only">プロジェクトメンバーと権限</caption>
                    <thead>
                         <tr>
                            <th scope="col">メンバー</th>
                            <th scope="col">権限</th>
                            <th scope="col">登録日</th>
                            <% if (isOwner) { %><th scope="col">権限の変更</th><% } %>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (int i = 0; i < members.length; i++) {
                            String[] m = members[i];
                            boolean rowOwner = "OWNER".equals(m[2]);
                            String label = rowOwner ? "リーダー" : ("ADMIN".equals(m[2]) ? "メンバー" : "閲覧者");
                        %>
                        <tr>
                            <td>
                                <div class="member">
                                    <span class="avatar" aria-hidden="true"><%= m[0].substring(0, 1) %></span>
                                    <span class="member-text">
                                       <strong><%= m[0] %></strong>
                                       <% if (i == meIndex) { %><span class="me-tag">나</span><% } %>
                                       <span class="member-id"><%= m[1] %></span>
                                   </span>
                                </div>
                            </td>
                            <td><span class="badge badge-<%= m[2].toLowerCase() %>"><%= label %></span></td>
                            <td class="nowrap"><%= m[3] %></td>
                            <% if (isOwner) { %>
                            <td>
                                <% if (rowOwner) { %>
                                    <span class="muted">変更不可能</span>
                                <% } else { %>
                                    <form class="role-form" action="${pageContext.request.contextPath}/project/members" method="get">
                                       <input type="hidden" name="role" value="<%= role %>">
                                       <input type="hidden" name="msg" value="roleChanged">
                                       <label class="sr-only" for="role-<%= i %>"><%= m[0] %>の権限</label>
                                       <select id="role-<%= i %>" name="newRole">
                                           <option value="ADMIN" <%= "ADMIN".equals(m[2]) ? "selected" : "" %>>メンバー</option>
                                           <option value="MEMBER" <%= "MEMBER".equals(m[2]) ? "selected" : "" %>>閲覧者</option>
                                       </select>
                                       <button type="submit" class="btn btn-small">변경</button>
                                    </form>
                                <% } %>
                            </td>
                            <% } %>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
            <p class="table-foot">全体 <%= members.length %>명</p>
        </div>
 
        <aside class="panel side-panel">
            <h2 class="side-title">権限の案内</h2>
            <dl class="role-legend">
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
                        <dd>プロジェクト情報の閲覧</dd>
                    </div>
                </dl>
            </aside>
                </div>
            </dl>
        </aside>
 
    </div>
 
</div>
</main>

<%@ include file="/include/footer.jsp"%>