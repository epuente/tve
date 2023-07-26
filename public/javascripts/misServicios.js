const arrUsosCabina = {C:'Calificación', R:'Revisión', M:'Musicalización', V:'Voz en off', I:'Ingesta'}

function verDetalles(servicioId, tipo, fpaId) {
	var cadena = "";
	var losTabs = "";
	var datosGenerales="";
	$('.popover, [data-toggle="popover"]').popover('hide');
	console.clear();
	console.log(servicioId+" - "+ tipo+" - "+ fpaId)	
	$.when(LlamadaAjaxBuscaDetallesServicio(servicioId, tipo, fpaId))
			.done(
					function(data) {
	
						console.dir("LlamadaAjaxBuscaDetallesServicio")
						console.dir(data)
						console.dir(data[0].folio)
						console.dir(data[0].folio.numfolio)
						
						cadena += "";
						cadena += "<div class='row' style='padding:10px'>";
						cadena += "    <div class='col-md-3'><strong>Folio: </strong> "
								+ data[0].folio.numfolio
								+ "</div><div class='col-md-9'><strong>Descripcion: </strong>"
								+ data[0].folio.oficio.descripcion + "</div>";
						cadena += "</div>";
						cadena += "<div class='row' style='padding:10px'>";
						cadena += "    <div class='col-md-12'><strong>Oficio: </strong> "
								+ data[0].folio.oficio.oficio + "</div>";
						cadena += "</div>";
						cadena += "<div class='row' style='padding:10px'>";
						if (data[0].folio.oficio.titulo != null) {
							cadena += "    <div class='col-md-9'><strong>Título:</strong> "
									+ data[0].folio.oficio.titulo + "</div>";
						}
						cadena += "    <div class='col-md-3'><strong>Fecha entrega: </strong> "
								+ moment(data[0].folio.fechaentrega,
										"YYYY-MM-DDTHH:mm:ss.SSSSZ").format(
										"DD/MM/YYYY") + "</div>";
						cadena += "</div>";
						cadena += "<div class='row' style='padding:10px'>";
						cadena += "  <div class='col-md-9'><strong>Unidad responsable: </strong>";
						cadena += data[0].folio.oficio.urremitente.nombreLargo;
						cadena += "  </div>";
						cadena += "</div>";							
						cadena += "<div class='row' style='padding:10px'>";
						cadena += "    <div class='col-md-6'><strong>Productores(as) asignados(as):</strong>";
						$.each(data[0].folio.productoresAsignados, function(k, v) {
							cadena += "        " + v.personal.nombre + " "
									+ v.personal.paterno + " "
									+ v.personal.materno + "     ";
						});
						cadena += "</div>";
						
						cadena += "    <div class='col-md-6'><strong>Servicio(s) solicitado(s):</strong>";
						$.each(data[0].folio.oficio.servicios, function(k,v){
							cadena+=v.servicio.descripcion+", ";
						});
						cadena += "</div>";						

						cadena += "</div>";
						
						cadena += "<hr>";
						
						datosGenerales = cadena;
						cadena ="";

						var servicios = null;
						if (data[0].preagendas != null)
							servicios = data[0].preagendas;
						if (data[0].agenda != null)
							servicios = data[0].agenda;

						if (servicios!=null){
							for (a = 0; a <= servicios.length - 1; a++) {
								servicio = servicios[a];
								cadena += "<div class='row' style='padding:10px'>";
								cadena += "    <div class='col-md-4'><strong>Fase: </strong>"
										+ servicio.fase.descripcion ;
								cadena += "    </div>";
								cadena += "    <div class='col-md-4'><strong>Estado: </strong>"
									+ servicio.estado.descripcion;
	  						    cadena += "    </div>";							
								
								
								cadena += "</div>";
								
								cadena += "<div class='row' style='padding:10px'>";
								cadena += "    <div class='col-md-3'><strong>Fecha: </strong>"
										+ moment(servicio.inicio,
												"YYYY-MM-DDTHH:mm:ss.SSSSZ")
												.format("DD/MM/YYYY") + "</div>";
								cadena += "    <div class='col-md-2'>";
								if (servicio.salidas.length != 0)
									cadena += "<strong>Salida: </strong>"
											+ moment(servicio.salidas[0].salida,
													"YYYY-MM-DDTHH:mm:ss.SSSSZ")
													.format("HH:mm");
								cadena += "</div>";
								
								
								cadena += "    <div class='col-md-2'><strong>Desde: </strong>"
										+ moment(servicio.inicio,
												"YYYY-MM-DDTHH:mm:ss.SSSSZ")
												.format("HH:mm") + "</div>";
								cadena += "    <div class='col-md-2'><strong>Hasta: </strong>"
										+ moment(servicio.fin,
												"YYYY-MM-DDTHH:mm:ss.SSSSZ")
												.format("HH:mm") + "</div>";
								cadena += "</div>";
	
								if (servicio.juntas.length != 0) {
									cadena += "<div class='row' style='padding:10px'>";
									cadena += "    <div class='col-md-12'><strong>Junta</strong></div>";
									cadena += "</div>";
								}
	
								if (servicio.locaciones.length != 0) {
									cadena += "<div class='row' style='padding:10px'>";
									cadena += "    <div class='col-md-12'><strong>Locación: </strong>"
											+ servicio.locaciones[0].locacion
											+ "</div>";
									cadena += "</div>";
								}
	
								if (servicio.salas.length != 0) {
									cadena += "<div class='row' style='padding:10px'>";
									cadena += "    <div class='col-md-6'><strong>Sala: </strong>"
											+ servicio.salas[0].sala.descripcion
											+ "</div>";
									if (servicio.salas[0].usoscabina.length != 0) {
										cadena += "    <div class='col-md-6'><strong>Uso: </strong>"
												+ arrUsosCabina[servicio.salas[0].usoscabina[0].usocabina]
												+ "</div>";
									}
									cadena += "</div>";
								}
								if (servicio.vehiculos.length != 0) {
									cadena += "<div class='row' style='padding:10px'>";
									cadena += "    <div class='col-md-6'><strong>Vehículo: </strong>";
									if (servicio.vehiculos[0].vehiculo != null)
										cadena += servicio.vehiculos[0].vehiculo.placa
												+ " - "
												+ servicio.vehiculos[0].vehiculo.descripcion;
									else
										cadena += "Solicitado";
									cadena += "</div>";
	
									cadena += "</div>";
								}
	
								
								cadena += "<hr><div class='row' style='padding:5px'>";							
								
								if (servicio.tipo == "preagenda" && servicio.personal.length != 0) {
	
									cadena += "  <br>  <div class='col-md-6' style='padding-bottom:0.8em'><strong>Personal solicitado: </strong><br>";
									for (x = 0; x <= servicio.personal.length - 1; x++) {
										p = servicio.personal[x];
										cadena += p.cantidad + "  "
												+ p.tipopersonal
												+ "   <br>";
									}
									cadena += "</div>";
								}
	
								if (servicio.tipo == "agenda" && servicio.personal.length != 0) {
									cadena += "  <div class='col-md-6' style='padding-bottom:0.8em'><strong>Personal asignado: </strong><br>";
									for (x = 0; x <= servicio.personal.length - 1; x++) {
										p = servicio.personal[x];
										cadena += p.personal.nombre + " "
												+ p.personal.paterno + " "
												+ p.personal.materno + " - "
												+ p.personal.tipo.descripcion
												+ "<br>";
									}
									cadena += "</div>";
								}
	
								if (servicio.tipo == "preagenda" && servicio.locutores != 0) {
									cadena += "   <div class='col-md-6' style='padding-bottom:0.8em' ><strong>Locutores solicitados: </strong><br>";
									for (x = 0; x <= servicio.locutores.length - 1; x++) {
										l = servicio.locutores[x];
										cadena += l.personal.nombre + " "
												+ l.personal.paterno + " "
												+ l.personal.materno + " <br>";
									}
									cadena += "</div>";
	
								}
	
								if (servicio.equipos != 0 || servicio.accesorios != 0) {
									cadena += "   <div class='col-md-6' style='padding-bottom:0.8em'><strong>Equipo solicitado:</strong><br>";
									for (x = 0; x <= servicio.equipos.length - 1; x++) {
										l = servicio.equipos[x];
										cadena += l.equipo.descripcion + " "
												+ l.equipo.marca + " "
												+ l.equipo.modelo + " "
												+ l.equipo.serie + "<br>";
									}
									cadena+="<div style='padding-bottom:0.8em' ></div>";
									cadena += "    <div><strong>Accesorios solicitados:</strong></div>";
									for (x = 0; x <= servicio.accesorios.length - 1; x++) {
										l = servicio.accesorios[x];
										cadena += l.accesorio.descripcion + " "
												+ l.accesorio.modelo + " "
												+ l.accesorio.serie + "<br>";
									}								
									cadena += "   </div>";
								}
	
								if (servicio.formatos != 0) {
									cadena += "    <div class='col-md-6' style='padding-bottom:0.8em'><strong>Formatos de entrega: </strong><br>";
									for (x = 0; x <= servicio.formatos.length - 1; x++) {
										l = servicio.formatos[x];
										cadena += l.cantidad + " "
												+ l.formato.descripcion + " <br>";
									}
									cadena += "</div>";
								}
	
								cadena += "</div>";
								
	
							}
						}

						losTabs = '<div role="tabpanel" data-example-id="togglable-tabs">';
						losTabs += '<ul id="myTab1" class="nav nav-tabs bar_tabs right" role="tablist">';
						losTabs += '  <li role="presentation" class=""><a href="#tab_contactos" role="tab" id="contactos-'+data[0].folio.id+'" data-toggle="tab" aria-controls="profile" aria-expanded="false">Contactos</a>';
						losTabs += '  </li>';
						losTabs += '  <li role="presentation" class=""><a href="#tab_cancelados" role="tab" id="eventosCancelados-'+data[0].folio.id+'" data-toggle="tab" aria-controls="profile" aria-expanded="false">Cancelados</a>';						
						losTabs += '  <li role="presentation" class=""><a href="#tab_terminados" role="tab" id="eventosTerminados-'+data[0].folio.id+'" data-toggle="tab" aria-controls="profile" aria-expanded="false">Terminados</a>';
						losTabs += '  <li role="presentation" class=""><a href="#tab_autorizados" role="tab" id="eventosAutorizados-'+data[0].folio.id+'" data-toggle="tab" aria-controls="profile" aria-expanded="false">Autorizados</a>';
						losTabs += '  <li role="presentation" class=""><a href="#tab_pendientes" role="tab" id="eventosPendientes-'+data[0].folio.id+'" data-toggle="tab" aria-controls="profile" aria-expanded="false">Pendientes</a>';
						losTabs += '  </li>';
			
						losTabs += '</ul>';
						
					//	CHECAR ESTE TAB
						losTabs += '<div id="myTabContent2" class="tab-content">';
/*						losTabs += '<div role="tabpanel" class="tab-pane fade active in" id="tab_fecha" aria-labelledby="home-tab" style="max-height:30em;  overflow-y:auto; overflow-x:hidden;">';
						losTabs +=  cadena; 
						losTabs += '</div>';
*/						losTabs += '<div role="tabpanel" class="tab-pane fade" id="tab_contactos" aria-labelledby="profile-tab" style="max-height:30em;  overflow-y:auto; overflow-x:hidden;">';
						losTabs += '<p>No se encontraron contactos.</p>';
						losTabs += '</div>';
						losTabs += '<div role="tabpanel" class="tab-pane fade active in" id="tab_pendientes" aria-labelledby="profile-tab"  style="max-height:30em;  overflow-y:auto; overflow-x:hidden;">';
						losTabs += '<p>No se encontraron pendientes.</p>';
						losTabs += '</div>';
						losTabs += '<div role="tabpanel" class="tab-pane fade" id="tab_autorizados" aria-labelledby="profile-tab"  style="max-height:30em;  overflow-y:auto; overflow-x:hidden;">';
						losTabs += '<p>No se encontraron autorizados.</p>';						
						losTabs += '</div>';
						losTabs += '<div role="tabpanel" class="tab-pane fade" id="tab_terminados" aria-labelledby="profile-tab"  style="max-height:30em;  overflow-y:auto; overflow-x:hidden;">';
						losTabs += '<p>No se encontraron terminados.</p>';						
						losTabs += '</div>';
						losTabs += '<div role="tabpanel" class="tab-pane fade" id="tab_cancelados" aria-labelledby="profile-tab"  style="max-height:30em;  overflow-y:auto; overflow-x:hidden;">';
						losTabs += '<p>No se encontraron cancelados.</p>';						
						losTabs += '</div>';						
						losTabs += '</div>';		
						
						losTabs += '</div>';

						if (servicios == null){
							$("#myModalDetalleContenido").html(datosGenerales+"<br/>");	
						} else {
							$("#myModalDetalleContenido").html(datosGenerales+"<br/>"+losTabs);	
						}						
						$("#myModalDetalle").modal("show");
						$('a[id^="eventosPendientes-"]').click();

					}).fail(function() {
				alert("Error en ajax  45");
			});

}




$(document).off( 'shown.bs.tab', 'a[id^="eventosPendientes-"], a[id^="eventosAutorizados-"], a[id^="eventosTerminados-"], a[id^="eventosCancelados-"]');
$(document).on( 'shown.bs.tab', 'a[id^="eventosPendientes-"], a[id^="eventosAutorizados-"], a[id^="eventosTerminados-"], a[id^="eventosCancelados-"]', function (e) {
	   console.log(e.target) // activated tab
	   var elId = $(this).prop("id");
	   
	   var folioId = elId.substring( elId.indexOf("-")+1  );
    $.ajax({
        url: '/ajaxTodosLosDatosFolioPA',
        method: 'POST',
        data: JSON.stringify( {folioId: folioId}),
        contentType: "application/json; charset=utf-8",
        dataType: "json"
    }).success(function(data){            
        console.dir(data)
        $("#elGrid").remove();
        var cad="";
        // Preagenda
        cad+="<div id='elGrid'>";
        
        var leyendaInicial = "No se encontraron eventos."
     	cad+= leyendaInicial;

        
        data.forEach(function(auxEvento){
            if (elId.startsWith("eventosPendientes-")){
                evento = auxEvento.preagendas;
            }
            if (elId.startsWith("eventosAutorizados-")  ||  elId.startsWith("eventosTerminados-")  ||  elId.startsWith("eventosCancelados-") ){
                evento = auxEvento.agenda;
            }
     	   evento.forEach(function(pa){
     		   if (          (pa.estado.id==5 && elId.startsWith("eventosPendientes-"))    
     				   ||    (pa.estado.id==7 && elId.startsWith("eventosAutorizados-")) 
     				   ||    (pa.estado.id>7 && pa.estado.id<100  && elId.startsWith("eventosTerminados-"))
     				   ||    (pa.estado.id==100  && elId.startsWith("eventosCancelados-"))
     				   ){
     			    cad = cad.replace(leyendaInicial,""); 
                    cad+="<div class='row'>";
                    cad+="    <div class='col-sm-4'>";
                    cad+="        <strong>Fase: </strong>"+pa.fase.descripcion;
                    if (pa.salas.length>0){
                        cad+="<br><strong>"+pa.salas[0].sala.descripcion;
                        if (pa.salas[0].usoscabina.length>0){
                            cad+=" (";
                            switch(pa.salas[0].usoscabina[0].usocabina){
                            case 'I': cad+="Ingesta"; break;
                            case 'C': cad+="Calificación"; break;
                            case 'R': cad+="Revisión"; break;
                            case 'M': cad+="Musicalización"; break;
                            case 'V': cad+="Voz en off"; break;
                            }
                            cad+=")";
                        }
                        cad+="</strong>";
                    }
                    if (pa.fase.id==1 && pa.salas.length==0 && pa.juntas.length>0){
                        cad+="<br><strong>Junta de trabajo</strong>";
                    }
                    if (pa.fase.id==1 && pa.salas.length==0 && pa.juntas.length==0){
                        cad+="<br><strong>Scouting</strong>";
                    }                             
                    cad+="       <br><strong>Fecha: </strong>"+moment(pa.inicio,"YYYY-MM-DDTHH:mm:ss.SSSSZ").format("DD/MM/YYYY")+"<br>";
                   
                    
                    if (pa.salidas.length>0){
                 	    cad+="        <strong>Salida: </strong>"+moment(pa.salidas[0].salida,"YYYY-MM-DDTHH:mm:ss.SSSSZ").format("HH:mm")+"<br>";                        	   
                    }
                    cad+="        <strong>Desde: </strong>"+moment(pa.inicio,"YYYY-MM-DDTHH:mm:ss.SSSSZ").format("HH:mm");
                    cad+="        <strong>Hasta: </strong>"+moment(pa.fin,"YYYY-MM-DDTHH:mm:ss.SSSSZ").format("HH:mm");
                    cad+="<br>";
                    
                    
                    if (pa.cancelaciones.length>0){
                    	cad+="<br><strong>Motivo de cancelación:</strong><br>";
                        pa.cancelaciones.forEach(function(c){
                            cad+=c.motivo.descripcion+"<br>";
                        });   
                        cad+="<br>";
                    }                    
                    
                    
                    
                    
                    if (pa.locaciones.length>0){
                        cad+="        <strong>Locación: </strong>"+pa.locaciones[0].locacion+"<br>";                               
                    }
                    if ( pa.vehiculos.length>0){
                 	   if (pa.tipo=='preagenda')
                 		    cad+="        <strong>Vehículo solicitado</strong><br>";
                        if (pa.tipo=='agenda')
                            cad+="        <strong>Vehículo asignado:"+pa.vehiculos[0].vehiculo.placa+" - "+pa.vehiculos[0].vehiculo.descripcion+"</strong><br>";                               
                    }   
                    cad+="    </div>";
                    
                    cad+="    <div class='col-sm-4'>";
                    if (pa.tipo=="preagenda" && pa.personal.length>0){
                        cad+="<strong>Personal solicitado:</strong><br>";
                        pa.personal.forEach(function(p){
                            cad+=p.cantidad+" - "+p.rol.descripcion+"<br>";
                        });
                        cad+="<br>";
                    }
                    if (pa.tipo=="agenda" && pa.personal.length>0){
                        cad+="<strong>Personal asignado:</strong><br>";
                        pa.personal.forEach(function(p){
                            cad+=p.personal.nombre+" "+p.personal.paterno+" "+p.personal.materno+" - "+p.personal.personal.cuentas[0].descripcion+"<br>";
                        });
                        cad+="<br>";
                    }                           
                    if ( (pa.estado.id==5 && elId.startsWith("eventosPendientes-")) &&   pa.locutores.length>0){
                 	   cad+="<strong>Locutores solicitados:</strong><br>";
                 	   pa.locutores.forEach(function(loc){
                 		   cad+=loc.personal.nombre+" "+loc.personal.paterno+" "+loc.personal.materno+"<br>";
                 	   });
                 	   cad+="<br>";
                    }
                    
                    if (pa.formatos.length>0){
                        cad+="<strong>Copias solicitadas:</strong><br>";
                        pa.formatos.forEach(function(loc){
                            cad+=loc.cantidad+" "+loc.formato.descripcion+"<br>";
                        });
                        cad+="<br>";
                    }                           
                    cad+="    </div>";

                    
                    cad+="    <div class='col-sm-4'>";
                    if (pa.equipos.length>0){
                 	   if (pa.tipo=="preagenda")
                 		    cad+="<strong>Equipo solicitado:</strong><br>";
                        if (pa.tipo=="agenda")
                            cad+="<strong>Equipo asignado:</strong><br>";                        		    
                        pa.equipos.forEach(function(e){
                            cad+=e.equipo.descripcion+" "+e.equipo.marca+" "+e.equipo.modelo+" "+e.equipo.serie+"<br>";
                        });
                        cad+="<br>";
                    }
                    if (pa.accesorios.length>0){
                 	   if (pa.tipo=="preagenda")
                 		    cad+="<strong>Accesorios solicitados:</strong><br>";
                        if (pa.tipo=="agenda")
                            cad+="<strong>Accesorios asignados:</strong><br>";                        		    
                        pa.accesorios.forEach(function(e){
                            cad+=e.accesorio.descripcion+" "+e.accesorio.modelo+" "+e.accesorio.serie+"<br>";
                        });
                        cad+="<br>";
                    }
                    

                    
                    
                    cad+="    </div>";                           
                    cad+="</div>";
     		   }
     	   });
        });
        cad+="</div>";
        if (elId.startsWith("eventosPendientes-")){
     	   //$("#tab_content33").html(cad);
     		   $("#tab_pendientes").html(cad);
        }
        if (elId.startsWith("eventosAutorizados-"))
     	   $("#tab_autorizados").html(cad);
        if (elId.startsWith("eventosTerminados-")){
                $("#tab_terminados").html(cad);
        }
        if (elId.startsWith("eventosCancelados-")){
            $("#tab_cancelados").html(cad);
        }        
        console.log("previo a aplicar estilos")
        $("#elGrid").find(".row:first").css("border-top","1px solid #ccc");
        $("#elGrid").find(".row:odd").css("background-color", "#ededed");
        $("#elGrid").find(".row:odd, .row:even").css("border-bottom","1px solid #ccc");
    })    	   
	   
	});


$(document).off( 'shown.bs.tab', 'a[id^="contactos-"]');
$(document).on( 'shown.bs.tab', 'a[id^="contactos-"]', function (e) {
    console.log(e.target) // activated tab
    var elId = $(this).prop("id");           
    var folioId = elId.substring( elId.indexOf("-")+1  );
    $.ajax({
        url: '/ajaxTodosLosDatosFolioPA',
        method: 'POST',
        data: JSON.stringify( {folioId: folioId}),
        contentType: "application/json; charset=utf-8",
        dataType: "json"
    }).success(function(data){
 	   console.dir(data)
 	   $("#elGrid").remove();
        var cad="";
        cad+="<div id='elGrid'>";
        data.forEach(function(d){
            if (d.folio.oficio.contactos.length==0){
                cad+="No se encontraron contactos.";
            } else {
         	   d.folio.oficio.contactos.forEach(function(cto){
         		   cad+="<div class='row'>";
         		     cad+="<div class='col-md-4'>";
         		       cad+="<strong>"+cto.nombre+"</strong>";
         		     cad+="</div>";
                      cad+="<div class='col-md-4'>";
                    if (cto.telefonos.length>0)
                    cad+="<strong>Teléfono(s):</strong><br>";
                    cto.telefonos.forEach(function(t){
                 	  cad+=t.telefono+"<br>" 
                    });
                    cad+="    </div>";
                    cad+="    <div class='col-md-4'>";
                    if (cto.correos.length>0)
                    cad+="<strong>Correo(s):</strong><br>";
                    cto.correos.forEach(function(c){
                       cad+=c.correo+"<br>" 
                    });
                    cad+="    </div>";                		   
         		   cad+="</div>";
         	   });
            }            	   
        });

        cad+="</div>";
        console.log(cad)
        
        $("#tab_contactos").html(cad);
        $("#elGrid").find(".row:first").css("border-top","1px solid #ccc");
        $("#elGrid").find(".row:odd").css("background-color", "#ededed");
        $("#elGrid").find(".row:odd, .row:even").css("border-bottom","1px solid #ccc");               
        
    });
});     



function eliminarEvento(servicioId, tipo){
    swal({
        title: "¿Desea eliminar el evento?",                
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#d33",
        cancelButtonColor: '#3085d6',
        confirmButtonText: "Si, borrar el evento.",
        cancelButtonText: "No, cancelar borrado.",
        preConfirm: function(){
            return new Promise(function (resolve, reject) {                 
                var evento = new Object();
                evento.id = servicioId;
                evento.tipo = tipo;
                //LLamada al controlador para eliminar evento
                $borrado=null;                  
                if (rolActual=="1")
                    $borrado = LlamadaAjaxEliminaEventoAdmin(evento);    
                if (rolActual=="100")    
                    $borrado = LlamadaAjaxEliminaEvento(evento);
                  
                $.when($borrado).done(
                        function(data){
                            if (data.eliminado){
                                //swal("Eliminado", "Se eliminó correctamente el evento.", "success");
                                $("#btnModalCancelar").click();
                                $("#tablaMisServiciosDetalle tr.selected").fadeOut(800, function() {
                                    $(this).remove();
                                    $("#contadorDetalle").html(  $("#tablaMisServiciosDetalle tbody tr").length);
                                });                                     
                                resolve()
                            } else {
                                reject("No fue posible eliminar el evento.");
                            }
                        }
                );                      
            });
        }
      }).then(function () {
            swal(
                      'Eliminado!',
                      'Se eliminó correctamente el evento.',
                      'success'
                    )
      }, function (dismiss) {
            if (dismiss === 'cancel') {
              swal(
                'Cancelado',
                'Usted canceló la eliminación del evento.<br>Se conserva el evento.',
                'error'
              )
            }
      });       
}



function irAFechaEvento(fecha, eventoId, tipo){
    var extra1 = document.createElement("input");
    extra1.setAttribute("type", "hidden");
    extra1.setAttribute("name", "fecha");
    extra1.setAttribute("value", fecha);
    
    var extra2 = document.createElement("input");
    extra2.setAttribute("type", "hidden");
    extra2.setAttribute("name", "eventoId");
    extra2.setAttribute("value", eventoId);
    
    var extra3 = document.createElement("input");
    extra3.setAttribute("type", "hidden");
    extra3.setAttribute("name", "tipo");
    extra3.setAttribute("value", tipo);           

    $("#frmAux").append(extra1);
    $("#frmAux").append(extra2);
    $("#frmAux").append(extra3);

	 $("#frmAux").attr("action", "/productorServicios");
	 $("#frmAux").attr("method","POST");
	 $("#frmAux").submit();
}





function AbrirMostrarDatos(eventoId, tipo){    	 
	alert("aca 4775");
	 var event = new Object();
	 var $d = LlamadaAjaxDatos(eventoId, tipo);
	 event.tipo=tipo;
	 event.id = eventoId; 
	 
	 $.when($d).done(function(dat){
		 console.dir(dat)
		 event.folioNum = dat.folioproductorasignado.folio.numfolio;
		 event.start = moment(dat.inicio, "YYYY-MM-DDTHH:mm:ss.SSSSZ");
		 event.end = moment(dat.fin, "YYYY-MM-DDTHH:mm:ss.SSSSZ");
		 event.faseId = dat.fase.id;
		 event.descripcion = dat.folioproductorasignado.folio.oficio.descripcion;
		 event.fpaId = dat.folioproductorasignado.id;
		 if (dat.salas.length>0)
		 	event.salaId = dat.salas[0].sala.id;
		 
		 if (dat.fase.id==1){
			 event.resourceId = "a";
			 if (dat.salas.length>0){
				 event.resourceId = "c";
			 }
		 }
		 if (dat.fase.id==2){
			 event.resourceId = "b";
		 }		
		 // Edición
		 if (dat.fase.id==3){
			 dat.salas.forEach(function (sala) {
				 // Salas Edición
				 switch (sala.sala.id){
				 	case 3:  event.resourceId = "d"; break;
				 	case 2:  event.resourceId = "e"; break;
				 	case 7:  event.resourceId = "f"; break;
				 }
			 });
		 }			 
		 
		 // Posproducción
		 if (dat.fase.id==4){
			 dat.salas.forEach(function (sala) {
				 // Sala de Ingesta, Calificación y Revisión
				 if (sala.sala.id == 1){
					 event.resourceId = "c";
					 sala.usoscabina.forEach(function(uso){
						 if (uso.usocabina=="R")
							 event.resourceId = "c";
					 });
				 }
				 switch (sala.sala.id){
				 	case 3:  event.resourceId = "d"; break;
				 	case 2:  event.resourceId = "e"; break;
				 	case 7:  event.resourceId = "f"; break;
				 	case 4:  event.resourceId = "g"; break;
				 	
				 }					 
			 });
			 
		 }
		 if (dat.fase.id==5){
			 event.resourceId = "h";
		 }
		 if (dat.fase.id==6){
			 event.resourceId = "c";
		 }			 
		 alert("aca 123");
		 mostrarDatosEvento(event);	 
		 
	 });
	 //event.folioNum = 
	 
		
	 
}