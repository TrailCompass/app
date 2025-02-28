package space.itoncek.trailcompass.app.hideandseek.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;

import space.itoncek.trailcompass.app.R;
import space.itoncek.trailcompass.app.hideandseek.api.HideAndSeekAPIFactory;
import space.itoncek.trailcompass.client.api.HideAndSeekAPI;

public class AwaitStartActivity extends AppCompatActivity {

    private HideAndSeekAPI api;

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

        api = new HideAndSeekAPIFactory(getFilesDir()).generateHideAndSeekAPI();

        loadMeta();
    }

    private void loadMeta() {
        Thread t = new Thread(()->{
            try {
                if(api.amIAdmin()) {
                    String hider = api.getCurrentHider();
                    runOnUiThread(()-> ((TextView)findViewById(R.id.await_admin_current_hider))
                            .setText(String.format(getResources().getString(R.string.await_admin_current_hider),hider)));
                }
            } catch (IOException e) {
                Log.e(AwaitStartActivity.class.getName(),"Unable to connect to the server!",e);
            }
        });
        t.start();
    }
}