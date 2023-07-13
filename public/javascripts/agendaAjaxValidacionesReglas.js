function LlamadaAjaxValidaProductorDisponible(desde, hasta, eventoId){ 
	var d = $.Deferred();
    var este = new Object();
    este.inicio = desde;
    este.fin = hasta;
    este.id = eventoId;
    //alert("Parametros enviados a ajaxProductorDisponible:"+JSON.stringify( {este}));
	$.ajax({
		url: '/ajaxProductorDisponible',
		method: "POST",
		data: JSON.stringify( {este}),
		contentType: "application/json; charset=utf-8",
		dataType: "json"
	})
	.success(function(data){
		d.resolve(data);		
	}).error(function(xhr, status, error) {
		d.reject;
	    alert("error rn ajax 025 -  "+ xhr.responseText );
	}); 	
	return d.promise();	
}


function LlamadaAjaxValidaOperadorSalaDisponible(desde, hasta, eventoId, salaId){ 
	var d = $.Deferred();
    var este = new Object();
    este.inicio = desde;
    este.fin = hasta;
    este.id = eventoId;
    este.salaId = salaId;
	$.ajax({
		url: '/ajaxOperadorSalaDisponible',
		method: "POST",
		data: JSON.stringify( {este}),
		contentType: "application/json; charset=utf-8",
		dataType: "json"
	})
	.success(function(data){
		d.resolve(data);		
	}).error(function(xhr, status, error) {
		d.reject;
	    alert("error rn ajax 026 -  "+ xhr.responseText );
	}); 	
	return d.promise();	
}


function LlamadaAjaxValidaPrevioAutorizar( objJson ){
	//console.clear()
	console.dir(objJson)
	/////////////////////////////////var objAux =  JSON.parse(JSON.stringify(objJson));
    var objAux =  objJson;


	//var objAux =  objJson;
	console.dir(objAux)

	//objAux.estado = {id:'5'};

	console.dir(objAux)


	var formularioVisible = false;
	// Se trata de validación desde agenda?
	
	console.log( $("#myModal2").is(":visible") )
	
	if ( $("#myModal2").is(":visible")  )
		formularioVisible = true;

	console.log("formularioVisible ",formularioVisible)
	//console.log("personal RD? ",(objAux.fase.id=="4"))
	if (formularioVisible){
		console.dir(objAux)
		var nombreSelectPersonal=(objAux.fase.id=="4")?"selectRDisenioDisponibles":"selectPersonalDisponibles";
		console.log(nombreSelectPersonal)
		console.dir(   $("select[name='"+nombreSelectPersonal+"'] option:selected")   )
		console.dir(   $("select[name='"+nombreSelectPersonal+"'] option:selected").length   )
		console.dir(   $("select[name='"+nombreSelectPersonal+"'] option:selected").val()   )
		var arrIds=[];
		$("select[name='"+nombreSelectPersonal+"'] option:selected").each(function(){
			console.dir(".......... "+$(this).val())
			arrIds.push(  { id: $(this).val()  }   );
		});
		//console.dir(arrIds)
		objAux.personalIds= arrIds;
		
		console.log("objeto json")
		console.dir(objAux)
	}
	console.log("PREVIO")
	var d = $.Deferred();	
	$.ajax({
		url: '/ajaxValidaPrevioAutorizar',
		method: "POST",
		data: JSON.stringify( {objAux}),
		contentType: "application/json; charset=utf-8",
		dataType: "json"
	})
	.success(function(data){
		d.resolve(data);		
	}).error(function(xhr, status, error) {
		d.reject;
	    alert("error en ajax -  "+ xhr.responseText );
	}); 
	return d.promise();		
}


/* ************************************  */

function validarLasCantidades(elPanel){

	console.log("recibiendo... ",elPanel)
	var d = $.Deferred();	
	var paneles = ["panelesFase1", "panelesFase2", "panelesSalaEdicion3", "panelesSalaEdicion2", "panelesSalaEdicion7"];
	$("#popoverPerfiles").remove();	
	
	
	
	
	$(elPanel).find('select[name="selectPersonalDisponibles"]').closest(".panel-body").find(".with-errors").html("");
	
	if (   paneles.includes( $(elPanel).prop("id")) ){
        var evento = new Object;
        evento.id = $("#divIdTemporal").text();
        evento.folioproductorasignado ={"id":$("#divFolioProductorAsignadoId").text()};
        evento.inicio =  moment($('#divConfirmarFecha').html()+" "+$('#evtDesde').text(), 'DD/MM/YYYY HH:mm');
        evento.fin =  moment($('#divConfirmarFecha').text()+" "+$('#evtHasta').html(), 'DD/MM/YYYY HH:mm');
        evento.estado = {id:$("#divIdEstado").text()   };   	
        evento.personal = [];
        evento.ctaRol = [];
        
        // Si se trata de Salas, se debe tomar en cuenta la ista de Resp de diseño    
        if ( $(elPanel).prop("id").startsWith("panelesSala" ) ){
                $(elPanel).find("select[name='selectRDisenioDisponibles'] option:selected").each(function(i, e){
              		evento.personal.push({ personal: {id: $(e).val()}} );
        		});
        } else {
	        $(elPanel).find("select[name='selectPersonalDisponibles'] option:selected").each(function(i, e){
	              //evento.personal.push({ personal: {id: $(e).val()}} );
	              evento.ctaRol.push({id: $(e).val()});
	        });    
        }    
	    $.when(LlamadaAjaxCantidadPerfiles(evento)).done(function(data){
	    	console.log("AAAAAAQQQQQUUUIIIIIIIIIIIIII");
	    	console.log("eeeeeeeeeeeesssssssssssssssssss");
	    	console.log("jajajajaj");
	    		    	console.log("tambien");
	    	console.log(data)
	    	
	    	$(elPanel).find('select[name="selectPersonalDisponibles"]').closest(".panel-body").find(".with-errors").html("");
	    	if (data.errores.length>0){
				var cad2 = "<div class='center-block'><table class='table table-striped table-condensed table-bordered'>"; 
				cad2+="<caption>Personal solicitado contra personal asignado</caption>";
				cad2+="<thead><tr><th>Perfil</th><th>Solicitado</th><th>Asignado</th></tr></thead>";
				data.errores.forEach(function(v){
					cad2+="<tr><td>"+v.tipo+"</td><td class='text-center'>"+v.solicitado+"</td><td class='text-center'>"+v.asignado+"</td></tr>";
				});
				cad2+="</table></div>";
				$(elPanel).find('select[name="selectPersonalDisponibles"]').closest(".panel-body").prepend('<a id="popoverPerfiles" href="#" data-content="'+cad2+'" data-toggle="popover" data-trigger="hover" data-html=true data-placement="auto" style="color:orange; z-index:1000; position: absolute; right: 1em;" data-container="body" class="animate-flicker"><i class="fas fa-question-circle fa-3x pull-right"></i></a>');
				
				$(elPanel).find('select[name="selectPersonalDisponibles"]').closest(".panel-body").find(".form-group").addClass("has-error");
				$(elPanel).find('select[name="selectPersonalDisponibles"]').closest(".panel-body").find(".with-errors").html("Faltan algunos de los perfiles solicitados");
				var t=$('#popoverPerfiles').popover();
				
				t.off("shown.bs.popover");
				t.on("shown.bs.popover", function(e){
					console.log("......................");
				    t.data("bs.popover").tip().css({"min-width": "350px"});
				});
				d.resolve("warning");
	    	}
	    	else
	    		d.resolve("ok");
	    });	
	} else {
		d.resolve("ok");
	}
    return d.promise();
}


function validarHoraSalida(elPanel){
	var d = $.Deferred();
	var retorno = "ok";
	var paneles = ["panelesFase1", "panelesFase2"];
	$(elPanel).find("div.iconoError1").remove();
	$(elPanel).find('input[name="hSalida"]').closest(".panel-body").find(".with-errors").html("");
	if (   paneles.includes( $(elPanel).prop("id")) ){
        var evento = new Object;
        evento.id = $("#divIdTemporal").text();
        evento.folioproductorasignado ={"id":$("#divFolioProductorAsignadoId").text()};
        evento.inicio =  moment($('#divConfirmarFecha').text()+" "+$('#evtDesde').html(), 'DD/MM/YYYY HH:mm');
        evento.fin =  moment($('#divConfirmarFecha').text()+" "+$('#evtHasta').html(), 'DD/MM/YYYY HH:mm');
        evento.estado = {id:$("#divIdEstado").text()   };   	
        if ( $(elPanel).find("input[name='hSalida']").val().length>0){
        	console.log($('#divConfirmarFecha').text())
        	console.log($(elPanel).find('input[name="hSalida"]').val())
           evento.salidas=[{salida:moment($('#divConfirmarFecha').text()+" "+$(elPanel).find('input[name="hSalida"]').val(), 'DD/MM/YYYY HH:mm')}];
           
           console.dir(evento.salidas)
        	if (  evento.salidas[0].salida.isAfter(   evento.inicio)  ){
				$(elPanel).find('input[name="hSalida"]').closest(".panel-body").prepend('<div class="iconoError1" style="color:red; z-index:1000; position:absolute; right:1em;"><i class="fas fa-exclamation-circle fa-3x pull-right"></i></div>');
				
			//	$(elPanel).find('input[name="hSalida"]').closest(".panel-body").find(".form-group").addClass("has-error");
				$(elPanel).find('input[name="hSalida"]').closest(".panel-body").find(".with-errors").html("bla bla bla");
        		retorno = "error";        		
        	}
        } 
	}
	d.resolve(retorno);
	return d.promise();
}


function validarVehiculos(elPanel){
	var d = $.Deferred();
	var retorno = "ok";
	var paneles = ["panelesFase1", "panelesFase2"];
	$(elPanel).find("div.iconoAdvertencia2").remove();
	$(elPanel).find('select[name="selectVehiculosDisponibles"]').closest(".panel-body").find(".with-errors").html("");
	if (   paneles.includes( $(elPanel).prop("id")) ){
        var evento = new Object;
        evento.id = $("#divIdTemporal").text();
        evento.folioproductorasignado ={"id":$("#divFolioProductorAsignadoId").text()};
        evento.inicio =  moment($('#divConfirmarFecha').html()+" "+$('#evtDesde').text(), 'DD/MM/YYYY HH:mm');
        evento.fin =  moment($('#divConfirmarFecha').html()+" "+$('#evtHasta').text(), 'DD/MM/YYYY HH:mm');
        evento.estado = {id:$("#divIdEstado").text()   };   	
        if ( $(elPanel).find("select[name='selectVehiculosDisponibles'] option:selected").length==0){
        	evento.vehiculos = [{vehiculo:{id: $(elPanel).find("select[name='selectVehiculosDisponibles'] option:selected").val() }}] ;
            console.dir(evento.vehiculos)
			$(elPanel).find('select[name="selectVehiculosDisponibles"]').closest(".panel-body").prepend('<div class="iconoAdvertencia2" style="color:orange; z-index:1000; position:absolute; right:1em;" ><i class="fas fa-exclamation-circle fa-3x pull-right"></i></div>');
            $(elPanel).find('select[name="selectVehiculosDisponibles"]').closest(".panel-body").find(".with-errors").html("No ha asignado vehículo");
     		retorno = "warning";        		
        } 
	}
	d.resolve(retorno);
	return d.promise();
}

function validarEquipoAccesorios(elPanel){
	var d = $.Deferred();
	var retorno = "ok";
	var paneles = ["panelesFase2"];
    var equipo = new Object;
    var accesorio = new Object;

	$(elPanel).find("form div.iconoError4").remove();
	$(elPanel).find('select[name="selectEquipoAccesoriosDisponibles"]').closest(".panel-body").find(".with-errors").html("");
	if (   paneles.includes( $(elPanel).prop("id")) ){
        var evento = new Object;
        evento.id = $("#divIdTemporal").text();
        evento.folioproductorasignado ={"id":$("#divFolioProductorAsignadoId").text()};
        evento.inicio =  moment($('#divConfirmarFecha').html()+" "+$('#evtDesde').text(), 'DD/MM/YYYY HH:mm');
        evento.fin =  moment($('#divConfirmarFecha').html()+" "+$('#evtHasta').text(), 'DD/MM/YYYY HH:mm');
        evento.estado = {id:$("#divIdEstado").text()   };   	
        evento.equipos = [];
        evento.accesorios = [];        
        $(elPanel).find('select[name="selectEquipoAccesoriosDisponibles"] option:selected').each(function(){
            if ( $(this).val().startsWith("E")  ){
                equipo = {equipo:{id: $(this).val().substring(1)}   };
                evento.equipos.push( equipo );
            }
            if ( $(this).val().startsWith("A")  ){
                accesorio = {accesorio:{id: $(this).val().substring(1)} };
                evento.accesorios.push( accesorio );
            }
        });
        
        if (evento.equipos.length==0){
        	retorno = "error";
			$(elPanel).find('select[name="selectEquipoAccesoriosDisponibles"]').closest(".panel-body").prepend('<div class="iconoError4" style="color:red; z-index:1000; position:absolute; right:1em;"><i class="fas fa-exclamation-circle fa-3x pull-right"></i></div>');
            $(elPanel).find('select[name="selectEquipoAccesoriosDisponibles"]').closest(".panel-body").find(".with-errors").html("No ha asignado cámara para la grabación");        	
        }
        } 
	d.resolve(retorno);
	return d.promise();
}


function validarRespDisenio(elPanel){
	var d = $.Deferred();
	var retorno = "ok";
	var paneles = ["panelesSalaEdicion3", "panelesSalaEdicion2", "panelesSalaEdicion7"];
    var personal = new Object;

	$(elPanel).find("form div.iconoAdvertencia5").remove();
	$(elPanel).find('select[name="selectRDisenioDisponibles"]').closest(".panel-body").find(".with-errors").html("");
	if (   paneles.includes( $(elPanel).prop("id"))  &&  $(elPanel).find("[name='inp_preprod_8']").val()> $(elPanel).find('select[name="selectRDisenioDisponibles"] option:selected').length  ){
        var evento = new Object;
        evento.id = $("#divIdTemporal").text();
        evento.folioproductorasignado ={"id":$("#divFolioProductorAsignadoId").text()};
        evento.inicio =  moment($('#divConfirmarFecha').text()+" "+$('#evtDesde').text(), 'DD/MM/YYYY HH:mm');
        evento.fin =  moment($('#divConfirmarFecha').text()+" "+$('#evtHasta').text(), 'DD/MM/YYYY HH:mm');
        evento.estado = {id:$("#divIdEstado").text()   };   	
        evento.personal = [];
        $(elPanel).find('select[name="selectRDisenioDisponibles"] option:selected').each(function(e){
        	evento.personal.push({ personal: {id: $(e).val()}} );
        });
        console.dir(evento.personal)
        if (evento.personal.length==0){
        	retorno = "warning";
			$(elPanel).find('select[name="selectRDisenioDisponibles"]').closest(".panel-body").prepend('<div class="iconoAdvertencia5" style="color:orange; z-index:1000; position:absolute; right:1em;"><i class="fas fa-exclamation-circle fa-3x pull-right"></i></div>');
            $(elPanel).find('select[name="selectRDisenioDisponibles"]').closest(".panel-body").find(".with-errors").html("Solicitados: "+$(elPanel).find("[name='inp_preprod_8']").val()+", asignados: "+$(elPanel).find('select[name="selectRDisenioDisponibles"] option:selected').length);        	
        }
    } 
	d.resolve(retorno);
	return d.promise();
}


function validarLocutores(elPanel){
	var d = $.Deferred();
	var retorno = "ok";
	var paneles = ["panelesUsoCabinaAudio"];
    var locutor = new Object;

	$(elPanel).find("form div.iconoError6").remove();
	$(elPanel).find('select[name="selectLocutoresDisponibles"]').closest(".panel-body").find(".with-errors").html("");
	console.log( $(elPanel).find("input[name='rgUsoCabina']:checked").val() )
	if (   paneles.includes( $(elPanel).prop("id"))  &&  $(elPanel).find("input[name='rgUsoCabina']:checked").val()=="V"  ){
        var evento = new Object;
        evento.id = $("#divIdTemporal").text();
        evento.folioproductorasignado ={"id":$("#divFolioProductorAsignadoId").text()};
        evento.inicio =  moment($('#divConfirmarFecha').text()+" "+$('#evtDesde').text(), 'DD/MM/YYYY HH:mm');
        evento.fin =  moment($('#divConfirmarFecha').text()+" "+$('#evtHasta').text(), 'DD/MM/YYYY HH:mm');
        evento.estado = {id:$("#divIdEstado").text()   };   	
        evento.locutores = [];
        $(elPanel).find('select[name="selectLocutoresDisponibles"] option:selected').each(function(e){
           	locutor =  {personal:{id: $(this).val()}   };
        	evento.locutores.push(  locutor  );
        });
        if (evento.locutores.length==0){
        	retorno = "error";
			$(elPanel).find('select[name="selectLocutoresDisponibles"]').closest(".panel-body").prepend('<div class="iconoError6" style="color:red; z-index:1000; position:absolute; right:1em;"><i class="fas fa-exclamation-circle fa-3x pull-right"></i></div>');
            $(elPanel).find('select[name="selectLocutoresDisponibles"]').closest(".panel-body").find(".with-errors").html("No ha asignado locutor para la grabación de voz");        	
        }
    } 
	d.resolve(retorno);
	return d.promise();
}


function validarCopias(elPanel){
	var d = $.Deferred();
	var retorno = "ok";
	var paneles = ["panelesFase5"];
    var locutor = new Object;

	$(elPanel).find("form div.iconoError7").remove();
	$(elPanel).find('input[name="losFormatos"]').closest(".panel-body").find(".with-errors").html("");
	
	copiasTotales = $(elPanel).find("input[name='losFormatos']").map(function(){return $(this).val().length>0?$(this).val():null;}).get();
	
	console.log( copiasTotales.length  )
	console.log( copiasTotales  )
	
	if (   paneles.includes( $(elPanel).prop("id"))  &&  copiasTotales.length==0  ){
        var evento = new Object;
        evento.id = $("#divIdTemporal").text();
        evento.folioproductorasignado ={"id":$("#divFolioProductorAsignadoId").text()};
        evento.inicio =  moment($('#divConfirmarFecha').text()+" "+$('#evtDesde').text(), 'DD/MM/YYYY HH:mm');
        evento.fin =  moment($('#divConfirmarFecha').text()+" "+$('#evtHasta').text(), 'DD/MM/YYYY HH:mm');
        evento.estado = {id:$("#divIdEstado").text()   };
        	retorno = "error";
			$(elPanel).find("input[name='losFormatos']").closest(".panel-body").prepend('<div class="iconoError7" style="color:red; z-index:1000; position:absolute; right:1em;"><i class="fas fa-exclamation-circle fa-3x pull-right"></i></div>');
            $(elPanel).find("input[name='losFormatos']").closest(".panel-body").find(".with-errors").html("Debe especificar número de copías en al menos un formato");        	
      
    } 
	d.resolve(retorno);
	return d.promise();
}


function validarDatosIngesta(){
	var d = $.Deferred();
	var paneles = ["panelesSiNoOtro"];
        var evento = new Object;
        evento.id = $("#divIdTemporal").text();
        evento.folioproductorasignado ={"id":$("#divFolioProductorAsignadoId").text()};
        evento.inicio =  moment($('#divConfirmarFecha').html()+" "+$('#evtDesde').text(), 'DD/MM/YYYY HH:mm');
        evento.fin =  moment($('#divConfirmarFecha').html()+" "+$('#evtHasta').text(), 'DD/MM/YYYY HH:mm');
        evento.estado = {id:$("#divIdEstado").text()   }; 
        
         var errores = [];
        
        // Cantidad de tarjetas
        var contaCantTar = 0;
        $("input[name^='cantidadTarjetas-']").each(function(){
        	console.log(  $(this).val().length )
        	if ($(this).val().length>0)
        	  contaCantTar++;
        	if ( ($("input[name='cantidadTarjetas-8']").val().length>0)  &&   ($("input[name='otroFormato']")  )) {
        	 	var cadenaErrores = new Object();
        		cadenaErrores.componente = "otroFormato";
        		cadenaErrores.descripcion = "Indique que otro formato";
        		errores.push(cadenaErrores);
        	}
        });
        if (contaCantTar==0){
         	var cadenaErrores = new Object();
        	cadenaErrores.componente = "cantidadTarjetas-";
        	cadenaErrores.descripcion = "Indique por lo menos un formato y la cantidad de tarjetas";
        	errores.push(cadenaErrores);
        } 
        
        //Cambio formato
        if ( componentesSinSeleccionarRadio("radiocambioFormato")==0 ){
        	var cadenaErrores = new Object();
        	cadenaErrores.componente = "radiocambioFormato";
        	cadenaErrores.descripcion = "Indique si se cambió el formato";
        	errores.push(cadenaErrores);
        } else {
        	if ( $("input[name='radiocambioFormato'][value='true']").prop('checked')  &&  componentesSinSeleccionarRadio("cambioFormato")==0  ){
	        	var cadenaErrores = new Object();
	        	cadenaErrores.componente = "cambioFormato";
	        	cadenaErrores.descripcion = "Indique el formato al cual se cambió";
        		errores.push(cadenaErrores);        		
        	}
        }
        
        //Estado del material
        if ( componentesSinSeleccionarRadio("estadoMat")==0  ){
        	 var cadenaErrores = new Object();
        	cadenaErrores.componente = "estadoMat";
        	cadenaErrores.descripcion = "Indique el estado del material";
        	errores.push(cadenaErrores);
        }        

        //Calificacion anterior
        if ( componentesSinSeleccionarRadio("calificado")==0  ){
        	 var cadenaErrores = new Object();
        	cadenaErrores.componente = "calificado";
        	cadenaErrores.descripcion = "Indique si ha calificado con anterior este material";
        	errores.push(cadenaErrores);
        }
        
        //Detalles técnicos sobre video y audio
        if ( $("#DetalleVideo").val().length ==0  ){
        	 var cadenaErrores = new Object();
        	cadenaErrores.componente = "DetalleVideo";
        	cadenaErrores.descripcion = "Escriba los detalles técnicos del video";
        	errores.push(cadenaErrores);        	
        }
        if ( $("#DetalleAudio").val().length ==0  ){
        	 var cadenaErrores = new Object();
        	cadenaErrores.componente = "DetalleAudio";
        	cadenaErrores.descripcion = "Escriba los detalles técnicos del audio";
        	errores.push(cadenaErrores);        	
        }
        
        //Idioma
        if ( componentesSinSeleccionarRadio("idioma")==0  ){
        	 var cadenaErrores = new Object();
        	cadenaErrores.componente = "idioma";
        	cadenaErrores.descripcion = "Indique el o los idiomas";
        	errores.push(cadenaErrores);
        }
        
        //Subtitulos
        if ( componentesSinSeleccionarRadio("subtitulado")==0  ){
        	 var cadenaErrores = new Object();
        	cadenaErrores.componente = "subtitulado";
        	cadenaErrores.descripcion = "Indique si el material esta subtitulado";
        	errores.push(cadenaErrores);
        }        

        //Presencia del productor en la sala
        if ( componentesSinSeleccionarRadio("presencia")==0  ){
        	 var cadenaErrores = new Object();
        	cadenaErrores.componente = "presencia";
        	cadenaErrores.descripcion = "Especifique si el productor estuvo en la sala";
        	errores.push(cadenaErrores);
        }   

        //Problemas para la ingesta
        if ( componentesSinSeleccionarRadio("problema")==0  ){
        	 var cadenaErrores = new Object();
        	cadenaErrores.componente = "presencia";
        	cadenaErrores.descripcion = "Indique si hubo problemas para la ingesta";
        	errores.push(cadenaErrores);
        } else {
        	if (  $("radio[name='problema'][value='true']").prop("checked")  &&   $("#DescripcionProblemas").val().length ==0   ){
        	 var cadenaErrores = new Object();
        	cadenaErrores.componente = "DescripcionProblemas";
        	cadenaErrores.descripcion = "Describa cual fue la problemática que existió";
        	errores.push(cadenaErrores);        		
        	}
        } 
        
        //Almacenamiento
        if ( componentesSinSeleccionarRadio("almacenamiento")==0  ){
        	 var cadenaErrores = new Object();
        	cadenaErrores.componente = "almacenamiento";
        	cadenaErrores.descripcion = "Indique cuales fueron los medios de almacenamiento";
        	errores.push(cadenaErrores);
        } else {
        	if (  $("input[name='almacenamiento'][value=5]").prop("checked")  && $("#otroMedio").val().length == 0   ){
        	 var cadenaErrores = new Object();
        	cadenaErrores.componente = "otroMedio";
        	cadenaErrores.descripcion = "Especifique en que otro medio de almacenamiento";
        	errores.push(cadenaErrores);        		
        	}
        }

		//Se concluyó la ingesta
        if ( componentesSinSeleccionarRadio("concluido")==0  ){
        	 var cadenaErrores = new Object();
        	cadenaErrores.componente = "concluido";
        	cadenaErrores.descripcion = "Indique si se concluyó la ingesta";
        	errores.push(cadenaErrores);
        }

		                
	d.resolve(errores);
	return d.promise();
}



function componentesSinSeleccionarRadio(nombreComponente){
	var contador = 0;
	 $("input[name='"+nombreComponente+"']").each(function(){
	 		if ($(this).prop('checked')){
        	  contador++;
        	}
	 });
	 return contador;
}




