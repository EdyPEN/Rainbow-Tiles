package pt.iade.games.rainbowtilesapp

import android.R
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import pt.iade.games.rainbowtilesapp.ui.theme.RainbowTilesAppTheme
import kotlin.random.Random
var numberOfRows: Int = 20
var numberOfButtons: Int = 4
var rowHeight: Float = 171f
var padding: Float = 0f
var buttonWidth: Float = 100f
const val startingTime = 6
const val blue: Int = 1
const val green: Int = 2
const val yellow: Int = 3
const val red: Int = 4
const val cyan: Int = 5
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RainbowTilesAppTheme {
                MainView()
            }
        }
    }
}

fun getScreenHeight(context: Context): Float {
    val displayMetrics = context.resources.displayMetrics
    return displayMetrics.heightPixels / displayMetrics.density
}

@Composable
fun MainView() {
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


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RainbowTilesAppTheme {
        MainView()
    }
}