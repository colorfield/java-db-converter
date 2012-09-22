-- phpMyAdmin SQL Dump
-- version 3.2.4
-- http://www.phpmyadmin.net
--
-- Serveur: localhost
-- Généré le : Dim 13 Février 2011 à 12:53
-- Version du serveur: 5.1.37
-- Version de PHP: 5.2.11

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

--
-- Base de données: `dbconvert01`
--

-- --------------------------------------------------------

--
-- Structure de la table `department`
--

CREATE TABLE `department` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dName` varchar(50) NOT NULL,
  `dSize` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=3 ;

--
-- Contenu de la table `department`
--

INSERT INTO `department` VALUES(1, 'department1', 500);
INSERT INTO `department` VALUES(2, 'department2', 300);

-- --------------------------------------------------------

--
-- Structure de la table `departmentPeople`
--

CREATE TABLE `departmentPeople` (
  `idDepartment` int(11) NOT NULL,
  `idPeople` int(11) NOT NULL,
  PRIMARY KEY (`idDepartment`,`idPeople`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Contenu de la table `departmentPeople`
--

INSERT INTO `departmentPeople` VALUES(1, 2);
INSERT INTO `departmentPeople` VALUES(2, 1);

-- --------------------------------------------------------

--
-- Structure de la table `people`
--

CREATE TABLE `people` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `firstName` varchar(50) NOT NULL,
  `lastName` varchar(50) NOT NULL,
  `birthdate` date DEFAULT NULL,
  `resume` text,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=3 ;

--
-- Contenu de la table `people`
--

INSERT INTO `people` VALUES(1, 'prenom1', 'nom1', '2011-01-24', 'resume1');
INSERT INTO `people` VALUES(2, 'prenom2', 'nom2', '2011-01-24', 'resume2');

-- --------------------------------------------------------

--
-- Structure de la table `society`
--

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `dbconvert01`.`society` AS select `D`.`id` AS `deptId`,`D`.`dName` AS `dName`,`P`.`id` AS `peopleId`,`P`.`firstName` AS `firstName`,`P`.`lastName` AS `lastName` from ((`dbconvert01`.`department` `D` join `dbconvert01`.`departmentPeople` `DP` on((`D`.`id` = `DP`.`idDepartment`))) join `dbconvert01`.`people` `P` on((`P`.`id` = `DP`.`idPeople`)));

--
-- Contenu de la table `society`
--

INSERT INTO `society` VALUES(1, 'department1', 2, 'prenom2', 'nom2');
INSERT INTO `society` VALUES(2, 'department2', 1, 'prenom1', 'nom1');
