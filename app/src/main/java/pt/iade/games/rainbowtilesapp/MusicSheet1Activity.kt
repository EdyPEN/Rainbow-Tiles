package pt.iade.games.rainbowtilesapp

import android.content.Context
import java.io.IOException
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import pt.iade.games.rainbowtilesapp.ui.theme.RainbowTilesAppTheme
import kotlin.random.Random

var numberOfRows: Int = 45
var numberOfButtons: Int = 4
var rowHeight: Float = 180f
var padding: Float = 2f
var buttonWidth: Float = 100f
const val startingTime = 6
const val blue: Int = 1
const val green: Int = 2
const val yellow: Int = 3
const val red: Int = 4
const val cyan: Int = 5

enum class MusicSheet(val fileName: String) {
    SHEET1("sheet1.txt"),
    SHEET2("sheet2.txt")
}
fun loadPatternFromAssets(
    context: Context,
    fileName: String,
    numberOfButtons: Int
): List<Int> {
    val pattern = mutableListOf<Int>()

    try {
        // Read entire file as text
        val text = context.assets.open(fileName)
            .bufferedReader()
            .use { it.readText() }

        // Split by lines, remove empty lines
        val lines = text
            .lines()
            .filter { it.isNotBlank() }

        for (line in lines) {
            // Safety: line must have exactly numberOfButtons characters
            if (line.length != numberOfButtons) {
                // Skip incorrect lines
                continue
            }

            // Find index of 'O' (active tile) in this line
            val index = line.indexOf('O')

            if (index == -1) {
                // No 'O' in this line -> skip or handle error
                continue
            } else {
                // index is 0-based, we need 1..numberOfButtons
                val columnNumber = index + 1
                pattern.add(columnNumber)
            }
        }
    } catch (e: IOException) {
        // File not found or read error
        e.printStackTrace()
    }

    return pattern
}
class MainActivity : ComponentActivity() {
    // A single place where we put the constant
    companion object {
        const val EXTRA_SHEET_NAME = "EXTRA_SHEET_NAME"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Read sheet name from Intent
        val sheetName = intent.getStringExtra(EXTRA_SHEET_NAME)

        // Convert String to enum (default is SHEET1)
        val sheet = when (sheetName) {
            "SHEET2" -> MusicSheet.SHEET2
            // "SHEET3" -> MusicSheet.SHEET3
            else -> MusicSheet.SHEET1
        }

        // Load pattern for this sheet
        val pattern = loadPatternFromAssets(
            context = this,
            fileName = sheet.fileName,
            numberOfButtons = numberOfButtons
        )

        enableEdgeToEdge()
        setContent {
            RainbowTilesAppTheme {
                MainView(pattern = pattern)
            }
        }
    }
}

fun getScreenHeight(context: Context): Float {
    val displayMetrics = context.resources.displayMetrics
    return displayMetrics.heightPixels / displayMetrics.density
}

@Composable
fun MainView(pattern: List<Int>) {
    val context = LocalContext.current
    val displayHeight = getScreenHeight(context)

    // How many rows the player has beaten
    var rowsBeaten by remember { mutableIntStateOf(0) }

    // Timer state
    var timeLeft by remember { mutableIntStateOf(startingTime) }
    var isTimerRunning by remember { mutableIntStateOf(0) } // 0 = stopped, 1 = running

    // Timer logic: counts down when running, resets game at 0
    LaunchedEffect(isTimerRunning, timeLeft) {
        if (isTimerRunning == 1 && timeLeft > 0) {
            delay(1000L) // 1 second
            timeLeft -= 1

            if (timeLeft == 0) {
                // Time is up → reset game
                rowsBeaten = 0
                isTimerRunning = 0
                timeLeft = startingTime
            }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .offset(
                    x = 0.dp,
                    y = -((((numberOfRows) / 2f) - rowsBeaten) * (rowHeight + (padding * 2.125f))- displayHeight / 2).dp
                )
                .padding(innerPadding)
        ) {
            // ---------- GAME AREA (TILES) ----------
            Box(
                modifier = Modifier
                    .weight(1f)               // take all free space above timer
                    .fillMaxWidth()
                    .offset(
                        x = 0.dp,
                        y = -(
                                (((numberOfRows + 1) / 2f) - rowsBeaten) * (rowHeight + (padding * 2.125f)) -
                                        displayHeight / 2
                                ).dp
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(unbounded = true),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    var highlightedKeyNumber: Int
                    var firstRow: Boolean

                    for (i in numberOfRows downTo 1) {
                        // Column of the correct tile: 1..4
                        var highlightedKeyNumber = i % 4 + 1

                        // Every 8th row is a cyan bonus row
                        val isCyanRow = i % 8 == 0

                        if (i == rowsBeaten + 1) {
                            // This is the active (bottom) row
                            firstRow = true

                            TilesRow(
                                firstRow = firstRow,
                                highlightedKeyNumber = highlightedKeyNumber,
                                isCyan = isCyanRow,
                                onCorrectClick = {
                                    // Start timer on very first correct tile
                                    if (isTimerRunning == 0 && rowsBeaten == 0) {
                                        isTimerRunning = 1
                                        timeLeft = startingTime
                                    }

                                    // Bonus time if this row is cyan
                                    if (isCyanRow) {
                                        timeLeft += 2
                                    }

                                    rowsBeaten++
                                },
                                onWrongClick = {
                                    // Wrong tile → reset game and timer
                                    rowsBeaten = 0
                                    isTimerRunning = 0
                                    timeLeft = startingTime
                                }
                            )
                        } else {
                            // Not the active row
                            firstRow = false

                            TilesRow(
                                firstRow = firstRow,
                                highlightedKeyNumber = highlightedKeyNumber,
                                isCyan = isCyanRow,
                                onCorrectClick = {
                                    // not used
                                },
                                onWrongClick = {
                                    // Any tap in non-active rows is a mistake
                                    rowsBeaten = 0
                                    isTimerRunning = 0
                                    timeLeft = startingTime
                                }
                            )
                        }
                for (i in numberOfRows downTo 1) {

                    // Row index in pattern list (0-based)
                    val rowIndex = i - 1

                    // Get column for this row from pattern.
                    // If pattern is shorter than numberOfRows, use 1 as default.
                    val highlightedKeyNumber = if (rowIndex < pattern.size) {
                        pattern[rowIndex]
                    } else {
                        1
                    }

                    if (i == rowsBeaten + 1) {
                        // Active row
                        TilesRow(
                            firstRow = true,
                            highlightedKeyNumber = highlightedKeyNumber,
                            onTileClick = { rowsBeaten++ } // go to next row
                        )
                    } else {
                        // Inactive row
                        TilesRow(
                            firstRow = false,
                            highlightedKeyNumber = highlightedKeyNumber,
                            onTileClick = { }
                        )
                    }
                }
            }

            // ---------- TIMER BAR AT BOTTOM ----------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(rowHeight.dp)          // same height as a tile row
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Time left: $timeLeft",
                    fontSize = 24.sp,
                    color = Color.Black
                )
            }
        }
    }
}
@Composable
fun TilesRow(
    firstRow: Boolean,
    highlightedKeyNumber: Int,
    isCyan: Boolean,
    onCorrectClick: () -> Unit,
    onWrongClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        for (i in 1..numberOfButtons) {

            val isCorrectTile = (i == highlightedKeyNumber)

            if (isCorrectTile) {
                // Decide color for the CORRECT tile
                val buttonColor: Color = if (isCyan) {
                    // SUPER BRIGHT cyan so it’s impossible to miss
                    Color(0xFF00FFFF)
                } else if (highlightedKeyNumber == blue) {
                    Color.Blue
                } else if (highlightedKeyNumber == green) {
                    Color.Green
                } else if (highlightedKeyNumber == yellow) {
                    Color.Yellow
                } else if (highlightedKeyNumber == red) {
                    Color.Red
                } else {
                    Color.Black
                }

                Button(
                    onClick = {
                        if (firstRow) {
                            onCorrectClick()
                        } else {
                            onWrongClick()
                        }
                    },
                    modifier = Modifier
                        .padding(padding.dp)
                        .width(buttonWidth.dp)
                        .height(rowHeight.dp),
                    shape = CutCornerShape(
                        topStart = 0f,
                        topEnd = 0f,
                        bottomEnd = 0f,
                        bottomStart = 0f
                    ),
                    colors = ButtonDefaults.buttonColors(buttonColor),
                ) {
                    // Optional: show debug text on cyan tiles
                    if (isCyan) {
                        Text("+2", fontSize = 20.sp, color = Color.Black)
                    }
                }
            } else {
                // White tiles are always "wrong"
                val buttonColor = Color.White

                Button(
                    onClick = {
                        onWrongClick()
                    },
                    modifier = Modifier
                        .padding(padding.dp)
                        .width(buttonWidth.dp)
                        .height(rowHeight.dp),
                    shape = CutCornerShape(
                        topStart = 0f,
                        topEnd = 0f,
                        bottomEnd = 0f,
                        bottomStart = 0f
                    ),
                    colors = ButtonDefaults.buttonColors(buttonColor),
                ) {
                    // empty tile
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RainbowTilesAppTheme {
        // Fake pattern for preview only
        val previewPattern = List(numberOfRows) { index ->
            (index % numberOfButtons) + 1
        }
        MainView(pattern = previewPattern)
    }
}
