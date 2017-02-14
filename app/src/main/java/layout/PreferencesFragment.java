package layout;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import androidapp.uturn.view.R;

/**
 * Created by Rakesh on 1/31/2016
 */
public class PreferencesFragment extends PreferenceFragment {

    public PreferencesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.application_preferences);
    }
}
