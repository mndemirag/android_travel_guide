package androidapp.uturn.view;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.content.Intent;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import androidapp.uturn.helper.CustomSearchLocationView;
import androidapp.uturn.helper.SearchInputs;
import androidapp.uturn.helper.PlacesTask;
import layout.LocationFragment;

public class HomeScreenActivity extends NavigationSliderActivity
                implements LocationFragment.RemoveLocationEventListener {

    private static int m_nFragmentsCounter = 0;
    private static ArrayList<String> m_addenLocationFragmentsList = new ArrayList<>();

    // Modes drop-down list control
    private Spinner m_commuteModeDropdownList;

    // Auto-list places (from Google) control
    CustomSearchLocationView autoCompleteTextView_start;
    CustomSearchLocationView autoCompleteTextView_end;

    // Identifies if the user selected the Round trip or the Straight trip
    private boolean m_bRoundTrip;
    private String m_startLocation = "";
    private String m_requiredLocation = "";

    private PlacesTask placesTask;
    private SearchInputs.CommuteMode m_commuteMode;

    List<AutoCompleteTextView> autoCompleteTextViewList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        /**
         * Adding our layout to parent class frame layout.
         */
        getLayoutInflater().inflate(R.layout.activity_home_screen, mContentLayout);

        // Initialize variables and associate necessary listeners for each
        autoCompleteTextView_start = (CustomSearchLocationView) findViewById(R.id.atvStartingPoint);
        autoCompleteTextView_start.setThreshold(1);

        autoCompleteTextView_end = (CustomSearchLocationView) findViewById(R.id.atvRequiredDestination);
        autoCompleteTextView_end.setThreshold(1);

        setTextListenerForAutoCompleteSearchLocationControls();
        setItemClickListenerForAutoCompleteSearchLocationControls();

        autoCompleteTextViewList.add(autoCompleteTextView_start);
        autoCompleteTextViewList.add(autoCompleteTextView_end);

        m_commuteModeDropdownList = (Spinner) findViewById(R.id.commuteModeDropdownList);

        // Set the data and selected item change listener to the spinner
        setCommuteModes();
        setModeChangeListener();

        // Populate the user mail ID and the display name
        Bundle extras = getIntent().getExtras();
        if(extras != null) {

            m_strUserMailID = extras.getString(RegistrationScreenActivity.KEY_MAIL_ID);
            m_strUserDispName = extras.getString(RegistrationScreenActivity.KEY_NAME);
        }

        Switch switchButton = (Switch)findViewById(R.id.currentLocationSwitch);
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    System.out.println("CHECKED");
                    autoCompleteTextView_start.setText(R.string.strCurrentLocationConstant);
                    m_startLocation = "CURRENT_LOCATION";
                }
                else{
                    System.out.println("UNCHECKED");
                    autoCompleteTextView_start.setText("");
                    m_startLocation = "";
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.saveRouteButton) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setTextListenerForAutoCompleteSearchLocationControls() {

        // Set the listener fot starting point search control
        autoCompleteTextView_start.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                placesTask = new PlacesTask(0, autoCompleteTextView_start);
                placesTask.execute(s.toString(), getBaseContext());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Set the listener for the Finish / Required destination control
        autoCompleteTextView_end.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                placesTask = new PlacesTask(1, autoCompleteTextView_end);
                placesTask.execute(s.toString(), getBaseContext());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setItemClickListenerForAutoCompleteSearchLocationControls() {

        // Set the item click listener for the starting point search location control
        autoCompleteTextView_start.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> map = (HashMap<String, String>) parent.getItemAtPosition(position);
                m_startLocation = map.get("description");
                m_startLocation = m_startLocation.replaceAll(" ", "+");
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(autoCompleteTextView_start.getWindowToken(), 0);
            }
        });

        // Set the item click listener for the finish/required destination search location control
        autoCompleteTextView_end.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> map = (HashMap<String, String>) parent.getItemAtPosition(position);
                m_requiredLocation = map.get("description");
                m_requiredLocation = m_requiredLocation.replaceAll(" ", "+");
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(autoCompleteTextView_end.getWindowToken(), 0);
            }
        });
    }

    private String getLocationFragmentTag() {

        String strLocationFragmentTag = "Location";
        strLocationFragmentTag = strLocationFragmentTag + (m_nFragmentsCounter + 1);

        return strLocationFragmentTag;
    }

    // Handles the radio button click events
    public void onTripTypeChanged(View view) {

        // Check which type of trip is chosen by the user
        boolean bIsSelected = ((RadioButton) view).isChecked();
        switch (view.getId()) {

            case R.id.rbRoundTrip:
                if (bIsSelected) {
                    m_bRoundTrip = true;
                }
                break;

            case R.id.rbAtoBTrip:
                if (bIsSelected) {
                    m_bRoundTrip = false;
                }
                break;
        }
    }

    private void setCommuteModes() {

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.arrCommuteOptions,
                                                                                android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        m_commuteModeDropdownList.setAdapter(adapter);
    }

    private void setModeChangeListener() {

        m_commuteModeDropdownList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int nItemPosition, long id) {

                switch (nItemPosition) {

                    // By Walk
                    case 0:
                        m_commuteMode = SearchInputs.CommuteMode.WALK;
                        break;

                    // By Bike
                    case 1:
                        m_commuteMode = SearchInputs.CommuteMode.BIKE;
                        break;

                    // By Car/Private driving
                    case 2:
                        m_commuteMode = SearchInputs.CommuteMode.CAR;
                        break;

                    // By Public Transport
                    case 3:
                        m_commuteMode = SearchInputs.CommuteMode.PUBLIC_TRANSIT;
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // Include fragment for adding a new location on clicking on the Plus button
    public void addLocation(View view) {

        // Create a new fragment and add it to the screen
        LocationFragment newLocationFragment = new LocationFragment( (m_nFragmentsCounter + 1), getBaseContext());

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        // Append the fragment Id for identification and usage later
        String strLocFragmentTag = getLocationFragmentTag();
        ft.add(R.id.addedLocationsContainerLayout, newLocationFragment, strLocFragmentTag);
        newLocationFragment.setFragmentTag(strLocFragmentTag);

        // Add it to the stack and commit to see the changes
        ft.addToBackStack(null);
        ft.commit();

        //  Increment the added fragments count and store the Id and the corresponding fragment object
        m_nFragmentsCounter++;
        m_addenLocationFragmentsList.add(strLocFragmentTag);
    }

    // Once the minus button is clicked in the location fragment,
    // the corresponding fragment shall be removed from the activity
    public void onRemoveLocationEvent(String strLocationFragmentTag) {

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        // Get the reference to appropriate fragment, remove it and commit the changes
        Fragment frag = fm.findFragmentByTag(strLocationFragmentTag);
        if (frag != null) {
            ft.remove(frag);
        }
        ft.commit();

        // Do not decrement here as it leads to tag clashes
        // Just remove the corresponding fragment object from the stored list
        for (int i = 0; i < m_addenLocationFragmentsList.size(); i++) {

            String strTempTag = m_addenLocationFragmentsList.get(i);
            if (Objects.equals(strTempTag, strLocationFragmentTag)) {
                m_addenLocationFragmentsList.remove(i);
                break;
            }
        }
    }

    public void resetFields(View view) {

        // Clear all the selections made of the user

        // Remove all the locations added by the user
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        for (int nIndex = 0; nIndex < fm.getBackStackEntryCount(); nIndex++) {
            int nFragId = fm.getBackStackEntryAt(nIndex).getId();
            fm.popBackStack(nFragId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        ft.commit();

        // Reset the counter and also the fragment object store list
        m_nFragmentsCounter = 0;
        m_addenLocationFragmentsList.clear();

        // Reset the trip type to Round Trip
        RadioButton rbRndTrip = (RadioButton) findViewById(R.id.rbRoundTrip);
        rbRndTrip.setChecked(true);
    }

    public void searchRoute(View view) {

        // Add a loading label
        TextView textView = (TextView)findViewById(R.id.tvLoadingText);
        textView.setText(R.string.strLoadingLabel);

        // Get all the locations
        ArrayList<String> arrDestinations = new ArrayList<>();
        if( ! m_startLocation.equals("") ) {
            arrDestinations.add(m_startLocation);
        }

        // Get the other intermediary destinations
        FragmentManager fm = getFragmentManager();
        int nFragments = m_addenLocationFragmentsList.size();
        for (int nIndex = 0; nIndex < nFragments; nIndex++) {

            LocationFragment frag = (LocationFragment) fm.findFragmentByTag(m_addenLocationFragmentsList.get(nIndex));
            if (frag != null) {
                String strLocation = frag.getLocationName();
                System.out.println(strLocation);
                if (!strLocation.equals("")) {
                    arrDestinations.add(strLocation);
                }
            } else {
                Toast.makeText(getApplicationContext(), "Fragment object is null", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Add the required destination/Finish point (in case of straight trip) at the end
        if( ! m_requiredLocation.equals("") ) {
            arrDestinations.add(m_requiredLocation);
        }

        // Check if all the inputs are supplied by the user, if not notify the user of empty locations
        boolean bEmptyChecksSuccess = false;
        if(arrDestinations.size() == (nFragments + 2)) {
            bEmptyChecksSuccess = true;
        }

        if (bEmptyChecksSuccess) {

            // Fill in all the data and send it to the map activity
            // where it will be supplied to the Google Map API to get the distance and time values.
            // Later, the time and distance info shall be used to trace the efficient and optimal route
            SearchInputs objSearchInputs = new SearchInputs();
            objSearchInputs.m_bRoundTrip = m_bRoundTrip;
            objSearchInputs.m_commuteMode = m_commuteMode;
            objSearchInputs.m_strStartingPoint = m_startLocation;
            objSearchInputs.m_strFinishPoint = m_requiredLocation;
            objSearchInputs.m_DestinationList.addAll(arrDestinations);

            Intent intent = new Intent(HomeScreenActivity.this, MapScreenActivity.class);
            intent.putExtra(RegistrationScreenActivity.KEY_MAIL_ID, m_strUserMailID);
            intent.putExtra(RegistrationScreenActivity.KEY_NAME, m_strUserDispName);
            intent.putExtra("user_search_inputs", objSearchInputs);

            HomeScreenActivity.this.startActivity(intent);

        } else {

            Toast.makeText(getApplicationContext(), "Some locations are empty", Toast.LENGTH_LONG).show();
        }
    }
}
