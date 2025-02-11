package space.itoncek.trailcompass.app.debug;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import space.itoncek.trailcompass.app.R;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class MainmenuDebug extends Fragment {

    public MainmenuDebug() {
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
        View v = inflater.inflate(R.layout.fragment_mainmenu_debug, container, false);

        v.findViewById(R.id.debug_fragment_enter).setOnClickListener(c-> {
            Intent myIntent = new Intent(v.getContext(), TestActivity.class);
            v.getContext().startActivity(myIntent);
        });
        return v;
    }
}