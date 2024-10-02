
// Ejecuta una llamada ajax deferred
// Parametros: 
//     ruta: proviene del archivo config/routes
//     metodo : es post o get
//     parametrosJson son los parametros en JSON por ejemplo:JSON.stringify({"fpaId": id, "estadoId":estado.id})
// Regresa info en formato json 
function LlamadaAjax(ur,metodo, parametrosJson){
	var d = $.Deferred();
	$.ajax({
	  url: ur,
	  method: metodo,
	  type: metodo,
	  data: parametrosJson,
	  // Tipo enviado al server
	  contentType: "application/json; charset=utf-8",
	  // Tipo que se recibe desde el server
	  dataType: "json"
	}).done(function(data) {
		d.resolve(data);
	}).fail(function(e){
		d.reject;
	});	
	return d.promise();
}



// Ejecuta una llamada ajax deferred
// Parametros: 
//     ruta proviene del archivo config/routes
//     metodo : es post o get
//     forma:  es la forma serializada 
// Regresa lo que el controlador indique 
function LlamadaAjaxSerialize(ur,metodo, forma){
	var d = $.Deferred();
	$.ajax({
	  url: ur,
	  method: metodo,
	  type: metodo,
	  data: forma,
	  // Tipo que se recibe desde el server
	  dataType: "json"
	}).done(function(data) {
		d.resolve(data);
	}).fail(function(e){
		d.reject;
	});	
	return d.promise();
}


