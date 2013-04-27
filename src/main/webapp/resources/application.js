function deleteDocument(id, name, folder, success) {
    var message = "Are you sure you want to delete <b>" + name + "</b>?";
    if (folder) {
        message = message + " <span class='danger'>Everything inside the folder will be deleted.</span> ";
    }
    bootbox.confirm(message, function (result) {
        if (result) {
            $.ajax({
                url: contextPath + "/admin/api/documents/" + id,
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

function initializeUploader(document) {
    var progressBar = $('#progress-bar');
    progressBar.hide();
    var uploader = new qq.FineUploaderBasic({
        button: $('#upload')[0],
        request: {
            endpoint: contextPath + '/admin/api/documents/' + document.id + (document.folder ? '/children' : ''),
            params:{type:"upload"}
        },
        validation: {
        },
        callbacks: {
            onSubmit: function(id, fileName) {
                if (!document.folder && fileName != document.name) {
                    bootbox.alert("The name of the current file <b>" + document.name + "</b> is different from the uploaded file <b>" + fileName + "</b>");
                    return false;
                }
                return true;
            },
            onUpload: function(id, fileName) {
                progressBar.show();
            },
            onProgress: function(id, fileName, loaded, total) {
                $(".bar").width((loaded / total * 100)  + "%");
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
                    contextPath + "/admin/api/documents",
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
