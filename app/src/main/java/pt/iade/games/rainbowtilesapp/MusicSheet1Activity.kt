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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import pt.iade.games.rainbowtilesapp.ui.theme.RainbowTilesAppTheme

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
        val text = context.assets.open(fileName)
            .bufferedReader()
            .use { it.readText() }

        val lines = text
            .lines()
            .filter { it.isNotBlank() }

        for (line in lines) {
            if (line.length != numberOfButtons) continue

            val index = line.indexOf('O')
            if (index == -1) {
                continue
            } else {
                val columnNumber = index + 1
                pattern.add(columnNumber)
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }

    return pattern
}

class MainActivity : ComponentActivity() {

    companion object {
        const val EXTRA_SHEET_NAME = "EXTRA_SHEET_NAME"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sheetName = intent.getStringExtra(EXTRA_SHEET_NAME)

        val sheet = when (sheetName) {
            "SHEET2" -> MusicSheet.SHEET2
            else -> MusicSheet.SHEET1
        }

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

    var rowsBeaten by remember { mutableIntStateOf(0) }

    var timeLeft by remember { mutableIntStateOf(startingTime) }
    var isTimerRunning by remember { mutableIntStateOf(0) }

    LaunchedEffect(isTimerRunning, timeLeft) {
        if (isTimerRunning == 1 && timeLeft > 0) {
            delay(1000L)
            timeLeft -= 1

            if (timeLeft == 0) {
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
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(innerPadding)
                    .offset(
                        x = 0.dp,
                        y = -(
                                (((numberOfRows + 1) / 2f) - rowsBeaten) *
                                        (rowHeight + (padding * 2.125f)) -
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
                    for (i in numberOfRows downTo 1) {
                        val rowIndex = i - 1

                        val highlightedKeyNumber = if (rowIndex < pattern.size) {
                            pattern[rowIndex]
                        } else {
                            1
                        }

                        val isCyanRow = i % 8 == 0

                        if (i == rowsBeaten + 1) {
                            TilesRow(
                                firstRow = true,
                                highlightedKeyNumber = highlightedKeyNumber,
                                isCyan = isCyanRow,
                                onCorrectClick = {
                                    if (isTimerRunning == 0 && rowsBeaten == 0) {
                                        isTimerRunning = 1
                                        timeLeft = startingTime
                                    }

                                    if (isCyanRow) {
                                        timeLeft += 2
                                    }

                                    rowsBeaten++
                                },
                                onWrongClick = {
                                    rowsBeaten = 0
                                    isTimerRunning = 0
                                    timeLeft = startingTime
                                }
                            )
                        } else {
                            TilesRow(
                                firstRow = false,
                                highlightedKeyNumber = highlightedKeyNumber,
                                isCyan = isCyanRow,
                                onCorrectClick = { /* not used */ },
                                onWrongClick = {
                                    rowsBeaten = 0
                                    isTimerRunning = 0
                                    timeLeft = startingTime
                                }
                            )
                        }
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(rowHeight.dp)
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
        modifier = Modifier.fillMaxWidth()
    ) {
        for (i in 1..numberOfButtons) {
            val isCorrectTile = (i == highlightedKeyNumber)

            if (isCorrectTile) {
                // Color for correct tile
                val buttonColor: Color = if (isCyan) {
                    Color(0xFF00FFFF) // bright cyan
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
                    if (isCyan) {
                        Text("+2", fontSize = 20.sp, color = Color.Black)
                    }
                }
            } else {
                Button(
                    onClick = { onWrongClick() },
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
                    colors = ButtonDefaults.buttonColors(Color.White),
                ) {
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GamePreview() {
    RainbowTilesAppTheme {
        val previewPattern = List(numberOfRows) { index ->
            (index % numberOfButtons) + 1
        }
        MainView(pattern = previewPattern)
    }
}
