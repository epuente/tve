    $("#filtroOpera, #filtroMantto").click(function(){
        toggleFiltro(this);
        
        
    });    
    
    function toggleFiltro(e){       
        $("div[name='filtroEdo']").css("font-weight", "normal");
        $("div[name='filtroEdo']").css("border-bottom","3px solid #F2F2F2");
        $(e).css("font-weight", "bold");
        $(e).css("border-bottom","3px solid "+$(e).css("color"));
alert( $(e).prop("id"));        
        $("#divOperador").toggle( $(e).prop("id")=="filtroOpera"  );
        $("#divMantto").toggle( $(e).prop("id")=="filtroMantto"  );
    }
        
    

    
    

    
    
    function eliminaMantto(op){
    	$("#divTablaMantto div.row:eq("+op+")").remove();
    }    
    
    