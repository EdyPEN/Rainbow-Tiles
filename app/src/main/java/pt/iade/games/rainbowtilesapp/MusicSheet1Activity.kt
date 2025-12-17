package pt.iade.games.rainbowtilesapp

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.SoundPool
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import pt.iade.games.rainbowtilesapp.ui.theme.RainbowTilesAppTheme
import kotlin.time.Duration.Companion.seconds


var numberOfRows: Int = 45
var numberOfButtons: Int = 4
var rowHeight: Float = 180f
var padding: Float = 2f
var buttonWidth: Float = 100f
const val startingTime = 10
const val timeBonus = 3
const val blue: Int = 1
const val green: Int = 2
const val yellow: Int = 3
const val red: Int = 4

private
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
fun rememberNoteSounds(): (Int) -> Unit {
    val context = LocalContext.current

    var soundPool: SoundPool? = remember { null }
    var blueId by remember { mutableIntStateOf(0) }
    var greenId by remember { mutableIntStateOf(0) }
    var yellowId by remember { mutableIntStateOf(0) }
    var redId by remember { mutableIntStateOf(0) }

    DisposableEffect(Unit) {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        val pool = SoundPool.Builder()
            .setMaxStreams(6)
            .setAudioAttributes(audioAttributes)
            .build()

        soundPool = pool

        blueId = pool.load(context, R.raw.blue_note, 1)
        greenId = pool.load(context, R.raw.green_note, 1)
        yellowId = pool.load(context, R.raw.yellow_note, 1)
        redId = pool.load(context, R.raw.red_note, 1)

        onDispose {
            pool.release()
            soundPool = null
        }
    }

    return remember {
        { noteColumn: Int ->
            val pool = soundPool
            if (pool == null) {
                return@remember
            }

            var idToPlay = 0
            if (noteColumn == blue) {
                idToPlay = blueId
            } else if (noteColumn == green) {
                idToPlay = greenId
            } else if (noteColumn == yellow) {
                idToPlay = yellowId
            } else if (noteColumn == red) {
                idToPlay = redId
            }

            if (idToPlay != 0) {
                pool.play(idToPlay, 1f, 1f, 1, 0, 1f)
            }
        }
    }
}


@Composable
fun MainView(pattern: List<Int>) {
    val context = LocalContext.current
    val displayHeight = getScreenHeight(context)

    val playNote = rememberNoteSounds()

    var rowsBeaten by remember { mutableIntStateOf(0) }

    var timeLeft by remember { mutableIntStateOf(startingTime) }
    var isTimerRunning by remember { mutableIntStateOf(0) }
    var lost by remember { mutableIntStateOf(0) }

    LaunchedEffect(isTimerRunning, timeLeft) {
        if (isTimerRunning == 1 && timeLeft > 0) {
            delay(1.seconds)
            timeLeft -= 1

            if (timeLeft == 0) {
                lost = 1
            }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (rowsBeaten < numberOfRows && lost == 0) {

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(innerPadding)
                        .offset(
                            x = 0.dp,
                            y = -((((numberOfRows + 1) / 2f) - rowsBeaten) * (rowHeight + (padding * 2.125f)) - displayHeight / 2).dp
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

                            val timeBonusRow = i % 8 == 0

                            if (i == rowsBeaten + 1) {
                                TilesRow(
                                    firstRow = true,
                                    highlightedKeyNumber = highlightedKeyNumber,
                                    timeBonusRow = timeBonusRow,
                                    onCorrectClick = {
                                        if (isTimerRunning == 0 && rowsBeaten == 0) {
                                            isTimerRunning = 1
                                            timeLeft = startingTime
                                        }

                                        if (timeBonusRow) {
                                            timeLeft += timeBonus
                                        }

                                        playNote(highlightedKeyNumber)

                                        rowsBeaten++
                                    },
                                    onWrongClick = {
                                        lost = 1
                                    }
                                )
                            } else {
                                TilesRow(
                                    firstRow = false,
                                    highlightedKeyNumber = highlightedKeyNumber,
                                    timeBonusRow = timeBonusRow,
                                    onCorrectClick = { },
                                    onWrongClick = {
                                        lost = 1
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
            else {
                isTimerRunning = 0
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (lost == 1) {
                            Text(
                                text = "You Lose...",
                                fontSize = 32.sp,
                                color = Color.Black
                            )
                            if (timeLeft == 0) {
                                Text(
                                    text = "Don't let the time run out!",
                                    fontSize = 24.sp,
                                    color = Color.Black
                                )
                            }
                            else if (rowsBeaten == numberOfRows - 1) {
                                Text(
                                    text = "I'm so sorry...",
                                    fontSize = 24.sp,
                                    color = Color.Black
                                )
                            }
                            else if (rowsBeaten > 0) {
                                Text(
                                    text = "Try pressing the highlighted tiles!",
                                    fontSize = 24.sp,
                                    color = Color.Black
                                )
                            }
                            else {
                                Text(
                                    text = "Are you even trying?",
                                    fontSize = 24.sp,
                                    fontStyle = FontStyle.Italic,
                                    color = Color.Black
                                )
                            }
                        }
                        else {
                            Text(
                                text = "You Win!",
                                fontSize = 32.sp,
                                color = Color.Black,
                            )
                            Text(
                                text = "Time left: $timeLeft",
                                fontSize = 24.sp,
                                color = Color.Black,
                                modifier = Modifier
                                    .padding(8.dp)
                            )
                        }
                        Button(
                            onClick = {
                                rowsBeaten = 0
                                isTimerRunning = 0
                                timeLeft = startingTime
                                lost = 0
                            },
                            modifier = Modifier
                                .padding(8.dp)
                                .width(125.dp)
                                .height(50.dp),
                            shape = CutCornerShape(
                                topStart = 10f,
                                topEnd = 10f,
                                bottomEnd = 10f,
                                bottomStart = 10f
                            ),
                            colors = ButtonDefaults.buttonColors(Color.Black),
                        ) {
                            Text(
                                text = "Retry",
                                fontSize = 24.sp,
                                color = Color.White
                            )
                        }
                        Button(
                            onClick = {
                                rowsBeaten = 0
                                isTimerRunning = 0
                                timeLeft = startingTime
                                lost = 0
                                val intent = Intent(context, ListOfSheetsActivity::class.java)
                                context.startActivity(intent)
                                // Close current activity so Back doesn't return to game-over screen
                                (context as? ComponentActivity)?.finish()
                            },
                            modifier = Modifier
                                .padding(8.dp)
                                .width(125.dp)
                                .height(50.dp),
                            shape = CutCornerShape(
                                topStart = 10f,
                                topEnd = 10f,
                                bottomEnd = 10f,
                                bottomStart = 10f
                            ),
                            colors = ButtonDefaults.buttonColors(Color.Black),
                        ) {
                            Text(
                                text = "List",
                                fontSize = 24.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun TilesRow(
    firstRow: Boolean,
    highlightedKeyNumber: Int,
    timeBonusRow: Boolean,
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
                val buttonColor: Color = if (timeBonusRow) {
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
                    if (timeBonusRow) {
                        Text("+$timeBonus", fontSize = 20.sp, color = Color.Black)
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
