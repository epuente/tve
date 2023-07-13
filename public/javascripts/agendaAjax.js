
//Retorna objeto JSON Preagendaservicio o agenda
function LlamadaAjaxDatos(id, tipo){
	var d = $.Deferred();
	var este = new Object();
	este.id = id;
	este.tipo = tipo;
	$.ajax({
		url: '/ajaxDatos',
		method: 'POST',
		data: JSON.stringify( {este}),
		contentType: "application/json; charset=utf-8",	    
	    dataType: "json"
	}).success(function(d1){
		console.dir(d1)
		d.resolve(d1);
	}).error(function(xhr, status, error) {
		  d.reject;
		  alert("error rn001 ajax -  "+ xhr.responseText );
		});	
	return d.promise();	
}


function LlamadaAjaxLocutoresDisponibles(desde, hasta){ 
	var d = $.Deferred();
    var este = new Object();
    este.inicio = desde;
    este.fin = hasta;
	$('select[name="selectLocutoresDisponibles"]').find('option').remove();	
	$('select[name="selectLocutoresDisponibles"]').bootstrapDualListbox('refresh');	
	// Después se seleccionan los ocupados
	$.ajax({
		url: '/ajaxLocutoresDisponibles',
		method: "POST",
		data: JSON.stringify( {este}),
		contentType: "application/json; charset=utf-8",
		dataType: "json"
	})
	.success(function(data){
		d.resolve(data);
		$.each(data, function(i,e){
			//$("select[name='selectLocutoresDisponibles'] option[value='"+e.id+"']").prop("selected", true);
			$('select[name="selectLocutoresDisponibles"]').append("<option value='"+e.id+"'>"+e.nombre+" "+e.paterno+" "+e.materno+"</option>");					
		});
		$('select[name="selectLocutoresDisponibles"]').bootstrapDualListbox('refresh');
		
	}).error(function(xhr, status, error) {
		d.reject;
	    alert("error rn ajax 002 -  "+ xhr.responseText );
	}); 	
	return d.promise();	
}
		


function LlamadaAjaxRDisenioDisponibles(desde, hasta){ 
	var d = $.Deferred();
    var este = new Object();
    este.inicio = desde;
    este.fin = hasta;
	$('select[name="selectRDisenioDisponibles"]').find('option').remove();	
	$('select[name="selectRDisenioDisponibles"]').bootstrapDualListbox('refresh');	
	// Después se seleccionan los ocupados
	$.ajax({
		url: '/ajaxRDisenioDisponibles',
		method: "POST",
		data: JSON.stringify( {este}),
		contentType: "application/json; charset=utf-8",
		dataType: "json"
	})
	.success(function(data){
		d.resolve(data);
		$.each(data, function(i,e){
			//$("select[name='selectLocutoresDisponibles'] option[value='"+e.id+"']").prop("selected", true);
			$('select[name="selectRDisenioDisponibles"]').append("<option value='"+e.id+"'>"+e.nombre+" "+e.paterno+" "+e.materno+"</option>");					
		});
		$('select[name="selectRDisenioDisponibles"]').bootstrapDualListbox('refresh');
		
	}).error(function(xhr, status, error) {
		d.reject;
	    alert("error rn ajax -  "+ xhr.responseText );
	}); 	
	return d.promise();	
}



function LlamadaAjaxEquipoAccesoriosDisponibles(desde, hasta){
	var d = $.Deferred();
    var este = new Object();
    este.inicio = desde;
    este.fin = hasta;	
    $('#panelesFase2 select[name="selectEquipoAccesoriosDisponibles"]').find('option').remove();
    $('#panelesFase2 select[name="selectEquipoAccesoriosDisponibles"]').bootstrapDualListbox('refresh');
    var $e = auxEquipo(este);				
	var $a = auxAccesorios(este);
	$.when($e, $a).done(function(data){
	    ordenarOptions("selectEquipoAccesoriosDisponibles");
	    $('#panelesFase2 select[name="selectEquipoAccesoriosDisponibles"]').bootstrapDualListbox('refresh');
	    d.resolve(data);
	});
	return d.promise();
}
		

function auxEquipo(este){	
			var ee = $.Deferred();
			$.ajax({
				url: '/ajaxEquiposDisponibles',
				method: "POST",
				data: JSON.stringify( {este}),
				contentType: "application/json; charset=utf-8",
				dataType: "json"			
			}).success(function(data){
				$.each(data, function(i,e){
					$('#panelesFase2 select[name="selectEquipoAccesoriosDisponibles"]').append("<option value='E"+e.id+"'>"+e.descripcion+" "+e.marca+" "+e.modelo+"  NS:"+e.serie+"</option>");
				});
				ee.resolve(data);
			}).error(function(xhr, status, error) {
				ee.reject;
			    alert("error rn ajax  003 -  "+ xhr.responseText );
			});
			return ee.promise();	
}


function auxAccesorios(este){
	var aa = $.Deferred();
	$.ajax({	
		url: '/ajaxAccesoriosDisponibles',
		method: "POST",
		data: JSON.stringify( {este}),
		contentType: "application/json; charset=utf-8",
		dataType: "json"			
	}).success(function(data){			

//		$('#panelesFase2 select[name="selectEquipoAccesoriosDisponibles"]').append("<option disabled>Accesorios</option>");
		$.each(data, function(i,e){
			$('#panelesFase2 select[name="selectEquipoAccesoriosDisponibles"]').append("<option value='A"+e.id+"'>"+e.descripcion+" "+e.modelo+"  NS:"+e.serie+"</option>");
		});
		aa.resolve(data);
	}).error(function(xhr, status, error) {
		aa.reject;				
				  alert("error rn ajax 004 -  "+ xhr.responseText );
	});   
	return aa.promise();
}

function ordenarOptions(idDelSelect){
    var options = $("#"+idDelSelect+" option");
    var arr = options.map(function(_, o) {
        return {
            t: $(o).text(),
            v: o.value
        };
    }).get();
    arr.sort(function(o1, o2) {
        return o1.t > o2.t ? 1 : o1.t < o2.t ? -1 : 0;
    });
    options.each(function(i, o) {
        //console.log(i);
        o.value = arr[i].v;
        $(o).text(arr[i].t);
    });
}


function LlamadaAjaxVehiculosDisponibles(desde, hasta){ 
	var d = $.Deferred();
    var este = new Object();
    este.inicio = desde;
    este.fin = hasta;
	$('select[name="selectVehiculosDisponibles"]').find('option').remove();	
	$('select[name="selectVehiculosDisponibles"]').bootstrapDualListbox('refresh');	
	// Después se seleccionan los ocupados
	$.ajax({
		url: '/ajaxVehiculosDisponibles',
		method: "POST",
		data: JSON.stringify( {este}),
		contentType: "application/json; charset=utf-8",
		dataType: "json"
	})
	.success(function(data){
		d.resolve(data);
		$.each(data, function(i,e){
			$('select[name="selectVehiculosDisponibles"]').append("<option value='"+e.id+"'>"+e.placa+"  -  "+e.descripcion+"</option>");
		});
		$('select[name="selectVehiculosDisponibles"]').bootstrapDualListbox('refresh');
		
	}).error(function(xhr, status, error) {
			d.reject;
				  alert("error rn ajax 005 -  "+ xhr.responseText );
	}); 	
	return d.promise();
}

function LlamadaAjaxPersonalDisponibles(desde, hasta, fase){ 
	var d = $.Deferred();
    var este = new Object();
    este.inicio = desde;
    este.fin = hasta;
    este.fase = fase;
	$('select[name="selectPersonalDisponibles"]').find('option').remove();	
	$('select[name="selectPersonalDisponibles"]').bootstrapDualListbox('refresh');	
	// Después se seleccionan los ocupados
	$.ajax({
		url: '/ajaxPersonalDisponibles',
		method: "POST",
		data: JSON.stringify( {este}),
		contentType: "application/json; charset=utf-8",
		dataType: "json"
	})
	.success(function(data){
		d.resolve(data);
		console.dir(data)
		$.each(data, function(i,e){
			$('select[name="selectPersonalDisponibles"]').append("<option value='"+e.id+"'>"+e.rolDescripcion+" - "+e.nombre+"</option>");
		});
		$('select[name="selectPersonalDisponibles"]').bootstrapDualListbox('refresh');
		
	}).error(function(xhr, status, error) {
		d.reject;
				  alert("error rn ajax 006 -  "+ xhr.responseText );
	}); 	
	return d.promise();
}

function LlamadaAjaxGrabaEvento(preagenda, eCalendario){
    console.log("%cpreagenda", "background-color:blue; color: white;")
    console.dir(preagenda)
	var d = $.Deferred();	
    var eventos = $('#calendario').fullCalendar('clientEvents', function(evt) {
        return (evt.id == eCalendario.id );
        });                     
    var e = eventos[0];  		
	$.ajax({
		url: '/ajaxGrabaEvento',
		method: 'POST',
	    data: JSON.stringify( {preagenda}),
	    contentType: "application/json; charset=utf-8",
	    dataType: "json"
	}).success(function(data){
		console.log("retornado por ajaxGrabaEvento")
		console.dir(data)
		d.resolve(data);
	}).error(function(xhr, status, error) {
		  d.reject;
		  alert("error rn ajax 007 -  "+ xhr.responseText );
			$.notify({
				title: "<strong>Error:</strong> ",
				message: "Ocurrió un error en ajax."
			},{
				type: 'danger'
			});							
			$('#calendario').fullCalendar('removeEvent', e);						  
		});	
	return d.promise();
}

//Administrador u operador de sala borran evento
function LlamadaAjaxEliminaEvento(evento){
	//alert("desde javascripts.agendaAjax.LlamadaAjaxEliminaEvento");
	var d = $.Deferred();
	$.ajax({
		url: '/ajaxEliminaEventoAdminOpera',
		method: 'POST',
	    data: JSON.stringify( {evento}),
	    contentType: "application/json; charset=utf-8",
	    dataType: "json"
	}).success(function(data){
		d.resolve(data);
		if (data.eliminado){
			var idAux = evento.id;
			var elEvento = $('#calendario').fullCalendar('clientEvents', function(evt) {
				return (evt.id == evento.id );			  	
		  	});
			console.dir(elEvento)
			$('#calendario').fullCalendar('removeEvents',  elEvento[0]._id   );
			$('#calendario').fullCalendar('removeEvents',  elEvento[0].id   );
			$("#myModalConfirmar").modal('hide'); 
			$('#myModal2').modal('hide');
			
			$.notify({
				title: "<strong>Correcto:</strong> ",
				message: "Se ha eliminado el evento."					
			});			
		} else {
			$.notify({
				title: "<strong>Error:</strong> ",
				message: "Se ha producido un error al intentar eliminar el evento."					
			},
			{
					type: 'danger'
			});					
		}
	}).error(function(xhr, status, error) {
		  d.reject;
		  alert("error rn ajax  008 -  "+ xhr.responseText );
		});		
	return d.promise();	
}


// Productor borra evento
function LlamadaAjaxEliminaEventoProd(evento){
	var d = $.Deferred();
	$.ajax({
		url: '/ajaxEliminaEvento',	
		method: 'POST',
	    data: JSON.stringify( {evento}),
	    contentType: "application/json; charset=utf-8",
	    dataType: "json"
	}).success(function(data){
		d.resolve(data);
		if (data.eliminado){
			var idAux = evento.id;
			var elEvento = $('#calendario').fullCalendar('clientEvents', function(evt) {
				return (evt.id == evento.id );			  	
		  	});
			console.dir(elEvento)
			$('#calendario').fullCalendar('removeEvents',  elEvento[0]._id   );
			$('#calendario').fullCalendar('removeEvents',  elEvento[0].id   );
			$("#myModalConfirmar").modal('hide'); 
			$('#myModal2').modal('hide');
			
			$.notify({
				title: "<strong>Correcto:</strong> ",
				message: "Se ha eliminado el evento."					
			});			
		} else {
			$.notify({
				title: "<strong>Error:</strong> ",
				message: "Se ha producido un error al intentar eliminar el evento."					
			},
			{
					type: 'danger'
			});					
		}
	}).error(function(xhr, status, error) {
		  d.reject;
		  alert("error rn ajax  008a -  "+ xhr.responseText );
		});		
	return d.promise();	
}




//Función ajax que actualiza datos del evento 
// Cuando se hace update desde el modal dialog, tipoOperacion = "update" 
// Cuando se hace resize del evento (fullcalendar), tipoOperacion = "resize"
// Cuando se mueve un evento en la agenda (fullcalendar), tipoOperacion = "move"
function LlamadaAjaxActualizaEvento(evento, tipoOperacion){
	console.log("recibido en LlamadaAjaxActualizaEvento")
	evento.tipoOperacion = tipoOperacion;
	console.dir(evento)
	$('.popover').popover('hide');
	$('[data-toggle="popover"]').popover();
	var d = $.Deferred();
	$.ajax({
		url: '/ajaxActualizaEvento',
		method: 'POST',
	    data: JSON.stringify( {evento}),
	    contentType: "application/json; charset=utf-8",
	    dataType: "json"
	}).success(function(data){
		d.resolve(data);
	}).error(function(xhr, status, error) {
		d.reject;
		  alert("error rn ajax  009 -  "+ xhr.responseText );
		});
	return 	d.promise();
}


/*
function LlamadaAjaxActualizaEventoAdmin(evento){
	var d = $.Deferred();
	$.ajax({
		url: '/ajaxActualizaEventoAdmin',
		method: 'POST',
	    data: JSON.stringify( {evento}),
	    contentType: "application/json; charset=utf-8",
	    dataType: "json"
	}).success(function(data){
		d.resolve(data);
	}).error(function(xhr, status, error) {
		d.reject;
		  alert("error rn ajax  010 -  "+ xhr.responseText );
		});		
	return d.promise();
}
*/


function LlamadaAjaxCantidadPerfiles(xyz){
	var d = $.Deferred();
	cc = JSON.stringify(xyz);
	$.ajax({
		url: '/ajaxValidaCantidadPerfiles',
		method: 'POST',
	    data: JSON.stringify(xyz),
	    contentType: "application/json; charset=utf-8",
	    dataType: "json"
	}).success(function(data){
		console.dir(data)
		d.resolve(data);
	});
	return d.promise();
}




function LlamadaAjaxAutorizaEvento(xyz){
	var d = $.Deferred();
	$.ajax({
		url: '/ajaxAutorizaEvento',
		method: 'POST',
	    data: JSON.stringify( {xyz}),
	    contentType: "application/json; charset=utf-8",
	    dataType: "json"
	}).success(function(data){
		d.resolve(data);
	}).error(function(xhr, status, error) {
		  d.reject;
		  alert("error rn ajax 011 -  "+ xhr.responseText );
			$.notify({
				title: "<strong>Error:</strong> ",
				message: "Ocurrió un error en ajax."
			},{
				type: 'danger'
			});							
			//$('#calendario').fullCalendar('removeEvent', e);						  
		});	
	return d.promise();	
}






// Método del administrador u operador de sala para eliminar/liberar un evento
function LlamadaAjaxLiberaEvento(evento){	
	console.log("desde agendaAjax.js")
	var d = $.Deferred();
	$.ajax({
		url: '/ajaxEliminaEventoAdminOpera',
		method: 'POST',
	    data: JSON.stringify( {evento}),
	    contentType: "application/json; charset=utf-8",
	    dataType: "json"
	}).success(function(data){
		d.resolve(data);
		console.dir(data)
		if (data.eliminado){
			console.log($("#myModal2").is(":visible"))
			 if (  $('#calendario').children().length > 0 ){
				var idAux = evento.id;
				var elEvento = $('#calendario').fullCalendar('clientEvents', function(evt) {
					return (evt.id == evento.id );			  	
			  	});
				console.dir(elEvento)
				$('#calendario').fullCalendar('removeEvents',  elEvento[0]._id   );
				$('#calendario').fullCalendar('removeEvents',  elEvento[0].id   );
				$('#btnModalCancelar').click();
			}
			$.notify({
				title: "<strong>Correcto:</strong> ",
				message: "Se ha liberado el evento (admin)."					
			});			
		} else {
			$.notify({
				title: "<strong>Error:</strong> ",
				message: "Se ha producido un error al intentar liberar el evento."					
			},
			{
					type: 'danger'
			});					
		}
	}).error(function(xhr, status, error) {
		  d.reject;
		  alert("error rn ajax 012 -  "+ xhr.responseText );
		});		
	return d.promise();		
}


// Método del administrador para eliminar un evento
function LlamadaAjaxEliminaEventoAdmin(evento){	
	var d = $.Deferred();
	$.ajax({
		url: '/ajaxEliminaEventoAdmin',
		method: 'POST',
	    data: JSON.stringify( {evento}),
	    contentType: "application/json; charset=utf-8",
	    dataType: "json"
	}).success(function(data){
		d.resolve(data);
		console.dir(data)
		if (data.eliminado){
			console.log($("#myModal2").is(":visible"))
			 if (  $('#calendario').children().length > 0 ){
				var idAux = evento.id;
				var elEvento = $('#calendario').fullCalendar('clientEvents', function(evt) {
					return (evt.id == evento.id );			  	
			  	});
				console.dir(elEvento)
				$('#calendario').fullCalendar('removeEvents',  elEvento[0]._id   );
				$('#calendario').fullCalendar('removeEvents',  elEvento[0].id   );
				$('#btnModalCancelar').click();
			}
			$.notify({
				title: "<strong>Correcto:</strong> ",
				message: "Se ha eliminado el evento (admin)."					
			});			
		} else {
			$.notify({
				title: "<strong>Error:</strong> ",
				message: "Se ha producido un error al intentar eliminar el evento."					
			},
			{
					type: 'danger'
			});					
		}
	}).error(function(xhr, status, error) {
		  d.reject;
		  alert("error rn ajax 013 -  "+ xhr.responseText );
		});		
	return d.promise();	
}


// Verifica que existe el folio antes de que se agrege al calendario
function LlamadaAjaxBuscaFolio(numFolio){
	var d = $.Deferred();
	$.ajax({
		url: '/ajaxBuscaFolio',
		method: 'POST',
	    data: JSON.stringify( { "folio": numFolio}),
	    contentType: "application/json; charset=utf-8",
	    dataType: "json"
	}).success(function(data){
		d.resolve(data);
	}).error(function(xhr, status, error) {
		  d.reject;
		  alert("error en ajax (LlamadaAjaxBuscaFolio) -  "+ xhr.responseText );		  
			$.notify({
				title: "<strong>Error:</strong> ",
				message: "Ocurrió un error en ajax."
			},{
				type: 'danger'
			});							
			$('#calendario').fullCalendar('removeEvent', e);						  
		});	
	return d.promise();	
}



//Validar que el evento no entre en colisión, que exista operador, etc.
function LlamadaAjaxValidaEvento(evento){
	var d = $.Deferred();
	$.ajax({
		url: '/ajaxValidaEvento',
		method: 'POST',
	    data: JSON.stringify( { "eventoId": evento.id, 
	    						"tipo": evento.tipo, 
	    						"inicio": evento.inicio, 
	    						"fin": evento.fin,  
	    						"salasSalaId":evento.salasSalaId,
	    						"folio": evento.folio,
	    						"faseId": evento.faseId
	    					  }),
	    
	    contentType: "application/json; charset=utf-8",
	    dataType: "json"
	}).success(function(data){
		d.resolve(data);
	}).error(function(xhr, status, error) {
		  d.reject;
		  alert("error en ajax (LlamadaAjaxValidaEvento) -  "+ xhr.responseText );		  
			$.notify({
				title: "<strong>Error:</strong> ",
				message: "Ocurrió un error en ajax."
			},{
				type: 'danger'
			});							
		});	
	return d.promise();	

}

/*REcibe el id de persona y regresa un objeto TipoPersonal*/
function LlamadaAjaxBuscaPersona(idPersona){
	var d = $.Deferred();
	var este = new Object();
	este.id = idPersona;
	console.log("recinbiso en LlamadaAjaxBuscaPersona "+ este.id)
	$.ajax({
		url: '/ajaxBuscaPersona',
		method: 'POST',
		data: JSON.stringify(este),
		contentType: "application/json; charset=utf-8",	    
	    dataType: "json"
	}).success(function(data){
		d.resolve(data);
	}).error(function(xhr, status, error) {
		  d.reject;
		  alert("error rn ajax 014 -  "+ xhr.responseText );
		});	
	return d.promise();	
}


/*Recibe el id de tipopersonal y regresa un objeto TipoPersonal*/
function LlamadaAjaxBuscaTipoPersonal(idTPersonal){
	var d = $.Deferred();
	var este = new Object();
	este.id = idTPersonal;
	console.log("recinbiso en LlamadaAjaxBuscaTipoPersonal "+ este.id)	
	$.ajax({
		url: '/ajaxBuscaTPersonal',
		method: 'POST',
		data: JSON.stringify(este),
		contentType: "application/json; charset=utf-8",	    
	    dataType: "json"
	}).success(function(data){
		console.log(data)
		d.resolve(data);
	}).error(function(xhr, status, error) {
		  d.reject;
		  alert("error rn ajax 015 -  "+ xhr.responseText );
		});	
	return d.promise();	
}





function np(idPerfil){
	var de = $.Deferred();
	var $t = LlamadaAjaxBuscaTipoPersonal(idPerfil);
	$.when($t).done(function(data){
		console.dir(data.descripcion)
		de.resolve(data.descripcion);
	});	
	return de.promise();	
}


function LlamadaAjaxCancelaEvento(eventoId, motivoId){
	var d = $.Deferred();
	var este = new Object();
	este.eventoId = eventoId;
	este.motivoId = motivoId;
	console.log("recibido en LlamadaAjaxCancelaEvento ")
	console.log(este.eventoId)
	console.log(este.motivoId)
	$.ajax({
		url: '/ajaxCancelaEvento',
		method: 'POST',
		data: JSON.stringify(este),
		contentType: "application/json; charset=utf-8",	    
	    dataType: "json"
	}).success(function(data){
		console.log(data)
		d.resolve(data);
	}).error(function(xhr, status, error) {
		  d.reject;
		  alert("error rn ajax 016 -  "+ xhr.responseText );
		});	
	return d.promise();		
}  


function LlamadaGuardaDatosIngesta(agendaServicioIngesta, eCalendario){
	var d = $.Deferred();	
    var eventos = $('#calendario').fullCalendar('clientEvents', function(evt) {
        return (evt.id == eCalendario.id );
        });                     
    var e = eventos[0];  		
	$.ajax({
		url: '/guardaDatosIngesta',
		method: 'POST',
	    data: JSON.stringify( {agendaServicioIngesta}),
	    contentType: "application/json; charset=utf-8",
	    dataType: "json"
	}).success(function(data){
		console.log("retornado por LlamadaGuardaDatosIngesta")
		console.dir(data)
		d.resolve(data);
	}).error(function(xhr, status, error) {
		  d.reject;
		  alert("error rn ajax 017 -  "+ xhr.responseText );
			$.notify({
				title: "<strong>Error:</strong> ",
				message: "Ocurrió un error en ajax."
			},{
				type: 'danger'
			});							
			//$('#calendario').fullCalendar('removeEvent', e);						  
		});	
	return d.promise();
}


