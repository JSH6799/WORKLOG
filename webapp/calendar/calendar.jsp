<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- JSTL 태그라이브러리 선언 (Tomcat 10+ : jakarta.tags.core) --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    // 비로그인 차단 (헤더 include보다 먼저 실행해야 함)
    if (session.getAttribute("loginMember") == null) {
        response.sendRedirect(request.getContextPath() + "/worklogMember/login");
        return;
    }
%>
<%-- 공통 헤더: <html>, <head>, CSS(calendarMy.css 포함), <header GNB> 출력.
     proList 는 ProjectListFilter 가 request 에 세팅 --%>
<%@ include file="/include/header.jsp" %>

<%-- 현재 프로젝트: ?projectId= 가 있으면 그 값, 없으면 내 프로젝트 목록의 첫 번째 --%>
<c:set var="curPid" value="${not empty param.projectId ? param.projectId : (not empty proList ? proList[0].projectId : '')}" />

<!-- FullCalendar v6 (CDN) + 日本語ロケール -->
<script src="https://cdn.jsdelivr.net/npm/fullcalendar@6.1.15/index.global.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/@fullcalendar/core@6.1.15/locales/ja.global.min.js"></script>

<%-- JS 에서 쓸 서버 값 (script 안에서는 EL 을 쓰지 않으므로 data 속성으로 전달) --%>
<div id="appCtx" hidden
     data-ctx="<c:out value='${pageContext.request.contextPath}'/>"
     data-project-id="<c:out value='${curPid}'/>"></div>

<div class="app">
  <!-- ===== 左サイドバー ===== -->
  <aside class="sidebar">

    <div class="side-section">
      <div class="side-title">参加中のプロジェクト</div>
      <c:choose>
        <c:when test="${not empty proList}">
          <c:forEach var="p" items="${proList}">
            <a class="side-item${p.projectId == curPid ? ' active' : ''}"
               href="${pageContext.request.contextPath}/project/manage?projectId=${p.projectId}">
              <span class="ico">🚀</span><c:out value="${p.projectName}" />
            </a>
          </c:forEach>
        </c:when>
        <c:otherwise>
          <div class="side-item" style="color:#adb5bd; cursor:default;">
            参加中のプロジェクトはありません
          </div>
        </c:otherwise>
      </c:choose>
    </div>

    <div class="side-section">
      <div class="side-title">日程調整チャット</div>
      <div id="pendingList"></div>
    </div>

    <div class="side-section">
      <div class="side-title">👥 チームメンバー</div>
      <div id="memberList"></div>
    </div>
  </aside>

  <!-- ===== メインカレンダー ===== -->
  <main class="main">
    <div class="toolbar">
      <h1 id="calTitle"></h1>
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

<!-- 予定 作成 / 編集 ダイアログ -->
<dialog id="createDialog">
  <form method="dialog" id="createForm">
    <h3 id="dlgTitle">予定を作成</h3>

    <label id="rowType">種類
      <select id="fType">
        <option value="CONFIRMED">確定済みの予定</option>
        <option value="PENDING">日程調整（時間を投票で決める）</option>
      </select>
    </label>

    <label>タイトル<input type="text" id="newTitle" required maxlength="100" placeholder="例：週次定例ミーティング"></label>

    <div class="two">
      <label>開始日<input type="date" id="newDate" required></label>
      <label>終了日<input type="date" id="newEnd"></label>
    </div>

    <label id="rowSlots" class="hide">候補時間（カンマ区切り）
      <input type="text" id="fSlots" placeholder="例：14:00, 15:00, 16:00">
    </label>
    <label id="rowTime">確定時間（任意）<input type="time" id="fTime"></label>

    <div class="two">
      <label>アイコン
        <select id="fIcon">
          <option value="EVENT">📅 予定</option>
          <option value="MEETING">📋 ミーティング</option>
          <option value="IDEA">💡 企画</option>
          <option value="DEADLINE">🚨 締切</option>
          <option value="DESIGN">🎨 デザイン</option>
          <option value="DISCUSS">🤝 打ち合わせ</option>
          <option value="REPORT">📊 報告書</option>
          <option value="TEAM">👥 チーム</option>
        </select>
      </label>
      <label> 色
        <select id="fColor">
          <option value="blue"> 青</option>
          <option value="green"> 緑</option>
          <option value="purple"> 紫</option>
          <option value="red"> 赤</option>
        </select>
      </label>
    </div>

    <label class="chk"><input type="checkbox" id="fDeadline">締切日として表示する（D-day）</label>

    <label>メモ<textarea id="fMemo" maxlength="2000" placeholder="任意"></textarea></label>

    <div class="row">
      <button type="button" class="btn-ghost" id="btnCancelCreate">キャンセル</button>
      <button type="submit" class="btn-primary" id="btnSubmit">保存する</button>
    </div>
  </form>
</dialog>

<div class="toast" id="toast"></div>

<script>
(function () {
  'use strict';

  /* =========================================================
   * ※ isELIgnored を外したため、このscript内ではEL式（ドル記号＋波括弧）を書かないこと
   *   （テンプレートリテラルも使わず、文字列連結を使う）
   * ========================================================= */
  function $(id) { return document.getElementById(id); }

  var CTX = $('appCtx').dataset;
  var PID = CTX.projectId || '';
  var API = CTX.ctx + '/worklogCalendar/event';

  var ME = { id: null, role: null };   // loadMembers() で設定
  var EVENTS = [];                     // 表示範囲の一覧（サーバーから取得）
  var DETAIL = null;                   // 選択中イベントの詳細
  var selectedId = null;
  var selectedSlot = null;             // 確定用に選択した候補時間（リーダーのみ）
  var editingId = null;                // null = 新規作成モード
  var firstLoad = true;
  var calendar = null;

  var ICONS = { EVENT: '📅', MEETING: '📋', IDEA: '💡', DEADLINE: '🚨',
                DESIGN: '🎨', DISCUSS: '🤝', REPORT: '📊', TEAM: '👥' };
  var ROLE_LABEL = { '1': 'リーダー', '2': 'メンバー', '3': '閲覧' };
  var AVATAR_COLORS = ['#3a82c8', '#d9628b', '#7b8aa3', '#e08a3c', '#4aa37a', '#7950f2'];

  var STATUS_ICON = {
    YES:   '<svg class="st" viewBox="0 0 20 20"><circle cx="10" cy="10" r="6" fill="none" stroke="#3dbb6d" stroke-width="1.8"/></svg>',
    MAYBE: '<svg class="st" viewBox="0 0 20 20"><circle cx="10" cy="10" r="7.5" fill="none" stroke="#8a93a5" stroke-width="1.5"/><text x="10" y="14" text-anchor="middle" font-size="10" fill="#8a93a5" font-weight="700">?</text></svg>',
    NO:    '<svg class="st" viewBox="0 0 20 20"><path d="M5 5l10 10M15 5L5 15" stroke="#d64545" stroke-width="2" stroke-linecap="round"/></svg>'
  };

  /* =========================================================
   * ユーティリティ
   * ========================================================= */
  function esc(s) {
    return String(s == null ? '' : s).replace(/[&<>"']/g, function (c) {
      return { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c];
    });
  }
  function ymd(d) {
    var m = d.getMonth() + 1, day = d.getDate();
    return d.getFullYear() + '-' + (m < 10 ? '0' : '') + m + '-' + (day < 10 ? '0' : '') + day;
  }
  function parseYmd(s) { return new Date(s + 'T00:00:00'); }
  function todayStart() { var t = new Date(); t.setHours(0, 0, 0, 0); return t; }
  function addDays(s, n) { var d = parseYmd(s); d.setDate(d.getDate() + n); return ymd(d); }

  var toastTimer;
  function toast(msg) {
    var t = $('toast'); t.textContent = msg; t.classList.add('show');
    clearTimeout(toastTimer); toastTimer = setTimeout(function () { t.classList.remove('show'); }, 2600);
  }

  /** 日付ラベル（start/end はともに「終了日を含む」yyyy-mm-dd） */
  function dateLabel(startStr, endStr) {
    var s = parseYmd(startStr), e = parseYmd(endStr);
    var wd = ['日', '月', '火', '水', '木', '金', '土'];
    function f(d) { return (d.getMonth() + 1) + '月' + d.getDate() + '日（' + wd[d.getDay()] + '）'; }
    return startStr === endStr ? f(s) : f(s) + ' 〜 ' + f(e);
  }

  /** 締切までの日数（今日=0, 過ぎたら負数）。ev.end は FullCalendar 形式（終了日の翌日） */
  function daysUntilDue(ev) {
    var due = parseYmd(addDays(ev.end, -1));
    return Math.round((due - todayStart()) / 86400000);
  }

  function avatarColor(id) {
    var h = 0, i;
    for (i = 0; i < id.length; i++) { h = (h * 31 + id.charCodeAt(i)) % 997; }
    return AVATAR_COLORS[h % AVATAR_COLORS.length];
  }

  /** API 呼び出し。body があれば POST(JSON)、なければ GET */
  function api(action, params, body) {
    var q = 'action=' + encodeURIComponent(action) + '&projectId=' + encodeURIComponent(PID);
    var k;
    for (k in (params || {})) { q += '&' + k + '=' + encodeURIComponent(params[k]); }
    var opt = { method: body ? 'POST' : 'GET', credentials: 'same-origin', headers: { 'Accept': 'application/json' } };
    if (body) {
      opt.headers['Content-Type'] = 'application/json; charset=UTF-8';
      opt.body = JSON.stringify(body);
    }
    return fetch(API + '?' + q, opt).then(function (r) {
      return r.text().then(function (t) {
        var j = null;
        try { j = JSON.parse(t); } catch (e) { /* JSON 以外（ログイン画面など） */ }
        if (!r.ok || j === null) {
          var err = new Error((j && j.error) || ('サーバーエラー (' + r.status + ')'));
          err.status = r.status;
          throw err;
        }
        return j;
      });
    });
  }

  /* =========================================================
   * 左サイドバー：メンバー / 調整中の予定
   * ========================================================= */
  function loadMembers() {
    return api('members').then(function (r) {
      ME.id = r.myId; ME.role = r.myRole;
      $('memberList').innerHTML = r.members.map(function (m) {
        return '<div class="member">' +
          '<div class="avatar" style="background:' + avatarColor(m.id) + '">' + esc(m.name.charAt(0)) + '</div>' +
          '<span class="name">' + esc(m.name) + '</span>' +
          '<span class="role">' + esc(ROLE_LABEL[m.role] || '') + '</span>' +
          '</div>';
      }).join('');
      if (ME.role === '3') {
        $('btnCreate').disabled = true;
        $('btnCreate').title = '閲覧専用メンバーは作成できません';
      }
    }).catch(function (err) { toast(err.message); });
  }

  function loadPending() {
    return api('pending').then(function (list) {
      var box = $('pendingList');
      if (!list.length) {
        box.innerHTML = '<div class="side-item" style="color:#adb5bd; cursor:default;">調整中の予定はありません</div>';
        return;
      }
      box.innerHTML = list.map(function (e) {
        return '<a class="side-item" href="#" data-id="' + esc(e.id) + '" data-start="' + esc(e.start) + '">' +
          '<span class="ico">💬</span><span>' + esc(e.title) + '</span></a>';
      }).join('');
    }).catch(function () { /* ポーリング中の一時エラーは無視 */ });
  }

  $('pendingList').addEventListener('click', function (e) {
    var a = e.target.closest('.side-item[data-id]'); if (!a) return;
    e.preventDefault();
    calendar.gotoDate(a.getAttribute('data-start'));
    selectEvent(a.getAttribute('data-id'));
  });

  /* =========================================================
   * 右サイドバー：イベント詳細
   * ========================================================= */
  var detailSeq = 0;

  function selectEvent(id) {
    selectedId = String(id);
    selectedSlot = null;
    loadDetail(selectedId, false);
    highlightEvent();
  }

  function clearSelection() {
    selectedId = null; selectedSlot = null; DETAIL = null; detailSeq++;
    renderDetail();
    highlightEvent();
  }

  function loadDetail(id, silent) {
    var seq = ++detailSeq;
    return api('detail', { id: id }).then(function (d) {
      if (seq !== detailSeq || selectedId !== id) return;
      if (silent && DETAIL && JSON.stringify(DETAIL) === JSON.stringify(d)) return;   // 変化なしなら再描画しない
      DETAIL = d;
      renderDetail();
    }).catch(function (err) {
      if (seq !== detailSeq) return;
      if (silent) { if (err.status === 404) clearSelection(); return; }
      toast(err.message);
      clearSelection();
    });
  }

  function renderDetail() {
    var body = $('detailBody'), foot = $('detailFoot'), d = DETAIL;
    if (!d) {
      body.innerHTML = '<div class="empty">カレンダーのイベントを選択すると<br>詳細がここに表示されます。</div>';
      foot.innerHTML = '';
      return;
    }
    var pending = d.status === 'PENDING';
    var total = d.totalCount, yes = d.yesCount;
    var html = '';

    var dateLine = dateLabel(d.startDate, d.endDate);
    if (d.confirmedTime) dateLine += '　' + d.confirmedTime + '〜';
    if (d.deadline) {
      var diff = Math.round((parseYmd(d.endDate) - todayStart()) / 86400000);
      dateLine += '　' + (diff === 0 ? 'D-Day' : (diff > 0 ? 'D-' + diff : 'D+' + Math.abs(diff)));
    }

    html += '<div class="ev-card">' +
      '<div class="t">' + (ICONS[d.iconKey] || ICONS.EVENT) + ' ' + (pending ? '[調整中] ' : '') + esc(d.title) + '</div>' +
      '<div class="d">' + esc(dateLine) + '</div>' +
      '<div class="m" style="white-space:pre-wrap">' + esc(d.memo) + '</div></div>';

    html += '<div class="block"><h3><span class="no">1</span>参加者の状況</h3>' +
      '<div class="progress"><div class="track"><div class="fill" style="width:' + (total ? yes / total * 100 : 0) + '%"></div></div>' +
      '<span class="num">' + yes + '/' + total + '</span></div>';
    if (d.canParticipate) {
      var mine = 'MAYBE';
      d.attendees.forEach(function (a) { if (a.memberId === d.myId) mine = a.status; });
      html += '<div class="att-pick">' + [['YES', '参加'], ['MAYBE', '未定'], ['NO', '不参加']].map(function (p) {
        return '<button type="button" data-att="' + p[0] + '" class="' + (mine === p[0] ? 'on' : '') + '">' + p[1] + '</button>';
      }).join('') + '</div>';
    }
    d.attendees.forEach(function (a) {
      html += '<div class="attendee"><span>' + esc(a.memberName) + (a.memberId === d.myId ? '（あなた）' : '') + '</span>' +
        (STATUS_ICON[a.status] || STATUS_ICON.MAYBE) + '</div>';
    });
    html += '</div>';

    if (pending || d.slots.length) {
      html += '<div class="block"><h3><span class="no">2</span>時間の候補</h3>';
      if (!d.slots.length) html += '<div style="color:#adb5bd;font-size:13px;">候補時間がありません</div>';
      var canVote = pending && d.canParticipate;     // 投票: リーダー・メンバー
      var canPick = pending && d.canConfirm;         // 確定する時間の選択: リーダーのみ
      d.slots.forEach(function (s) {
        html += '<div class="slot' + (canPick && selectedSlot === s.time ? ' selected' : '') + '"' +
          (canPick ? ' data-time="' + esc(s.time) + '" style="cursor:pointer"' : ' style="cursor:default"') + '>' +
          (canVote ? '<button type="button" class="vote-btn' + (s.votedByMe ? ' on' : '') + '" data-vote="' + s.slotId +
                     '" title="' + (s.votedByMe ? '投票を取り消す' : 'この時間に投票') + '">✓</button>' : '') +
          '<span class="time">' + esc(s.time) + '</span>' +
          '<span class="bar"><i style="width:' + (total ? s.votes / total * 100 : 0) + '%"></i></span>' +
          '<span class="cnt">' + s.votes + '票</span></div>';
      });
      if (canPick) html += '<div style="color:#adb5bd;font-size:12px;margin-top:6px;">確定する時間の行をクリックして選択</div>';
      html += '</div>';
    }
    body.innerHTML = html;

    var f = '';
    if (pending) {
      f += '<button type="button" class="btn-ghost" id="btnAddSlot"' +
           (d.canParticipate ? '' : ' disabled title="閲覧専用メンバーは追加できません"') + '>＋ 候補時間を追加</button>';
      f += '<button type="button" class="btn-primary" id="btnConfirm"' +
           (d.canConfirm ? '' : ' disabled title="リーダーのみ確定できます"') + '>確定する（リーダー専用）</button>';
    }
    if (d.canEdit) {
      f += '<button type="button" class="btn-ghost" id="btnEdit">✎ 編集</button>';
      f += '<button type="button" class="btn-danger" id="btnDelete">🗑 削除</button>';
    }
    foot.innerHTML = f;
    highlightEvent();
  }

  function highlightEvent() {
    document.querySelectorAll('.fc-event').forEach(function (el) { el.classList.remove('selected'); });
    if (!selectedId) return;
    document.querySelectorAll('.fc-event[data-evid="' + selectedId + '"]').forEach(function (el) { el.classList.add('selected'); });
  }

  $('detailFoot').addEventListener('click', function (e) {
    if (!DETAIL) return;
    if (e.target.id === 'btnEdit') openDialog('edit');
    if (e.target.id === 'btnDelete') doDelete();
    if (e.target.id === 'btnAddSlot') doAddSlot();
    if (e.target.id === 'btnConfirm') doConfirm();
  });

  $('detailBody').addEventListener('click', function (e) {
    if (!DETAIL) return;
    var att = e.target.closest('.att-pick button');
    if (att) { doAttend(att.getAttribute('data-att')); return; }
    var vote = e.target.closest('.vote-btn');
    if (vote) { doVote(vote.getAttribute('data-vote')); return; }
    var slot = e.target.closest('.slot[data-time]');
    if (slot && DETAIL.canConfirm && DETAIL.status === 'PENDING') {
      var t = slot.getAttribute('data-time');
      selectedSlot = (selectedSlot === t) ? null : t;
      renderDetail();
    }
  });
  $('btnCloseDetail').addEventListener('click', clearSelection);

  /* =========================================================
   * FullCalendar
   * ========================================================= */
  function findEvent(id) { return EVENTS.filter(function (e) { return e.id === id; })[0]; }

  function toFcEvent(e) {
    return { id: e.id, title: e.title, start: e.start, end: e.end, allDay: true,
             classNames: [e.status === 'PENDING' ? 'ev-pending' : 'ev-' + e.colorCls] };
  }

  /** 締切日セルの色付け + D-day バッジ、調整中の開始日セルの投票バッジ */
  function decorateDays() {
    document.querySelectorAll('.cal-badge').forEach(function (b) { b.remove(); });
    document.querySelectorAll('.deadline-day, .pending-day').forEach(function (c) {
      c.classList.remove('deadline-day', 'pending-day');
    });
    EVENTS.forEach(function (e) {
      if (e.deadline) {
        var diff = daysUntilDue(e);
        if (diff >= 0) mark(addDays(e.end, -1), 'deadline-day', 'dday', diff === 0 ? 'D-Day' : 'D-' + diff);
      }
      if (e.status === 'PENDING') {
        mark(e.start, 'pending-day', 'vote', e.voterCount + '/' + e.totalCount + ' 投票');
      }
    });
  }
  function mark(dateStr, cls, kind, text) {
    var cell = document.querySelector('.fc-daygrid-day[data-date="' + dateStr + '"]');
    if (!cell) return;
    cell.classList.add(cls);
    var top = cell.querySelector('.fc-daygrid-day-top'); if (!top) return;
    var b = document.createElement('span');
    b.className = 'badge cal-badge ' + kind;
    b.textContent = text;
    top.appendChild(b);
  }

  /** 初回読み込み時：これから（または進行中）の最も近い予定を選択 */
  function pickInitial() {
    firstLoad = false;
    var t = ymd(new Date()), pick = null;
    EVENTS.forEach(function (e) { if (e.end > t && (!pick || e.start < pick.start)) pick = e; });
    if (pick) selectEvent(pick.id);
  }

  calendar = new FullCalendar.Calendar($('calendar'), {
    locale: 'ja',
    initialView: 'dayGridMonth',
    // now / initialDate は指定しない → 実際の現在日時を使用（日付が変わると「今日」表示も自動更新）
    height: '100%',
    headerToolbar: false,          // ツールバーは独自UIを使用
    fixedWeekCount: false,
    dayMaxEvents: 3,
    firstDay: 0,

    events: function (info, success, failure) {
      if (!PID) { success([]); return; }
      api('list', { start: info.startStr.slice(0, 10), end: info.endStr.slice(0, 10) })
        .then(function (list) {
          EVENTS = list;
          success(list.map(toFcEvent));
          if (firstLoad) pickInitial();
        })
        .catch(function (err) { toast(err.message); failure(err); });
    },

    eventContent: function (arg) {
      var e = findEvent(arg.event.id);
      var icon = e ? (ICONS[e.iconKey] || ICONS.EVENT) + ' ' : '';
      var pre = e && e.status === 'PENDING' ? '[調整中] ' : '';
      return { html: '<div>' + icon + esc(pre + arg.event.title) + '</div>' };
    },
    eventDidMount: function (info) { info.el.setAttribute('data-evid', info.event.id); },
    eventClick: function (info) {
      info.jsEvent.preventDefault();
      selectEvent(info.event.id);
    },

    // 日本語ロケールは「20日」と表示されるため「日」を取り除き、数字だけにする
    dayCellContent: function (arg) {
      return { html: arg.dayNumberText.replace('日', '') };
    },

    datesSet: function (info) {
      var d = info.view.currentStart;
      $('calTitle').textContent = info.view.type === 'dayGridMonth'
        ? d.getFullYear() + '年' + (d.getMonth() + 1) + '月'
        : info.view.title;
      document.querySelectorAll('#viewGroup button').forEach(function (b) {
        b.classList.toggle('active', b.getAttribute('data-view') === info.view.type);
      });
      setTimeout(function () { decorateDays(); highlightEvent(); }, 0);
    },
    eventsSet: function () { setTimeout(function () { decorateDays(); highlightEvent(); }, 0); },
    windowResize: function () { setTimeout(decorateDays, 0); }
  });
  calendar.render();

  // フォント・CSS読み込み後にレイアウトを再計算
  var resize = function () { if (calendar) calendar.updateSize(); };
  if (document.fonts && document.fonts.ready) { document.fonts.ready.then(resize); }
  window.addEventListener('load', resize);
  setTimeout(resize, 300);

  /* ---- ツールバー操作 ---- */
  $('btnPrev').addEventListener('click', function () { calendar.prev(); });
  $('btnNext').addEventListener('click', function () { calendar.next(); });
  $('btnToday').addEventListener('click', function () { calendar.today(); });
  $('viewGroup').addEventListener('click', function (e) {
    var v = e.target.getAttribute('data-view'); if (v) calendar.changeView(v);
  });

  /* =========================================================
   * 予定の 作成 / 編集 / 削除
   * ========================================================= */
  var dlg = $('createDialog');

  function syncType() {
    var edit = editingId !== null;
    var type = edit ? DETAIL.status : $('fType').value;
    $('rowSlots').classList.toggle('hide', edit || type !== 'PENDING');
    $('rowTime').classList.toggle('hide', edit || type !== 'CONFIRMED');
  }

  function openDialog(mode) {
    var edit = mode === 'edit' && DETAIL;
    editingId = edit ? DETAIL.id : null;
    $('dlgTitle').textContent = edit ? '予定を編集' : '予定を作成';
    $('rowType').classList.toggle('hide', !!edit);

    if (edit) {
      $('newTitle').value = DETAIL.title;
      $('newDate').value = DETAIL.startDate;
      $('newEnd').value = DETAIL.endDate;
      $('fIcon').value = DETAIL.iconKey;
      $('fColor').value = DETAIL.colorCls;
      $('fDeadline').checked = !!DETAIL.deadline;
      $('fMemo').value = DETAIL.memo || '';
    } else {
      $('fType').value = 'CONFIRMED';
      $('newTitle').value = '';
      $('newDate').value = ymd(new Date());
      $('newEnd').value = ymd(new Date());
      $('fSlots').value = '';
      $('fTime').value = '';
      $('fIcon').value = 'EVENT';
      $('fColor').value = 'blue';
      $('fDeadline').checked = false;
      $('fMemo').value = '';
    }
    $('newEnd').min = $('newDate').value;     // 終了日は開始日より前を選べない
    syncType();
    dlg.showModal();
  }

  /** "9:00, 14:00" → ["09:00","14:00"]（不正なら null） */
  function parseSlots(s) {
    var parts = s.split(/[,、\s]+/).filter(Boolean), out = [], i;
    if (!parts.length) return null;
    for (i = 0; i < parts.length; i++) {
      var t = normTime(parts[i]);
      if (!t) return null;
      if (out.indexOf(t) < 0) out.push(t);
    }
    return out;
  }

  function refreshAll() {
    calendar.refetchEvents();
    loadPending();
  }

  $('btnCreate').addEventListener('click', function () {
    if (!PID) { toast('プロジェクトを選択してください'); return; }
    openDialog('create');
  });
  $('fType').addEventListener('change', syncType);

  // 開始日を変えたら、終了日が開始日より前にならないよう自動で合わせる
  $('newDate').addEventListener('change', function () {
    var s = $('newDate').value;
    $('newEnd').min = s;
    if (s && (!$('newEnd').value || $('newEnd').value < s)) $('newEnd').value = s;
  });
  $('btnCancelCreate').addEventListener('click', function () { dlg.close(); });

  $('createForm').addEventListener('submit', function (e) {
    e.preventDefault();
    var edit = editingId !== null;
    var type = edit ? DETAIL.status : $('fType').value;

    var body = {
      title: $('newTitle').value.trim(),
      startDate: $('newDate').value,
      endDate: $('newEnd').value || $('newDate').value,
      iconKey: $('fIcon').value,
      colorCls: $('fColor').value,
      deadline: $('fDeadline').checked,
      memo: $('fMemo').value
    };
    if (!body.title || !body.startDate) return;
    if (body.endDate < body.startDate) { toast('終了日は開始日以降にしてください'); return; }

    if (edit) {
      body.eventId = editingId;
    } else {
      body.status = type;
      if (type === 'PENDING') {
        var slots = parseSlots($('fSlots').value);
        if (!slots) { toast('候補時間は「14:00, 15:00」の形式で1つ以上入力してください'); return; }
        body.slots = slots;
      } else if ($('fTime').value) {
        body.confirmedTime = $('fTime').value;
      }
    }

    var btn = $('btnSubmit'); btn.disabled = true;
    api(edit ? 'update' : 'create', {}, body).then(function (r) {
      dlg.close();
      toast(edit ? '予定を更新しました' : '予定を作成しました');
      refreshAll();
      if (edit) {
        loadDetail(selectedId, false);
      } else {
        calendar.gotoDate(body.startDate);
        selectEvent(r.eventId);
      }
    }).catch(function (err) {
      toast(err.message);
    }).then(function () { btn.disabled = false; });
  });

  function doDelete() {
    if (!DETAIL || !confirm('この予定を削除しますか？')) return;
    api('delete', {}, { eventId: DETAIL.id }).then(function () {
      toast('予定を削除しました');
      clearSelection();
      refreshAll();
    }).catch(function (err) { toast(err.message); });
  }

  /* ---- 参加 / 投票 / 候補追加 / 確定 ---- */
  function reloadAfterChange() {
    calendar.refetchEvents();                       // 投票バッジ更新
    if (selectedId) loadDetail(selectedId, false);
  }

  function normTime(s) {
    var m = /^(\d{1,2}):(\d{2})$/.exec(String(s).trim());
    if (!m || +m[1] > 23 || +m[2] > 59) return null;
    return (m[1].length < 2 ? '0' : '') + m[1] + ':' + m[2];
  }

  function doAttend(status) {
    api('attend', {}, { eventId: DETAIL.id, status: status })
      .then(reloadAfterChange)
      .catch(function (err) { toast(err.message); });
  }

  function doVote(slotId) {
    api('vote', {}, { eventId: DETAIL.id, slotId: slotId })
      .then(reloadAfterChange)
      .catch(function (err) { toast(err.message); reloadAfterChange(); });
  }

  function doAddSlot() {
    var t = prompt('追加する候補時間を入力してください（例：18:00）', '18:00');
    if (t === null) return;
    var n = normTime(t);
    if (!n) { toast('「18:00」の形式で入力してください'); return; }
    api('addSlot', {}, { eventId: DETAIL.id, time: n })
      .then(function () { toast('候補時間を追加しました'); reloadAfterChange(); })
      .catch(function (err) { toast(err.message); });
  }

  function doConfirm() {
    if (!selectedSlot) { toast('確定する時間を選択してください'); return; }
    if (!confirm(selectedSlot + ' で確定しますか？\n（候補時間と投票は削除されます）')) return;
    api('confirm', {}, { eventId: DETAIL.id, time: selectedSlot })
      .then(function () {
        selectedSlot = null;
        toast('日程を確定しました');
        refreshAll();
        loadDetail(selectedId, false);
      })
      .catch(function (err) { toast(err.message); });
  }

  /* =========================================================
   * 自動更新（30秒ごと／タブに戻ったとき）
   * ========================================================= */
  function poll() {
    if (document.hidden || !PID) return;
    calendar.refetchEvents();
    loadPending();
    if (selectedId) loadDetail(selectedId, true);
  }
  setInterval(poll, 30000);
  document.addEventListener('visibilitychange', function () { if (!document.hidden) poll(); });

  /* ---- 初期表示 ---- */
  renderDetail();
  if (PID) {
    loadMembers();
    loadPending();
  } else {
    $('btnCreate').disabled = true;
    $('pendingList').innerHTML = '';
    toast('参加中のプロジェクトがありません');
  }
})();
</script>

<%-- 공통 푸터: </body></html> 을 닫아준다면 이 파일에서는 닫지 않음 --%>
<%@ include file="/include/footer.jsp" %>
