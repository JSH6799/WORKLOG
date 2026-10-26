<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/include/header.jsp"%>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/proandmem.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/project/addproject.css">

<main class="project-wrap">
    <div id="project-content">

        <nav class="crumbs" aria-label="현재 위치">
            <a href="${pageContext.request.contextPath}/project/list">프로젝트 목록</a>
            <span>새로운 프로젝트</span>
        </nav>

        <header class="page-head">
            <div>
                <h1 class="page-title">새로운 프로젝트 생성</h1>
                <p class="page-desc">프로젝트 정보를 입력해주세요. 생성자가 리더(OWNER)로 자동 지정됩니다.</p>
            </div>
        </header>

        <div class="layout-2">
            <form class="panel form-panel" action="${pageContext.request.contextPath}/project/add" method="post">

                <div class="field">
                    <label for="projectName">프로젝트명<span class="req" aria-hidden="true">*</span></label>
                    <input type="text" id="projectName" name="projectName" maxlength="30" required
                           placeholder="예: 2026 하반기 신규 서비스 런칭">
                    <p class="field-hint">30자 이내로 입력하세요.</p>
                </div>

                <div class="field">
                    <label for="description">상세 설명</label>
                    <textarea id="description" name="description" maxlength="2000"
                              placeholder="프로젝트의 목적과 범위를 간단히 적어주세요."></textarea>
                    <p class="field-hint">2000자 이내로 입력하세요.</p>
                </div>

                <div class="field-row">
                    <div class="field">
                        <label for="startDate">시작일</label>
                        <input type="date" id="startDate" name="startDate">
                    </div>
                    <div class="field">
                        <label for="endDate">종료일</label>
                        <input type="date" id="endDate" name="endDate">
                    </div>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn btn-primary">프로젝트 생성</button>
                    <a class="btn" href="${pageContext.request.contextPath}/project/list">취소</a>
                </div>
            </form>

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