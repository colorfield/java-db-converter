/*
Méta-données simplistes :
pas de vues, pas de trigger, pas de procédures stockées
pas d'événements (on delete cascade, ...)
*/

CREATE TABLE  `dbconvert01`.`people` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY ,
    `firstName` VARCHAR( 50 ) NOT NULL ,
    `lastName` VARCHAR( 50 ) NOT NULL ,
    `birthdate` DATE NULL ,
    `resume` TEXT NULL
) ENGINE = MYISAM ;


CREATE TABLE `department` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dName` varchar(50) NOT NULL,
  `dSize` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM ;

CREATE TABLE `dbconvert01`.`departmentPeople` (
   `idDepartment` int(11) NOT NULL,
   `idPeople` int(11) NOT NULL,
   PRIMARY KEY (`idDepartment`,`idPeople`)
) ENGINE=MyISAM ;


/* Vues */

CREATE VIEW society AS
SELECT D.id AS deptId, D.dName,
       P.id AS peopleId, P.firstName, P.lastName
FROM (department D JOIN departmentPeople DP ON D.id = DP.idDepartment)
     JOIN people P ON P.id = DP.idPeople;