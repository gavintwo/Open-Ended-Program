//const socket = io.connect("http://localhost:8080", {transports: ['websocket']});

setupSocket();

function setupSocket() {

    socket.on('initialize', function (event) {

        let gameHTML = '<p id="health">"hey1"</p>';
        gameHTML += '<p id="potionsHeld"></p>';
        gameHTML += '<p id="armor"></p>';
        gameHTML += '<p id="attack"></p>';
        gameHTML += '<p id="println3"></p>';
        gameHTML += '<p id="println2"></p>';
        gameHTML += '<p id="println"></p>';
        gameHTML += '<p id="enemyHealth"></p>';
        gameHTML += '<p id="enemyAttack"></p>';
        gameHTML += '<p id="enemyArmor"></p>';


        document.getElementById('game').innerHTML = gameHTML
    });


    socket.on('gameState', function (event) {
        const gameState = JSON.parse(event);

        context.clearRect(0, 0, windowWidth, windowHeight);
        drawGameBoard(gameState['tiles']);
        drawPlayers(gameState['location']);
        drawHealth(gameState['health']);
        drawArmor(gameState['armor']);
        drawHealthPotions(gameState['potion']);
        drawAttack(gameState['attack']);
        drawEnemyHealth(gameState['enemyHealth']);
        drawEnemyAttack(gameState['enemyAttack']);
        drawEnemyArmor(gameState['enemyArmor'])


    });

}

function drawGameBoard(tiles) {
    for (let i = 0; i < tiles.length; i++) {
        for (let j = 0; j < tiles[i].length; j++) {
            const tile = tiles[i][j];
            let color = '#fc03e3';
            if (tile.type === "path") {
                color = '#4c4d45';
            }
            else if (tile.type === "water") {
                color = '#0caff5'
            }
            else if (tile.type === "lava") {
                color = '#f54a0c'
            }
            else if (tile.type === "treasure chest") {
                color = '#6b461b'
            }
            else if (tile.type === "grass") {
                color = '#4c6b2c'
            }
            else if (tile.type === "ice") {
                color = '#9ef7ed'
            }
            else if (tile.type === "sand") {
                color = '#ebcd7c'
            }
            else if (tile.type === "wall") {
                color = '#1f1f1f';
            }
            else if (tile.type === "hotsand") {
                color = '#6e2716'
            }
            else if (tile.type === "snow") {
                color = '#ffffff'
            }
            placeTile(j, i, color);
        }
    }
}


function drawPlayers(location) {
    placePlayer(location[0], location[1]);

}


function placeTile(x, y, color) {
    context.fillStyle = color;
    context.fillRect(x * tileSize, y * tileSize, tileSize, tileSize);
    context.strokeStyle = 'black';
    context.strokeRect(x * tileSize, y * tileSize, tileSize, tileSize);
}

function placePlayer(x, y) {
    const scaledX = x * tileSize;
    const scaledY = y * tileSize;
    context.fillStyle = "#0000ff";
    context.beginPath();
    context.arc(scaledX, scaledY, tileSize / 4.0, 0, 2 * Math.PI);
    context.fill();
    context.strokeStyle = 'black';
    context.stroke();
}

socket.on('printline', function (event) {
    const newLine = JSON.parse (event);
    addToPrintLine(newLine)
});

function addToPrintLine(inputString) {

    const line2 = document.getElementById("println2").innerHTML;
    const line1 = document.getElementById("println").innerHTML;

    document.getElementById("println3").innerHTML = line2;
    document.getElementById("println2").innerHTML = line1;
    document.getElementById("println").innerHTML = inputString;

}


function drawHealth(health) {
    document.getElementById("health").innerHTML = "HP: " + health
}

function drawArmor(armor) {
    document.getElementById("armor").innerHTML = "AC: " + armor
}

function drawHealthPotions(potion) {
    document.getElementById("potionsHeld").innerHTML = "Potions: " + potion
}

function drawAttack(attack) {
    document.getElementById("attack").innerHTML = "Attack: " + attack
}

function drawEnemyHealth(health) {
    document.getElementById("enemyHealth").innerHTML = "Enemy Health: " + health
}

function drawEnemyAttack(attack) {
    document.getElementById("enemyAttack").innerHTML = "Enemy Attack: " + attack
}

function drawEnemyArmor(armor) {
    document.getElementById("enemyArmor").innerHTML = "Enemy Armor: " + armor
}



