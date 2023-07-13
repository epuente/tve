CREATE OR REPLACE FUNCTION public.cuentarolinsert()
 RETURNS trigger
 LANGUAGE plpgsql
AS $function$
 BEGIN
   New.id:=nextval('cuenta_rol_seq');
   Return NEW;
 END;
 $function$
;
