create table if not exists sys_user(
    id int GENERATED BY DEFAULT AS IDENTITY,
    username text,
    password text,
    email text,
    role text
);