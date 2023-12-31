    //Valida campos para cantidad que solo acepten números diferentes a cero
    $(document).off("keydown", ".soloNumeros");
    $(document).on("keydown", ".soloNumeros", function (e) {
        // Allow: backspace, delete, tab, escape, enter and .
        if ($.inArray(e.keyCode, [46, 8, 9, 27, 13, 110, 190]) !== -1 ||
             // Allow: Ctrl+A, Command+A
            (e.keyCode === 65 && (e.ctrlKey === true || e.metaKey === true)) ||
             // Allow: home, end, left, right, down, up
            (e.keyCode >= 35 && e.keyCode <= 40)) {
                 // let it happen, don't do anything
                 return;
        }
            // Ensure that it is a number and stop the keypress
            if ((e.shiftKey || (e.keyCode < 49 || e.keyCode > 57)) && (e.keyCode < 96 || e.keyCode > 105)) {
                $(this).popover({
                  title: "Error de validación",
                  content: "Este campo solo acepta números diferentes a cero.",
                  placement: "top"
                }).blur(function(){$(this).popover('hide')});
                $(this).parents('.form-group').addClass('has-error');
                $(this).popover('show');
                e.preventDefault();
            } else {
                $(this).parent().removeClass('has-error');
                $(this).parents('.form-group').removeClass('has-error');
                $(this).popover('hide');
            }
        });


    
    //Valida campos para cantidad que solo acepten números incluyendo al cero
    $(document).off("keydown", ".soloNumerosCero");
    $(document).on("keydown", ".soloNumerosCero", function (e) {
        // Allow: backspace, delete, tab, escape, enter and .
        if ($.inArray(e.keyCode, [46, 8, 9, 27, 13, 110, 190]) !== -1 ||
             // Allow: Ctrl+A, Command+A
            (e.keyCode === 65 && (e.ctrlKey === true || e.metaKey === true)) ||
             // Allow: home, end, left, right, down, up
            (e.keyCode >= 35 && e.keyCode <= 40)) {
                 // let it happen, don't do anything
                 return;
        }
            // Ensure that it is a number and stop the keypress
            if ((e.shiftKey || (e.keyCode < 48 || e.keyCode > 57)) && (e.keyCode < 95 || e.keyCode > 105)) {
                $(this).popover({
                  title: "Error de validación",
                  content: "Este campo solo acepta números.",
                  placement: "top",
                  clase:"soloNumerosCero"
                }).blur(function(){$(this).popover('hide')});
                $(this).parents('.form-group').addClass('has-error');
                $(this).popover('show');
                e.preventDefault();
            } else {
                $(this).parent().removeClass('has-error');
                $(this).parents('.form-group').removeClass('has-error');
                //$(this).popover('hide');
                $(this).popover('destroy');
            }
        });    



    
        // Valida campos de tipo hora
        $(document).off("blur", ".soloHora");
        $(document).on("blur", ".soloHora", function () {
            if ($(this).val()){                
            	if (!/^(?:\d|[01]\d|2[0-3]):[0-5]\d$/.test($(this).val())) {
                    $(this).popover({
                      title: "Error de validación",
                      content: "Este campo solo acepta hora en formato hh:mm",
                      placement: "top",
                      clase:"soloHora"
                    });
                    $(this).parent().addClass('has-error');
                    $(this).popover('show');
                } else {
                    $(this).parent().removeClass('has-error');
                   // $(this).popover('hide');
                    $(this).popover('destroy');
                }
            }
        });
        
        
        
        
        //Valida el tamaño del campo
        //El campo requiere tener el attributo maxlength
        //Se mostrará un popover cuando se iguale o sobrepase el 90% del maxlength
        $(document).off("keyup",".validaLongitud");
        $(document).on("keyup",".validaLongitud",function(){
        	var maximo =    $(this).prop("maxlength");
        	var actual = $(this).val().length;
        	var disponibles = maximo-actual;
        	var PC90 = Math.floor((90*maximo)/100);
 
        	console.log("longitudCampo: ",actual)
        	console.log("maximo: ",maximo)
        	console.log("quedan: ", disponibles)
        	console.log("PC90:", PC90)
        	
        	if ( $(this).data("bs.popover")==undefined ){
                $(this).popover({
                    title: "Aviso",
                    content: "Calculando...",
                    placement: "top",
                    trigger: 'focus',
                    clase:"validaLongitud"
                  });          		
                console.log("nuevo")
        	}
        	
        	console.dir( $(this).data("bs.popover") )
        	if ( $(this).data("bs.popover").options.clase=="validaLongitud"){
        		//console.log("es validaLongitud")
            	if ( actual>=PC90){
            		$(this).attr('data-content', "Quedan "+disponibles+" caracteres para este campo");        		
                    $(this).popover('show');       
                    console.log("supera el 90%")
            	} else {
            		$(this).popover('destroy');
            		//console.log("destroy")
            	}        		
        	} else{
        		//console.log("la clase es: ",$(this).data("bs.popover").options.clase)
        		//console.log("content: ",$(this).data("bs.popover").options.content)
        		$(this).attr('data-content', $(this).data("bs.popover").options.content);
        		 $(this).popover('show');
        	}
        });
        
        
        
    // Visor de un solo archivo del oficio
    function verOficio(idImagen, sufijo){
	    console.log("desde verOficio")
		$("#divMsgModal").css("display","block");
        fetch('/verOficio?id='+idImagen+'&sufijo='+sufijo)
          .then(response => {
            const contentType = response.headers.get('content-type');
            if (contentType.includes('image') || contentType.includes('pdf') || contentType.includes('text') || contentType.includes('audio') || contentType.includes('video')) {
                $("iframe[name='visoriFrame']").attr("src", "/verOficio?id="+idImagen+"&sufijo="+sufijo);
                $("#divMsgModal").css("display","none");
                $("#myModalVisorArchivo").modal('show');
            } else {
                $("iframe[name='visoriFrame']").attr("src", "/verOficio?id="+idImagen+"&sufijo="+sufijo);
                $.notify({
                    message: "<div clas='row'>"+
                             "  <div class='col-sm-1'><i class=\"fas fa-download fa-2x\"></i></div>"+
                             "  <div class='col-sm-11'>El archivo no puede ser visualizado en el navegador, pero es <strong>posible descargarlo</strong> a su equipo.</div>"+
                             "</div>",

                    type:"info",
                    delay: 10000
                });
                $("#divMsgModal").css("display","none");
                $("#myModalVisorArchivo").modal('hide');
            }
          })
		//$("iframe[name='visoriFrame']").attr("src", "/verOficio?id="+idImagen+"&sufijo="+sufijo);
	}

    // Visor de archivos externos (archivos que no se han guardado en la DB)
    // Visor de un solo archivo del oficio
    function verArchivoExterno(archivo){
	    console.log("desde verArchivoExterno")
		$("#divMsgModal").css("display","block");
        fetch('/pruebaPdfExterno?archivo='+archivo)
          .then(response => {
            const contentType = response.headers.get('content-type');
            if (contentType.includes('image') || contentType.includes('pdf') || contentType.includes('text') || contentType.includes('audio') || contentType.includes('video')) {
                $("iframe[name='visoriFrame']").attr("src", response);
                $("#divMsgModal").css("display","none");
                $("#myModalVisorArchivo").modal('show');
            } else {
                $("iframe[name='visoriFrame']").attr("src", response);
                $.notify({
                    message: "<div clas='row'>"+
                             "  <div class='col-sm-1'><i class=\"fas fa-download fa-2x\"></i></div>"+
                             "  <div class='col-sm-11'>El archivo no puede ser visualizado en el navegador, pero es <strong>posible descargarlo</strong> a su equipo.</div>"+
                             "</div>",
                    type:"info",
                    delay: 10000
                });
                $("#divMsgModal").css("display","none");
                $("#myModalVisorArchivo").modal('hide');
            }
          })
		//$("iframe[name='visoriFrame']").attr("src", "/verOficio?id="+idImagen+"&sufijo="+sufijo);
	}


     // Abre la ventana modal (visor de archvios con menú del lado izquierdo) y muestra los archivos relacionados al oficio
     function modalVerArchivosOficio(oficioId){
        console.log("Se recibió en ... "+oficioId)
        $("#tiposArchivos").html("");
        $archs = LlamadaAjax("/oficioArchivos", "POST", JSON.stringify({"oficioId":oficioId}));
        $.when($archs).done(function(data){
            console.dir(data)
            if (data.imagen!=undefined){
                $("#tiposArchivos").append("<div style='margin-bottom:-0.7em; '><h4>Oficio</h4></div>");
                data.imagen.forEach( function(i){
                    $("#tiposArchivos").append("<div style='margin-left:1em; line-height: 1.2;' data-gpoarchivo='modalListaArchivos'><a href=\"javascript:void(0)\" id=\"verOficioArchivo-"+i.id+"-imagen\">      <small>"+ i.nombrearchivo+"</small></div>");
                });
            }

            if (data.respuesta!=undefined){
                $("#tiposArchivos").append("<div style='margin-bottom:-0.7em; '><h4>Oficio de respuesta</h4></div>");
                data.respuesta.forEach( function(i){
                    $("#tiposArchivos").append("<div style='margin-left:1em; line-height: 1.2; letter-spacing: 0.01em;' data-gpoarchivo='modalListaArchivos'><a href=\"javascript:void(0)\" id=\"verOficioArchivo-"+i.id+"-Respuesta\">      <small>"+ i.nombrearchivo+"</small></a>");
                });
            }

            if (data.minuta!=undefined){
                $("#tiposArchivos").append("<div style='margin-bottom:-0.7em; '><h4>Minutas de acuerdos</h4></div>");
                data.minuta.forEach( function(i){
                    $("#tiposArchivos").append("<div style='margin-left:1em; line-height: 1.2; letter-spacing: 0.01em;' data-gpoarchivo='modalListaArchivos'><a href=\"javascript:void(0)\" id=\"verOficioArchivo-"+i.id+"-Minuta\">      <small>"+ i.nombrearchivo+"</small></div>");
                });
            }

            if (data.guion!=undefined){
                $("#tiposArchivos").append("<div style='margin-bottom:-0.7em; '><h4>Guión o escaleta</h4></div>");
                data.guion.forEach(function(i){
                    $("#tiposArchivos").append("<div style='margin-left:1em; line-height: 1.2; letter-spacing: 0.01em;' data-gpoarchivo='modalListaArchivos'><a href=\"javascript:void(0)\" id=\"verOficioArchivo-"+i.id+"-Guion\">      <small>"+ i.nombrearchivo+"</small></div>");
                });
            }

            if (data.entrada!=undefined){
                $("#tiposArchivos").append("<div style='margin-bottom:-0.7em; '><h4>Entradas y salidas de material</h4></div>");
                data.entrada.forEach(function(i){
                    $("#tiposArchivos").append("<div style='margin-left:1em; line-height: 1.2; letter-spacing: 0.01em;' data-gpoarchivo='modalListaArchivos'><a href=\"javascript:void(0)\" id=\"verOficioArchivo-"+i.id+"-Entrada\">      <small>"+ i.nombrearchivo+"</small></div>");
                });
            }

            if (data.bitacora!=undefined){
                $("#tiposArchivos").append("<div style='margin-bottom:-0.7em; '><h4>Bitácoras</h4></div>");
                data.bitacora.forEach(function(i){
                    $("#tiposArchivos").append("<div style='margin-left:1em; line-height: 1.2; letter-spacing: 0.01em;' data-gpoarchivo='modalListaArchivos'><a href=\"javascript:void(0)\" id=\"verOficioArchivo-"+i.id+"-Bitacora\">      <small>"+ i.nombrearchivo+"</small></div>");
                });
            }

            if (data.evidencia!=undefined){
                $("#tiposArchivos").append("<div style='margin-bottom:-0.7em; '><h4>Evidencia de entrega</h4></div>");
                data.evidencia.forEach(function(i){
                    $("#tiposArchivos").append("<div style='margin-left:1em; line-height: 1.2; letter-spacing: 0.01em;' data-gpoarchivo='modalListaArchivos'><a href=\"javascript:void(0)\" id=\"verOficioArchivo-"+i.id+"-Evidencia\">      <small>"+ i.nombrearchivo+"</small></div>");
                });
            }

            if (data.encuesta!=undefined){
                $("#tiposArchivos").append("<div style='margin-bottom:-0.7em; '><h4>Encuesta de satisfacción</h4></div>");
                data.encuesta.forEach(function(i){
                    $("#tiposArchivos").append("<div style='margin-left:1em; line-height: 1.2; letter-spacing: 0.01em;' data-gpoarchivo='modalListaArchivos'><a href=\"javascript:void(0)\" id=\"verOficioArchivo-"+i.id+"-Encuesta\">      <small>"+ i.nombrearchivo+"</small></div>");
                });
            }
        });
        $('#myModalLosVisores').modal('show');
        document.getElementsByName("visoriFrame2").innerHTML="TEXTO";
       // $("iframe[name='visoriFrame2']").replaceWith('<p>hi</p>');

        var iFrameDoc = $("iframe[name='visoriFrame2']")[0].contentDocument || $("iframe[name='visoriFrame2']")[0].contentWindow.document;
        var cadena = "<div style='text-align:center; margin-top:3em;'  class='text-muted'><h4>Elija del menú de la <strong>izquierda</strong> para visualizar los archivos relacionados al oficio</h4>";
        cadena+="<i class=\"far fa-hand-point-left fa-4x\"></i></div>";
        var estilos = "<link rel=\"stylesheet\" href=\"/assets/lib/bootstrap/css/bootstrap.min.css\" type=\"text/css\">";
        estilos+="<link rel=\"stylesheet\" href=\"/assets/lib/font-awesome/css/all.min.css\">";
        estilos+="<script defer src=\"/assets/lib/font-awesome/js/all.min.js\"></script>";
        iFrameDoc.write(estilos+cadena);
        iFrameDoc.close();

     }

    // Pone en negritas(tag strong) la opción seleccionada (archivo) de la lista en el visor de documentos de la ventana modal
    $(document).off("click", "div[data-gpoarchivo='modalListaArchivos']");
    $(document).on("click", "div[data-gpoarchivo='modalListaArchivos']", function(){
        console.log("OKKK")
        $("#tiposArchivos").find("strong>div[data-gpoarchivo='modalListaArchivos']").css("letter-spacing", "0.01em");
        $("#tiposArchivos").find("strong>div[data-gpoarchivo='modalListaArchivos']").unwrap();
        $(this).wrap("<strong></strong>");
        $(this).css("letter-spacing", "-0.02em");
    });

    // En la ventana modal...
    // Llama al método que regresa el contenido del archivo y lo despliega dentro del iframe (visoriFrame2)
    $(document).off("click", "[id^='verOficioArchivo']");
    $(document).on("click", "[id^='verOficioArchivo']", function(){
        console.debug("desde ^=verOficioArchivo click")
        var aux = this.id.split("-");
        var sufijo = aux[2];
        var id = aux[1];
        console.debug("sufijo: "+ sufijo)
        console.debug("id: "+ id)
        $("iframe[name='visoriFrame2']").attr("src", "/verOficio?id="+id+"&sufijo="+sufijo);
    });





        
        
        