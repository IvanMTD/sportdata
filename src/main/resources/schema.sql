create table if not exists sys_user(
    id int GENERATED BY DEFAULT AS IDENTITY,
    username text,
    password text,
    email text,
    role text
);

create table if not exists subject(
    id int GENERATED BY DEFAULT AS IDENTITY,
    title text not null,
    iso text not null,
    federal_district text not null,
    sport_school_ids integer[],
    base_sport_ids integer[]
);

create table if not exists base_sport(
    id int GENERATED BY DEFAULT AS IDENTITY,
    subject_id int,
    type_of_sport_id int,
    issue_date date,
    expiration int
);

create table if not exists type_of_sport(
    id int GENERATED BY DEFAULT AS IDENTITY,
    title text not null,
    season text,
    sport_filter_type text,
    base_sport_ids integer[],
    discipline_ids integer[],
    age_group_ids integer[],
    qualification_ids integer[]
);

create table if not exists discipline(
    id int GENERATED BY DEFAULT AS IDENTITY,
    title text not null,
    type_of_sport_id int
);

create table if not exists age_group(
    id int GENERATED BY DEFAULT AS IDENTITY,
    title text,
    min_age int,
    max_age int,
    type_of_sport_id int
);

create table if not exists qualification(
    id int GENERATED BY DEFAULT AS IDENTITY,
    category text not null,
    type_of_sport_id int,
    participant_id int
);

create table if not exists participant(
    id int GENERATED BY DEFAULT AS IDENTITY,
    name text,
    middle_name text,
    lastname text not null,
    birthday date not null,
    sport_school_ids integer[],
    qualification_ids integer[]
);

create table if not exists sport_school(
    id int GENERATED BY DEFAULT AS IDENTITY,
    title text not null,
    address text,
    subject_id int,
    participant_ids integer[]
);

create table if not exists contest(
    id bigint GENERATED BY DEFAULT AS IDENTITY,
    ekp text,
    title text not null,
    city text,
    location text,
    contest_rule text,
    federal_rule text,
    beginning date,
    ending date,
    subject_id int,
    subject_title text,
    type_of_sport_id int,
    sport_title text,

    total_subjects integer[],
    first_place integer[],
    second_place integer[],
    last_place integer[],

    a_sport_ids bigint[],

    participant_total int,
    boy_total int,
    girl_total int,
    br int,
    yn1 int,
    yn2 int,
    yn3 int,
    r1 int,
    r2 int,
    r3 int,
    kms int,
    ms int,
    msmk int,
    zms int,

    trainer_total int,
    judge_total int,
    nonresident_judge int,
    mc int,
    vrc int,
    fc int,
    sc int,
    tc int,
    bc int,
    dc int,

    yn1_date int,
    yn2_date int,
    yn3_date int,
    r1_date int,
    r2_date int,
    r3_date int,
    kms_date int,
    ms_date int,
    msmk_date int,
    zms_date int,

    complete bool
);

create table if not exists archive_sport(
    id bigint GENERATED BY DEFAULT AS IDENTITY,
    contest_id bigint,
    discipline_id int,
    age_group_id int,
    place_ids bigint[],
    federal_standards text[],
    allowed text[]
);

create table if not exists place(
    id bigint GENERATED BY DEFAULT AS IDENTITY,
    a_sport_id bigint,
    participant_id int,
    qualification_id int,
    new_qualification_id int,
    result_category text,
    sport_school_id int,
    place int,

    parallel_school_id int,
    info text,

    condition text
);

create table if not exists news(
    id bigint GENERATED BY DEFAULT AS IDENTITY,
    user_id int,
    title text,
    content text,
    placed_at date,
    color text
);