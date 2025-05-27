package space.itoncek.trailcompass.hideandseek.seekers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import space.itoncek.trailcompass.ui.theme.ComposeTestTheme

class InGameSeekerUI : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeTestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting2(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting2(name: String, modifier: Modifier = Modifier) {
    var screen by remember { mutableStateOf("map")}
    val navigate = {target: String ->
        when (target) {
            "map", "requests", "curses", "settings" -> {
                screen = target
            }
        }
    }

    when (screen) {
        "map" -> MapActivityMain(navigate, screen)
        "requests" -> RequestsActivityMain(navigate)
        "curses" -> CursesActivityMain(navigate)
        "settings" -> SettingsIngameActivityMain(navigate)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview9() {
    ComposeTestTheme {
        Greeting2("Android")
    }
}