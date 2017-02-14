package layout;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.view.LayoutInflater;
import android.view.inputmethod.InputMethodManager;

import java.util.HashMap;

import androidapp.uturn.view.R;
import androidapp.uturn.helper.PlacesTask;
import androidapp.uturn.helper.CustomSearchLocationView;


/**
 * Created by Rakesh on 2/13/2016
 */
public class LocationFragment extends Fragment {

    private int index;
    private Context context;
    private String locationName = null;
    private String mStrLocationFragmentTag;
    private RemoveLocationEventListener mListener;

    private CustomSearchLocationView custSearchLocationView;

    public LocationFragment() {}

    public LocationFragment(int index, Context context) {

        this.index = index;
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_addlocation, container, false);

        custSearchLocationView = (CustomSearchLocationView) fragmentView.findViewById(R.id.atvLocationX);
        custSearchLocationView.setThreshold(1);
        setTextChangeListener();
        setItemClickListener();

        System.out.println("ADDED: " + custSearchLocationView);

        // Initialize the variables and associate the onClick event listener with remove location button
        ImageButton btnRemoveLocation = (ImageButton) fragmentView.findViewById(R.id.btnRemoveLocation);
        btnRemoveLocation.setOnClickListener(new View.OnClickListener() {

            // Event that invokes the method on the parent activity to remove the fragment
            @Override
            public void onClick(View view) {
                mListener.onRemoveLocationEvent(mStrLocationFragmentTag);
            }
        });

        return fragmentView;
    }

    private void setTextChangeListener() {

        custSearchLocationView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                PlacesTask placesTask = new PlacesTask(index, custSearchLocationView);
                placesTask.execute(s.toString(), context);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setItemClickListener() {

        custSearchLocationView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                HashMap<String, String> map = (HashMap<String, String>) parent.getItemAtPosition(position);
                locationName = map.get("description");
                locationName = locationName.replaceAll(" ", "+");
                InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(custSearchLocationView.getWindowToken(), 0);
            }
        });
    }

    public void setFragmentTag(String strFragmentId) {

        mStrLocationFragmentTag = strFragmentId;
    }

    public String getLocationName() {

        return locationName;
    }

    // Callback interface for the activity to remove fragments on minus button click
    public interface RemoveLocationEventListener {
        void onRemoveLocationEvent(String strLocationFragmentId);
    }

    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);

        // Get the parent activity reference
        mListener = (RemoveLocationEventListener) activity;
    }
}
