package space.itoncek.trailcompass.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.map_button).setOnClickListener((e)-> {
            Intent myIntent = new Intent(e.getContext(), MapActivity.class);
            e.getContext().startActivity(myIntent);
        });
        findViewById(R.id.nfc_test).setOnClickListener((e)-> {
            Intent myIntent = new Intent(e.getContext(), QRActivity.class);
            e.getContext().startActivity(myIntent);
        });
        findViewById(R.id.notif_test).setOnClickListener((e)-> {
            Intent myIntent = new Intent(e.getContext(), NotificationActivity.class);
            e.getContext().startActivity(myIntent);
        });
    }
}