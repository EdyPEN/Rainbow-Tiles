package pt.iade.games.rainbowtilesapp

import android.content.Context
import java.io.IOException
import android.os.Bundle
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.iade.games.rainbowtilesapp.ui.theme.RainbowTilesAppTheme

var numberOfRows: Int = 45
var numberOfButtons: Int = 4
var rowHeight: Float = 180f
var padding: Float = 2f
var rowsBeaten: Int = 0
val blue: Int = 1
val green: Int = 2
val yellow: Int = 3
val red: Int = 4

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load pattern from sheet1.txt in assets
        val pattern = loadPatternFromAssets(
            context = this,
            fileName = "sheet1.txt",
            numberOfButtons = numberOfButtons // 4
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

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(
                    x = 0.dp,
                    y = -((((numberOfRows) / 2f) - rowsBeaten) * (rowHeight + (padding * 2.125f))- displayHeight / 2).dp
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(unbounded = true),
                verticalArrangement = Arrangement.Bottom
            ) {
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
