function newFolder() {
    bootbox.prompt("Folder Name?", function(result) {
        if (result === null) {
        } else {
            $.ajax({
                url: "new/",
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
