<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ include file="/include/header.jsp"%>

<main class="mypage-wrap">
    <section class="unconfirmed-section">
        <h3 class="unconfirmed-title">未確認投稿</h3>
        <p class="unconfirmed-desc">確認が必要な投稿をプロジェクト別に確認できます。</p>

        <!-- 프로젝트 1 -->
        <div class="unconfirmed-project">

            <button type="button" class="project-toggle"
                    data-bs-toggle="collapse" data-bs-target="#unconfirmedProject1"
                    aria-expanded="false" aria-controls="unconfirmedProject1">

                <span class="project-info">
                    <span class="project-name">プロジェクトA</span>
                    <span class="project-count">未確認 3件</span>
                </span>

                <span class="toggle-label">▶ 開く</span>
            </button>

            <div class="collapse" id="unconfirmedProject1">
                <ul class="unconfirmed-list">
                    <li>
                        <a href="detail.jsp" class="unconfirmed-post">
                            <div class="post-title">タイトル</div>
                            <div class="post-info">投稿者 · YYYY-MM-DD</div>
                        </a>
                    </li>
                    <li>
                        <a href="detail.jsp" class="unconfirmed-post">
                            <div class="post-title">タイトル</div>
                            <div class="post-info">投稿者 · YYYY-MM-DD</div>
                        </a>
                    </li>
                    <li>
                        <a href="detail.jsp" class="unconfirmed-post">
                            <div class="post-title">タイトル</div>
                            <div class="post-info">投稿者 · YYYY-MM-DD</div>
                        </a>
                    </li>
                </ul>
            </div>

        </div>

        <!-- 프로젝트 2 -->
        <div class="unconfirmed-project">

            <button type="button" class="project-toggle"
                    data-bs-toggle="collapse" data-bs-target="#unconfirmedProject2"
                    aria-expanded="false" aria-controls="unconfirmedProject2">

                <span class="project-info">
                    <span class="project-name">プロジェクトB</span>
                    <span class="project-count">未確認 1件</span>
                </span>

                <span class="toggle-label">▶ 開く</span>
            </button>

            <div class="collapse" id="unconfirmedProject2">
                <ul class="unconfirmed-list">
                    <li>
                        <a href="detail.jsp" class="unconfirmed-post">
                            <div class="post-title">タイトル</div>
                            <div class="post-info">投稿者 · YYYY-MM-DD</div>
                        </a>
                    </li>
                </ul>
            </div>

        </div>

    </section>
</main>

<%@ include file="/include/footer.jsp"%>
<script src="${pageContext.request.contextPath}/js/mypage.js"></script>