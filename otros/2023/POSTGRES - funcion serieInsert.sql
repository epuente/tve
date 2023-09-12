CREATE OR REPLACE FUNCTION public.serieinsert()
	RETURNS trigger
	LANGUAGE plpgsql
AS $function$
	BEGIN
		New.id:=nextval('serie_seq');
   		Return NEW;
	END;
$function$
;