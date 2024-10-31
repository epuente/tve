$("form[name='frmVTK']").off("keypress");
$("form[name='frmVTK']").on("keypress", function(e){
    console.log("serieDescripcion keypress")
            if (e.which == 13) {
                console.log("13!!!!!!!!!!!!1")
                e.stopPropagation();
                return false;
            }
});


$("#btnModalGuardar").off("click");
$("#btnModalGuardar").on("click", function(){
    console.debug("click! ")
});

//function agregaJSON(){
$("#btnActualizar, #btnAgregar").click(function(e){
        console.clear()
        console.log(" - agregaJSON -")
        //var aux4 = valoresCreditos2();
        //$("#txtLosCreditos").val(   aux4   );
        $("#txaTimeLine").val(JSON.stringify(valoresTimeLine()));
        console.debug(    JSON.stringify(valoresTimeLine())   );
        var msgError="";
        console.log("has-error:"+  $("div.has-error").length)
        if ($("form[name='frmVTK']").attr("data-modo")=="edicion"){
            $("#id").val(  $("#id").val()   );
        }
//return false;
        // Validar que folio sea único (con excepcion de S/N)
        //var $uf = LlamadaAjax("/vtkBuscaFolio", "POST", JSON.stringify( {"folio":$("#folio").val()}));
        // Validar que clave (ID) sea única
        var $ui = LlamadaAjax("/vtkBuscaClaveID", "POST", JSON.stringify( {"id":$("#clave").val()}));
        // Evitar que la sinopsis y el título sean iguales
        var tituloSinopsis = $("#titulo").val().trim().toUpperCase() == $("#sinopsis").val().trim().toUpperCase();

        $.when( $ui ).done(function(datai){
            console.log("DOS")
            console.dir(datai)

            if (datai.estado!="correcto" && datai.id !=  parseInt($("#id").val())){
                msgError+="El ID ya esta registrado.<br>";
                $("#clave").closest("div.form-group").addClass("has-error has-danger");
            }

            if ( !$("#clave").val() ){
                msgError+="No se ha seleccionado un ID.<br>";
                $("#clave").closest("div.form-group").addClass("has-error has-danger");
            }

             if ($("*[data-name='cbEvento']:checked").length==0){
                msgError+="Seleccione al menos un evento<br>";
                $("*[data-name='cbEvento']").closest("div.form-group").addClass("has-error has-danger");
                }
            if ( $("*[data-name='cbNivelAcademico']:checked").length==0){
                msgError+="Seleccione al menos un nivel<br>";
                $("*[data-name='cbNivelAcademico']").closest("div.form-group").addClass("has-error has-danger");
                }
            if ( !$("#titulo").val() ){
                msgError+="No se ha escrito el título<br>";
                }
            if ( !$("#sinopsis").val()){
                msgError+="No se ha escrito la sinópsis<br>";
                }
            if ( tituloSinopsis==true && ($("#titulo").val() && $("#sinopsis").val())  ){
                msgError+="El título y la sinópsis no pueden ser iguales<br>";
                $("#sinopsis").closest("div.form-group").addClass("has-error has-danger");
                }
            if ( $("#fechaProduccion").val().length!=0 ){
                    if (   moment($("#fechaProduccion").val()).year()<1995 ){
                        msgError+="El año de producción no puede ser anterior a 1995<br>";
                    }
                }
            if ( $("#fechaPublicacion").val().length!=0 ){
                if (   moment($("#fechaPublicacion").val()).year()<1995 ){
                    msgError+="El año de publicacion no puede ser anterior a 1995<br>";
                }
            }


            if ( !$("#palabrasClaveStr").val()){
                    msgError+="No se han definido las palabras clave<br>";
                   // $("#palabrasClaveStr").parent().find("tags").css("outline","1px solid #b94a48");
                }

            if ( $("[type='checkbox'][name^='areastematicas']:checked").length==0){
                msgError+="No se ha seleccionado el área temática<br>";
                $("#areatematica_id_field").parent().addClass("has-error has-danger");
                }

            if ( $("#formato_id option:selected").length==0 || $("#formato_id option:selected").val().length==0 ){
                msgError+="No se ha seleccionado el formato<br>";
                $("#formato_id").closest("div.form-group").addClass("has-error has-danger");
                }

            //Audio y video
            //audio
            if ( $("#audio_id option:selected").length==0 || $("#audio_id option:selected").val().length==0 ){
                msgError+="No se ha seleccionado el audio<br>";
                $("#audio_id").closest("div.form-group").addClass("has-error has-danger");
                }
            // calidad audio
            if (  $("input[name='calidadAudio']:checked").length==0  ){
                msgError+="Inidique la calidad del audio (bueno o malo)<br>";
                $("#calidad_audio_B").closest("div.form-group").addClass("has-error has-danger");
                }
            // resolución video
            if ( $("#video_id option:selected").length==0 || $("#video_id option:selected").val().length==0 ){
                msgError+="No se ha selecionado la resolución del video<br>";
                $("#video_id").closest("div.form-group").addClass("has-error has-danger");
                }
            // calidad video
            if (  $("input[name='calidadVideo']:checked").length==0  ){
                msgError+="Inidique la calidad del video (bueno o malo)<br>";
                $("#calidad_video_B").closest("div.form-group").addClass("has-error has-danger");
                }


            if (msgError.length>1){
                    event.preventDefault();
                    //msgError.length==1? msgError+="<br><br>Complete el campo faltante" : msgError+="<br><br>Complete los campos faltantes";
                    msgError+="<br><br>Corrija lo indicado.";
                    swal({
                            title: "Aviso",
                            html: msgError,
                            type: "warning",
                            confirmButtonText: "Aceptar"
                    });
                    e.preventDefault();
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
                var xEventos = {};
                xEventos = valoresEventos();

                var xNiveles = {};
                xNiveles = valoresNiveles();

                var xEvidencias = {};
                xEvidencias = valoresEvidencias();
                console.log("xEvidencias: "+JSON.stringify(xEvidencias))

                // Si es edición, se guardan los datos de la tabla tblDetalleArchivos (las evidencias que estan en la DB)
                var xEvidenciasDB = {};
                xEvidenciasDB = valoresEvidenciasDB();
                console.log("xEvidenciasDB: "+JSON.stringify(xEvidenciasDB))



                $("<textarea style='padding-left:100px; display:none;' name='txaEventos' id='txaEventos'>"+JSON.stringify(xEventos)+"</textarea>").appendTo("form[name='frmVTK']");
                $("<textarea style='padding-left:100px; display:none;' name='txaNiveles' id='txaNiveles'>"+JSON.stringify(xNiveles)+"</textarea>").appendTo("form[name='frmVTK']");
                $("<textarea style='padding-left:100px; display:none;' name='txaCreditos' id='txaCreditos'>"+JSON.stringify(x)+"</textarea>").appendTo("form[name='frmVTK']");
                //$("<textarea style='padding-left:100px; display:none;' name='txaCreditos2' id='txaCreditos2'>"+JSON.stringify(x2)+"</textarea>").appendTo("form[name='frmVTK']");
                $("#txaPalabrasClave").val(JSON.stringify(valoresPalabrasClave()));
                $("<textarea style='padding-left:100px; display:none;' name='txaTranzapp' id='txaTranzapp'>"+JSON.stringify(xEvidencias)+"</textarea>").appendTo("form[name='frmVTK']");
                $("<textarea style='padding-left:100px; display:none;' name='txaTranzappDB' id='txaTranzappDB'>"+JSON.stringify(xEvidenciasDB)+"</textarea>").appendTo("form[name='frmVTK']");


                $("<textarea style='padding-left:100px; display:none;' name='pruebaCreditos'>"+valoresCreditos2()+"</textarea>").appendTo("form[name='frmVTK']");

                $("form[name='frmVTK']").submit();
            }
        });  // fin del when
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
        }
    });
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
    aux= $("#palabrasClaveStr").val();
    console.log("    aux:"+aux+"("+aux.length+")")
    if (aux.length!=0){
        obj = jQuery.parseJSON(aux);
        console.log("    obj:"+obj)
        cadena="";
        $.each(obj, function(key,value) {
            var k={};
            k['descripcion'] = value.value;
            j.push(k);
        });
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


function valoresEvidencias(){
    var j=[];
    if ( $("#panelTranzapp").is(":checked") ){


            //********** Cuando es create se usa tblDetalle, cuando es edit se usa ....tblDetalleArchivos



            //var trs = $("#tblDetalle").length>0?$("#tblDetalle tr"):$("#tblDetalleArchivos tr");
            var trs = $("#tblDetalle tr");

            console.log("trs "+$(trs).length)



            $(trs).each(function(i,e){
                if ( $(e).find("td:eq(3)").text()=="Si" ){
                    var lDescarga="";
                    var lBorrado="";
                    var nombre ="";
                    var id="";
                    console.log("Es si")
                    nombre = $(e).find("td:eq(1)").text();
                    lDescarga = $(e).find("td:eq(4)").text();
                    lBorrado = $(e).find("td:eq(5)").text();

                    if ($(e).find("td:eq(0) div[name='evidenciaId']").length!=0)
                        id = $(e).find("td:eq(0) div[name='evidenciaId']").text();
                    //console.log("elId de ta:"+  $(e).find("td:eq(0) div[name='evidenciaId']").text())


                    var evidencia = {};
                    evidencia["archivo"] = nombre;
                    evidencia["lDescarga"] = lDescarga;
                    evidencia["lBorrado"] = lBorrado;
                    evidencia["id"]= id;
                    j.push(evidencia);
                }
        });
    }
    console.dir(j)
    return j;
}

function valoresEvidenciasDB(){
    var j=[];
    if ( $("#panelTranzapp").is(":checked") ){
            //********** Cuando es create se usa tblDetalle, cuando es edit se usa ....tblDetalleArchivos
            var trs = $("#tblDetalleArchivos tr");
            console.log("trs (db)"+$(trs).length)
            $(trs).each(function(i,e){
                if ( $(e).find("td:eq(3)").text()=="Si" ){
                    var lDescarga="";
                    var lBorrado="";
                    var nombre ="";
                    var id="";
                    console.log("Es si")
                    nombre = $(e).find("td:eq(1)").text();
                    lDescarga = $(e).find("td:eq(4)").text();
                    lBorrado = $(e).find("td:eq(5)").text();

                    if ($(e).find("td:eq(0) div[name='evidenciaId']").length!=0)
                        id = $(e).find("td:eq(0) div[name='evidenciaId']").text();

                    var evidencia = {};
                    evidencia["archivo"] = nombre;
                    evidencia["lDescarga"] = lDescarga;
                    evidencia["lBorrado"] = lBorrado;
                    evidencia["id"]= id;
                    j.push(evidencia);
                }
        });
    }
    console.dir(j)
    return j;
}


function labelsCamposRequeridos(){
        $("label").each(function( index, e ) {
            var aux = $(e).attr("for");
            if (   $("#"+aux).attr("required")  ){
                console.log("    requerido" )
                $("label[for='"+aux+"']").addClass("campoRequerido");
            }
        });

        // Otros labels de componentes no convencionales
        $("label[for='eventos_0_id'], label[for='nivel1'], label[for='nivelesacademicos[0].nivel.id'], label[for='palabrasClaveStr'], label[for='areatematicaDescripcion'], label[for='areatematica_id'], label[for='calidad_audio_B'] , label[for='calidad_video_B']" ).addClass("campoRequerido");
}

function agregarAbrir(label){
        console.log("agregarAbrir recibe "+label)
        console.log("label[for='"+label+"']")
        $("label[for='"+label+"']").append("  <span style='display:none; font-size:small'>  <a><i class='fas fa-angle-down'></i></a>Abrir lista</span>     ");
}


// Validar que los campos fecha de producción y fecha de grabación cumplan con alguno de los formatos estrablecidos
$("#fechaProduccion, #fechaPublicacion").off("blur");
$("#fechaProduccion, #fechaPublicacion").on("blur",  function(){
    var componente = $(this);
    var cadena = $(this).val();
    console.log("La FECHA "+cadena)

    if (cadena.length!=0){
        if (!formatoFechaValido(cadena)){
            $("#fechaProduccion, #fechaPublicacion").closest("div.form-group").addClass("has-error has-danger");
            $("#fechaProduccion, #fechaPublicacion").closest("div.form-group").find("div.help-block").html('<ul class="list-unstyled"><li>No cumple con formato</li></ul>');
        } else {
            $("#fechaProduccion, #fechaPublicacion").closest("div.form-group").removeClass("has-error has-danger");
            $("#fechaProduccion, #fechaPublicacion").closest("div.form-group").find("div.help-block").html('');
            if (moment(cadena).year() < 1995){
                swal({
                        title: "Advertencia",
                        html: "El año de la fecha produccion/publicacion no puede ser anterior a 1995.",
                        type: "warning",
                        showConfirmButton: true,
                        confirmButtonText: "Aceptar"
                });
                return false;
            }
            console.log(  moment(new Date()).format("DD-MM-YYYY")   )
            console.log(  moment(cadena).isAfter(moment(new Date()).format("DD-MM-YYYY") )  )
            if (moment(cadena).isAfter(moment(new Date()).format("DD-MM-YYYY"))){
                swal({
                        title: "Advertencia",
                        html: "La fecha produccion/publicacion no puede mayor al día de hoy.",
                        type: "warning",
                        showConfirmButton: true,
                        confirmButtonText: "Aceptar"
                });
                $(this).closest("div.form-group").addClass("has-error has-danger");
                $(this).closest("div.form-group").find("div.help-block").html('<ul class="list-unstyled"><li>La fecha de producción/publicación no puede ser mayor al día de hoy.</li></ul>');
                return false;
            }

            // Validar que fechaProduccion sea anterior a fechaPublicacion
            // hay info en ambos?
            if ( $("#fechaProduccion").val().length>=4  &&  $("#fechaPublicacion").val().length>=4){
                var prod = moment($("#fechaProduccion").val());
                var publ = moment($("#fechaPublicacion").val());
                if (  prod.isAfter(publ)  ){
                    swal({
                            title: "Advertencia",
                            html: "La fecha de publicación no puede ser anterior a la fecha de producción.",
                            type: "warning",
                            showConfirmButton: true,
                            confirmButtonText: "Aceptar"
                    });
                    $("#fechaProduccion, #fechaPublicacion").closest("div.form-group").addClass("has-error has-danger");
                    $("#fechaProduccion, #fechaPublicacion").closest("div.form-group").find("div.help-block").html('<ul class="list-unstyled"><li>Producción debe ser anterior a publicación</li></ul>');
                    return false;
                }
            }
        }
    }
} );

function formatoFechaValido(cadena){
    return moment(cadena,  ["DD/MM/YYYY", "MM/YYYY", "YYYY"], true).isValid();
}



function agregarTimeLine(){
    var now = Date.now();
    $("#baseTimeLine").clone().attr("id", "renglonTimeLine-"+ now ).appendTo("#abc").find("div.row").css("display","block");
    $("*[name='desde']").each(function(i,e){
        var mMascaraDesde = IMask(e, {
                                     mask: 'h:m:s',
                                     lazy: false,
                                     autofix: true,
                                     blocks: {
                                         h: {mask: IMask.MaskedRange, placeholderChar: 'h', from: 0, to: 99, maxLength: 2},
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
                                         h: {mask: IMask.MaskedRange, placeholderChar: 'h', from: 0, to: 99, maxLength: 2},
                                         m: {mask: IMask.MaskedRange, placeholderChar: 'm', from: 0, to: 59, maxLength: 2},
                                         s: {mask: IMask.MaskedRange, placeholderChar: 's', from: 0, to: 59, maxLength: 2}
                                     }
                           });
    });
}


function agregarTablaEvidencias(){
    $("#baseEvidencias").clone()
        .attr("id", "laTablaEvidencias-1")
        .appendTo("#soloTablaEvidencias")
        .css({"display":"block", "background-color": "white"
    });
    if ($("#tblDetalleArchivos tbody").find("tr").length === 0) {
            $("#tblDetalleArchivos tbody").append('<tr><td colspan="6" class="no-file">Sin archivos</td></tr>');
       }
}

function agregarRenglonTablaEvidencias(){
    $("#laTablaEvidencias-1 tbody").append("<tr><td></td><td></td><td></td><td></td><td></td><td></td><td><button type='button' class='deleteBtn'><i class='material-icons-outlined'>delete</i></button></td></tr>");
}

function eliminarTimeLine(e){
    console.log("e "+$(e))
    console.dir("e "+$(e))
    console.dir("e "+$(e).prop("id"))
    //console.log("e "+$(e).attr("id"))
    $(e).closest("div.row-participante").parent().remove();
}


function segundosACadena(seconds) {
  var hour = Math.floor(seconds / 3600);
  hour = (hour < 10)? '0' + hour : hour;
  var minute = Math.floor((seconds / 60) % 60);
  minute = (minute < 10)? '0' + minute : minute;
  var second = seconds % 60;
  second = (second < 10)? '0' + second : second;
  return hour + ':' + minute + ':' + second;
}


$("#clave").blur(function(e){
    console.log("   "+$(this).val())
    if (  $(this).val().length!=0 ){
        var $u = LlamadaAjax("/vtkBuscaClaveID", "POST", JSON.stringify( {"id":$(this).val()}));
        $.when($u).done(function(data){
            console.dir(data)
            var modo=$("form[name='frmVTK']").attr("data-modo");
            var idClave = parseInt( $("#clave").val() );
            if (data.estado.localeCompare("correcto")!=0){
                if ( modo.localeCompare("creacion")==0){
                    var msg="";
                    if (modo.localeCompare("edicion")==0  && (data.clave != idClave ) )
                        msg="<br><br>El ID ya está asignado.";
                    swal({
                            title: "Advertencia",
                            html: "El ID ya esta registrado<br><br>No se permite registrar mas de una vez el mismo ID."+msg,
                            type: "warning",
                            showConfirmButton: true,
                            confirmButtonText: "Aceptar"
                    });
                    return false;
                }
            }
        });
    }
});

// Cuando se seleccione 'Otra' en Disponibilidad, mostrar un input para especificarla
$("#disponibilidades999").change(function(){
    console.log("click")
    if ( $(this).prop("checked")==true ){
        $("#divDisponibilidadOtra").show();
    } else
        $("#divDisponibilidadOtra").hide();
});


// Cuando se seleccione 'Otra' en Área temática, mostrar un input para especificarla
$("input[data-name='cbArea'][value=999]").change(function(){
    if ( $(this).prop("checked")==true ){
        $("#divAreaTematicaOtra").show();
    } else
        $("#divAreaTematicaOtra").hide();
});


// Si la entrada a tagify incluye algún enter (retorno de carro), lo sustituye por un espacio en blanco
function tagifySinEnter( tagData ){
    tagData.value = tagData.value.replace(/\r?\n/g, ' ');
}


$(document).off("click","#panelTranzapp");
$(document).on("click","#panelTranzapp", function(){
    console.log("click panel tranzapp")
    $("#divTranzapp").toggle();
});


 $('#fileUpload').on('change', function(event) {
        console.log("...change")
        const files = event.target.files; // Obtiene los archivos seleccionados
        const fileList = $('#fileList');
        fileList.empty(); // Limpia la lista anterior

        for (let i = 0; i < files.length; i++) {
            const fileName = files[i].name; // Obtiene el nombre del archivo
            fileList.append(`<p>${fileName}</p>`); // Muestra el nombre en la lista
        }
        console.log(fileList)
    });




    function subirEvidencias(){
        console.clear();
        console.log("Se inicia subirEvidencias")
        if ($("#fileUpload-1")[0].files.length==0){
            swal(
              'Aviso',
              'No se han seleccionado archivos',
              'warning'
            )
        } else {
            // Crear un formData por cada archivo en el file input (#fileUpload-1)
            for (i=0; i<$("#fileUpload-1")[0].files.length; i++){
                var formData = new FormData();
                console.log("i...")
                var a = $("#fileUpload-1")[0].files[i];
                console.log("nombre: "+a.name)
                formData.append(a.name, a);
                // Llamada ajax
                console.log("llamando ajax   (auxiliar(formData, i))    " )
                var $aux=auxiliar(formData, i);
            }
        }

    }


    function subirEvidenciasEnDisco(){
        console.clear();
        console.log("Se inicia subirEvidenciasEnDisco")
        if ($("#fileUpload-1")[0].files.length==0){
            swal(
              'Aviso',
              'No se han seleccionado archivos',
              'warning'
            )
        } else {
            // Crear un formData por cada archivo en el file input (#fileUpload-1)
            for (i=0; i<$("#fileUpload-1")[0].files.length; i++){
                var formData = new FormData();
                console.log("i...")
                var a = $("#fileUpload-1")[0].files[i];
                console.log("nombre: "+a.name)
                formData.append(a.name, a);
                // Llamada ajax
                console.log("llamando ajax   (auxiliar(formData, i))    " )
                var $aux=auxiliar(formData, i);
            }
        }

    }

    function auxiliar(parametro, x){
        console.log(parametro)
        $aux = LlamaAjaxMultiPart(parametro, x);
        $.when($aux).done(function(data){
              console.dir( data);
              console.log("#subido-"+x)
              if (data.estado=="subido"){
                  $("#subido-"+x).html("<div class='text-success'>Si</div>");
                  $("#descarga-"+x).html("<a href="+data.descargar+" target='_blank'>"+data.descargar+"</a>");
                  $("#borrado-"+ x).html("<a href="+data.borrar+" target='_blank'>"+data.borrar+"</a>");
              }
              if (data.estado=="error"){
                $("#subido-"+x).html("<div class='text-danger'>No</div>");
                $("#descarga-"+x).html("Error");
                $("#borrado-"+x).html("Error");

              }
        });
    }


    function LlamaAjaxMultiPart(formData, i){
        console.log("Llamada desde videoteca.js  LlamaAjaxMultiPart")
        var d = $.Deferred();
            $.ajax({
                   url: '/uploadForma',
                   data: formData,
                   type: 'POST',
                   enctype: 'multipart/form-data',
                   contentType: false,
                   processData: false,
                   cache: false,
                   dataType: "json",
                   beforeSend: function (xhr) {
                        $("#subido-"+i).html("<div class='text-success'>Procesando...</div>");
                        xhr.setRequestHeader('CONTENT_LENGTH', '10737418240');
                   },
                   success: function (  data) {
                        d.resolve(data);
                  },
                  fail:function(data){
                    alert("error de ajax");
                    d.reject;
                  }
            });
            return d.promise();
    }


    // Para eliminar archivo de evidencia en edición de bitácora
    $(document).on("click",".deleteBtn", function(btn){
        console.log("click......")
        $(this).closest("tr").remove();

    });

