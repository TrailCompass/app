package space.itoncek.trailcompass.hideandseek.hiders

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.Info
import com.composables.Send
import com.composables.Skull
import space.itoncek.trailcompass.client.api.HideAndSeekAPI
import space.itoncek.trailcompass.commons.objects.Card
import space.itoncek.trailcompass.commons.objects.CardClass
import space.itoncek.trailcompass.commons.objects.CardType
import space.itoncek.trailcompass.runOnUiThread
import space.itoncek.trailcompass.ui.theme.ComposeTestTheme
import java.util.UUID
import kotlin.concurrent.thread

@Composable
fun HiderDeckActivity(api: HideAndSeekAPI?) {
    val ctx = LocalContext.current
    var cardList = remember { mutableListOf<Card>() }
    var handSize by remember { mutableIntStateOf(5) }
    var update by remember { mutableStateOf(true) }
    var test by remember { mutableStateOf("test1") }

    if (api != null) {
        val list = ArrayList<Card>();
        thread {
            list.addAll(api.raw.deck().listCards())
            handSize = api.raw.deck().handSize;
            runOnUiThread {
                cardList = list
            }
        }
    } else {
        val list = ArrayList<Card>();
        list.add(Card(UUID.randomUUID(), UUID.randomUUID(), CardType.Veto, false))
        list.add(Card(UUID.randomUUID(), UUID.randomUUID(), CardType.Move, false))
        list.add(
            Card(
                UUID.randomUUID(),
                UUID.randomUUID(),
                CardType.TimeBonusGreen,
                false
            )
        )
        list.add(
            Card(
                UUID.randomUUID(),
                UUID.randomUUID(),
                CardType.Curse_JammmedDoor,
                false
            )
        )
        cardList = list;
    }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        (0 until 5).forEach { i ->
            val card = cardList.getOrNull(i);
            Row(
                Modifier
                    .weight(1f, true)
                    .fillMaxWidth(1f)
                    .padding(8.dp)
            ) {
                if (card == null) {
                    SpawnEmptyCard()
                } else {
                    when (card.type.cardClass) {
                        CardClass.Curse -> SpawnCurseCard(api, card)
                        CardClass.Powerup -> SpawnPowerupCard(api, card)
                        CardClass.Time -> SpawnTimeCard(api, card)
                        null -> SpawnErrorCard("Null card type")
                    }
                }
            }
        }
    }
}

@Composable
fun SpawnErrorCard(s: String) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
    ) {
        Text(s)
    }
}

@Composable
fun SpawnTimeCard(api: HideAndSeekAPI?, card: Card) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
    ) {
        Text(card.type.name)
    }
}

@Composable
fun SpawnPowerupCard(api: HideAndSeekAPI?, card: Card) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
    ) {
        Text(card.type.name)
    }
}

@Composable
fun SpawnCurseCard(api: HideAndSeekAPI?, card: Card) {
    val ctx = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.padding(10.dp)
                .fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.weight(1f)
            ) {
                IconButton({
                    runOnUiThread {
                        Toast.makeText(ctx,"This is a curse", Toast.LENGTH_SHORT).show()
                    }
                }, modifier = Modifier.weight(1f).padding(bottom = 10.dp)) {
                    Icon(Skull, "skull", modifier = Modifier.size(64.dp))
                }
                IconButton({}, modifier = Modifier.weight(.8f)) {
                    Icon(Info, "get_card_info")
                }
                IconButton({}, modifier = Modifier.weight(1f).padding(top = 8.dp)) {
                    Icon(Send, "get_card_info")
                }
            }
            Column(modifier = Modifier.weight(6f).padding(start = 8.dp)) {
                var title by remember { mutableStateOf("Loading...") }
                var description by remember { mutableStateOf("Loading...") }
                var castingCost by remember { mutableStateOf("Loading...") }

                val curseMetadata = api?.raw?.deck()?.getCurseMetadata(card.cardID)
                if (curseMetadata == null) {
                    title = "The Jammed Door"
                    description =
                        "For the next 1 hour, whenever the seeker(s) want to pass through a doorway into a building, business, train, or other vehicle they must first roll 2 dice. If they do not roll a 7 or higher they cannot enter that space (including through other doorways) any given doorway can be reattempted after [5 minutes,10 minutes,15 minutes]."
                    castingCost = "Discard 2 cards"
                } else {
                    title = curseMetadata.title
                    description = curseMetadata.description
                    castingCost = curseMetadata.castingConst
                }

                Text("Curse of $title", fontWeight = FontWeight.Bold, fontSize = 23.sp,modifier = Modifier.wrapContentHeight())
                Text(text = description, fontSize = 12.sp, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f).padding(top = 4.dp, bottom = 4.dp), style = TextStyle(
                    lineHeight = 14.sp
                ))
                Text("Casting cost: $castingCost",modifier = Modifier.wrapContentHeight())
            }
        }
    }
}

fun trim(description: String): String {
    val maxlen = 90;
    if(description.length > maxlen) {
        return description.substring(0,maxlen) + "…"
    } else return description;
}

@Composable
fun SpawnEmptyCard() {
    val stroke = Stroke(
        width = 8f,
        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
            floatArrayOf(20f, 20f),
            0f
        )
    )

    Box(
        modifier = Modifier
            .background(Color(0x50888888))
            .drawBehind {
                drawRoundRect(
                    color = Color.Gray,
                    style = stroke,
                    cornerRadius = CornerRadius(8.dp.toPx())
                )
            }
            .fillMaxHeight()
            .fillMaxWidth(),
    ) {

    }
}


//@Preview(
//    name = "Dynamic Red Dark",
//    group = "dark",
//    showBackground = true,
//    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
//    wallpaper = Wallpapers.RED_DOMINATED_EXAMPLE
//)
//@Preview(
//    name = "Dynamic Green Dark",
//    group = "dark",
//    showBackground = true,
//    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
//    wallpaper = Wallpapers.GREEN_DOMINATED_EXAMPLE
//)
//@Preview(
//    name = "Dynamic Yellow Dark",
//    group = "dark",
//    showBackground = true,
//    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
//    wallpaper = Wallpapers.YELLOW_DOMINATED_EXAMPLE
//)
@Preview(
    name = "Dynamic Blue Dark",
    group = "dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    wallpaper = Wallpapers.BLUE_DOMINATED_EXAMPLE
)
//@Preview(
//    name = "Dynamic Red Light",
//    group = "light",
//    showBackground = true,
//    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
//    wallpaper = Wallpapers.RED_DOMINATED_EXAMPLE
//)
//@Preview(
//    name = "Dynamic Green Light",
//    group = "light",
//    showBackground = true,
//    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
//    wallpaper = Wallpapers.GREEN_DOMINATED_EXAMPLE
//)
//@Preview(
//    name = "Dynamic Yellow Light",
//    group = "light",
//    showBackground = true,
//    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
//    wallpaper = Wallpapers.YELLOW_DOMINATED_EXAMPLE
//)
//@Preview(
//    name = "Dynamic Blue Light",
//    group = "light",
//    showBackground = true,
//    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
//    wallpaper = Wallpapers.BLUE_DOMINATED_EXAMPLE
//)
@Composable
fun GreetingPreview7() {
    ComposeTestTheme {
        HiderDeckActivity(null)
    }
}