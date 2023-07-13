function LlamadaAjaxBuscaOficioPorId(id){
	var d = $.Deferred();
	$.ajax({
		url: '/folioBuscarOficioPorId/'+encodeURIComponent(id),
		method: 'GET',
	    data: JSON.stringify( { "oficio": id}),
	    contentType: "application/json; charset=utf-8",
	    dataType: "json"
	}).success(function(data){
		d.resolve(data);
	}).error(function(xhr, status, error) {		  
		  alert("error en ajax (LlamadaAjaxBuscaOficioPorId) -  "+ xhr.responseText );		  
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


function LlamadaAjaxBuscaOficio(numOficio){
	var d = $.Deferred();
	$.ajax({
		url: '/folioBuscarOficio/'+encodeURIComponent(numOficio),
		method: 'GET',
	    data: JSON.stringify( { "oficio": numOficio}),
	    contentType: "application/json; charset=utf-8",
	    dataType: "json"
	}).success(function(data){
		d.resolve(data);
	}).error(function(xhr, status, error) {		  
		  alert("error en ajax (LlamadaAjaxBuscaOficio) -  "+ xhr.responseText );		  
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



function existeOficio(numOficio){
	console.log("Desde prueba");
	alert("Desde prueba");
	LlamadaAjaxBuscaOficio(numOficio);
}