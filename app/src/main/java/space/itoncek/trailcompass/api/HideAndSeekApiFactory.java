package space.itoncek.trailcompass.api;

import android.util.Log;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import space.itoncek.trailcompass.client.api.HideAndSeekAPI;
import space.itoncek.trailcompass.client.api.HideAndSeekConfig;

public class HideAndSeekApiFactory {
    private final File filesDir;

    public HideAndSeekApiFactory(File filesDir) {
        this.filesDir = filesDir;
    }
    public HideAndSeekAPI generateApi() {
        return new HideAndSeekAPI() {
            @Override
            public void sendLogMessage(String message) {
                Log.d("HideAndSeekApi",message);
            }

            @Override
            public void sendExceptionMessage(String message, Throwable t) {
                Log.w("HideAndSeekApi",message,t);
            }

            @Override
            public @Nullable HideAndSeekConfig getConfig() {
                File cfile = new File(filesDir + "/config.dat");
                if(cfile.exists()) {
                    try {
                        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cfile));
                        Object o = ois.readObject();
                        if (!(o instanceof HideAndSeekConfig cfg)) {
                            Log.e("HideAndSeekConfigFactory", "Unable to find HideAndSeekConfig object in config!");
                            return null;
                        }
                        ois.close();
                        return cfg;
                    } catch (IOException | ClassNotFoundException e) {
                        Log.e("HideAndSeekConfigFactory", "Unable to read config!", e);
                        //noinspection ResultOfMethodCallIgnored
                        cfile.delete();
                        return HideAndSeekConfig.generateEmptyConfig();
                    }
                } else return HideAndSeekConfig.generateEmptyConfig();
            }

            @Override
            public void saveConfig(HideAndSeekConfig cfg) {
                File cfile = new File(filesDir + "/config.dat");
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(cfile));
                    oos.writeObject(cfg);
                    oos.close();
                } catch (IOException e) {
                    Log.e("HideAndSeekConfigFactory", "Unable to save config!",e);
                }
            }
        };
    }
}
