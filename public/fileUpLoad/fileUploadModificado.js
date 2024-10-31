(function ($) {
    var fileUploadCount = 0;

    $.fn.fileUpload = function () {
        return this.each(function () {
            var fileUploadDiv = $(this);
            var fileUploadId = `fileUpload-${++fileUploadCount}`;

            // Creates HTML content for the file upload area.
            var fileDivContent = `
                <label for="${fileUploadId}" class="file-upload">
                    <div>
                        <i class="material-icons-outlined">cloud_upload</i>
                        <p>Arrastra y suelta los archivos aqui</p>
                        <span>O</span>
                        <div>Explora archivos</div>
                        <br><br>
                        <p>
                         Para seleccionar más de un archivo, usa la tecla control.
                        </p>
                    </div>
                    <input type="file" id="${fileUploadId}" name="${fileUploadId}" multiple hidden  style="display:none" />
                </label>
            `;

            fileUploadDiv.html(fileDivContent).addClass("file-container");

            var table = null;
            var tableBody = null;
            // Creates a table containing file information.
            function createTable() {
                table = $(`
                    <table id="tblDetalle">
                        <thead>
                            <tr>
                                <th></th>
                                <th style="width: 20%;">Nombre archivo</th>
                                <th style="width: 10%;">Tamaño</th>
                                <th style="width: 10%;">Subido</th>
                                <th style="width: 25%;">Liga descarga</th>
                                <th style="width: 25%;">Liga borrado</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody>
                        </tbody>
                    </table>
                `);

                tableBody = table.find("tbody");
                fileUploadDiv.append(table);
            }

            // Adds the information of uploaded files to table.
            function handleFiles(files) {
                if (!table) {
                    createTable();
                }

                //tableBody.empty();
                if (files.length > 0) {
                    $.each(files, function (index, file) {
                        var fileName = file.name;
                        var fileSize = (file.size / 1024).toFixed(2) + " KB";
                        var auxTam =  parseInt( fileSize.split(" ")[0] );
                        if (auxTam>1000){
                            fileSize = (auxTam / 1024).toFixed(2)+ " MB";
                            auxTam =  parseInt( fileSize.split(" ")[0] );
                            if (auxTam>1000) {
                                fileSize = (auxTam / 1024).toFixed(2)+ " GB";

                            }
                        }

                        var fileType = file.type;
                        var preview = fileType.startsWith("image")
                            ? `<img src="${URL.createObjectURL(file)}" alt="${fileName}" height="30">`
                            : `<i class="material-icons-outlined">visibility_off</i>`;

                        tableBody.append(`
                            <tr>
                                <td>${index + 1}</td>
                                <td>${fileName}</td>

                                <td>${fileSize}</td>

                                <td><div id="subido-${index}"></td>
                                <td><div id="descarga-${index}"></td>
                                <td><div id="borrado-${index}"></td>
                                <td><button type="button" class="deleteBtn"><i class="material-icons-outlined">delete</i></button></td>
                            </tr>
                        `);
                    });

                    tableBody.find(".deleteBtn").click(function () {
                        console.log("deleteBtn...")
                        var boton = $(this);


                        swal({
                          title: "Eliminar el archivo de evidencia",
                          html: 'También se eliminará de tranzapp.',
                          type: "warning",
                          showCancelButton: true,
                          confirmButtonColor: "#d33",
                          cancelButtonColor: '#3085d6',
                          confirmButtonText: "Si, borrar",
                          cancelButtonText: "No, cancelar borrado.",
                          preConfirm: function(){
                              return new Promise(function (resolve, reject) {
                                    $(boton).closest("tr").remove();
                                    if (tableBody.find("tr").length === 0) {
                                        tableBody.append('<tr><td colspan="6" class="no-file">Sin archivos</td></tr>');
                                    }
                                    // Eliminar de tranzapp el archivo
                                    var liga = $(boton).closest("tr").find("td:eq(5)").find("a").attr("href");
                                    console.log(liga);
                                    liga = liga.replace("f.php","script.php");
                                    // De manera desatendida elimina el archivo en tranzapp
                                    var $eTA = LlamadaAjax("/apiEliminarTranzapp", "POST", JSON.stringify({liga:liga}));
                                    resolve();
                              });
                          }
                        }).then(function () {
                                  swal(
                    							    'Eliminado!',
                    							    'Se eliminó correctamente la evidencia.',
                    							    'success'
                    							  )
                    				}, function (dismiss) {
                    					  if (dismiss === 'cancel') {
                    					    swal(
                    					      'Cancelado',
                    					      'Usted canceló la eliminación de evidencia.',
                    					      'error'
                    					    )
                    					  }
                    				}).catch(swal.noop);
                    });
                }
            }

            // Events triggered after dragging files.
            fileUploadDiv.on({
                dragover: function (e) {
                    e.preventDefault();
                    fileUploadDiv.toggleClass("dragover", e.type === "dragover");
                },
                drop: function (e) {
                    e.preventDefault();
                    fileUploadDiv.removeClass("dragover");
                    handleFiles(e.originalEvent.dataTransfer.files);
                    subirEvidencias();
                },
            });

            // Event triggered when file is selected.
            fileUploadDiv.find(`#${fileUploadId}`).change(function () {
                handleFiles(this.files);
                subirEvidencias();
            });
        });
    };
})(jQuery);
