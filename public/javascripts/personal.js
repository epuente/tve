$("#btnPassword").click(function(){
	if ( $("#cuentas_password").attr("type")=="password"  ){
		$("#cuentas_password").attr("type","input")
	} else {
		$("#cuentas_password").attr("type","password")
	}
});


$("#btnPersonalSubmit, #btnPersonalActualizar").click(function(){
	$("#forma").submit();
});

	
$("input[name='auxRoles[]']").change(function(){
	if ( $("#cuentas_username").val().length >0 ){
		if ( $("input[name='auxRoles[]']:checked").length >0 ){
			$("input[name='auxRoles[]']").closest(".form-group").find("div.help-block").html("");
			$("#auxRoles_1").closest(".form-group").parent().removeClass('has-danger has-error');			
		} else {
			$("input[name='auxRoles[]']").closest(".form-group").find("div.help-block").html("Seleccione al memos un rol");
			$("#auxRoles_1").closest(".form-group").parent().addClass('has-danger has-error');
		}
	}
});	


$("#forma").submit(function(event){
	var conErrores = false;
	var mensaje = "";
	console.log("desde submit")
	$("#numEmpleado").closest("div.form-group").find("div.help-block").html("");
	$("#numEmpleado").closest("div.form-group").removeClass("has-error");
	$("input[name='auxRoles[]']").parent().find(".with-errors").html("");
	$("input[name='auxRoles[]']").parent().removeClass("has-error");


	// El número de empleado es obligatorio
    if (!$("#numEmpleado").val()){

    }

	//¿ya existe el número de empleado?
	var $d = LlamadaAjax("/ajaxEsNumEmpleadoDuplicado","post", JSON.stringify({ numero: $("#numEmpleado").val()}) );
	$.when($d).done(function(data){
		if (data.duplicado){
			msj="El numero de empleado ya esta registrado";
			mensaje+=mensaje+"<br>";
			$("#numEmpleado").closest("div.form-group").find("div.help-block").html("Els número de empleado ya existe");
			$("#numEmpleado").closest("div.form-group").addClass("has-error");
		}

		//Turno y horarios requeridos para personal diferente a freelance
		if ( $("#tipocontrato_id").val()==1 ){
			if ($("input[name='turno']:checked").length==0){
				mensaje+="Se requiere el turno"+"<br>";
			}
			if ( $("input[name^='auxHorarios['][name$='].desde'").val().length==0  ){
				mensaje+="Se requiere al menos un horario"+"<br>";
				conErrores = true;
			} else {
				// Se tienen las parejas desde/hasta?
				//Los horarios tienen el formato hh:mm?
				 var mensajeHorarios="";
				 for (var i = 0; i < 5; i++) {
					 var d = $("input[name='auxHorarios["+i+"].desde']").val();
					 var h = $("input[name='auxHorarios["+i+"].hasta']").val();
					 if ( (!moment(d,"HH:mm").isValid()  || !moment(h,"HH:mm").isValid())
							 &&
							(d!=""  || h!="" )
							 ){
						 conErrores=true;
						 mensajeHorarios="Al menos uno de los formatos de los horarios no es correcto o al menos una de las parejas desde/hasta no esta completa";
						 mensaje+= mensajeHorarios+"<br>";
					 }
				}
			}
		}


		if ( $("input[name='auxRoles[]']:checked").length == 0){
			console.log(".................")
			console.log( $("input[name='auxRoles[]']").closest(".form-group").find("div.help-block").text() )
			$("input[name='auxRoles[]']").closest(".form-group").find("div.help-block").html("Seleccione al menos un rol");
			$("#auxRoles_1").closest(".form-group").parent().toggleClass('has-danger has-error');
			conErrores=true;

		} else {
			$("input[name='auxRoles[]']").closest(".form-group").find("div.help-block").html("");
			$("#auxRoles_1").closest(".form-group").parent().toggleClass('has-danger has-error');
		}
		if (conErrores){
			event.preventDefault();
			swal('Error',mensaje,'error');
		}
	});	 // fin del when
});

$('#forma').validator().off('submit');
$('#forma').validator().on('submit', function (e) {
	console.log("Desde forma.validator.submit")
	  if (e.isDefaultPrevented()) {
	    // handle the invalid form...
		  console.log("Formulario con errores")
	  } else {
			// Renombrar campos
			$("#cuentas_username").attr('name', 'cuentas[0].username');
			$("#cuentas_password").attr('name', 'cuentas[0].password');
            var aux=0;
			$("[type='text'][name='auxRolesId[]']").each(function(i,e){
			    if ($(this).val().length>0){
                    $(this).attr("name", "cuentas[0].roles["+aux+"].id");
                    aux++;
			    }
			});
			$("[type='checkbox'][id^='auxRoles_']:checked").each(function(i,e){
				$(this).attr("name","cuentas[0].roles["+i+"].rol.id");
			});

			// El personal de Freelance no requiere horario
			if ( $("#tipocontrato_id").val()== "3" ||   $("#tipocontrato_id").val()== "2" ){
				$("[id^='auxHorarios_']").attr("name","quitar");
			} else {
				$("[id^='auxHorarios_'][id$='_desde']").each(function(i, e){
					//if ($(this).val()){
						var indice =  ($(e).attr("id")).substring( $(e).attr("id").indexOf("_")+1, $(e).attr("id").lastIndexOf("_"));
						$(e).attr("name",  "horarios["+i+"].desde");
						$("#auxHorarios_"+indice+"_hasta").attr("name", "horarios["+i+"].hasta");
						$("#auxHorarios_"+indice+"_diasemana").attr("name", "horarios["+i+"].diasemana");
					//}
				});

			}
			// Correos
			var indice = 0;
			$("#divCorreos input[name='auxCorreos']").each(function(i,e){
				if ( $(e).val().length!=0 ){
					$(e).attr('name', 'correos['+(indice++)+'].email');
				}
			});
	  }
	})
	
	
	
function agregarCorreo(){
	var $copiaCorreo = $("#baseCorreo").clone(false);
	var id= (new Date()).getTime();
	$copiaCorreo.attr("id",  "correo"+id  );
	$("#divCorreos").append( $copiaCorreo );
	$("#correo"+id).find("[name='aEliminarCorreo']a").attr("onclick","eliminarCorreo("+id+")");
	$("#correo"+id).show();
} 	


function eliminarCorreo(cid){
	$("#correo"+cid).remove();	
}


function esNumEmpleadoDuplicado(){
	var $d = LlamadaAjax("/ajaxEsNumEmpleadoDuplicado","post", JSON.stringify({ numero: $("#numEmpleado").val()}) );
	$("#numEmpleado").closest("div.form-group").removeClass("has-error");
	$("#numEmpleado").closest("div.form-group").find("div.help-block").html("");
	$.when($d).done(function(data){
		console.dir(data)
		if (data.duplicado){
			$("#numEmpleado").closest("div.form-group").find("div.help-block").html("Es número de empleado ya existe");
			$("#numEmpleado").closest("div.form-group").addClass("has-error");
		}
	});	
}
	
	
	
$(document).off('blur', "[name='auxCorreos']");
$(document).on('blur', "[name='auxCorreos']", function(){

    if(!this.checkValidity()){
        console.log("NO PASA")
    } else {
        console.log("SI PASA")
    }
    if (this.value().contains(",")){
        console.log("No se acepta ese signo")
    }



});

	
	
	