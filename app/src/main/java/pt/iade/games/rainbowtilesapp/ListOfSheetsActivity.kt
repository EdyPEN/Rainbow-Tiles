package pt.iade.games.rainbowtilesapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.iade.games.rainbowtilesapp.ui.theme.RainbowTilesAppTheme

class ListOfSheetsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RainbowTilesAppTheme {
                ListOfSheetsScreen()
            }
        }
    }
}

@Composable
fun ListOfSheetsScreen() {
    // Context for starting activities
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Button for Music Sheet 1
            Button(
                onClick = {
                    // Start game with SHEET1
                    val intent = Intent(context, MainActivity::class.java)
                    intent.putExtra(MainActivity.EXTRA_SHEET_NAME, "SHEET1")
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(text = "Music Sheet 1")
            }

            // Button for Music Sheet 2
            Button(
                onClick = {
                    // Start game with SHEET2
                    val intent = Intent(context, MainActivity::class.java)
                    intent.putExtra(MainActivity.EXTRA_SHEET_NAME, "SHEET2")
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(text = "Music Sheet 2")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ListOfSheetsPreview() {
    RainbowTilesAppTheme {
        ListOfSheetsScreen()
    }
}
