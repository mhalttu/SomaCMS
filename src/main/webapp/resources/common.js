function newFolder() {
    bootbox.prompt("Folder Name?", function(result) {
        if (result === null) {
        } else {
            $.ajax({
                url: "newFolder/",
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

function saveText(text, parentId) {
    $.ajax({
        url: "save/",
        type: "put",
        data: "contents=" + text,
        success: function(result) {
            location.href="/admin/view/" + parentId + "/";
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
    bootbox.confirm("Delete " + name + "?", function(result) {
        if (result) {
            $.ajax({
                url: "delete/" + id + "/",
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
