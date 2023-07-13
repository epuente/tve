select concat("insert into unidad_responsable values(null, '", urscve_pk ,"', '",ursnombre,"', '", ursnomcorto,"', '", urstipo, "', '",ursnivel,"', '",ursareaconoc, "', null, null);")
from cat_unidadresp

select concat("insert into estado values(",estid_pk,", '",estdescripcion,"', null, null);") as query
from estados

select concat("insert into tipo_personal values(",tpeid_pk, ", '",tpedescripcion,"', null, null);")
from cat_tipopersonal



select concat("insert into servicio values(",serid_pk,", '",serdescripcion,"', null, null);") 
from cat_servicios;


SELECT concat("insert into equipo values (",eqpid_pk,", '",eqpdescripcion,"', ",eqpestado,", '",eqpnoserie,"' , '", eqpmarca,"', '", eqpmodelo,"', '", eqpobservaciones, "', '",now(),"', '", now(), "');")
 FROM tves2016.cat_equipo;



************************************************************************************
tv2017.personal,  tv2017.cuenta,   tv2017.cuenta_rol
************************************************************************************
CREATE DEFINER=`root`@`localhost` FUNCTION `migracion`() RETURNS int(11)
BEGIN
    DECLARE nempleado, numEmpleado, paterno, materno, nombre, turno, tipopersonal, activo, usu, pass CHAR(100);
    DECLARE maxPersonal int;
    DECLARE done INT DEFAULT 0;
    
    declare curPersonal  cursor for 
		select pernumempleado_pk, perpaterno, permaterno, pernombre, perturno, pertipopersonal_fk, peractivo from tves2016.cat_personal; 
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;
    
    
    delete from tv2017.cuenta_rol;
    delete from tv2017.cuenta;
    delete from tv2017.personal;
    
    
    open curPersonal;
	loopPersonal: loop    
		fetch curPersonal into nempleado, paterno, materno, nombre, turno, tipopersonal, activo;		
        if done then
			leave loopPersonal;
		end if;
        select  ifnull(max(id),0) into maxPersonal from tv2017.personal;
		insert into tv2017.personal values (maxPersonal+1,nempleado, paterno, materno, nombre, tipopersonal, turno, activo, null, null);
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
					select ifnull(max(id),0) into maxCuenta from tv2017.cuenta;
					insert into tv2017.cuenta values (maxCuenta+1, maxPersonal+1,  usu, pass, null, null);
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
							insert into tv2017.cuenta_rol values (null, maxCuenta+1,  if(rolid=0,1, rolid), null, null);
						end if;
					end loop;
					CLOSE cur3;        
				end bloqueCuentasRoles;				
				
				
				
			end loop;
			CLOSE cur1;        
		end bloqueCuentas;  
        
    end loop;
        

    close curPersonal;
RETURN 1;
END
************************************************************************************




***********************************************************************************
oficio,  oficio_contacto, oficio_contacto_telefono,  oficio_contacto_correo,    oficio_fecha_grabacion,  oficio_productor_solicitado, oficio_servicio_solicitado
folio
***********************************************************************************
CREATE DEFINER=`root`@`localhost` FUNCTION `migracionOficio002`() RETURNS int(11)
BEGIN
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
                (tves2016.cat_folios inner join tv2017.unidad_responsable on folurremitente_fk = clave) 
                left join tv2017.personal on folproductorsol_fk = num_empleado
			 );
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;
    
    delete from tv2017.folio_productor;
	delete from tv2017.folio;
    
    delete from tv2017.oficio_contacto_telefono;
    delete from tv2017.oficio_contacto_correo;
    delete from tv2017.oficio_contacto;
    delete from tv2017.oficio_fecha_grabacion;
    delete from tv2017.oficio_productor_solicitado;
    delete from tv2017.oficio_servicio_solicitado;

    delete from tv2017.oficio;  
    
    
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
        select  ifnull(max(id),0) into maxOficio from tv2017.oficio;
		insert into tv2017.oficio values (maxOficio+1, nOficio, remitente, descripcion, titulo, fecharemitente, fecharecepcion, observaciones, auditInsert, ifnull(auditInsert,now()));
        if length(connombre) != 0 then
			select  ifnull(max(id),0) into maxOficioContacto from tv2017.oficio_contacto;
			insert into tv2017.oficio_contacto values(maxOficioContacto+1, maxOficio+1, connombre, auditInsert, ifnull(auditInsert,now()));
			if length(contel) != 0 then
				insert into tv2017.oficio_contacto_telefono values(null, maxOficioContacto+1, contel, auditInsert, ifnull(auditInsert,now()));
            end if;
			if length(concorreo) != 0 then
				insert into tv2017.oficio_contacto_correo values(null, maxOficioContacto+1, concorreo, audit_insert, ifnull(auditInsert,now()));
            end if;            
		end if;
        if length(fechagrabacion) != 0 then
			insert into tv2017.oficio_fecha_grabacion values(null, maxOficio+1, fechagrabacion, auditInsert, ifnull(auditInsert,now()));
		end if;
        if productorsol is not null then
			insert into tv2017.oficio_productor_solicitado values(null, maxOficio+1, productorsol, auditInsert, ifnull(auditInsert,now()));
		end if;        
        if servicio is not null then
			insert into tv2017.oficio_servicio_solicitado values(null, maxOficio+1, servicio, auditInsert, ifnull(auditInsert,now()));
		end if;   
        
        #OFICIO
        select  ifnull(max(id),0) into maxFolio from tv2017.folio;
        insert into tv2017.folio values (maxFolio+1, folio, maxOficio+1, estado, fechaentrega, observaciones, auditInsert, ifnull(auditInsert,now()));
        
        #OFICIO_PRODUCTOR

		bloqueProductorAsignado: begin	
			DECLARE donePA INT DEFAULT 0;			
			DECLARE productorAsignado bigint(20);
			declare curPA  cursor for 
					select personal.id from tves2016.productorasignado inner join tv2017.personal on pprnumfolio_pf = folio and pprnumempleado_pf = personal.num_empleado;
			DECLARE CONTINUE HANDLER FOR NOT FOUND SET donePA = 1;        
			open curPA;
			loopPA: loop    
				fetch curPA into productorAsignado;	
				if donePA then
					leave loopPa;
				end if;
				if productorAsignado is not null then	
					insert into tv2017.folio_productor values (null, maxFolio+1,  productorAsignado, auditInsert, ifnull(auditInsert,now()) );
				end if;
			end loop loopPA;
			close curPA;	
        end bloqueProductorAsignado;
        
    end loop;
        

    close curOficio;
RETURN 1;
END
***********************************************************************************

**************************************************************************************
his_folio
**************************************************************************************
CREATE DEFINER=`root`@`localhost` FUNCTION `migracionHisFolios003`() RETURNS int(11)
BEGIN
    DECLARE auditInsert DATE;
    DECLARE folioid, estado bigint(20);
    DECLARE done INT DEFAULT 0;
    

    
    declare curHisto cursor for 
		select folio.id, if(hfoestado_pf!=99,hfoestado_pf+1, 99), audit_insert from tves2016.his_folios inner join tv2017.folio on hfonumfolio_pf = folio.folio;


	DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;
    
    delete from tv2017.his_folio;
    
    open curHisto;
	loopH: loop    
		fetch curHisto into folioid, estado, auditInsert;	
        if done then
			leave loopH;
		end if;
		insert into tv2017.his_folio values (null, folioid, estado, auditInsert, auditInsert);
        
    end loop;
    close curHisto;
RETURN 1;
END
**********************************************************************************************

ALTER DATABASE tves2016 CHARACTER SET latin1 COLLATE latin1_spanish_ci;
ALTER DATABASE tv2017 CHARACTER SET latin1 COLLATE latin1_spanish_ci;

ALTER TABLE tves2016.cat_personal CONVERT TO CHARACTER SET latin1 COLLATE latin1_spanish_ci;
ALTER TABLE tves2016.cuentas CONVERT TO CHARACTER SET latin1 COLLATE latin1_spanish_ci;
ALTER TABLE tves2016.cuentasroles CONVERT TO CHARACTER SET latin1 COLLATE latin1_spanish_ci;

ALTER TABLE tv2017.personal CONVERT TO CHARACTER SET latin1 COLLATE latin1_spanish_ci;
ALTER TABLE tv2017.cuenta CONVERT TO CHARACTER SET latin1 COLLATE latin1_spanish_ci;
ALTER TABLE tv2017.cuenta_rol CONVERT TO CHARACTER SET latin1 COLLATE latin1_spanish_ci;
select migracion() from dual;


***********************************************************************************************
CREATE DEFINER=`root`@`localhost` FUNCTION `migracionPreagenda004`() RETURNS int(11)
BEGIN	
			DECLARE donePAServicio INT DEFAULT 0;			
			DECLARE nomfolio, folio, folioproductor, fase, secuencial, estado, sala, maxPAS, maxPASSala bigint(20);
            DECLARE inicio, fin, auditInsertPAS DATETIME; 
            DECLARE salida, lugar, usocabina, observacion varchar(255);
            DECLARE monitor varchar(255);
			declare curPAS  cursor for 
select pagnumfolio_pf, folio.id, folio_productor.id, fase.id, pagsecuencial_pk,
	concat(pagfechainicial," ", paghorasalida), 
    concat(if(pagfechainicial='0000-00-00', pagfechafinal, pagfechainicial)," ",if(paghorainicial='00:00:00',date_format(now(),'%H:%m:%s'), paghorainicial)), 
    concat(if(pagfechafinal='0000-00-00', pagfechainicial, pagfechafinal)," ",if(paghorafinal='00:00:00',date_format(now(),'%H:%m:%s'), paghorafinal)), 
		if(pagestado_fk!=99,pagestado_fk+1, pagestado_fk), paglugar, pagtsala, pagusocabina, pagobservaciones, ageaudit_insert
from tv2017.oficio 	inner join tv2017.folio on oficio.id = folio.oficio_id 
					inner join tv2017.folio_productor on folio.id = folio_productor.folio_id
                    inner join tves2016.preagenda on pagnumfolio_pf = tv2017.folio.folio
                    inner join tv2017.fase on pagfase_pf = fase.id;

			DECLARE CONTINUE HANDLER FOR NOT FOUND SET donePAServicio = 1; 
            
            delete from tv2017.pre_agenda_formato_entrega;
			delete from tv2017.pre_agenda_accesorio;
            delete from tv2017.pre_agenda_equipo;
            delete from tv2017.pre_agenda_locacion;
            delete from tv2017.pre_agenda_salida;
            delete from tv2017.pre_agenda_sala_uso_cabina;
            delete from tv2017.pre_agenda_sala;
            delete from tv2017.pre_agenda;
			open curPAS;
			loopPAS: loop    
				fetch curPAS into nomfolio, folio, folioproductor, fase, secuencial, salida, inicio, fin, estado, lugar, sala, usocabina, observacion, auditInsertPAS;	
				if donePAServicio then
					leave loopPaS;
				end if;
				select  ifnull(max(id),0) into maxPAS from tv2017.pre_agenda;
				insert into tv2017.pre_agenda values (maxPAS+1, folioproductor,  fase, inicio, fin, estado, observacion, ifnull(auditInsertPAS,now()), ifnull(auditInsertPAS,now()));
                if salida != null or ( length(salida)!=0 and  substr(salida, 12)!='00:00:00')      then
					insert into tv2017.pre_agenda_salida values (null, maxPAS+1, salida, ifnull(auditInsertPAS,now()), ifnull(auditInsertPAS,now()));
				end if;
                if lugar!= null or (length(lugar)!=0 and lugar!='' and lugar!='na' and lugar!='no definido') then
					insert into tv2017.pre_agenda_locacion values (null, maxPAS+1, lugar, ifnull(auditInsertPAS,now()), ifnull(auditInsertPAS,now()));
                end if;
                if sala!= null or (length(sala)!=0 and sala!=0) then
					select  ifnull(max(id),0) into maxPASSala from tv2017.pre_agenda_sala;
					insert into tv2017.pre_agenda_sala values (maxPASSala+1, maxPAS+1, sala, ifnull(auditInsertPAS,now()), ifnull(auditInsertPAS,now()));
					if usocabina!= null or (length(usocabina)!=0) then
						insert into tv2017.pre_agenda_sala_uso_cabina values (null, maxPASSala+1, usocabina, ifnull(auditInsertPAS,now()), ifnull(auditInsertPAS,now()));
					end if;			                    
                end if;		
                
                if (fase=2) then
					#PRE_AGENDA_SERVICIO_ACCESORIO
					bloqueAccesorios: begin
						DECLARE doneAccesorio INT DEFAULT 0;
						DECLARE accesorio bigint(20);
						DECLARE auditInsertAcc DATETIME;

						DECLARE curAccesorios cursor for select psaaccesorio_pf, if (psaaudit_insert='0000-00-00 00:00:00', now(), psaaudit_insert)
								from tves2016.preservicioaccesorios inner join tves2016.preagenda on preservicioaccesorios.psanumfolio_pf = pagnumfolio_pf and psafase_pf = pagfase_pf and psasecuencial_pf = pagsecuencial_pk
								where pagnumfolio_pf = nomfolio and pagfase_pf = fase and pagsecuencial_pk = secuencial;  
						
						DECLARE CONTINUE HANDLER FOR NOT FOUND SET doneAccesorio = 1;  
				
						open curAccesorios;
						loopAccesorios: loop
							fetch curAccesorios into accesorio, auditInsertAcc;
							if doneAccesorio then
								leave loopAccesorios;
							end if;
							#set monitor = concat("folio: ", convert(folio, char), " fase: ", convert(fase, char), "  sec: ",convert(secuencial,char));
							insert into tv2017.pre_agenda_accesorio values (null, maxPAS+1,  accesorio, auditInsertAcc, auditInsertAcc);
						end loop loopAccesorios;
						close curAccesorios;
					end bloqueAccesorios;
                    
					#PRE_AGENDA_SERVICIO_EQUIPO
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
							insert into tv2017.pre_agenda_equipo values (null, maxPAS+1,  equipo, auditInsertEq, auditInsertEq);
						end loop loopEquipos;
						close curEquipos;
					end bloqueEquipo;                    
                end if;
                if (fase = 5) then
					#PRE_AGENDA_SERVICIO_FORMATO_ENTREGA
					bloqueFmto: begin
						DECLARE doneFmto INT DEFAULT 0;
						DECLARE formato, cantidad bigint(20);
						DECLARE auditInsertFmto DATETIME;

						DECLARE curFmtos cursor for 								
                                select  pefformato_pf, pefcopias, pefaudit_insert
								from tves2016.preentregaformato inner join tv2017.folio on pefnumfolio_pf = folio.folio
                                where pefnumfolio_pf = nomfolio;
						DECLARE CONTINUE HANDLER FOR NOT FOUND SET doneFmto = 1;  				
						open curFmtos;
						loopFmtos: loop
							fetch curFmtos into formato, cantidad, auditInsertFmto;
							if doneFmto then
								leave loopFmtos;
							end if;		
							insert into tv2017.pre_agenda_formato_entrega values (null, maxPAS+1,  formato, cantidad, auditInsertFmto, auditInsertFmto);
						end loop loopFmtos;
						close curFmtos;
					end bloqueFmto; 					
                end if;
                
                
                
			end loop loopPAS;
			close curPAS;
RETURN 1;
END
**************************************************************************************************





select concat("insert into cuenta_rol values(null, '",clrpersonal_pf,"', ",clrrol_fk,"');" )
from cuentasroles, cat_personal
where clrpersonal_pf = pernumempleado_pk;
