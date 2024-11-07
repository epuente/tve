CREATE DEFINER=`root`@`localhost` FUNCTION `sys`.`migracionDev`() RETURNS int(11)
BEGIN


     DECLARE nempleado, numEmpleado, paterno, materno, nombre, turno, tipopersonal, activo, usu, pass CHAR(100);
    DECLARE maxPersonal int;
    DECLARE done INT DEFAULT 0;


    declare curPersonal  cursor for
		select pernumempleado_pk, perpaterno, permaterno, pernombre, perturno, pertipopersonal_fk, peractivo from tves2016.cat_personal;
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;


    delete from tv2017_dev.folio_productor_asignado;
    delete from tv2017_dev.cuenta_rol;
    delete from tv2017_dev.cuenta;
    delete from tv2017_dev.personal;


    open curPersonal;
	loopPersonal: loop
		fetch curPersonal into nempleado, paterno, materno, nombre, turno, tipopersonal, activo;
        if done then
			leave loopPersonal;
		end if;
        select  ifnull(max(id),0) into maxPersonal from tv2017_dev.personal;
		insert into tv2017_dev.personal values (maxPersonal+1,nempleado, paterno, materno, nombre, tipopersonal, null,  turno, activo, null, null);
        bloqueCuentas: begin
			DECLARE done2 INT DEFAULT 0;
            declare maxCuenta int;
			declare cur1 cursor for
				SELECT ctapersonal_pf, ctausername_pk, ctapassword
				FROM tves2016.cat_personal, tves2016.cuentas
				where pernumempleado_pk  =  nempleado and pernumempleado_pk = ctapersonal_pf;
        	DECLARE CONTINUE HANDLER FOR not found SET done2 = 1;
			OPEN cur1;
			loopCuenta: loop
				FETCH cur1 INTO numEmpleado, usu, pass;
                if done2 then
					leave loopCuenta;
				end if;
                if numEmpleado = nempleado then
					select ifnull(max(id),0) into maxCuenta from tv2017_dev.cuenta;
					insert into tv2017_dev.cuenta values (maxCuenta+1, maxPersonal+1,  usu, pass, null, null);
				end if;

		        bloqueCuentasRoles: begin
					DECLARE done3 INT DEFAULT 0;
                    declare rolid int;
                    declare crEmpleado char(100);
					declare cur3 cursor for select clrpersonal_pf, clrrol_fk from tves2016.cuentasroles where clrpersonal_pf = numempleado;
		        	DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET done3 = 1;
					OPEN cur3;
					loopCuentaRol: loop
						FETCH cur3 INTO crEmpleado, rolid;
                        if done3 then
							leave loopCuentaRol;
						end if;
		                if nempleado = crEmpleado and nempleado = numEmpleado then
							insert into tv2017_dev.cuenta_rol values (null, maxCuenta+1,  if(rolid=0,1, rolid), null, null);
						end if;
					end loop;
					CLOSE cur3;
				end bloqueCuentasRoles;



			end loop;
			CLOSE cur1;
		end bloqueCuentas;

    end loop;


    close curPersonal;


/*migracion00*/
INSERT INTO tv2017_dev.tipo_contrato (id, descripcion, audit_insert, audit_update) VALUES(1, 'Nómina', '2018-12-14 12:00:00.000', '2018-12-14 12:00:00.000');
INSERT INTO tv2017_dev.tipo_contrato (id, descripcion, audit_insert, audit_update) VALUES(2, 'Honorarios', '2018-12-14 12:00:00.000', '2018-12-14 12:00:00.000');
INSERT INTO tv2017_dev.tipo_contrato (id, descripcion, audit_insert, audit_update) VALUES(3, 'Freelance', '2018-12-14 12:00:00.000', '2018-12-14 12:00:00.000');

INSERT INTO tv2017_dev.motivo_cancelacion (id, descripcion, audit_insert, audit_update) VALUES(1, 'Cancelado por el área solcicitante', '2018-12-14 12:00:00.000', '2018-12-14 12:00:00.000');
INSERT INTO tv2017_dev.motivo_cancelacion (id, descripcion, audit_insert, audit_update) VALUES(2, 'Falta de personal', '2018-12-14 12:00:00.000', '2018-12-14 12:00:00.000');
INSERT INTO tv2017_dev.motivo_cancelacion (id, descripcion, audit_insert, audit_update) VALUES(3, 'Falta de vehívulo', '2018-12-14 12:00:00.000', '2018-12-14 12:00:00.000');
INSERT INTO tv2017_dev.motivo_cancelacion (id, descripcion, audit_insert, audit_update) VALUES(4, 'Falta de combustible', '2018-12-14 12:00:00.000', '2018-12-14 12:00:00.000');
INSERT INTO tv2017_dev.motivo_cancelacion (id, descripcion, audit_insert, audit_update) VALUES(5, 'ECU cerrada', '2018-12-14 12:00:00.000', '2018-12-14 12:00:00.000');


/*migracion001*/
DECLARE nempleado, numEmpleado, paterno, materno, nombre, turno, tipopersonal, activo, usu, pass CHAR(100);
DECLARE maxPersonal int;
DECLARE done INT DEFAULT 0;

declare curPersonal  cursor for
    select pernumempleado_pk, perpaterno, permaterno, pernombre, perturno, pertipopersonal_fk, peractivo from tves2016.cat_personal;
DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;




delete from tv2017_dev.cuenta_rol;
delete from tv2017_dev.cuenta;
delete from tv2017_dev.personal_horario;
delete from tv2017_dev.personal;


delete from tv2017_dev.operador_sala;
delete from tv2017_dev.personal;


delete from tv2017_dev.oficio_contacto_telefono;
delete from tv2017_dev.oficio_contacto_correo;
delete from tv2017_dev.oficio_contacto;
delete from tv2017_dev.oficio_fecha_grabacion;




open curPersonal;
loopPersonal: loop
    fetch curPersonal into nempleado, paterno, materno, nombre, turno, tipopersonal, activo;
    if done then
        leave loopPersonal;
    end if;
    select  ifnull(max(id),0) into maxPersonal from tv2017_dev.personal;
    insert into tv2017_dev.personal values (maxPersonal+1,nempleado, paterno, materno, nombre, tipopersonal, 1, turno,  activo, null, null);


    bloqueCuentas: begin
        DECLARE done2 INT DEFAULT 0;
        declare maxCuenta int;
        declare cur1 cursor for
            SELECT ctapersonal_pf, ctausername_pk, ctapassword
            FROM tves2016.cat_personal, tves2016.cuentas
            where pernumempleado_pk  =  nempleado and pernumempleado_pk = ctapersonal_pf;
        DECLARE CONTINUE HANDLER FOR not found SET done2 = 1;
        OPEN cur1;
        loopCuenta: loop
            FETCH cur1 INTO numEmpleado, usu, pass;
            if done2 then
                leave loopCuenta;
            end if;
            if numEmpleado = nempleado then
                select ifnull(max(id),0) into maxCuenta from tv2017_dev.cuenta;
                insert into tv2017_dev.cuenta values (maxCuenta+1, maxPersonal+1,  usu, pass, null, null);
            end if;

            bloqueCuentasRoles: begin
                DECLARE done3 INT DEFAULT 0;
                declare rolid int;
                declare crEmpleado char(100);
                declare cur3 cursor for select clrpersonal_pf, clrrol_fk from tves2016.cuentasroles where clrpersonal_pf = numempleado;
                DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET done3 = 1;
                OPEN cur3;
                loopCuentaRol: loop
                    FETCH cur3 INTO crEmpleado, rolid;
                    if done3 then
                        leave loopCuentaRol;
                    end if;
                    if nempleado = crEmpleado and nempleado = numEmpleado then
                        insert into tv2017_dev.cuenta_rol values (null, maxCuenta+1,  if(rolid=0,1, rolid), null, null);
                    end if;
                end loop;
                CLOSE cur3;
            end bloqueCuentasRoles;



        end loop;
        CLOSE cur1;
    end bloqueCuentas;

end loop;


close curPersonal;

insert into tv2017_dev.operador_sala (id, personal_id, sala_id, turno, audit_insert, audit_update)
select null, personal.id, sala.id,  atsturno_pk, '2013-02-07 11:38:58', '2013-02-07 11:38:58'
from tves2016.atencionsala inner join tv2017_dev.personal on atspersonal_pf = personal.num_empleado
inner join tv2017_dev.sala on atssala_pf = sala.id;


/*migracion002*/
DECLARE nOficio, remitente, titulo, connombre, contel, concorreo CHAR(255);
DECLARE descripcion, observaciones  VARCHAR(600);
DECLARE fecharemitente, fecharecepcion, auditInsert, fechagrabacion, fechaentrega DATE;
DECLARE maxOficio, maxOficioContacto, maxFolio, servicio, productorsol   int;
DECLARE done INT DEFAULT 0;

DECLARE folio, estado  int(20);

declare curOficio  cursor for
    select folnumoficio, unidad_responsable.id ur,  foldescripcion, foltitulo, folfecharemitente, folrecepcion, folobservaciones, folaudit_insert,
    folserviciosolicitado_fk,
    folfechagrabacion,    folfechaentrega, cat_folios.folaudit_insert,
    folcontactonombre, folcontactotel, folcontactocorreo,
    personal.id,
    folnumfolio_pk, if(folestado_fk!=99,folestado_fk+1, 99)
    from  (
            (tves2016.cat_folios inner join tv2017_dev.unidad_responsable on folurremitente_fk = clave)
            left join tv2017_dev.personal on folproductorsol_fk = num_empleado
         );
DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

delete from tv2017_dev.folio_productor_asignado;
delete from tv2017_dev.folio;

delete from tv2017_dev.oficio_contacto_telefono;
delete from tv2017_dev.oficio_contacto_correo;
delete from tv2017_dev.oficio_contacto;
delete from tv2017_dev.oficio_fecha_grabacion;
delete from tv2017_dev.oficio_productor_solicitado;
delete from tv2017_dev.oficio_servicio_solicitado;

delete from tv2017_dev.oficio;


open curOficio;
loopOficio: loop
    fetch curOficio into nOficio, remitente, descripcion, titulo, fecharemitente, fecharecepcion, observaciones, auditInsert,
    servicio,
    fechagrabacion,  fechaentrega, auditInsert,
    connombre, contel, concorreo,
    productorsol,
    folio, estado;
    if done then
        leave loopOficio;
    end if;
    select  ifnull(max(id),0) into maxOficio from tv2017_dev.oficio;
    insert into tv2017_dev.oficio values (maxOficio+1, nOficio, remitente, descripcion, titulo, fecharemitente, fecharecepcion, observaciones, auditInsert, ifnull(auditInsert,now()));
    if length(connombre) != 0 then
        select  ifnull(max(id),0) into maxOficioContacto from tv2017_dev.oficio_contacto;
        insert into tv2017_dev.oficio_contacto values(maxOficioContacto+1, maxOficio+1, connombre, auditInsert, ifnull(auditInsert,now()));
        if length(contel) != 0 then
            insert into tv2017_dev.oficio_contacto_telefono values(null, maxOficioContacto+1, contel, auditInsert, ifnull(auditInsert,now()));
        end if;
        if length(concorreo) != 0 then
            insert into tv2017_dev.oficio_contacto_correo values(null, maxOficioContacto+1, concorreo, ifnull(auditInsert,now()), ifnull(auditInsert,now()));
        end if;
    end if;
    if length(fechagrabacion) != 0 then
        insert into tv2017_dev.oficio_fecha_grabacion values(null, maxOficio+1, fechagrabacion, fechagrabacion, auditInsert, ifnull(auditInsert,now()));
    end if;
    if productorsol is not null then
        insert into tv2017_dev.oficio_productor_solicitado values(null, maxOficio+1, productorsol, auditInsert, ifnull(auditInsert,now()));
    end if;
    if servicio is not null then
        insert into tv2017_dev.oficio_servicio_solicitado values(null, maxOficio+1, servicio, auditInsert, ifnull(auditInsert,now()));
    end if;

    /*OFICIO*/
    select  ifnull(max(id),0) into maxFolio from tv2017_dev.folio;
    insert into tv2017_dev.folio values (maxFolio+1, folio, maxOficio+1, estado, fechaentrega, observaciones, auditInsert, ifnull(auditInsert,now()));

    /*FOLIO_PRODUCTOR_ASIGNADO*/
    bloqueProductorAsignado: begin
        DECLARE donePA INT DEFAULT 0;
        DECLARE productorAsignado, maxFolioProductor bigint(20);
        declare curPA  cursor for
                select personal.id from tves2016.productorasignado inner join tv2017_dev.personal on pprnumfolio_pf = folio and pprnumempleado_pf = personal.num_empleado;
        DECLARE CONTINUE HANDLER FOR NOT FOUND SET donePA = 1;
        open curPA;
        loopPA: loop
            fetch curPA into productorAsignado;
            if donePA then
                leave loopPa;
            end if;
            if productorAsignado is not null then
                select  ifnull(max(id),0) into maxFolioProductor from tv2017_dev.folio_productor_asignado;
                insert into tv2017_dev.folio_productor_asignado values (maxFolioProductor+1, maxFolio+1,  productorAsignado, auditInsert, ifnull(auditInsert,now()) );
            end if;
        end loop loopPA;
        close curPA;
    end bloqueProductorAsignado;



end loop;


close curOficio;


/*migracion003*/
DECLARE auditInsert DATE;
DECLARE folioid, estado bigint(20);
DECLARE done INT DEFAULT 0;


declare curHisto cursor for
    select folio.id, if(hfoestado_pf!=99,hfoestado_pf+1, 99), audit_insert from tves2016.his_folios inner join tv2017_dev.folio on hfonumfolio_pf = folio.folio;


DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

delete from tv2017_dev.his_folio;

open curHisto;
loopH: loop
    fetch curHisto into folioid, estado, auditInsert;
    if done then
        leave loopH;
    end if;
    insert into tv2017_dev.his_folio values (null, folioid, estado, auditInsert, auditInsert);

end loop;
close curHisto;
update tv2017_dev.his_folio set estado_id=7 where estado_id=6;



/*migracion004*/
DECLARE donePAServicio INT DEFAULT 0;
DECLARE nomfolio, folio, folioproductor, fase, secuencial, estado, maxPAS bigint(20);
DECLARE inicio, fin, auditInsertPAS DATETIME;
DECLARE salida, lugar, observacion varchar(255);
DECLARE monitor varchar(255);
declare curPAS  cursor for
select pagnumfolio_pf, folio.id, folio_productor_asignado.id, fase.id, pagsecuencial_pk,
concat(pagfechainicial," ", paghorasalida),
concat(if(pagfechainicial='0000-00-00', pagfechafinal, pagfechainicial)," ",if(paghorainicial='00:00:00',date_format(now(),'%H:%m:%s'), paghorainicial)),
concat(if(pagfechafinal='0000-00-00', pagfechainicial, pagfechafinal)," ",if(paghorafinal='00:00:00',date_format(now(),'%H:%m:%s'), paghorafinal)),
if(pagestado_fk!=99,pagestado_fk+1, pagestado_fk), paglugar, pagobservaciones, ageaudit_insert
from tv2017_dev.oficio 	inner join tv2017_dev.folio on oficio.id = folio.oficio_id
        inner join tv2017_dev.folio_productor_asignado on folio.id = folio_productor_asignado.folio_id
        inner join tves2016.preagenda on pagnumfolio_pf = tv2017_dev.folio.folio
        inner join tv2017_dev.fase on pagfase_pf = fase.id;

DECLARE CONTINUE HANDLER FOR NOT FOUND SET donePAServicio = 1;

delete from tv2017_dev.pre_agenda_vehiculo;
delete from tv2017_dev.pre_agenda_tipo_personal;
delete from tv2017_dev.pre_agenda_formato_entrega;
delete from tv2017_dev.pre_agenda_accesorio;
delete from tv2017_dev.pre_agenda_equipo;
delete from tv2017_dev.pre_agenda_locacion;
delete from tv2017_dev.pre_agenda_salida;
delete from tv2017_dev.pre_agenda_sala_uso_cabina;
delete from tv2017_dev.pre_agenda_sala;
delete from tv2017_dev.pre_agenda;
open curPAS;
loopPAS: loop
    fetch curPAS into nomfolio, folio, folioproductor, fase, secuencial, salida, inicio, fin, estado, lugar, observacion, auditInsertPAS;
    if donePAServicio then
        leave loopPaS;
    end if;
    select  ifnull(max(id),0) into maxPAS from tv2017_dev.pre_agenda;
    insert into tv2017_dev.pre_agenda values (maxPAS+1, folioproductor,  fase, inicio, fin, estado, observacion, ifnull(auditInsertPAS,now()), ifnull(auditInsertPAS,now()));
    if salida != null or ( length(salida)!=0 and  substr(salida, 12)!='00:00:00')      then
        insert into tv2017_dev.pre_agenda_salida values (null, maxPAS+1, salida, ifnull(auditInsertPAS,now()), ifnull(auditInsertPAS,now()));
    end if;
    if lugar!= null or (length(lugar)!=0 and lugar!='' and lugar!='na' and lugar!='no definido') then
        insert into tv2017_dev.pre_agenda_locacion values (null, maxPAS+1, lugar, ifnull(auditInsertPAS,now()), ifnull(auditInsertPAS,now()));
    end if;

    /*PRE_AGENDA_SERVICIO_SALA                */
    if ((fase=3) or (fase=4) )then
        bloqueSalas: begin
            DECLARE doneSala INT DEFAULT 0;
            DECLARE sala, maxPASSala  bigint(20);
            DECLARE auditInsertSala DATETIME;
            DECLARE usoCabina char(1);
            DECLARE curSalas cursor for
                select psssala_fk, pssusosala_fk, pssaudit_insert
                from tves2016.preserviciosala inner join tves2016.preagenda on pssnumfolio_pf = pagnumfolio_pf and pssfase_pf = pagfase_pf and psssecuencial_pf = pagsecuencial_pk
                where pagnumfolio_pf = nomfolio and pagfase_pf = fase and pagsecuencial_pk = secuencial;
            DECLARE CONTINUE HANDLER FOR NOT FOUND SET doneSala = 1;
            open curSalas;
            loopSalas: loop
                fetch curSalas into sala, usoCabina, auditInsertSala;
                if doneSala then
                    leave loopSalas;
                end if;
                select  ifnull(max(id),0) into maxPASSala from tv2017_dev.pre_agenda_sala;
                insert into tv2017_dev.pre_agenda_sala values (maxPASSala+1, maxPAS+1, sala, auditInsertSala, auditInsertSala);
                if usoCabina != null or (length(usoCabina)!=0 ) then
                    insert into tv2017_dev.pre_agenda_sala_uso_cabina values (null, maxPASSala+1, usoCabina, ifnull(auditInsertSala,now()), ifnull(auditInsertSala,now()));
                end if;
            end loop loopSalas;
            close curSalas;
        end bloqueSalas;

    end if;


    if (fase=2) then
        /*PRE_AGENDA_SERVICIO_ACCESORIO*/
        bloqueAccesorios: begin
            DECLARE doneAccesorio INT DEFAULT 0;
            DECLARE accesorio bigint(20);
            DECLARE auditInsertAcc DATETIME;

            DECLARE curAccesorios cursor for
                select psaaccesorio_pf, if (psaaudit_insert='0000-00-00 00:00:00', now(), psaaudit_insert)
                from tves2016.preservicioaccesorios inner join tves2016.preagenda on preservicioaccesorios.psanumfolio_pf = pagnumfolio_pf and psafase_pf = pagfase_pf and psasecuencial_pf = pagsecuencial_pk
                where pagnumfolio_pf = nomfolio and pagfase_pf = fase and pagsecuencial_pk = secuencial;
            DECLARE CONTINUE HANDLER FOR NOT FOUND SET doneAccesorio = 1;
            open curAccesorios;
            loopAccesorios: loop
                fetch curAccesorios into accesorio, auditInsertAcc;
                if doneAccesorio then
                    leave loopAccesorios;
                end if;
                /*set monitor = concat("folio: ", convert(folio, char), " fase: ", convert(fase, char), "  sec: ",convert(secuencial,char));*/
                insert into tv2017_dev.pre_agenda_accesorio values (null, maxPAS+1,  accesorio, auditInsertAcc, auditInsertAcc);
            end loop loopAccesorios;
            close curAccesorios;
        end bloqueAccesorios;

        /*PRE_AGENDA_SERVICIO_EQUIPO*/
        bloqueEquipo: begin
            DECLARE doneEquipo INT DEFAULT 0;
            DECLARE equipo bigint(20);
            DECLARE auditInsertEq DATETIME;

            DECLARE curEquipos cursor for
                    select pseequipo_pf, ifnull(auditInsertPAS,now())
                    from tves2016.preservicioequipo inner join tves2016.preagenda on preservicioequipo.psenumfolio_pf = pagnumfolio_pf and psefase_pf = pagfase_pf and psesecuencial_pf = pagsecuencial_pk
                    where pagnumfolio_pf = nomfolio and pagfase_pf = fase and pagsecuencial_pk = secuencial;
            DECLARE CONTINUE HANDLER FOR NOT FOUND SET doneEquipo = 1;
            open curEquipos;
            loopEquipos: loop
                fetch curEquipos into equipo, auditInsertEq;
                if doneEquipo then
                    leave loopEquipos;
                end if;
                insert into tv2017_dev.pre_agenda_equipo values (null, maxPAS+1,  equipo, auditInsertEq, auditInsertEq);
            end loop loopEquipos;
            close curEquipos;
        end bloqueEquipo;
    end if;
    if (fase = 5) then
        /*PRE_AGENDA_SERVICIO_FORMATO_ENTREGA*/
        bloqueFmto: begin
            DECLARE doneFmto INT DEFAULT 0;
            DECLARE formato, cantidad bigint(20);
            DECLARE auditInsertFmto DATETIME;

            DECLARE curFmtos cursor for
                    select  pefformato_pf, pefcopias, pefaudit_insert
                    from tves2016.preentregaformato inner join tv2017_dev.folio on pefnumfolio_pf = folio.folio
                    where pefnumfolio_pf = nomfolio;
            DECLARE CONTINUE HANDLER FOR NOT FOUND SET doneFmto = 1;
            open curFmtos;
            loopFmtos: loop
                fetch curFmtos into formato, cantidad, auditInsertFmto;
                if doneFmto then
                    leave loopFmtos;
                end if;
                insert into tv2017_dev.pre_agenda_formato_entrega values (null, maxPAS+1,  formato, cantidad, auditInsertFmto, auditInsertFmto);
            end loop loopFmtos;
            close curFmtos;
        end bloqueFmto;
    end if;

    /*PRE_AGENDA_SERVICIO_TIPO_PERSONAL*/
    bloqueTP: begin
        DECLARE doneTP INT DEFAULT 0;
        DECLARE tipo, cantidad bigint(20);
        DECLARE auditInsertTP DATETIME;

        DECLARE curTP cursor for
                    select prstipopersonal_pf, prscantpersonal, prsaudit_insert
                    from tves2016.preservicio
                    where prsnumfolio_pf = nomfolio and prsfase_pf = fase;


        DECLARE CONTINUE HANDLER FOR NOT FOUND SET doneTP = 1;
        open curTP;
        loopTP: loop
            fetch curTP into tipo, cantidad, auditInsertTP;
            if doneTP then
                leave loopTP;
            end if;
            insert into tv2017_dev.pre_agenda_tipo_personal values (null, maxPAS+1,  tipo, cantidad, auditInsertTP, auditInsertTP);
        end loop loopTP;
        close curTP;
    end bloqueTP;


    /*PRE_AGENDA_SERVICIO_VEHICULO*/
    bloqueV: begin
        DECLARE doneV INT DEFAULT 0;
        DECLARE vehiculo char(1);
        DECLARE auditInsertV DATETIME;

        DECLARE curV cursor for
                    select psvvehiculo, psvaudit_insert
                    from tves2016.preservehiculos
                    where psvnumfolio_pf = nomfolio and psvfase_pf = fase and psvsecuencial_pf = secuencial;


        DECLARE CONTINUE HANDLER FOR NOT FOUND SET doneV = 1;
        open curV;
        loopV: loop
            fetch curV into vehiculo, auditInsertV;
            if doneV then
                leave loopV;
            end if;
            insert into tv2017_dev.pre_agenda_vehiculo values (null, maxPAS+1,  auditInsertV, auditInsertV);
        end loop loopV;
        close curV;
    end bloqueV;

end loop loopPAS;
close curPAS;
update tv2017_dev.folio set estado_id = 7 where estado_id = 6;
update tv2017_dev.pre_agenda set estado_id = 7 where estado_id = 6;

/*migracion005*/
DECLARE doneAServicio INT DEFAULT 0;
DECLARE nomfolio, folio, folioproductor, fase, secuencial, estado, sala, maxAS, maxASSala bigint(20);
DECLARE inicio, fin, auditInsertAS DATETIME;
DECLARE salida, lugar, usocabina, observacion varchar(255);
declare curAS  cursor for
select agenumfolio_pf, folio.id, folio_productor_asignado.id, fase.id, agesecuencial_pf,
concat(agefechainicial," ", agehorasalida),
concat(if(agefechainicial='0000-00-00', agefechafinal, agefechainicial)," ",if(agehorainicial='00:00:00',date_format(now(),'%H:%m:%s'), agehorainicial)),
concat(if(agefechafinal='0000-00-00', agefechainicial, agefechafinal)," ",if(agehorafinal='00:00:00',date_format(now(),'%H:%m:%s'), agehorafinal)),
if(ageestado_fk!=99,ageestado_fk+1, ageestado_fk), agelugar, ageobservaciones, ageaudit_insert
from tv2017_dev.oficio 	inner join tv2017_dev.folio on oficio.id = folio.oficio_id
        inner join tv2017_dev.folio_productor_asignado on folio.id = folio_productor_asignado.folio_id
        inner join tves2016.agendaservicio on agenumfolio_pf = tv2017_dev.folio.folio
        inner join tv2017_dev.fase on agenfase_pf = fase.id;

DECLARE CONTINUE HANDLER FOR NOT FOUND SET doneAServicio = 1;

delete from tv2017_dev.agenda_servicio_vehiculo;
delete from tv2017_dev.agenda_servicio_personal;
delete from tv2017_dev.agenda_servicio_formato_entrega;
delete from tv2017_dev.agenda_servicio_accesorio;
delete from tv2017_dev.agenda_servicio_equipo;
delete from tv2017_dev.agenda_servicio_locacion;
delete from tv2017_dev.agenda_servicio_salida;
delete from tv2017_dev.agenda_servicio_sala_uso_cabina;
delete from tv2017_dev.agenda_servicio_sala;
delete from tv2017_dev.agenda_servicio;
open curAS;
loopAS: loop
    fetch curAS into nomfolio, folio, folioproductor, fase, secuencial, salida, inicio, fin, estado, lugar,  observacion, auditInsertAS;
    if doneAServicio then
        leave loopAS;
    end if;
    select  ifnull(max(id),0) into maxAS from tv2017_dev.agenda_servicio;
    insert into tv2017_dev.agenda_servicio values (maxAS+1, folioproductor,  fase, inicio, fin, estado, observacion, ifnull(auditInsertAS,now()), ifnull(auditInsertAS,now()), ifnull(auditInsertAS,now()));

    if ( (fase=1) or (fase=2)) then
        if salida != null or ( length(salida)!=0 and  substr(salida, 12)!='00:00:00')      then
            insert into tv2017_dev.agenda_servicio_salida values (null, maxAS+1, salida, ifnull(auditInsertAS,now()), ifnull(auditInsertAS,now()));
        end if;
        if lugar!= null or (length(lugar)>1 and lugar!='na' and lugar!='no definido') then
            insert into tv2017_dev.agenda_servicio_locacion values (null, maxAS+1, lugar, ifnull(auditInsertAS,now()), ifnull(auditInsertAS,now()));
        end if;

        /*AGENDA_SERVICIO_VEHICULO*/
        bloqueV: begin
            DECLARE doneV INT DEFAULT 0;
            DECLARE vehiculo bigint(20);
            DECLARE auditInsertV DATETIME;

            DECLARE curV cursor for
                select vehiculo.id,  if (vesaudit_insert='0000-00-00 00:00:00', now(), vesaudit_insert)
                from tves2016.vehiculoservicio inner join tves2016.agendaservicio on vesnumfolio_pf = agenumfolio_pf and vesfase_pf = agenfase_pf and vessecuencial_pf = agesecuencial_pf
                inner join tv2017_dev.vehiculo on vesvehiculo_fk = vehiculo.placa
                where agenumfolio_pf = nomfolio and agenfase_pf = fase and agesecuencial_pf = secuencial;

            DECLARE CONTINUE HANDLER FOR NOT FOUND SET doneV = 1;

            open curV;
            loopV: loop
                fetch curV into vehiculo, auditInsertV;
                if doneV then
                    leave loopV;
                end if;
                /*set monitor = concat("folio: ", convert(folio, char), " fase: ", convert(fase, char), "  sec: ",convert(secuencial,char));*/
                insert into tv2017_dev.agenda_servicio_vehiculo values (null, maxAS+1,  vehiculo, auditInsertV, auditInsertV, auditInsertV);
            end loop loopV;
            close curV;
        end bloqueV;



        if (fase=2) then
            /*AGENDA_SERVICIO_ACCESORIO*/
            bloqueAccesorios: begin
                DECLARE doneAccesorio INT DEFAULT 0;
                DECLARE accesorio bigint(20);
                DECLARE auditInsertAcc DATETIME;

                DECLARE curAccesorios cursor for
                    select acsaccesorio_pf, if (acsaudit_insert='0000-00-00 00:00:00', now(), acsaudit_insert)
                    from tves2016.accesoriosservicio inner join tves2016.agendaservicio on acsnumfolio_pf = agenumfolio_pf and acsfase_pf = agenfase_pf and acssecuencial_pf = agesecuencial_pf
                    where agenumfolio_pf = nomfolio and agenfase_pf = fase and agesecuencial_pf = secuencial;

                DECLARE CONTINUE HANDLER FOR NOT FOUND SET doneAccesorio = 1;

                open curAccesorios;
                loopAccesorios: loop
                    fetch curAccesorios into accesorio, auditInsertAcc;
                    if doneAccesorio then
                        leave loopAccesorios;
                    end if;
                    /*set monitor = concat("folio: ", convert(folio, char), " fase: ", convert(fase, char), "  sec: ",convert(secuencial,char));*/
                    insert into tv2017_dev.agenda_servicio_accesorio values (null, maxAS+1,  accesorio, auditInsertAcc, auditInsertAcc);
                end loop loopAccesorios;
                close curAccesorios;
            end bloqueAccesorios;
            /*AGENDA_SERVICIO_EQUIPO*/
            bloqueEquipo: begin
                DECLARE doneEquipo INT DEFAULT 0;
                DECLARE equipo bigint(20);
                DECLARE auditInsertEq DATETIME;

                DECLARE curEquipos cursor for
                        select eqsequipo_pf, ifnull(auditInsertAS,now())
                        from tves2016.equiposervicio inner join tves2016.agendaservicio on equiposervicio.eqsnumfolio_pf = agenumfolio_pf and eqsfase_pf = agenfase_pf and eqssecuencial_pf = agesecuencial_pf
                        where agenumfolio_pf = nomfolio and agenfase_pf = fase and agesecuencial_pf = secuencial;
                DECLARE CONTINUE HANDLER FOR NOT FOUND SET doneEquipo = 1;
                open curEquipos;
                loopEquipos: loop
                    fetch curEquipos into equipo, auditInsertEq;
                    if doneEquipo then
                        leave loopEquipos;
                    end if;
                    insert into tv2017_dev.agenda_servicio_equipo values (null, maxAS+1,  equipo, auditInsertEq, auditInsertEq);
                end loop loopEquipos;
                close curEquipos;
            end bloqueEquipo;
        end if;
    end if;
    if (fase = 5) then
        /*AGENDA_SERVICIO_FORMATO_ENTREGA*/
        bloqueFmto: begin
            DECLARE doneFmto INT DEFAULT 0;
            DECLARE formato, cantidad bigint(20);
            DECLARE auditInsertFmto DATETIME;

            DECLARE curFmtos cursor for
                    select  enfformato_pf, enfcopias, entaudit_insert
                    from tves2016.entregaformato inner join tv2017_dev.folio on enfnumfolio_pf = folio.folio
                    where enfnumfolio_pf = nomfolio;
            DECLARE CONTINUE HANDLER FOR NOT FOUND SET doneFmto = 1;
            open curFmtos;
            loopFmtos: loop
                fetch curFmtos into formato, cantidad, auditInsertFmto;
                if doneFmto then
                    leave loopFmtos;
                end if;
                if cantidad >0 then
                    insert into tv2017_dev.agenda_servicio_formato_entrega values (null, maxAS+1,  formato, cantidad, auditInsertFmto, auditInsertFmto);
                end if;
            end loop loopFmtos;
            close curFmtos;
        end bloqueFmto;
    end if;


    /*AGENDA_SERVICIO_PERSONAL*/
    bloqueP: begin
        DECLARE doneP INT DEFAULT 0;
        DECLARE personal bigint(20);
        DECLARE auditInsertP DATETIME;

        DECLARE curP cursor for
                    select personal.id, pesaudit_insert
                    from tves2016.personalservicio  inner join tv2017_dev.personal on pespersonal_pf = personal.num_empleado
                    where pesnumfolio_pf = nomfolio and pesfase_pf = fase and pessecuencial_pf = secuencial;


        DECLARE CONTINUE HANDLER FOR NOT FOUND SET doneP = 1;
        open curP;
        loopP: loop
            fetch curP into personal, auditInsertP;
            if doneP then
                leave loopP;
            end if;
            insert into tv2017_dev.agenda_servicio_personal values (null, maxAS+1,  personal, auditInsertP, auditInsertP);
        end loop loopP;
        close curP;
    end bloqueP;

    /*AGENDA_SERVICIO_SALA                */
    if ((fase=3) or (fase=4) )then
        bloqueSalas: begin
            DECLARE doneSala INT DEFAULT 0;
            DECLARE sala, maxASSala  bigint(20);
            DECLARE auditInsertSala DATETIME;
            DECLARE usoCabina char(1);
            DECLARE curSalas cursor for
                select osasala_fk, osausosala_fk, osaaudit_insert
                from tves2016.salaservicio inner join tves2016.agendaservicio on osanumfolio_pf = agenumfolio_pf and osafase_pf = agenfase_pf and osasecuencial_pf = agesecuencial_pf
                where agenumfolio_pf = nomfolio and agenfase_pf = fase and agesecuencial_pf = secuencial;
            DECLARE CONTINUE HANDLER FOR NOT FOUND SET doneSala = 1;
            open curSalas;
            loopSalas: loop
                fetch curSalas into sala, usoCabina, auditInsertSala;
                if doneSala then
                    leave loopSalas;
                end if;
                select  ifnull(max(id),0) into maxASSala from tv2017_dev.agenda_servicio_sala;
                insert into tv2017_dev.agenda_servicio_sala values (maxASSala+1, maxAS+1, sala, auditInsertSala, auditInsertSala);
                if usoCabina != null or (length(usoCabina)!=0 ) then
                    insert into tv2017_dev.agenda_servicio_sala_uso_cabina values (null, maxASSala+1, usoCabina, ifnull(auditInsertSala,now()), ifnull(auditInsertSala,now()));
                end if;
            end loop loopSalas;
            close curSalas;
        end bloqueSalas;
    end if;


end loop loopAS;
close curAS;

update tv2017_dev.agenda_servicio set estado_id = 7 where estado_id = 6;
/*
update tv2017_dev.agenda_servicio set estado_id = 99
where folioproductorasignado_id in (select distinct f.id
from tv2017_dev.folio f
inner join tv2017_dev.folio_productor_asignado fpa on f.id = fpa.folio_id
where f.estado_id = 99);
*/

INSERT INTO tv2017_dev.personal_horario (id, personal_id, diasemana, desde, hasta, audit_insert, audit_update) VALUES(329, 1, 2, '1970-01-01 08:00:00.000', '1970-01-01 15:00:00.000', '2019-02-06 09:04:12.000', '2019-02-06 09:04:12.000');
INSERT INTO tv2017_dev.personal_horario (id, personal_id, diasemana, desde, hasta, audit_insert, audit_update) VALUES(328, 5, 2, '1970-01-01 08:00:00.000', '1970-01-01 15:00:00.000', '2019-02-06 09:03:15.000', '2019-02-06 09:03:15.000');
INSERT INTO tv2017_dev.personal_horario (id, personal_id, diasemana, desde, hasta, audit_insert, audit_update) VALUES(322, 74, 2, '1970-01-01 15:00:00.000', '1970-01-01 21:00:00.000', '2018-12-17 10:34:31.000', '2018-12-17 10:34:31.000');
INSERT INTO tv2017_dev.personal_horario (id, personal_id, diasemana, desde, hasta, audit_insert, audit_update) VALUES(323, 74, 3, '1970-01-01 15:00:00.000', '1970-01-01 21:00:00.000', '2018-12-17 10:34:31.000', '2018-12-17 10:34:31.000');
INSERT INTO tv2017_dev.personal_horario (id, personal_id, diasemana, desde, hasta, audit_insert, audit_update) VALUES(324, 74, 4, '1970-01-01 15:00:00.000', '1970-01-01 21:00:00.000', '2018-12-17 10:34:31.000', '2018-12-17 10:34:31.000');
INSERT INTO tv2017_dev.personal_horario (id, personal_id, diasemana, desde, hasta, audit_insert, audit_update) VALUES(325, 74, 5, '1970-01-01 15:00:00.000', '1970-01-01 21:00:00.000', '2018-12-17 10:34:31.000', '2018-12-17 10:34:31.000');
INSERT INTO tv2017_dev.personal_horario (id, personal_id, diasemana, desde, hasta, audit_insert, audit_update) VALUES(326, 74, 6, '1970-01-01 15:00:00.000', '1970-01-01 21:00:00.000', '2018-12-17 10:34:31.000', '2018-12-17 10:34:31.000');
INSERT INTO tv2017_dev.personal_horario (id, personal_id, diasemana, desde, hasta, audit_insert, audit_update) VALUES(317, 33, 2, '1970-01-01 08:00:00.000', '1970-01-01 15:00:00.000', '2018-12-17 10:32:46.000', '2018-12-17 10:32:46.000');
INSERT INTO tv2017_dev.personal_horario (id, personal_id, diasemana, desde, hasta, audit_insert, audit_update) VALUES(318, 33, 3, '1970-01-01 08:00:00.000', '1970-01-01 15:00:00.000', '2018-12-17 10:32:46.000', '2018-12-17 10:32:46.000');
INSERT INTO tv2017_dev.personal_horario (id, personal_id, diasemana, desde, hasta, audit_insert, audit_update) VALUES(319, 33, 4, '1970-01-01 08:00:00.000', '1970-01-01 15:00:00.000', '2018-12-17 10:32:46.000', '2018-12-17 10:32:46.000');
INSERT INTO tv2017_dev.personal_horario (id, personal_id, diasemana, desde, hasta, audit_insert, audit_update) VALUES(320, 33, 5, '1970-01-01 08:00:00.000', '1970-01-01 15:00:00.000', '2018-12-17 10:32:46.000', '2018-12-17 10:32:46.000');
INSERT INTO tv2017_dev.personal_horario (id, personal_id, diasemana, desde, hasta, audit_insert, audit_update) VALUES(321, 33, 6, '1970-01-01 08:00:00.000', '1970-01-01 15:00:00.000', '2018-12-17 10:32:46.000', '2018-12-17 10:32:46.000');
INSERT INTO tv2017_dev.personal_horario (id, personal_id, diasemana, desde, hasta, audit_insert, audit_update) VALUES(312, 46, 2, '1970-01-01 16:00:00.000', '1970-01-01 21:00:00.000', '2018-12-17 10:31:05.000', '2018-12-17 10:31:05.000');
INSERT INTO tv2017_dev.personal_horario (id, personal_id, diasemana, desde, hasta, audit_insert, audit_update) VALUES(313, 46, 3, '1970-01-01 16:00:00.000', '1970-01-01 21:00:00.000', '2018-12-17 10:31:05.000', '2018-12-17 10:31:05.000');
INSERT INTO tv2017_dev.personal_horario (id, personal_id, diasemana, desde, hasta, audit_insert, audit_update) VALUES(314, 46, 4, '1970-01-01 16:00:00.000', '1970-01-01 21:00:00.000', '2018-12-17 10:31:05.000', '2018-12-17 10:31:05.000');
INSERT INTO tv2017_dev.personal_horario (id, personal_id, diasemana, desde, hasta, audit_insert, audit_update) VALUES(315, 46, 5, '1970-01-01 16:00:00.000', '1970-01-01 21:00:00.000', '2018-12-17 10:31:05.000', '2018-12-17 10:31:05.000');
INSERT INTO tv2017_dev.personal_horario (id, personal_id, diasemana, desde, hasta, audit_insert, audit_update) VALUES(316, 46, 6, '1970-01-01 16:00:00.000', '1970-01-01 21:00:00.000', '2018-12-17 10:31:05.000', '2018-12-17 10:31:05.000');
INSERT INTO tv2017_dev.personal_horario (id, personal_id, diasemana, desde, hasta, audit_insert, audit_update) VALUES(307, 51, 2, '1970-01-01 08:00:00.000', '1970-01-01 15:00:00.000', '2018-12-17 10:28:39.000', '2018-12-17 10:28:39.000');
INSERT INTO tv2017_dev.personal_horario (id, personal_id, diasemana, desde, hasta, audit_insert, audit_update) VALUES(308, 51, 3, '1970-01-01 08:00:00.000', '1970-01-01 15:00:00.000', '2018-12-17 10:28:39.000', '2018-12-17 10:28:39.000');
INSERT INTO tv2017_dev.personal_horario (id, personal_id, diasemana, desde, hasta, audit_insert, audit_update) VALUES(309, 51, 4, '1970-01-01 08:00:00.000', '1970-01-01 15:00:00.000', '2018-12-17 10:28:39.000', '2018-12-17 10:28:39.000');
INSERT INTO tv2017_dev.personal_horario (id, personal_id, diasemana, desde, hasta, audit_insert, audit_update) VALUES(310, 51, 5, '1970-01-01 08:00:00.000', '1970-01-01 15:00:00.000', '2018-12-17 10:28:39.000', '2018-12-17 10:28:39.000');
INSERT INTO tv2017_dev.personal_horario (id, personal_id, diasemana, desde, hasta, audit_insert, audit_update) VALUES(311, 51, 6, '1970-01-01 08:00:00.000', '1970-01-01 15:00:00.000', '2018-12-17 10:28:39.000', '2018-12-17 10:28:39.000');
INSERT INTO tv2017_dev.personal_horario (id, personal_id, diasemana, desde, hasta, audit_insert, audit_update) VALUES(302, 30, 2, '1970-01-01 15:00:00.000', '1970-01-01 21:00:00.000', '2018-12-14 13:55:11.000', '2018-12-14 13:55:11.000');
INSERT INTO tv2017_dev.personal_horario (id, personal_id, diasemana, desde, hasta, audit_insert, audit_update) VALUES(303, 30, 3, '1970-01-01 15:00:00.000', '1970-01-01 21:00:00.000', '2018-12-14 13:55:11.000', '2018-12-14 13:55:11.000');
INSERT INTO tv2017_dev.personal_horario (id, personal_id, diasemana, desde, hasta, audit_insert, audit_update) VALUES(304, 30, 4, '1970-01-01 15:00:00.000', '1970-01-01 21:00:00.000', '2018-12-14 13:55:11.000', '2018-12-14 13:55:11.000');
INSERT INTO tv2017_dev.personal_horario (id, personal_id, diasemana, desde, hasta, audit_insert, audit_update) VALUES(305, 30, 5, '1970-01-01 15:00:00.000', '1970-01-01 21:00:00.000', '2018-12-14 13:55:11.000', '2018-12-14 13:55:11.000');
INSERT INTO tv2017_dev.personal_horario (id, personal_id, diasemana, desde, hasta, audit_insert, audit_update) VALUES(306, 30, 6, '1970-01-01 15:00:00.000', '1970-01-01 21:00:00.000', '2018-12-14 13:55:11.000', '2018-12-14 13:55:11.000');
INSERT INTO tv2017_dev.personal_horario (id, personal_id, diasemana, desde, hasta, audit_insert, audit_update) VALUES(297, 42, 2, '1970-01-01 08:00:00.000', '1970-01-01 15:00:00.000', '2018-12-14 13:53:51.000', '2018-12-14 13:53:51.000');
INSERT INTO tv2017_dev.personal_horario (id, personal_id, diasemana, desde, hasta, audit_insert, audit_update) VALUES(298, 42, 3, '1970-01-01 08:00:00.000', '1970-01-01 15:00:00.000', '2018-12-14 13:53:51.000', '2018-12-14 13:53:51.000');
INSERT INTO tv2017_dev.personal_horario (id, personal_id, diasemana, desde, hasta, audit_insert, audit_update) VALUES(299, 42, 4, '1970-01-01 08:00:00.000', '1970-01-01 15:00:00.000', '2018-12-14 13:53:51.000', '2018-12-14 13:53:51.000');
INSERT INTO tv2017_dev.personal_horario (id, personal_id, diasemana, desde, hasta, audit_insert, audit_update) VALUES(300, 42, 5, '1970-01-01 08:00:00.000', '1970-01-01 15:00:00.000', '2018-12-14 13:53:51.000', '2018-12-14 13:53:51.000');
INSERT INTO tv2017_dev.personal_horario (id, personal_id, diasemana, desde, hasta, audit_insert, audit_update) VALUES(301, 42, 6, '1970-01-01 08:00:00.000', '1970-01-01 15:00:00.000', '2018-12-14 13:53:51.000', '2018-12-14 13:53:51.000');


INSERT INTO tv2017_dev.ambito (id, descripcion, audit_insert, audit_update) VALUES(1, 'Administración Depto', '2019-01-01 00:00:00.000', '2019-01-01 00:00:00.000');
INSERT INTO tv2017_dev.ambito (id, descripcion, audit_insert, audit_update) VALUES(2, 'Administración', '2019-01-01 00:00:00.000', '2019-01-01 00:00:00.000');
INSERT INTO tv2017_dev.ambito (id, descripcion, audit_insert, audit_update) VALUES(3, 'Agendado', '2019-01-01 00:00:00.000', '2019-01-01 00:00:00.000');
INSERT INTO tv2017_dev.ambito (id, descripcion, audit_insert, audit_update) VALUES(4, 'Emisión de Reportes', '2019-01-01 00:00:00.000', '2019-01-01 00:00:00.000');


INSERT INTO tv2017_dev.rol_derecho (id, rol_id, ambito_id, lectura, escritura, ejecucion, audit_insert, audit_update) VALUES(1, 1, 1, 1, 1, 1, '2019-01-01', '2019-01-01');
INSERT INTO tv2017_dev.rol_derecho (id, rol_id, ambito_id, lectura, escritura, ejecucion, audit_insert, audit_update) VALUES(4, 10, 3, 1, 0, 0, '2019-01-01', '2019-01-01');
INSERT INTO tv2017_dev.rol_derecho (id, rol_id, ambito_id, lectura, escritura, ejecucion, audit_insert, audit_update) VALUES(6, 11, 3, 1, 0, 0, '2019-01-01', '2019-01-01');
INSERT INTO tv2017_dev.rol_derecho (id, rol_id, ambito_id, lectura, escritura, ejecucion, audit_insert, audit_update) VALUES(7, 12, 3, 1, 0, 0, '2019-01-01 00:00:00.000', '2019-01-01 00:00:00.000');
INSERT INTO tv2017_dev.rol_derecho (id, rol_id, ambito_id, lectura, escritura, ejecucion, audit_insert, audit_update) VALUES(8, 13, 3, 1, 0, 0, '2019-01-01 00:00:00.000', '2019-01-01 00:00:00.000');
INSERT INTO tv2017_dev.rol_derecho (id, rol_id, ambito_id, lectura, escritura, ejecucion, audit_insert, audit_update) VALUES(9, 14, 3, 1, 0, 0, '2019-01-01 00:00:00.000', '2019-01-01 00:00:00.000');
INSERT INTO tv2017_dev.rol_derecho (id, rol_id, ambito_id, lectura, escritura, ejecucion, audit_insert, audit_update) VALUES(10, 15, 3, 1, 0, 0, '2019-01-01 00:00:00.000', '2019-01-01 00:00:00.000');
INSERT INTO tv2017_dev.rol_derecho (id, rol_id, ambito_id, lectura, escritura, ejecucion, audit_insert, audit_update) VALUES(11, 16, 3, 1, 0, 0, '2019-01-01 00:00:00.000', '2019-01-01 00:00:00.000');
INSERT INTO tv2017_dev.rol_derecho (id, rol_id, ambito_id, lectura, escritura, ejecucion, audit_insert, audit_update) VALUES(17, 17, 3, 1, 0, 0, '2019-01-01 00:00:00.000', '2019-01-01 00:00:00.000');
INSERT INTO tv2017_dev.rol_derecho (id, rol_id, ambito_id, lectura, escritura, ejecucion, audit_insert, audit_update) VALUES(3, 100, 3, 1, 1, 1, '2019-01-01 00:00:00.000', '2019-01-01 00:00:00.000');
INSERT INTO tv2017_dev.rol_derecho (id, rol_id, ambito_id, lectura, escritura, ejecucion, audit_insert, audit_update) VALUES(25, 126, 1, 1, 0, 0, '2019-01-01 00:00:00.000', '2019-01-01 00:00:00.000');
INSERT INTO tv2017_dev.rol_derecho (id, rol_id, ambito_id, lectura, escritura, ejecucion, audit_insert, audit_update) VALUES(2, 127, 2, 1, 1, 1, '2019-01-01 00:00:00.000', '2019-01-01 00:00:00.000');

INSERT INTO tv2017_dev.ctacorreo (id, hostname, puerto, cuenta, contrasenia, auditinsert, auditlastupdate) VALUES(1, 'correo.ipn.mx', '587', 'epuente@ipn.mx', '', '2016-04-05 13:21:22.000', '2018-10-03 12:15:11.000');

update tv2017_dev.accesorio set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.agenda_servicio set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.agenda_servicio_accesorio set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.agenda_servicio_cancelacion set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.agenda_servicio_equipo set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.agenda_servicio_formato_entrega set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.agenda_servicio_junta set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.agenda_servicio_locacion set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.agenda_servicio_personal set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.agenda_servicio_sala set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.agenda_servicio_sala_uso_cabina set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.agenda_servicio_salida set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.agenda_servicio_vehiculo set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.ambito set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.cola_correos set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.ctacorreo set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.cuenta set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.cuenta_rol set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.equipo set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.estado set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.estado_equipo_accesorio_vehiculo set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.fase set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.folio set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.folio_productor_asignado set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.formato set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.his_folio set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.motivo_cancelacion set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.oficio set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.oficio_contacto set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.oficio_contacto_correo set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.oficio_contacto_telefono set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.oficio_fecha_grabacion set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.oficio_productor_solicitado set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.oficio_respuesta set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.oficio_servicio_solicitado set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.operador_sala set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.personal set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.personal_avatar set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.personal_cambio_horario set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.personal_correo set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.personal_horario set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.pre_agenda set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.pre_agenda_accesorio set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.pre_agenda_cancelacion set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.pre_agenda_equipo set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.pre_agenda_formato_entrega set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.pre_agenda_junta set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.pre_agenda_locacion set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.pre_agenda_locutor set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.pre_agenda_sala set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.pre_agenda_sala_uso_cabina set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.pre_agenda_salida set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.pre_agenda_tipo_personal set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.pre_agenda_vehiculo set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.rol set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.rol_derecho set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.sala set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.sala_mantenimiento set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.servicio set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.tipo_contrato set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.tipo_equipo_accesorio set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.tipo_mantenimiento set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.tipo_personal set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.tipo_personal_fase set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.unidad_responsable set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.vehiculo set audit_insert='2018-01-01 00:00:00' where audit_insert is null;
update tv2017_dev.accesorio set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.agenda_servicio set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.agenda_servicio_accesorio set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.agenda_servicio_cancelacion set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.agenda_servicio_equipo set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.agenda_servicio_formato_entrega set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.agenda_servicio_junta set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.agenda_servicio_locacion set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.agenda_servicio_personal set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.agenda_servicio_sala set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.agenda_servicio_sala_uso_cabina set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.agenda_servicio_salida set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.agenda_servicio_vehiculo set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.ambito set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.cola_correos set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.ctacorreo set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.cuenta set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.cuenta_rol set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.equipo set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.estado set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.estado_equipo_accesorio_vehiculo set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.fase set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.folio set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.folio_productor_asignado set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.formato set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.his_folio set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.motivo_cancelacion set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.oficio set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.oficio_contacto set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.oficio_contacto_correo set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.oficio_contacto_telefono set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.oficio_fecha_grabacion set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.oficio_productor_solicitado set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.oficio_respuesta set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.oficio_servicio_solicitado set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.operador_sala set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.personal set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.personal_avatar set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.personal_cambio_horario set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.personal_correo set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.personal_horario set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.pre_agenda set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.pre_agenda_accesorio set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.pre_agenda_cancelacion set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.pre_agenda_equipo set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.pre_agenda_formato_entrega set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.pre_agenda_junta set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.pre_agenda_locacion set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.pre_agenda_locutor set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.pre_agenda_sala set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.pre_agenda_sala_uso_cabina set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.pre_agenda_salida set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.pre_agenda_tipo_personal set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.pre_agenda_vehiculo set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.rol set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.rol_derecho set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.sala set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.sala_mantenimiento set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.servicio set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.tipo_contrato set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.tipo_equipo_accesorio set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.tipo_mantenimiento set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.tipo_personal set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.tipo_personal_fase set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.unidad_responsable set audit_update='2018-01-01 00:00:00' where audit_update is null;
update tv2017_dev.vehiculo set audit_update='2018-01-01 00:00:00' where audit_update is null;










RETURN 1;
END
