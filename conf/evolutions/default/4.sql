# --- Sample dataset

# --- !Ups
CREATE TRIGGER cuentaRolInsert
 BEFORE INSERT
 ON cuenta_rol
 FOR EACH ROW
 EXECUTE PROCEDURE cuentaRolInsert();

 # --- !Downs
