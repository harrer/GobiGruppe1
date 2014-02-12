SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

CREATE SCHEMA IF NOT EXISTS `gobi1` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;
USE `gobi1` ;

-- -----------------------------------------------------
-- Table `gobi1`.`gene`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gobi1`.`gene` (
  `geneid` VARCHAR(15) NOT NULL,
  `strand` TINYINT(1) NOT NULL,
  `chromosome` VARCHAR(10) NULL,
  PRIMARY KEY (`geneid`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `gobi1`.`transcript`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gobi1`.`transcript` (
  `transcriptid` VARCHAR(20) NOT NULL,
  `proteinid` VARCHAR(20) NOT NULL,
  `gene_geneid` VARCHAR(15) NOT NULL,
  PRIMARY KEY (`transcriptid`, `proteinid`, `gene_geneid`),
  INDEX `fk_transcript_gene1_idx` (`gene_geneid` ASC),
  CONSTRAINT `fk_transcript_gene1`
    FOREIGN KEY (`gene_geneid`)
    REFERENCES `gobi1`.`gene` (`geneid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `gobi1`.`exon`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gobi1`.`exon` (
  `id` INT NOT NULL,
  `start` INT UNSIGNED NOT NULL,
  `stop` INT UNSIGNED NOT NULL,
  `frame` TINYINT NOT NULL,
  `transcript_transcriptid` VARCHAR(20) NOT NULL,
  `transcript_proteinid` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`id`, `transcript_transcriptid`, `transcript_proteinid`),
  INDEX `fk_exon_transcript1_idx` (`transcript_transcriptid` ASC, `transcript_proteinid` ASC),
  CONSTRAINT `fk_exon_transcript1`
    FOREIGN KEY (`transcript_transcriptid` , `transcript_proteinid`)
    REFERENCES `gobi1`.`transcript` (`transcriptid` , `proteinid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `gobi1`.`pdb`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gobi1`.`pdb` (
  `pdbid` VARCHAR(7) NOT NULL,
  `filepath` VARCHAR(200) NOT NULL,
  PRIMARY KEY (`pdbid`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `gobi1`.`event`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gobi1`.`event` (
  `id` INT NOT NULL,
  `start` INT UNSIGNED NOT NULL,
  `stop` INT UNSIGNED NOT NULL,
  `secstructure` CHAR NULL,
  `acc` FLOAT NULL,
  `pdb_pdbid` VARCHAR(7) NOT NULL,
  PRIMARY KEY (`id`, `pdb_pdbid`),
  INDEX `fk_event_pdb1_idx` (`pdb_pdbid` ASC),
  CONSTRAINT `fk_event_pdb1`
    FOREIGN KEY (`pdb_pdbid`)
    REFERENCES `gobi1`.`pdb` (`pdbid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `gobi1`.`pattern`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gobi1`.`pattern` (
  `name` VARCHAR(15) NOT NULL,
  `pattern` VARCHAR(1000) NOT NULL,
  `description` VARCHAR(120) NULL,
  `link` VARCHAR(45) NULL,
  PRIMARY KEY (`name`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `gobi1`.`patternevent`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gobi1`.`patternevent` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `start` INT UNSIGNED NOT NULL,
  `stop` INT UNSIGNED NOT NULL,
  `patternid` VARCHAR(15) NOT NULL,
  `event_id` INT NOT NULL,
  `pattern_name` VARCHAR(15) NOT NULL,
  PRIMARY KEY (`id`, `event_id`, `pattern_name`),
  INDEX `fk_patternevent_event1_idx` (`event_id` ASC),
  INDEX `fk_patternevent_pattern1_idx` (`pattern_name` ASC),
  CONSTRAINT `fk_patternevent_event1`
    FOREIGN KEY (`event_id`)
    REFERENCES `gobi1`.`event` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_patternevent_pattern1`
    FOREIGN KEY (`pattern_name`)
    REFERENCES `gobi1`.`pattern` (`name`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `gobi1`.`transcript_has_transcript`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gobi1`.`transcript_has_transcript` (
)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `gobi1`.`transcript_has_transcript1`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gobi1`.`transcript_has_transcript1` (
  `transcript_transcriptid` VARCHAR(20) NOT NULL,
  `transcript_proteinid` VARCHAR(20) NOT NULL,
  `transcript_transcriptid1` VARCHAR(20) NOT NULL,
  `transcript_proteinid1` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`transcript_transcriptid`, `transcript_proteinid`, `transcript_transcriptid1`, `transcript_proteinid1`),
  INDEX `fk_transcript_has_transcript1_transcript2_idx` (`transcript_transcriptid1` ASC, `transcript_proteinid1` ASC),
  INDEX `fk_transcript_has_transcript1_transcript1_idx` (`transcript_transcriptid` ASC, `transcript_proteinid` ASC),
  CONSTRAINT `fk_transcript_has_transcript1_transcript1`
    FOREIGN KEY (`transcript_transcriptid` , `transcript_proteinid`)
    REFERENCES `gobi1`.`transcript` (`transcriptid` , `proteinid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_transcript_has_transcript1_transcript2`
    FOREIGN KEY (`transcript_transcriptid1` , `transcript_proteinid1`)
    REFERENCES `gobi1`.`transcript` (`transcriptid` , `proteinid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `gobi1`.`transcript_has_event`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gobi1`.`transcript_has_event` (
  `transcript_transcriptid` VARCHAR(20) NOT NULL,
  `transcript_proteinid` VARCHAR(20) NOT NULL,
  `event_id` INT NOT NULL,
  PRIMARY KEY (`transcript_transcriptid`, `transcript_proteinid`, `event_id`),
  INDEX `fk_transcript_has_event_event1_idx` (`event_id` ASC),
  INDEX `fk_transcript_has_event_transcript1_idx` (`transcript_transcriptid` ASC, `transcript_proteinid` ASC),
  CONSTRAINT `fk_transcript_has_event_transcript1`
    FOREIGN KEY (`transcript_transcriptid` , `transcript_proteinid`)
    REFERENCES `gobi1`.`transcript` (`transcriptid` , `proteinid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_transcript_has_event_event1`
    FOREIGN KEY (`event_id`)
    REFERENCES `gobi1`.`event` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `gobi1`.`event_has_transcript2`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gobi1`.`event_has_transcript2` (
  `event_id` INT NOT NULL,
  `transcript_transcriptid` VARCHAR(20) NOT NULL,
  `transcript_proteinid` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`event_id`, `transcript_transcriptid`, `transcript_proteinid`),
  INDEX `fk_event_has_transcript_transcript1_idx` (`transcript_transcriptid` ASC, `transcript_proteinid` ASC),
  INDEX `fk_event_has_transcript_event1_idx` (`event_id` ASC),
  CONSTRAINT `fk_event_has_transcript_event1`
    FOREIGN KEY (`event_id`)
    REFERENCES `gobi1`.`event` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_event_has_transcript_transcript1`
    FOREIGN KEY (`transcript_transcriptid` , `transcript_proteinid`)
    REFERENCES `gobi1`.`transcript` (`transcriptid` , `proteinid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `gobi1`.`event_has_transcript1`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gobi1`.`event_has_transcript1` (
  `event_id` INT NOT NULL,
  `transcript_transcriptid` VARCHAR(20) NOT NULL,
  `transcript_proteinid` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`event_id`, `transcript_transcriptid`, `transcript_proteinid`),
  INDEX `fk_event_has_transcript1_transcript1_idx` (`transcript_transcriptid` ASC, `transcript_proteinid` ASC),
  INDEX `fk_event_has_transcript1_event1_idx` (`event_id` ASC),
  CONSTRAINT `fk_event_has_transcript1_event1`
    FOREIGN KEY (`event_id`)
    REFERENCES `gobi1`.`event` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_event_has_transcript1_transcript1`
    FOREIGN KEY (`transcript_transcriptid` , `transcript_proteinid`)
    REFERENCES `gobi1`.`transcript` (`transcriptid` , `proteinid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `gobi1`.`transcript_has_pdb`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gobi1`.`transcript_has_pdb` (
  `transcript_transcriptid` VARCHAR(20) NOT NULL,
  `transcript_proteinid` VARCHAR(20) NOT NULL,
  `transcript_gene_geneid` VARCHAR(15) NOT NULL,
  `pdb_pdbid` VARCHAR(7) NOT NULL,
  PRIMARY KEY (`transcript_transcriptid`, `transcript_proteinid`, `transcript_gene_geneid`, `pdb_pdbid`),
  INDEX `fk_transcript_has_pdb_pdb1_idx` (`pdb_pdbid` ASC),
  INDEX `fk_transcript_has_pdb_transcript1_idx` (`transcript_transcriptid` ASC, `transcript_proteinid` ASC, `transcript_gene_geneid` ASC),
  CONSTRAINT `fk_transcript_has_pdb_transcript1`
    FOREIGN KEY (`transcript_transcriptid` , `transcript_proteinid` , `transcript_gene_geneid`)
    REFERENCES `gobi1`.`transcript` (`transcriptid` , `proteinid` , `gene_geneid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_transcript_has_pdb_pdb1`
    FOREIGN KEY (`pdb_pdbid`)
    REFERENCES `gobi1`.`pdb` (`pdbid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
