		$('#btnModalAceptar, #btnModalAutorizar').off('click');
		$('#btnModalAceptar, #btnModalAutorizar').on('click',function(){
		    console.clear();
			console.log("desde #btnModalAceptar,  #btnModalAutorizar    click");
			var esteBoton = $(this)[0].id;
            var evento = new Object;
            evento.id = $("#divIdTemporal").text();
            evento.folioproductorasignado ={"id":$("#divFolioProductorAsignadoId").text()};
            evento.inicio =  moment($('#divConfirmarFecha').text()+" "+$('#evtDesde').html(), 'DD/MM/YYYY HH:mm');
            evento.fin =  moment($('#divConfirmarFecha').html()+" "+$('#evtHasta').text(), 'DD/MM/YYYY HH:mm');
            evento.estado = {id:$("#divIdEstado").text()   };     
            $(".with-errors").removeClass("has-error");

            var forma = $("#myModal2 div[id^='paneles']:visible");
            
            
         	// VALIDAR 
         	
/*          	Hora de inicio antes de hora de fin
         	
         	Preproducción
         		Hora de salida antes de hora de inicio
         		scouting:     cantidad de personal solicitado  contra cantidad de personal a asignar (cantidades por perfil)
        	Grabación 
        		Hora de salida antes de hora de inicio
        		Si solicita vehiculo lanzar aviso sino se asignó vehículo
        		Cantidad de personal solicitado  contra cantidad de personal a asignar (cantidades por perfil)
        		Que al menos se asigne una cámara
        	Salas
        		Posproducción:	cantidad de personal solicitado  contra cantidad de personal a asignar (cantidades por perfil)
        		
        	Entrega
        		Indicar cantidad de al menos un formato
        		
        	
        		
        		
        	EN TODOS LOS CASOS DEBERA DE VOLVERSE A VALIDAR DISPONIBILIDAD DE HORARIOS PASRA SALAS, EQUIPO Y ACCESORIOS,  Y/O PERSONAL	
        	
        	*/
        	

            
            // FASE 1 - PREPRODUCCIÓN
            if ( $("#panelesFase1").is(":visible")  ){
            	//console.clear()
            	console.log("FASE 1")
            	evento.fase = {id:1};            	
            	if ( $("#panelesFase1 input[type='radio'][name='rgTipoPreprod']:checked").length==0  ){
                    swal(
                            'Error',
                            'Debe seleccionar entre \'junta de trabajo\' o \'Scouting\' ',
                            'error'
                          );
                    return false;            		
            	}
            	
            	
                if ( $('#panelesFase1 input[name="hSalida"]').val().length!=0  )
                    evento.salidas=[{salida:moment($('#divConfirmarFecha').text()+" "+$('#panelesFase1 input[name="hSalida"]').val(), 'DD/MM/YYYY HH:mm')}];
                // Junta de trabajo
                if ( $("#panelesFase1 input[type='radio'][name='rgTipoPreprod'][value='1']").is(":checked") ){
                    evento.juntas = [{}];
                }

                // Scouting
                if ( $("#panelesFase1 input[type='radio'][name='rgTipoPreprod'][value='2']").is(":checked") ){
                	evento.locaciones = [{}];
                    if ($("#panelesFase1 input[name='locacion']").val().length != 0)
                        evento.locaciones = [{locacion:$("#panelesFase1 input[name='locacion']").val()}];
                    //Productor
                    if (rolActual=="100"){
                            // Obtener los ids de los roles para esta fase
                            /*
                            console.log ("ANTES DE LOS     LOS ROLESSSSSSSSSSSSSSSSSSSSSSSSSSS")

                            console.log('%c ROLES ', 'background: #FF0000; color: #FFFFFF');

                            $rolesFase = LlamadaAjax("/rolPorFase", "POST",  JSON.stringify( {faseId: 1 }  ) );
                            $.when($rolesFase).done(function(roles){
                                console.log ("LOS ROLESSSSSSSSSSSSSSSSSSSSSSSSSSS")
                                console.dir(roles)
                                var cadena="[";
                                for (var i=0; i<roles.length; i++){
                                    cadena+="{\"rol\":{\"id\":"+roles[i]+"}, \"cantidad\": "+$("#panelesFase1 input[name='inp_preprod_"+roles[i]+"']").val()+"},";
                                }
                                if (cadena.length>1)
                                    cadena = cadena.substring(0, cadena.length-1);
                                cadena+="]";
                                console.log(cadena);
                                evento.personal = JSON.parse(cadena);
                                console.dir(cadena);
                                alert("oki");
                            });
                            */

                         evento.personal =  [
                             {
                                rol:{id:18},
                                cantidad:$("#panelesFase1 input[name='inp_preprod_18']").val()
                             },
                             {
                                rol:{id:14},
                                cantidad:$("#panelesFase1 input[name='inp_preprod_14']").val()
                             },
                             {
                                rol:{id:17},
								cantidad:$("#panelesFase1 input[name='inp_preprod_17']").val()
                             },
                             {
                                rol:{id:13},
								cantidad:$("#panelesFase1 input[name='inp_preprod_13']").val()
                             }
                           ]
                     } 

                    // Administrador
                    if (rolActual=="1"){
                        evento.cuentaRol = [];
	                    $("#panelesFase1 select[name='selectPersonalDisponibles'] option:selected").each(function(i, e){
	                          evento.cuentaRol.push({ cuentarol: {id: $(e).val()}} );
	                    });
                    }
                }

	
                		
                if (esteBoton == "btnModalAceptar" && rolActual=="100"  &&  $("#panelesFase1 div[name='preguntaSalida'] input[name='vehiculo']:checked").length > 0 ){
                	evento.vehiculos = [];
                }

                // Es autorización y administrador u Operador de sala o ingeniero?
                if(esteBoton == "btnModalAutorizar" && (rolActual=="1" || rolActual=="16" || rolActual=="10" )){
                	evento.vehiculos = [];
	                  $('#panelesFase1 select[name="selectVehiculosDisponibles"] option:selected').each(function(){
	                      evento.vehiculos.push( {vehiculo:{id: $(this).val()}}  );
	                  });
                }



                // ESTADO 4 o 5   / TIPO PREAGENDA
                if (evento.estado.id == 4 || evento.estado.id == 5 ){
                    // ROL PRODUCTOR
                  }


            } //fin fase 1



            // FASE 2 - GRABACIÓN
            if ( $("#panelesFase2").is(":visible")){
            	console.log('%c FASE 2! ', 'background: #222; color: #bada55');
                evento.fase = {id:2};
                if (rolActual!="10"){
                    if ( $('#panelesFase2 input[name="hSalida"]').val().length!=0  )
                        evento.salidas=[{salida:moment($('#divConfirmarFecha').text()+" "+$('#panelesFase2 input[name="hSalida"]').val(), 'DD/MM/YYYY HH:mm')}];
                    if ($("#panelesFase2 input[name='locacion']").val().length!=0)
                        evento.locaciones = [{locacion:$("#panelesFase2 input[name='locacion']").val()}];
                    if ($("#panelesFase2 input[name='vehiculo']").is(":checked")){
                        evento.vehiculos = [];
                    }
                    //Locutores
                    if ($('#panelesFase2 select[name="selectLocutoresDisponibles"] option:selected').length > 0){
                      var locutor = new Object;
                      evento.locutores = [];
                      if (rolActual=="100"){
                            console.log("AAAAAAAAQUI")
                          $('#panelesFase2 select[name="selectLocutoresDisponibles"] option:selected').each(function(){
                                  locutor =  {personal:{id: $(this).val()}};
                                  evento.locutores.push(locutor) ;
                          });
                      }
                    }

                    //Personal y vehiculos
                    if (rolActual=="1"){
                          evento.cuentaRol = [];

                          evento.vehiculos = [];
                          if ($('#panelesFase2 select[name="selectPersonalDisponibles"] option:selected').length == 0) {
                              swal(
                                      'Error',
                                      'No ha asignado personal para cubrir la grabación',
                                      'error'
                                    );
                              return false;
                          } else {
                              $('#panelesFase2 select[name="selectPersonalDisponibles"] option:selected').each(function(){
                                  evento.cuentaRol.push( { cuentarol:{id: $(this).val()}} );
                              });
                          }

                          //evento.vehiculos = {vehiculo: {id: $("#selectVehiculosDisponibles option:selected").val()} };
                          //if ($("#panelesFase2 select[name='selectVehiculosDisponibles'] option:selected").length >0)
                              $('#panelesFase2 select[name="selectVehiculosDisponibles"] option:selected').each(function(){
                                  evento.vehiculos.push( {vehiculo:{id: $(this).val()}}  );
                              });
                    }
                }


                //Equipo y accesorios
                // Equipo/accesorios seleccionados
                if ($('#panelesFase2 select[name="selectEquipoAccesoriosDisponibles"] option:selected').length > 0){
                  var equipo = new Object;
                  var accesorio = new Object;
                  evento.equipos = [];
                  evento.accesorios = [];

                  $('#panelesFase2 select[name="selectEquipoAccesoriosDisponibles"] option:selected').each(function(){
                      if ( $(this).val().startsWith("E")  ){
                        //Lo autoriza un ingeniero?
                        if (rolActual=="10")
                            equipo = {equipo:{id: $(this).val().substring(1)} , autorizo: {id: usuarioActual}   };
                        else
                            equipo = {equipo:{id: $(this).val().substring(1)} };
                        evento.equipos.push( equipo );
                      }
                      if ( $(this).val().startsWith("A")  ){
                          if (rolActual=="10")
                            accesorio = {accesorio:{id: $(this).val().substring(1)}, autorizo: {id: usuarioActual} };
                          else
                            accesorio = {accesorio:{id: $(this).val().substring(1)} };
                          evento.accesorios.push( accesorio );
                      }
                  });

                  // No seleccionó equipo
                  if (evento.equipos.length<1){
                      swal(
                              'Error',
                              'No ha seleccionado equipo (cámara) para la grabación.',
                              'error'
                            );
                      return false;
                  }
                }
                // Equipo/accesorios NO seleccionados
                if ($('#panelesFase2 select[name="selectEquipoAccesoriosDisponibles"] option:selected').length == 0){
                    //                	$("[name='inp_preprod_4']").closest(".form-group").removeClass("has-error");
                    swal(
                            'Error',
                            'No ha seleccionado equipo ni accesorios.',
                            'error'
                          );
                	return false;
                }

                if (rolActual!="10"){
                    //Ingenieros para la admon de equipo y accesorios
                    //El productor elejirá de entre los ingenieros activos a quién revisará y asignará el equipo y accesorios
                    if ($('#panelesFase2 select[name="selectIngenierosDisponibles"] option:selected').length > 0){
                        evento.ingsAdmonEqpo = [];
                        $('#panelesFase2 select[name="selectIngenierosDisponibles"] option:selected').each(function(){
                           var ingeniero = new Object;
                           ingeniero = { ingeniero:{id: $(this).val()} };
                           evento.ingsAdmonEqpo.push(ingeniero);
                        });
                    } else {
                        swal(
                                'Error',
                                'No ha seleccionado un ingeniero con quién se coordine para la asignación de equipo y accesorios.',
                                'error'
                              );
                        return false;
                    }



                    $("[name='inp_preprod_14']").closest(".form-group").removeClass("has-error");
                    //Valida que se solicite al menos un camarógrafo
                    if ( (rolActual=="100") &&  ($("#panelesFase2 input[name='inp_preprod_14']").val().length == 0)){
                        $("[name='inp_preprod_4']").closest(".form-group").addClass("has-error");
                        swal(
                                'Error',
                                'No es posible agendar grabación sin camarógrafo, solicite al menos un camarógrafo.',
                                'error'
                              );
                        return false;
                    }

                    // FASE 2, ESTADO 4 o 5   / TIPO PREAGENDA
                    if (evento.estado.id == 4 || evento.estado.id == 5 ){
                        // ROL PRODUCTOR
                        if (rolActual=="100"){
                            console.log('%c FASE 2 - ROL PROD! ', 'background: #222; color: #bada55');
                                evento.personal =  [
                                    {
                                        rol:{id:18},
                                        cantidad:$("#panelesFase2 input[name='inp_preprod_18']").val()
                                    },
                                    {
                                        rol:{id:14},
                                        cantidad:$("#panelesFase2 input[name='inp_preprod_14']").val()
                                    },
                                    {
                                        rol:{id:17 },
                                        cantidad:$("#panelesFase2 input[name='inp_preprod_17']").val()
                                    }

                                  ]

                        }
                    }

                    // ESTADO 7  /  TIPO AGENDA
                    if (evento.estado.id == 4 && evento.estado.id == 5){
                        var $x = LlamadaAjaxDatos($("#divIdTemporal").html(), $("#divIdEstado").text()==7?'agenda':'preagenda');
                    }
                }
            } // fin fase 2


            // RECURSO SALA DE INGESTA, CALIFICACIÓN Y REVISIÓN -  panelesSalaCR
            if ( $("#panelesSalaCR").is(":visible")){
				console.log("- SALA DE INGESTA y CALIFICACION -")
            	if ($("#panelesSalaCR input[name='rgFaseSala']:checked").length==0){
            		swal("Error",
                			"Debe especificar el uso de la sala",
                			"error");
                		return false;              		
            	}
            	var e = null;            
            //    if (  $('#calendario').children().length > 0 ){
    	            var eventos = $('#calendario').fullCalendar('clientEvents', function(evt) {
    	                return (evt.id == $('#divIdTemporal').text()   );
    	                });
    	            e = eventos[0];
                
	                console.dir(evento)
	                console.dir(e)
	            	auxFase = $("#panelesSalaCR input[name='rgFaseSala']:checked").val()=='I'||$("#panelesSalaCR input[name='rgFaseSala']:checked").val()=='C'?1:4;
	                evento.fase = {id:  auxFase  };
	                evento.salas = [{sala:{id:1}
	                				, usoscabina:[{usocabina:$("#panelesSalaCR input[name='rgFaseSala']:checked").val()}]
	                				}];
	                evento.operadoresSala = [{personal:{id:e.resourceId.slice(1,10)}}];
	        //    }    
            }

            // RECURSO SALAS DE EDICIÓN    panelesSalaEdicion3, panelesSalaEdicion2, panelesSalaEdicion7
            if ( $("#panelesSalaEdicion3, #panelesSalaEdicion2, #panelesSalaEdicion7").is(":visible")){
	             	let numSala = 0;
		            if ( $("#panelesSalaEdicion3").is(":visible"))
		            	numSala = 3;
		            if ( $("#panelesSalaEdicion2").is(":visible"))
		                numSala = 2;
		            if ( $("#panelesSalaEdicion7").is(":visible"))
		                numSala = 7;
		
		        	if (  $("#panelesSalaEdicion"+numSala+" input[name='rgFaseSala']:checked").length==0  ){
		        		swal("Error",
		            			"Por favor, seleccione Edición o Posproducción",
		            			"error");
		            		return false;                		
		        	}            
		            
		            
		            evento.fase = {id:  $("#panelesSalaEdicion"+numSala+" input[name='rgFaseSala']:checked").val()  };
		            evento.salas = [{sala:{id:numSala}}];
		            if (  $("#panelesSalaEdicion"+numSala+" input[name='rgFaseSala']:checked").val() == 4 ){
		                if ( rolActual=="1" ){
		                    evento.personal = [];
		                    $("#panelesSalaEdicion"+numSala+" select[name='selectRDisenioDisponibles'] option:selected").each(function(i, e){
		                          evento.personal.push({ personal: {id: $(e).val()}} );
		                    });
		                }
		                /*
		                if ( rolActual=="100"  &&  $("#panelesSalaEdicion"+numSala+" input[name='inp_preprod_12']").val().length!=0  ){
		                    evento.personal =  [
		                        {
		                            rol:{id:12},
		                            cantidad: $("#panelesSalaEdicion"+numSala+" input[name='inp_preprod_12']").val()
		                        }
		                    ]
		                }
		                */
		            }
	        }


            // RECURSO CABINA DE AUDIO - panelesUsoCabinaAudio
            if ( $("#panelesUsoCabinaAudio").is(":visible")){
                evento.fase = {id:  4  };
                //Seleccionó uso de cabina???
                if (  $("#panelesUsoCabinaAudio input[name='rgUsoCabina']:checked").length == 0 ){
            		swal("Error",
                			"Debe especificar el uso de la cabina de audio",
                			"error");
                		return false;                	
                	
                }
                if (  $("#panelesUsoCabinaAudio input[name='rgUsoCabina']:checked").val() == 'V' ){


                	//Se ha seleccionado locutor?
                	if ( $("#panelesUsoCabinaAudio select[name='selectLocutoresDisponibles'] option:selected").length==0 ){
                		swal("Error",
                			"Debe solicitar locutor para grabar voz.",
                			"error");
                		return false;
                	}
                	evento.salas = [{sala:{id:4}, usoscabina:[{usocabina:"V"}] }];
                	var locutor = new Object;
                	evento.locutores = [];
                	var personal = new Object;
                    evento.personal = [];

                	if (  rolActual=="100"  &&  (evento.estadoId!=7)   ){
	                    $("#panelesUsoCabinaAudio select[name='selectLocutoresDisponibles'] option:selected").each(function(){
	                    	locutor =  {personal:{id: $(this).val()}   };
	                    	evento.locutores.push(  locutor  );
	                    });
                	}

                	if (  rolActual=="1" ){
                        $("#panelesUsoCabinaAudio select[name='selectLocutoresDisponibles'] option:selected").each(function(){
                            personal =  {personal:{id: $(this).val()}   };
                            evento.personal.push(  personal  );
                        });
                	}
                }
                if (  $("#panelesUsoCabinaAudio input[name='rgUsoCabina']:checked").val() == 'M' ){
                    evento.salas = [{sala:{id:4}, usoscabina:[{usocabina:"M"}] }];
                }
            }

            // RECURSO ENTREGA
            if ( $("#panelesFase5").is(":visible")){
            	var numCeros=0;
            	var tamFmtos = $("[name='losFormatos']").length;
            	console.dir( $("[name='losFormatos']")  )
            	console.log(tamFmtos)
            	for (var i=0; i<tamFmtos; i++){
            		if (  $("#fmto"+(i+1)).val() == 0 || $("#fmto"+(i+1)).val().length == 0  ){
            			numCeros++;
            		}
            	}
            	
            	console.log(numCeros)
            	
            	
            	if (numCeros == $("[name='losFormatos']").length){
            		swal("Error",
                			"Debe solicitar copias en al menos un tipo de formato.",
                			"error");
                		return false;
                	
            	}
            	
            	
            	evento.fase = {id:5};
                evento.formatos =  [
                    {
                        formato:{id:1},
                        cantidad: $("#fmto1").val()
                    },
                    {
                        formato:{id:2},
                        cantidad: $("#fmto2").val()
                    },
                    {
                        formato:{id:3},
                        cantidad: $("#fmto3").val()
                    },
                    {
                        formato:{id:4},
                        cantidad: $("#fmto4").val()
                    }
            ];
                console.dir(evento.formatos)
                
            }            


            	evento.inicio = moment(evento.inicio).format("YYYY-MM-DD[T]kk:mm[:00.000-06:00]");
            	evento.fin = moment(evento.fin).format("YYYY-MM-DD[T]kk:mm[:00.000-06:00]");
            	if (evento.salidas!=undefined && evento.salidas[0].salida !=undefined)
            	    evento.salidas[0].salida = moment(evento.salidas[0].salida).format("YYYY-MM-DD[T]kk:mm[:00.000-06:00]");

			console.log("--------evento")            
            console.dir(evento)         	
            var $validos = LlamadaAjaxValidaPrevioAutorizar(evento );
        	$.when($validos).done(function(data){
        		console.log("LlamadaValidaPrevioAutorizar regresó ....!")
        		console.dir(data)
                var e = null;
                if (  $('#calendario').children().length > 0 ){
    	            var eventos = $('#calendario').fullCalendar('clientEvents', function(evt) {
    	                return (evt.id == $('#divIdTemporal').text()   );
    	                });
    	            e = eventos[0];
                }

                // Botón aceptar y rol de productor
                if (esteBoton == "btnModalAceptar" && rolActual=="100"){
                	//Si hay hora de salida, esta debe ser antes de la hora de inicio
                	$("[name='hSalida']").closest(".form-group").removeClass("has-error");
                	if (evento.salidas){
                		if (evento.salidas[0] && evento.salidas[0].salida){


                			if (    moment(evento.salidas[0].salida).isAfter(   evento.inicio)  ){
                				$("[name='hSalida']").closest(".form-group").addClass("has-error");
        					    swal(
        							      'Error',
        							      'La hora de salida no puede ser posterior a la hora de inicio, por favor corrija.',
        							      'error'
        							    )
                				return false;
                			}
                		}
                	}

                    if (evento.id == 0){
                    	evento.estado.id = 5;
                    }

                    evento.operadoresSala = [{  personal:{id:e.resourceId.slice(1)}/*, sala: {id: evento.salas[0].sala.id}*/  }]; 

                    console.log("justo previo a LlamadaAjaxGrabaEvento.....");
                    console.dir(evento)
                    console.log("justo previo a LlamadaAjaxGrabaEvento.....");
                    console.dir(e)

                    $grabar = LlamadaAjaxGrabaEvento(evento, e);
                    $.when($grabar).done(function(data){
                		if (data.agregado){
                				$.notify({
                					title: "<strong>Correcto:</strong> ",
                					message: "Se ha solicitado."
                				});	
                				if (e != null){
    	            				e.id = data.id; 
    	            				e.tipo="preagenda";
    	            				e.estadoId = 5;
    	            				e.eventoExterno=false;
    	            				$('#calendario').fullCalendar('updateEvent', e);
                				}
                				$("#divIdTemporal").html(data.id);
                				$('#btnModalCancelar').click();
                			} else {
                				$.notify({
                					title: "<strong>Error:</strong> ",
                					message: "No fue posible hacer la solicitud.."
                				},{
                					type: 'danger'
                				});							
    	            			if (e != null){	
    	            				$('#calendario').fullCalendar('removeEvent', e);
    	            			}
                			}                	
                    });
                } // fin Botón aceptar y rol de productor

                console.log("....")
                console.log(rolActual)
                console.log(esteBoton)

    			// Autorizar evento por administrador u operador de sala
                if (esteBoton == "btnModalAutorizar" && (rolActual=="1" || rolActual=="16" || rolActual=="10")){
                    console.log("se envía a LlamadaAjaxAutorizaEvento.....", evento);
    	            for (let [k, v] of Object.entries(evento)) {
    	            	    console.log( k+" : "+v +"   "+typeof(v) )
    	            }    	            
    	            elPanel = $("div[id^='paneles']:visible");
    	            console.log( $(elPanel).prop("id")   )
    	            // Volver a checar las validaciones

    	            var arrEntradas = new Array();
    	            // Es administrador?
    	            if (  rolActual == "1"){
                        arrEntradas.push(validarHoraSalida(elPanel));
                        arrEntradas.push(validarVehiculos(elPanel));
                        arrEntradas.push(validarLasCantidades(elPanel));
                        arrEntradas.push(validarRespDisenio(elPanel));
                        arrEntradas.push(validarLocutores(elPanel));
                        arrEntradas.push(validarCopias(elPanel));
                    }

                    // Es Ingeniero admon accesorios y equipo?
                    if (  rolActual == "10"){
    	                arrEntradas.push(validarEquipoAccesorios(elPanel));
                    }


                    Promise.all(arrEntradas).then((arrRetornos)=>{
                        console.dir(arrRetornos)
    	            	var contaErrores=0;
    	            	var contaWarnings=0;
    	            	var contaOks=0;

    	            	arrRetornos.forEach(function(r){
    	            		switch (r) {
								case "ok": contaOks++; break;
								case "warning": contaWarnings++; break;
								case "error": contaErrores++; break;
							}
    	            	});

    	            	if ( contaErrores>0 ){
    						    swal(
    								      'Error',
    								      'Corrija los errores marcados con <div class="mismaLinea" style="color:red;"><i class="fas fa-exclamation-circle"></i></div>',
    								      'error'
    								    );
    						    return false;
	            		} else {
		            		if (contaWarnings>0){
	                			swal({
	                				  title: "¿Continuar?",
	                				  html: "Existen algunas advertencias en las secciones marcadas con <div style='color:orange' class='mismaLinea'><i class='fas fa-exclamation-circle'></i></div> </br></br>",
	                				  type: "question",
	                				  showCancelButton: true,
	                				  confirmButtonColor: "#3085d6",
	                				  cancelButtonColor: '#d33',
	                				  confirmButtonText: "Si, autorizar así",
	                				  cancelButtonText: "No, corregir el formulario",
	                				  buttonsStyling: false,
	                				  confirmButtonClass: 'btn btn-warning btn-lg',
	                				  cancelButtonClass: 'btn btn-success btn-lg',
	                				  preConfirm: function(){
	                					  return new Promise(function (resolve, reject) {
	                  						var e = null;
	                						  if ($('#calendario').fullCalendar()){
	                      						var eventos = $('#calendario').fullCalendar('clientEvents', function(evt) {
	                		    					return (evt.id == $('#divIdTemporal').text()   );
	                      						});						
	                      						e = eventos[0];            							  
	                						  }
	                						  console.log("002")
	                						  console.dir(evento)
	                						  console.dir(e)
	                						  var $o = auxAutorizaEvento(evento, e);
	                						  $.when($o).done(function(data){
	                							  resolve();
	                						  });
	                					  });
	                				  }
	                				}).then(function () {
	                						return d.promise();            						
	                				}, function (dismiss) {
	                					  if (dismiss === 'cancel') {
//	                 						Se cierra el cuadro de diálogo (sweetAlert2) y se continua con el formulario
	                						  reject();
	                					  }
	                				}).catch(swal.noop);  
		            		} else {
          						var e = null;
      						  if ($('#calendario').fullCalendar()){
            						var eventos = $('#calendario').fullCalendar('clientEvents', function(evt) {
      		    					return (evt.id == $('#divIdTemporal').text()   );
            						});						
            						e = eventos[0];            							  
      						  }
      						  console.log("001")
  							  console.dir(evento)
  							  console.dir(e)
  							  console.log("antes de auxAutorizaEvento")
      						  var $o = auxAutorizaEvento(evento, e);
      						  $.when($o).done(function(data){
      							//  console.clear()
      							console.dir(data)
      							  console.log("Se autorizó: "+data.autorizado)
      						  });
		            		}   	            		
	            		}
	            	
    	            });
                }  // fin autorizar evento por administrador u operador de sala





		//********** ************* *************** ************* ***********

    			// Autorizar evento por ingeniero admon equipo y accesorios
                if (esteBoton == "btnModalAutorizar" && rolActual=="10"){
    	            for (let [k, v] of Object.entries(evento)) {
    	            	    console.log( k+" : "+v +"   "+typeof(v) )
    	            }
    	            elPanel = $("div[id^='paneles']:visible");
    	            console.log( $(elPanel).prop("id")   )
    	            // Volver a checar las validaciones

                    console.log("validarEquipoAccesorios....................");

    	            var $v4 = validarEquipoAccesorios(elPanel);
    	            $.when($v4).done(function(r4){
    	            	var arrRes =  [r4];
    	            	var contaErrores=0;
    	            	var contaWarnings=0;
    	            	var contaOks=0;
    	            	console.log(r4)
    	            	console.dir(evento)
    	            	console.dir(arrRes)
    	            	arrRes.forEach(function(r){
    	            		switch (r) {
								case "ok": contaOks++; break;
								case "warning": contaWarnings++; break;
								case "error": contaErrores++; break;
							}
    	            	});

    	            	if ( contaErrores>0 ){
    						    swal(
    								      'Error',
    								      'Corrija los errores marcados con <div class="mismaLinea" style="color:red;"><i class="fas fa-exclamation-circle"></i></div>',
    								      'error'
    								    );
    						    return false;
	            		} else {
		            		if (contaWarnings>0){
	                			swal({
	                				  title: "¿Continuar?",
	                				  html: "Existen algunas advertencias en las secciones marcadas con <div style='color:orange' class='mismaLinea'><i class='fas fa-exclamation-circle'></i></div> </br></br>",
	                				  type: "question",
	                				  showCancelButton: true,
	                				  confirmButtonColor: "#3085d6",
	                				  cancelButtonColor: '#d33',
	                				  confirmButtonText: "Si, autorizar así",
	                				  cancelButtonText: "No, corregir el formulario",
	                				  buttonsStyling: false,
	                				  confirmButtonClass: 'btn btn-warning btn-lg',
	                				  cancelButtonClass: 'btn btn-success btn-lg',
	                				  preConfirm: function(){
	                					  return new Promise(function (resolve, reject) {
	                  						var e = null;
	                						  if ($('#calendario').fullCalendar()){
	                      						var eventos = $('#calendario').fullCalendar('clientEvents', function(evt) {
	                		    					return (evt.id == $('#divIdTemporal').text()   );
	                      						});
	                      						e = eventos[0];
	                						  }
	                						  console.log("002")
	                						  console.dir(evento)
	                						  console.dir(e)
	                						  var $o = auxAutorizaEvento(evento, e);
	                						  $.when($o).done(function(data){
	                							  resolve();
	                						  });
	                					  });
	                				  }
	                				}).then(function () {
	                						return d.promise();
	                				}, function (dismiss) {
	                					  if (dismiss === 'cancel') {
                                            //Se cierra el cuadro de diálogo (sweetAlert2) y se continua con el formulario
	                						  reject();
	                					  }
	                				}).catch(swal.noop);
		            		} else {
          						var e = null;
      						  if ($('#calendario').fullCalendar()){
            						var eventos = $('#calendario').fullCalendar('clientEvents', function(evt) {
      		    					return (evt.id == $('#divIdTemporal').text()   );
            						});
            						e = eventos[0];
      						  }
      						  console.log("001")
  							  console.dir(evento)
  							  console.dir(e)
      						  //var $o = auxAutorizaEvento(evento, e);
      						  //var $o = LlamadaAjax("/autorizaEquipoAccesorios", "POST", JSON.stringify({evento}));

      						  /////////////////////////////////////////////////////////////////////////////////////
      						  /////////////////////////////////////////////////////////////////////////////////////

      						 // var $o = LlamadaAjax("/ajaxAsignaEA", "POST", JSON.stringify({evento}));
/*
      						  $.when($o).done(function(data){
      						        //  console.clear()
      							    console.dir(data)
      							    console.log("Se autorizó: "+data.autorizado)
      							          // Se llama desde agenda
                                          if ( (rolActual=="1" || rolActual=="16") && data.autorizado==true && e!=null){
                                              e.tipo="agenda";
                                              e.estadoId = 7;
                                              console.log(e)
                                              e.eventoExterno=false;
                                              $('#calendario').fullCalendar('updateEvent', e);
                                          }
                                          // Se llama desde misServiciosAdmin
                                          if (  (rolActual=="1" || rolActual=="16")  && e==null){
                                              if ( data.autorizado==true){
                                                  $("#tablaMisServiciosDetalle tr.selected").fadeOut(800, function() {
                                                      $(this).remove();
                                                      $("#contadorDetalle").html(  $("#tablaMisServiciosDetalle tbody tr").length);
                                                      $.notify( "Se autorizó el evento" );
                                                  });
                                              } else {
                                                  $.notify( "No fue posible autorizar el evento" );
                                              }
                                          }
                                          $('#btnModalCancelar').click();
      						  });
      						  */
		            		}
	            		}

    	            });
                }  // fin autorizar evento por ingeniero admon equipo y accesorios

            	         		
        	});	// fin de when
        	
        	
        	





	  	});      
        
        
        function auxAutorizaEvento(evento, e ){
        	var dd = $.Deferred();
        	var $grabar;
            // Si se trata de Administrador autoriza evento
            if (rolActual=="1"){
                $grabar = LlamadaAjaxAutorizaEvento(evento, e);
            }
            // Si se trata de Admin de Equipo y Acesorios, autoriza/asigna EA al evento
            if (rolActual=="10"){
                console.dir(evento)
                $grabar = LlamadaAjax("/ajaxAsignaEA", "POST",  JSON.stringify(evento, e));
            }
            $.when($grabar).done(function(data){
                  // Se llama desde agenda?
                  if (e!=null){
                        if ( (rolActual=="1" || rolActual=="16") && data.autorizado==true ){
                              e.tipo="agenda";
                              e.estadoId = 7;
                              console.log(e)
                              e.eventoExterno=false;
                              $('#calendario').fullCalendar('updateEvent', e);
                          }
                        if ( rolActual=="10")
                            if (data.asignado==true){
                                $.notify({
                                    title: "<strong>Correcto:</strong> ",
                                    message: "Se asignó el equipo y accesorios."
                                });
                                e.tipo="agenda";
                                e.estadoId = 8;
                                console.log(e)
                                e.eventoExterno=false;
                                $('#calendario').fullCalendar('updateEvent', e);
                            } else {
                                $.notify({
                                    title: "<strong>Error:</strong> ",
                                    message: "No se pudo asignar el equipo y accesorios."
                                }),{type: 'danger'}
                        }
                  }

                  // Se llama desde MisServiociosX
                  if (e==null){
                      if (  (rolActual=="1" || rolActual=="16")){
                          if ( data.autorizado==true){
                              $("#tablaMisServiciosDetalle tr.selected").fadeOut(800, function() {
                                  $(this).remove();
                                  $("#contadorDetalle").html(  $("#tablaMisServiciosDetalle tbody tr").length);
                                  $.notify( "Se autorizó el evento" );
                              });
                          } else {
                              $.notify( "No fue posible autorizar el evento" );
                          }
                      }
                  }



              $('#btnModalCancelar').click();
              dd.resolve(data);	
            });   
            return dd.promise();	
        }
        
        
        // Deferred devuelve false cuando hay error 
        function validacionesHorarioSalidaInicio( salida,  inicio ){
        	console.log("validacionesHorarioSalidaInicio recibe "+salida+"  "+inicio)
        	var fok=true;
        	//Si hay hora de salida, esta debe ser antes de la hora de inicio
        	$("[name='hSalida']").closest(".form-group").removeClass("has-error");
        	console.log("valido "+moment(salida).isValid())
        	if (moment(salida).isValid()){
        			if (  salida.isAfter(inicio) ){
        				console.log("Abriendo sa2")
        				$("[name='hSalida']").closest(".form-group").addClass("has-error");
					    swal(
							      'Error',
							      'La hora de salida no puede ser posterior a la hora de inicio, por favor corrija.',
							      'error'
							    );
					    fok=false;
					    return false;
        			}        			
        	}    
        	return fok;
        }
        
        $(document).off( "change", "select[name='selectPersonalDisponibles']");
        $(document).on( "change", "select[name='selectPersonalDisponibles']", function(){
        	console.log("!!!!")
        	var elPanel = $(this).closest("div[id^='paneles']");
            if (rolActual=="1"){
                var $x=validarLasCantidades(elPanel);
                $.when($x).done(function(d1){
                	console.log(d1)
                });
            }            
		});
        

        $(document).off("blur","input[name='hSalida']");        
        $(document).on("blur","input[name='hSalida']", function(){
        	var elPanel = $(this).closest("div[id^='paneles']");
        	if (rolActual=="1"){
	            var $x=validarHoraSalida(elPanel);
	            $.when($x).done(function(d1){
	            	console.log(d1)
	            });
        	}
        });
        
        $(document).off("change","select[name='selectVehiculosDisponibles']");
        $(document).on("change","select[name='selectVehiculosDisponibles']", function(){
        	var elPanel = $(this).closest("div[id^='paneles']");
        	if (rolActual=="1"){
	            var $x=validarVehiculos(elPanel);
	            $.when($x).done(function(d1){
	            	console.log(d1)
	            });
        	}
        });
                	
        $(document).off("change","select[name='selectEquipoAccesoriosDisponibles']");        	
        $(document).on("change","select[name='selectEquipoAccesoriosDisponibles']", function(){
        	var elPanel = $(this).closest("div[id^='paneles']");
        	if (rolActual=="1"){
	            var $x=validarEquipoAccesorios(elPanel);
	            $.when($x).done(function(d1){
	            	console.log(d1)
	            });
        	}
        });        
        
        $(document).off("change","select[name='selectRDisenioDisponibles']");
        $(document).on("change","select[name='selectRDisenioDisponibles']", function(){
        	var elPanel = $(this).closest("div[id^='paneles']");
        	if (rolActual=="1"){
	            var $x=validarRespDisenio(elPanel);
	            $.when($x).done(function(d1){
	            	console.log(d1)
	            });
        	}
        });          
        
        
        $(document).off("change","select[name='selectLocutoresDisponibles']");
        $(document).on("change","select[name='selectLocutoresDisponibles']", function(){
        	var elPanel = $(this).closest("div[id^='paneles']");
        	if (rolActual=="1"){
	            var $x=validarLocutores(elPanel);
	            $.when($x).done(function(d1){
	            	console.log(d1)
	            });
        	}
        });          
        
        
        $(document).off("blur","input[name='losFormatos']");
        $(document).on("blur","input[name='losFormatos']", function(){
        	var elPanel = $(this).closest("div[id^='paneles']");
        	if (rolActual=="1"){
	            var $x=validarCopias(elPanel);
	            $.when($x).done(function(d1){
	            	console.log(d1)
	            });
        	}        	
        });
        
        
        $('#btnModalGuardar').off('click');
		$('#btnModalGuardar').on('click',function(){
			console.clear()
			var esteBoton = $(this)[0].id;
			let cadenaErroresTP="";
            var evento = new Object;
            evento.id = $("#divIdTemporal").text();
            evento.folioproductorasignado ={"id":$("#divFolioProductorAsignadoId").text()};
            evento.inicio =  moment($('#divConfirmarFecha').text()+" "+$('#evtDesde').text(), 'DD/MM/YYYY HH:mm');
            evento.fin =  moment($('#divConfirmarFecha').text()+" "+$('#evtHasta').text(), 'DD/MM/YYYY HH:mm');
            evento.estado = {id:$("#divIdEstado").text()   };     
            $(".with-errors").removeClass("has-error");

            var forma = $("#myModal2 div[id^='paneles']:visible");
			
			console.log(evento)
			console.log(forma)
			
			
			var AgendaServicioIngesta = new Object();
			var arrCambioFormatos = [];
			
			
			AgendaServicioIngesta.cambioFormato = $("input[name='radiocambioFormato']:checked").val();
			if (  AgendaServicioIngesta.cambioFormato === 'true' ) {
				$.each($("input[name^='cambioFormato']:checked"), function(){
					var formatosalida = new Object();
					var formatoingesta = new Object();
					formatoingesta.id = $(this).val();
					formatosalida.formatoingesta = formatoingesta;
					arrCambioFormatos.push(formatosalida);
					
				});
				AgendaServicioIngesta.formatosalida = arrCambioFormatos;
			}
			
			
			
			
			var EstadoMaterial = new Object();
			
			EstadoMaterial.id = $("#tabEdoCalificacion input[name='estadoMat']:checked").val();
			
			AgendaServicioIngesta.estado = EstadoMaterial;   
			
			AgendaServicioIngesta.calificadoPrevio = $("input[name='calificado']:checked").val();
			
			
			
			

			/*
			var formatosOtros = [];
			$.each($("input[name='cambioFormato']:checked"), function(){
                formatosOtros.push($(this).val());
            });
			AgendaServicioIngesta.solicitudesIngestaFormatoOtro = formatosOtros;
			*/
			var arrSolicitudesIngestaFormato = [];
			
			$.each($("input[name^='cantidadTarjetas-']"), function(){
				if ( $(this).val().length>0  ){
				console.log(  $(this).attr("name")  )
				console.log(  ($(this).attr("name")).indexOf("-")  )   
				console.log(  ($(this).attr("name")).substring(    ($(this).attr("name")).indexOf("-")+1 )   )
					var id =  ($(this).attr("name")).substring(    ($(this).attr("name")).indexOf("-")+1 );
					var AgendaServicioIngestaFormato = new Object();
					var Formatos = new Object();
					var solicitudesIngestaFormato = new Object();
					
					Formatos.id = id;
					
					
					AgendaServicioIngestaFormato.formatoingesta =  Formatos;
					AgendaServicioIngestaFormato.cantidadtarjetas = $(this).val();
                	arrSolicitudesIngestaFormato.push(AgendaServicioIngestaFormato);
                }
            });			
			
			AgendaServicioIngesta.solicitudesIngestaFormato = arrSolicitudesIngestaFormato;

			
			var arrDetallesVideo = [];
			var detalleVideo = new Object();
			var arrDetallesAudio = [];
			var detalleAudio = new Object();
					
			console.dir(AgendaServicioIngesta)		
			var arrIdiomas = [];			
			$.each($("#panelesSiNoOtro input[name='idioma']:checked"), function(){
					var idiomas = new Object();
					var idioma = new Object();
					
					idioma.id = $(this).val();
					idiomas.idioma = idioma; 
					console.dir(idiomas)
                	arrIdiomas.push(idiomas);
            });
            
            // Almacenamiento
			var arrAlmacen = [];			
			$.each($("#panelesSiNoOtro input[name='almacenamiento']:checked"), function(){
					var almacenamientos = new Object();
					var medioalmacenamiento = new Object();
					
					medioalmacenamiento.id = $(this).val();
					almacenamientos.medioalmacenamiento = medioalmacenamiento; 
					console.dir(almacenamientos)
                	arrAlmacen.push(almacenamientos);
            });            
            AgendaServicioIngesta.almacenamientos = arrAlmacen;

            
            detalleVideo.detalle = $("#panelesSiNoOtro #DetalleVideo").val();
            
            console.dir(detalleVideo)
            
            arrDetallesVideo.push(detalleVideo);
            
            detalleAudio.detalle = $("#panelesSiNoOtro #DetalleAudio").val();
            arrDetallesAudio.push(detalleAudio);
            

			AgendaServicioIngesta.detallesVideo = arrDetallesVideo;
			AgendaServicioIngesta.detallesAudio = arrDetallesAudio;
			
			
			AgendaServicioIngesta.idiomas = arrIdiomas;
	//		AgendaServicioIngesta.idiomasOtro = $("input[name='otroIdioma']").val();
	
	
	
			AgendaServicioIngesta.problemaIngesta = $("radio[name='problema']:checked").val();
			console.log(AgendaServicioIngesta.problemaIngesta === 'true')
			
			console.log(AgendaServicioIngesta.problemaIngesta);
			if (AgendaServicioIngesta.problemaIngesta === 'true'){
				var problemasIngesta = new Object();
				problemasIngesta.descripcion = $("#DescripcionProblemas").val();
				AgendaServicioIngesta.problemasIngesta = problemasIngesta;
				
			}
			
			
			console.dir(AgendaServicioIngesta)
			
			
                var e = null;            
                if (  $('#calendario').children().length > 0 ){
    	            var eventos = $('#calendario').fullCalendar('clientEvents', function(evt) {
    	                return (evt.id == $('#divIdTemporal').text()   );
    	                });
    	            e = eventos[0];
                }			
			
			var agenda = new Object();
			agenda.id = evento.id;
			AgendaServicioIngesta.agenda = agenda;
			evento.datosIngesta = AgendaServicioIngesta;
			
			
			var $pase=validarDatosIngesta();
			var $datosRetorno = null;
			$.when($pase).done(function(validaciones){
				console.log(validaciones)
				console.log(validaciones.length)
				if (validaciones.length > 0){
					var cadena="";
	            	validaciones.forEach(function(r){
	            		cadena+= r.descripcion+"<br>" ;
	            	});
                    swal(
                            'Error',
                            cadena,
                            'error'
                          );
                	return false;					
				
				} else {
				var estado = new Object();
				estado.id = 17;
				evento.estado = estado;
					$datosRetorno = LlamadaGuardaDatosIngesta(evento, e);	
				
					$.when($datosRetorno).done(function(data){
						console.dir(data)
						if (data.guardado != null){
						//	$('#myModal2').modal('hide');
							
							$.notify({
								title: "<strong>Correcto:</strong> ",
								message: "Se agrego la información de ingesta."					
							});												
						}
					});
				}
			
			
			
			
			
			});
			

			
			

			
			
			
		});     
        
        
		String.prototype.startsWith = function( str ){
			console.log(str+"   startsWith regresa: "+(this.indexOf( str ) === 0))
		    return ( this.indexOf( str ) === 0 );
		};        
        
        
        function nombreUsoCabina(str) {
			var cadena="";
			switch (str){
				case 'I': cadena="Ingesta"; break; 
				case 'D': cadena="Digitalización"; break;
				case 'M': cadena="Musicalización"; break;
				case 'V': cadena="Voz"; break;
				default: cadena="No definido";
			}
			return cadena;
		}
	

        
        
        