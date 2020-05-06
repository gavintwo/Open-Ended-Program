/*
document.addEventListener("click", function () {
    socket.emit("mouseClick");
});

 */

document.addEventListener("keydown", function(event) {

    if (event.code === 'KeyA') {
        socket.emit("left");
        console.log("HTML sent a message to move left");
    }
    if (event.code === 'KeyD') {
        socket.emit("right");
        console.log("HTML sent a message to move right");
    }
    if (event.code === 'KeyW') {
        socket.emit("up");
        console.log("HTML sent a message to move up");
    }
    if (event.code === 'KeyS') {
        socket.emit("down");
        console.log("HTML sent a message to move down");
    }
    if (event.code === 'KeyE') {
        socket.emit("usePotion");
        console.log("HTML sent a message to use a potion");
    }
    if (event.code === "Space") {
        socket.emit("attack");
        console.log("HMTL sent a message to attack");
    }
});


