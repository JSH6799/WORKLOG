<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="true" %>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>カレンダー | WORKLOG</title>

<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Noto+Sans+JP:wght@400;500;700&display=swap" rel="stylesheet">

<!-- FullCalendar v6 (CDN) + 日本語ロケール -->
<script src="https://cdn.jsdelivr.net/npm/fullcalendar@6.1.15/index.global.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/@fullcalendar/core@6.1.15/locales/ja.global.min.js"></script>

<link rel="stylesheet" href="../css/calendarMy.css">
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/calendarMy.css">

</head>
<body>
<div class="app">
  <!-- ===== 左サイドバー ===== -->
  <aside class="sidebar">
    <div class="brand"><span class="brand-mark">W</span> WORKLOG</div>

    <div class="side-section">
      <div class="side-title">マイワークスペース</div>
      <a class="side-item" href="<%= request.getContextPath() %>/index.jsp"><span class="ico">🏠</span>ダッシュボード</a>
      <a class="side-item active" href="#"><span class="ico">📅</span>カレンダー</a>
    </div>

    <div class="side-section">
      <div class="side-title">参加中のプロジェクト</div>
      <a class="side-item" href="#"><span class="ico">🚀</span>A プロジェクト</a>
      <a class="side-item" href="#"><span class="ico">🎨</span>B プロジェクト</a>
    </div>

    <div class="side-section">
      <div class="side-title">日程調整チャット</div>
      <a class="side-item" href="#" id="chatLink"><span class="ico">💬</span>第3回会議の日程調整</a>
    </div>

    <div class="side-section">
      <div class="side-title">👥 チームメンバー</div>
      <div id="memberList"></div>
    </div>
  </aside>

  <!-- ===== メインカレンダー ===== -->
  <main class="main">
    <div class="toolbar">
      <h1 id="calTitle">2026年10月</h1>
      <div class="nav-group">
        <button type="button" id="btnPrev" aria-label="前へ">‹</button>
        <button type="button" id="btnToday">今日</button>
        <button type="button" id="btnNext" aria-label="次へ">›</button>
      </div>
      <div class="view-group" id="viewGroup">
        <button type="button" data-view="dayGridMonth" class="active">月</button>
        <button type="button" data-view="timeGridWeek">週</button>
        <button type="button" data-view="timeGridDay">日</button>
        <button type="button" data-view="listWeek">タイムライン</button>
      </div>
      <button type="button" class="btn-primary" id="btnCreate">＋ 日程を作成</button>
    </div>
    <div class="cal-wrap"><div id="calendar"></div></div>
  </main>

  <!-- ===== イベント詳細 ===== -->
  <aside class="detail">
    <div class="detail-head">
      <h2>イベント詳細</h2>
      <button type="button" class="icon-btn" id="btnCloseDetail" aria-label="閉じる">✕</button>
    </div>
    <div class="detail-body" id="detailBody"></div>
    <div class="detail-foot" id="detailFoot"></div>
  </aside>
</div>

<!-- 会議作成ダイアログ -->
<dialog id="createDialog">
  <form method="dialog" id="createForm">
    <h3>会議を作成</h3>
    <label>タイトル<input type="text" id="newTitle" required placeholder="例：週次定例ミーティング"></label>
    <label>日付<input type="date" id="newDate" required></label>
    <div class="row">
      <button type="button" class="btn-ghost" id="btnCancelCreate">キャンセル</button>
      <button type="submit" class="btn-primary">作成する</button>
    </div>
  </form>
</dialog>

<div class="toast" id="toast"></div>

<script>
(function () {
  'use strict';

  /* =========================================================
   * ダミーデータ（実運用ではAPI / JSTLで置き換えてください）
   * ========================================================= */
  // デモ用に「今日」を2026-10-15に固定。本番では null にすると実際の日付になります。
  var DEMO_TODAY = '2026-10-15';
  var CURRENT_USER = { id: 'm1', role: 'admin' };   // 'admin' のみ「確定する」が有効

  var MEMBERS = [
    { id: 'm1', name: '山田一郎', color: '#3a82c8', presence: 'online'},
    { id: 'm2', name: '山田二郎', color: '#d9628b', presence: 'online'},
    { id: 'm3', name: '山田三郎', color: '#7b8aa3', presence: 'away'},
    { id: 'm4', name: '山田四郎', color: '#e08a3c', presence: 'offline'},
    { id: 'm5', name: '山田五郎', color: '#4aa37a', presence: 'offline'}
  ];

  // status: yes=参加 / maybe=未定 / no=不参加
  var ALL_YES = { m1: 'yes', m2: 'yes', m3: 'yes', m4: 'yes', m5: 'yes' };

  var EVENTS = [
    { id: 'e1', icon: '📋', title: 'ミーティング', start: '2026-10-05', end: '2026-10-07', cls: 'ev-blue',
      memo: 'プロジェクトの目的・体制・スケジュールを共有します。', status: ALL_YES },
    { id: 'e2', icon: '💡', title: '企画案フィードバック', start: '2026-10-13', end: '2026-10-14', cls: 'ev-green',
      memo: '企画案に対するレビューコメントを各自まとめて提出。', status: ALL_YES },
    { id: 'e3', icon: '🚨', title: '開発最終締切', start: '2026-10-14', end: '2026-10-15', cls: 'ev-red', dday: 'D-2',
      memo: 'アプリ開発の最終提出期限です。未完了のタスクを確認してください。', status: ALL_YES },
    { id: 'e4', icon: '🎨', title: 'デザイン案レビュー', start: '2026-10-16', end: '2026-10-17', cls: 'ev-purple',
      memo: 'UIデザイン案の最終レビュー。Figmaのリンクを事前に確認。', status: { m1: 'yes', m2: 'yes', m3: 'yes', m4: 'maybe', m5: 'yes' } },
    { id: 'e5', icon: '🤝', title: '[調整中] 第3回会議', fullTitle: '[調整中] 第3回会議の日程調整', start: '2026-10-20', end: '2026-10-21', cls: 'ev-pending',
      pending: true, memo: '候補時間に投票してください。管理者が確定します。',
      status: { m1: 'yes', m2: 'yes', m3: 'maybe', m4: 'no', m5: 'yes' },
      slots: [
        { time: '14:00', votes: 3 },
        { time: '15:00', votes: 3 },
        { time: '16:00', votes: 2 },
        { time: '17:00', votes: 4 }
      ] },
    { id: 'e6', icon: '📊', title: '中間報告書作成', start: '2026-10-21', end: '2026-10-24', cls: 'ev-blue',
      memo: '進捗・課題・次フェーズの計画を中間報告書にまとめます。', status: { m1: 'yes', m2: 'yes', m3: 'yes', m4: 'maybe', m5: 'yes' } },
    { id: 'e7', icon: '👥', title: 'チーム報告書作成', start: '2026-10-27', end: '2026-10-29', cls: 'ev-blue',
      memo: 'チーム別の成果と振り返りを報告書にします。', status: ALL_YES }
  ];

  /* =========================================================
   * ユーティリティ
   * ========================================================= */
  function $(id) { return document.getElementById(id); }
  function esc(s) {
    return String(s).replace(/[&<>"']/g, function (c) {
      return { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c];
    });
  }
  function ymd(d) {
    var m = d.getMonth() + 1, day = d.getDate();
    return d.getFullYear() + '-' + (m < 10 ? '0' : '') + m + '-' + (day < 10 ? '0' : '') + day;
  }
  function findEvent(id) { return EVENTS.filter(function (e) { return e.id === id; })[0]; }
  function findMember(id) { return MEMBERS.filter(function (m) { return m.id === id; })[0]; }
  function yesCount(ev) {
    var n = 0; for (var k in ev.status) { if (ev.status[k] === 'yes') n++; } return n;
  }
  function dateLabel(ev) {
    var s = new Date(ev.start + 'T00:00:00');
    var e = new Date(ev.end + 'T00:00:00'); e.setDate(e.getDate() - 1);
    var wd = ['日', '月', '火', '水', '木', '金', '土'];
    function f(d) { return (d.getMonth() + 1) + '月' + d.getDate() + '日（' + wd[d.getDay()] + '）'; }
    return ymd(s) === ymd(e) ? f(s) : f(s) + ' 〜 ' + f(e);
  }
  var toastTimer;
  function toast(msg) {
    var t = $('toast'); t.textContent = msg; t.classList.add('show');
    clearTimeout(toastTimer); toastTimer = setTimeout(function () { t.classList.remove('show'); }, 2200);
  }

  /* =========================================================
   * 左サイドバー：メンバー一覧
   * ========================================================= */
  function initials(name) { return name.charAt(0); }
  $('memberList').innerHTML = MEMBERS.map(function (m) {
    return '<div class="member">' +
      '<div class="avatar" style="background:' + m.color + '">' + esc(initials(m.name)) +
      '<span class="dot ' + m.presence + '"></span></div>' +
      '<span class="name">' + esc(m.name) + '</span>' +
      (m.presence === 'online' ? '<span class="live"></span>' : '') +
      '</div>';
  }).join('');

  /* =========================================================
   * 右サイドバー：イベント詳細
   * ========================================================= */
  var selectedId = null;
  var selectedSlot = null;

  var STATUS_ICON = {
    yes:   '<svg class="st" viewBox="0 0 20 20"><circle cx="10" cy="10" r="6" fill="none" stroke="#3dbb6d" stroke-width="1.8"/></svg>',
    maybe: '<svg class="st" viewBox="0 0 20 20"><circle cx="10" cy="10" r="7.5" fill="none" stroke="#8a93a5" stroke-width="1.5"/><text x="10" y="14" text-anchor="middle" font-size="10" fill="#8a93a5" font-weight="700">?</text></svg>',
    no:    '<svg class="st" viewBox="0 0 20 20"><path d="M5 5l10 10M15 5L5 15" stroke="#d64545" stroke-width="2" stroke-linecap="round"/></svg>'
  };

  function renderDetail(id) {
    var ev = findEvent(id);
    var body = $('detailBody'), foot = $('detailFoot');
    if (!ev) {
      body.innerHTML = '<div class="empty">カレンダーのイベントを選択すると<br>詳細がここに表示されます。</div>';
      foot.innerHTML = '';
      return;
    }
    selectedId = id;
    var total = MEMBERS.length, yes = yesCount(ev);
    var html = '';

    html += '<div class="ev-card">' +
      '<div class="t">' + ev.icon + ' ' + esc(ev.fullTitle || ev.title) + '</div>' +
      '<div class="d">' + esc(dateLabel(ev)) + (ev.confirmedTime ? '　' + esc(ev.confirmedTime) : '') + '</div>' +
      '<div class="m">' + esc(ev.memo) + '</div></div>';

    html += '<div class="block"><h3><span class="no">1</span>参加者の状況</h3>' +
      '<div class="progress"><div class="track"><div class="fill" style="width:' + (yes / total * 100) + '%"></div></div>' +
      '<span class="num">' + yes + '/' + total + '</span></div>';
    MEMBERS.forEach(function (m) {
      html += '<div class="attendee"><span>' + esc(m.name) + '</span>' + STATUS_ICON[ev.status[m.id] || 'maybe'] + '</div>';
    });
    html += '</div>';

    if (ev.slots) {
      html += '<div class="block"><h3><span class="no">2</span>時間の候補</h3>';
      ev.slots.forEach(function (s) {
        html += '<button type="button" class="slot' + (selectedSlot === s.time ? ' selected' : '') + '" data-time="' + s.time + '">' +
          '<span class="time">' + s.time + '</span>' +
          '<span class="bar"><i style="width:' + (s.votes / total * 100) + '%"></i></span>' +
          '<span class="cnt">' + s.votes + '票</span></button>';
      });
      html += '</div>';
    }
    body.innerHTML = html;

    if (ev.pending) {
      foot.innerHTML =
        '<button type="button" class="btn-ghost" id="btnAddSlot">＋ 候補時間を追加</button>' +
        '<button type="button" class="btn-primary" id="btnConfirm"' + (CURRENT_USER.role === 'admin' ? '' : ' disabled') + '>確定する（管理者専用）</button>';
    } else {
      foot.innerHTML = '';
    }
    highlightEvent();
  }

  function highlightEvent() {
    document.querySelectorAll('.fc-event').forEach(function (el) { el.classList.remove('selected'); });
    if (!selectedId) return;
    document.querySelectorAll('.fc-event[data-evid="' + selectedId + '"]').forEach(function (el) { el.classList.add('selected'); });
  }

  $('detailBody').addEventListener('click', function (e) {
    var btn = e.target.closest('.slot'); if (!btn) return;
    selectedSlot = btn.getAttribute('data-time');
    renderDetail(selectedId);
  });

  $('detailFoot').addEventListener('click', function (e) {
    var ev = findEvent(selectedId); if (!ev) return;

    if (e.target.id === 'btnAddSlot') {
      var t = prompt('追加する候補時間を入力してください（例：18:00）', '18:00');
      if (t && /^\d{1,2}:\d{2}$/.test(t.trim())) {
        ev.slots.push({ time: t.trim(), votes: 1 });
        ev.slots.sort(function (a, b) { return a.time.localeCompare(b.time); });
        renderDetail(ev.id);
        toast('候補時間を追加しました');
      } else if (t) {
        toast('「18:00」の形式で入力してください');
      }
    }

    if (e.target.id === 'btnConfirm' && !e.target.disabled) {
      if (!selectedSlot) { toast('確定する時間を選択してください'); return; }
      ev.pending = false;
      ev.cls = 'ev-blue';
      ev.title = '第3回会議';
      ev.fullTitle = '第3回会議';
      ev.confirmedTime = selectedSlot + '〜';
      ev.memo = '日程が確定しました。参加者に通知されます。';
      delete ev.slots;
      selectedSlot = null;
      mountCalendar();      // 斜線・投票バッジを消すため再描画
      renderDetail(ev.id);
      toast('会議を確定しました');
    }
  });

  $('btnCloseDetail').addEventListener('click', function () {
    selectedId = null; selectedSlot = null; renderDetail(null); highlightEvent();
  });
  $('chatLink').addEventListener('click', function (e) {
    e.preventDefault(); calendar.gotoDate('2026-10-20'); renderDetail('e5');
  });

  /* =========================================================
   * FullCalendar
   * ========================================================= */
  var calendar = null;

  function toFcEvent(e) {
    return { id: e.id, title: e.title, start: e.start, end: e.end, allDay: true, classNames: [e.cls] };
  }

  function mountCalendar() {
    var view = calendar ? calendar.view.type : 'dayGridMonth';
    var date = calendar ? calendar.getDate() : (DEMO_TODAY || new Date());
    if (calendar) calendar.destroy();

    calendar = new FullCalendar.Calendar($('calendar'), {
      locale: 'ja',
      initialView: view,
      initialDate: date,
      now: DEMO_TODAY || undefined,
      height: '100%',
      headerToolbar: false,          // ツールバーは独自UIを使用
      fixedWeekCount: false,
      dayMaxEvents: 3,
      firstDay: 0,
      events: EVENTS.map(toFcEvent),

      eventContent: function (arg) {
        var e = findEvent(arg.event.id);
        return { html: '<div>' + (e ? e.icon + ' ' : '') + esc(arg.event.title) + '</div>' };
      },
      eventDidMount: function (info) { info.el.setAttribute('data-evid', info.event.id); },
      eventClick: function (info) {
        info.jsEvent.preventDefault();
        selectedSlot = null;
        renderDetail(info.event.id);
      },

      // 日本語ロケールは「20日」と表示されるため「日」を取り除き、数字だけにする
      dayCellContent: function (arg) {
        return { html: arg.dayNumberText.replace('日', '') };
      },

      dayCellClassNames: function (arg) {
        var key = ymd(arg.date), cls = [];
        EVENTS.forEach(function (e) {
          if (e.dday && e.start === key) cls.push('deadline-day');
          if (e.pending && e.start === key) cls.push('pending-day');
        });
        return cls;
      },
      dayCellDidMount: function (arg) {
        var key = ymd(arg.date), top = arg.el.querySelector('.fc-daygrid-day-top');
        if (!top) return;
        EVENTS.forEach(function (e) {
          if (e.start !== key) return;
          var b = null;
          if (e.dday) { b = document.createElement('span'); b.className = 'badge dday'; b.textContent = e.dday; }
          if (e.pending) { b = document.createElement('span'); b.className = 'badge vote'; b.textContent = yesCount(e) + '/' + MEMBERS.length + ' 投票'; }
          if (b) top.appendChild(b);
        });
      },

      datesSet: function (info) {
        var d = calendar.getDate();
        var title = info.view.type === 'dayGridMonth'
          ? d.getFullYear() + '年' + (d.getMonth() + 1) + '月'
          : info.view.title;
        $('calTitle').textContent = title;
        document.querySelectorAll('#viewGroup button').forEach(function (b) {
          b.classList.toggle('active', b.getAttribute('data-view') === info.view.type);
        });
        setTimeout(highlightEvent, 0);
      },
      eventsSet: function () { setTimeout(highlightEvent, 0); }
    });
    calendar.render();

    // フォント・CSS読み込み後にレイアウトを再計算（イベントが日付に被る問題の対策）
    var resize = function () { if (calendar) calendar.updateSize(); };
    if (document.fonts && document.fonts.ready) { document.fonts.ready.then(resize); }
    window.addEventListener('load', resize);
    setTimeout(resize, 300);
  }

  /* ---- ツールバー操作 ---- */
  $('btnPrev').addEventListener('click', function () { calendar.prev(); });
  $('btnNext').addEventListener('click', function () { calendar.next(); });
  $('btnToday').addEventListener('click', function () { calendar.today(); });
  $('viewGroup').addEventListener('click', function (e) {
    var v = e.target.getAttribute('data-view'); if (v) calendar.changeView(v);
  });

  /* ---- 会議作成 ---- */
  var dlg = $('createDialog');
  $('btnCreate').addEventListener('click', function () {
    $('newTitle').value = '';
    $('newDate').value = DEMO_TODAY || ymd(new Date());
    dlg.showModal();
  });
  $('btnCancelCreate').addEventListener('click', function () { dlg.close(); });
  $('createForm').addEventListener('submit', function (e) {
    e.preventDefault();
    var title = $('newTitle').value.trim(), date = $('newDate').value;
    if (!title || !date) return;
    var end = new Date(date + 'T00:00:00'); end.setDate(end.getDate() + 1);
    var ev = { id: 'e' + (EVENTS.length + 1) + '_' + Date.now(), icon: '📅', title: title, start: date, end: ymd(end),
               cls: 'ev-blue', memo: '新しく作成された会議です。', status: { m1: 'yes', m2: 'maybe', m3: 'maybe', m4: 'maybe', m5: 'maybe' } };
    EVENTS.push(ev);
    dlg.close();
    mountCalendar();
    renderDetail(ev.id);
    toast('会議を作成しました');
  });

  /* ---- 初期表示 ---- */
  mountCalendar();
  renderDetail('e5');    // 画面イメージ同様、調整中の会議を選択状態で表示
})();

</script>
</body>
</html>