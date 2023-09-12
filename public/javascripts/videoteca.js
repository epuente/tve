





$("#btnModalGuardar").on("click", function(){
    console.debug("click! ")
});

//$("#btnBusquedaSerie").on("click", function(){
$("#serieDescripcion").on("keyup", function(){
    texto =
    console.debug("click! ")
    $("#msgCoincidencias").html("");
    $("#divCoincidencias div.list-group button").remove();
    var cadena = $("#serieDescripcion").val();
    if (cadena.length==0){
        console.log("búsqueda vacía");
    } else {


        var $f = LlamadaAjax("/textsearch", "POST", JSON.stringify({cadena:cadena}));
        $.when($f).done(function(dataTS){
                console.log("....")
                console.dir(dataTS)
                console.log("tam "+dataTS.coincidencias.length)
                if (dataTS.coincidencias.length!=0){
                    $("#msgCoincidencias").html("Se encontraron "+dataTS.coincidencias.length+" coincidencias.</br>Si la serie que desea agregar aparece en la lista de coincidencias, elijala de la lista.</br>Si la serie es nueva y no aparece en la lista de coincidencias, oprima el botón <strong>nueva</strong>");
                    for(var c=0; c < dataTS.coincidencias.length; c++){
                        var aux = dataTS.coincidencias[c];
                        $("#divCoincidencias div.list-group").append( '<button type="button" class="list-group-item" onclick="javascript:seleccionaSerie('+aux.id+')">'+ aux.descripcion+ '</button>');
                    }
                } else {
                    $("#msgCoincidencias").html("No se encontraron coincidencias");

                }
            });



        /*
        var $f = LlamadaAjax("/tsQuery", "POST", JSON.stringify({cadena:cadena}));
        $.when($f).done(function(data){
            console.log("....")
            console.dir(data)
            if (data.tsquery.length>-1){
                //Lllamar otro Llamada ajax a tsCoincidencias
                var $g = LlamadaAjax("/tsCoincidencias", "POST", JSON.stringify( {cadena:data.tsquery} ));
                $.when($g).done(function(dataTS){
                    console.log("coincidencias");
                    console.dir(dataTS)
                    if (dataTS.coincidencias.length!=0){
                        $("#msgCoincidencias").html("Se encontraron "+dataTS.coincidencias.length+" coincidencias.</br>Si la serie que desea agregar aparece en la lista de coincidencias, elijala de la lista.</br>Si la serie es nueva y no aparece en la lista de coincidencias, oprima el botón <strong>nueva</strong>");
                        for(var c=0; c < dataTS.coincidencias.length; c++){
                            var aux = dataTS.coincidencias[c];
                            $("#divCoincidencias div.list-group").append( '<button type="button" class="list-group-item" onclick="javascript:alert('+aux.id+')">'+ aux.descripcion+ '</button>');
                        }
                    } else {
                        $("#msgCoincidencias").html("No se encontraron coincidencias");

                    }
                });


            }
        });
        */
    }
});



$("#btnNuevaSerie").on("click", function(){
    console.log("abc")
    var laNueva = $("#serieDescripcion").val();
    if (laNueva.length==0){
        alert("No ha escrito la descripción de la nueva serie");
        $("#serieDescripcion").focus();
    } else {
        var $f = LlamadaAjax("/textsearch", "POST", JSON.stringify({cadena:laNueva}));
        $.when($f).done(function(dataTS){
            if (dataTS.coincidencias.length>0){
                alert("Hay coincidencias");
            }
            var $salva = LlamadaAjax("/nuevaSerie","POST", JSON.stringify({descripcion:laNueva}));
            $.when($salva).done(function(salvado){
                console.dir(salvado)
                if (salvado.estado=="ok"){
                    alert("Se agregó correctamente.");
                    $('#myModal2').modal('hide');
                    $("#serie_id").find("option").remove().end();
                    $('#serie_id').selectpicker('refresh');

                    $recarga = LlamadaAjax("/serieList", "POST", JSON.stringify({}));
                    $.when($recarga).done(function(data){
                        console.dir(data)
                        for (d of data){
                            $('#serie_id').append("<option value='"+d.id+"'>"+d.descripcion+"</option>");
                        }
                     //   $('#serie_id option[value='+salvado.id+']').prop("selected", true);

                        $('#serie_id').selectpicker('refresh');
                        $('#serie_id').selectpicker('val', salvado.id);
                    });

                } else {
                    alert("No fue posible agregar la serie.");
                }
            });
        });
    }

});


function seleccionaSerie(id){
    console.debug("Se seleccionó la serie id: "+id);
    $('#myModal2').modal('hide');

    //$('#serie_id').selectpicker('refresh');
    $('#serie_id').selectpicker('val', id);

    $('#serie_id').selectpicker('refresh');
}

