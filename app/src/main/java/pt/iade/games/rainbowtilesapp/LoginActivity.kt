package pt.iade.games.rainbowtilesapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import pt.iade.games.rainbowtilesapp.ui.theme.RainbowTilesAppTheme
import androidx.core.content.edit

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RainbowTilesAppTheme {
                LoginScreen(
                    onLoginSuccess = {
                        // Close login screen and return to menu
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Login Rainbow Strings", style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Username") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (name.isBlank() || password.isBlank()) {
                        message = "Enter username and password."
                        return@Button
                    }

                    isLoading = true
                    message = ""

                    scope.launch {
                        val result = loginToServer(
                            context = context,
                            baseUrl = SERVER_BASE_URL,
                            name = name,
                            password = password
                        )

                        isLoading = false

                        if (result.ok) {
                            saveLoggedInUser(context, result.userId, result.userName)
                            message = "Logged in as ${result.userName}"
                            onLoginSuccess()
                        } else {
                            message = result.errorMessage
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isLoading) "Logging in..." else "Login")
            }

            if (message.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = message)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // No registration button on purpose
            Text(text = "Registration is disabled in this app.")
        }
    }
}
// Render URL here
private const val SERVER_BASE_URL = "https://rainbowserver-1.onrender.com"
data class LoginResult(
    val ok: Boolean,
    val userId: Int = -1,
    val userName: String = "",
    val errorMessage: String = ""
)

suspend fun loginToServer(
    context: Context,
    baseUrl: String,
    name: String,
    password: String
): LoginResult {
    return withContext(Dispatchers.IO) {
        try {
            // server.js has POST /login
            val url = URL("$baseUrl/login")
            val conn = (url.openConnection() as HttpURLConnection)

            // English comments inside code as requested
            // Configure request
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8")
            conn.doOutput = true

            // Build JSON body
            val bodyJson = JSONObject()
            bodyJson.put("name", name)
            bodyJson.put("password", password)

            // Write request body
            conn.outputStream.use { os ->
                os.write(bodyJson.toString().toByteArray(Charsets.UTF_8))
            }

            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream

            val responseText = BufferedReader(InputStreamReader(stream)).use { it.readText() }

            // Parse JSON response
            val json = JSONObject(responseText)
            val ok = json.optBoolean("ok", false)

            if (ok) {
                val userId = json.optInt("userId", -1)
                val userName = json.optString("name", "")
                LoginResult(ok = true, userId = userId, userName = userName)
            } else {
                val msg = json.optString("message", "Login failed.")
                LoginResult(ok = false, errorMessage = msg)
            }
        } catch (e: Exception) {
            LoginResult(ok = false, errorMessage = "Network error: ${e.message}")
        }
    }
}

fun saveLoggedInUser(context: Context, userId: Int, userName: String) {
    val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    prefs.edit {
        putInt("user_id", userId)
            .putString("user_name", userName)
    }
}
