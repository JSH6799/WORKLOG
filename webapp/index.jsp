<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="ja">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>WORKLOG - プロジェクトの『複雑』を、もっと『シンプル』に。</title>
  
  <!-- Noto Sans JP 폰트 연결 -->
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+JP:wght@400;500;600;700&display=swap" rel="stylesheet">
  
  <!-- 외부 CSS 파일 연결 -->
  <link rel="stylesheet" href="<%= request.getContextPath() %>/css/index.css">
</head>
<body>

  <!-- 네비게이션 바 -->
  <header class="navbar">
    <div class="nav-container">
      <a href="#" class="logo">
        <span class="logo-icon">📂</span>
        <span class="logo-text">WORKLOG</span>
      </a>
      <nav class="nav-links">
        <a href="#features" class="nav-link">機能</a>
        <a href="#how-it-works" class="nav-link">使い方</a>
      </nav>
      <div class="auth-buttons">
        <a href="<%= request.getContextPath() %>/worklogMember/login" class="btn btn-login">ログイン</a>
        <a href="<%= request.getContextPath() %>/worklogMember/join" class="btn btn-primary">無料で始める</a>
      </div>
    </div>
  </header>

  <!-- 히어로 섹션 -->
  <section class="hero-section">
    <div class="container">
      <div class="hero-badge">
        <span>✨ チームの仕事を、もっとシンプルに。</span>
      </div>
      <h1 class="hero-title">
        思考をすっきり整理。<br>
        チームの作業を、もっとシンプルに。
      </h1>
      <p class="hero-subtitle">
        ドキュメント、日程共有、ロードマップをひとつの場所に。<br>
        WORKLOGは直感的に使えるノート & プロジェクト管理ワークスペースです。
      </p>
      <div class="hero-actions">
        <a href="<%= request.getContextPath() %>/worklogMember/join" class="btn btn-primary btn-large">無料でWORKLOGを始める ➔</a>
        <a href="#demo" class="btn btn-secondary btn-large">デモを見る</a>
      </div>
    </div>
  </section>

  <!-- 제품 인터페이스 / 뷰 전환 예시 섹션 -->
  <section id="demo" class="demo-section">
    <div class="container">
      <div class="workspace-card">
        <div class="workspace-header">
          <div class="window-controls">
            <span class="dot dot-red"></span>
            <span class="dot dot-yellow"></span>
            <span class="dot dot-green"></span>
          </div>
          <div class="workspace-title">📄 無題のデータベース / プロジェクト一覧</div>
        </div>

        <div class="workspace-tabs">
          <button class="tab-btn active">📋 ボードビュー</button>
          <button class="tab-btn">📅 カレンダー</button>
          <button class="tab-btn">📑 リスト</button>
        </div>

        <div class="workspace-content">
          <div class="kanban-board">
            <!-- 未着手 (To Do) -->
            <div class="kanban-column">
              <div class="column-header">
                <span class="status-badge todo">未着手</span>
                <span class="count">2</span>
              </div>
              <div class="kanban-card">
                <div class="card-tag tag-blue">デザイン</div>
                <div class="card-title">✨ LPのワイヤーフレーム作成</div>
                <div class="card-meta">担当: 山田 / 10月28日</div>
              </div>
              <div class="kanban-card">
                <div class="card-tag tag-green">企画</div>
                <div class="card-title">📝 新機能の仕様書作成</div>
                <div class="card-meta">担当: 鈴木 / 11月02日</div>
              </div>
            </div>

            <!-- 進行中 (In Progress) -->
            <div class="kanban-column">
              <div class="column-header">
                <span class="status-badge progress">進行中</span>
                <span class="count">1</span>
              </div>
              <div class="kanban-card">
                <div class="card-tag tag-purple">開発</div>
                <div class="card-title">⚡ 認証APIの実装</div>
                <div class="card-meta">担当: 佐々木 / 明日</div>
              </div>
            </div>

            <!-- 完了 (Done) -->
            <div class="kanban-column">
              <div class="column-header">
                <span class="status-badge done">完了</span>
                <span class="count">1</span>
              </div>
              <div class="kanban-card opacity-muted">
                <div class="card-tag tag-gray">開発</div>
                <div class="card-title">✅ DBスキーマ設計</div>
                <div class="card-meta">担当: 佐藤 / 完了</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>

  <!-- 핵심 가치 / 기능 소개 섹션 -->
  <section id="features" class="features-section">
    <div class="container">
      <div class="section-header">
        <h2 class="section-title">WORKLOGが選ばれる理由</h2>
        <p class="section-subtitle">直感的な操作で、誰でもすぐに使いこなせます</p>
      </div>

      <div class="features-grid">
        <div class="feature-card">
          <div class="feature-icon">📑</div>
          <h3 class="feature-title">プロジェクトの情報を、全部ここに</h3>
          <p class="feature-desc">企画書や議事録、メモをひとつの場所にすっきり整理。チームみんなの「あれ、どこだっけ？」をなくします。</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">⚡</div>
          <h3 class="feature-title">チームの動きが、ひと目でわかる</h3>
          <p class="feature-desc">「いま誰が何をやっているんだっけ？」を解消。進捗がリアルタイムで共有されるから、チームワークもスムーズに。</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">📅</div>
          <h3 class="feature-title">面倒な日程調整を、もっとスマートに</h3>
          <p class="feature-desc">空き時間の投票やカレンダー合わせはおまかせ。みんなのスケジュール調整にかける時間をグッと減らします。</p>
        </div>
      </div>
    </div>
  </section>

  <!-- 간단한 사용 방법 (3 Step) -->
  <section id="how-it-works" class="steps-section">
    <div class="container">
      <div class="section-header">
        <h2 class="section-title">たったの3ステップでスタート</h2>
      </div>

      <div class="steps-grid">
        <div class="step-card">
          <div class="step-number">01</div>
          <h3 class="step-title">ワークスペースを作成</h3>
          <p class="step-desc">メールアドレスだけで数秒で登録完了。すぐにチームを招待できます。</p>
        </div>
        <div class="step-card">
          <div class="step-number">02</div>
          <h3 class="step-title">タスクとノートを整理</h3>
          <p class="step-desc">直感的なドラッグ＆ドロップで、アイデアやタスクをすっきりまとめます。</p>
        </div>
        <div class="step-card">
          <div class="step-number">03</div>
          <h3 class="step-title">スムーズな進行</h3>
          <p class="step-desc">進捗を一目で把握し、プロジェクトを確実に成功へ導きます。</p>
        </div>
      </div>
    </div>
  </section>

  <!-- 하단 CTA 섹션 -->
  <section class="cta-section">
    <div class="container">
      <h2 class="cta-title">今すぐ、新しい働き方をはじめましょう</h2>
      <p class="cta-subtitle">クレジットカードの登録は不要です。無料で体験できます。</p>
      <a href="<%= request.getContextPath() %>/worklogMember/join" class="btn btn-primary btn-large">無料で今すぐ始める ➔</a>
    </div>
  </section>

  <!-- 푸터 -->
  <footer class="footer">
    <div class="container footer-container">
      <div class="footer-left">
        <div class="logo">
          <span class="logo-icon">📂</span>
          <span class="logo-text">WORKLOG</span>
        </div>
        <p class="copyright">© 2026 WORKLOG Inc. All rights reserved.</p>
      </div>
      <div class="footer-links">
        <a href="#">利用規約</a>
        <a href="#">プライバシーポリシー</a>
        <a href="#">お問い合わせ</a>
      </div>
    </div>
  </footer>

</body>
</html>