package space.itoncek.trailcompass.hideandseek.hiders

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.composables.Layers
import com.composables.Map
import com.composables.Quiz
import com.composables.Sync
import kotlinx.coroutines.delay
import space.itoncek.trailcompass.R
import space.itoncek.trailcompass.api.HideAndSeekApiFactory
import space.itoncek.trailcompass.client.api.HideAndSeekAPI
import space.itoncek.trailcompass.hideandseek.seekers.SettingsIngameActivityMain
import space.itoncek.trailcompass.ui.theme.ComposeTestTheme
import java.time.Duration
import java.time.ZonedDateTime
import kotlin.concurrent.thread

class InGameHiderUI : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContent {
            ComposeTestTheme {
                InGameSeekerActivity()
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun InGameSeekerActivity() {
    var screen by remember { mutableStateOf("cards") }
    val ctx = LocalContext.current
    val api: HideAndSeekAPI = HideAndSeekApiFactory(ctx.filesDir).generateApi()
    var username by remember { mutableStateOf("Loading...") }
    var startTime by remember { mutableStateOf(ZonedDateTime.now()) }
    var endTime by remember { mutableStateOf(ZonedDateTime.now()) }
    var displayTime by remember { mutableStateOf("00:00:00") }
    var loaded by remember { mutableStateOf(false) }
    val navigate = { target: String ->
        when (target) {
            "map", "requests", "cards", "settings" -> {
                screen = target
            }
        }
    }

    Scaffold { padding ->
        when (screen) {
            "map" -> HiderMapActivity(api);
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(.9f)
                    .wrapContentHeight()
                    .padding(top = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                elevation = CardDefaults.elevatedCardElevation(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(start = 20.dp, end = 8.dp, top = 10.dp, bottom = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Box {
                        Text(username)
                    }
                    Box {
                        // Text Clock
                        Text(displayTime, fontSize = 36.sp, fontWeight = FontWeight.Black)
                    }
                    Box {
                        IconButton(onClick = {
                            // TODO))
                        }) {
                            Icon(Sync, "Sync");
                        }
                    }
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                when (screen) {
                    "requests" -> HiderRequestsActivity(api)
                    "cards" -> HiderDeckActivity(null)
                    "settings" -> SettingsIngameActivityMain(api)
                }
            }

            LaunchedEffect(key1 = Unit, block = {
                while (true) {
                    delay(250)
                    val now = ZonedDateTime.now()
                    if (!loaded) {
                        displayTime = "Loading..."
                    } else if (now.isAfter(endTime)) {
                        displayTime = "Rest period!"
                    } else if (now.isBefore(startTime)) {
                        displayTime = "Rest period!"
                    } else {
                        val totalTimeToday = Duration.between(startTime, now)
                        displayTime = String.format("%02d:%02d:%02d",totalTimeToday.toHours(), totalTimeToday.toMinutesPart(), totalTimeToday.toSecondsPart());
                    }
                }
            })

            Card(
                modifier = Modifier
                    .fillMaxWidth(.9f)
                    .wrapContentHeight()
                    .padding(bottom = 32.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                elevation = CardDefaults.elevatedCardElevation(8.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(top = 10.dp, start = 10.dp, end = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround,
                    ) {
                        IconButton(onClick = {
                            navigate("map")
                        }, enabled = screen != "map") {
                            Icon(Map, "Map", Modifier.size(48.dp));
                        }
                        IconButton(onClick = {
                            navigate("requests")
                        }, enabled = screen != "requests") {
                            Icon(Quiz, "Requests", Modifier.size(48.dp));
                        }
                        IconButton(onClick = {
                            navigate("cards")
                        }, enabled = screen != "cards") {
                            Icon(Layers, "Cards", Modifier.size(48.dp));
                        }

                        IconButton(onClick = {
                            navigate("settings")
                        }, enabled = screen != "settings") {
                            Icon(Icons.Rounded.Build, "Settings", Modifier.size(48.dp));
                        }
                    }

                    Row(
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(end = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painterResource(R.drawable.madebyitoncek),
                            "icon",
                            Modifier.size(12.dp)
                        )
                        Text(
                            "#madebyitoncek",
                            fontSize = 10.sp,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
        }

        val lifecycleOwner = LocalLifecycleOwner.current

        DisposableEffect(loaded) {
            val observer = LifecycleEventObserver { source, event ->
                if (event == Lifecycle.Event.ON_START) {
                    thread {
                        username = api.raw.auth().profile.nickname
                        startTime = api.raw.gameMgr().startingTime;
                        endTime = api.raw.gameMgr().endingTime;
                        loaded = true;
                    }
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview9() {
    ComposeTestTheme {
        InGameSeekerActivity()
    }
}