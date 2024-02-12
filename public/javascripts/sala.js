    $("#filtroOpera, #filtroMantto").click(function(){
        toggleFiltro(this);
        
        
    });    
    
    function toggleFiltro(e){       
        $("div[name='filtroEdo']").css("font-weight", "normal");
        $("div[name='filtroEdo']").css("border-bottom","3px solid #F2F2F2");
        $(e).css("font-weight", "bold");
        $(e).css("border-bottom","3px solid "+$(e).css("color"));
        $("#divOperador").toggle( $(e).prop("id")=="filtroOpera"  );
        $("#divMantto").toggle( $(e).prop("id")=="filtroMantto"  );
    }
        

    function eliminaMantto(op){
    	$("#divTablaMantto div.row:eq("+op+")").remove();
    }    
    




    function convertirAJson(){
        // Pasar a object y despues enviar como Json
        var sala = {};
        var operadores = [];
        var manttos = [];

        sala["id"] = $("input[name='id']").val();
        sala["descripcion"] = $("input[name='descripcion']").val();
        var ii =0;
        $.each($('select[name="selectLocutoresDisponiblesMat"] option:selected') , function(i,e){
        	var personal = {};
        	personal = {"personal": { "id":$(e).val()}, "turno": "M"};
        	operadores[ii++] = personal;
        });
        $.each($('select[name="selectLocutoresDisponiblesVesp"] option:selected') , function(i,e){
        	var personal = {};
        	personal = {"personal": { "id":$(e).val()}, "turno": "V"};
        	operadores[ii++] = personal;
        });

        $.each($('#divTablaMantto div.row'), function(irenglon, renglon){
        		var mantto = {};
        		console.log("-"+$(renglon).find('input[name="mantenimiento.desde"]').val())
        		mantto = {"desde": moment($(renglon).find('input[name="mantenimiento.desde"]').val(),"DD/MM/YYYY HH:mm").format("YYYY-MM-DD[T]HH:MM:00.000[Z]")
        				,"hasta": moment($(renglon).find('input[name="mantenimiento.hasta"]').val(),"DD/MM/YYYY hh:mm").format("YYYY-MM-DD[T]HH:MM:00.000[Z]")
        				,"tipo": {id : $(renglon).find('select[name="mantenimiento.tipo.id"] option:selected').val()}
        				,"motivo": $(renglon).find('input[name="mantenimiento.motivo"]').val()};
        		manttos[irenglon] = mantto;
        });

        sala["operadores"] = operadores;
        sala["mantenimiento"] = manttos;
        return sala;
    }