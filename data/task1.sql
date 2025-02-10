-- Write your Task 1 answers in this file
drop database if exists bedandbreakfast;

create database bedandbreakfast;
use bedandbreakfast;

drop table if exists users;
drop table if exists bookings;
drop table if exists reviews;

create table users (
    email varchar(128),
    name varchar(128),
    constraint pk_email primary key(email)
);

create table bookings (
    booking_id char(8),
    listing_id varchar(20), 
    duration int,
    email varchar(128),
    constraint pk_booking_id primary key(booking_id),
    constraint fk_email foreign key(email) references users(email)
);

create table reviews (
    id int not null auto_increment,
    date timestamp,
    listing_id varchar(20),
    reviewer_name varchar(64),
    comments text,
    constraint pk_id primary key(id)
);

grant all privileges on bedandbreakfast.* to 'chloe'@'%';
flush privileges;

select "Test";

load data local infile "data/users.csv"
into table users
fields terminated by ', '
lines terminated by '\n'
ignore 1 lines;