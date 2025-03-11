package com.example.composeApp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun gameMobile(screenWidth : Dp, screenHeight : Dp){
    var board by remember { mutableStateOf(generateBoard()) }
    var revealed by remember { mutableStateOf(List(BOARD_SIZE) { MutableList(BOARD_SIZE) { false } }) }
    var flagged by remember { mutableStateOf(List(BOARD_SIZE) { MutableList(BOARD_SIZE) { false } }) }
    var gameOver by remember { mutableStateOf(false) }
    var flagMode by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var tempBoardSize by remember { mutableStateOf(BOARD_SIZE) }
    var tempMineCount by remember { mutableStateOf(MINE_COUNT) }

    fun resetGame() {
        board = generateBoard()
        revealed = List(BOARD_SIZE) { MutableList(BOARD_SIZE) { false } }
        flagged = List(BOARD_SIZE) { MutableList(BOARD_SIZE) { false } }
        gameOver = false
        flagMode = false
    }

    fun reveal(x: Int, y: Int) {
        if (gameOver || revealed[x][y] || flagged[x][y]) return
        revealed = revealed.mapIndexed { i, row ->
            row.mapIndexed { j, cell ->
                if (i == x && j == y) true else cell
            }.toMutableList()
        }

        if (board[x][y] == -1) {
            gameOver = true
        } else if (board[x][y] == 0) {
            for (dx in -1..1) {
                for (dy in -1..1) {
                    val nx = x + dx
                    val ny = y + dy
                    if (nx in 0 until BOARD_SIZE && ny in 0 until BOARD_SIZE && !revealed[nx][ny]) {
                        reveal(nx, ny)
                    }
                }
            }
        }
    }

    fun toggleFlag(x: Int, y: Int) {
        if (revealed[x][y]) return
        flagged = flagged.mapIndexed { i, row ->
            row.mapIndexed { j, cell ->
                if (i == x && j == y) !cell else cell
            }.toMutableList()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)

            ,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row {
            Button(onClick = { flagMode = !flagMode }) {
                Text(if (flagMode) "Flag Mode" else "Reveal Mode")
            }
            Spacer(Modifier.width(8.dp))
            Button(onClick = { resetGame() }) {
                Text("Restart")
            }
            Spacer(Modifier.width(8.dp))
            Button(onClick = { showSettings = true }) {
                Text("Settings")
            }
        }
        Spacer(Modifier.height(5.dp))
        Text("Board Size: ${BOARD_SIZE} x ${BOARD_SIZE}, Mines: $MINE_COUNT")
        Spacer(Modifier.height(16.dp))

        val cellSize = minOf((screenWidth / BOARD_SIZE), (screenHeight / BOARD_SIZE))

        Column(
            modifier = Modifier.size(cellSize * BOARD_SIZE)
        ) {
            for (x in 0 until BOARD_SIZE) {
                Row {
                    for (y in 0 until BOARD_SIZE) {
                        Box(
                            modifier = Modifier
                                .size(cellSize)
                                .weight(1f, false)
                                .background(
                                    when {
                                        gameOver && board[x][y] == -1 -> Color.Red
                                        flagged[x][y] -> Color.Yellow
                                        revealed[x][y] -> Color.LightGray
                                        else -> Color.DarkGray
                                    },
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .clickable {
                                    if (flagMode) toggleFlag(x, y) else reveal(x, y)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (flagged[x][y]) {
                                Text("🚩", color = Color.Black)
                            } else if (revealed[x][y]) {
                                Text(
                                    text = when (board[x][y]) {
                                        -1 -> "💣"
                                        0 -> ""
                                        else -> board[x][y].toString()
                                    },
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }

        if (gameOver) {
            Spacer(Modifier.height(16.dp))
            Text("Game Over! Press Restart to play again.", color = Color.Red)
        }
    }


    if (showSettings) {
        AlertDialog(
            onDismissRequest = { showSettings = false },
            confirmButton = {
                Button(onClick = {
                    MINE_COUNT = tempMineCount.coerceAtMost(tempBoardSize * tempBoardSize - 1)
                    BOARD_SIZE = tempBoardSize
                    resetGame()
                    showSettings = false
                }) {
                    Text("Apply")
                }
            },
            dismissButton = {
                Button(onClick = { showSettings = false }) {
                    Text("Cancel")
                }
            },
            title = { Text("Game Settings") },
            text = {
                Column {
                    Text("Board Size (N*N): $tempBoardSize")
                    Slider(
                        value = tempBoardSize.toFloat(),
                        onValueChange = { tempBoardSize = it.toInt() },
                        valueRange = 5f..20f,
                        steps = 15
                    )
                    Text("Mines Count: ${tempMineCount.coerceAtMost(tempBoardSize * tempBoardSize - 1)}")
                    Slider(
                        value = tempMineCount.toFloat(),
                        onValueChange = { tempMineCount = it.toInt().coerceAtMost(tempBoardSize * tempBoardSize - 1) },
                        valueRange = 1f..(tempBoardSize * tempBoardSize - 1).toFloat()
                    )
                }
            }
        )
    }
}