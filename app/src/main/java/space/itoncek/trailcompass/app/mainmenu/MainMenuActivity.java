package space.itoncek.trailcompass.app.mainmenu;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import space.itoncek.trailcompass.app.R;
import space.itoncek.trailcompass.app.debug.MainmenuDebug;
import space.itoncek.trailcompass.app.hideandseek.generic_ui.MainmenuHideandseekLogin;

public class MainMenuActivity extends AppCompatActivity {

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_menu);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setAppVersionTitle();
        setupGameDropdown();
    }

    private void setupGameDropdown() {
        String[] gamemodes = new String[] {
                "Hide and seek",
                "Debug"
        };
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.activity_mainmenu_dropdown_item, gamemodes);
        ((Spinner) findViewById(R.id.spinner)).setAdapter(arrayAdapter);
        ((Spinner) findViewById(R.id.spinner)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("MainMenu","i="+i);
                Log.i("MainMenu","l="+l);
                Fragment f;
                if(l == 0) {
                    f = new MainmenuHideandseekLogin();
                } else if (l == 1) {
                    f = new MainmenuDebug();
                } else return;

                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.initFragment, f.getClass(), null)
                        .commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.i("MainMenu", "nothing");
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .remove(((FragmentContainerView)findViewById(R.id.initFragment)).getFragment())
                        .commit();
            }
        });

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