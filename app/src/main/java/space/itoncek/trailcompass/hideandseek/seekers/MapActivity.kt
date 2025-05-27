package space.itoncek.trailcompass.hideandseek.seekers

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.mapsforge.core.model.LatLong
import org.mapsforge.core.util.Parameters
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.android.util.AndroidUtil
import org.mapsforge.map.android.view.MapView
import org.mapsforge.map.datastore.MapDataStore
import org.mapsforge.map.layer.overlay.Marker
import org.mapsforge.map.layer.renderer.TileRendererLayer
import org.mapsforge.map.reader.MapFile
import org.mapsforge.map.rendertheme.internal.MapsforgeThemes
import org.mapsforge.map.util.MapViewProjection
import space.itoncek.trailcompass.R
import space.itoncek.trailcompass.api.HideAndSeekApiFactory
import space.itoncek.trailcompass.client.api.HideAndSeekAPI
import space.itoncek.trailcompass.ui.theme.ComposeTestTheme
import java.io.File
import java.io.FileInputStream
import kotlin.concurrent.thread

@Composable
fun MapActivityMain(navigate: (String) -> Unit, screen: String) {
    val ctx = LocalContext.current
    val api: HideAndSeekAPI = HideAndSeekApiFactory(ctx.filesDir).generateApi()
    val markers = remember { mutableStateMapOf<String, Marker>() }
    Box(
        modifier = Modifier
            .fillMaxSize(1f)
            .background(MaterialTheme.colorScheme.background)
    ) {

        MapsforgeMapView(markers = markers, context = ctx, onMapTap = { position ->
            addMarker(
                "marker_${System.currentTimeMillis()}",
                position,
                Color.Red.toArgb(),
                ctx,
                markers
            )
        })
        Column(
            modifier = Modifier
                .fillMaxSize(),
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
                        Text("todo")
                    }
                    Box {
                        Text("00:00:00", fontSize = 36.sp, fontWeight = FontWeight.Black)
                    }
                    Box {
                        IconButton(onClick = {
                            // TODO))
                        }) {
                            Icon(Icons.Rounded.Sync, "Sync");
                        }
                    }
                }
            }

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
                        GenerateNavBar(navigate, "map")
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
                        Text("#madebyitoncek", fontSize = 10.sp, modifier = Modifier.padding(start=4.dp))
                    }
                }
            }
        }
        Text(modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(end = 8.dp),text = "(c) OpenStreetMap contributors", fontSize = 8.sp)
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(LocalContext.current) {
        val observer = LifecycleEventObserver { source, event ->
            if (event == Lifecycle.Event.ON_START) {
                thread {
                    api.raw.auth()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}



fun addMarker(
    id: String,
    position: LatLong,
    color: Int,
    context: Context,
    markers: MutableMap<String, Marker>
) {
    Log.w("MapActivity", "TAP")
    val bitmap = AndroidGraphicFactory.convertToBitmap(context.getDrawable(R.drawable.marker_blue));
    bitmap.scaleTo(62, 62)
    val marker = Marker(
        position,
        bitmap,
        0,
        0
    )
    markers[id] = marker
    Log.w("MapActivity","marker ${markers.size}")
}

fun removeMarker(id: String, markers: MutableMap<String, Marker>) {
    markers.remove(id)
}

@Composable
fun MapsforgeMapView(
    markers: MutableMap<String, Marker>, onMapTap: (LatLong) -> Unit, context: Context
) {
    AndroidView(factory = { ctx ->
        val mapView = MapView(ctx).apply {
            isClickable = true
            mapScaleBar.isVisible = true
            setBuiltInZoomControls(true)

            Parameters.NUMBER_OF_THREADS = 8
            Parameters.ANTI_ALIASING = true
            Parameters.PARENT_TILES_RENDERING = Parameters.ParentTilesRendering.SPEED

            val tileCache = AndroidUtil.createTileCache(
                ctx,
                "mapcache",
                model.displayModel.tileSize,
                1f,
                model.frameBufferModel.overdrawFactor
            )

            val file: File

            if (ctx.cacheDir == null) {
                file = File("O:\\test\\ComposeTest\\data\\map.map")
                Log.w("MapActivity","Using override!")
            } else {
                file = File(ctx.cacheDir.path + "/map.map")
                Log.i("MapActivity", "Mapfile found!")
            }

            val mapDataStore: MapDataStore = MapFile(FileInputStream(file))
            val tileRendererLayer = TileRendererLayer(
                tileCache, mapDataStore, model.mapViewPosition, AndroidGraphicFactory.INSTANCE
            )
            tileRendererLayer.cacheTileMargin = 1
            tileRendererLayer.cacheZoomMinus = 1
            tileRendererLayer.cacheZoomPlus = 2
            tileRendererLayer.setXmlRenderTheme(MapsforgeThemes.OSMARENDER)

            tileCache.purge()
            tileRendererLayer.requestRedraw()

            layerManager.layers.add(tileRendererLayer)

            setCenter(mapDataStore.startPosition())
            setZoomLevel(mapDataStore.startZoomLevel())
        }

        val gestureDetector =
            GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
                    val projection = MapViewProjection(mapView)
                    val tappedPosition =
                        projection.fromPixels(event.x.toDouble(), event.y.toDouble())
                    onMapTap(tappedPosition)
                    return true
                }
            })

        mapView.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
        }

        mapView
    }, update = { mapView ->
        val activity = context as? Activity
        if (activity != null) {
            AndroidGraphicFactory.createInstance(activity.application)
        } else {
            Log.w("MapActivity","Unable to init AndroidGraphicFactory")
        }
        markers.forEach { (_, marker) ->
            if (!mapView.layerManager.layers.contains(marker)) {
                mapView.layerManager.layers.add(marker)
            }
        }
        mapView.layerManager.redrawLayers()
    }, modifier = Modifier.fillMaxSize(), onRelease = { o ->
        o.destroy()
        o.destroyAll()
        System.gc()
    })
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
fun GreetingPreview5() {
    ComposeTestTheme {
        MapActivityMain({ }, "map")
    }
}