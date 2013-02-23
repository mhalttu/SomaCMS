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

function showDocument(id, viewable) {
    if (!viewable) {
        return;
    }
    location.href = "/admin/view/" + id + "/";
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