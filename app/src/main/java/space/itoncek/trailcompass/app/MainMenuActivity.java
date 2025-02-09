package space.itoncek.trailcompass.app;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setAppVersionTitle();
        setupGameDropdown();
    }

    private void setupGameDropdown() {
        String[] gamemodes = getResources().getStringArray(R.array.gamemodes);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.activity_mainmenu_dropdown_item, gamemodes);
        ((AutoCompleteTextView)findViewById(R.id.autoCompleteTextView)).setAdapter(arrayAdapter);
    }

    private void setAppVersionTitle() {
        try {
            PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
            String version = pInfo.versionName;

            ((TextView) findViewById(R.id.versionText)).setText(String.format(getResources().getText(R.string.version_known_prefix).toString(),version));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("MainMenuActivity.setAppVersionTitle()", "Unable to find app version!",e);
            ((TextView) findViewById(R.id.versionText)).setText(R.string.version_unknown);
        }
    }
}