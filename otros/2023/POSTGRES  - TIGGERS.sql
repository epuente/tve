CREATE OR REPLACE FUNCTION cuentaRolInsert()
 RETURNS "trigger" AS
 $BODY$
 BEGIN
   New.id:=nextval('cuenta_rol_seq');
   Return NEW;
 END;
 $BODY$
 LANGUAGE 'plpgsql' VOLATILE;


CREATE TRIGGER cuentaRolInsert
 BEFORE INSERT
 ON cuenta_rol
 FOR EACH ROW
 EXECUTE PROCEDURE cuentaRolInsert();