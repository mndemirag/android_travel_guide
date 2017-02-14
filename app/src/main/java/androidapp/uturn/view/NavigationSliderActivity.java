package androidapp.uturn.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.support.v7.widget.Toolbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBarDrawerToggle;

import androidapp.uturn.model.AccountInformation;
import androidapp.uturn.model.DatabaseHandler;


/**
 * Created by Rakesh on 2/5/2016.
 *
 * This activity is responsible for the Navigation Slider menu creation. This activity shall be extended
 * by other activities so that every activity shall have the Navigation Slider in it.
 */
public class NavigationSliderActivity extends AppCompatActivity {

    // Variables that hold references to the UI elements
    protected Toolbar mToolbar;
    protected NavigationView mNavSliderMenuView;
    protected DrawerLayout mNavSliderLayout;
    protected ActionBarDrawerToggle actionbarSliderToggle;

    // Variables to store email and name of the user
    protected String m_strUserMailID;
    protected String m_strUserDispName;

    SharedPreferences m_sharedPreferences;
    SharedPreferences.OnSharedPreferenceChangeListener m_sharedPrefChangeListener;

    /**
     *  Frame layout: parent layout for the child activity (Content on other activities).
     *  Most variables are protected for access in the child activity
     */
    protected FrameLayout mContentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Associate the class with the layout XML
        setContentView(R.layout.activity_navigation_slider);

        // Initializing Toolbar and setting it as the actionbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initializing the variables
        mNavSliderLayout = (DrawerLayout) findViewById(R.id.nav_slider_layout);
        mContentLayout = (FrameLayout)findViewById(R.id.mainContainerLayout);
        mNavSliderMenuView = (NavigationView) findViewById(R.id.nav_slider_menuView);

        // Setting Navigation View Item Selected Listener to handle the item click of the navigation menu option
        registerSliderOptionSelectionListener();

        // Initializing ActionBar button toggle, associate events and listeners
        initializeActionBarSliderToggleButton();

        // Initialise Shared Preferences and associate the change listener to it
        m_sharedPreferences = getSharedPreferences(RegistrationScreenActivity.PREFERENCES_FILENAME, MODE_PRIVATE);
        registerPreferenceListener();
    }

    private void registerSliderOptionSelectionListener() {

        mNavSliderMenuView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                // Checking if the menu option is in checked state or not, if not check it
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }

                // As the option is already chosen, close/hide the slider
                mNavSliderLayout.closeDrawers();

                // Determine which option is selected and replace the screen content with that fragment
                return openActivity(menuItem.getItemId());
            }
        });
    }

    private void initializeActionBarSliderToggleButton() {

        actionbarSliderToggle = new ActionBarDrawerToggle(this, mNavSliderLayout, mToolbar,
                                                            R.string.slider_open, R.string.slider_close){

            // Code here will be triggered once the slider closes
            @Override
            public void onDrawerClosed(View drawerView) {

                getSupportActionBar().setTitle(R.string.app_name);
                super.onDrawerClosed(drawerView);
            }

            // Code here will be triggered once the slider opens
            @Override
            public void onDrawerOpened(View drawerView) {

                getSupportActionBar().setTitle(R.string.app_name);
                super.onDrawerOpened(drawerView);
            }
        };

        // Setting the actionbarToggle to slider layout
        mNavSliderLayout.setDrawerListener(actionbarSliderToggle);

        // Syncing the state for proper display of the hamburger icon
        actionbarSliderToggle.syncState();
    }

    private void registerPreferenceListener() {

        m_sharedPrefChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {

                DatabaseHandler dbHandler = new DatabaseHandler(getApplicationContext());
                switch(key) {

                    case "user_display_name":
                        // Change the corresponding display name in the database
                        AccountInformation accInfo = new AccountInformation();
                        accInfo.setEmail(m_strUserMailID);
                        accInfo.setName(prefs.getString(key, null));
                        dbHandler.updateDispName(accInfo);

                        // Also, change the display name in the application
                        TextView m_tvDisplayName = (TextView) findViewById(R.id.tvDispName);
                        m_tvDisplayName.setText(m_strUserDispName);
                        break;

                    case "clear_history":
                        // Clear the data in the database
                    break;

                    case "application_updates":
                        // Look for application updates
                        break;

                    default:
                        break;
                }
            }
        };

        m_sharedPreferences.registerOnSharedPreferenceChangeListener(m_sharedPrefChangeListener);
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

    // Called whenever invalidateOptionsMenu() is called
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

         return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Replaces the main screen content with the corresponding fragment when any slider menu option is clicked.
     *
     * @param optionId - Indicates the position of the selected option in the slider menu
     */
    protected boolean openActivity(int optionId) {

        switch (optionId) {

            case R.id.about_us:
                Toast.makeText(getApplicationContext(), "About Us Selected", Toast.LENGTH_SHORT).show();
                displaySliderOptionScreen(R.id.about_us);
                break;

            case R.id.connect:
                Toast.makeText(getApplicationContext(), "Connect Selected", Toast.LENGTH_SHORT).show();
                displaySliderOptionScreen(R.id.connect);
                break;

            case R.id.history:
                Toast.makeText(getApplicationContext(), "Past Trips Selected", Toast.LENGTH_SHORT).show();
                displaySliderOptionScreen(R.id.history);
                break;

            case R.id.preferences:
                Toast.makeText(getApplicationContext(), "Preferences Selected", Toast.LENGTH_SHORT).show();
                displaySliderOptionScreen(R.id.preferences);
                break;

            case R.id.logout:
                logoutUser();
                break;

            default:
                displayErrorOnSliderOptionSelected();
                break;
        }

        return true;
    }

    // Displays the About Us page fragment
    private void displaySliderOptionScreen(int fragID) {

        // Replace the screen content with the fragment
        Intent intent = new Intent(this, SliderOptionsManagerActivity.class);
        intent.putExtra("disp_fragment", fragID);
        startActivity(intent);
    }

    // Logs out the user from the application and displays the Login page
    private void logoutUser() {

        // Display a toast message
        Toast.makeText(getApplicationContext(), "Signing off", Toast.LENGTH_SHORT).show();

        // Update the database by setting the sign out option to true
        DatabaseHandler dbHandler = new DatabaseHandler(getApplicationContext());
        AccountInformation accInfo = new AccountInformation();
        accInfo.setEmail(m_strUserMailID);
        accInfo.setName(m_strUserDispName);
        accInfo.setSignedOut(true);
        dbHandler.updateSignedOutOption(accInfo);

        //  Redirect user to the login screen
        Intent loginScreenIntent = new Intent(getApplicationContext(), LoginScreenActivity.class);
        loginScreenIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginScreenIntent);
        this.finish();
    }

    // Displays, if any, error message in case of selecting an option from the slider menu
    private void displayErrorOnSliderOptionSelected() {

        Toast.makeText(getApplicationContext(), "Somethings went wrong", Toast.LENGTH_SHORT).show();
    }
}
