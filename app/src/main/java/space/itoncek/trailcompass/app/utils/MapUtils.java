package space.itoncek.trailcompass.app.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Toast;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.Point;
import org.mapsforge.map.android.graphics.AndroidBitmap;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.overlay.Marker;

public class MapUtils {
    /**
     * Compatibility method.
     *
     * @param a the current activity
     */
    public static void enableHome(Activity a) {
        // Show the Up button in the action bar.
        a.getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Compatibility method.
     *
     * @param view       the view to set the background on
     * @param background the background
     */
    public static void setBackground(View view, Drawable background) {
        view.setBackground(background);
    }

    public static Marker createMarker(Context c, int resourceIdentifier, LatLong latLong) {
        Bitmap bitmap = new AndroidBitmap(BitmapFactory.decodeResource(c.getResources(), resourceIdentifier));
        return new Marker(latLong, bitmap, 0, -bitmap.getHeight() / 2);
    }

    public static Paint createPaint(int color, int strokeWidth, Style style) {
        Paint paint = AndroidGraphicFactory.INSTANCE.createPaint();
        paint.setColor(color);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(style);
        return paint;
    }

    public static Marker createTappableMarker(Context c, int resourceIdentifier, LatLong latLong, MapView mapView) {
        Bitmap bitmap = new AndroidBitmap(BitmapFactory.decodeResource(c.getResources(), resourceIdentifier));
        bitmap.incrementRefCount();
        return new Marker(latLong, bitmap, 0, -bitmap.getHeight() / 2) {
            @Override
            public boolean onLongPress(LatLong tapLatLong, Point layerXY, Point tapXY) {
                if (contains(layerXY, tapXY, mapView)) {
                    Toast.makeText(c, "Marker long press\n" + tapLatLong, Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }

            @Override
            public boolean onTap(LatLong tapLatLong, Point layerXY, Point tapXY) {
                if (contains(layerXY, tapXY, mapView)) {
                    Toast.makeText(c, "Marker tap\n" + tapLatLong, Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        };
    }

    @SuppressWarnings("deprecation")
    public static Bitmap viewToBitmap(Context c, View view) {
        view.measure(View.MeasureSpec.getSize(view.getMeasuredWidth()),
                View.MeasureSpec.getSize(view.getMeasuredHeight()));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setDrawingCacheEnabled(true);
        Drawable drawable = new BitmapDrawable(c.getResources(),
                android.graphics.Bitmap.createBitmap(view.getDrawingCache()));
        view.setDrawingCacheEnabled(false);
        return AndroidGraphicFactory.convertToBitmap(drawable);
    }

    private MapUtils() {
        throw new IllegalStateException();
    }
}
