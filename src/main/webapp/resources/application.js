function documentClicked() {
    if (this.id == "") {
        location.href="0";
    } else {
        location.href=this.id;
    }
}

function createFolder(documentId) {
    bootbox.prompt("What is the name of the folder?", function(fileName) {
        if (fileName == null) {
        } else {
            $.ajax({
                url: contextPath + "/admin/api/document/" + documentId  + "/folders",
                type: "post",
                data: "name=" + fileName,
                success: function(result) {
                    if (result.success) {
                        location.reload()
                    } else {
                        bootbox.alert("Failed to create folder <b>" + fileName + "</b>. " + result.explanation);
                    }
                },
                error: function(xhr) {
                    if (xhr.status == 403) {
                        location.href="/login/";
                    } else {
                        bootbox.alert("Failed to create folder " + name);
                    }
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
                url: contextPath + "/admin/api/document/" + currentDocument.id  + "/documents",
                type: "post",
                data: "name=" + fileName,
                success: function(result) {
                    if (result.success) {
                        location.href = result.documentId;
                    } else {
                        bootbox.alert("Failed to create document <b>" + fileName + "</b>. " + result.explanation);
                    }
                },
                error: function(xhr) {
                    if (xhr.status == 403) {
                        location.href="/login/";
                    } else {
                        bootbox.alert("Failed to create document " + name);
                    }
                }
            });
        }
    });

}

function saveText(text, documentId) {
    $.ajax({
        type: "put",
        data: text,
        url: contextPath + "/admin/api/document/" + documentId + "/save",
        success: function(result) {
            notify("Document Saved!");
            window.editor.markClean();
            updateEditorButtons(false);
        },
        error: function() {
            if (xhr.status == 403) {
                location.href="/login/";
            } else {
                bootbox.alert("Could not save the changes. Sorry!");
            }
        }
    });
}

function discardText() {
    bootbox.confirm("Are you sure you want to discard all changes?", function (result) {
        if (result) {
            window.onbeforeunload = null;
            location.reload();
        }
    });
}

function deleteDocument(id, name, folder, success) {
    var message = "Are you sure you want to delete <b>" + name + "</b>?";
    if (folder) {
        message = message + " <span class='danger'>Everything inside the folder will be deleted.</span> ";
    }
    bootbox.confirm(message, function (result) {
        if (result) {
            $.ajax({
                url: contextPath + "/admin/api/document/" + id,
                type: "delete",
                success: success,
                error: function () {
                    if (xhr.status == 403) {
                        location.href="/login/";
                    } else {
                        bootbox.alert("Could not delete " + name + ". Sorry!");
                    }
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
    deleteDocument(id, name, true, function (result) {
        location.reload();
    });
}

function initializeUploader(document) {
    var progressBar = $('#progress-bar');
    progressBar.hide();
    var uploader = new qq.FineUploaderBasic({
        button: $('#upload')[0],
        request: {
            endpoint: document.folder ? contextPath + '/admin/api/document/' + document.id + '/files' : contextPath + '/admin/api/document/' + document.id + '/replace'
        },
        validation: {
        },
        callbacks: {
            onSubmit: function(id, fileName) {
                if (!document.folder && fileName != document.name) {
                    bootbox.alert("The name of the current file <b>" + document.name + "</b> is different from the uploaded file <b>" + fileName + "</b>");
                    return false;
                }
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
                    bootbox.alert("Failed to upload <b>" + fileName + "</b>. " + responseJSON.explanation);
                }
            }
        }
    });
    $("#upload").click(function() {
        return ($('#upload').attr('disabled') != 'disabled');
    })
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
                    contextPath + "/admin/api/search/",
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

function updateEditorButtons(unsavedChanges) {
    $('#save').attr('disabled', !unsavedChanges);
    $('#discard').attr('disabled', !unsavedChanges);
    $('#upload').attr('disabled', unsavedChanges);
}

function initializeEditor(editorMode) {
    window.editor = CodeMirror.fromTextArea(document.getElementById("code"), {
        mode: editorMode,
        tabMode: "indent",
        lineNumbers:true,
        matchBrackets:true,
        viewportMargin:Infinity
    });

    updateEditorButtons(false);
    editor.on("change", function() {
        updateEditorButtons(true);
    });

    window.onbeforeunload = function (e) {
        if (!editor.isClean()) {
            return 'Your document contains unsaved changes.'
        }
    };
}

function navigateToParent() {
    window.onbeforeunload = null;
    location.href=currentDocument.parentId;
}

function notify(message) {
    $('.top-center').notify({
        type: "success",
        message: { html: message },
        fadeOut: { enabled: true, delay: 2000 }
    }).show();
}
