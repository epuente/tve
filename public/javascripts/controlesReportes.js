

	
	
	$("#sabanaNavegaAnioMenos").click(function(){
		navegar("anio", -1);		
	});
	
	$("#sabanaNavegaAnioMas").click(function(){
		navegar("anio", 1);		
	});

	$("#sabanaNavegaSemestreMenos").click(function(){
		navegar("semestre", -1);		
	});
	
	$("#sabanaNavegaSemestreMas").click(function(){
		navegar("semestre", 1);		
	});


	$("#sabanaNavegaTrimestreMenos").click(function(){
		console.log("Enviando trimestre: -1   divTrimestres: "+trimestres.indexOf($("#divTrimestre").text()));
		navegar("trimestre", -1 );		
	});		
	$("#sabanaNavegaTrimestreMas").click(function(){
		console.log("Enviando trimestre: 1   divTrimestres: "+trimestres.indexOf($("#divTrimestre").text()));
		navegar("trimestre", 1 );		
	});		
	
	$("#sabanaNavegaMesMas").click(function(){
		console.log("Enviando mes: "+1+"    divMeses: "+meses.indexOf($("#divMes").text()));
		navegar("mes", 1 );		
	});

	$("#sabanaNavegaMesMenos").click(function(){
		console.log("Enviando mes: -1   divMeses: "+meses.indexOf($("#divMes").text()));
		navegar("mes", -1 );		
	});	
	

	$("#sabanaNavegaHoy").click(function(){
		navegar("hoy", 0);	
		
	});
	
	
	function navegar(unidad, cantidad){
			
			
			console.log("mes "+$("#divMes>span").text())
			console.log("mes "+$("#divMes>span").data("mes"))
		var fecha = "01/"+$("#divMes>span").data("mes")+"/"+$("#divAnio").text();
		var semestre = $("#divSemestre>span").data("semestre");
		var trimestre = $("#divTrimestre>span").data("trimestre");
		var mes = $("#divMes>span").data("mes")
		var semana = $("#divSemana").data("nsemana")
		
		$("#divSabana").html("");
		console.log("enviando al controlador fecha::  "+fecha)
		console.log("enviando al controlador semestre::  "+semestre)
		console.log("enviando al controlador trimestre::  "+trimestre)
		console.log("enviando al controlador mes::  "+mes)
		console.log("enviando al controlador semana::  "+semana)
		
		
		
		$.ajax({
			url: '/ajaxSabanaNavega',
			method: 'POST',
		    data: JSON.stringify( { "unidad": unidad, "cantidad":cantidad, "fecha":fecha, "semestre":semestre, "trimestre":trimestre, "mes":mes, "semana":semana}),
		    contentType: "application/json; charset=utf-8",
		    dataType: "json"
		}).success(function(data){	
			console.dir(data)
			var c="<table class='table table-condensed table-bordered' id='tablaNavegacionFechas'>";
			

			c+="</table>"
 			$("#divAnio").html(data.anio);
			$("#divSemestre>span").text( semestres[data.semestre-1]).data("semestre",data.semestre);
			$("#divTrimestre>span").text( trimestres[data.trimestre-1]).data("trimestre", data.trimestre  );
 			$("#divMes>span").text( meses[data.mes-1]).data("mes",data.mes);
//			console.log(data.inicioSemana+" - "+data.terminaSemana)
			$("#divSemana").text( data.inicioSemana+" - "+data.terminaSemana).data("nsemana",data.numSemana);;				
			
			
			
			c2="";
			$("#navegacionFechas").html(c);
			$("#navegacionFechas tr:even").css("background-color","#f0f0f0");
			$('#tablaNavegacionFechas').off('click', 'tr');
			$('#tablaNavegacionFechas').on('click', 'tr', function(event) {
				$("#navegacionFechas tr:even").css("background-color","#f0f0f0");
				$(this).css('background-color','');
				$(this).addClass('seleccionado').siblings().removeClass('seleccionado');
			});			
		}).error(function(xhr, status, error) {
			  alert("error en ajax (LlamadaAjaxBuscaServiciosFolio 004) -  "+ error );
			});		
	}
	



$("[name='btnNavega']").click(function(){
	$("[name='btnNavega']").removeClass("active");
	var btnActivo = $(this);
	$(btnActivo).addClass("active");
	var pos =  $(btnActivo).index("[name='btnNavega']" );
	console.log("pos");
	console.log(pos);
	switch(pos) {
		case 0:		//Año
			$("#controlNavegaSemestre, #controlNavegaTrimestre, #controlNavegaMes, #controlNavegaSemana, #navegacionFechas").fadeOut();
		break;
		case 1:		//Semestre
			$("#controlNavegaTrimestre, #controlNavegaMes, #controlNavegaSemana, #navegacionFechas").fadeOut();
			$("#controlNavegaSemestre").fadeIn();
		break;		
		case 2:		//Trimestre
			$("#controlNavegaSemestre, #controlNavegaMes, #controlNavegaTrimestre, #controlNavegaSemana, #navegacionFechas").fadeOut();
			$("#controlNavegaTrimestre").fadeIn();
		break;
		case 3:		//Mes
			$("#controlNavegaSemestre, #controlNavegaTrimestre, #controlNavegaSemana, #navegacionFechas").fadeOut();
			$("#controlNavegaMes").fadeIn();
		break;				
		case 4:		//Semana
			$("#controlNavegaSemestre, #controlNavegaTrimestre, #controlNavegaSemana").fadeOut();
			$("#controlNavegaMes, #controlNavegaSemana").fadeIn();
		break;	
	}
});



$("[name='btnNavegaSem']").click(function(){
	$("[name='btnNavegaSem']").removeClass("active");
	var btnActivo = $(this);
	$(btnActivo).addClass("active");
});


$(document).off("click","[name='btnDetalle']");
$(document).on("click","[name='btnDetalle']", function(){
		$("[id='trDetalle-"+$(this).attr("id")+"']").toggle("slow", "swing");
		if ($("[id='trDetalle-"+$(this).attr("id")+"']").css("display")!="none"){
			$(this).html("menos detalle");
		}
		if ($("[id='trDetalle-"+$(this).attr("id")+"']").css("display")=="none"){
			$(this).html("ver detalle");
		}
});


//Imprimir div

$("#btnImprimirDiv").click(function(){
console.log("imorimir")
	     var contenido= document.getElementById("divCuerpoReporte").innerHTML;
     var contenidoOriginal= document.body.innerHTML;

     document.body.innerHTML = contenido;

     window.print();

     //document.body.innerHTML = contenidoOriginal;
});




