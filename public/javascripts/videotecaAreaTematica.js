$("#areatematicaDescripcion").off("keyup");
$("#areatematicaDescripcion").on("keyup", function(){
    console.debug("AreaTematica keyup! ")

    $("#msgCoincidenciasAreaTematica").html("");
    $("#divCoincidenciasAreaTematica div.list-group").html("");
    //$("#divCoincidenciasAreaTematica div.list-group button").remove();
    var cadena = $("#areatematicaDescripcion").val();
    $("#panelCoincidenciasAreaTematica").toggle(cadena.length>0);
    if (cadena.length >= 1 ){

        console.debug("cadena "+cadena)
        $("#btnNuevaAreaTematica").toggle(cadena.length>2);
        if (cadena.length==0){
            console.log("búsqueda vacía");
        } else {
            console.log("búsqueda NO vacía");

            var $f = LlamadaAjax("/textsearch", "POST", JSON.stringify({campo:"areatematica", cadena:cadena}));
            $.when($f).done(function(dataTS){
                    console.log("....")
                    console.dir(dataTS)
                    console.log("tam "+dataTS.coincidencias.length)
                    $("#panelCoincidenciasAreaTematica").show();
                    if (dataTS.coincidencias!=0){
                        $("#msgCoincidenciasAreaTematica").html("Se encontraron las siguientes "+dataTS.coincidencias.length+" coincidencias:");
                        $("#divCoincidenciasAreaTematica div.list-group").html("");
                        for(var c=0; c < dataTS.coincidencias.length; c++){
                            var aux = dataTS.coincidencias[c];
                            var comillasEscapadas = aux.descripcion.replace(/"/g, '&#34;');
                            $("#divCoincidenciasAreaTematica div.list-group").append( '<button type="button" class="list-group-item" onclick="javascript:seleccionaAreaTematica('+aux.id+', \''+comillasEscapadas+'\')">'+ aux.descripcion+ '</button>');
                        }
                        //$("#divCoincidenciasAreaTematica").append("<span class='small'>¿Nueva <i>área temática</i>?, <a href='javascript:void(0);' onclick='javascript: $('#btnNuevaAreaTematica').click();  ' >agréguela  </a> </span>");
                    } else {
                       // $("#msgCoincidenciasAreaTematica").html("No se encontraron coincidencias para el área temática.<br><br>Si se trata de una nueva área temática, oprima el siguiente botón para agregarla<br><br> <div style='text-align:center;'>  <input class='btn btn-default' type='button' role='button' id='btnNuevaAreaTematica'  value='Nueva Área Temática'></div>");
                        $("#msgCoincidenciasAreaTematica").html("No se encontraron coincidencias para el área temática.<br><br>Si se trata de una nueva área temática, oprima el siguiente botón para agregarla");

                    }

                });
        }
    }
});


function abrirAreas(){
    console.log("nadaaaaaaaa")
    $("#divBusquedaAreaTematica, #divCoincidenciasAreaTematica, #msgCoincidenciasAreaTematica").show();
    $("#divResultadoBusquedaAreaTematica").hide();
    $("#areatematicaDescripcion").val(   $("#textAreaTematica").html()  );
    $("#areatematicaDescripcion").keyup();
}


$(document).off("click", "#btnNuevaAreaTematica");
$(document).on("click", "#btnNuevaAreaTematica", function(e){
    console.log("Desde btnNuevaAreaTematica.click")
    e.preventDefault();
    $("#divBusquedaAreaTematica, #divCoincidenciasAreaTematica, #msgCoincidenciasAreaTematica").show();
    $("#divResultadoBusquedaAreaTematica").hide();
    var laNueva = $("#areatematicaDescripcion").val();
    var $f = LlamadaAjax("/textsearchCampoCompleto", "POST", JSON.stringify({campo:"areatematica",cadena:laNueva}));
    $.when($f).done(function(dataTS){
        console.log("Hay coincidencias? "+   (dataTS.coincidencias.length>0))
        console.dir(dataTS)
        if (dataTS.coincidencias>0){
                swal({
                        title:'No se realizó la operación',
                        html:'No se puede agregar la areatematica porque ya existe<br><br>Revise la lista de coincidencias y observará que ya existe. Puede usar la areatematica existente, solo selecciónela de la lista de coincidencias.<br>',
                        type:'warning',
                        confirmButtonText: "Aceptar"
                      }) ;
        } else {
            var $salva = LlamadaAjax("/nuevaAreaTematica","POST", JSON.stringify({descripcion:laNueva}));
            $.when($salva).done(function(salvado){
                console.log("salvado")
                console.dir(salvado)
                if (salvado.estado=="ok"){
                    swal({
                          title:  'Correcto',
                          html:  'Se agregó la nueva área temática.<br><br>Continúe con el formulario del acervo de la videoteca.',
                          type:  'success',
                          confirmButtonText: "Aceptar"
                      });

                    $("#areatematica_id").val(salvado.id);

                    $("#textAreaTematica").html(  salvado.descripcion);

                    $("#divResultadoBusquedaAreaTematica").show();
                    $("#divBusquedaAreaTematica, #msgCoincidenciasAreaTematica, #divCoincidenciasAreaTematica" ).hide();

                    agregarAbrir("areatematicaDescripcion");
                    $("label[for='areatematicaDescripcion'] span").eq(1).click(function(){
                        abrirAreas();
                    });
                    $("label[for='areatematicaDescripcion'] span").eq(1).show();

                } else {
                    alert("No fue posible agregar la Área Temática.");
                }
            });
        }
    });
});

// Cuando se da click a un elemento de la lista de coincidencias de areatematica
function seleccionaAreaTematica(id, texto){
    console.debug("Se seleccionó la areatematica id: "+id);
    $('#myModal2').modal('hide');
    //$('#areatematica_id').selectpicker('refresh');
    $('#areatematica_id').selectpicker('val', id);
    $('#areatematica_id').selectpicker('refresh');
    $("#divBusquedaAreaTematica, #divCoincidenciasAreaTematica, #msgCoincidenciasAreaTematica").hide();

    agregarAbrir("areatematicaDescripcion");
    $("label[for='areatematicaDescripcion'] span").eq(1).click(function(){
        abrirAreas();
    });
    $("label[for='areatematicaDescripcion'] span").eq(1).show();

    $("#divResultadoBusquedaAreaTematica").show();
    $("#areatematica_id").val(id);
    $("#textAreaTematica").html(  texto);
}