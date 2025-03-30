package space.itoncek.trailcompass.app.hideandseek.hider_ui;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static space.itoncek.trailcompass.app.utils.RunnableUtils.runOnBackgroundThread;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.util.Parameters;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.datastore.MapDataStore;
import org.mapsforge.map.layer.Layers;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.internal.MapsforgeThemes;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import space.itoncek.trailcompass.app.R;
import space.itoncek.trailcompass.app.hideandseek.api.HideAndSeekAPIFactory;
import space.itoncek.trailcompass.app.hideandseek.seeker_ui.SeekerMapView;
import space.itoncek.trailcompass.app.utils.MapUtils;
import space.itoncek.trailcompass.client.api.HideAndSeekAPI;

public class HiderMapView extends AppCompatActivity {
    private HideAndSeekAPI api;
    File mapfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_hide_mapview);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        AndroidGraphicFactory.createInstance(getApplication());

        mapfile = new File(getCacheDir() + "/map.map");
        api = new HideAndSeekAPIFactory(getFilesDir()).generateHideAndSeekAPI();

        if (amIHider()) {
            showAdminView();
            setUsername();
            setupTimer();
            setupButtons();
            openMap();
        } else {
            Intent i = new Intent(this, SeekerMapView.class);
            startActivity(i);
            finish();
        }
    }

    private boolean amIHider() {
        AtomicBoolean result = new AtomicBoolean(false);
        CountDownLatch cdl = new CountDownLatch(1);
        runOnBackgroundThread(() -> {
            try {
                result.set(api.amIHider());
                cdl.countDown();
            } catch (IOException e) {
                Log.e(HiderMapView.class.getName(), "Unable to determine if I'm a hider or not", e);
            }
        });
        try {
            cdl.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return result.get();
    }

    private void setupButtons() {
        findViewById(R.id.map_resetButton).setOnClickListener((c) -> {
            setUsername();
            showAdminView();
            setupTimer();
        });
    }

    // TODO)) Create timer handler!
    private void setupTimer() {

    }

    private void setUsername() {
        Thread t = new Thread(() -> {
            try {
                String name = api.myName();
                runOnUiThread(() -> ((TextView) findViewById(R.id.map_username)).setText(name));
            } catch (IOException e) {
                Log.e(HiderMapView.class.getName(), "Unable to contact the main server", e);
            }
        });
        t.start();
    }

    private void showAdminView() {
        Thread t = new Thread(() -> {
            try {
                if (api.amIAdmin()) {
                    runOnUiThread(() -> findViewById(R.id.map_switchToSettings).setVisibility(VISIBLE));
                } else {
                    runOnUiThread(() -> findViewById(R.id.map_switchToSettings).setVisibility(GONE));
                }
            } catch (IOException e) {
                Log.e(HiderMapView.class.getName(), "Unable to contact the main server", e);
            }
        });
        t.start();
    }

    private void openMap() {
        MapView mw = new MapView(this);
        mw.setClickable(true);
        mw.getMapScaleBar().setVisible(true);
        mw.setBuiltInZoomControls(true);

        Parameters.NUMBER_OF_THREADS = 8;
        Parameters.ANTI_ALIASING = true;
        Parameters.PARENT_TILES_RENDERING = Parameters.ParentTilesRendering.SPEED;

        TileCache tileCache = AndroidUtil.createTileCache(this, "mapcache",
                mw.getModel().displayModel.getTileSize(), 1f,
                mw.getModel().frameBufferModel.getOverdrawFactor());

        MapDataStore mapDataStore = new MapFile(mapfile);
        TileRendererLayer tileRendererLayer = new TileRendererLayer(tileCache, mapDataStore,
                mw.getModel().mapViewPosition, AndroidGraphicFactory.INSTANCE);
        tileRendererLayer.setCacheTileMargin(1);
        tileRendererLayer.setCacheZoomMinus(1);
        tileRendererLayer.setCacheZoomPlus(2);
        tileRendererLayer.setXmlRenderTheme(MapsforgeThemes.OSMARENDER);

        Layers layers = mw.getLayerManager().getLayers();
        layers.add(tileRendererLayer);

        Marker m1 = MapUtils.createMarker(this, R.drawable.location_marker_blue, new LatLong(50.0835035, 14.4341259));
        Marker m2 = MapUtils.createMarker(this, R.drawable.location_marker_green, new LatLong(50.0948317, 14.4158918));
        Marker m3 = MapUtils.createMarker(this, R.drawable.location_marker_red, new LatLong(50.0833949, 14.3950736));
        Marker m4 = MapUtils.createMarker(this, R.drawable.location_marker_orange, new LatLong(50.0637249, 14.4165013));

        layers.add(m1);
        layers.add(m2);
        layers.add(m3);
        layers.add(m4);

        mw.setCenter(mapDataStore.startPosition());
        mw.setZoomLevel(mapDataStore.startZoomLevel());
        View C = findViewById(R.id.map_mapview);
        ViewGroup parent = (ViewGroup) C.getParent();
        int index = parent.indexOfChild(C);
        parent.removeView(C);
        C = mw;
        parent.addView(C, index);

    }
}