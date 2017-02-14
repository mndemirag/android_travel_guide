package androidapp.uturn.view;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import layout.AboutUsFragment;
import layout.ConnectionsFragment;
import layout.PreferencesFragment;
import layout.TripsHistoryFragment;


public class SliderOptionsManagerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider_option_manager);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {

            int fragID = extras.getInt("disp_fragment");
            displayFragment(fragID);
        }
    }

    private void displayFragment(int fragmentID) {

        switch (fragmentID) {

            case R.id.about_us:
                displayAboutusFragment();
                break;

            case R.id.connect:
                displayConnectionsFragment();
                break;

            case R.id.history:
                displayHistoryFragment();
                break;

            case R.id.preferences:
                displayPreferencesFragment();
                break;

            default:
                break;
        }
    }

    private void displayAboutusFragment() {

        AboutUsFragment frag = new AboutUsFragment();
        FragmentManager fragManager = getFragmentManager();
        fragManager.beginTransaction().replace(R.id.fragmentHolder, frag).commit();
    }

    private void displayConnectionsFragment() {

        ConnectionsFragment frag = new ConnectionsFragment();
        FragmentManager fragManager = getFragmentManager();
        fragManager.beginTransaction().replace(R.id.fragmentHolder, frag).commit();
    }

    private void displayHistoryFragment() {

        TripsHistoryFragment frag = new TripsHistoryFragment();
        FragmentManager fragManager = getFragmentManager();
        fragManager.beginTransaction().replace(R.id.fragmentHolder, frag).commit();
    }

    private void displayPreferencesFragment() {

        // Replace the screen content with the about us page
        Fragment fragment = new PreferencesFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragmentHolder, fragment).commit();
    }
}
