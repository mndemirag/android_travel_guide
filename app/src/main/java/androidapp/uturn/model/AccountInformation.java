package androidapp.uturn.model;

/**
 * Created by Rakesh on 2/19/2016
 *
 * Java class that contains the getters and setters for the columns
 * in the table AccountInformation in database. Each object of this class
 * represents a row in the table.
 *
 * Class name represents the table name in the database
 */
public class AccountInformation {

    // Columns in the table
    private String m_strEmailID;        // To store and retrieve the user mail ID
    private String m_strName;           // To store and retrieve the name of the user
    private String m_strPassword;       // To store and retrieve the hashed password
    private String m_strSalt;           // To store and retrieve the random salt string used for password hashing
    private boolean m_bKeepMeSignedIn;  // To enable auto sign-in to prevent entering credentials every time
    private boolean m_bSignedOut;       // To prevent auto sign-in into the application once user logs out

    public AccountInformation() {

        m_strEmailID = m_strName = m_strPassword = m_strSalt = "";
        m_bKeepMeSignedIn = m_bSignedOut = false;
    }

    // Getter and setter for the name of the user
    public String getName() {

        return m_strName;
    }

    public void setName(String strName) {

        m_strName = strName;
    }

    // Getter and setter for the e-mail ID of the user
    public String getEmail() {

        return m_strEmailID;
    }

    public void setEmail(String strEmail) {

        m_strEmailID = strEmail;
    }

    // Getter and setter for the password of the user
    public String getPassword() {

        return m_strPassword;
    }

    public void setPassword(String strPwd) {

        m_strPassword = strPwd;
    }

    // Getter and setter for the random salt value
    public String getSaltValue() {

        return m_strSalt;
    }

    public void setSaltValue(String strSalt) {

        m_strSalt = strSalt;
    }

    // Getter and setter for the always sign in option
    public boolean getIfAlwaysSignIn() {

        return m_bKeepMeSignedIn;
    }

    public void setAlwaysSignIn(boolean bAlwaysSignIn) {

        m_bKeepMeSignedIn = bAlwaysSignIn;
    }

    // Getter and setter to check if user in his previous application
    // access signed out of the application or not
    public boolean getIfSignedOut() {

        return m_bSignedOut;
    }

    public void setSignedOut(boolean bSignedOut) {

        m_bSignedOut = bSignedOut;
    }
}
