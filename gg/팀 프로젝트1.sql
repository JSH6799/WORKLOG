


-- 회원
create table member (
    member_id varchar2(20) not null,
    pwd varchar2(200) not null,
    member_name varchar2(30) not null,
    email varchar2(100) not null,
    constraint pk_member primary key (member_id)
);

-- 프로젝트
create table project (
    project_id varchar2(20) not null,
    team_name varchar2(30) not null,
    project_name varchar2(30) not null,
    start_date date,
    end_date date,
    description varchar2(2000) not null,
    creator_id varchar2(20) not null,
    reated_date date default sysdate not null,
    constraint p_id primary key (project_id),
    constraint fk_creator_id foreign key (creator_id) references member (member_id)
);

-- 프로젝트 멤버
create table project_member (
    project_id varchar2(20) not null,
    member_id varchar2(20) not null,
    role  varchar2(30) not null,
    constraint pk_project_member primary key (project_id, member_id),
    constraint pro_id foreign key (project_id) references project (project_id),
    constraint pm_id foreign key (member_id) references member (member_id)
);


-- 게시판
create table board (
    bno number not null,
    title varchar2(200) not null,
    content clob not null,
    writer varchar2(20) not null,
    img_path varchar2(300),
    write_date date default sysdate,
    project_id varchar2(20) not null,
    check_required char(1) default 'N' not null,
    constraint board_bno primary key (bno),
    constraint board_writer foreign key (writer) references member(member_id),
    constraint board_p_id foreign key (project_id) references project (project_id)
);


-- 확인 여부
create table board_check (
    bno number not null,
    member_id varchar2(20) not null,
    check_yn char(1) default 'N' not null,
    check_date date,
    constraint pk_board_check primary key (bno, member_id),
    constraint ck_board_check_yn check (check_yn in ('Y', 'N')),
    constraint check_bno foreign key (bno) references board (bno),
    constraint check_id foreign key (member_id) references member (member_id)
);

-- 북마크
create table bookmark (
    bno number not null,
    member_id varchar2(20) not null,
    bookmark_date date default sysdate,
    constraint pk_bmk primary key (bno, member_id),
    constraint bmk_bno foreign key (bno) references board (bno),
    constraint bmk_m_id foreign key (member_id) references member (member_id)
);

-- 프로젝트 멤버 초대
CREATE TABLE project_invitation (
    invitation_id NUMBER NOT NULL,
    project_id VARCHAR2(20) NOT NULL,
    inviter_id VARCHAR2(20) NOT NULL,
    invitee_id VARCHAR2(20) NOT NULL,
    invitation_status VARCHAR2(10) DEFAULT 'PENDING' NOT NULL,
    invited_date DATE DEFAULT SYSDATE NOT NULL,
    responded_date DATE,
    CONSTRAINT pk_project_invitation PRIMARY KEY (invitation_id),
    CONSTRAINT fk_invitation_project FOREIGN KEY (project_id) REFERENCES project(project_id),
    CONSTRAINT fk_invitation_inviter FOREIGN KEY (inviter_id) REFERENCES member(member_id),
    CONSTRAINT fk_invitation_invitee FOREIGN KEY (invitee_id) REFERENCES member(member_id),
    CONSTRAINT ck_invitation_status CHECK (invitation_status IN ('PENDING', 'ACCEPTED', 'REJECTED'))
);

alter table project_invitation add role varchar2(20) default 'member' not null;
ALTER TABLE project_invitation MODIFY (invitee_id NULL);
ALTER TABLE project_invitation ADD (invite_code  VARCHAR2(20), expires_date DATE);
CREATE UNIQUE INDEX uq_invitation_code ON project_invitation (invite_code);

SELECT invitation_id,
       project_id,
       inviter_id,
       invitee_id,
       invitation_status,
       responded_date
FROM WORKLOG.project_invitation
WHERE invitee_id = 'aaa'
ORDER BY invitation_id DESC;

SELECT invitation_id, invitation_status
FROM WORKLOG.project_invitation
WHERE invitation_id = 23;

SELECT *
FROM WORKLOG.project_member
WHERE member_id = 'aaa';

SELECT
    invitation_id,
    project_id,
    inviter_id,
    invitee_id,
    role,
    invitation_status,
    invited_date
FROM WORKLOG.PROJECT_INVITATION
ORDER BY invited_date DESC;

SELECT project_id, member_id, role
FROM WORKLOG.PROJECT_MEMBER
WHERE project_id = 'PROJ_91539e68';
-- 초대 코드? 번호 자동 생성
CREATE SEQUENCE project_invitation_seq
START WITH 1
INCREMENT BY 1
NOCACHE;

-- 게시글 번호 자동 증가
CREATE SEQUENCE seq_board
START WITH 1
INCREMENT BY 1
NOCACHE
NOCYCLE;

-- 프로젝트 id 번호 자동 증가
-- sql문 예시 : insert into project (project_id, project_name) values ('pno_' || seq_project.NEXTVAL, ?)";
CREATE SEQUENCE seq_project
START WITH 1
INCREMENT BY 1
NOCACHE
NOCYCLE;


--캘린더용 시퀀스
create sequence seq_cal_event start with 1 increment by 1 nocache nocycle;
create sequence seq_event_slot start with 1 increment by 1 nocache nocycle;

-- 일정
create table calendar_event (
    event_id       number not null,
    project_id     varchar2(20) not null,
    title          varchar2(100 char) not null,
    memo           varchar2(2000 char),
    icon_key       varchar2(20) default 'EVENT' not null,
    color_cls      varchar2(20) default 'blue' not null,
    start_date     date not null,                    -- 시작일 (포함)
    end_date       date not null,                    -- 종료일 (포함)
    status         varchar2(10) default 'CONFIRMED' not null,
    is_deadline    char(1) default 'N' not null,
    confirmed_time varchar2(5),                      -- 확정 시간 'HH24:MI'
    created_by     varchar2(20) not null,
    created_date   date default sysdate not null,
    constraint pk_calendar_event primary key (event_id),
    constraint fk_ce_project foreign key (project_id) references project (project_id),
    constraint fk_ce_creator foreign key (created_by) references member (member_id),
    constraint ck_ce_status check (status in ('CONFIRMED', 'PENDING')),
    constraint ck_ce_deadline check (is_deadline in ('Y', 'N')),
    constraint ck_ce_color check (color_cls in ('blue', 'green', 'purple', 'red')),
    constraint ck_ce_period check (end_date >= start_date)
);


create index idx_ce_project_period on calendar_event (project_id, start_date, end_date);

-- 참가 여부 (YES / MAYBE / NO)
create table event_attendee (
    event_id      number not null,
    member_id     varchar2(20) not null,
    attend_status varchar2(5) default 'MAYBE' not null,
    constraint pk_event_attendee primary key (event_id, member_id),
    constraint fk_ea_event foreign key (event_id) references calendar_event (event_id) on delete cascade,
    constraint fk_ea_member foreign key (member_id) references member (member_id),
    constraint ck_ea_status check (attend_status in ('YES', 'MAYBE', 'NO'))
);

-- 후보 시간
create table event_slot (
    slot_id    number not null,
    event_id   number not null,
    slot_time  varchar2(5) not null,                 -- 'HH24:MI'
    created_by varchar2(20) not null,
    constraint pk_event_slot primary key (slot_id),
    constraint fk_es_event foreign key (event_id) references calendar_event (event_id) on delete cascade,
    constraint fk_es_creator foreign key (created_by) references member (member_id),
    constraint uq_es_event_time unique (event_id, slot_time)
);

-- 후보 시간 투표
create table slot_vote (
    slot_id    number not null,
    member_id  varchar2(20) not null,
    voted_date date default sysdate not null,
    constraint pk_slot_vote primary key (slot_id, member_id),
    constraint fk_sv_slot foreign key (slot_id) references event_slot (slot_id) on delete cascade,
    constraint fk_sv_member foreign key (member_id) references member (member_id)
);

commit;

SELECT *
FROM WORKLOG.PROJECT_MEMBER
WHERE PROJECT_ID = 'PROJ_2cedf687'
AND MEMBER_ID = 'qqq';

SELECT *
FROM WORKLOG.PROJECT_INVITATION
WHERE PROJECT_ID = 'PROJ_2cedf687'
AND INVITEE_ID = 'qqq'
ORDER BY INVITATION_ID DESC;

SELECT CASE WHEN EXISTS (
    SELECT 1
    FROM project_invitation pi
    WHERE pi.project_id = 'PROJ_91539e68'
      AND pi.invitee_id = 'aaa'
      AND pi.invitation_status = 'PENDING'
) THEN 'PENDING'
  ELSE 'NONE'
END AS invitation_status
FROM dual;

SELECT invitation_id,
       project_id,
       inviter_id,
       invitee_id,
       invitation_status
FROM WORKLOG.project_invitation
ORDER BY invited_date DESC;