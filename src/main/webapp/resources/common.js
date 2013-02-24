function folders() {
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
        data: "contents=" + text,
        success: function(result) {
            location.href="/admin/document/" + documentId;
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
                url: "/admin/document/" + id,
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
