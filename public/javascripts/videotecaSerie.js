$("#serieDescripcion").off("keyup");
$("#serieDescripcion").on("keyup", function(){
    console.debug("keyup! ")
    $("#msgCoincidencias").html("");
    $("#msgCoincidencias div.list-group").html("");
    //$("#divCoincidencias div.list-group button").remove();
    var cadena = $("#serieDescripcion").val();
    $("#panelCoincidenciasSerie").toggle(cadena.length>0);
    if (cadena.length >= 1 ){
        console.debug("cadena "+cadena)
        //$("#btnNuevaSerie").toggle(cadena.length>2);
        $("#btnNuevaSerie").show();
        if (cadena.length==0){
            console.log("búsqueda vacía");
        } else {
            console.log("búsqueda NO vacía");

            var $f = LlamadaAjax("/textsearch", "POST", JSON.stringify({campo:"serie", cadena:cadena}));
            $.when($f).done(function(dataTS){
                    console.log("....")
                    console.dir(dataTS)
                    console.log("tam "+dataTS.coincidencias.length)
                    $("#panelCoincidenciasSerie").show();
                    if (dataTS.coincidencias.length!=0){

                        $("#msgCoincidencias").html("Se encontraron las siguientes "+dataTS.coincidencias.length+" coincidencias:");
                        $("#divCoincidencias div.list-group").html("");
                        for(var c=0; c < dataTS.coincidencias.length; c++){
                            var aux = dataTS.coincidencias[c];
                            var comillasEscapadas = aux.descripcion.replace(/"/g, '&#34;');
                            $("#divCoincidencias div.list-group").append( '<button type="button" class="list-group-item" onclick="javascript:seleccionaSerie('+aux.id+', \''+comillasEscapadas+'\')">'+ aux.descripcion+ '</button>');
                        }
                    } else {
                        //$("#msgCoincidencias").html("No se encontraron coincidencias para la serie.<br><br>Si se trata de una nueva serie, oprima el siguiente botón para agregarla<br><br> <div style='text-align:center;'>  <input class='btn btn-default' type='button' role='button' id='btnNuevaSerie'  value='Neva Serie'></div>");
                        $("#msgCoincidencias").html("No se encontraron coincidencias para la serie.<br><br>Si se trata de una nueva serie, oprima el siguiente botón para agregarla");

                    }
                });
        }
    }
});


function abrirSeries(){
    console.log("nadaaaaaaaa")
    $("#divBusqueda, #divCoincidencias, #msgCoincidencias").show();
    $("#divResultadoBusqueda").hide();
    $("#serieDescripcion").val(   $("#textSerie").html()  );
    $("#serieDescripcion").keyup();
}


$(document).off("click","#btnNuevaSerie");
$(document).on("click","#btnNuevaSerie", function(e){
    const excepcionesSerieER=[/especiales/, /espesiales/, /espeziales/, /especialez/, /espesialez/, /espezialez/, /ezpezialez/,
                                                          /ezpeciales/, /ezpesiales/, /ezpeziales/, /ezpecialez/, /ezpesialez/,
                                                          /especial/, /espesial/, /espezial/,
                                                          /ezpecial/, /ezpesial/, /ezpezial/];
    var coincide = false;
    console.log("abc")
    e.preventDefault();
    var laNueva = $("#serieDescripcion").val().trim().toLowerCase();
    excepcionesSerieER.forEach(expresion => {
        //coincide = coincide && expresion.test(laNueva);

          if (expresion.test(laNueva)) {
            console.log("La expresión regular coincide con la cadena."+ laNueva.match(expresion));
            coincide = coincide || true;
          } else {
            console.log("La expresión regular no coincide con la cadena.");
            coincide = coincide || false;
          }

    });

    console.log("COINCIDE "+coincide)
    e.preventDefault();

    if (coincide){
        swal({
              title:  'Aviso',
              html:  'El nombre para la serie que desea agregar (<strong>'+laNueva+'</strong>) no es aceptado.<br><br>No se acepta <b>especial</b> ni <b>especiales</b>.<br><br>Sea más descriptivo en cuanto a la naturaleza de la serie.',
              type:  'warning',
              confirmButtonText: "Aceptar"
          });
          return false;
    }



    $("#divBusqueda, #divCoincidencias, #msgCoincidencias").show();
    $("#divResultadoBusqueda").hide();

    var $f = LlamadaAjax("/textsearchCampoCompleto", "POST", JSON.stringify({campo:"serie",cadena:laNueva}));
    $.when($f).done(function(dataTS){
        if (dataTS.coincidencias>0){
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
                    $("#divBusqueda, #msgCoincidencias, #divCoincidencias" ).hide();
                    agregarAbrir("serieDescripcion");
                    $("label[for='serieDescripcion'] span").eq(1).click(function(){
                        abrirSeries();
                    });
                    $("label[for='serieDescripcion'] span").eq(1).show();

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
    $("#divBusqueda, #divCoincidencias, #msgCoincidencias").hide();

    agregarAbrir("serieDescripcion");
    $("label[for='serieDescripcion'] span").eq(1).click(function(){
        abrirSeries();
    });
    $("label[for='serieDescripcion'] span").eq(1).show();

    $("#divResultadoBusqueda").show();
    $("#serie_id").val(id);
    $("#textSerie").html(  texto);
}