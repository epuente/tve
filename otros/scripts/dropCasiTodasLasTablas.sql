SET FOREIGN_KEY_CHECKS = 0;
SET GROUP_CONCAT_MAX_LEN=32768;
SET @tables = NULL;
SELECT GROUP_CONCAT('`', table_name, '`') INTO @tables
  FROM information_schema.tables
  WHERE table_schema = 'tveservicios2021_dev' and TABLE_NAME <> 'play_evolutions';
SELECT IFNULL(@tables,'dummy') INTO @tables;

SET @tables = CONCAT('DROP TABLE IF EXISTS ', @tables);
PREPARE stmt FROM @tables;
EXECUTE stmt;
TRUNCATE TABLE tveservicios2021_dev.play_evolutions;
DEALLOCATE PREPARE stmt;
SET FOREIGN_KEY_CHECKS = 1;