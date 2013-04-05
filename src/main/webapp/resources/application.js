function documentClicked() {
    if (this.id == "") {
        location.href="0";
    } else {
        location.href=this.id;
    }
}

function createFolder() {
    bootbox.prompt("What is the name of the folder?", function(result) {
        if (result == null) {
        } else {
            $.ajax({
                url: window.location.pathname + "/folders",
                type: "post",
                data: "name=" + result,
                success: function(result) {
                    location.reload()
                },
                error: function() {
                    bootbox.alert("Failed to create folder " + name);
                }
            });
        }
    });
}

function createDocument() {
    bootbox.prompt("What is the name of the document?", function(fileName) {
        if (fileName == null) {
        } else {
            $.ajax({
                url: window.location.pathname + "/documents",
                type: "post",
                data: "name=" + fileName,
                success: function(result) {
                    if (result.success) {
                        location.href = result.documentId;
                    } else {
                        bootbox.alert("Failed to create document <b>" + fileName + "</b>. " + result.message);
                    }
                },
                error: function() {
                    bootbox.alert("Failed to create document " + name);
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
            notify("Document Saved!");
            window.editor.markClean();
            $('#save').attr('disabled', 'disabled');
        },
        error: function() {
            bootbox.alert("Could not save the changes. Sorry!");
        }
    });
}

function discardText() {
    bootbox.confirm("Are you sure you want to discard all changes?", function (result) {
        if (result) {
            alert("Discard not implemented");
            window.editor.discardChanges();
        }
    });

}

function deleteDocument(id, name, success) {
    bootbox.confirm("Are you sure you want to delete <b>" + name + "</b>?", function (result) {
        if (result) {
            $.ajax({
                url: id,
                type: "delete",
                success: success,
                error: function () {
                    bootbox.alert("Could not delete " + name + ". Sorry!");
                }
            });
        }
    });
}

function deleteDocumentOnRow() {
    event.stopImmediatePropagation();
    var row = $(this).closest('tr');
    var id = row.attr('id');
    var name = row.find(".document-name").html();
    deleteDocument(id, name, function (result) {
        location.reload();
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
                    location.reload();
                } else {
                    bootbox.alert("Failed to upload " + fileName + ". Please ask the administrator for details.");
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

function initializeEditor(editorMode) {
    window.editor = CodeMirror.fromTextArea(document.getElementById("code"), {
        mode: editorMode,
        tabMode: "indent",
        lineNumbers:true,
        matchBrackets:true,
        viewportMargin:Infinity
    });

    $('#save').attr('disabled', 'disabled');
    $('#discard').attr('disabled', 'disabled');
    editor.on("change", function() {
        $('#save').removeAttr('disabled');
        $('#discard').removeAttr('disabled');
    });

    window.onbeforeunload = function (e) {
        if (!editor.isClean()) {
            return 'Your document contains unsaved changes.'
        }
    };
}

function navigateToParent() {
    var parentId = textDocument.parentId;
    if (parentId == null) {
        parentId = 0;
    }
    location.href=parentId;
}

function notify(message) {
    $('.top-right').notify({
        type: "success",
        message: { html: message },
        fadeOut: { enabled: true, delay: 2000 }
    }).show();
}
