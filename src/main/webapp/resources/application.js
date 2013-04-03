function documentClicked() {
    if (this.id == "") {
        location.href="0";
    } else {
        location.href=this.id;
    }
}

function createFolder() {
    bootbox.prompt("Folder Name?", function(result) {
        if (result === null) {
        } else {
            $.ajax({
                url: window.location.pathname + "/folders",
                type: "post",
                data: "name=" + result,
                success: function(result) {
                    location.reload()
                },
                error: function() {
                    alert("Error");
                }
            });
        }
    });
}

function saveText(text, documentId) {
    $.ajax({
        type: "put",
        data: text,
        url: window.location.pathname,
        success: function(result) {
            location.href=documentId;
        },
        error: function() {
            alert("Error");
        }
    });
}

function deleteDocument() {
    event.stopImmediatePropagation();
    var row = $(this).closest('tr');
    var id = row.attr('id');
    var name = row.find(".document-name").html();
    bootbox.confirm("Are you sure you want to delete <b>" + name + "</b>?", function(result) {
        if (result) {
            $.ajax({
                url: id,
                type: "delete",
                success: function(result) {
                    location.reload();
                },
                error: function() {
                    alert("Error");
                }
            });
        }
    });
}

function initializeUploader() {
    var progressBar = $('#progress-bar');
    progressBar.hide();
    var uploader = new qq.FineUploaderBasic({
        button: $('#fine-uploader-basic')[0],
        request: {
            endpoint: window.location.pathname + '/files'
        },
        validation: {
        },
        callbacks: {
            onSubmit: function(id, fileName) {
            },
            onUpload: function(id, fileName) {
                progressBar.show();
            },
            onProgress: function(id, fileName, loaded, total) {
                $(".bar").width(loaded / total  + "%");
            },
            onComplete: function(id, fileName, responseJSON) {
                progressBar.hide();

                if (responseJSON.success) {
                    location.reload(); // TODO Better message, don't reload?
                } else {
                    bootbox.alert("Failed to upload " + fileName + " failed: " + responseJSON.rea);
                }
            }
        }
    });
}

function initializeSearch() {
    window.pathToId = {};
    $('#search').typeahead({
        source:function(query,process){
            if( typeof searching != "undefined") {
                clearTimeout(searching);
                process([]);
            }
            searching = setTimeout(function() {
                return $.getJSON(
                    "../search/",
                    { query:query },
                    function(data){
                        var paths = [];
                        $.each(data, function(){
                            paths.push(this.path);
                            pathToId[this.path] = this.id;
                        });
                        pathToId[data.path] = data.id;
                        return process(paths);
                    }
                );
            }, 300); // 300 ms
        },
        updater:function(item) {
            location.href = window.pathToId[item];
        }
    });
}
