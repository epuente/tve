$("#btnModalGuardar").off("click");
$("#btnModalGuardar").on("click", function(){
    console.debug("click! ")
});

$("#serieDescripcion").off("keyup");
$("#serieDescripcion").on("keyup", function(){
    console.debug("keyup! ")
    $("#msgCoincidencias").html("");
    $("#divCoincidencias div.list-group button").remove();
    var cadena = $("#serieDescripcion").val();
    $("#btnNuevaSerie").toggle(cadena.length>1);
    if (cadena.length==0){
        console.log("búsqueda vacía");
    } else {
        var $f = LlamadaAjax("/textsearch", "POST", JSON.stringify({campo:"serie", cadena:cadena}));
        $.when($f).done(function(dataTS){
                console.log("....")
                console.dir(dataTS)
                console.log("tam "+dataTS.coincidencias.length)
                if (dataTS.coincidencias.length!=0){
                    $("#msgCoincidencias").html("Se encontraron las siguientes "+dataTS.coincidencias.length+" coincidencias:");
                    for(var c=0; c < dataTS.coincidencias.length; c++){
                        var aux = dataTS.coincidencias[c];
                        var comillasEscapadas = aux.descripcion.replace(/"/g, '&#34;');
                        $("#divCoincidencias div.list-group").append( '<button type="button" class="list-group-item" onclick="javascript:seleccionaSerie('+aux.id+', \''+comillasEscapadas+'\')">'+ aux.descripcion+ '</button>');
                    }
                } else {
                    $("#msgCoincidencias").html("No se encontraron coincidencias");
                }
            });
    }
});


function abrirSeries(){
    console.log("nadaaaaaaaa")
    $("#divBusqueda, #divCoincidencias, #msgCoincidencias, #divIndicaciones").show();
    $("#divResultadoBusqueda, #aAbrirSeries").hide();
    $("#serieDescripcion").val(   $("#textSerie").html()  );
    $("#serieDescripcion").keyup();
}


$("#btnNuevaSerie").off("click");
$("#btnNuevaSerie").on("click", function(e){
    console.log("abc")
    e.preventDefault();
    $("#divBusqueda, #divCoincidencias, #msgCoincidencias, #divIndicaciones").show();
    $("#divResultadoBusqueda").hide();
    var laNueva = $("#serieDescripcion").val();
    var $f = LlamadaAjax("/textsearchCampoCompleto", "POST", JSON.stringify({campo:"serie",cadena:laNueva}));
    $.when($f).done(function(dataTS){
        if (dataTS.coincidencias.length>0){
                swal({
                        title:'No se realizó la operación',
                        html:'No se puede agregar la serie porque ya existe<br><br>Revise la lista de coincidencias y observará que ya existe. Puede usar la serie existente, solo selecciónela de la lista de coincidencias.<br>',
                        type:'warning',
                        confirmButtonText: "Aceptar"
                      }) ;
        } else {
            var $salva = LlamadaAjax("/nuevaSerie","POST", JSON.stringify({descripcion:laNueva}));
            $.when($salva).done(function(salvado){
                console.log("salvado")
                console.dir(salvado)
                if (salvado.estado=="ok"){
                    swal({
                          title:  'Correcto',
                          html:  'Se agregó la nueva serie.<br><br>Continúe con el formulario del acervo de la videoteca.',
                          type:  'success',
                          confirmButtonText: "Aceptar"
                      });
                    $('#myModal2').modal('hide');
                    $("#serie_id").val(salvado.id);

                    $("#textSerie").html(  salvado.descripcion);

                    $("#divResultadoBusqueda, #aAbrirSeries").show();
                    $("#divBusqueda, #divIndicaciones, #msgCoincidencias, #btnNuevaSerie, #divCoincidencias" ).hide();




                } else {
                    alert("No fue posible agregar la serie.");
                }
            });
        }
    });


});

// Cuando se da click a un elemento de la lista de coincidencias de serie
function seleccionaSerie(id, texto){
    console.debug("Se seleccionó la serie id: "+id);
    $('#myModal2').modal('hide');
    //$('#serie_id').selectpicker('refresh');
    $('#serie_id').selectpicker('val', id);
    $('#serie_id').selectpicker('refresh');
    $("#divBusqueda, #divCoincidencias, #msgCoincidencias, #divIndicaciones, #btnNuevaSerie").hide();
    $("#divResultadoBusqueda, #aAbrirSeries").show();
    $("#serie_id").val(id);
    $("#textSerie").html(  texto);
}

function agregaJSON(){
    $("<textarea style='padding-left:100px' name='txaCreditos' id='txaCreditos'>Este es un texto de prueba que despues recibirá código JSON</textarea>").appendTo("#frmVTKCreate");
}





$("button[name='laBotoniza']").off("click");
$("button[name='laBotoniza']").on("click", function(e){
    e.preventDefault();
    console.log("laBotoniza")
    var indice = $("button[name='laBotoniza']").index( $(this)   );
    console.log( "indice "+indice  )
    console.log( $("button[name='laBotoniza']").eq(indice) )


    var id = $(this).prop("id").substring( $(this).prop("id").indexOf("_")+1   );
    console.log("CREDITOS "+id)
    //console.log("id->"+id)
    console.log(  $("#ta"+id).val() )



});



$("form").submit(function(event){
  //event.preventDefault();
    $("#cuentas_username").attr('name', 'cuentas[0].username');
    $("#cuentas_password").attr('name', 'cuentas[0].password');

    $("[type='checkbox'][id^='auxRoles_']:checked").each(function(i,e){
        $(this).attr("name","cuentas[0].roles["+i+"].rol.id");
    });
    var x = {};
    var j=[];
    $("#navTabs>li>a").each(function(index, element){
        var id = $(element).attr("id").substring(3);
        console.log("  id "+id)
        var tipo={};
        tipo["tipo"]=id;
        aux= $("#ta"+id).val();
        console.log("    aux:"+aux+"("+aux.length+")")
        if (aux.length!=0){


            obj = jQuery.parseJSON(aux);
            console.log("    obj:"+obj)

            cadena="";
            $.each(obj, function(key,value) {
                cadena+=value.value+",";
            });
            cadena = cadena.substring(0, cadena.length-1);
            console.log("    cadena:"+cadena)
            tipo["creditos"]=cadena;
            j.push(tipo);
        }
    });
    x['losDatos']=j;
    $("<textarea style='padding-left:100px' name='txaCreditos' id='txaCreditos'>"+JSON.stringify(x)+"</textarea>").appendTo("#frmVTKCreate");
    console.dir(j)
});



