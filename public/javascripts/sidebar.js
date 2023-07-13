
function cambiarRol(rolId){
	console.log("intentando rol "+rolId+".....")
	var $cr = LlamadaAjax("/cambiarRol","POST",JSON.stringify({rolId:rolId}));
	$.when($cr).done(function(data){
		console.log(data)
		if (data.estado=="ok"){
			console.log("va.............")
			$("#frmAux").attr("action", "/redirecciona");
			$("#frmAux").attr("method", "GET");
			$("#frmAux").submit();			
		}
	});						

}