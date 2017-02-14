package androidapp.uturn.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.ProgressDialog;

import androidapp.uturn.helper.OperationsHelper;
import androidapp.uturn.model.AccountInformation;
import androidapp.uturn.model.DatabaseHandler;

/**
 * Created by Rakesh on 2/16/2016
 */
public class RegistrationScreenActivity extends AppCompatActivity {

    public static final String PREFERENCES_FILENAME = "UTurn_Preferences";
    public static final String KEY_MAIL_ID = "USER_MAIL_ID";
    public static final String KEY_NAME = "NAME";

    private EditText m_tfNameOfUser;
    private EditText m_tfRegEmailID;
    private EditText m_tfRegPassword;
    private EditText m_tfRegConfirmPassword;
    private Button m_registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Initializing the variables
        m_tfNameOfUser = (EditText) findViewById(R.id.tfUserName);
        m_tfRegEmailID = (EditText) findViewById(R.id.tfLoginEmailID);
        m_tfRegPassword = (EditText) findViewById(R.id.tfLoginPassword);
        m_tfRegConfirmPassword = (EditText) findViewById(R.id.tfRegConfirmPassword);
        m_registerButton = (Button) findViewById(R.id.btnRegister);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login_screen, menu);
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

    // Validate the user inputs and check for format errors and minimum requirement errors
    private boolean validateInputs() {

        boolean bValid = true;

        // Extract the inputs entered by the user and verify them
        String strEmail = m_tfRegEmailID.getText().toString();
        String strPassword = m_tfRegPassword.getText().toString();
        String strConfirmPassword = m_tfRegConfirmPassword.getText().toString();

        // Check if the e-mail format is right
        if (strEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
            m_tfRegEmailID.setError("Enter a valid email address");
            bValid = false;
        } else {
            m_tfRegEmailID.setError(null);
        }

        // Check if the password is at least 6 characters long and within 12 characters
        if (strPassword.isEmpty() || strPassword.length() < 6 || strPassword.length() > 12) {
            m_tfRegPassword.setError("Must be between 6-12 alphanumeric characters");
            bValid = false;
        } else {
            m_tfRegPassword.setError(null);
        }

        // Check if password and confirm password matches
        if (strConfirmPassword.isEmpty() || strConfirmPassword.length() < 6 ||
                strConfirmPassword.length() > 12 || ! (strConfirmPassword.equals(strPassword)) ) {
            m_tfRegConfirmPassword.setError("Passwords do not match");
            bValid = false;
        } else {
            m_tfRegConfirmPassword.setError(null);
        }

        return bValid;
    }

    // Verify the user credentials by comparing it with the data store
    private boolean storeCredentials() {

        boolean bStored = false;

        // Read all the inputs
        String strNameOfUser = m_tfNameOfUser.getText().toString();
        String strEmail = m_tfRegEmailID.getText().toString();
        String strPassword = m_tfRegPassword.getText().toString();

        // Get the salt value and get the hashed password
        OperationsHelper helper = new OperationsHelper();
        String strSaltValue = helper.getSalt();
        if( !strSaltValue.equals("") ) {
            String strHashedPwd = helper.getHashedPassword(strPassword, strSaltValue);

            // Create a new account object and fill in the values to store it in the DB
            AccountInformation newAccount = new AccountInformation();
            newAccount.setName(strNameOfUser);
            newAccount.setEmail(strEmail);
            newAccount.setPassword(strHashedPwd);
            newAccount.setSaltValue(strSaltValue);
            newAccount.setAlwaysSignIn(false);
            newAccount.setSignedOut(false);

            DatabaseHandler dbHandler = new DatabaseHandler(getApplicationContext());
            bStored = dbHandler.addAccount(newAccount);

            // Store the user mail id in the preferences file
            if(bStored) {
                SharedPreferences appPreferences = getSharedPreferences(PREFERENCES_FILENAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = appPreferences.edit();

                editor.putString(KEY_MAIL_ID, strEmail);

                editor.apply(); // Commit writes the data immediately while apply does it in the background
            }
        }

        return  bStored;
    }

    public void onRegistrationSuccess() {

        // Set the login button to true and set the progress bar to complete
        m_registerButton.setEnabled(true);
        setResult(RESULT_OK);
        finish();
    }
    
    public void onRegistrationFailed() {

        // On Login failure, display error to the user
        Toast.makeText(getBaseContext(), "Registration failed. Try again", Toast.LENGTH_LONG).show();
        m_registerButton.setEnabled(true);
    }

    // Responds to the Register button click events
    public void registerUser(View view) {

        if ( validateInputs() ) {

            // Disable the login button
            m_registerButton.setEnabled(false);

            // Show the progress dialog
            final ProgressDialog registerProgressDialog = new ProgressDialog(RegistrationScreenActivity.this);
            registerProgressDialog.setIndeterminate(true);
            registerProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            registerProgressDialog.setMessage("Creating Account...");
            registerProgressDialog.show();

            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {

                            boolean bRegistered = storeCredentials();

                            // Dismiss the progress bar dialog
                            registerProgressDialog.dismiss();

                            if( bRegistered ) {

                                onRegistrationSuccess();
                            } else {

                                onRegistrationFailed();
                            }


                        }
                    }, 2000);
        } else {

            onRegistrationFailed();
        }
    }
}
