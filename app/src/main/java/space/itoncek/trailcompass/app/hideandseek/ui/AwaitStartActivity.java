package space.itoncek.trailcompass.app.hideandseek.ui;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import static space.itoncek.trailcompass.app.utils.RunnableUtils.runOnBackgroundThread;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import space.itoncek.trailcompass.app.R;
import space.itoncek.trailcompass.app.hideandseek.api.HideAndSeekAPIFactory;
import space.itoncek.trailcompass.app.hideandseek.configurator.ConfiguratorActivity;
import space.itoncek.trailcompass.client.api.GameState;
import space.itoncek.trailcompass.client.api.HideAndSeekAPI;

public class AwaitStartActivity extends AppCompatActivity {
    private HideAndSeekAPI api;
    private Context c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_hideandseek_awaitstart);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        c = this;
        api = new HideAndSeekAPIFactory(getFilesDir()).generateHideAndSeekAPI();

        findViewById(R.id.await_refresh).setOnClickListener(v->runOnBackgroundThread(this::updateAppDisplay));

        runOnBackgroundThread(this::updateAppDisplay);
    }

    private void loadMeta() {
        runOnBackgroundThread(()-> {
            try {
                if(api.amIAdmin()) {
                    JSONObject hider = api.getCurrentHider();
                    String name = hider.getString("name");
                    runOnUiThread(()-> ((TextView)findViewById(R.id.await_admin_current_hider))
                            .setText(String.format(getResources().getString(R.string.await_admin_current_hider),name)));
                    runOnUiThread(()-> findViewById(R.id.await_admin_card).setVisibility(VISIBLE));
                    runOnUiThread(()-> {
                        findViewById(R.id.await_switch).setOnClickListener(this::switchHider);
                        findViewById(R.id.await_start).setOnClickListener(this::startGame);
                        findViewById(R.id.await_setup).setOnClickListener(v-> {
                            Intent i = new Intent(v.getContext(), ConfiguratorActivity.class);
                            startActivity(i);
                        });
                    });
                } else {
                    runOnUiThread(()-> findViewById(R.id.await_admin_card).setVisibility(GONE));
                }
            } catch (IOException | JSONException e) {
                Log.e(AwaitStartActivity.class.getName(),"Unable to connect to the server!",e);
            }
        });
    }

    private void startGame(View v) {
        runOnBackgroundThread(()-> {
            try {
                JSONObject hider = api.getCurrentHider();
                if(hider == null) {
                    runOnUiThread(()-> {
                        Toast.makeText(v.getContext(), "You have not selected a hider!", Toast.LENGTH_SHORT).show();
                    });
                    return;
                }

                if (api.startGame()) {
                    updateAppDisplay();
                } else {
                    runOnUiThread(()-> {
                        Toast.makeText(v.getContext(), "Unable to start this game!", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (IOException e) {
                Log.e(AwaitStartActivity.class.getName(),"Unable to start the game!",e);
            }
        });
    }

    private void switchHider(View v) {
        runOnBackgroundThread(()-> {
            try {
                if (!api.cycleHider()) {
                    Log.e(AwaitStartActivity.class.getName(),"Unable to cycle hider!");
                } else {
                    runOnBackgroundThread(this::updateAppDisplay);
                }
            } catch (IOException e) {
                Log.e(AwaitStartActivity.class.getName(),"Unable to cycle hider!",e);
                runOnUiThread(()->Toast.makeText(c, "Unable to cycle hider!", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void updateAppDisplay() {
        try {
            runOnUiThread(()-> {
                findViewById(R.id.await_refresh).setVisibility(GONE);
                findViewById(R.id.await_admin_card).setVisibility(GONE);
                ((TextView)findViewById(R.id.await_title)).setText(R.string.generic_loading);
                ((TextView)findViewById(R.id.await_subtitle)).setText(R.string.generic_loading);
            });
            GameState newState = api.getGameState();
            switch (newState) {
                case SETUP -> runOnUiThread(()-> {
                    ((TextView)findViewById(R.id.await_title)).setText(R.string.await_title_setup);
                    ((TextView)findViewById(R.id.await_subtitle)).setText(R.string.await_subtitle_setup);
                    runOnUiThread(()-> findViewById(R.id.await_refresh).setVisibility(VISIBLE));
                    runOnUiThread(()-> findViewById(R.id.await_admin_card).setVisibility(VISIBLE));
                    loadMeta();
                });
                case INGAME,MOVE_PERIOD -> {
                    Intent i = new Intent(c, MapView.class);
                    startActivity(i);
                    finish();
                }
                case OUTSIDE_OF_GAME -> {
                    ZonedDateTime datetime = api.getGameStartTime();
                    runOnUiThread(()-> findViewById(R.id.await_refresh).setVisibility(VISIBLE));
                }
            }
        } catch (IOException e) {
            Log.e(AwaitStartActivity.class.getName(),"Unable to get game state from the server!",e);
        }
    }
}