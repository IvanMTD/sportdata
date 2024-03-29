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
    id int GENERATED BY DEFAULT AS IDENTITY,
    ekp text,
    title text not null,
    city text,
    location text,
    contest_rule text,
    federal_rule text,
    beginning date,
    ending date,
    subject_id int,
    type_of_sport_id int,

    total_subjects integer[],
    first_place integer[],
    second_place integer[],
    last_place integer[],

    a_sport_ids integer[],
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
    zms int
);

create table if not exists archive_sport(
    id int GENERATED BY DEFAULT AS IDENTITY,
    contest_id int,
    type_of_sport_id int,
    discipline_id int,
    age_group_id int,
    place_ids integer[],
    federal_standards text[],
    allowed text[]
);

create table if not exists place(
    id int GENERATED BY DEFAULT AS IDENTITY,
    a_sport_id int,
    participant_id int,
    qualification_id int,
    new_qualification_id int,
    sport_school_id int,
    place int,
    condition text
);