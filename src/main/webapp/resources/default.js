function newFolder() {
    bootbox.prompt("Folder Name?", function(result) {
        if (result === null) {
        } else {
            location.href = location.href + "/newFolder";
        }
    });
}
