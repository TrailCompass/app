package space.itoncek.trailcompass.app.hideandseek.ui;

import static space.itoncek.trailcompass.client.api.LoginResponse.UNABLE_TO_CONNECT;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import space.itoncek.trailcompass.app.R;
import space.itoncek.trailcompass.app.hideandseek.api.HideAndSeekAPIFactory;
import space.itoncek.trailcompass.client.api.HideAndSeekAPI;
import space.itoncek.trailcompass.client.api.HideAndSeekConfig;
import space.itoncek.trailcompass.client.api.LoginResponse;

public class LoginActivity extends AppCompatActivity {
    ProgressBar mProgressBar;
    TextView mProgressLabel;
    HideAndSeekAPI api;

    File mapfile;
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
        mapfile = new File(getCacheDir() + "/map.map");

        mProgressBar = findViewById(R.id.loading_progressbar);
        mProgressLabel = findViewById(R.id.loading_label);

        api = new HideAndSeekAPIFactory(getFilesDir()).generateHideAndSeekAPI();

        CountDownLatch cdl = new CountDownLatch(1);
        AtomicReference<LoginResponse> login = new AtomicReference<>();
        Thread t = new Thread(() -> {
            runOnUiThread(()->mProgressLabel.setText(String.format(getString(R.string.login_started) , api.getConfig().username)));
            try {
                login.set(api.login());
            } catch (IOException | JSONException e) {
                Log.e(LoginActivity.class.getName(), getResources().getText(R.string.login_unable_to_connect).toString(), e);
                Toast.makeText(this, getResources().getText(R.string.login_unable_to_connect), Toast.LENGTH_LONG).show();
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
                runOnUiThread(()->mProgressLabel.setText(R.string.login_unable_to_connect));
                Toast.makeText(this, getResources().getText(R.string.login_unable_to_connect), Toast.LENGTH_LONG).show();
                this.finish();
            }
            case INCOMPATIBLE_BACKEND -> {
                runOnUiThread(()->mProgressLabel.setText(R.string.login_wrong_version));
                Toast.makeText(this, getResources().getText(R.string.login_wrong_version), Toast.LENGTH_LONG).show();
                this.finish();
            }
            case UNABLE_TO_AUTH -> {
                runOnUiThread(()->mProgressLabel.setText(R.string.login_wrong_password));
                Toast.makeText(this, getResources().getText(R.string.login_wrong_password), Toast.LENGTH_LONG).show();
                this.finish();
            }
            case OK -> {
                runOnUiThread(()->mProgressLabel.setText(String.format(getString(R.string.login_succes),api.getConfig().username)));
                downloadMapUpdates();
            }
        }

    }

    private void downloadMapUpdates() {
        // execute this when the downloader must be fired
        Thread t = new Thread(() -> {
            AtomicBoolean isCanceled = new AtomicBoolean(false);
            findViewById(R.id.loading_cancel).setOnClickListener((c) -> isCanceled.set(true));
            try {
                HideAndSeekConfig c = api.getConfig();

                runOnUiThread(()->mProgressLabel.setText(R.string.login_checking_map_hash));
                String str = api.getMapHash();

                if (!str.equals(c.map_hash) || !mapfile.exists()) {
                    c.map_hash = str;
                    api.saveConfig(c);
                    runOnUiThread(()->mProgressLabel.setText(R.string.login_map_update_needed));
                    InputStream input = null;
                    OutputStream output = null;
                    HttpURLConnection connection = null;
                    try {
                        URL url = new URL(c.base_url + "/mapserver/getServerMap");
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestProperty("Authorization","Bearer " + c.jwt_token);
                        connection.connect();

                        // expect HTTP 200 OK, so we don't mistakenly save error report
                        // instead of the file
                        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                            throw new RuntimeException(new IOException("Server returned HTTP " + connection.getResponseCode()
                                    + " " + connection.getResponseMessage()));
                        }

                        // this will be useful to display download percentage
                        // might be -1: server did not report the length
                        int fileLength = connection.getContentLength();
                        Log.i("LoginActivity", "FL " + fileLength);
                        // download the file
                        input = connection.getInputStream();
                        output = new FileOutputStream(mapfile);

                        byte[] data = new byte[65535];
                        long total = 0;
                        int count;
                        while ((count = input.read(data)) != -1) {
                            // allow canceling with back button
                            if (isCanceled.get()) {
                                input.close();
                            }
                            total += count;
                            // publishing the progress....
                            if (fileLength > 0) // only if total length is known
                            {
                                int finalTotal = (int) total;
                                runOnUiThread(()-> {
                                    mProgressBar.setIndeterminate(false);
                                    mProgressBar.setProgress((int) (100*((float)finalTotal/fileLength)),true);
                                    mProgressBar.setMax(100);

                                    mProgressLabel.setText(MessageFormat.format(getString(R.string.login_map_download_progress), (int) (100 * ((float) finalTotal / fileLength))));
                                });
                            }
                            output.write(data, 0, count);
                        }
                        Log.i("LoginActivity","total "  + total);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    } finally {
                        try {
                            if (output != null)
                                output.close();
                            if (input != null)
                                input.close();
                        } catch (IOException ignored) {
                        }

                        if (connection != null)
                            connection.disconnect();
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            runOnUiThread(this::loaded);
        });

        t.start();
    }

    private void loaded() {
        Intent intent = new Intent(this, AwaitStartActivity.class);
        startActivity(intent);
        finish();
    }
}