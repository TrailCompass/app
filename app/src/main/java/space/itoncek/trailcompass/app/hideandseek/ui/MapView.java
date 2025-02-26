package space.itoncek.trailcompass.app.hideandseek.ui;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.mapsforge.core.util.Parameters;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.datastore.MapDataStore;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.internal.MapsforgeThemes;

import java.io.File;
import java.io.IOException;

import space.itoncek.trailcompass.app.R;
import space.itoncek.trailcompass.app.hideandseek.api.HideAndSeekAPIFactory;
import space.itoncek.trailcompass.client.api.HideAndSeekAPI;

public class MapView extends AppCompatActivity {
    private HideAndSeekAPI api;
    private org.mapsforge.map.android.view.MapView mapView;
    File mapfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_hideandseek_mapview);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        AndroidGraphicFactory.createInstance(getApplication());

        mapfile = new File(getCacheDir() + "/map.map");
        mapView = findViewById(R.id.map_mapview);
        api = new HideAndSeekAPIFactory(getFilesDir()).generateHideAndSeekAPI();

        showAdminView();
        setUsername();
        setupTimer();
        setupButtons();
        openMap();
    }

    private void setupButtons() {
        findViewById(R.id.map_resetButton).setOnClickListener((c)-> {
            setUsername();
            showAdminView();
            setupTimer();
        });
    }

    private void setupTimer() {

    }

    private void setUsername() {
        Thread t = new Thread(()-> {
            try {
                String name = api.myName();
                runOnUiThread(()-> {
                    ((TextView)findViewById(R.id.map_username)).setText(name);
                });
            } catch (IOException e) {
                Log.e(MapView.class.getName(), "Unable to contact the main server", e);
            }
        });
        t.start();
    }

    private void showAdminView() {
        Thread t = new Thread(()-> {
            try {
                if (api.amIAdmin()) {
                    runOnUiThread(()->findViewById(R.id.map_switchToSettings).setVisibility(VISIBLE));
                } else {
                    runOnUiThread(()->findViewById(R.id.map_switchToSettings).setVisibility(GONE));
                }
            } catch (IOException e) {
                Log.e(MapView.class.getName(), "Unable to contact the main server", e);
            }
        });
        t.start();
    }

    private void openMap() {
        mapView.setClickable(true);
        mapView.getMapScaleBar().setVisible(true);
        mapView.setBuiltInZoomControls(true);

        Parameters.NUMBER_OF_THREADS = 8;
        Parameters.ANTI_ALIASING = true;
        Parameters.PARENT_TILES_RENDERING = Parameters.ParentTilesRendering.SPEED;

        TileCache tileCache = AndroidUtil.createTileCache(this, "mapcache",
                mapView.getModel().displayModel.getTileSize(), 1f,
                mapView.getModel().frameBufferModel.getOverdrawFactor());

        MapDataStore mapDataStore = new MapFile(mapfile);
        TileRendererLayer tileRendererLayer = new TileRendererLayer(tileCache, mapDataStore,
                mapView.getModel().mapViewPosition, AndroidGraphicFactory.INSTANCE);
        tileRendererLayer.setCacheTileMargin(1);
        tileRendererLayer.setCacheZoomMinus(1);
        tileRendererLayer.setCacheZoomPlus(2);
        tileRendererLayer.setXmlRenderTheme(MapsforgeThemes.OSMARENDER);

        mapView.getLayerManager().getLayers().add(tileRendererLayer);

        mapView.setCenter(mapDataStore.startPosition());
        mapView.setZoomLevel(mapDataStore.startZoomLevel());
    }
}