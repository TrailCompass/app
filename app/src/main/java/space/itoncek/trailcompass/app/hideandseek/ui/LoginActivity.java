package space.itoncek.trailcompass.app.hideandseek.ui;

import static space.itoncek.trailcompass.client.api.LoginResponse.INCOMPATIBLE_BACKEND;
import static space.itoncek.trailcompass.client.api.LoginResponse.OK;
import static space.itoncek.trailcompass.client.api.LoginResponse.UNABLE_TO_AUTH;
import static space.itoncek.trailcompass.client.api.LoginResponse.UNABLE_TO_CONNECT;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import space.itoncek.trailcompass.app.R;
import space.itoncek.trailcompass.app.hideandseek.api.HideAndSeekAPIFactory;
import space.itoncek.trailcompass.client.api.HideAndSeekAPI;
import space.itoncek.trailcompass.client.api.LoginResponse;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_hideandseek_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        HideAndSeekAPI api = new HideAndSeekAPIFactory(getFilesDir()).generateHideAndSeekAPI();

        CountDownLatch cdl = new CountDownLatch(1);
        AtomicReference<LoginResponse> login = new AtomicReference<>();
        Thread t = new Thread(() -> {
            try {
                login.set(api.login());
            } catch (IOException e) {
                Log.e(LoginActivity.class.getName(), "Unable to connect to the server!", e);
                Toast.makeText(this, "Unable to connect to the server!", Toast.LENGTH_LONG).show();
                login.set(UNABLE_TO_CONNECT);
            }
            cdl.countDown();
        });
        t.start();

        try {
            cdl.await();
        } catch (InterruptedException e) {
            Log.d(LoginActivity.class.getName(), "Interrupted", e);
        }

        Toast.makeText(this, login.get().name(), Toast.LENGTH_SHORT).show();
        switch (login.get()) {
            case UNABLE_TO_CONNECT -> {
                Toast.makeText(this, "Unable to connect to the server!", Toast.LENGTH_LONG).show();
                this.finish();
            }
            case INCOMPATIBLE_BACKEND -> {
                Toast.makeText(this, "Server is on a different version!", Toast.LENGTH_LONG).show();
                this.finish();
            }
            case UNABLE_TO_AUTH -> {
                Toast.makeText(this, "Unable to login, check username and password!", Toast.LENGTH_LONG).show();
                this.finish();
            }
            case OK -> {
                Toast.makeText(this, "Logged in!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MapView.class);
                startActivity(intent);
                finish();
            }
        }

    }
}