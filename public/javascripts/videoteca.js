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
    //if (cadena.length >= 3 ){
        console.debug("cadena "+cadena)
        $("#btnNuevaSerie").toggle(cadena.length>1);
        if (cadena.length==0){
            console.log("búsqueda vacía");
        } else {
            console.log("búsqueda NO vacía");

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
   // }
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
    console.log(" - agregaJSON -")
}


function agregaJSON2(){
    var json ={};
    var strEventos ="";
    var strNiveles ="";
  //  console.clear();
    console.log("UR:"+$("#unidadresponsable_id").val())
    json["folio"] = $("#folio").val();

    if ($("#unidadresponsable_id").val()!="")
        json["unidadresponsable"] = {id: $("#unidadresponsable_id").val() }


    //json["unidadresponsable"] = {id: $("#unidadresponsable_id").val() };
    json["eventos"] = JSON.parse('[{"id":1}, {"id":2}]');
    json["eventos"] = [];
    $("*[name^='eventos[']:checked").each(function(index, e){
           strEventos += '{"servicio":{"id":'+$(e).val()+' }},'
    });

   // console.dir("strEventos:"+strEventos)
    if (strEventos.length>0)
        strEventos = strEventos.substring(0, strEventos.length-1);
    console.dir("strEventos:"+strEventos)
    json["eventos"] =  JSON.parse('['+strEventos+']');

    $("*[name^='nivelesacademicos[']:checked").each(function(index, e){
           strNiveles += '{"nivel":{"id":'+$(e).val()+' }},'
    });
    if (strNiveles.length>0)
        strNiveles = strNiveles.substring(0, strNiveles.length-1);
    json["nivelesacademicos"] = JSON.parse('['+strNiveles+']');

    json["folioDEV"] = $("#folioDEV").val();
    json["serie"] = {id: $("#serie_id").val()};
    json["titulo"] = $("#titulo").val();
    json["clave"] = $("#clave").val();
    json["sinopsis"] = $("#sinopsis").val();
    json["produccion"] = {id: $("#produccion_id").val()};
    json["anioproduccion"] =  $("#anioproduccion").val();
    json["duracion"] = $("#duracion").val();
    json["formato"] = {id: $("#formato_id").val()};
    json["idioma"] = {id: $("#idioma_id").val()};
    json["disponibilidad"] = {id: $("#disponibilidad_id").val()};
    json["areatematica"] = {id: $("#areatematica_id").val()};
    json["audio"] = $("#audio").val();


    tempo = valoresCreditos();
    console.log("los creditos")
    console.dir(tempo)


    console.dir($("#ta1").val());

    var strCreditos="";
    var arrCreditos=new Array();
    var txt = "";
    $("*[name='tasCreditos'").each(function(i,e){
        var indice = $(e).attr("id").substring(2);
        console.log(":")
        console.log($(e).val());
        console.dir($(e).val());
        console.log("type: "+ typeof  $(e).val());
        if ($(e).val()!=""){
            const az = JSON.parse( $(e).val()  );
            console.log(az)
            console.log("type az: "+ typeof  az);
            console.log("tam az: "+  az.length);


            for (let i = 0; i < az.length; i++) {
                txt += '{"personas":"'+az[i].value+'", "tipoCredito": {"id":'+indice+'}},';
                console.log("txt:"+txt)
            }
            //console.dir(JSON.parse(  txt  ))
        }

    });
    if (txt.length>0){
        txt=txt.substring(0, txt.length-1);
        console.log(" rl trxto:"+txt)
        json["creditos"] = JSON.parse( '['+txt+']');
    } else {
        json["creditos"] = JSON.stringify({});
    }


    console.log("---------------");
    console.log(JSON.stringify(json));
    console.log("---------------");
    console.dir(json);

    $aux = LlamadaAjax("/vtkCatalogo/save2", "POST",  JSON.stringify(json) );
    $.when( $aux )
        .done(function(data){
                console.dir(data)
            });

}


$("form").submit(function(event){
   // event.preventDefault();
    console.log(" - submit -")
    var msgError="";
    //console.clear()

    if ( !$("#clave").val() )
        msgError+="No se ha seleccionado un ID.<br>";
     if ($("*[data-name='cbEvento']:checked").length==0)
        msgError+="Seleccione al menos un evento<br>";
    if ( $("*[data-name='cbNivelAcademico']:checked").length==0)
        msgError+="Seleccione al menos un nivel<br>";
    if ( !$("#sinopsis").val())
        msgError+="No se ha escrito la sinópsis<br>";
    if ( !$("#formato_id").val())
        msgError+="No se ha seleccionado el formato<br>";
    if ( !$("#areatematica_id").val())
        msgError+="No se ha seleccionado el área temática<br>";
    if ( !$("#palabrasClaveStr").val()){
            msgError+="No se han definido las palabras clave<br>";
            $("#palabrasclave").closest("div.form-group").addClass("has-error has-danger");
        }
    if ( !$("#video").val())
        msgError+="No se han escrito las características del video<br>";

    if (msgError.length>0){
            event.preventDefault();
            msgError.length==1? msgError+="<br><br>Complete el campo faltante" : msgError+="<br><br>Complete los campos faltantes";

            swal({
                    title: "Aviso",
                    html: msgError,
                    type: "warning",
                    confirmButtonText: "Aceptar"
            });
            return false;
    }
    else {
        //swal("En construcción", "Faltan algunas definiciones para completar esta funcionalidad","warning");
        //event.preventDefault();

        $("#cuentas_username").attr('name', 'cuentas[0].username');
        $("#cuentas_password").attr('name', 'cuentas[0].password');

        $("[type='checkbox'][id^='auxRoles_']:checked").each(function(i,e){
            $(this).attr("name","cuentas[0].roles["+i+"].rol.id");
        });


        var x = {};
        var ppcc = {};

        x['losDatos']=valoresCreditos();
        ppcc['lasPalabras'] = valoresPalabrasClave();

        $("<input type='text' name='palabrasClave[0].descripcion' value='uno'>").appendTo("#frmVTKCreate");
        $("<input type='text' name='palabrasClave[1].descripcion' value='dos'>").appendTo("#frmVTKCreate");

        $("<textarea style='padding-left:100px; display:none;' name='txaCreditos' id='txaCreditos'>"+JSON.stringify(x)+"</textarea>").appendTo("#frmVTKCreate");
        $("#txaPalabrasClave").val(JSON.stringify(valoresPalabrasClave()));
        $("#txaTimeLine").val(JSON.stringify(valoresTimeLine()));

        //event.preventDefault();
        //return false;
    }
});


function valoresCreditos(){
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
    return j;
}


function valoresPalabrasClave(){
    var j=[];
  //  var palabraclave={};
        aux= $("#palabrasClaveStr").val();
        console.log("    aux:"+aux+"("+aux.length+")")
        if (aux.length!=0){
            obj = jQuery.parseJSON(aux);
            console.log("    obj:"+obj)
            cadena="";
            $.each(obj, function(key,value) {
                var k={};
                k['descripcion'] = value.value;
                //cadena+=value.value+",";
                j.push(k);
            });
            //cadena = cadena.substring(0, cadena.length-1);
            //console.log("    cadena:"+cadena)
            //palabraclave["descripcion"]
            //j.push(palabraclave);
        }
        console.dir(j)
    return j;
}

function valoresTimeLine(){
    var j=[];

    $("div[id^='renglonTimeLine-']").each(function(i,e){
        var renglon = {};
        var auxDesde = $(e).find("input[name='desde']").val();
        var auxHasta = $(e).find("input[name='hasta']").val();
        var auxNombre = $(e).find("input[name='personaje']").val();
        var auxGrado = $(e).find("input[name='grado']").val();
        var auxCargo = $(e).find("input[name='cargo']").val();
        var auxTema = $(e).find("input[name='tema']").val();
        console.log("auxDesde "+ auxDesde)
        console.log("auxHasta "+ auxHasta)
        if (auxDesde.length!=0)
            renglon['desde'] = auxDesde;
        if (auxHasta.length!=0)
            renglon['hasta'] = auxHasta;
        if (auxNombre.length>0)
            renglon['nombre'] = auxNombre;
        if (auxGrado.length>0)
            renglon['grado'] = auxGrado;
        if (auxCargo.length>0)
            renglon['cargo'] = auxCargo;
        if (auxTema.length>0)
            renglon['tema'] = auxTema;

        j.push(renglon);
    });
    return j;
}


function labelsCamposRequeridos(){
        $("label").each(function( index, e ) {
            var aux = $(e).attr("for");
            console.log("for "+aux);
            if (   $("#"+aux).attr("required")  ){
                console.log("    requerido" )
                $("label[for='"+aux+"']").addClass("campoRequerido");
            }
        });

        // Otros labels de componentes no convencionales
        $("label[for='eventos_0_id'], label[for='nivel1'], label[for='nivelesacademicos[0].nivel.id'], label[for='palabrasClaveStr'], label[for='areatematica_id']").addClass("campoRequerido");
}




function agregarTimeLine(){
    console.log("------------------");
    var now = Date.now();
    $("#baseTimeLine").clone().attr("id", "renglonTimeLine-"+ now ).appendTo("#abc").find("div.row").css("display","block");

    $("*[name='desde']").each(function(i,e){
        var mMascaraDesde = IMask(e, {
                                     mask: 'h:m:s',
                                     lazy: false,
                                     autofix: true,
                                     blocks: {
                                         h: {mask: IMask.MaskedRange, placeholderChar: 'h', from: 0, to: 999, maxLength: 3},
                                         m: {mask: IMask.MaskedRange, placeholderChar: 'm', from: 0, to: 59, maxLength: 2},
                                         s: {mask: IMask.MaskedRange, placeholderChar: 's', from: 0, to: 59, maxLength: 2}
                                     }
                           });
    });

    $("*[name='hasta']").each(function(i,e){
        var mMascaraHasta = IMask(e, {
                                     mask: 'h:m:s',
                                     lazy: false,
                                     autofix: true,
                                     blocks: {
                                         h: {mask: IMask.MaskedRange, placeholderChar: 'h', from: 0, to: 999, maxLength: 3},
                                         m: {mask: IMask.MaskedRange, placeholderChar: 'm', from: 0, to: 59, maxLength: 2},
                                         s: {mask: IMask.MaskedRange, placeholderChar: 's', from: 0, to: 59, maxLength: 2}
                                     }
                           });
    });





}


function eliminarTimeLine(e){
    console.log("e "+$(e))
    console.log("e "+$(e).attr("id"))
    $(e).closest("div.row").remove();
}


function segundosACadena(seconds) {
  var hour = Math.floor(seconds / 3600);
  hour = (hour < 10)? '0' + hour : hour;
  if (hour.length<3)
    hour = "0"+hour;
  var minute = Math.floor((seconds / 60) % 60);
  minute = (minute < 10)? '0' + minute : minute;
  var second = seconds % 60;
  second = (second < 10)? '0' + second : second;
  return hour + ':' + minute + ':' + second;
}
