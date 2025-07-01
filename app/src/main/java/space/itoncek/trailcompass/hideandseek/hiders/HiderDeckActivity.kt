@file:OptIn(ExperimentalMaterial3Api::class)

package space.itoncek.trailcompass.hideandseek.hiders

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.Delete_forever
import com.composables.Send
import com.composables.Skull
import space.itoncek.trailcompass.client.api.HideAndSeekAPI
import space.itoncek.trailcompass.commons.objects.Card
import space.itoncek.trailcompass.commons.objects.CardClass
import space.itoncek.trailcompass.commons.objects.CardType
import space.itoncek.trailcompass.icons.TimeBonusBlue
import space.itoncek.trailcompass.icons.TimeBonusGreen
import space.itoncek.trailcompass.icons.TimeBonusOrange
import space.itoncek.trailcompass.icons.TimeBonusRed
import space.itoncek.trailcompass.icons.TimeBonusYellow
import space.itoncek.trailcompass.runOnUiThread
import space.itoncek.trailcompass.ui.theme.ComposeTestTheme
import java.util.UUID
import kotlin.concurrent.thread


@Composable
fun HiderDeckActivity(api: HideAndSeekAPI?) {
    val ctx = LocalContext.current
    var cardList = remember { mutableListOf<Card>() }
    var handSize by remember { mutableIntStateOf(5) }
    val update: ()-> Unit = {
        if (api != null) {
            val list = ArrayList<Card>();
            thread {
                list.addAll(api.raw.deck().listCards())
                handSize = api.raw.deck().handSize;
                runOnUiThread {
                    cardList = list
                }
            }
        }
    }

    if (api != null) {
        update.invoke()
    } else {
        val list = ArrayList<Card>();
        list.add(
            Card(
                UUID.randomUUID(),
                UUID.randomUUID(),
                CardType.Curse_JammmedDoor,
                false
            )
        )
        list.add(
            Card(
                UUID.randomUUID(),
                UUID.randomUUID(),
                CardType.TimeBonusGreen,
                false
            )
        )
        list.add(Card(UUID.randomUUID(), UUID.randomUUID(), CardType.Veto, false))
        list.add(Card(UUID.randomUUID(), UUID.randomUUID(), CardType.Move, false))
        cardList = list;
    }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        (0 until 5).forEach { i ->
            val card = cardList.getOrNull(i);
            Row(
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(8.dp)
            ) {
                if (card == null) {
                    SpawnEmptyCard()
                } else {
                    when (card.type.cardClass) {
                        CardClass.Curse -> SpawnCurseCard(api, card,update)
                        CardClass.Powerup -> SpawnPowerupCard(api, card)
                        CardClass.Time -> SpawnTimeCard(api, card,update)
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
            .height(200.dp)
            .fillMaxWidth(),
    ) {
        Text(s)
    }
}

@Composable
fun SpawnTimeCard(api: HideAndSeekAPI?, card: Card, updateCardList: () -> Unit) {
    val ctx = LocalContext.current
    val showDeleteDialog = remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .height(100.dp)
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
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight()
            ) {
                OutlinedIconButton({
                    runOnUiThread {
                        Toast.makeText(ctx, "This is a curse", Toast.LENGTH_SHORT).show()
                    }
                }, modifier = Modifier.weight(1f).size(96.dp),shape = RectangleShape, border = BorderStroke(0.dp,Color.Unspecified)) {
                    Image(pickIcon(card, MaterialTheme.colorScheme.onPrimaryContainer), "skull", modifier = Modifier.size(128.dp))
                }
            }

            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .weight(5f)
                    .fillMaxHeight()
            ) {
                val length = when (card.type) {
                    CardType.TimeBonusRed -> TimeBonusDuration(2,3,5);
                    CardType.TimeBonusOrange -> TimeBonusDuration(4,6,10)
                    CardType.TimeBonusYellow -> TimeBonusDuration(6,9,15)
                    CardType.TimeBonusGreen -> TimeBonusDuration(8,12,20)
                    CardType.TimeBonusBlue -> TimeBonusDuration(12,18,30)
                    null, CardType.Randomize, CardType.Veto, CardType.Duplicate, CardType.Move, CardType.Discard1, CardType.Discard2, CardType.Draw1Expand, CardType.Curse_Zoologist, CardType.Curse_UnguidedTourist, CardType.Curse_EndlessTumble, CardType.Curse_HiddenHangman, CardType.Curse_OverflowingChalice, CardType.Curse_MediocreTravelAgent, CardType.Curse_LuxuryCard, CardType.Curse_UTurn, CardType.Curse_BridgeTroll, CardType.Curse_WaterWeight, CardType.Curse_JammmedDoor, CardType.Curse_Cairn, CardType.Curse_Urbex, CardType.Curse_ImpressionableConsumer, CardType.Curse_EggPartner, CardType.Curse_DistantCuisine, CardType.Curse_RightTurn, CardType.Curse_Labyrinth, CardType.Curse_BirdGuide, CardType.Curse_SpottyMemory, CardType.Curse_LemonPhylactery, CardType.Curse_DrainedBrain, CardType.Curse_RansomNote, CardType.Curse_GamblersFeet -> TimeBonusDuration(-1,-1,-1);
                }
                var selectedGameMode = 1;

                thread {
                    if(api == null) {
                        selectedGameMode = 2;
                    } else {
                        selectedGameMode = api.raw.gameMgr().gameSize
                    }
                }

                val selectedLength = when (selectedGameMode) {
                    1 -> length.small
                    2 -> length.medium
                    else -> length.large
                }
                Text(
                    "$selectedLength minute bonus",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )

                IconButton({
                    showDeleteDialog.value = true;
                }, modifier = Modifier.weight(1f)) {
                    Icon(Delete_forever, "discard_timeBonus", modifier = Modifier.size(64.dp))
                }
            }
        }


        DiscardDialog(card,showDeleteDialog, {
            api?.raw?.deck()?.discardCard(card.cardID)
            updateCardList.invoke()
        })
    }
}

data class TimeBonusDuration(val small: Int, val medium: Int, val large: Int)


fun pickIcon(card: Card, primaryColor: Color): ImageVector {
    return when (card.type) {
        CardType.TimeBonusRed -> TimeBonusRed(primaryColor).vector
        CardType.TimeBonusOrange -> TimeBonusOrange(primaryColor).vector
        CardType.TimeBonusYellow -> TimeBonusYellow(primaryColor).vector
        CardType.TimeBonusGreen -> TimeBonusGreen(primaryColor).vector
        CardType.TimeBonusBlue -> TimeBonusBlue(primaryColor).vector
        null, CardType.Randomize, CardType.Veto, CardType.Duplicate, CardType.Move, CardType.Discard1, CardType.Discard2, CardType.Draw1Expand, CardType.Curse_Zoologist, CardType.Curse_UnguidedTourist, CardType.Curse_EndlessTumble, CardType.Curse_HiddenHangman, CardType.Curse_OverflowingChalice, CardType.Curse_MediocreTravelAgent, CardType.Curse_LuxuryCard, CardType.Curse_UTurn, CardType.Curse_BridgeTroll, CardType.Curse_WaterWeight, CardType.Curse_JammmedDoor, CardType.Curse_Cairn, CardType.Curse_Urbex, CardType.Curse_ImpressionableConsumer, CardType.Curse_EggPartner, CardType.Curse_DistantCuisine, CardType.Curse_RightTurn, CardType.Curse_Labyrinth, CardType.Curse_BirdGuide, CardType.Curse_SpottyMemory, CardType.Curse_LemonPhylactery, CardType.Curse_DrainedBrain, CardType.Curse_RansomNote, CardType.Curse_GamblersFeet -> Icons.Rounded.Warning
    }
}

@Composable
fun SpawnPowerupCard(api: HideAndSeekAPI?, card: Card) {
    Box(
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth(),
    ) {
        Text(card.type.name)
    }
}

@Composable
fun SpawnCurseCard(api: HideAndSeekAPI?, card: Card, updateCardList: () -> Unit) {
    val ctx = LocalContext.current
    val showDeleteDialog = remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .height(200.dp)
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
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight()
            ) {
                IconButton({
                    runOnUiThread {
                        Toast.makeText(ctx, "This is a curse", Toast.LENGTH_SHORT).show()
                    }
                }, modifier = Modifier.fillMaxWidth().weight(1f)) {
                    Icon(Skull, "skull",Modifier.size(96.dp))
                }

                Row {
                    IconButton({
                        showDeleteDialog.value = true;
                    }) {
                        Icon(Delete_forever, "discard_curse", modifier = Modifier.size(64.dp))
                    }

                    IconButton({
                        runOnUiThread {
                            Toast.makeText(ctx, "This is a curse", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(Send, "cast_curse", modifier = Modifier.size(64.dp))
                    }
                }
            }
            Column(modifier = Modifier.weight(5f).padding(start = 8.dp)) {
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

                Text(
                    "Curse of $title",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.wrapContentHeight()
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 4.dp, bottom = 4.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = description,
                        fontSize = 12.sp,
                        style = TextStyle(
                            lineHeight = 14.sp
                        )
                    )
                }
                Text(
                    "Casting cost: $castingCost",
                    modifier = Modifier.wrapContentHeight(),
                    fontSize = 14.sp
                )
            }
        }
    }

    DiscardDialog(card,showDeleteDialog, {
        api?.raw?.deck()?.discardCard(card.cardID)
        updateCardList.invoke()
    })
}

@Composable
fun DiscardDialog(card: Card, showDeleteDialog: MutableState<Boolean>, discardCard: () -> Unit) {
    if (showDeleteDialog.value) {
        CreateDialog(
            "Discard a card",
            "This action is not reversible. Once \"confirm\" has been clicked, the card will be removed from your hand.\n\nCard: ${card.type.name}",
            Delete_forever, {
                discardCard.invoke()
                showDeleteDialog.value = false;
            },
            {
                showDeleteDialog.value = false;
            })
    }
}

@Composable
fun CreateDialog(title: String, body: String, icon: ImageVector, confirm: () -> Unit, dismiss: () -> Unit){
    AlertDialog(
        onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onDismissRequest.
            dismiss.invoke()
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, "dialog_icon", modifier = Modifier.padding(end = 8.dp))
                Text(text = title)
            }
        },
        text = { Text(text = body) },
        confirmButton = {
            TextButton(onClick = { confirm.invoke() }) { Text("Confirm") }
        },
        dismissButton = {
            TextButton(onClick = { dismiss.invoke() }) { Text("Dismiss") }
        }
    )
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
            .height(200.dp)
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