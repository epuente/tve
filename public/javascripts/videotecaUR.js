$("#URDescripcion").off("keyup");
$("#URDescripcion").on("keyup", function(){
    console.debug("keyup URDescripcion! ")
    $("#msgCoincidenciasUR").html("");
   // $("#divCoincidenciasUR div.list-group button").remove();
    var cadena = $("#URDescripcion").val();
    $("#panelCoincidenciasUR").toggle(cadena.length>0);
    if (cadena.length >= 1 ){
        //$("#btnNuevaUR").toggle(cadena.length>2);
        $("#btnNuevaUR").show();
        if (cadena.length==0){
            console.log("búsqueda vacía");
        } else {
            var $f = LlamadaAjax("/textsearch", "POST", JSON.stringify({campo:"ur", cadena:cadena}));
            $.when($f).done(function(dataTS){
                    console.log("....")
                    console.dir(dataTS)
                    console.log("tam "+dataTS.coincidencias.length)
                    $("#panelCoincidenciasUR").show();
                    if (dataTS.coincidencias.length!=0){
                        $("#msgCoincidenciasUR").html("Se encontraron las siguientes "+dataTS.coincidencias.length+" coincidencias:");
                        $("#divCoincidenciasUR div.list-group").html("");
                        for(var c=0; c < dataTS.coincidencias.length; c++){
                            var aux = dataTS.coincidencias[c];
                            var comillasEscapadas = aux.descripcion.replace(/"/g, '&#34;');
                            $("#divCoincidenciasUR div.list-group").append( '<button type="button" class="list-group-item" onclick="javascript:seleccionaUR('+aux.id+', \''+comillasEscapadas+'\')">'+ aux.descripcion+ '</button>');
                        }
                    } else {
                        //$("#msgCoincidenciasUR").html("No se encontraron coincidencias para la instancia solicitante.<br><br>Si se trata de una nueva instancia, oprima el siguiente botón para agregarla<br><br> <div style='text-align:center;'>  <button class='btn btn-sm btn-primary'>Agregarla</button></div>");

                        $("#msgCoincidenciasUR").html("No se encontraron coincidencias para la instancia solicitante.<br><br>Si se trata de una nueva instancia, oprima el siguiente botón para agregarla");
                    }
                });
        }
    }
});


function abrirURs(){
    console.log("nadaaaaaaaa")
    $("#panelCoincidenciasUR, #divBusquedaUR, #divCoincidenciasUR, #msgCoincidenciasUR").show();
    $("#divResultadoBusquedaUR, #aabrirURs").hide();
    $("#URDescripcion").val(   $("#textUR").html()  );
    $("#URDescripcion").keyup();
}


$(document).off("click", "#btnNuevaUR");
$(document).on("click", "#btnNuevaUR", function(e){
    console.log("abc")
    e.preventDefault();
    $("#divBusquedaUR, #divCoincidenciasUR, #msgCoincidenciasUR").show();
    $("#divResultadoBusquedaUR").hide();
    var laNueva = $("#URDescripcion").val();
    var $f = LlamadaAjax("/textsearchCampoCompleto", "POST", JSON.stringify({campo:"ur", cadena:laNueva}));
    $.when($f).done(function(dataTS){
        console.dir(dataTS)
        console.log(dataTS.coincidencias>0)
        if (dataTS.coincidencias>0){
                swal({
                        title:'No se realizó la operación',
                        html:'No se puede agregar la Instancia porque ya existe<br><br>Revise la lista de coincidencias y observará que ya existe. Puede usar la Instancia existente, solo selecciónela de la lista de coincidencias.<br>',
                        type:'warning',
                        confirmButtonText: "Aceptar"
                      }) ;
        } else {
            console.log("se va por el else")
            var $salva = LlamadaAjax("/nuevaUR","POST", JSON.stringify({nombreLargo:laNueva}));
            $.when($salva).done(function(salvado){
                console.log("salvado")
                console.dir(salvado)
                if (salvado.estado=="ok"){
                    swal({
                          title:  'Correcto',
                          html:  'Se agregó la nueva Instancia.<br><br>Continúe con el formulario del acervo de la videoteca.',
                          type:  'success',
                          confirmButtonText: "Aceptar"
                      });
                    $('#myModal2').modal('hide');
                    $("#unidadresponsable_id").val(salvado.id);

                    $("#textUR").html(  salvado.descripcion);
                    $("#divResultadoBusquedaUR").show();
                    $("#divBusquedaUR, #msgCoincidenciasUR, #divCoincidenciasUR" ).hide();

                    agregarAbrir("URDescripcion");
                    $("label[for='URDescripcion'] span").eq(1).click(function(){
                        abrirURs();
                    });
                    $("label[for='URDescripcion'] span").eq(1).show();

                } else {
                    alert("No fue posible agregar la Instancia.");
                }
            });
        }
    });


});

// Cuando se da click a un elemento de la lista de coincidencias de urs
function seleccionaUR(id, texto){
    console.debug("Se seleccionó la ur id: "+id+" - "+texto);
    $('#myModal2').modal('hide');
    $('#unidadresponsable_id').selectpicker('val', id);
    $('#unidadresponsable_id').selectpicker('refresh');
    $("#divBusquedaUR, #divCoincidenciasUR, #msgCoincidenciasUR").hide();

    agregarAbrir("URDescripcion");
    $("label[for='URDescripcion'] span").eq(1).click(function(){
        abrirURs();
    });
    $("label[for='URDescripcion'] span").eq(1).show();

    $("#divResultadoBusquedaUR").show();
    $("#unidadresponsable_id").val(id);
    $("#textUR").html(  texto);
}
