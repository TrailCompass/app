package space.itoncek.trailcompass.app.debug;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

import space.itoncek.trailcompass.app.R;

public class NotificationActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "main";

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Toast.makeText(this, "Permission has been granted!", Toast.LENGTH_SHORT).show();
                    createNotificationChannel();
                } else {
                    Intent myIntent = new Intent(this.getBaseContext(), TestActivity.class);
                    this.getBaseContext().startActivity(myIntent);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_debug_notification);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        checkPerms();
    }

    private void checkPerms() {
        if (ContextCompat.checkSelfPermission(
                getBaseContext(), Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
            createNotificationChannel();
        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        channel.enableLights(true);
        channel.setLightColor(Color.valueOf(0,0,128).toArgb());
        // Register the channel with the system. You can't change the importance
        // or other notification behaviors after this.
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        runOnUiThread(()-> {
            findViewById(R.id.button).setOnClickListener((v)-> {
                NotificationCompat.Builder notif = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.icon_foreground)
                        .setContentTitle("TestNotification")
                        .setContentText("TestNotificationContent")
                        .setLights(Color.argb(255,0,0,255),200,200)
                        .setColor(Color.rgb(13,28,54))
                        .setColorized(true)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                    // notificationId is a unique int for each notification that you must define.
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    checkPerms();
                    return;
                }
                NotificationManagerCompat.from(this).notify(new Random().nextInt(), notif.build());

            });
        });
    }
}