function LlamadaSabanaAjax(anio, nSemana){
	var d = $.Deferred();
	$.ajax({
		url: '/ajaxSabana',
		method: 'POST',
	    data: JSON.stringify( { "anio": anio, "nSemana": nSemana}),
	    contentType: "application/json; charset=utf-8",
	    dataType: "json"
	}).success(function(data){
		d.resolve(data);
	}).error(function(xhr, status, error) {
		  alert("error en ajax (LlamadaSabanaAjax) -  "+ xhr.responseText );
		  d.reject;
		});	
	return d.promise();	
}