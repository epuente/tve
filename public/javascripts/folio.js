$("input[name='auxProductores[]']").click(function(){
	validarProductoresAsignados();
}); 	


function validarProductoresAsignados(){
	if (varRequiereProductor){
		if ( $("[type='checkbox'][name='auxProductores[]']:checked").length==0 ){
			$("#divProductores div.help-block").show();
			$("#msgProductores").html('No ha asignado ningún productor');
			$("#divProductores").addClass("has-error");
			return false;
		} else {
			$("#divProductores div.help-block").hide();
			return true;
		} 
	}
}	


	function validar(){
		var errores=false;
		if ( $("#numfolio").val().length>0 ){
			var $existe = LlamadaAjaxBuscaFolio( $("#numfolio").val() );		
			// Validar que el número de folio no exista
			$.when($existe).done(function(data){
				console.dir(data)
				if (data!=null){			
					$("#numfolio").closest(".form-group").addClass("has-error");
					$("#numfolio").closest(".form-group").find(".help-block").html("El número de folio ya esta registrado, el folio debe ser único.");
					console.log("Ya existe el oficio");
					errores=true;
				} else {
					$("#numfolio").closest(".form-group").find(".help-block").html("");
					$("#numfolio").closest(".form-group").removeClass("has-error");				
					if ( $("[type='checkbox'][name='auxProductores[]']:checked").length==0   &&   varRequiereProductor   ){
						if (validarProductoresAsignados() == false) {
							errores = true;
						};
					}	
					if ( $("#fechaentrega").val().length==0 ){
						$("#fechaentrega").closest(".form-group").addClass("has-error");
						$("#fechaentrega").closest(".form-group").find(".help-block").html("Se requiere la fecha de entrega");
						errores=true;
					} else {
						$("#fechaentrega").closest(".form-group").removeClass("has-error");
						$("#fechaentrega").closest(".form-group").find(".help-block").html("");
					}
					
					
					if (errores){
						alert("Corrija los errores");
					} else {
						/*
						alert("antes de enviar");
						alert (   $("#idOficio").val()   );
						alert (   $("#id_oficio").val()   );
						alert (   $("#oficio_id").val()   );
						*/
						console.log(  $("#formaFolio")  )
					//	$("#formaFolio").submit();


						alert($("#folio_resguardo").val());
						console.log("resguardo:"+$("#folio_resguardo").val())

						var cadena = "{";
						cadena+="\"oficio.id\":"+ $("#oficio_id").val();
						cadena+=", \"numfolio\":"+ $("#numfolio").val();
						cadena+=", \"fechaentrega\":\""+ $("#fechaentrega").val()+"\"";
						if ($("#numeroresguardo").val()!=null && $("#numeroresguardo").val()!="")
							cadena+=", \"numeroresguardo\":"+ $("#numeroresguardo").val();
						cadena+=", \"observacion\":\""+ $("#observacion").val()+"\"";
						
						cadena+=", \"productoresAsignados\":";
						var productores="[ ";
						$("input[name='auxProductores[]']:checked").each(function(i, e){
							productores+="{\"personal\":{\"id\":"+$(e).val()+"}}, ";
						});
						productores=productores.substring(0, productores.length-2);
						cadena+=productores+"]";
						
						
						
						cadena+="}";
						
						
						
						console.dir(cadena);
						
						$t = LlamadaAjax("/folioSave2","POST", cadena);
						$.when($t).done(function(dataFS){
							console.dir(dataFS);
							if (dataFS.estado=="ok"){
					                swal({
					                        title: "Agregado",
					                        text: "Se agregó correctamente el folio",
					                        type: "success",
					                        showConfirmButton: false
					                });
					                setTimeout(
					                		  function() 
					                		  {
					                			  $("ol.breadcrumb>li>a")[0].click();                      
					                		  }, 2000);    			
					    		}
					    		else {
						    		swal({
					                    title: "No se pudo agregar el folio",
					                    text: "...............................",
					                    type: "error",
					                    showConfirmButton: true
					                  });
					    		}								
						});
					}
				}
			});		
		}
	}
	

$('#formaFolio').submit(function (e) {
	console.log("desde submit")
	

});

$('#formaFolio').validator().submit(function (e) {
	console.log("desde validator.submit")
	if (e.isDefaultPrevented()) {
		alert("Hay errores en el formulario");
		return false;
	} else {		
		$("[type='checkbox'][id^='auxProductores_']:checked").each(function(i,e){
			$(this).attr("name","productoresAsignados["+i+"].personal.id");
		});            
	}
});	


$("#btnBuscar").click(function(){
		$("#divOficiosAsignados2").html("");
		if ( $("#busquedaOficio").val().length == 0  ){
			$("#msgBusqueda").html("<h3>Indique el número de oficio a buscar</h3>").removeClass("text-success").addClass("text-danger");
		} else {
			$.ajax({
				url: '/folioBuscarOficio/'+encodeURIComponent($("#busquedaOficio").val()),
				method: 'GET',
				dataType: "json"        
			}).success(function(data){
				if (data.length != 0)
					if (data[0].servicios.length!=0)
						varRequiereProductor = data[0].servicios.map(function(n){ return n.servicio.id == 1|| n.servicio.id ==2 }).reduce(function(r, n){return r || n});
				$("#conDatos").toggle(data!=0);
 			    $("#divProductores").toggle( varRequiereProductor  );
				var t = '<div class="panel panel-default">'+
				'<div class="panel-body">';
				$("[name='conOficio']").toggle(data.length!=0);
                console.dir(data);
				if (data.length!=0){
					$("#formaFolio")[0].reset();
					$("[name='folio']").focus();
					$("#msgBusqueda").html("<h3>Encontrado el número de oficio.</h3>").removeClass("text-danger").addClass("text-success");
					data.forEach(function(o,io, arreglo){
						$("#oficio_id").val(o.id);
						$("#oficio\\.oficio").html(o.oficio);
						$("#oficio\\.titulo").html(o.titulo);						
						$("#oficio\\.descripcion").html(o.descripcion);
						$("#oficio\\.urremitente\\.nombreLargo").html(o.urremitente.nombreLargo);
						if(o.observacion!=null){
						    $("#oficio\\.observacion").html(o.observacion);
						}
						var cadServicios="";
						for(i=0; i<o.servicios.length;i++){
							var s = o.servicios[i];
						    cadServicios+='<div class="col-xs-6 col-sm-6 col-md-6 col-lg-6">'+s.servicio.descripcion+'</div>';
						}        	
						$("#oficio\\.servicios").html(cadServicios);
						var cadProd="";
						for(j=0; j<o.productoresSolicitados.length; j++){
							var ps= o.productoresSolicitados[j];
						    cadProd+='<div class="col-xs-6 col-sm-6 col-md-6 col-lg-6">'+ps.personal.nombre+" "+ps.personal.paterno+" "+ps.personal.materno +'</div>';
						    $("input[name='auxProductores[]'][value="+ps.personal.id+"]").prop("checked", true);
						}
						$("#oficio\\.productores").html(cadProd);
						if(o.folios.length>0){
								var cad2="<div class='row'>";
								    cad2+="  <div class='col-sm-12'><strong>Folios asignados a este oficio</strong></div>";
								    cad2+="</div>";
				                for(l=0; l<o.folios.length;l++){
									var q = o.folios[l];
									cad2+="<div class='row' style='font-size:0.8em; border-top:1px solid; border-top-color:#C0C0C0;'>";
									cad2+="  <div class='col-sm-2 col-md-2 col-lg-2'>"+q.numfolio+"</div>";
									cad2+="    <div class='col-sm-10 col-md-102 col-lg-10'>";
									cad2+="      <div class='row'>";
						                            for(m=0; m<q.productoresAsignados.length; m++){
														var p=q.productoresAsignados[m];
						                                cad2+="<div class='col-sm-4 col-md-4 col-lg-12'>"+p.personal.nombre+" "+p.personal.paterno+" "+p.personal.materno+"</div>";                                
						                            }
	                            	cad2+="      </div>";
									cad2+="    </div>";						                            
				                    cad2+="  </div>";
				                    cad2+="</div>";                                                
				                }                                    
						    $("#divOficiosAsignados2").html(cad2);
						}
					});
					$("#divRowOficioInfo").css("display","block");
				} else {
					$.notify({
						title: "<strong>No localizado:</strong> ",
						message: "No se ha encontrado el número de oficio"
					},{
						type: 'danger'
					});	        	
					$("#msgBusqueda").html("<h3>El número de oficio no existe.</h3>").removeClass("text-success").addClass("text-danger");
				}
	
	
			}).error(function(xhr, status, error) {
				alert("error en ajax (a)-  "+ xhr.responseText );
				alert("error en ajax (a)-  "+ error );
				alert("error en ajax (a)-  "+ xhr);
			})  
		} 
	
});  



function validarNoFolio(noFol){
	retorno = false;
	$existe = LlamadaAjaxBuscaFolio(noFol);
    $.when($existe).done(function(d){
    	console.log(d)
    	if (d != null ){
			$("#numfolio").closest(".form-group").addClass("has-error");
			$("#numfolio").closest(".form-group").find(".help-block").html("El número de folio ya esta registrado, el folio debe ser único.");
    	} else {
    		$("#numfolio").closest(".form-group").removeClass("has-error");
    		$("#numfolio").closest(".form-group").find(".help-block").html("");        		
    		retorno = true;
    	}
    	return retorno;
    });    	
}


function validarNoResguardo(noRes){
	retorno = false;
	var $nRes = LlamadaAjax("/folioBuscarNumResguardo", "POST", JSON.stringify( { "nresguardo": noRes})); 
    $.when($nRes).done(function(d){
    	console.log(d)
    	if (d.existe == true ){
			$("#folio_resguardo").closest(".form-group").addClass("has-error");
			$("#folio_resguardo").closest(".form-group").find(".help-block").html("El número de resguardo ya esta registrado, el número de resguardo debe ser único.");
    	} else {
    		$("#folio_resguardo").closest(".form-group").removeClass("has-error");
    		$("#folio_resguardo").closest(".form-group").find(".help-block").html("");        		
    		retorno = true;
    	}
    	return retorno;
    });    	
}

	$("#numfolio").blur(function(){
		if ($(this).val()){
			validarNoFolio( $(this).val() );
		}
	});
	
	$("#folio_resguardo").blur(function(){
		if ($(this).val()){
			validarNoResguardo( $(this).val() );
		}
	});	

	
	
	$("#btnActualizaFolio").click(function(){
		console.log("Actulizar")
		var params = $("#formaFolio").serializeJSON();
		var prod ="";
		$("input[name='auxProductores[]']:checked").each(function(i, e){
			var idx="";
			if ($(e).data("folioproductorasignado")!=undefined)
				idx = "\"id\":"+$(e).data("folioproductorasignado")+",";
			prod += "{"+idx+"\"personal\":{\"id\":"+$(e).val()+"}},";			
		});
		if (prod.length>1)
			prod = prod.substring(0, prod.length-1);
		params.productoresAsignados = JSON.parse("["+prod+"]");
		if ($("#operacionFormulario").data("idedicion")!=null)
			params["id"]=$("#operacionFormulario").data("idedicion");
		delete params["auxProductores"];
		delete params["oficio.id"];
		console.dir(params);
		$.when( LlamadaAjax("/folio/updateAjax", "POST", JSON.stringify(params))  ).done(function(data){
			console.dir(data)
			if (data.estado=="actualizado"){
				   $.notify( "Se actualizó el folio" );
	                setTimeout(
                		  function() 
                		  {
                			  $("ol.breadcrumb>li>a")[0].click();                      
                		  }, 2000);   
			}
		});
	});

	$("#btnCancelaFolio").click(function(){
		console.log("Cancelar folio")
        var id=$("#operacionFormulario").data("idedicion");
        var numfolio=$("#numfolio").val();
        swal({
            title: "¿Desea cancelar el folio "+numfolio+"?",
            text: "Al cancelarlo no aparecerá en la agenda, ni en bla, bla, bla",
            type: "question",
            showConfirmButton: true,

            showCancelButton: true,
            confirmButtonColor: "#d33",
            cancelButtonColor: '#3085d6',
            confirmButtonText: "Si, cancelar el folio",
            cancelButtonText: "No, conservar el folio",
            preConfirm: function(){
                return new Promise(function (resolve, reject) {
                    //LLamada al controlador para eliminar evento
                    $cancelado = LlamadaAjax("/cancelaFolio", "POST",   JSON.stringify({id: id, motivoId: 1})  );
                    $.when($cancelado).done(
                            function(d){
                                if (d.cancelado){
                                    resolve();
                                } else {
                                    reject("No fue posible cancelar el folio");
                                }
                            }
                    );
                });
            }
          }).then(function () {
                swal(
                          'Cancelado!',
                          'Se canceló correctamente el folio '+numfolio,
                          'success'
                        );
                setTimeout(
                          function()
                          {
                              $("ol.breadcrumb>li>a")[0].click();
                                  }, 2000);
          }, function (dismiss) {
                if (dismiss === 'cancel') {
                  swal(
                    'Cancelado',
                    'Usted canceló la operación sobre el folio.<br>Se conserva el folio',
                    'info'
                  )
                }
          });



	});


    $("#btnEliminaFolio").click(function(){
        console.log("Boton elimina Folio")
        var params = $("#formaFolio").serializeJSON();
        console.log(params)

        var id=$("#operacionFormulario").data("idedicion");

        console.dir(  JSON.stringify({id: id}) )

        // Buscar que el folio no ete relacionado a ninguna preagenda y/o agenda
        $b = LlamadaAjax("/ajaxBuscaFolioProductorAsignado", "POST", JSON.stringify({id: id})   );
        $.when($b).done(function (data){
            console.dir(data)
            if (data.borrable==true){
	    		swal({
                    title: "¿Desea eliminar el folio "+params['numfolio']+"?",
                    text: data.mensajes,
                    type: "info",
                    showConfirmButton: true,

                    showCancelButton: true,
                    confirmButtonColor: "#d33",
                    cancelButtonColor: '#3085d6',
                    confirmButtonText: "Si, borrar el folio",
                    cancelButtonText: "No, cancelar borrado",
                    preConfirm: function(){
                        return new Promise(function (resolve, reject) {
                            //LLamada al controlador para eliminar evento
                            $borrado = LlamadaAjax("/eliminaFolio", "POST",   JSON.stringify({id: id})  );
                            $.when($borrado).done(
                                    function(d){
                                        if (d.borrado){
                                            resolve();
                                        } else {
                                            reject("No fue posible eliminar el folio");
                                        }
                                    }
                            );
                        });
                    }
                  }).then(function () {
                        swal(
                                  'Eliminado!',
                                  'Se eliminó correctamente el folio '+params['numfolio'],
                                  'success'
                                );
                        setTimeout(
                                  function()
                                  {
                                      $("ol.breadcrumb>li>a")[0].click();
                                          }, 2000);
                  }, function (dismiss) {
                        if (dismiss === 'cancel') {
                          swal(
                            'Cancelado',
                            'Usted canceló la eliminación del folio.<br>Se conserva el folio',
                            'info'
                          )
                        }
                  });
              }
            if (data.borrable==false){
	    		swal({
                    title: "No es posible eliminar el folio "+params['numfolio'],
                    text: data.mensajes,
                    type: "error",
                    showConfirmButton: false,
                    showCancelButton: true,
                    cancelButtonColor: '#3085d6',
                    cancelButtonText: "Entendido"
                    });
            }
        });
   });
