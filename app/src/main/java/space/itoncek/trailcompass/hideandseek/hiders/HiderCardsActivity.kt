package space.itoncek.trailcompass.hideandseek.hiders

import android.content.res.Configuration
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import space.itoncek.trailcompass.client.api.HideAndSeekAPI
import space.itoncek.trailcompass.commons.objects.Card
import space.itoncek.trailcompass.commons.objects.CardClass
import space.itoncek.trailcompass.commons.objects.CardType
import space.itoncek.trailcompass.ui.theme.ComposeTestTheme
import java.util.UUID

@Composable
fun HiderCardsActivity(api: HideAndSeekAPI?) {
    val ctx = LocalContext.current
    val cardList = remember { mutableListOf<Card>() }
    var update by remember { mutableStateOf(true) }
    var test by remember { mutableStateOf("test1") }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(test)
        for (card in cardList) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(.95f)
                    .wrapContentHeight()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                var title by remember { mutableStateOf("Loading...") }
                var description by remember { mutableStateOf("Loading...") }
                var note by remember { mutableStateOf("Loading...") }
                LaunchedEffect(card) {
                    if (card.type.cardClass == CardClass.Curse) {
                        if (api != null) {
                            val curseMetadata = api.raw.deck().getCurseMetadata(card.cardID)
                            title = curseMetadata.title
                            description = curseMetadata.description
                            note = curseMetadata.castingConst;
                        } else {
                            title = "Test curse"
                            description =
                                "This curse is meant as a test in the unexpected case of 'api' object being null during debugging."
                            note = "Please report this if in production!"
                        }
                    }
                }
                Text(test)
            }
        }


        LaunchedEffect(update) {
            if (api != null) {
                //cardList.clear()
                cardList.addAll(api.raw.deck().listCards())
            } else {
                //cardList.clear()
                cardList.add(Card(UUID.randomUUID(), UUID.randomUUID(), CardType.Veto, false))
                cardList.add(Card(UUID.randomUUID(), UUID.randomUUID(), CardType.Move, false))
                cardList.add(
                    Card(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        CardType.TimeBonusGreen,
                        false
                    )
                )
                cardList.add(
                    Card(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        CardType.Curse_JammmedDoor,
                        false
                    )
                )
                test = "%d".format(cardList.count())
            }
        }
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
        HiderCardsActivity(null)
    }
}