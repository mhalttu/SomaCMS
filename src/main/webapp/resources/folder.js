function documentClicked() {
    var row = $(this).closest('tr');
    var id = row.attr('id');
    location.href=id;
}

function createFolder(documentId) {
    bootbox.prompt("What is the name of the folder?", function(fileName) {
        if (fileName == null) {
        } else {
            $.ajax({
                url: contextPath + "/admin/api/documents/" + documentId  + "/children",
                type: "post",
                data: "type=folder&name=" + fileName,
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
                url: contextPath + "/admin/api/documents/" + currentDocument.id  + "/children",
                type: "post",
                data: "type=text&name=" + fileName,
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

function deleteDocumentOnRow() {
    var row = $(this).closest('tr');
    var id = row.attr('id');
    var selectedDocument = currentDocument.children[id]
    deleteDocument(id, selectedDocument.name, selectedDocument.folder, function (result) {
        location.reload();
    });
}

