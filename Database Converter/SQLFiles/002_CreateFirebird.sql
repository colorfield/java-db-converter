create table people (
  id int not null,
  nickname varchar(12) not null,
  country char(4),
  constraint pk_people primary key (id)
)

commit work;

insert into people values (1,'test1','BLGM');
insert into people values (2,'test2','NDLS');
insert into people values (3,'test3','ITLY');

commit work;



/*-------------------------------------------*/

/* -- pas de `,
pas de AUTO INC,
pas de NULL,
TEXT = BLOB SUB_TYPE TEXT,
INT n'a pas de size

ajout de commit work après DCL et DML
*/

CREATE TABLE  people (
    id INT NOT NULL PRIMARY KEY ,
    firstName VARCHAR( 50 ) NOT NULL ,
    lastName VARCHAR( 50 ) NOT NULL ,
    birthdate DATE ,
    resume BLOB SUB_TYPE TEXT
);

CREATE TABLE department (
  id int NOT NULL,
  dName varchar(50) NOT NULL,
  dSize int NOT NULL,
  constraint pk_people primary key (id)
);

CREATE TABLE departmentPeople (
   idDepartment int NOT NULL,
   idPeople int NOT NULL,
   constraint pk_dptPeople primary key (idDepartment,idPeople)
);

commit work;

insert into people (id,firstName,lastName,resume) values (1,'testP','testN','test test test');
insert into people (id,firstName,lastName,resume) values (2,'testP2','testN','test test test');
insert into people (id,firstName,lastName,resume) values (3,'testP3','testN','test test test');

insert into department values(1,'dpt1',50);
insert into department values(2,'dpt2',500);
insert into department values(3,'dpt3',5000);

insert into departmentPeople values (1,2);
insert into departmentPeople values (2,2);



commit work;