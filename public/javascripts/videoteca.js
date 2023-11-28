const ayudaCampos = ['ayuda del campo 1',
                        'ayuda del campo2',
                        'Escriba el nombre completo de la Instancia solicitante y se realizará automáticamente la búsqueda.<br><br>Si la Instancia que desea agregar aparece en la lista de coincidencias, elijala de la lista.<br><br>Si la Instancia es nueva, oprima el botón <strong>Nueva UR</strong>',
                        'ayuda del campo 4'
                    ]


$("#btnModalGuardar").off("click");
$("#btnModalGuardar").on("click", function(){
    console.debug("click! ")
});



function agregaJSON(){
    console.log(" - agregaJSON -")
        var aux4 = valoresCreditos2();

        $("#txtLosCreditos").val(   aux4   );
        $("#txaTimeLine").val(JSON.stringify(valoresTimeLine()));

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
    //json["eventos"] = JSON.parse('[{"id":1}, {"id":2}]');
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


$("form[name='frmVTK']").submit(function(event){
    //event.preventDefault();
    console.log(" - submit -")
    var msgError="";
    console.clear()
    console.log("has-error:"+  $("div.has-error").length)


    if (  $("div.has-error").length!=0  ){

        console.log("123")
        $("div.has-error").each(function(i,e){
            console.log("456")
           msgError+="El campo "+$(e).attr("name")+" tiene error<br";
        });
    }



    var $uf = LlamadaAjax("/vtkBuscaFolio", "POST", JSON.stringify( {"folio":$("#folio").val()}));
    var $ui = LlamadaAjax("/vtkBuscaClaveID", "POST", JSON.stringify( {"id":$("#clave").val()}));
    $.when( $uf, $ui  ).done(function(dataf, datai){
        console.log("DOS")
        console.dir(dataf)
        console.dir(datai)
        console.log(  dataf.estado=="correcto"  )
        if (dataf.estado!="correcto"){
            msgError+="El folio ya esta registrado.<br>";
        }
        if (datai.estado!="correcto"){
            msgError+="El ID ya esta registrado.<br>";
        }

        if ( !$("#clave").val() )
            msgError+="No se ha seleccionado un ID.<br>";
         if ($("*[data-name='cbEvento']:checked").length==0)
            msgError+="Seleccione al menos un evento<br>";
        if ( $("*[data-name='cbNivelAcademico']:checked").length==0)
            msgError+="Seleccione al menos un nivel<br>";
        if ( !$("#titulo").val() )
            msgError+="No se ha escrito el título<br>";
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
        if ( $("#video_id option:selected").length==0)
            msgError+="No se han escrito las características del video<br>";
        if ( $("#audio_id option:selected").length==0 )
            msgError+="No se han escrito las características del audio<br>";
        if (  $("input[name='calidadAudio']:checked").length==0  )
            msgError+="Inidique la calidad del audio (bueno o malo)<br>";
        if (  $("input[name='calidadVideo']:checked").length==0  )
            msgError+="Inidique la calidad del video (bueno o malo)<br>";
        if (  !$("#observaciones").val()  )
            msgError+="Escriba sus observaciones<br>";

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
            x=valoresCreditos();

            console.log("creditos x")
            console.log(x)
            console.dir(x)

/*
            var x2 = {};
            x2=valoresCreditos2();

                        console.log("creditos x2")
                        console.dir(x2)
*/

            var xEventos = {};
            xEventos = valoresEventos();

            var xNiveles = {};
            xNiveles = valoresNiveles();


            $("<textarea style='padding-left:100px; display:none;' name='txaEventos' id='txaEventos'>"+JSON.stringify(xEventos)+"</textarea>").appendTo("form[name='frmVTK']");
            $("<textarea style='padding-left:100px; display:none;' name='txaNiveles' id='txaNiveles'>"+JSON.stringify(xNiveles)+"</textarea>").appendTo("form[name='frmVTK']");
            $("<textarea style='padding-left:100px; display:none;' name='txaCreditos' id='txaCreditos'>"+JSON.stringify(x)+"</textarea>").appendTo("form[name='frmVTK']");
            //$("<textarea style='padding-left:100px; display:none;' name='txaCreditos2' id='txaCreditos2'>"+JSON.stringify(x2)+"</textarea>").appendTo("form[name='frmVTK']");
            $("#txaPalabrasClave").val(JSON.stringify(valoresPalabrasClave()));

            $("#tasCreditos").val(  JSON.stringify(x)  );


            console.log(">>>>>>>>>>>>>>>>>>>>>>")
            console.log(  JSON.stringify(x)   );






            //event.preventDefault();
            //return false;
        }

    });



});


function valoresCreditos(){
    var j=[];
    var general = "";
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
            general += cadena;
            console.log("    cadena:"+cadena)
            tipo["creditos"]=cadena;
            j.push(tipo);
        }
    });
    return general;
}


function valoresCreditos2(){
    var j=[];
    var general="";
    $("#navTabs>li>a").each(function(index, element){
        var id = $(element).attr("id").substring(3);
        console.log("  id "+id)
        var tipo={};




        aux= $("#ta"+id).val();
        console.log("    aux:"+aux+"("+aux.length+")")
        if (aux.length!=0){
            obj = jQuery.parseJSON(aux);
            console.log("    obj:"+obj)
            console.dir(obj)
            var todos = "";
            $.each(obj, function(key,value) {

          //      alert(value.value);
                todos += value.value+"|";
            });
            console.log("todos:"+todos)
            var arrString = todos.split("|");
            console.log("arrString "+arrString+"("+arrString.length+")")
            for (var i=0; i < arrString.length-1; i++){
                tipox={tipoCredito:{id:id}, personas:arrString[i]}
                general += JSON.stringify(tipox)+",";
                j.push(tipox);
            }
            console.log("");
            console.log("");
            console.log("");


        }
    });
    console.log("->j")
    console.dir(j)
                general = general.substring(0, general.length-1);

    return   "["+ general+"]";
}


function valoresEventos(){
    var j=[];
     $("*[name^='eventos[']:checked").each(function(index, element){
        var evento = {};
        evento["servicio"] = {id:$(this).val()};
        j.push(evento);
     });
     console.log("j : ")
     console.dir(j)
    return j;
}

function valoresNiveles(){
    var j=[];
     $("*[name^='niveles[']:checked").each(function(index, element){
        var evento = {};
        evento["nivel"] = {id:$(this).val()};
        j.push(evento);
     });
     console.log("j : ")
     console.dir(j)
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
        $("label[for='eventos_0_id'], label[for='nivel1'], label[for='nivelesacademicos[0].nivel.id'], label[for='palabrasClaveStr'], label[for='areatematicaDescripcion'], label[for='areatematica_id'], label[for='calidad_audio_b'] , label[for='calidad_video_B']" ).addClass("campoRequerido");

}

function agregarAbrir(label){
        console.log("agregarAbrir recibe "+label)
        console.log("label[for='"+label+"']")
        $("label[for='"+label+"']").append("  <span style='display:none; font-size:small'>  <a><i class='fas fa-angle-down'></i></a>Abrir lista</span>     ");
}


// Validar que los campos fecha de producción y fecha de grabación cumplan con alguno de los formatos estrablecidos
$("#fechaProduccion, #fechaPublicacion").off("blur");
$("#fechaProduccion, #fechaPublicacion").on("blur",  function(){
    var cadena = $(this).val();
    if (cadena.length!=0){
        if (!formatoFechaValido(cadena)){
            $(this).closest("div.form-group").addClass("has-error has-danger");
            $(this).closest("div.form-group").find("div.help-block").html('<ul class="list-unstyled"><li>No cumple con formato</li></ul>');
        } else {
            $(this).closest("div.form-group").removeClass("has-error has-danger");
            $(this).closest("div.form-group").find("div.help-block").html('');
        }
    }
} );

function formatoFechaValido(cadena){
    return moment(cadena,  ["DD/MM/YYYY", "MM/YYYY", "YYYY"], true).isValid();
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

$("#folio").blur(function(e){
    console.log("   "+$(this).val())
    if (  $(this).val().length!=0 ){
        var $u = LlamadaAjax("/vtkBuscaFolio", "POST", JSON.stringify( {"folio":$(this).val()}));
        $.when($u).done(function(data){
            console.dir(data)
            if (data.estado!="correcto"){
                swal({
                        title: "Advertencia",
                        html: "El número de folio ya esta registrado<br><br>No se permite registrar mas de una vez el mismo folio.",
                        type: "warning",
                        showConfirmButton: true
                });
            }
        });
    }
});

$("#clave").blur(function(e){
    console.log("   "+$(this).val())
    if (  $(this).val().length!=0 ){
        var $u = LlamadaAjax("/vtkBuscaClaveID", "POST", JSON.stringify( {"id":$(this).val()}));
        $.when($u).done(function(data){
            console.dir(data)
            if (data.estado!="correcto"){
                swal({
                        title: "Advertencia",
                        html: "El número de ID ya esta registrado<br><br>No se permite registrar mas de una vez el mismo ID.",
                        type: "warning",
                        showConfirmButton: true
                });
            }
        });
    }
});


