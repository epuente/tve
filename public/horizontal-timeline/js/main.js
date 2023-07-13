
	var timelines = $('.cd-horizontal-timeline'),
		eventsMinDistance = 60;

	

	function initTimeline(timelines) {
		timelines.each(function(){
			var timeline = $(this),
				timelineComponents = {};
			//cache timeline components 
			timelineComponents['timelineWrapper'] = timeline.find('.events-wrapper');
			timelineComponents['eventsWrapper'] = timelineComponents['timelineWrapper'].children('.events');
			timelineComponents['fillingLine'] = timelineComponents['eventsWrapper'].children('.filling-line');
			timelineComponents['timelineEvents'] = timelineComponents['eventsWrapper'].find('a');
			timelineComponents['timelineDates'] = parseDate(timelineComponents['timelineEvents']);
			timelineComponents['eventsMinLapse'] = minLapse(timelineComponents['timelineDates']);
			timelineComponents['timelineNavigation'] = timeline.find('.cd-timeline-navigation');
			timelineComponents['eventsContent'] = timeline.children('.events-content');

			//assign a left postion to the single events along the timeline
			setDatePosition(timelineComponents, eventsMinDistance);
			//assign a width to the timeline
			var timelineTotWidth = setTimelineWidth(timelineComponents, eventsMinDistance);
			//the timeline has been initialize - show it
			timeline.addClass('loaded');

			//detect click on the next arrow
			timelineComponents['timelineNavigation'].on('click', '.next', function(event){
				event.preventDefault();
				updateSlide(timelineComponents, timelineTotWidth, 'next');
			});
			//detect click on the prev arrow
			timelineComponents['timelineNavigation'].on('click', '.prev', function(event){
				event.preventDefault();
				updateSlide(timelineComponents, timelineTotWidth, 'prev');
			});
			//detect click on the a single event - show new event content
			timelineComponents['eventsWrapper'].on('click', 'a', function(event){
				console.log("Modificando para hacerlo serverside!!!!")
				var fecha = $(this)[0].dataset.date;
				
				event.preventDefault();
				timelineComponents['timelineEvents'].removeClass('selected');
				$(this).addClass('selected');
				updateOlderEvents($(this));
				updateFilling($(this), timelineComponents['fillingLine'], timelineTotWidth);
				console.log( timelineComponents['eventsContent'] )
				updateVisibleContent($(this), timelineComponents['eventsContent']);
				console.dir( fecha )
				$.ajax({
			        url: '/ajaxTableroEventosDiaTimeLine',
			        method: 'POST',
	                data: JSON.stringify( {fecha: fecha}),
	                contentType: "application/json; charset=utf-8",
	                dataType: "json"
			    }).success(function(data){
			    	console.log(data);
			    	var aux='<div style="font-size: x-large;">Programación para el día '+ moment(data[0].inicio, "YYYY-MM-DDTHH:mm:ss.SSSSZ").format("DD/MM/YYYY")+'</div><br/>';
			    	
			    	if (data[0].folioproductorasignado.folio.oficio.observacion){
	                 	aux+="<div class='table'>";
	                 	aux+="  <div class='table-row'>";
	                 	aux+="    <div class='table-cell bg-warning' style='vertical-align:middle; border-right:none'><i class='fas fa-exclamation fa-2x'></i></div>";
	                 	aux+="    <div class='table-cell bg-warning' style='vertical-align:middle; border-left:none'><strong>Observación del oficio</strong></br>"+data[0].folioproductorasignado.folio.oficio.observacion+"</div>";
	                 	aux+="  </div>";
	                 	aux+="</div>";			    	
			    	}
			    	
			    	if (data[0].folioproductorasignado.folio.observacion){
	                 	aux+="<div class='table'>";
	                 	aux+="  <div class='table-row'>";
	                 	aux+="    <div class='table-cell bg-warning' style='vertical-align:middle; border-right:none'><i class='fas fa-exclamation fa-2x'></i></div>";
	                 	aux+="    <div class='table-cell bg-warning' style='vertical-align:middle; border-left:none'><strong>Observación del folio</strong></br>"+data[0].folioproductorasignado.folio.observacion+"</div>";
	                 	aux+="  </div>";
	                 	aux+="</div>";			    	
			    	}
			    	
			    	aux+='<table class="table table-bordered table-striped jambo_table bulk_action order-column dataTable no-footer" id="tablaTimeLine">';
			    	aux+="<thead><tr>    <th>Salida</th><th>Desde</th><th>hasta</th>    <th>Folio</th>    <th>Detalle</th>   </tr>  </thead><tbody></tbody>";
			    	
			    	
			    	/*
			    	data.forEach(function(e){
			    		aux+='<tr>';
			    		aux+='  <td>';
			    		if (e.salidas.length!=0 ){
			    			var hs=moment(e.salidas[0].salida, "YYYY-MM-DDTHH:mm:ss.SSSSZ");
			    			var hi=moment(e.inicio, "YYYY-MM-DDTHH:mm:ss.SSSSZ");
			    			var color="bg-green";
			    			if (hs.isSameOrAfter(hi))
			    				color="bg-red"
			    			aux+='<span class="badge '+color+'">salida: '+moment(e.salidas[0].salida, "YYYY-MM-DDTHH:mm:ss.SSSSZ").format("HH:mm")+'</span>&nbsp;';
			    		}
			    		aux+=moment(e.inicio, "YYYY-MM-DDTHH:mm:ss.SSSSZ").format("HH:mm")+' - '+moment(e.fin, "YYYY-MM-DDTHH:mm:ss.SSSSZ").format("HH:mm");
			    		aux+='</td>';
			    		aux+='  <td>'+e.folioproductorasignado.folio.folio+'</td>';
			    		aux+='  <td>'+e.folioproductorasignado.folio.oficio.descripcion+'</td>';
			    		aux+='  <td>'+e.fase.descripcion+'</td>';
			    		aux+='</tr>';
			    	});
			    	
			    	
			    	aux+='</table>';
			    	*/
			    	$("div.events-content").html(aux);
			    	
			    	
			    	//Mostrar la info en la tabla de detalle
	                 for(f=0; f<=data.length-1;f++){
	                	 r = data[f];

	                     var c = "";
	                     c="<td>";
				    		if (r.salidas.length!=0 ){
				    			console.log("SALIDA")
				    			var hs=moment(r.salidas[0].salida, "YYYY-MM-DDTHH:mm:ss.SSSSZ");
				    			var hi=moment(r.inicio, "YYYY-MM-DDTHH:mm:ss.SSSSZ");
				    			console.log(hs.format("HH:mm"))
				    			var color="bg-green";
				    			if (hs.isSameOrAfter(hi))
				    				color="bg-red"
				    			var aux=hs.format("HH:mm");
				    			c+=aux;
				    		}
				    		
                            if ( r.vehiculos.length!=0 ){
	                         	c+='</br><span class="badge"';	                         	
                         		c+='data-toggle="popover" data-title="Vehículo" data-content="Auto asignado" data-trigger="hover" data-container="body"';
	                         	c+='><i class="fas fa-car"></i>';
	                         	for(j=0; j< r.vehiculos.length; j++){
                         			c+=" "+r.vehiculos[j].vehiculo.placa;
                         			c+=" "+r.vehiculos[j].vehiculo.descripcion.toLowerCase();
                         		}
	                         	c+='</span>'; 
	                         }				    		
				    		
				    	 c+="</td>";
				    	 
                         c+="<td>"+moment(r.inicio, "YYYY-MM-DDTHH:mm:ss.SSSSZ").format("HH:mm")+"</td>";
                         
                         c+="<td>"+moment(r.fin, "YYYY-MM-DDTHH:mm:ss.SSSSZ").format("HH:mm")+"</td>";
                         
                         c+="<td>"+r.folioproductorasignado.folio.numfolio+"</br>"+r.folioproductorasignado.folio.oficio.descripcion+"</td>";
                         
	                     c+="<td>";
	                     c+="<div style='font-size:large; padding-bottom:0.3em'>";
	                     
	                     c+="<p><strong>Productor: </strong>"+r.folioproductorasignado.personal.nombre+" "+r.folioproductorasignado.personal.paterno+" "+r.folioproductorasignado.personal.materno+"</p>";
	                     
	                     c+="<strong>"+r.fase.descripcion+"</strong></div>";
                         if (r.locaciones.length>0){
                        	 if (r.locaciones[0].locacion != null){
                            	 c+="<p><strong>Locación</strong></br>";
                            	 c+=r.locaciones[0].locacion;
                            	 c+="</p>";
                        	 }
                         }
                         if (r.salas.length >0){
                        	c+="<p>";
                         	c+="<span class='badge'>"+r.salas[0].sala.descripcion
                         	if ( r.salas[0].usoscabina.length>0 ){
                         		var uc="";
                         		switch (r.salas[0].usoscabina[0].usocabina){
                         			case "M": uc="<i class='fa fa-music'></i>"; break;  
                         			case "V": uc="<i class='fa fa-comment'></i>"; break;
                         			case "I": uc="<i class='fa fa-download'></i>"; break;
                         			case "C": uc="<i class='fa fa-check'></i>"; break;  
                         			case "R": uc="<i class='fa fa-flag'></i>"; break;
                         		}
                         		c+="&nbsp;&nbsp;"+uc;
                         	} else {
                         		// No aplica uso de cabina
                         		c+="&nbsp;&nbsp;<i class='fas fa-pencil-alt'></i>"
                         	}
                         	c+="</span>";
                         	c+="</p>";
                         }	                                          
	                     //if (r.personal.length!=0){
		console.log("*************************************************************************");
		console.dir(r)
						if (r.cuentaRol.length!=0){
	                    	 var auxPersonal="<p><strong>Personal</strong></br>";
                        	 for (var j = 0; j<r.cuentaRol.length; j++){
								 var per = r.cuentaRol[j].cuentarol.cuenta.personal;	                                		 
                        		 auxPersonal+=per.nombre+" "+per.paterno+" "+per.materno+"</br>";
                        	 }
                        	 auxPersonal+="</p>";                        	 
                        	 c+= auxPersonal;
	                     }
                         if (r.equipos.length >0 || r.accesorios.length >0 ){
                        	 c+="<p><strong>Equipo y accesorios</strong>";
                        	                                  
                             if (r.equipos.length>0){
                            	 for (var j = 0; j<r.equipos.length; j++){
                                	 c+="<div class='mail-list' style= 'width: 100%; display: inline-block; line-height: initial;'>";         
                                	 c+="<div class='left' style='    width: 5%; float:left;  margin-right:0.1em;'>-</div>";
                                	 c+="<div class='right' style='width: 90%;  float: left;'>";	                                	 
                            		 c+=""+r.equipos[j].equipo.descripcion+" "+r.equipos[j].equipo.modelo+" "+r.equipos[j].equipo.marca+" "+r.equipos[j].equipo.serie+" "+r.equipos[j].equipo.observacion+"";
                            		 if (r.equipos[j].equipo.estado.id!=1 && moment(r.inicio).isSameOrAfter( moment(), 'day'  ))
                            			 c+=" <div class='conError mismaLinea'>"+r.equipos[j].equipo.estado.descripcion+"</div>";
	                                 c+="</div>";
	                                 c+="</div>";                                		 
                            	 }
                             }                                
                             if (r.accesorios.length >0){                                	 
                            	 for (var j = 0; j<r.accesorios.length; j++){
                                	 c+="<div class='mail-list' style= 'width: 100%; display: inline-block; line-height: initial;'>";         
                                	 c+="<div class='left' style='    width: 5%;   float: left;  margin-right: 0.1em;'>-</div>";
                                	 c+="<div class='right' style='width: 90%;  float: left;'>";		                                		 
                            		 c+=""+r.accesorios[j].accesorio.descripcion+" "+r.accesorios[j].accesorio.modelo+" "+r.accesorios[j].accesorio.serie+" "+r.accesorios[j].accesorio.observacion;
                            		 if (r.accesorios[j].accesorio.estado.id!=1 && moment(r.inicio).isSameOrAfter( moment(), 'day'  ))
                            			 c+=" <div class='conError mismaLinea'>"+r.accesorios[j].accesorio.estado.descripcion+"</div>";
	                                 c+="</div>";
	                                 c+="</div>";
                            	 }
                              }       
                         	c+="</p>";
                         	
                         }	                     
                         if (r.formatos.length>0){

                            console.dir(r)

                        	 c+="<p><strong>Formatos</strong></br>";
                        	 for (var j = 0; j<r.formatos.length; j++){
                        		 if (r.formatos[j].cantidad>0)
                        		 	c+=r.formatos[j].formato.descripcion+": "+r.formatos[j].cantidad+"</br>";	 
                        	 }
                        	 c+="</p>";
                        	 console.log("FORMATOS ",c)
                         }                         
                         if (r.observacion){
                         	c+="<div class='table'>";
                           	c+="  <div class='table-row'>";
                           	c+="    <div class='table-cell bg-warning' style='vertical-align:middle; border-right:none'><i class='fas fa-exclamation fa-2x'></i></div>";
                           	c+="    <div class='table-cell bg-warning' style='vertical-align:middle; border-left:none'><strong>Observacion </strong></br>"+r.observacion+"</div>";
                           	c+="  </div>";
                           	c+="</div>";  
                           	console.log("OBSERVACION ",c)
                         }
                         c+="</td>";
                         $("#tablaTimeLine tbody").append("<tr>"+c+"</tr>");	
                         console.log("TODO  ", "<tr>"+c+"</tr>"  )
	                 }
	                     			    	
	                
			    }).error(function(){
			    	alert("Error en ajax.")
			    });
				
			});

			//on swipe, show next/prev event content
			timelineComponents['eventsContent'].on('swipeleft', function(){
				var mq = checkMQ();
				( mq == 'mobile' ) && showNewContent(timelineComponents, timelineTotWidth, 'next');
			});
			timelineComponents['eventsContent'].on('swiperight', function(){
				var mq = checkMQ();
				( mq == 'mobile' ) && showNewContent(timelineComponents, timelineTotWidth, 'prev');
			});

			//keyboard navigation
			$(document).keyup(function(event){
				if(event.which=='37' && elementInViewport(timeline.get(0)) ) {
					showNewContent(timelineComponents, timelineTotWidth, 'prev');
				} else if( event.which=='39' && elementInViewport(timeline.get(0))) {
					showNewContent(timelineComponents, timelineTotWidth, 'next');
				}
			});
		});
	}

	function updateSlide(timelineComponents, timelineTotWidth, string) {
		//retrieve translateX value of timelineComponents['eventsWrapper']
		var translateValue = getTranslateValue(timelineComponents['eventsWrapper']),
			wrapperWidth = Number(timelineComponents['timelineWrapper'].css('width').replace('px', ''));
		//translate the timeline to the left('next')/right('prev') 
		(string == 'next') 
			? translateTimeline(timelineComponents, translateValue - wrapperWidth + eventsMinDistance, wrapperWidth - timelineTotWidth)
			: translateTimeline(timelineComponents, translateValue + wrapperWidth - eventsMinDistance);
	}

	function showNewContent(timelineComponents, timelineTotWidth, string) {
		//go from one event to the next/previous one
		var visibleContent =  timelineComponents['eventsContent'].find('.selected'),
			newContent = ( string == 'next' ) ? visibleContent.next() : visibleContent.prev();

		if ( newContent.length > 0 ) { //if there's a next/prev event - show it
			var selectedDate = timelineComponents['eventsWrapper'].find('.selected'),
				newEvent = ( string == 'next' ) ? selectedDate.parent('li').next('li').children('a') : selectedDate.parent('li').prev('li').children('a');
			
			updateFilling(newEvent, timelineComponents['fillingLine'], timelineTotWidth);
			updateVisibleContent(newEvent, timelineComponents['eventsContent']);
			newEvent.addClass('selected');
			selectedDate.removeClass('selected');
			updateOlderEvents(newEvent);
			updateTimelinePosition(string, newEvent, timelineComponents);
		}
	}

	function updateTimelinePosition(string, event, timelineComponents) {
		//translate timeline to the left/right according to the position of the selected event
		if ( event.get(0)!=undefined  ){
			var eventStyle = window.getComputedStyle(event.get(0), null),
				eventLeft = Number(eventStyle.getPropertyValue("left").replace('px', '')),
				timelineWidth = Number(timelineComponents['timelineWrapper'].css('width').replace('px', '')),
				timelineTotWidth = Number(timelineComponents['eventsWrapper'].css('width').replace('px', ''));
			var timelineTranslate = getTranslateValue(timelineComponents['eventsWrapper']);
	
	        if( (string == 'next' && eventLeft > timelineWidth - timelineTranslate) || (string == 'prev' && eventLeft < - timelineTranslate) ) {
	        	translateTimeline(timelineComponents, - eventLeft + timelineWidth/2, timelineWidth - timelineTotWidth);
	        }
		}
	}

	function translateTimeline(timelineComponents, value, totWidth) {
		var eventsWrapper = timelineComponents['eventsWrapper'].get(0);
		value = (value > 0) ? 0 : value; //only negative translate value
		value = ( !(typeof totWidth === 'undefined') &&  value < totWidth ) ? totWidth : value; //do not translate more than timeline width
		setTransformValue(eventsWrapper, 'translateX', value+'px');
		//update navigation arrows visibility
		(value == 0 ) ? timelineComponents['timelineNavigation'].find('.prev').addClass('inactive') : timelineComponents['timelineNavigation'].find('.prev').removeClass('inactive');
		(value == totWidth ) ? timelineComponents['timelineNavigation'].find('.next').addClass('inactive') : timelineComponents['timelineNavigation'].find('.next').removeClass('inactive');
	}

	function updateFilling(selectedEvent, filling, totWidth) {
		console.log("*********",selectedEvent.get(0))
			if ( selectedEvent.get(0)!=undefined  ){
			//change .filling-line length according to the selected event
			var eventStyle = window.getComputedStyle(selectedEvent.get(0), null),
				eventLeft = eventStyle.getPropertyValue("left"),
				eventWidth = eventStyle.getPropertyValue("width");
			eventLeft = Number(eventLeft.replace('px', '')) + Number(eventWidth.replace('px', ''))/2;
			var scaleValue = eventLeft/totWidth;
			setTransformValue(filling.get(0), 'scaleX', scaleValue);
		}
	}

	function setDatePosition(timelineComponents, min) {
		for (i = 0; i < timelineComponents['timelineDates'].length; i++) { 
		    var distance = daydiff(timelineComponents['timelineDates'][0], timelineComponents['timelineDates'][i]),
		    	distanceNorm = Math.round(distance/timelineComponents['eventsMinLapse']) + 2;
		    timelineComponents['timelineEvents'].eq(i).css('left', distanceNorm*min+'px');
		}
	}

	function setTimelineWidth(timelineComponents, width) {
		var timeSpan = daydiff(timelineComponents['timelineDates'][0], timelineComponents['timelineDates'][timelineComponents['timelineDates'].length-1]),
			timeSpanNorm = timeSpan/timelineComponents['eventsMinLapse'],
			timeSpanNorm = Math.round(timeSpanNorm) + 4,
			totalWidth = timeSpanNorm*width;
		timelineComponents['eventsWrapper'].css('width', totalWidth+'px');
		updateFilling(timelineComponents['eventsWrapper'].find('a.selected'), timelineComponents['fillingLine'], totalWidth);
		updateTimelinePosition('next', timelineComponents['eventsWrapper'].find('a.selected'), timelineComponents);
	
		return totalWidth;
	}

	function updateVisibleContent(event, eventsContent) {
		var eventDate = event.data('date'),
			visibleContent = eventsContent.find('.selected'),
			selectedContent = eventsContent.find('[data-date="'+ eventDate +'"]'),
			selectedContentHeight = selectedContent.height();

		if (selectedContent.index() > visibleContent.index()) {
			var classEnetering = 'selected enter-right',
				classLeaving = 'leave-left';
		} else {
			var classEnetering = 'selected enter-left',
				classLeaving = 'leave-right';
		}

		selectedContent.attr('class', classEnetering);
		visibleContent.attr('class', classLeaving).one('webkitAnimationEnd oanimationend msAnimationEnd animationend', function(){
			visibleContent.removeClass('leave-right leave-left');
			selectedContent.removeClass('enter-left enter-right');
		});
		eventsContent.css('height', selectedContentHeight+'px');
	}

	function updateOlderEvents(event) {
		event.parent('li').prevAll('li').children('a').addClass('older-event').end().end().nextAll('li').children('a').removeClass('older-event');
	}

	function getTranslateValue(timeline) {
		var timelineStyle = window.getComputedStyle(timeline.get(0), null),
			timelineTranslate = timelineStyle.getPropertyValue("-webkit-transform") ||
         		timelineStyle.getPropertyValue("-moz-transform") ||
         		timelineStyle.getPropertyValue("-ms-transform") ||
         		timelineStyle.getPropertyValue("-o-transform") ||
         		timelineStyle.getPropertyValue("transform");

        if( timelineTranslate.indexOf('(') >=0 ) {
        	var timelineTranslate = timelineTranslate.split('(')[1];
    		timelineTranslate = timelineTranslate.split(')')[0];
    		timelineTranslate = timelineTranslate.split(',');
    		var translateValue = timelineTranslate[4];
        } else {
        	var translateValue = 0;
        }

        return Number(translateValue);
	}

	function setTransformValue(element, property, value) {
		element.style["-webkit-transform"] = property+"("+value+")";
		element.style["-moz-transform"] = property+"("+value+")";
		element.style["-ms-transform"] = property+"("+value+")";
		element.style["-o-transform"] = property+"("+value+")";
		element.style["transform"] = property+"("+value+")";
	}

	//based on http://stackoverflow.com/questions/542938/how-do-i-get-the-number-of-days-between-two-dates-in-javascript
	function parseDate(events) {
		var dateArrays = [];
		events.each(function(){
			var singleDate = $(this),
				dateComp = singleDate.data('date').split('T');
			if( dateComp.length > 1 ) { //both DD/MM/YEAR and time are provided
				var dayComp = dateComp[0].split('/'),
					timeComp = dateComp[1].split(':');
			} else if( dateComp[0].indexOf(':') >=0 ) { //only time is provide
				var dayComp = ["2000", "0", "0"],
					timeComp = dateComp[0].split(':');
			} else { //only DD/MM/YEAR
				var dayComp = dateComp[0].split('/'),
					timeComp = ["0", "0"];
			}
			var	newDate = new Date(dayComp[2], dayComp[1]-1, dayComp[0], timeComp[0], timeComp[1]);
			dateArrays.push(newDate);
		});
	    return dateArrays;
	}

	function daydiff(first, second) {
	    return Math.round((second-first));
	}

	function minLapse(dates) {
		//determine the minimum distance among events
		var dateDistances = [];
		for (i = 1; i < dates.length; i++) { 
		    var distance = daydiff(dates[i-1], dates[i]);
		    dateDistances.push(distance);
		}
		return Math.min.apply(null, dateDistances);
	}

	/*
		How to tell if a DOM element is visible in the current viewport?
		http://stackoverflow.com/questions/123999/how-to-tell-if-a-dom-element-is-visible-in-the-current-viewport
	*/
	function elementInViewport(el) {
		var top = el.offsetTop;
		var left = el.offsetLeft;
		var width = el.offsetWidth;
		var height = el.offsetHeight;

		while(el.offsetParent) {
		    el = el.offsetParent;
		    top += el.offsetTop;
		    left += el.offsetLeft;
		}

		return (
		    top < (window.pageYOffset + window.innerHeight) &&
		    left < (window.pageXOffset + window.innerWidth) &&
		    (top + height) > window.pageYOffset &&
		    (left + width) > window.pageXOffset
		);
	}

	function checkMQ() {
		//check if mobile or desktop device
		return window.getComputedStyle(document.querySelector('.cd-horizontal-timeline'), '::before').getPropertyValue('content').replace(/'/g, "").replace(/"/g, "");
	}
