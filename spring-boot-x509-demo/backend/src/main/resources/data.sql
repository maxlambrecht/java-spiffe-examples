
CREATE TABLE task(
   id INT NOT NULL,
   title VARCHAR(50) NOT NULL,
   completed BOOLEAN NOT NULL
);

insert into task(id, title, completed) values(1, 'Write SPIFFE tutorial', false);
insert into task(id, title, completed) values(2, 'Review PR', false);
insert into task(id, title, completed) values(3, 'Call my Mom', false);
