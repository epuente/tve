/**
 *  Abre la ventana modal que muestra los controles dependiendo del resourceid y pone la
 *  info correspondiente en los controles
 */
function mostrarDatosEvento(event) {
        $('.popover, [data-toggle="popover"]').popover('hide');
        console.log("LlamadasAjax mostrarDatosEvento");
        console.log("Recibido en agendaMostrarDatosEvento: ")
        console.dir(event);

        // vaciar los select
        $('select[name^="select"][name$="Disponibles"]').find('option').remove();
        $('select[name^="select"][name$="Disponibles"]').bootstrapDualListbox('refresh');

        var $l0 = LlamadaAjaxBuscaFolio(event.folioNum);
        var $eq = LlamadaAjaxEquipoAccesoriosDisponibles(event.start, event.end);
        var $inges = LlamadaAjax("/todosIngenieros","POST",JSON.stringify({}));
        var $vh = LlamadaAjaxVehiculosDisponibles(event.start, event.end);
        var $todosRoles = LlamadaAjax("/ajaxPersonalDisponible","POST",JSON.stringify({desde:event.start, hasta:event.end, fase:event.faseId}));

        //Oculta todos los paneles
        $('div[id^="panel"], #preguntaFormatosAsignados, div[name^="preguntaPersonal"], div[name="preguntaVehiculo"],  div[name="personalAsignado"]').hide();
        $("[class^='.iconoError'], [class^='iconoAdvertencia'], #popoverPerfiles").remove();
        $.when($l0, $eq, $vh, $todosRoles, $inges).done(function(data0, dataEq, dataVh, dataTodo, dataIngs) {
            // Se muestran los componentes comunes (no importa si es recien creado el evento)
            console.log("%c data0 ","background-color:green")
            console.dir(data0)
            console.log("%c dataTodo ","background-color:green")
            console.log("dataTodo.length:"+dataTodo.length)
            console.dir(dataTodo)
            console.log("%c dataIngs ","background-color:green")
            console.dir(dataIngs)
            console.log("%c dataEq ","background-color:green")
            console.dir(dataEq)

            // Llenar la lista de locutores disponibles
            dataTodo.forEach(function(p){
            // El administrador (rol=1) tendrá la lista de todo el personal disponible
                if (rolActual==1){
                        $('select[name="selectPersonalDisponibles"]').append("<option value='"+p.cuentaRolId+"'>"+p.nombreCompleto+" - "+p.rol+" </option>");
                } else{
                    // Si no es administrador, solo llenará la lista de locutores disponibles
                    // Locutores
                    if (p.rolId==15){
                        var aux="<option value='"+p.personalId+"'";
                        aux+=">"+p.nombreCompleto+" - "+p.rol+"</option>";
                        $('select[name="selectLocutoresDisponibles"]').append(aux);
                    }
                }
            });

            if (event.datos!=undefined && event.datos.personalAsignado!=undefined){
                dataTodo.forEach(function(p){
                    var cadenaSelected="";
                    //Se encuentra entre el personal asignado?
                    if (event.datos.personalAsignado.length>0)
                        event.datos.personalAsignado.forEach(function(pasig){
                                if (p.cuentaRolId == pasig.ctaRolId)
                                    cadenaSelected=" selected ";
                        });

                    if (p.rolId!=15 && p.rolId!=12)
                        $('select[name="selectPersonalDisponibles"]').append("<option value='"+p.cuentaRolId+"'"+cadenaSelected+">"+p.nombreCompleto+" - "+p.rol+" </option>");

                    // Locutores
                    if (p.rolId==15)
                        $('select[name="selectLocutoresDisponibles"]').append("<option value='"+p.personalId+"'"+cadenaSelected+">"+p.nombreCompleto+" - "+p.rol+" </option>");
                    // Resp. Diseño
                    if (p.rolId==12)
                        $('select[name="RDisenioDisponibles"]').append("<option value='"+p.personalId+"'"+cadenaSelected+">"+p.nombreCompleto+" - "+p.rol+" </option>");
                });
            }

             dataIngs.forEach(function(p){
                    var aux="<option value='"+p.id+"'";
                    aux+=">"+p.nombre+"</option>";
                    $('select[name="selectIngenierosDisponibles"]').append(aux);
             });


            //Refrescar los bootstrapDualListbox
            $('select[name^="select"][name$="Disponibles"]').bootstrapDualListbox('refresh');

            $("#fc_myModal2").click();
            $('[data-toggle="popover"], .popover').popover("hide");
            $('#myModal2 form').each(function(i, e) {
                $(e)[0].reset()
            });

            $('#divConfirmarFolio').html("<label>Folio:</label> " + event.folioNum);
            $('#divConfirmarDescripcion').html("<label>Descripción:</label> " + event.descripcion);
            $('#divConfirmarEstado').html("<h3><span style='font-weight:bold'>" +
                (event.tipo == "preagenda" ? "Sin Autorizar" :
                    "Autorizado") + "</span></h3>");

            $('#divConfirmarFecha').html(moment(event.start).format("DD/MM/YYYY"));
            $('#divConfirmarHoraInicial').html(
                    "<label>Desde: </label><div id='evtDesde' class='mismaLinea'>" +
                    event.start.format("HH:mm") + "</div></h5>");
            $('#divConfirmarHoraFinal').html(
                    "<label>Hasta:</label> <div id='evtHasta' class='mismaLinea'>" +
                    event.end.format("HH:mm") + "</div>");
            $('#divConfirmarFechaEntrega').html(
                "<label>Fecha entrega(a):</label> " + moment(data0.fechaEntrega, "ddd MMM DD HH:mm:ss Z YYYY").format("DD/MM/YYYY"));

            $("#divFolioProductorAsignadoId").html(event.fpaId);

            if (event.id == undefined) {
                event.id = 0;
                $('#calendario').fullCalendar('updateEvent', event);
                $("#divIdTemporal").html(0);
                $("#divIdEstado").html(event.estadoId);
            } else {
                $("#divIdTemporal").html(event.id);
                $("#divIdEstado").html(event.estadoId);
            }

            console.log("  -->event.id "+event.id)
            console.log("  -->event.tipo "+event.tipo)

            var $datosEvento = LlamadaAjax("/ajaxDatos", "POST", JSON.stringify( {"id":event.id, "tipo": event.tipo})  );


            //$.when(LlamadaAjaxDatos(event.id, event.tipo))
            $.when($datosEvento)
                .done(
                    function(data) {
                        console.log("ajaxDatos regresa..........")
                        console.dir(data)
                        console.dir(event)
                        if (data) {
                            $("#divProductorAsignado").html("<label>Prodcutor asignado:</label> " + data.folioproductorasignado.personal.nombre + " " + data.folioproductorasignado.personal.paterno + " " + data.folioproductorasignado.personal.materno);
                        }
                        // MUESTRA PANELES DEPENDIENDO DEL RESOURCE

                        $("[id^='panelesFase']").hide();
                        $("div[name^='pregunta']").hide();
                        $("div[name='preguntaPersonal']").hide();

                        if (event.resourceId == "a" || event.resourceId == "b") {
                            $("#panelesFase" + event.faseId + " div[name='preguntaSalida']").show();
                            $("#panelesFase" + event.faseId).show();
                            if (event.resourceId == "a") {
                                $('#myModalLabel2').html("<h3>Agenda para Preproducción</h3>");
                                $("#panelesFase1 div[name='preguntaTipoPreproduccion']").show();
                                $("#panelesFase1 div[name='preguntaPersonal']").hide();
                                $('#panelesFase1 div[name="preguntaPersonal"]').toggle((rolActual=="100" && event.estadoId >= 7) || (rolActual=="1"));
                                if (event.id == 0) {
                                    $("[name='rgTipoPreprod'][value=1]").click();
                                }
                                if (data) {
                                    if (data.locaciones.length > 0) {
                                        $('#panelesFase1 div[name="preguntaPersonalSolicitado"]').show();
                                    }
                                }
                            }
                            if (event.resourceId == "b") {
                                $('#myModalLabel2').html("<h3>Agenda para Grabación</h3>");
                              //  $("#panelesFase2 div[name='preguntaEquipoAccesoriosSolicitados']").show();
                                $('#panelesFase2 div[name="preguntaLocutoresSolicitados"]').show();
                                $('#panelesFase2 div[name="preguntaPersonalSolicitado"]').show();
                                $("#panelesFase2 div[name='preguntaIngenierosSolicitados']").show();
                                $('#panelesFase2 div[name="preguntaPersonal"]').toggle(((rolActual=="100" && (event.estadoId >= 7))) || (rolActual=="1"));
                                // Si es ingeniero Admon. Equipo y accesorios (rol 10) y el estado es < 7   o    si es productor (rol 100) y estado =0 o estado >=7

                                $('#panelesFase2 div[name="preguntaEquipoAccesoriosSolicitados"]').toggle(       (rolActual=="10" && event.estadoId == "7") ||  (rolActual=="100" && (event.estadoId==5 ||  event.estadoId>=7 || event.estadoId==4))      );
                                //Productor, estado autorizado
                                $('#panelesFase2').show();
                            }
                        }


                        if (event.resourceId.substring(0,1) == "c") {
                            $('#myModalLabel2').html("<h4>Agenda para la Sala de Ingesta y Digitalización</h4>");
                            $('#panelesSalaCR').show();
                            $('#panelInfoOperadoresSala').show();
                        }

                        if (event.resourceId.substring(0,1) == "d") {
                            $('#myModalLabel2').html("<h4>Agenda para la Sala de Edición</h4>");
                            $('#panelesSalaEdicion3').show();
                            $('#panelInfoOperadoresSala').show();
                        }

                        if (event.resourceId.substring(0,1) == "e") {
                            $('#myModalLabel2').html("<h4>Agenda para la Sala de Posproducción 1</h4>");
                            $('#panelesSalaEdicion2').show();
                            $('#panelInfoOperadoresSala').show();
                        }

                        if (event.resourceId.substring(0,1) == "f") {
                            $('#myModalLabel2').html("<h4>Agenda para la Sala de Posproducción 2</h4>");
                            $('#panelesSalaEdicion7').show();
                            $('#panelInfoOperadoresSala').show();
                        }

                        if (event.resourceId.substring(0,1) == "g") {
                            $('#myModalLabel2').html("<h3>Agenda para la Cabina de Audio</h3>");
                            $('#panelesUsoCabinaAudio div[name="preguntaUsoCabina2"], #panelesUsoCabinaAudio div[name="preguntaLocutoresSolicitados"]').hide();
                            $("#panelesUsoCabinaAudio, [name='preguntaUsoCabina']").show();
                            $('#panelInfoOperadoresSala').show();
                        }

                        if (event.resourceId == "h") {
                            $('#myModalLabel2').html("<h3>Agenda para Entrega</h3>");
                            $('#panelesFase5 div[name="preguntaFormatoSolicitado"]').show();
                            $('#panelesFase5').show();
                        }






                        // PONER LOS VALORES EN LOS CONTROLES DEPENDIENDO DEL RESOURCE
                        console.log("==========")
                        console.dir(data)
                        console.log("==========")
                        if (data) {
                            $("#divConfirmarFechaEntrega").html("<label>Fecha entrega:</label> " + moment(data.folioproductorasignado.folio.fechaentrega).format("DD/MM/YYYY"));
                            if (event.resourceId == "a" || event.resourceId == "b") {
                                if (data.salidas) {
                                    if (data.salidas.length > 0)
                                        $("#panelesFase" + data.fase.id + " input[type='text'][name='hSalida']").val(moment(data.salidas[0].salida).format('HH:mm'));
                                } else {
                                    $("#panelesFase" + data.fase.id + " input[type='text'][name='hSalida']").val("");
                                }
                                $("#panelesFase" + data.fase.id + " input[type='checkbox'][name='vehiculo']").prop("checked", (data.vehiculos.length != 0)).prop("disabled", rolActual=="1");

                                if (data.locaciones.length > 0) {
                                    console.log(data)
                                    $('#myModalLabel2').html("<h3>Agenda para Preproducción (Scouting)</h3>");
                                    $("#panelesFase" + data.fase.id + " input[name='rgTipoPreprod'][value='2']").prop("checked", true).change();

                                    if (data.locaciones[0].locacion)
                                        $("#panelesFase" + data.fase.id + " input[name='locacion']").val(data.locaciones[0].locacion);
                                }
                                if (event.tipo == "preagenda") {
                                    $("#divIdEstado").html(event.estadoId);
                                    $.each(data.personal, function(ip, p) {
                                        if (p.cantidad > 0) {
                                            $("input[name='inp_preprod_" + p.rol.id + "']").val(p.cantidad);
                                            $("input[name='inp_preprod_" + p.rol.id + "']").prop("disabled", rolActual=="1");
                                        }
                                    });
                                }
                                //Administrador y preagenda
                                if (rolActual=="1" && (data.estado.id == 4 || data.estado.id == 5)) {
                                    $("#panelesFase" + data.fase.id + " div[name='preguntaVehiculo']").show();
                                }

                                //Agenda
                                if (data.estado.id == 7) {
                                    $("#panelesFase" + data.fase.id + " div[name='preguntaLocutoresSolicitados']").hide();
                                    $("#panelesFase" + data.fase.id + " div[name='preguntaPersonal']").toggle(data.locaciones.length > 0);
                                    $("#panelesFase" + data.fase.id + " div[name='preguntaVehiculo']").toggle( rolActual!="10" && data.vehiculos.length > 0);
                                    $.each(data.personal, function(key, value) {
                                        $("select[name='selectPersonalDisponibles']").append("<option value='" + value.personal.id + "' selected>" + value.personal.tipo.descripcion + " - " + value.personal.nombre + " " + value.personal.paterno + " " + value.personal.materno + "</option>")
                                            .bootstrapDualListbox('refresh');
                                    });

                                    $.each(data.vehiculos, function(key, value) {
                                        $("select[name='selectVehiculosDisponibles']").append("<option value='" + value.vehiculo.id + "' selected>" + value.vehiculo.placa + " " + value.vehiculo.descripcion + "</option>")
                                            .bootstrapDualListbox('refresh');
                                    });
                                }


                                if (event.resourceId.startsWith("a")) {
                                    if (data.juntas.length > 0) {
                                        $('#myModalLabel2').html("<h3>Agenda para Preproducción (Junta de trabajo..)</h3>");
                                        $("#panelesFase1 [name='preguntaTipoPreproduccion']").show();
                                        $("#panelesFase1 input[name='rgTipoPreprod'][value='1']").prop("checked", true).change();
                                    }
                                }

                                if (event.resourceId.startsWith("b")) {
                                    $('#myModalLabel2').html("<h3>Agenda para Grabación</h3>");
                                    $('#panelesFase2 div[name="preguntaPersonal"]').toggle((rolActual=="100" && data.estado.id == 7) || rolActual=="1");

                                    //Productor (rol 100) estado con solicitud
                                    //Se muestra el select de los locutores
                                    if (rolActual=="100" && (data.estado.id == 4 || data.estado.id == 5)) {
                                        $('select[name="selectLocutoresDisponibles"] option').remove();
                                        $.each(data.locutores, function(il, l) {
                                            dataTodo.forEach(function(p){
                                                if (p.rolId!=15 && p.rolId!=12)
                                                    $('select[name="selectPersonalDisponibles"]').append("<option value='"+p.cuentaRolId+"'>"+p.nombreCompleto+" - "+p.rol+" </option>");
                                                // Locutores
                                                if (p.rolId==15){
                                                    var aux="<option value='"+p.personalId+"'";
                                                    console.log(">>> "+l.personal.id+"   "+p.personalId);
                                                    if (Number(l.personal.id) == Number(p.personalId)){
                                                        aux+=" selected ";
                                                        console.log("match!!!!");
                                                    }
                                                    aux+=">"+p.nombreCompleto+" - "+p.rol+"</option>";
                                                    $('select[name="selectLocutoresDisponibles"]').append(aux);
                                                }
                                            });
                                        });
                                            //                                    $('select[name="selectLocutoresDisponibles"]').bootstrapDualListbox('refresh', true);
                                    }

                                    //Administrador (rol 1) estado con solicitud
                                    //Se muestra el select del personal disponible (incluyendo a locutores)
                                    //Si el productor solicitó un locutor, este deberá aparecerle al administrador como seleccionado en el select 'personal seleccionado'
                                    if (rolActual=="1" && (data.estado.id == 4 || data.estado.id == 5)) {
                                        $.each(data.locutores, function(il, l) {
                                            $.each(l.personal.cuentas, function(ic, c) {
                                                    $.each(c.roles, function(ir, r) {
                                                        $('select[name="selectPersonalDisponibles"] option[value="'+r.id+'"]').prop("selected", true);
                                                    });
                                            });
                                        });
                                    }

                                    // Ingenieros para admon equipo y accesorios
                                    $.each(data.ingsAdmonEqpo, function(ii, i) {
                                            var aux="<option value='"+i.ingeniero.id+"'";
                                            aux+=" selected ";
                                            aux+=">"+i.ingeniero.nombre+" "+i.ingeniero.paterno+" "+i.ingeniero.materno+"</option>";
                                            $('select[name="selectIngenierosDisponibles"] option[value="'+i.ingeniero.id+'"]').prop("selected", true);
                                    });

                                    //Rol Administrador, estado autorizado, mostrar al ingeniero admon equipo y accesorios
                                    if (rolActual=="1" && data.estado.id==7){
                                        $.each(data.folioproductorasignado.agenda, function(iags, ags) {
                                            $.each(ags.ingsAdmonEqpo, function(iiadmon, iaadmon) {
                                                var aux="<option value='"+iaadmon.ingeniero.id+"'";
                                                aux+=" selected ";
                                                aux+=">"+iaadmon.ingeniero.nombre+" "+iaadmon.ingeniero.paterno+" "+iaadmon.ingeniero.materno+"</option>";
                                                $('select[name="selectIngenierosDisponibles"] option[value="'+iaadmon.ingeniero.id+'"]').prop("selected", true);
                                            });
                                        });
                                    }

                                    // Rol Administrador. De la lista de locutores disponibles eligir al solicitado(s)
                                    if ((rolActual=="1" && (data.estado.id == 4 || data.estado.id == 5))) {
                                        $.each(data.locutores, function(il, l) {
                                            $.when(LlamadaAjax("/infoCuentaRolPorPersona","POST",JSON.stringify({"id":l.personal.id}))).done(function(dataCR){
                                                if (dataCR.estado=="encontrado"){
                                                    dataCR.datos.forEach(function(d){
                                                        if (d.rolId==15){
                                                            $('select[name="selectPersonalDisponibles"] option[value="' + d.id + '"]').prop("selected", true);
                                                            console.log("es el locutor!!!")
                                                            //$('select[name="selectPersonalDisponibles"]').bootstrapDualListbox('refresh');
                                                        }
                                                    });
                                                }
                                            });

                                        });
                                    }
                                    $('select[name="selectLocutoresDisponibles"], select[name="selectPersonalDisponibles"]').bootstrapDualListbox('refresh');
                                    $.each(data.equipos,
                                        function(ieq, eq) {
                                            $('#panelesFase2 select[name="selectEquipoAccesoriosDisponibles"]')
                                                .append(
                                                    "<option value='E" + eq.equipo.id + "' selected>" +
                                                    eq.equipo.descripcion +
                                                    " " +
                                                    eq.equipo.marca +
                                                    " " +
                                                    eq.equipo.modelo +
                                                    " NS:" +
                                                    eq.equipo.serie +
                                                    "</option>");
                                        });
                                    $.each(data.accesorios, function(iacc, acc) {
                                            $('#panelesFase2 select[name="selectEquipoAccesoriosDisponibles"]')
                                                .append(
                                                    "<option value='A" + acc.accesorio.id + "' selected>" +
                                                    acc.accesorio.descripcion + " " + acc.accesorio.modelo + " NS:" + acc.accesorio.serie +
                                                    "</option>");
                                        });

                                    console.log("AAAAAQUI")
                                    console.dir(data.folioproductorasignado)

                                    $.each(data.folioproductorasignado.agenda, function(ia, ag){
                                            console.dir(ag)
                                            $.each(ag.previoautorizaequipo, function(iprev, prev){
                                                $('#panelesFase2 select[name="selectEquipoAccesoriosDisponibles"]')
                                                    .append(
                                                        "<option value='E" + prev.preagendaequipo.equipo.id + "' selected>" +
                                                        prev.preagendaequipo.equipo.descripcion + " " + prev.preagendaequipo.equipo.modelo + " NS:" + prev.preagendaequipo.equipo.serie +
                                                        "</option>");
                                            });
                                            $.each(ag.previoautorizaaccesorios, function(iprev, prev){
                                                $('#panelesFase2 select[name="selectEquipoAccesoriosDisponibles"]')
                                                    .append(
                                                        "<option value='A" + prev.preagendaaccesorio.accesorio.id + "' selected>" +
                                                        prev.preagendaaccesorio.accesorio.descripcion + " " + prev.preagendaaccesorio.accesorio.modelo + " NS:" + prev.preagendaaccesorio.accesorio.serie +
                                                        "</option>");
                                            });
                                        });


                                    $('select[name="selectEquipoAccesoriosDisponibles"]').bootstrapDualListbox('refresh');
                                    $('#panelesFase2 div[name="preguntaPersonalSolicitado"]').toggle((rolActual=="100" && (data.estado.id == 4 || data.estado.id == 5)) || (rolActual=="1" && data.estado.id != 7));
                                    $('#panelesFase2 div[name="preguntaLocutoresSolicitados"]').toggle((rolActual=="100" && (data.estado.id == 4 || data.estado.id == 5)));
                                }
                                $('#panelesFase' + data.fase.id + " :input").prop("disabled", rolActual=="100" && data.estado.id == 7);
                            } // Fin de resource 'a' o 'b'
                            console.dir(data)
                            console.log("RESOURCEID "+event.resourceId)
                            // Sala de Ingesta y digitalización
                            if (event.resourceId.startsWith("c")) {
                                console.log("RESOURCE C")
                                if (data.salas.length > 0)
                                    if (data.salas[0].usoscabina.length > 0)
                                        $("#panelesSalaCR input[name='rgFaseSala'][value='" + data.salas[0].usoscabina[0].usocabina + "']").prop("checked", true);
                                $("#panelesSalaCR :input").prop("disabled", rolActual=="100" && data.estado.id == 7);
                                //Información que proporciona solo el operador de sala
                                $("#panelesSiNoOtro").toggle(  rolActual=="16" && data.estado.id == 7  );
                                $("#panelesSiNoOtro :input, #panelesSiNoOtro [name='cambioFormato']").prop("enabled", rolActual=="16" && data.estado.id == 7 );


                                $("#panelInfoOperadoresSala div").html("<strong>Operador de sala</strong><div class='row'>"+nombresOperadores(data.operadoresSala)+"</div>");
                            }
                            console.dir(data)
                            // Salas:   d   , e posproduccion 1,   f
                            if (event.resourceId.startsWith("d") || event.resourceId.startsWith("e") || event.resourceId.startsWith("f")) {
                                $("[name='rgFaseSala']").prop("disabled", rolActual=="100" && data.estado.id == 7);
                                $('#myModalLabel2').html("<h4><small>Agenda para " + data.salas[0].sala.descripcion + " - " + data.fase.descripcion + "</h4>");
                                $("#panelesSalaEdicion" + event.salaId + " input[name='rgFaseSala'][value=" + data.fase.id + "]").prop("checked", true).change();
                                if (data.personal!=undefined)
                                    if (data.personal.length > 0) {
                                        if (data.personal[0].cantidad) {
                                            //$("#panelesSalaEdicion" + event.salaId + " input[name='inp_preprod_12']").val(data.personal[0].cantidad);
                                            $('#panelesSalaEdicion' + event.salaId + ' div[name="preguntaPersonal"]').toggle(rolActual=="1");
                                        }
                                        if (data.personal[0].personal) {
                                            $.each(data.personal, function(i, v) {
                                                $("select[name='selectRDisenioDisponibles'] option[value='" + v.personal.id + "']").prop("selected", true);
                                            });
                                            $('select[name="selectRDisenioDisponibles"]').bootstrapDualListbox('refresh');
                                        }
                                    }
                                $("#panelesSalaEdicion" + event.salaId + " :input").prop("disabled", rolActual=="100" && data.estado.id == 7);
                                $("#panelInfoOperadoresSala div").html("<strong>Operador de sala</strong><div class='row'>"+nombresOperadores(data.operadoresSala)+"</div>");
                            }

                            if (event.resourceId.startsWith("g")) {
                                console.log("cabina de audio resource g")
                                console.dir(data)
                                if (data.salas.length > 0) {
                                    if (data.salas[0].usoscabina.length > 0) {
                                        $("#panelesUsoCabinaAudio input[name='rgUsoCabina'][value='" + data.salas[0].usoscabina[0].usocabina + "']").change();
                                        $("#panelesUsoCabinaAudio input[name='rgUsoCabina'][value='" + data.salas[0].usoscabina[0].usocabina + "']").prop("selected", true);

                                        if ($("input[name='rgUsoCabina']:checked").val()=="V"){
                                            $("#panelesUsoCabinaAudio div[name='preguntaLocutoresSolicitados']").toggle(true);
                                            $("#preguntaLocutoresSolicitados").css("display","block");
                                        }
                                    }
                                }
                                //Rol administrador y estado autorizado
                                if (rolActual=="1" && data.estado.id == 7) {
                                    console.dir(dataTodo)
                                    $.each(dataTodo, function(it, t) {
                                        if (t.rolId==15)
                                            $('#panelesUsoCabinaAudio select[name="selectLocutoresDisponibles"]').append("<option value='"+t.personalId+"'>"+t.nombreCompleto+"</option>");
                                    });
                                    $.each(data.personal, function(il, l) {
                                        //$('#panelesUsoCabinaAudio select[name="selectLocutoresDisponibles"][value="'+l.personal.id+'"]');
                                    });
                                }

                                // Administrador, estado no autorizado
                                if (rolActual=="1" && data.estado.id != 7) {
                                    console.log(data.personal)
                                    var arrLocutoresSolicitados = [];
                                    $.each(data.locutores, function(il, l) {
                                        arrLocutoresSolicitados.push(l.personal.id);
                                    });
                                    console.log(arrLocutoresSolicitados)
                                    //Agrega los locutores disponibles al select
                                    $.each(dataTodo, function(it, t) {
                                        if (t.rolId==15){
                                            var cadena="";

                                            if (  arrLocutoresSolicitados.includes(t.personalId))
                                                cadena="selected";
                                            $('#panelesUsoCabinaAudio select[name="selectLocutoresDisponibles"]').append("<option value='"+t.personalId+"' "+cadena+">"+t.nombreCompleto+"</option>");
                                        }
                                    });
                                    console.dir(data.locutores)
                                    $.each(data.locutores, function(il, l) {
                                        console.log("il:"+il);
                                        $.each(data.personal, function(il, l) {
                                            console.log(    '#panelesUsoCabinaAudio select[name="selectLocutoresDisponibles"] option[value="'+l.personal.id+'"]'  )
                                            $('#panelesUsoCabinaAudio select[name="selectLocutoresDisponibles"] option[value="'+l.personal.id+'"]').prop("selected", true);
                                        });
                                        $('select[name="selectLocutoresDisponibles"], select[name="selectPersonalDisponibles"]').bootstrapDualListbox('refresh');
                                        $('select[name="selectPersonalDisponibles"] option[value=' + l.personal.id + ']').prop('selected', true);
                                    });

                                }

                                //Productor y estado 'no autorizado'
                                if (rolActual=="100" && data.estado.id != 7) {
                                    console.log("asdasdfasdfasdgfsfg sfgasdfg sdfg")
                                    console.dir(data)
                                    console.dir(data.locutores)
                                    $.each(data.locutores, function(il, l) {
                                        console.log("il:"+il);
                                        console.dir(l)
                                        console.log(  "----->"+l.personal.id  )
                                        $('#panelesUsoCabinaAudio select[name="selectLocutoresDisponibles"] option[value="'+l.personal.id+'"]').prop("selected", true);
                                    });
                                }
                                $('select[name="selectLocutoresDisponibles"], select[name="selectPersonalDisponibles"]').bootstrapDualListbox('refresh');
                                $("#panelesUsoCabinaAudio :input").prop("disabled", rolActual=="100" && data.estado.id == 7);
                                $("#panelInfoOperadoresSala div").html("<strong>Operador de sala</strong><div class='row'>"+nombresOperadores(data.operadoresSala)+"</div>");
                            }

                            if (event.resourceId.startsWith("h") && data.formatos.length > 0) {
                                $.each(data.formatos, function(indf, f) {
                                    $("#fmto" + f.formato.id).val(f.cantidad).prop("disabled", rolActual=="100" && data.estado.id == 7);;
                                });
                                $("#panelesFase5 :input").prop("disabled", rolActual=="100" && data.estado.id == 7);
                            }

                            if (data.estado.id <= 5) {
                                $("#btnModalAceptar").html("Actualizar");
                                $("#btnModalCancelar").html("Cerrar ventana sin actualizar");
                                //admin
                                $("#btnModalBorrar").toggle(true);

                            }

                            if ( rolActual=="10" && data.estado.id == 7) {
                                $("#btnModalAutorizar").show();
                                $("#btnModalCancelar").html("Cerrar ventana sin autorizar");
                            }

                            if ( rolActual!="10" && data.estado.id >= 7) {
                                $("#btnModalAceptar").hide();
                                $("#btnModalCancelar").html("Cerrar ventana");
                                //admin
                                $("#btnModalBorrar").toggle(rolActual=="1");
                            }
                        } else {
                            // SE TRATA DE UN EVENTO NUEVO
                            // CAMBIAR ROTULOS DEL BOTON DE ACEPTAR SEGUN CORRESPONDA
                            $(":input").prop("disabled", false)
                            $("#btnModalAceptar").html("Solicitar");
                            $("#btnModalCancelar").html("Cerrar ventana sin solicitar");
                            $("#btnModalBorrar").hide();

                            // Muestra quien es el operador de sala
                            const rs = ["c","d","e","f","g"];
                            if( rs.includes(event.resourceId.slice(0,1))){
                                var idPersonal = event.resourceId.slice(1);
                                var $operadoresSalaTurno=LlamadaAjax("/ajaxOperadoresDisponiblesPorSalaEvento","POST",JSON.stringify({"salaId":event.salaId, "fecha": event.start.format("YYYY-MM-DD") ,"desde": event.start.format("HH:mm"), "hasta": event.end.format("HH:mm")}));
                                var $nombrePersonal = LlamadaAjax("/ajaxBuscaPersona", "POST",JSON.stringify({"id":idPersonal}));
                                $.when($operadoresSalaTurno, $nombrePersonal).done(function(data, dataNombre){
                                    console.dir(data);
                                    console.dir(dataNombre);
                                    $("#panelInfoOperadoresSala").find(".row").html("<div class='col-sm-12'>Operador: "+dataNombre.nombre+" "+dataNombre.paterno+" "+dataNombre.materno+"</div>");
                                });
                            }
                        }

                        // Si es operador de sala o jefe de depto o administrador
                        $("input[type='radio'][name='rgFaseSala']").prop("disabled", (rolActual=="16" || rolActual=="127" || rolActual=="1") );
                        // Cuando es operador de sala y estado 7 (Equipo y accesorios asignados)
                        console.log("mostrar boton")
                        $("#btnModalGuardar, #btnModalLiberar").toggle(  rolActual=="16" && data.estado.id == 7  );
                        if (data!=null && data.estado!=null)
                            if(   ((rolActual.localeCompare("100")==0) && (data.estado.id == 7))
                                  || (rolActual.localeCompare("1")==0)
                              ){
                                $("#myModal2 input[type='radio'],\
                                   #myModal2 input[type='text'],\
                                   #myModal2 input[type='checkbox']\
                                   ").prop("disabled", true);
                                  $('select').bootstrapDualListbox("refresh");
                            }
                        $('select[name^="select"][name$="Disponibles"]').bootstrapDualListbox('refresh');
                    }
                );
        });
}


/**
 * Regresa una cadena con los nombres de los operadores de sala dentro de divs
 * divs ocultos
 */
function nombresOperadores(datos){
	var cad="";
    $.each(datos, function(i,v){
		cad+="<div style='display:none'>"+v.personal.id+"</div><div class='col-sm-3 col-md-3 col-lg-3'>"+v.personal.nombre+" "+v.personal.paterno+" "+v.personal.materno +"</div>";
	});
	return cad;
}




