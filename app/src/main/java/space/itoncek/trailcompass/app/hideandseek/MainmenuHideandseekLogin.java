package space.itoncek.trailcompass.app.hideandseek;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import space.itoncek.trailcompass.app.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainmenuHideandseekLogin#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainmenuHideandseekLogin extends Fragment {

    public MainmenuHideandseekLogin() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainmenuHideandseekLogin.
     */
    // TODO: Rename and change types and number of parameters
    public static MainmenuHideandseekLogin newInstance(String param1, String param2) {
        MainmenuHideandseekLogin fragment = new MainmenuHideandseekLogin();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_mainmenu_hideandseek_login, container, false);

        return inflate;
    }
}