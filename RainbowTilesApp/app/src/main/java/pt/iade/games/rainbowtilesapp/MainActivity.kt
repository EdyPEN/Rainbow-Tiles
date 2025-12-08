package pt.iade.games.rainbowtilesapp

import android.R
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import pt.iade.games.rainbowtilesapp.ui.theme.RainbowTilesAppTheme
import pt.iade.games.rainbowtilesapp.ui.theme.RainbowTilesAppTheme
import kotlin.random.Random
var numberOfRows: Int = 20
var numberOfButtons: Int = 4
var rowHeight: Float = 180f
var padding: Float = 2f
var rowsBeaten: Int = 0
val blue: Int = 1
val green: Int = 2
val yellow: Int = 3
val red: Int = 4
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

    var rowsBeaten by remember { mutableIntStateOf(0) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(
                    x = 0.dp,
                    y = -((((numberOfRows) / 2f) - rowsBeaten) * (rowHeight + (padding * 2.125f)) - displayHeight / 2).dp
                )

        ) {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(unbounded = true),
                verticalArrangement = Arrangement.Bottom
            ) {
                var highlightedKeyNumber: Int = 1
                var firstRow: Boolean = false

                for (i in numberOfRows downTo 1 step 1)
                {
                    highlightedKeyNumber = i%4 + 1
                    if (i == rowsBeaten + 1)
                    {
                        firstRow = true
                        TilesRow(firstRow, highlightedKeyNumber = highlightedKeyNumber, onTileClick = {rowsBeaten++})
                    }
                    else
                    {
                        firstRow = false
                        TilesRow(firstRow, highlightedKeyNumber = highlightedKeyNumber, onTileClick = { })
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
    onTileClick: () -> Unit
) {
    Row (
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        var buttonColor: Color
        for (i in 1..numberOfButtons) {

            if (i == highlightedKeyNumber)
            {
                if (highlightedKeyNumber == blue)
                {
                    buttonColor = Color.Blue.copy(alpha = 1f)
                }
                else if (highlightedKeyNumber == green)
                {
                    buttonColor = Color.Green.copy(alpha = 1f)
                }
                else if (highlightedKeyNumber == yellow)
                {
                    buttonColor = Color.Yellow.copy(alpha = 1f)
                }
                else if (highlightedKeyNumber == red)
                {
                    buttonColor = Color.Red.copy(alpha = 1f)
                }
                else
                {
                    buttonColor = Color.Black.copy(alpha = 1f)
                }

                Button(
                    onClick = onTileClick,
                    modifier = Modifier
                        .padding(padding.dp)
                        .width(95.dp)
                        .height(rowHeight.dp),
                    shape = CutCornerShape(
                        topStart = 0f,
                        topEnd = 0f,
                        bottomEnd = 0f,
                        bottomStart = 0f
                    ),
                    colors = ButtonDefaults.buttonColors(buttonColor),
                ) {
                    if (firstRow)
                    {
                        Text(
                            text = "$i",
                            fontSize = 64.sp
                        )
                    }
                    else
                    {
                        Text(
                            "$i",
                            fontSize = 16.sp
                        )
                    }
                }
            }
            else
            {
                buttonColor = Color.White.copy(alpha = 1f)
                Button(
                    onClick = { },
                    modifier = Modifier
                        .padding(padding.dp)
                        .width(95.dp)
                        .height(rowHeight.dp),
                    shape = CutCornerShape(
                        topStart = 0f,
                        topEnd = 0f,
                        bottomEnd = 0f,
                        bottomStart = 0f
                    ),
                    colors = ButtonDefaults.buttonColors(buttonColor),
                ) {
                    if (firstRow)
                    {
                        Text(
                            text = "$i",
                            fontSize = 64.sp
                        )
                    }
                    else
                    {
                        Text(
                            "$i",
                            fontSize = 16.sp
                        )
                    }
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