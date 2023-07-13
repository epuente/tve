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
        
$("iframe[name='visoriFrame']").on("load",  function(){
    console.log ("iframe cargado.......")
    console.log(  $("#title")   )
    $("iframe[name='visoriFrame']").find("#title").html("jajajaj");

});

        
        
        
        