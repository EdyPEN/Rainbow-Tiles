package pt.iade.games.rainbowtilesapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
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

class MenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RainbowTilesAppTheme {
                val context = LocalContext.current
                val prefs = context.getSharedPreferences("auth", android.content.Context.MODE_PRIVATE)
                val userName = prefs.getString("user_name", "") ?: ""
                val userId = prefs.getInt("user_id", -1)
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        // Push content down a bit so it's closer to center
//                        Spacer(modifier = Modifier.weight(0.8f))

                        if (userName.isNotBlank() && userId != -1) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "Welcome, $userName!")
                                Text(text = "your ID: $userId")
                            }

//                            Spacer(modifier = Modifier.padding(12.dp))
                        }

                        Button(
                            onClick = {
                                val intent = Intent(context, ListOfSheetsActivity::class.java)
                                context.startActivity(intent)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp)
                        ) {
                            Text("List of Music Sheets")
                        }

                        Button(
                            onClick = {
                                val intent = Intent(context, LoginActivity::class.java)
                                context.startActivity(intent)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Text("Login Rainbow Strings")
                        }


                    }
                }
            }
        }
    }
}

@Composable
fun Greeting2(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    RainbowTilesAppTheme {
        Greeting2("Android")
    }
}