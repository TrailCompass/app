package space.itoncek.composetest

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.commons.codec.digest.DigestUtils.sha512Hex
import space.itoncek.composetest.api.HideAndSeekApiFactory
import space.itoncek.composetest.debug.DebugActivity
import space.itoncek.composetest.hideandseek.LoginActivity
import space.itoncek.composetest.ui.theme.ComposeTestTheme
import space.itoncek.composetest.ui.theme.DesignFg
import space.itoncek.trailcompass.client.api.HideAndSeekAPI
import space.itoncek.trailcompass.client.api.ServerValidity
import kotlin.concurrent.thread

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContent {
            ComposeTestTheme {
                MainUI()
            }
        }
    }
}

val modeList = listOf(
    "Hide and seek", "Debug"
)

fun runOnUiThread(block: suspend () -> Unit) = CoroutineScope(Dispatchers.Main).launch { block() }

@Composable
fun MainUI() {
    Column(
        modifier = Modifier
            .fillMaxSize(1f)
            .background(MaterialTheme.colorScheme.background)
            .padding(8.dp)
    ) {
        Card(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(1f)
                .wrapContentHeight(),
            elevation = CardDefaults.elevatedCardElevation(8.dp)
        ) {
            if (isSystemInDarkTheme()) {
                Image(
                    painter = painterResource(R.drawable.title_dark),
                    contentDescription = "Title",
                    modifier = Modifier.padding(8.dp)
                )
            }else {
                Image(
                    painter = painterResource(R.drawable.title_light),
                    contentDescription = "Title",
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
        var module by remember { mutableStateOf(modeList[0]) }
        ModeSelection {
            module = it
        }

        if (module.isNotEmpty()) {
            if (module == modeList[0]) {
                HideAndSeekLogin()
            } else {
                DebugModeLogin()
            }
        }
    }
}

@Composable
fun HideAndSeekLogin() {
    val ctx = LocalContext.current;
    val api: HideAndSeekAPI = HideAndSeekApiFactory(ctx.filesDir).generateApi()
    var state by remember { mutableStateOf(ServerValidity.NOT_FOUND) }
    var url by remember { mutableStateOf(api.getConfig().base_url) }
    var username by remember { mutableStateOf(api.getConfig().username) }
    val passwordHint = if (api.getConfig().password_hash != null) "(not changed)" else "(empty)"
    var password by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(1f)
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
        elevation = CardDefaults.elevatedCardElevation(8.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            OutlinedTextField(value = url,
                onValueChange = {
                    url = it
                    thread {
                        try {
                            val cfg = api.config
                            cfg.base_url = it;
                            api.saveConfig(cfg)

                            val s = api.checkValidity();
                            runOnUiThread {
                                state = s
                            }
                        } catch (e: Exception) { /* ignore */
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                label = {
                    Text("Server url")
                })
            OutlinedTextField(value = username,
                onValueChange = {
                    username = it

                    val cfg = api.config
                    cfg.username = it;
                    api.saveConfig(cfg)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                label = {
                    Text("Username")
                })
            OutlinedTextField(value = password,
                onValueChange = {
                    password = it;

                    if (it != "") {
                        val cfg = api.config
                        cfg.password_hash = sha512Hex(it);
                        api.saveConfig(cfg)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                label = {
                    Text("Password")
                },
                placeholder = {
                    Text(passwordHint, color = Color.Gray)
                })
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = state.icon, modifier = Modifier.padding(4.dp), color = DesignFg)
                Button(
                    onClick = {
                        ctx.startActivity(Intent(ctx, LoginActivity::class.java));
                    }, modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    Text("Login")
                }
            }
        }
    }
}

@Composable
fun DebugModeLogin() {
    val c = LocalContext.current;
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(1f)
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
        elevation = CardDefaults.elevatedCardElevation(8.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()) {
            Button(onClick = {
                c.startActivity(Intent(c, DebugActivity::class.java))
            },
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp)) {
                Text(text = "Debug")
            }
        }
    }
}

@Composable
fun DropDownList(
    requestToOpen: Boolean = false,
    list: List<String>,
    request: (Boolean) -> Unit,
    selectedString: (String) -> Unit
) {
    DropdownMenu(
        modifier = Modifier.fillMaxWidth(.9f),
        expanded = requestToOpen,
        onDismissRequest = { request(false) },
    ) {
        list.forEach {
            DropdownMenuItem(modifier = Modifier.fillMaxWidth(), text = {
                Text(
                    it, modifier = Modifier
                        .wrapContentWidth()
                        .align(Alignment.Start)
                )
            }, onClick = {
                request(false)
                selectedString(it)
            })
        }
    }
}

@Composable
fun ModeSelection(
    selectionChanged: (String) -> Unit
) {
    val text = remember { mutableStateOf(modeList[0]) } // initial value
    val isOpen = remember { mutableStateOf(false) } // initial value
    val openCloseOfDropDownList: (Boolean) -> Unit = {
        isOpen.value = it
    }
    val userSelectedString: (String) -> Unit = {
        text.value = it;
        selectionChanged.invoke(it)
    }
    Box(
        modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 0.dp, bottom = 8.dp)
    ) {
        Column {
            OutlinedTextField(
                value = text.value,
                onValueChange = { text.value = it },
                label = { Text(text = "App mode") },
                modifier = Modifier.fillMaxWidth()
            )
            DropDownList(
                requestToOpen = isOpen.value,
                list = modeList,
                openCloseOfDropDownList,
                userSelectedString
            )
        }
        Spacer(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Transparent)
                .padding(10.dp)
                .clickable(onClick = { isOpen.value = true })
        )
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
@Composable
fun GreetingPreview() {
    ComposeTestTheme {
        MainUI()
    }
}