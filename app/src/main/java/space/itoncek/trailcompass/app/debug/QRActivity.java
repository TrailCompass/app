package space.itoncek.trailcompass.app.debug;

import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import java.util.Arrays;

import de.markusfisch.android.barcodescannerview.widget.BarcodeScannerView;
import de.markusfisch.android.zxingcpp.ZxingCpp;
import space.itoncek.trailcompass.app.R;

public class QRActivity extends AppCompatActivity {

    private BarcodeScannerView scannerView;
    String last = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        controller.hide(WindowInsetsCompat.Type.systemBars());
        setContentView(R.layout.activity_debug_qr);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        scannerView = findViewById(R.id.barcode_scanner);
        scannerView.setCropRatio(.75f);
        scannerView.setShowOverlay(true);
        scannerView.formats.clear();
        scannerView.formats.addAll(Arrays.asList(ZxingCpp.BarcodeFormat.values()));
        scannerView.setOnBarcodeListener(result -> {
            if(!result.getText().equals(last)) {
                // This listener is called from the Camera thread.
                runOnUiThread(()-> Toast.makeText(this, result.getText(), Toast.LENGTH_SHORT).show());
                last = result.getText();
            }
            // Return true to keep scanning for barcodes.
            return true;
        });
        scannerView.openAsync();
    }

    @Override
    public void onResume() {
        super.onResume();
        scannerView.openAsync();
    }

    @Override
    public void onPause() {
        super.onPause();
        scannerView.close();
    }
}