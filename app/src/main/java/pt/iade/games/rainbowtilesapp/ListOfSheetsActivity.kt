package pt.iade.games.rainbowtilesapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import pt.iade.games.rainbowtilesapp.ui.theme.RainbowTilesAppTheme

class ListOfSheetsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RainbowTilesAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Button(
                        onClick = {
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        }
                    ) {
                        Text("Sheet1")
                    }
//                    Button(
//                        onClick = {
//                            val intent = Intent(this, MainActivity::class.java)
//                            startActivity(intent)
//                        }
//                    ) {
//                        Text("Sheet2")
//                    }
                }
            }
        }
    }
}

@Composable
fun Greeting3(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview3() {
    RainbowTilesAppTheme {
        Greeting3("Android")
    }
}