package space.itoncek.trailcompass.hideandseek

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.delay
import org.apache.commons.lang3.time.DurationFormatUtils
import space.itoncek.trailcompass.api.HideAndSeekApiFactory
import space.itoncek.trailcompass.client.api.HideAndSeekAPI
import space.itoncek.trailcompass.hideandseek.hiders.InGameHiderUI
import space.itoncek.trailcompass.hideandseek.seekers.InGameSeekerUI
import space.itoncek.trailcompass.runOnUiThread
import space.itoncek.trailcompass.ui.theme.ComposeTestTheme
import java.time.Duration
import java.time.ZonedDateTime
import kotlin.concurrent.thread
import kotlin.time.Duration.Companion.milliseconds

class AwaitStartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContent {
            ComposeTestTheme {
                AwaitStart()
            }
        }
    }
}

@Composable
fun AwaitStart() {
    val ctx = LocalContext.current;
    var startTime: ZonedDateTime? = null;
    var time by remember {
        mutableStateOf(
            "Loading..."
        )
    }

    LaunchedEffect(Unit) {
        while (true) {
            if (startTime == null) time = "Loading...";
            else {
                val d = Duration.between(ZonedDateTime.now(), startTime);
                time = format(d)
                if (d.toMillis() < 1000) {
                    thread {
                        val api: HideAndSeekAPI = HideAndSeekApiFactory(ctx.filesDir).generateApi()
                        val isHider = api.raw.gameMgr().currentHider == api.raw.auth().profile.id;
                        runOnUiThread {
                            if (isHider) {
                                ctx.startActivity(Intent(ctx, InGameHiderUI::class.java))
                            } else {
                                ctx.startActivity(Intent(ctx, InGameSeekerUI::class.java))
                            }

                            val a = ctx as? Activity
                            a?.finish()
                        }
                    }
                    break
                } else {
                    delay(500.milliseconds)
                    continue
                }
            }
            delay(500.milliseconds)
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize(1f)
            .background(MaterialTheme.colorScheme.background)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
            elevation = CardDefaults.elevatedCardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Awaiting start",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 48.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "The game is set up and will start in $time",
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        val lifecycleOwner = LocalLifecycleOwner.current
        val api: HideAndSeekAPI = HideAndSeekApiFactory(ctx.filesDir).generateApi()

        DisposableEffect(ctx) {
            val observer = LifecycleEventObserver { source, event ->
                if (event == Lifecycle.Event.ON_START) {
                    thread {
                        startTime = api.gameStartTime;
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

fun format(d: Duration?): String {
    if (d == null) return "Loading..."
    if (d.toSeconds() > 172800 /* two days */) return "more than 48 hours."
    if (d.toMillis() < 1111) return "Switching"
    return DurationFormatUtils.formatDuration(d.toMillis(), "H:mm:ss", true);
}

@Preview(
    name = "Dynamic Red Dark",
    group = "dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    wallpaper = Wallpapers.RED_DOMINATED_EXAMPLE
)
@Preview(
    name = "Dynamic Green Dark",
    group = "dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    wallpaper = Wallpapers.GREEN_DOMINATED_EXAMPLE
)
@Preview(
    name = "Dynamic Yellow Dark",
    group = "dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    wallpaper = Wallpapers.YELLOW_DOMINATED_EXAMPLE
)
@Preview(
    name = "Dynamic Blue Dark",
    group = "dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    wallpaper = Wallpapers.BLUE_DOMINATED_EXAMPLE
)
@Preview(
    name = "Dynamic Red Light",
    group = "light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
    wallpaper = Wallpapers.RED_DOMINATED_EXAMPLE
)
@Preview(
    name = "Dynamic Green Light",
    group = "light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
    wallpaper = Wallpapers.GREEN_DOMINATED_EXAMPLE
)
@Preview(
    name = "Dynamic Yellow Light",
    group = "light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
    wallpaper = Wallpapers.YELLOW_DOMINATED_EXAMPLE
)
@Preview(
    name = "Dynamic Blue Light",
    group = "light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
    wallpaper = Wallpapers.BLUE_DOMINATED_EXAMPLE
)
@Preview(showBackground = true, apiLevel = 33)
@Composable
fun GreetingPreview4() {
    ComposeTestTheme {
        AwaitStart()
    }
}