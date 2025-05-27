package space.itoncek.trailcompass.hideandseek.seekers

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.ElectricBolt
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GenerateNavBar(navigate: (String) -> Unit, source: String) {
    IconButton(onClick = {
        navigate("map")
    }, enabled = source != "map") {
        Icon(Icons.Rounded.Map, "Map", Modifier.size(48.dp));
    }
    IconButton(onClick = {
        navigate("requests")
    }, enabled = source != "requests") {
        Icon(Icons.Rounded.Quiz, "Requests", Modifier.size(48.dp));
    }
    IconButton(onClick = {
        navigate("curses")
    }, enabled = source != "curses") {
        Icon(Icons.Rounded.ElectricBolt, "Curses", Modifier.size(48.dp));
    }

    IconButton(onClick = {
        navigate("settings")
    }, enabled = source != "settings") {
        Icon(Icons.Rounded.Build, "Settings", Modifier.size(48.dp));
    }
}