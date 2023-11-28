$("#produccionDescripcion").off("keyup");
$("#produccionDescripcion").on("keyup", function(){
    console.debug("Produccion keyup! ")
    $("#msgCoincidenciasProduccion").html("");
    $("#divCoincidenciasProduccion div.list-group").html("");
    //$("#divCoincidenciasProduccion div.list-group button").remove();
    var cadena = $("#produccionDescripcion").val();
    $("#panelCoincidenciasProduccion").toggle(cadena.length>0);
    if (cadena.length >= 1 ){
        console.debug("cadena "+cadena)
        //$("#btnNuevaProduccion").toggle(cadena.length>2);
        $("#btnNuevaProduccion").show();
        if (cadena.length==0){
            console.log("búsqueda vacía");
        } else {
            console.log("búsqueda NO vacía");

            var $f = LlamadaAjax("/textsearch", "POST", JSON.stringify({campo:"produccion", cadena:cadena}));
            $.when($f).done(function(dataTS){
                    console.log("....")
                    console.dir(dataTS)
                    console.log("tam "+dataTS.coincidencias.length)
                     $("#panelCoincidenciasProduccion").show();
                    if (dataTS.coincidencias.length!=0){
                        $("#msgCoincidenciasProduccion").html("Se encontraron las siguientes "+dataTS.coincidencias.length+" coincidencias:");
                        $("#divCoincidenciasProduccion div.list-group").html("");
                        for(var c=0; c < dataTS.coincidencias.length; c++){
                            var aux = dataTS.coincidencias[c];
                            var comillasEscapadas = aux.descripcion.replace(/"/g, '&#34;');
                            $("#divCoincidenciasProduccion div.list-group").append( '<button type="button" class="list-group-item" onclick="javascript:seleccionaProduccion('+aux.id+', \''+comillasEscapadas+'\')">'+ aux.descripcion+ '</button>');
                        }
                    } else {
                        $("#msgCoincidenciasProduccion").html("No se encontraron coincidencias para la producción.<br><br>Si se trata de una nueva producción, oprima el siguiente botón para agregarla<br><br> <div style='text-align:center;'>  <input class='btn btn-default' type='button' role='button' id='btnNuevaProduccion'  value='Nueva Producción'></div>");
                        $("#msgCoincidenciasProduccion").html("No se encontraron coincidencias para la producción.<br><br>Si se trata de una nueva producción, oprima el siguiente botón para agregarla");
                    }
                });
        }
    }
});


function abrirProducciones(){
    console.log("nadaaaaaaaa")
    $("#divBusquedaProduccion, #divCoincidenciasProduccion, #msgCoincidenciasProduccion").show();
    $("#divResultadoBusquedaProduccion").hide();
    $("#produccionDescripcion").val(   $("#textProduccion").html()  );
    $("#produccionDescripcion").keyup();
}

$(document).off("click", "#btnNuevaProduccion");
$(document).on("click", "#btnNuevaProduccion",function(e){
    console.log("Desde btnNuevaProduccion.click")
    e.preventDefault();
    $("#divBusquedaProduccion, #divCoincidenciasProduccion, #msgCoincidenciasProduccion").show();
    $("#divResultadoBusquedaProduccion").hide();
    var laNueva = $("#produccionDescripcion").val();
    var $f = LlamadaAjax("/textsearchCampoCompleto", "POST", JSON.stringify({campo:"produccion",cadena:laNueva}));
    $.when($f).done(function(dataTS){
        console.log("Hay coincidencias? "+   (dataTS.coincidencias.length>0))
        console.dir(dataTS)
        if (dataTS.coincidencias>0){
                swal({
                        title:'No se realizó la operación',
                        html:'No se puede agregar la produccion porque ya existe<br><br>Revise la lista de coincidencias y observará que ya existe. Puede usar la produccion existente, solo selecciónela de la lista de coincidencias.<br>',
                        type:'warning',
                        confirmButtonText: "Aceptar"
                      }) ;
        } else {
            var $salva = LlamadaAjax("/nuevaProduccion","POST", JSON.stringify({descripcion:laNueva}));
            $.when($salva).done(function(salvado){
                console.log("salvado")
                console.dir(salvado)
                if (salvado.estado=="ok"){
                    swal({
                          title:  'Correcto',
                          html:  'Se agregó la nueva produccion.<br><br>Continúe con el formulario del acervo de la videoteca.',
                          type:  'success',
                          confirmButtonText: "Aceptar"
                      });
                    $('#myModal2').modal('hide');
                    $("#produccion_id").val(salvado.id);

                    $("#textProduccion").html(  salvado.descripcion);

                    $("#divResultadoBusquedaProduccion").show();
                    $("#divBusquedaProduccion, #msgCoincidenciasProduccion, #divCoincidenciasProduccion" ).hide();

                    agregarAbrir("produccionDescripcion");
                    $("label[for='produccionDescripcion'] span").eq(1).click(function(){
                        abrirProducciones();
                    });
                    $("label[for='produccionDescripcion'] span").eq(1).show();

                } else {
                    alert("No fue posible agregar la produccion.");
                }
            });
        }
    });
});

// Cuando se da click a un elemento de la lista de coincidencias de produccion
function seleccionaProduccion(id, texto){
    console.debug("Se seleccionó la produccion id: "+id);
    $('#myModal2').modal('hide');
    //$('#produccion_id').selectpicker('refresh');
    $('#produccion_id').selectpicker('val', id);
    $('#produccion_id').selectpicker('refresh');
    $("#divBusquedaProduccion, #divCoincidenciasProduccion, #msgCoincidenciasProduccion").hide();

    agregarAbrir("produccionDescripcion");
    $("label[for='produccionDescripcion'] span").eq(1).click(function(){
        abrirProducciones();
    });
    $("label[for='produccionDescripcion'] span").eq(1).show();

    $("#divResultadoBusquedaProduccion").show();
    $("#produccion_id").val(id);
    $("#textProduccion").html(  texto);
}