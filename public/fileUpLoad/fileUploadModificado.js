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
                        <p>Arrastrar y soltar los archivos aqui</p>
                        <p>O</p>
                        <div>Explorar archivos</div>
                        <br><br>
                        <p class="text-muted" style="color:#777; font-weight:normal;">
                         Al explorar, para seleccionar más de un archivo se debe presionar la tecla <i>control</i> mientras se seleccionan los archivos.
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

                tableBody.empty();
                if (files.length > 0) {
                    $.each(files, function (index, file) {
                        var fileName = file.name;
                        //var fileSize = (file.size / 1024).toFixed(2) + " KB";
                        var fileSize = (file.size / 1024).toFixed(2) + " KB";
                        if (fileSize.substring(0,  fileSize.length-3 )>=1000)
                            fileSize = (fileSize.substring(0,  fileSize.length-3 ) / 1000).toFixed(2) + " MB";
                        if (fileSize.substring(0,  fileSize.length-3 )>=1000)
                            fileSize = (fileSize.substring(0,  fileSize.length-3 ) / 1000).toFixed(2) + " GB";
                        if (fileSize.substring(0,  fileSize.length-3 )>=1000)
                            fileSize = (fileSize.substring(0,  fileSize.length-3 ) / 1000).toFixed(2) + " TB";
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
                        $(this).closest("tr").remove();

                        if (tableBody.find("tr").length === 0) {
                            tableBody.append('<tr><td colspan="6" class="no-file">Sin archivos</td></tr>');
                        }
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
                },
            });

            // Event triggered when file is selected.
            fileUploadDiv.find(`#${fileUploadId}`).change(function () {
                handleFiles(this.files);
            });
        });
    };
})(jQuery);
