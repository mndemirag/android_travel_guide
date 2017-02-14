package androidapp.uturn.view;


import android.os.Bundle;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import androidapp.uturn.helper.OperationsHelper;
import androidapp.uturn.model.AccountInformation;
import androidapp.uturn.model.DatabaseHandler;
import layout.ForgotPasswordFragment;

/**
 * Created by Rakesh on 2/16/2016
 */
public class LoginScreenActivity extends AppCompatActivity implements ForgotPasswordFragment.ResetPasswordDialogListener {

    private EditText m_tfUserEmailID;
    private EditText m_tfUserPassword;
    private Button m_loginButton;
    private CheckBox m_cbAutoLogon;

    private String strUserMailID, strUserName;
    private static final int NEW_USER_REGISTER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        // Initializing the variables
        m_tfUserEmailID = (EditText) findViewById(R.id.tfLoginEmailID);
        m_tfUserPassword = (EditText) findViewById(R.id.tfLoginPassword);
        m_loginButton = (Button) findViewById(R.id.btnLogin);
        m_cbAutoLogon = (CheckBox) findViewById(R.id.cbKeepMeSignedIn);

        // Auto-login functionality
        autoLogon();
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

    // Check if auto-logon option was enabled and then login to the app
    // automatically based on the setting. If the user had signed out of
    // the app explicitly in the previous session, then prompt for credentials
    private void autoLogon() {

        // Get the username from the shared preferences file
        String strMailID = null;
        SharedPreferences appPreferences = getSharedPreferences(RegistrationScreenActivity.PREFERENCES_FILENAME, MODE_PRIVATE);
        if( appPreferences.contains(RegistrationScreenActivity.KEY_MAIL_ID) ) {
            strMailID = appPreferences.getString(RegistrationScreenActivity.KEY_MAIL_ID, null);
        }

        // If the username is not stored in the file, then request
        // the user to explicitly login by providing the credentials
        if(strMailID != null) {

            // Fetch the data from the database
            DatabaseHandler dbHandler = new DatabaseHandler(getApplicationContext());
            AccountInformation accountInfo = dbHandler.getAccountInfo(strMailID);
            if(accountInfo != null) {

                // If the account exists, then check auto logon is set to true
                if( !accountInfo.getIfSignedOut() && accountInfo.getIfAlwaysSignIn() ) {

                    strUserMailID = accountInfo.getEmail();
                    strUserName = accountInfo.getName();

                    // Do auto login by auto displaying the home screen
                    Intent intent = new Intent(this, HomeScreenActivity.class);
                    intent.putExtra(RegistrationScreenActivity.KEY_MAIL_ID, strUserMailID);
                    intent.putExtra(RegistrationScreenActivity.KEY_NAME, strUserName);
                    startActivity(intent);

                } else {

                    // Request user to enter the credentials
                    Toast.makeText(getApplicationContext(), "Session signed out previously. \n " +
                                            "Please enter credentials to login", Toast.LENGTH_LONG).show();
                }
            } else {
                // Display error if the account does not exists
                Toast.makeText(getApplicationContext(), "No such user exists. \n " +
                            "Please register to login", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Opens up the register form when clicked on the text link "Register here"
    public void displayRegisterForm(View v) {

        Intent intent = new Intent(getApplicationContext(), RegistrationScreenActivity.class);
        startActivityForResult(intent, NEW_USER_REGISTER);
    }

    @Override
    public void onFinishResetDialog(String strMailID) {

        Toast.makeText(getApplicationContext(), "Password reset link sent to your mail", Toast.LENGTH_LONG).show();
    }

    // Displays the dialog to accept email id input from the user
    public void displayForgotPasswordDialog(View v) {

        FragmentManager fm = getSupportFragmentManager();
        ForgotPasswordFragment frag = new ForgotPasswordFragment();
        frag.show(fm, "fragment_forgot_password");
    }

    // Validate the user inputs and check for format errors and minimum requirement errors
    private boolean validateInputs() {

        boolean bValid = true;

        // Extract the inputs entered by the user and verify them
        String strEmail = m_tfUserEmailID.getText().toString();
        String strPassword = m_tfUserPassword.getText().toString();

        // Check if the e-mail format is right
        if (strEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
            m_tfUserEmailID.setError("Enter a valid email address");
            bValid = false;
        } else {
            m_tfUserEmailID.setError(null);
        }

        // Check if the password is at least 6 characters long and within 12 characters
        if (strPassword.isEmpty() || strPassword.length() < 6 || strPassword.length() > 12) {
            m_tfUserPassword.setError("Must be between 6-12 alphanumeric characters");
            bValid = false;
        } else {
            m_tfUserPassword.setError(null);
        }

        return bValid;
    }

    // Verify the user credentials by comparing it with the data store
    private boolean verifyCredentials() {

        boolean bCredentialsValid = false;
        strUserMailID = strUserName = "";

        // Get the account information, if existing
        DatabaseHandler dbHandler = new DatabaseHandler(getApplicationContext());
        AccountInformation accInfo = dbHandler.getAccountInfo(m_tfUserEmailID.getText().toString());

        if(accInfo != null) {

            // Compute the hash for the user entered password
            OperationsHelper helper = new OperationsHelper();
            String strHashPwd = helper.getHashedPassword(m_tfUserPassword.getText().toString(), accInfo.getSaltValue());
            if(strHashPwd.equals(accInfo.getPassword())) {

                bCredentialsValid = true;
                strUserMailID = accInfo.getEmail();
                strUserName = accInfo.getName();

                // If Always login option is checked, update the value in the database
                if( ! (m_cbAutoLogon.isChecked() == accInfo.getIfAlwaysSignIn()) ) {

                    accInfo.setAlwaysSignIn(m_cbAutoLogon.isChecked());
                    dbHandler.updateAlwaysSignOnOption(accInfo);
                }
            } else {
                Toast.makeText(getApplicationContext(), "Incorrect Password", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "No such account exists. \n" +
                                                " Create one to login", Toast.LENGTH_LONG).show();
        }

        return  bCredentialsValid;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == NEW_USER_REGISTER) {
            if (resultCode == RESULT_OK) {

                Toast.makeText(getApplicationContext(), "Login first time", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Responds to the Login button click event in the Login screen
    public void login(View v) {

        // Disable the login button
        m_loginButton.setEnabled(false);

        if (validateInputs()) {

            if (verifyCredentials()) {

                // Start the home screen on  successful login
                Intent intent = new Intent(LoginScreenActivity.this, HomeScreenActivity.class);
                intent.putExtra(RegistrationScreenActivity.KEY_MAIL_ID, strUserMailID);
                intent.putExtra(RegistrationScreenActivity.KEY_NAME, strUserName);
                startActivity(intent);
            }
        } else {

            // On Login failure, display error to the user
            Toast.makeText(getBaseContext(), "Login failed. Check credentials", Toast.LENGTH_LONG).show();
        }

        // Enable the login button for use
        m_loginButton.setEnabled(true);
    }
}
