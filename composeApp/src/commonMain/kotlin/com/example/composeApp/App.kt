package com.example.composeApp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.Typography
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontFamily
import demo1.composeapp.generated.resources.IBMPlexSansKR_Regular
import demo1.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    var rows by remember { mutableStateOf(10) }
    var cols by remember { mutableStateOf(10) }
    var minesCount by remember { mutableStateOf(20) }
    var game = MineSweeper(rows, cols)
    val font = Font(Res.font.IBMPlexSansKR_Regular)


    // 지뢰판 UI 생성
    MaterialTheme(
        typography = Typography(FontFamily(font))
    ) {
        Column {
            Text("MineSweeper")
            Button(onClick = { game.generateMines(minesCount) }) {
                Text("게임 시작")
            }

            // 각 셀의 버튼을 만들고 상태 관리
            for (row in 0 until rows) {
                Row {
                    for (col in 0 until cols) {
                        val cell = game.uncoverCell(row, col)
                        Button(onClick = {
                            // 셀 클릭 시 처리
                        }) {
                            Text("셀[$row, $col]")
                        }
                    }
                }
            }
        }
    }
}



// GameLogic.kt
class MineSweeper(val rows: Int, val cols: Int) {
    private val board = Array(rows) { Array(cols) { Cell() } }

    fun generateMines(minesCount: Int) {
        // 랜덤으로 지뢰 배치
    }

    fun uncoverCell(row: Int, col: Int): Cell {
        // 셀을 여는 로직
        return board[row][col]
    }

    data class Cell(var isMine: Boolean = false, var isRevealed: Boolean = false, var adjacentMines: Int = 0)
}
