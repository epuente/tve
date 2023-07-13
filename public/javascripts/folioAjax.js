function LlamadaAjaxBuscaFolio(numFolio){
	var d = $.Deferred();
	$.ajax({
		url: '/folioBuscarFolio/'+encodeURIComponent(numFolio),
		method: 'GET',
	    data: JSON.stringify( { "folio": numFolio}),
	    contentType: "application/json; charset=utf-8",
	    dataType: "json"
	}).success(function(data){
		d.resolve(data);
	}).error(function(xhr, status, error) {		  
		  alert("error en ajax (LlamadaAjaxBuscaFolio) -  "+ xhr.responseText );		  
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



function existeFolio(numFolio){
	LlamadaAjaxBuscaFolio(numFolio);
}