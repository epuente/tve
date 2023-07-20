const tiposArchivos = ["Oficio", "Oficio de respuesta", "Minuta de acuerdo", "Guión o escaleta", "Entrada/salida material", "Bitácora", "Evidencia de entrega", "Encuesta satisfacción"];
const descripcion = ["", "respuesta", "minuta", "guion", "entrada", "bitacora", "evidencia", "encuesta"]
const tiposArchivosCardinalidad = [1,1, 3,10, 1,10, 1,1];



$("#btnAgregar").click(function(e){
    var conErrores= false;
    //El número de oficio existe en la BD?
    if ($("#oficio").val().length>0){
        $existe = LlamadaAjaxBuscaOficio($("#oficio").val());
        $.when($existe).done(function(oficio){
            if (oficio.length>0){
                console.log("ya existe el oficio")
                    swal({
                        title: 'Error',
                        html:  'Ya existe el oficio <strong>'+$("#oficio").val()+"</strong><br>.......... ",
                        icon:  'error',
                        confirmButtonText: 'Cerrar'
                    });

                $("#oficio").closest('div.form-group').find('.with-errors').html("Este número de oficio ya existe");
                $("#oficio").closest('div.form-group').addClass("has-error");
                conErrores=true;
            } else {
                $("#oficio").closest('div.form-group').removeClass("has-error");
            }
            var aux = new validacionCompleta();
            aux.datos().then(function (datos){
                console.log($(".has-error").length)
                if ( $(".has-error").length==0){
                    console.log("antes de submit")
                    $("#frmAux").submit();
                } else{
                        console.log("campos con error "+$(".has-error").length)
                        swal("Error","Corrija los errores por favor 1","error");
                }
            });
        });
    } else {
        swal("Error","El número de oficio es obligatorio","error");
    }
});


$("#frmAux").submit(function(event){
    console.log("DESDE SUBMIT")
});


$('#frmAux').validator().off('submit');
$('#frmAux').validator().on('submit', function (e) {
      if (e.isDefaultPrevented()) {
        // handle the invalid form...
          swal(
                "Error",
                "El formulario poresenta errores, por favor corríjalos.",
                "error"
            );
      } else {
            $("[type='checkbox'][id^='auxProductores_']:checked").each(function(i,e){
                $(this).attr("name","productoresSolicitados["+i+"].personal.id");
            });
                $("[type='checkbox'][id^='auxServicios_']:checked").each(function(i,e){
                    $(this).attr("name","servicios["+i+"].servicio.id");
                    // Es grabación de evento?
                    if ( $(e).val() === "2"  ){
                        var ifg=0;
                        $("#divFechasGrabacion").find(".row").each(function(i, e){
                            var fechaString =  moment($(this).find("input[name='fechagrabaciones.fecha']").val(),"DD/MM/YYYY").format("YYYY-MM-DD");
                            var inicioString = moment(fechaString+" "+$(this).find("input[name='fechagrabaciones.inicio']").val()).format("YYYY-MM-DD HH:mm:ss");
                            var finString =    moment(fechaString+" "+$(this).find("input[name='fechagrabaciones.fin']").val()).format("YYYY-MM-DD HH:mm:ss");

                        });
                    }
                });
                    var arrAuxC = [];
                $("#divContactos").each(function(i, e){
                    console.log(i)
                    var i=0;

                    $(e).find("div[id^='contacto']").each(function(icontacto, contacto)
                        {
                            var itel=0;
                            var icor=0;

                            //console.dir(  $(e).find("div.row:eq("+i+") div").html()  )
                            console.log(  $(contacto).find(".row:eq(0) [name='auxContactos']").val()  )
                            if ( $(contacto).find(".row:eq(0) [name='auxContactos']").val().length>0 ){

                                $(contacto).find(".row:eq(0) [name='auxContactos']").attr("name", "contactos["+i+"].nombre");

                                var arrCT=[];
                                $(contacto).find(".row:eq(1) div[name='panelTelefonos'] div[name='divContactoTelefono'] input[name^='contactos.telefono']").each(function(itelefono, etelefono){
                                    console.log( $(etelefono).val())
                                    var auxCT = {};
                                    if ($(etelefono).val().length>0){
                                        $(etelefono).attr("name", "contactos["+i+"].telefonos["+itel+"].telefono");
                                        auxCT={"telefono":$(etelefono).val()};
                                        arrCT[itel]=auxCT;
                                        itel++;
                                    }
                                });

                                var arrCC=[]
                                $(contacto).find(".row:eq(1) div[name='panelCorreos'] div[name='divContactoCorreo'] input[name^='contactos.correo']").each(function(icorreo, ecorreo){
                                    console.log( $(ecorreo).val())
                                    var auxCC = {};
                                    if($(ecorreo).val().length>0){
                                        $(ecorreo).attr("name", "contactos["+i+"].correos["+icor+"].correo");
                                        auxCC={"correo":$(ecorreo).val()};
                                        arrCC[icorreo]=auxCC;
                                        icor++;
                                    }

                                });

                                arrAuxC[i]={"nombre": $(contacto).find("[name='contactos["+i+"].nombre']").val(), "telefonos":arrCT, "correos":arrCC};

                                i++;
                            }
                        });

                });
                 console.log("************************************************************************************")



                e.preventDefault();

                // Archivos del Oficio
                var formData = new FormData($("#frmAux")[0]);
                console.debug("num input file "+$("div[name='copiaBaseArchivo']").find("input[type='file']").length)
                $("div[name='copiaBaseArchivo']").find("input[type='file']").each(function(i,e){
                    var id = $(e).attr("id");
                    var aux = id.split("-");
                    var tipo = aux[1];
                    var secuencia = aux[2];
                    formData.append('file'+id, $(e).get(0).files[0]);
                   // fdata.append('file'+id, $(e).get(0).files[0]);
                    console.log ("  ... agregando "+id)
                });

                //return false;

                $.each($("#frmAux:input"), function(i, componente) {
                  var nombreCampo = $(componente).prop("name");
                  var valorCampo = $(componente).val();
                  var tipoComponente = $(componente).prop("type");
                  if (nombreCampo!=""){
                    if (tipoComponente=="checkbox"){
                            if ($(componente).is(":checked")) {
                                fdata.append(nombreCampo, valorCampo);
                                //console.log( "nombre "+tipoComponente+" - "+(tipoComponente=="checkbox")+"  "+  nombreCampo+" -> "+valorCampo )
                            }
                    } else {
                        fdata.append(nombreCampo, valorCampo);
                    }
                  }
                });



                if ( $("#operacionFormulario").data("operacion")=="create" ){
                     $.ajax({
                           url: '/oficio/save',
                           data: formData,
                           type: 'POST',
                           enctype: 'multipart/form-data',
                           contentType: false,
                           processData: false,
                           cache: false,
                           dataType: "json",
                           beforeSend: function () {

                           },
                           success: function (  data) {
                             console.dir(data)
                             console.log(data.estado=="ok")
                             if (data.estado=="ok"){
                                    $.notify({
                                        title: "<strong>Correcto:</strong> ",
                                        message: "Se creó el oficio "+formDataObj["oficio"]
                                    },{
                                        type: 'success'
                                    });
                                    swal({
                                        title: "¿Asignar folio al oficio "+formDataObj["oficio"]+"?",
                                        title: "¿Asignar folio al oficio "+formDataObj["oficio"]+"?",
                                        html: "El oficio ya ha sido guardado.</br><div style='margin:5px;'>¿Desea asignarle un folio?</dir></br></br> "+
                                                "<a href='/folio/"+data.id+"' class='btn btn-primary' role='button'>Si, asignar folio</a>"+
                                                "<a href='/oficios' class='btn btn-warning' role='button'>No, continuar con oficios</a>",
                                        icon: "question",
                                        showCancelButton: false,
                                        showConfirmButton: false,
                                        confirmButtonColor: "#d33",
                                        cancelButtonColor: '#3085d6',
                                        confirmButtonText: "Si",
                                        cancelButtonText: "No",
                                        focusConfirm: false,
                                        preConfirm: ()=>{
                                                console.log("------------------------------------------- ")

                                        },
                                        preDeny: ()=>{

                                                console.log("* * * * * ");
                                            }
                                      }), function (dismiss) {
                                            if (dismiss === 'cancel') {
                                                console.log(".....................");
                                            }
                                        }
                             }

                           },
                           error: function (jqXHR, textStatus, errorThrown) {
                             alert(textStatus + ': ' + errorThrown);
                           }
                    });
                }

                if ( $("#operacionFormulario").data("operacion")=="edit" ){



                    console.debug(formData);
                    //return false;
                    $.ajax({
                           url: '/oficio/update/'+$("#operacionFormulario").data("idedicion"),
                           data: formData,
                           type: 'POST',
                           enctype: 'multipart/form-data',
                           contentType: false,
                           processData: false,
                           cache: false,
                           dataType: "json",
                           beforeSend: function () {

                           },
                           success: function (  data) {
                                console.dir(data);
                                if (data.estado=="ok"){
                                        $.notify({
                                            title: "<strong>Correcto:</strong> ",
                                            message: "Se actualizó el oficio "+$("#oficio").val()
                                        },{
                                            type: 'success'
                                        });
                                        setTimeout(
                                          function()
                                          {
                                             $("ol.breadcrumb>li>a")[0].click();
                                          }, 2000);
                                }
                            }

                    });
                }
    }
});


$("#btnAgregarContacto").click(function(){
    agregarContacto();
    //agregarContactoTelefono()
});


function agregarContacto(){
    var $copiaContacto = $("#baseContacto").clone(false);
    var id= (new Date()).getTime();
    $copiaContacto.attr("id",  "contacto"+id  );
    $("#divContactos").append( $copiaContacto );
    agregarTelefono(id);
    // Se actualiza el parametro en la llamda a agregarTelefono
    $("#contacto"+id).find("div[name='panelTelefonos'] h5>a").attr("onclick","agregarTelefono("+id+")");

    agregarCorreo(id);
    // Se actualiza el parametro en la llamda a agregarCorreo
    $("#contacto"+id).find("div[name='panelCorreos'] h5>a").attr("onclick","agregarCorreo("+id+")");

    //Se actualiza el parametro en la llamada a eliminarContacto
    $("#contacto"+id).find("[name='aEliminarContacto']a").attr("onclick","eliminarContacto("+id+")");
}


function eliminarContacto(cid){
    $("#contacto"+cid).remove();
}

function agregarFechaGrabacion(){
    var id = (new Date()).getTime();
    $("#divFechasGrabacion").append(
          '<div class="row" id="rowFechaGrabacion'+id+'">'+
              '<div class="col-xs-5 col-sm-5 col-md-5 col-lg-5 form-group">'+
                  '<input type="text" name="fechagrabaciones.fecha" class="form-control" required=true>'+
                  '<div class="help-block with-errors"></div>'+
              '</div>'+
              '<div class="col-xs-3 col-sm-3 col-md-3 col-lg-3 form-group">'+
                '<div class="input-group clockpicker" data-autoclose="true">'+
                        '<input type="text" class="form-control" name="fechagrabaciones.inicio" placeholder="Inicio" required=true>'+
                        '<div class="help-block with-errors"></div>'+
                '</div>'+
              '</div>'+

              '<div class="col-xs-3 col-sm-3 col-md-3 col-lg-3 form-group">'+
                  '<div class="input-group clockpicker" data-autoclose="true">'+
                      '<input type="text" class="form-control" name="fechagrabaciones.fin" placeholder="Fin">'+
                  '</div>'+
              '</div>'+
              '<div class="col-xs-1 col-sm-1 col-md-1 col-lg-1">'+
                      '<a href="javascript:eliminarFechaGrabacion('+id+')" class="text-danger"><i class="fa fa-minus-circle" aria-hidden="false"></i></a>'+
              '</div>'+
          '</div>'
          );
    $("input[name='fechagrabaciones.fecha']").daterangepicker({
         singleDatePicker: true,
          "locale": {
                "format": "DD/MM/YYYY",
                "separator": " - ",
                "applyLabel": "Aplicar",
                "cancelLabel": "Cancelar",
                "fromLabel": "Desde",
                "toLabel": "_Hasta",
                "customRangeLabel": "Custom",
                "weekLabel": "W",
                "daysOfWeek": [
                    "Do",
                    "Lu",
                    "Ma",
                    "Mi",
                    "Ju",
                    "Vi",
                    "Sa"
                ],
                "monthNames": [
                    "Enero",
                    "Febrero",
                    "Marzo",
                    "Abril",
                    "Mayo",
                    "Junio",
                    "Julio",
                    "Agosto",
                    "Septiembre",
                    "Octubre",
                    "Noviembre",
                    "Diciembre"
                ],
                "firstDay": 1
            }

        });
    $('.clockpicker').clockpicker();
    $('#frmAux').validator('update');
}

function eliminarFechaGrabacion(i){
    $("#rowFechaGrabacion"+i).remove();
};

function agregarTelefono(cid){
    var e = $("[id=contacto"+cid+"]");
    var id = (new Date()).getTime();
    $(e).find("[name='divContactoTelefono']").append(
            '<div class="row" id="rowTel'+id+'">'+
                '<div class="col-xs-10 col-sm-10 col-md-10 col-lg-10 form-group">'+
                    '<input type="text" name="contactos.telefono" value="" class="form-control" placeholder="Número telefónico o extensión">'+
                '</div>'+
                '<div class="col-xs-2 col-sm-2 col-md-2 col-lg-2 form-group">'+
                    '<a href="javascript:eliminarTelefono('+id+')" class="text-danger"><i class="fa fa-minus-circle" aria-hidden="true"></i></a>'+
                '</div>'+
            '</div>'
    );
};

function eliminarTelefono(i){
    $("#rowTel"+i).remove();
};


function agregarCorreo(cid){
    var e = $("[id=contacto"+cid+"]");
    var id = (new Date()).getTime();
    $(e).find("[name='divContactoCorreo']").append(
            '<div class="row" id="rowCorreo'+id+'">'+
                '<div class="col-xs-10 col-sm-10 col-md-10 col-lg-10 form-group">'+
                    '<input type="text" name="contactos.correo" value="" class="form-control" placeHolder="Correo electrónico (email)">'+
                '</div>'+
                '<div class="col-xs-2 col-sm-2 col-md-2 col-lg-2 form-group">'+
                    '<a href="javascript:eliminarCorreo('+id+')" class="text-danger"><i class="fa fa-minus-circle" aria-hidden="true"></i></a>'+
                '</div>'+
            '</div>'
    );
};

function eliminarCorreo(i){
    $("#rowCorreo"+i).remove();
};


$("input[name='auxServicios[]'][value=2]").click(function(){
    $("#FechasGrabacion").toggle( $(this).prop('checked'));
});



function validarNoOficio(noOf){
    retorno = false;
    $existe = LlamadaAjaxBuscaOficio(noOf);
    $.when($existe).done(function(d){
        console.log(d)
        if (d.length>0){
            $("#oficio_field").parent().addClass("has-error").addClass("has-danger");
            $("#oficio_field").parent().find("div.help-block.with-errors").html('<ul class="list-unstyled"><li>Ya existe este número de oficio</li></ul>')
        } else {
            $("#oficio_field").parent().removeClass("has-error").removeClass("has-danger");
            $("#oficio_field").parent().find("div.help-block.with-errors").html('')
            retorno = true;
        }
        return retorno;
    });
}


$("#oficio").blur(function(){
    if ($(this).val().length>0)
        return  validarNoOficio($(this).val());
    else
        return false;

});

$("#descripcion, #titulo").keyup(function(e){
    validarTituloDescripcion();
});


$("#fecharemitente, #fecharecibidoupev, #fecharecibidodtve").change(function(){
    $("#fecharemitente, #fecharecibidoupev, #fecharecibidodtve").closest('div.form-group').find('.with-errors').html(" ");
    $("#fecharemitente, #fecharecibidoupev, #fecharecibidodtve").closest('div.form-group').removeClass("has-error");
    if ( $("#fecharemitente").val().length!=0 && ($("#fecharecibidoupev").val().length!=0 || $("#fecharecibidodtve").val().length!=0 )){
        validarFechas();
    }
});


$("#urremitente_id").change(function(){
    $("#urremitente_id").closest('div.form-group').find('.with-errors').html(" ");
    $("#urremitente_id").closest('div.form-group').removeClass("has-error");
    if ($("#urremitente_id option:selected").val()==""){
        $("#urremitente_id").closest('div.form-group').find('.with-errors').html("Especifique la Unidad Responsable");
        $("#urremitente_id").closest('div.form-group').addClass("has-error");
    }
});


$("input[name='auxServicios[]']").change(function(){
    validarServiciosSolicitados();
});


function otro(){
    console.log("001");
    $("#oficioArchivo").click();
}


function subirArchivo(c){
    console.clear()
    console.log(c.id)
    var i2 = (c.id).indexOf('btnSubir');
    console.log("i2: "+i2)
    var n = c.id.substring(8);
    var id = "oficio"+n;
    console.log("n: "+n)
    console.log("id "+id)
    $("#"+id).click();


    //oficioArchivoRespuesta
    //btnSubirArchivoRespuesta
}

function subirArchivo2(c){
    console.clear()
    console.log(c)
    console.log("#file"+c)
    $("#file"+c).click();
}

function clonarBaseArchivo(tipo, filename, id){
    // if ( $("#ajaxEdoInicialArchivos").find("div[name='copiaBaseArchivo']").length==0)
    //     $("#ajaxEdoInicialArchivos").html("");
    var copia = $("#divBaseArchivo").clone(false);
    var secuencial = ($("input[type='file'][id^='fileOficioArchivo-"+tipo+"']").length)+1

    // Cambia el id del input file
    $(copia).find("input[type='file']").attr("id", "fileOficioArchivo-"+tipo+"-"+secuencial);

    //Renombra copia del id del div que contiene el filename
    $(copia).find("div[id='txtArchivo-0-0']").attr("id", "txtArchivo-"+tipo+"-"+secuencial).css("display","block");

    // Asigna el botón upload al input file
    /*
    $(copia).find(".fa-file-upload").closest("a").attr("href", "javascript:$(\"#fileOficioArchivo-"+tipo+"-"+secuencial+"\").click()"  );
    $(copia).find(".fa-file-upload").closest("div").css("display","block")
    */

    // Asigna funcion al boton de eliminar archivo
    $(copia).find(".fa-minus-circle").closest("a").attr("href", "javascript:eliminaArchivo("+tipo+","+secuencial+", "+id+")"  );
    $(copia).find(".fa-minus-circle").closest("div").css("display","block")

    $(copia).find("#BaseArchivo").attr("name", "copiaBaseArchivo");
    $(copia).find("#BaseArchivo").removeAttr("id");

    if (filename!=undefined){
        $(copia).find("#txtArchivo-"+tipo+"-"+secuencial).html("<small>"+filename+"</small>");
        $((copia).find("#txtArchivo-"+tipo+"-"+secuencial)).attr("data-arch", id);
    }

    $(copia).find(".fa-eye").parent().attr("href", "javascript:verOficio("+id+",\""+descripcion[tipo-1]+"\")");
    $(copia).find(".fa-eye").closest("div").css("display", "block");

    if (  $("#archivos"+tipo).length==0 ){
        $("#ajaxEdoInicialArchivos").append( "<div id='archivos"+tipo+"' style='display:block'>       <div class='row' style='margin-bottom:0.5em'><div class='col-xs-12 col-sm-12 col-md-12 col-lg-12'>"+tiposArchivos[tipo-1]+"</div></div></div>"  );
    }
    var agregados = $("#archivos"+tipo).find("div[name='copiaBaseArchivo']").lenght+1;
    //$(copia).append(  botonAgregarMas(tipo, agregados)  );
    $("#archivos"+tipo).append($(copia).html());
    $("#archivos"+tipo).append(botonAgregarMas(tipo, agregados));

    return secuencial;
}


function subirArchivoNuevo(tipo){
    console.debug("Desde subirArchivoNuevo "+tipo);

    /*
    var aux = clonarBaseArchivo(tipo);
    var copia = aux.renglon.html();
    var sec = aux.secuencial;
    $(copia).appendTo("#archivos"+tipo);
    $("#fileOficioArchivo-"+tipo+"-"+sec).click();
    */
    s = clonarBaseArchivo(tipo);
    $("#fileOficioArchivo-"+tipo+"-"+s).click();
}

// Despues de que seleccionó el archivo
$(document).off("change", "[id^='fileOficioArchivo-']");
$(document).on("change", "[id^='fileOficioArchivo-']", function(){
      console.log("003");
      console.debug("this.id:"+$(this).attr("id"))
      filename = this.files[0].name;
      aux = this.id.split("-");
      cname = aux[0].split("file")[1];
      tipo = aux[1];
      secuencial = aux[2];
      console.debug("cname ", cname)
      console.log(filename);

      $("#archivos"+tipo).find("button[name='btnAgregarOtroArchivo']").remove();
      //AQUI ME QUEDÉ

      // Ocultar el div divNoHay
      $("#archivos"+tipo).find("div[name='divNoHay']").css("display", "none");

      cantidad = $("input[type='file'][id^='fileOficioArchivo-"+tipo+"']").length;

      if ( tiposArchivosCardinalidad[tipo-1]>1 && cantidad < tiposArchivosCardinalidad[tipo-1]){
        console.debug("cantidad de file "+tipo+":"+cantidad)
        console.debug("tiposArchivosCardinalidad[tipo-1] "+tiposArchivosCardinalidad[tipo-1])
        if (cantidad <= tiposArchivosCardinalidad[tipo-1]){
            boton = "<div class='center-block'><button name='btnAgregarOtroArchivo' class='btn btn-sm btn-primary btn-block' type='button' style='margin-top:0.8em;' onclick='subirArchivoNuevo("+tipo+")'>Agregar otro archivo</button></div>";
        }
        $("#archivos"+tipo).append(boton);
      }

      console.debug("#txtArchivo-"+tipo+"-"+secuencial)
      $("#txtArchivo-"+tipo+"-"+secuencial).html("<small>"+filename+"</small>");

      $(this).parent().parent().find("a[href^='javascript:verOficio']").remove();
      // Muestra el div que contiene el clon de BaseArchivo
      $(this).closest("div[name='copiaBaseArchivo']").css("display","block")
});



$("#oficioArchivo").change(function() {
      console.log("002");
      filename = this.files[0].name;
      $("#nombreArchivo").val(filename);
      console.log(filename);
      $("#btnSubirArchivo").blur();
      $("#btnVerArchivo").css("display","none");
});


function validarGrales(){
	var d = $.Deferred();
	// Título y descripcion
	validarTituloDescripcion();
		
	// UR solicitante
	validarURSol();

	// Servicios solicitados
	validarServiciosSolicitados();
	
	
   	d.resolve();
   	return d.promise();			
}


    function validarFechas(){   
	console.log("desde validarFechas")
		var d = $.Deferred(); 
    	$("#fecharemitente, #fecharecibidoupev, #fecharecibidodtve").closest('div.form-group').find('.with-errors').html(" ");
    	$("#fecharemitente, #fecharecibidoupev, #fecharecibidodtve").closest('div.form-group').removeClass("has-error");
    	//$("#fecharecibidoupev, #fecharecibidodtve").closest('div.form-group').removeClass("has-danger");
		
		 if ( $("#fecharemitente").val().length==0){
        	$("#fecharemitente").closest('div.form-group').find('.with-errors').html("Es necesario indicar la fecha del oficio")
        	$("#fecharemitente").closest('div.form-group').addClass('has-error');
		} else {
				
		        if ( moment($("#fecharemitente").val()).isAfter( $("#fecharecibidoupev").val()) ){
		        	$("#fecharecibidoupev").closest('div.form-group').find('.with-errors').html("No se puede recibir un oficio antes de la fecha de emisión del mismo");
		        	$("#fecharecibidoupev").closest('div.form-group').addClass('has-error');
		        }  
		
		        if ( moment($("#fecharemitente").val()).isAfter( $("#fecharecibidodtve").val()) ){
		        	$("#fecharecibidodtve").closest('div.form-group').addClass('has-error').find('.with-errors').html("No se puede recibir un oficio en el DTVE antes de la fecha de emisión del mismo").addClass('has-error');
		        	$("#fecharecibidodtve").closest('div.form-group').addClass('has-error');
		        }
		        
		        if ( moment($("#fecharecibidoupev").val()).isAfter( $("#fecharecibidodtve").val()) ){
		        	$("#fecharecibidodtve").closest('div.form-group').find('.with-errors').html("No se puede recibir un oficio en el DTVE antes de la fecha de recepción en la DEV").addClass('has-error');
		        	$("#fecharecibidodtve").closest('div.form-group').addClass('has-error');
		        }
        }
       	d.resolve();
       	return d.promise();	
    }


function validarTituloDescripcion(){
	if (!$("#titulo").val()  &&  !$("#descripcion").val() ){
    	$("#titulo, #descripcion").closest('div.form-group').find('.with-errors').html("Se debe incluir al menos el título o la descripción");
    	$("#titulo, #descripcion").closest('div.form-group').addClass('has-error');		
	} else {
		$("#titulo, #descripcion").closest('div.form-group').find('.with-errors').html("");
    	$("#titulo, #descripcion").closest('div.form-group').removeClass('has-error');		
	}	
}

function validarURSol(){
	console.log("urremitente_id option:selected).val() "+$("#urremitente_id option:selected").val())
	if ($("#urremitente_id option:selected").val()==""){
		$("#urremitente_id").closest('div.form-group').find('.with-errors').html("Debe indicar cual es la UR solicitante");
    	$("#urremitente_id").closest('div.form-group').addClass('has-error');
	} else {
		$("#urremitente_id").closest('div.form-group').find('.with-errors').html("");
    	$("#urremitente_id").closest('div.form-group').removeClass('has-error');		
	}	
}


function validarServiciosSolicitados(){
    console.log( $("input[name='auxServicios[]']:checked").length  )
    if ( $("input[name='auxServicios[]']:checked").length==0){
    	$("input[name='auxServicios[]']:first").closest('div.form-group').find('.with-errors').html("Debe seleccionar al menos un servicio");
    	$("input[name='auxServicios[]']:first").closest('div.form-group').addClass("has-error");
    	$("input[name='auxServicios[]']:first").closest('div.form-group').addClass("has-danger");        	
    } else {
    	$("input[name='auxServicios[]']:first").closest('div.form-group').find('.with-errors').html("");
    	$("input[name='auxServicios[]']:first").closest('div.form-group').removeClass("has-error").removeClass("has-danger");            	
    }    	
}

class validacionCompleta {
    constructor() {
        this.datos = function() {
            var d = $.Deferred();
            var $fechas = validarFechas();
            var $generales = validarGrales();
            $.when($fechas, $generales).then(function($fechas, $generales) {
                d.resolve();
            });
            return d.promise();
        };
    }
}


$("[name='oficioArchivo']").click(function(){
    console.log("***************")
    toggleOpcion(this);
});


function toggleOpcion(e){
    $("div[name='oficioArchivo']").css("font-weight", "normal");
    $("div[name='oficioArchivo']").css("border-bottom","3px solid #F2F2F2");
    $(e).css("font-weight", "bold");
    $(e).css("border-bottom","3px solid "+$(e).css("color"));

    console.log("e.id:"+e.id)
    $("[name='divArchivoVista']").addClass("hide")
    $("#vista"+e.id).removeClass("hide");
}


// Funcion generica para subir los archivos
$("input[id^='oficioArchivo']").change(function() {
      console.log("función genérica")
      var posfijo = this.id.substring(13);
      console.log("002");
      console.log(posfijo)
      filename = this.files[0].name;
      $("#nombreArchivo"+posfijo).val(filename);
      console.log(filename);
      $("#btnSubirArchivo"+posfijo).blur();
      $("#btnVerArchivo"+posfijo).css("display","none");
});


function listaArchTipo(tipo){
    console.debug("Desde listaArchTipo "+tipo)
    $("[id^='archivos']").fadeOut("slow","swing");
    $("[id^='archivos']").css("display","none");
    $("#archivos"+tipo).fadeIn("slow","swing");
    $("#listaTiposArchivos strong>a").unwrap();
    $("#listaTiposArchivos a:eq("+(tipo-1)+")").wrap("<strong></strong>");
}


function edoInicialArchivos(oficioId){
    var $archs = LlamadaAjax("/oficioArchivos", "POST",  JSON.stringify({"oficioId":oficioId}) );
    var auxRetorno = "";
    $.when($archs).done(function(data){
        var retorno="";
        var nomTipoArchivo;
        var numTipoArchivo;
        console.debug("regresando oficioArchivos ...." )
        console.dir(data)

        // imagen (oficio digitalizado) - oneToMany
        numTipoArchivo = 1;
        var agregados = 0;
        if (data.imagen!=undefined && data.imagen.length>0){
            $("#archivos"+numTipoArchivo).css("display","none");
            data.imagen.forEach(function(a){
                var id = a.id;
                var nomArchivo = a.nombrearchivo;
                clonarBaseArchivo(numTipoArchivo, nomArchivo, id);
                agregados++;
            });
        }
        if (data.imagen==undefined){
             $("#ajaxEdoInicialArchivos").append( "<div id='archivos"+numTipoArchivo+"' style='display:none'>       <div class='row' style='margin-bottom:0.5em'><div class='col-xs-12 col-sm-12 col-md-12 col-lg-12'>"+tiposArchivos[numTipoArchivo-1]+"</div></div></div>"  );
             $("#archivos"+numTipoArchivo).append("<div class='row'><div class='col-xs-12 col-sm-12 col-md-12 col-lg-12' style='text-align:center;'><div name='divNoHay'>No se han subido archivos<div class='center-block'><a href='javascript:subirArchivoNuevo("+numTipoArchivo+")' style='cursor:hand'><i class='fas fa-2x  fa-file-upload'></i></a></div></div> </div></div>");
        }
        $("#archivos"+numTipoArchivo).append(botonAgregarMas(numTipoArchivo, agregados));



        // respuesta (oficio de respuesta) - oneToMany
        numTipoArchivo = 2;
        var agregados = 0;
        if (data.respuesta!=undefined && data.respuesta.length>0){
            $("#archivos"+numTipoArchivo).css("display","none");
            data.respuesta.forEach(function(a){
                console.dir(a)
                var id = a.id;
                var nomArchivo = a.nombrearchivo;
                clonarBaseArchivo(numTipoArchivo, nomArchivo, id);
                agregados++;
            });
        }
        if (data.respuesta==undefined){
             $("#ajaxEdoInicialArchivos").append( "<div id='archivos"+numTipoArchivo+"' style='display:none'>       <div class='row' style='margin-bottom:0.5em'><div class='col-xs-12 col-sm-12 col-md-12 col-lg-12'>"+tiposArchivos[numTipoArchivo-1]+"</div></div></div>"  );
             $("#archivos"+numTipoArchivo).append("<div class='row'><div class='col-xs-12 col-sm-12 col-md-12 col-lg-12' style='text-align:center;'><div name='divNoHay'>No se han subido archivos<div class='center-block'><a href='javascript:subirArchivoNuevo("+numTipoArchivo+")' style='cursor:hand'><i class='fas fa-2x  fa-file-upload'></i></a></div></div> </div></div>");
        }
        $("#archivos"+numTipoArchivo).append(botonAgregarMas(numTipoArchivo, agregados));


        // minuta - OneToMany
        numTipoArchivo = 3;
        var agregados = 0;
        console.debug("data.minuta "+data.minuta)
        if (data.minuta!=undefined && data.minuta.length>0){
            data.minuta.forEach(function(a){
                console.dir(a)
                var id = a.id;
                var nomArchivo = a.nombrearchivo;
                clonarBaseArchivo(numTipoArchivo, nomArchivo,id);
                agregados++;
            });
        }
        if (data.minuta==undefined){
             $("#ajaxEdoInicialArchivos").append( "<div id='archivos"+numTipoArchivo+"' style='display:none'>       <div class='row' style='margin-bottom:0.5em'><div class='col-xs-12 col-sm-12 col-md-12 col-lg-12'>"+tiposArchivos[numTipoArchivo-1]+"</div></div></div>"  );
             $("#archivos"+numTipoArchivo).append("<div class='row'><div class='col-xs-12 col-sm-12 col-md-12 col-lg-12' style='text-align:center;'><div name='divNoHay'>No se han subido archivos<div class='center-block'><a href='javascript:subirArchivoNuevo("+numTipoArchivo+")' style='cursor:hand'><i class='fas fa-2x  fa-file-upload'></i></a></div></div> </div></div>");
        }
        $("#archivos"+numTipoArchivo).append(botonAgregarMas(numTipoArchivo, agregados));



        // guion o escaleta - OneToMany
        numTipoArchivo = 4;
        var agregados = 0;
        if (data.guion!=undefined && data.guion.length>0){
            data.guion.forEach(function(a){
                var id = a.id;
                var nomArchivo = a.nombrearchivo;
                clonarBaseArchivo(numTipoArchivo, nomArchivo, id);
                agregados++;
            });
        }
        if (data.guion==undefined){
             $("#ajaxEdoInicialArchivos").append( "<div id='archivos"+numTipoArchivo+"' style='display:none'>       <div class='row' style='margin-bottom:0.5em'><div class='col-xs-12 col-sm-12 col-md-12 col-lg-12'>"+tiposArchivos[numTipoArchivo-1]+"</div></div></div>"  );
             $("#archivos"+numTipoArchivo).append("<div class='row'><div class='col-xs-12 col-sm-12 col-md-12 col-lg-12' style='text-align:center;'><div name='divNoHay'>No se han subido archivos<div class='center-block'><a href='javascript:subirArchivoNuevo("+numTipoArchivo+")' style='cursor:hand'><i class='fas fa-2x  fa-file-upload'></i></a></div></div> </div></div>");
        }
        $("#archivos"+numTipoArchivo).append(botonAgregarMas(numTipoArchivo, agregados));

        // entrada y salida de materiales - OneToOne
        numTipoArchivo = 5;
        var agregados = 0;
        if (data.entrada!=undefined){
            data.entrada.forEach(function(a){
                console.debug("   -> "+data.entrada[0].nombrearchivo)
                console.dir(data.entrada)
                var id = a.id;
                var nomArchivo = a.nombrearchivo;
                clonarBaseArchivo(numTipoArchivo, nomArchivo, id);
                agregados++;
            });
        }
        if (data.entrada==undefined){
             $("#ajaxEdoInicialArchivos").append( "<div id='archivos"+numTipoArchivo+"' style='display:none'>       <div class='row' style='margin-bottom:0.5em'><div class='col-xs-12 col-sm-12 col-md-12 col-lg-12'>"+tiposArchivos[numTipoArchivo-1]+"</div></div></div>"  );
             $("#archivos"+numTipoArchivo).append("<div class='row'><div class='col-xs-12 col-sm-12 col-md-12 col-lg-12' style='text-align:center;'><div name='divNoHay'>No se han subido archivos<div class='center-block'><a href='javascript:subirArchivoNuevo("+numTipoArchivo+")' style='cursor:hand'><i class='fas fa-2x  fa-file-upload'></i></a></div></div> </div></div>");
        }
        $("#archivos"+numTipoArchivo).append(botonAgregarMas(numTipoArchivo, agregados));

        // 6 bitacora - OneToMany
        numTipoArchivo =6;
        var agregados = 0;
        console.debug("BITACORA "+data.bitacora)
        if (data.bitacora!=undefined && data.bitacora.length>0){
            data.bitacora.forEach(function(a){
                var id = a.id;
                var nomArchivo = a.nombrearchivo;
                clonarBaseArchivo(numTipoArchivo, nomArchivo, id);
                agregados++;
            });
        }
        if (data.bitacora==undefined){
             $("#ajaxEdoInicialArchivos").append( "<div id='archivos"+numTipoArchivo+"' style='display:none'>       <div class='row' style='margin-bottom:0.5em'><div class='col-xs-12 col-sm-12 col-md-12 col-lg-12'>"+tiposArchivos[numTipoArchivo-1]+"</div></div></div>"  );
             $("#archivos"+numTipoArchivo).append("<div class='row'><div class='col-xs-12 col-sm-12 col-md-12 col-lg-12' style='text-align:center;'><div name='divNoHay'>No se han subido archivos<div class='center-block'><a href='javascript:subirArchivoNuevo("+numTipoArchivo+")' style='cursor:hand'><i class='fas fa-2x  fa-file-upload'></i></a></div></div> </div></div>");
        }
        $("#archivos"+numTipoArchivo).append(botonAgregarMas(numTipoArchivo, agregados));


        // 7 evidencia de entrega - OneToMany
        numTipoArchivo =7;
        var agregados = 0;
        if (data.evidencia!=undefined && data.evidencia.length>0){
            data.evidencia.forEach(function(a){
                var id = a.id;
                var nomArchivo = a.nombrearchivo;
                clonarBaseArchivo(numTipoArchivo, nomArchivo, id);
                agregados++;
            });
        }
        if (data.evidencia==undefined){
            $("#ajaxEdoInicialArchivos").append( "<div id='archivos"+numTipoArchivo+"' style='display:none'>       <div class='row' style='margin-bottom:0.5em'><div class='col-xs-12 col-sm-12 col-md-12 col-lg-12'>"+tiposArchivos[numTipoArchivo-1]+"</div></div></div>"  );
             $("#archivos"+numTipoArchivo).append("<div class='row'><div class='col-xs-12 col-sm-12 col-md-12 col-lg-12' style='text-align:center;'><div name='divNoHay'>No se han subido archivos<div class='center-block'><a href='javascript:subirArchivoNuevo("+numTipoArchivo+")' style='cursor:hand'><i class='fas fa-2x  fa-file-upload'></i></a></div></div> </div></div>");
        }
        $("#archivos"+numTipoArchivo).append(botonAgregarMas(numTipoArchivo, agregados));

        // 8 encuesta de satisfacción - OneToMany
        numTipoArchivo =8;
        var agregados = 0;
        if (data.encuesta!=undefined && data.encuesta.length>0){
            data.encuesta.forEach(function(a){
                var id = a.id;
                var nomArchivo = a.nombrearchivo;
                clonarBaseArchivo(numTipoArchivo, nomArchivo, id);
                agregados++;
            });
        }
        if (data.encuesta==undefined){
             $("#ajaxEdoInicialArchivos").append( "<div id='archivos"+numTipoArchivo+"' style='display:none'>       <div class='row' style='margin-bottom:0.5em'><div class='col-xs-12 col-sm-12 col-md-12 col-lg-12'>"+tiposArchivos[numTipoArchivo-1]+"</div></div></div>"  );
             $("#archivos"+numTipoArchivo).append("<div class='row'><div class='col-xs-12 col-sm-12 col-md-12 col-lg-12' style='text-align:center;'><div name='divNoHay'>No se han subido archivos<div class='center-block'><a href='javascript:subirArchivoNuevo("+numTipoArchivo+")' style='cursor:hand'><i class='fas fa-2x  fa-file-upload'></i></a></div></div> </div></div>");
        }
        $("#archivos"+numTipoArchivo).append(botonAgregarMas(numTipoArchivo, agregados));

        $("div[id^='archivos']").css("display","none");

    }); // fin del when
}


function eliminaArchivo(tipo, secuencia, idArchivo){
    console.debug("Desde eliminaArchivo "+tipo+"-"+secuencia)
    // Eliminar de la tabla
    $b = LlamadaAjax("/eliminaArchivoOficio", "POST", JSON.stringify({"tipo":tipo, "idArchivo": idArchivo}));
    $.when($b).done(function(data){
        console.dir(data)
        if (data.eliminado){
            e = $("#fileOficioArchivo-"+tipo+"-"+secuencia).closest("div.row");
            console.debug("e tam "+e.length)
            e.remove();
            var contador = $("input[type='file'][id^='fileOficioArchivo-"+tipo+"']").length;
            console.debug("contador "+contador)
            var auxTipo = tiposArchivos[tipo-1];
            // Elimina botón de agregar otro archivo
             $("#archivos"+tipo).find("button[name='btnAgregarOtroArchivo']").remove();
            if (contador ==0 ){
                 retorno="<div class='row'>"+
                                "<div class='col-xs-12 col-sm-12 col-md-12 col-lg-12' style='text-align:center;'><div name='divNoHay'>No se han subido archivos<div class='center-block'><a href='javascript:subirArchivoNuevo("+tipo+")' style='cursor:hand'><i class='fas fa-2x  fa-file-upload'></i></a></div></div> </div>" +
                            "</div>";
                 console.debug(retorno)
                 $("#archivos"+tipo).find("row:gt(1)").remove();
                 $("#archivos"+tipo).append(retorno);
             } else {
                $("#archivos"+tipo).append(  botonAgregarMas(tipo, contador)  );
             }
        } else {
                swal(
                        'Error',
                        'nO FUE POSIBLE ELIMINAR EL ARCHIVO',
                        'error'
                      )
        }

    });


}


function botonAgregarMas(tipo, cantidad){
    var retorno = "";
    console.debug("cantidad de file "+tipo+":"+cantidad)
    console.debug("tiposArchivosCardinalidad[tipo-1] "+tiposArchivosCardinalidad[tipo-1])
      if ( cantidad >0 && tiposArchivosCardinalidad[tipo-1]>1 && cantidad < tiposArchivosCardinalidad[tipo-1]){
        if (cantidad <= tiposArchivosCardinalidad[tipo-1]){
            retorno = "<div class='center-block'><button name='btnAgregarOtroArchivo' class='btn btn-sm btn-primary btn-block' type='button' style='margin-top:0.8em;' onclick='subirArchivoNuevo("+tipo+")'>Agregar otro archivo</button></div>";
        }
        //$("#archivos"+numTipoArchivo).append(boton);
      }
      console.log("botonAgregarMas regresa.... "+retorno)
    return retorno;
}