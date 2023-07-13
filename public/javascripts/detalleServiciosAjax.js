

// Busca los servicios relacionados a un id proviniente de folioproductorasignado 
function LlamadaAjaxBuscaServiciosFolio(id, estado){
	var oficioId=null;
	if (estado==2){		
		oficioId=id;
		id=null;
	}
	var d = $.Deferred();
	$.ajax({
		url: '/ajaxBuscaServiciosFolio',
		method: 'POST',
	    data: JSON.stringify( { "folioId": id, "estadoId":estado, "oficioId": oficioId}),
	    contentType: "application/json; charset=utf-8",
	    dataType: "json"
	}).success(function(data){		
		d.resolve(data);
	}).error(function(xhr, status, error) {
		  d.reject;
		  alert("error en ajax (LlamadaAjaxBuscaServiciosFolio 001) -  "+ error );
			$.notify({
				title: "<strong>Error:</strong> ",
				message: "Ocurrió un error en ajax."
			},{
				type: 'danger'
			});							
		});	
	return d.promise();	
}




//Busca un servicio de un id de (preagenda o agenda) o de folioproductorasignado
function LlamadaAjaxBuscaDetallesServicio(id, tipo, fpaId){
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
		  
		  alert("error en ajax (LlamadaAjaxBuscaServiciosFolio 002) -  "+ xhr.responseText );
		 // alert("error en ajax (LlamadaAjaxBuscaServiciosFolio) -  "+ error );
		//  alert("error en ajax (LlamadaAjaxBuscaServiciosFolio) -  "+ xhr);
		  
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
