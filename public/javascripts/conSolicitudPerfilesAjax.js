

// Autoriza el servicio 
function LlamadaAjaxConSolicitudPerfiles(id, estado){
	var d = $.Deferred();
	$.ajax({
		url: '/ajaxSolicitaPerfiles',
		method: 'POST',
	    data: JSON.stringify( { "fpaId": id, "estadoId":estado}),
	    contentType: "application/json; charset=utf-8",
	    dataType: "json"
	}).success(function(data){
		if (data.conSolicitud==false){
			//MARCA* 
			$("#autorizacionRapida"+id).css("visibility", "visible");
		}
		d.resolve(data);
	}).error(function(xhr, status, error) {

		  alert("error en ajax (LlamadaAjaxBuscaServiciosFolio 006) -  "+ xhr.responseText );
		  alert("error en ajax (LlamadaAjaxBuscaServiciosFolio 007) -  "+ error );
		  alert("error en ajax (LlamadaAjaxBuscaServiciosFolio 008) -  "+ xhr);
		  
			$.notify({
				title: "<strong>Error:</strong> ",
				message: "Ocurrió un error en ajax."
			},{
				type: 'danger'
			});		
			d.reject;					
		});	
	return d.promise();	
}




//Busca un servicio de un id de (preagenda o agenda) o de folioproductorasignado
function LlamadaAjaxBuscaDetallesServicio(id, tipo, fpaId){
console.log(id);	
	var d = $.Deferred();
	$.ajax({
		url: '/ajaxBuscaDetallesServicio',
		method: 'POST',
	    data: JSON.stringify( { "servicioId": id, "tipo":tipo, "fpaId":fpaId}),
	    contentType: "application/json; charset=utf-8",
	    dataType: "json"
	}).success(function(data){
		d.resolve(data);
	}).error(function(xhr, status, error) {		  
		  alert("error en ajax (LlamadaAjaxBuscaServiciosFolio 009) -  "+ xhr.responseText );
			$.notify({
				title: "<strong>Error:</strong> ",
				message: "Ocurrió un error en ajax."
			},{
				type: 'danger'
			});	
			d.reject;
		});	
	return d.promise();	
}
