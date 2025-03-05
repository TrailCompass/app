package space.itoncek.trailcompass.app.hideandseek.generic_ui;

import static com.google.common.hash.Hashing.sha512;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.nio.charset.Charset;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import space.itoncek.trailcompass.app.R;
import space.itoncek.trailcompass.app.hideandseek.api.HideAndSeekAPIFactory;
import space.itoncek.trailcompass.client.api.HideAndSeekAPI;
import space.itoncek.trailcompass.client.api.HideAndSeekConfig;
import space.itoncek.trailcompass.client.api.ServerValidity;

public class MainmenuHideandseekLogin extends Fragment {

    public MainmenuHideandseekLogin() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_mainmenu_hideandseek_login, container, false);

        HideAndSeekAPI api = new HideAndSeekAPIFactory(v.getContext().getFilesDir()).generateHideAndSeekAPI();
        HideAndSeekConfig config = api.getConfig();

        if (!config.base_url.isEmpty()) {
            ((EditText) v.findViewById(R.id.hideandseek_fragment_server_url)).setText(config.base_url);
        } else {
            ((EditText) v.findViewById(R.id.hideandseek_fragment_server_url)).setText("");
        }

        if (!config.username.isEmpty()) {
            ((EditText) v.findViewById(R.id.hideandseek_fragment_username)).setText(config.username);
        } else {
            ((EditText) v.findViewById(R.id.hideandseek_fragment_username)).setText("");
        }

        if (!config.password_hash.isEmpty()) {
            ((EditText) v.findViewById(R.id.hideandseek_fragment_password)).setHint("<not changed>");
            ((EditText) v.findViewById(R.id.hideandseek_fragment_password)).setText("");
        } else {
            ((EditText) v.findViewById(R.id.hideandseek_fragment_password)).setHint("Password");
            ((EditText) v.findViewById(R.id.hideandseek_fragment_password)).setText("");
        }


        TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                HideAndSeekConfig c = api.getConfig();

                requireActivity().runOnUiThread(() -> c.base_url = ((EditText) v.findViewById(R.id.hideandseek_fragment_server_url)).getText().toString());
                api.saveConfig(c);

                Thread t = new Thread(() -> {
                    AtomicReference<ServerValidity> serverValidity = new AtomicReference<>();

                    try {
                        serverValidity.set(api.checkValidity());
                    } catch (Exception e) {
                        serverValidity.set(ServerValidity.NOT_FOUND);
                    }
                    requireActivity().runOnUiThread(() -> {
                        int textID;
                        if (Objects.requireNonNull(serverValidity.get()) == ServerValidity.NOT_FOUND) {
                            textID = R.string.fragment_status_unknown;
                        } else if (serverValidity.get() == ServerValidity.INCOMPATIBLE_VERSION) {
                            textID = R.string.fragment_status_incompatible;
                        } else if (serverValidity.get() == ServerValidity.DEVELOPMENT_VERSION) {
                            textID = R.string.fragment_status_dev;
                        } else if (serverValidity.get() == ServerValidity.OK) {
                            textID = R.string.fragment_status_ok;
                        } else {
                            throw new IllegalArgumentException();
                        }
                        ((TextView) v.findViewById(R.id.hideandseek_fragment_status)).setText(textID);
                    });
                });

                t.start();

                try {
                    t.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        ((EditText) v.findViewById(R.id.hideandseek_fragment_server_url)).addTextChangedListener(tw);

        tw.afterTextChanged(((EditText) v.findViewById(R.id.hideandseek_fragment_server_url)).getText());

        v.findViewById(R.id.hideandseek_fragment_login).setOnClickListener(c -> {
            HideAndSeekConfig cfg = api.getConfig();
            cfg.username = ((EditText) v.findViewById(R.id.hideandseek_fragment_username)).getText().toString();
            String pwd = ((EditText) v.findViewById(R.id.hideandseek_fragment_password)).getText().toString();
            if (!pwd.isEmpty()) {
                cfg.password_hash = sha512().hashString(pwd, Charset.defaultCharset()).toString();
            }
            cfg.base_url = ((EditText) v.findViewById(R.id.hideandseek_fragment_server_url)).getText().toString();

            api.saveConfig(cfg);

            Intent myIntent = new Intent(v.getContext(), LoginActivity.class);
            v.getContext().startActivity(myIntent);
        });

        return v;
    }
}