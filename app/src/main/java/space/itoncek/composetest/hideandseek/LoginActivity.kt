package space.itoncek.composetest.hideandseek

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.apache.commons.codec.digest.DigestUtils
import space.itoncek.composetest.api.HideAndSeekApiFactory
import space.itoncek.composetest.runOnUiThread
import space.itoncek.composetest.ui.theme.ComposeTestTheme
import space.itoncek.composetest.ui.theme.DesignBg
import space.itoncek.composetest.ui.theme.DesignBlueWhite
import space.itoncek.composetest.ui.theme.DesignFg
import space.itoncek.composetest.ui.theme.DesignShadow
import space.itoncek.trailcompass.client.api.HideAndSeekAPI
import space.itoncek.trailcompass.client.api.LoginResponse
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContent {
            ComposeTestTheme {
                LoginView()
            }
        }
    }
}


@Composable
fun LoginView() {
    var progress by remember { mutableFloatStateOf(.10f) }
    var label by remember { mutableStateOf("Loading...") }
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
                .fillMaxWidth(.9f)
                .wrapContentHeight()
                .padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
            elevation = CardDefaults.elevatedCardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .wrapContentHeight()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    progress = { progress },
                    strokeCap = StrokeCap.Round,
                    modifier = Modifier.padding(8.dp, 4.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    trackColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(text = label, modifier = Modifier.padding(8.dp, 4.dp), color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    val ctx = LocalContext.current;
    val api: HideAndSeekAPI = HideAndSeekApiFactory(ctx.filesDir).generateApi()

    DisposableEffect(LocalContext.current) {
        val observer = LifecycleEventObserver { source, event ->
            if (event == Lifecycle.Event.ON_START) {
                thread {
                    val cfg = api.config
                    runOnUiThread { label = "Logging in as " + cfg.username }

                    val login = api.login()

                    val activity = ctx as? Activity
                    when (login) {
                        LoginResponse.UNABLE_TO_CONNECT, null -> {
                            runOnUiThread {
                                label = "Unable to connect!"
                                Toast.makeText(ctx, "Unable to connect!", Toast.LENGTH_SHORT)
                                    .show();
                                activity?.finish()
                                return@runOnUiThread
                            }
                        }

                        LoginResponse.INCOMPATIBLE_BACKEND -> {
                            runOnUiThread {
                                label = "Server is on the wrong version!"
                                Toast.makeText(
                                    ctx, "Server is on the wrong version!", Toast.LENGTH_SHORT
                                ).show();
                                activity?.finish()
                                return@runOnUiThread
                            }
                        }

                        LoginResponse.UNABLE_TO_AUTH -> {
                            runOnUiThread {
                                label = "Unable to auth!"
                                Toast.makeText(ctx, "Unable to auth!", Toast.LENGTH_SHORT).show();
                                activity?.finish()

                                return@runOnUiThread
                            }
                        }

                        LoginResponse.OK -> {
                            runOnUiThread {
                                label = "Logged in as ${cfg.username}!"
                                progress = .6f;
                            }
                        }
                    }

                    runOnUiThread {
                        label = "Updating hash"
                        progress = .7f
                    }
                    val mapfile = File(ctx.cacheDir.path + "/map.map");
                    val remoteMapHash = api.mapHash
                    var localMapHash = "";

                    if (mapfile.exists()) {
                        localMapHash = DigestUtils.sha256Hex(FileInputStream(mapfile))
                    }
                    runOnUiThread {
                        label = "Downloading"
                        progress = 1f
                    }
                    if (!remoteMapHash.equals(localMapHash)) {
                        val url = URL(cfg.base_url + "/mapserver/getServerMap")
                        val connection = url.openConnection() as HttpURLConnection
                        connection.setRequestProperty("Authorization", "Bearer " + cfg.jwt_token)
                        connection.connect()

                        // expect HTTP 200 OK, so we don't mistakenly save error report
                        // instead of the file
                        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                            throw RuntimeException(
                                IOException(
                                    ("Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage())
                                )
                            )
                        }

                        // this will be useful to display download percentage
                        // might be -1: server did not report the length
                        val fileLength = connection.contentLength
                        Log.i("LoginActivity", "FL $fileLength")

                        // download the file
                        val input = connection.inputStream
                        val output = FileOutputStream(mapfile)

                        val data = ByteArray(65535)
                        var total: Long = 0
                        var count: Int
                        while ((input.read(data).also { count = it }) != -1) {
                            output.write(data, 0, count)
                            total += count.toLong()
                            if (fileLength > 0)  // only if total length is known
                            {
                                val finalTotal = total.toInt()
                                runOnUiThread {
                                    progress = finalTotal.toFloat() / fileLength
                                    label =
                                        "Map download ${(100 * (finalTotal.toFloat() / fileLength)).toInt()}%"
                                }
                            }
                        }
                        Log.i("LoginActivity", "total $total")
                    }

                    runOnUiThread {
                        ctx.startActivity(Intent(ctx, AwaitStartActivity::class.java))
                        val a = ctx as? Activity
                        a?.finish();
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
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
fun GreetingPreview3() {
    ComposeTestTheme {
        LoginView()
    }
}