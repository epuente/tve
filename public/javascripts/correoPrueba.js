$(document).off("click","#txtPruebaCorreo");
$(document).on("click","#txtPruebaCorreo", function(event){
	event.preventDefault();
	$("#divPruebaCorreo").toggle("slow", "swing");
	if ( $("#divPruebaCorreo").is(":visible")){
		$("#txtPruebaCorreo a").text("Ocultar panel de prueba de correo");
	} else {
		$("#txtPruebaCorreo a").text("Prueba de correo");
	}
});


$(document).off("click","#btnPruebaCorreo");
$(document).on("click","#btnPruebaCorreo", function(event){
    event.preventDefault();
	console.log("desde aca")
	
	$("#forma").validator();
	var conErrores = true;
	
	if ( $("#hostname").val()=="" || $("#puerto").val()=="" || $("#cuenta").val()=="" || $("#contrasenia").val()==""
	|| $("#txtPara").val()=="" ){
		console.log("hay error")
		$("#msgPruebaCorreo").text("Se deben llenar todos los campos");
	} else {
		conErrores=false;
		//var jsO = {"host": "\""+$("#hostname").val()+"\",   ", };
		var x={};
		x.host = $("#hostname").val();
		x.puerto = $("#puerto").val();
		x.cuenta = $("#cuenta").val();
		x.password = $("#contrasenia").val();
		x.para = $("#txtPara").val();
		
		console.dir (JSON.stringify(x));
		$("#msgPruebaCorreo").text("Preparando el envío...");
		var $envio=LlamadaAjax("/envioCorreoPrueba","POST", JSON.stringify(x));
		$.when($envio).done(function(data){
			console.dir(data);
			if (data.enviado){
				$("#msgPruebaCorreo").html("<div>Se envió con éxito el correo.</div><div>Lo cual indica que la configuración de hostname, puerto, cuenta y clave son los correctos.</div>");
			} else {
				$("#msgPruebaCorreo").text("Ocurrió un error, no se pudo enviar el correo.");
			}
		});
	}
	
});

