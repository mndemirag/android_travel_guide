package androidapp.uturn.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Rakesh on 2/19/2016
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "UTurnDataStore";

    // AccountInformation table
    private static final String TABLE_ACCOUNT_INFORMATION = "contacts";

    // Columns in AccountInformation table
    private static final String COL_EMAIL = "EMAIL_ID";
    private static final String COL_NAME = "NAME";
    private static final String COL_PASSWORD = "PASSWORD";
    private static final String COL_SALT = "SALT";
    private static final String COL_ALWAYS_SIGN_ON = "ALWAYS_SIGN_ON";
    private static final String COL_SIGNED_OUT = "SIGNED_OUT";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Invoked when the database is created
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Creates the ACCOUNT INFORMATION table
        String CREATE_ACC_INFO_TABLE = "CREATE TABLE " + TABLE_ACCOUNT_INFORMATION + "("
                + COL_EMAIL + " TEXT PRIMARY KEY," + COL_NAME + " TEXT," + COL_PASSWORD + " TEXT,"
                + COL_SALT + " TEXT," + COL_ALWAYS_SIGN_ON + " INTEGER," + COL_SIGNED_OUT + " INTEGER)";

        db.execSQL(CREATE_ACC_INFO_TABLE);
    }

    // Invoked while upgrading the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older table, if exists and create a new table
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNT_INFORMATION);
        onCreate(db);
    }

    // Method to add a new account
    public boolean addAccount(AccountInformation newAccount) {

        try {
            SQLiteDatabase db = getWritableDatabase();

            ContentValues row = new ContentValues();
            row.put(COL_EMAIL, newAccount.getEmail());                   // User Mail ID
            row.put(COL_NAME, newAccount.getName());                     // Display Name of the user
            row.put(COL_PASSWORD, newAccount.getPassword());             // Hashed Password
            row.put(COL_SALT, newAccount.getSaltValue());                // Get the salt value
            row.put(COL_ALWAYS_SIGN_ON, newAccount.getIfAlwaysSignIn()); // Sets the Keep Me Always Signed In option value
            row.put(COL_SIGNED_OUT, newAccount.getIfSignedOut());        // Sets if the user signed out in the previous session

            db.insert(TABLE_ACCOUNT_INFORMATION, null, row);

            db.close();

            return true;
        } catch (SQLException ex) {
            return false;
        }
    }

    // Methods to update the account information
    public boolean updatePassword(AccountInformation account) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues newValues = new ContentValues();
        newValues.put(COL_PASSWORD, account.getPassword());
        newValues.put(COL_SALT, account.getSaltValue());

        int nCount = db.update(TABLE_ACCOUNT_INFORMATION, newValues, COL_EMAIL + " = ?",
                                                new String[] { String.valueOf(account.getEmail()) });

        return nCount == 1;
    }

    public boolean updateDispName(AccountInformation account) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues newValues = new ContentValues();
        newValues.put(COL_NAME, account.getName());

        int nCount = db.update(TABLE_ACCOUNT_INFORMATION, newValues, COL_EMAIL + " = ?",
                new String[] { String.valueOf(account.getEmail()) });

        return nCount == 1;
    }

    public boolean updateAlwaysSignOnOption(AccountInformation account) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues newValues = new ContentValues();
        newValues.put(COL_ALWAYS_SIGN_ON, account.getIfAlwaysSignIn());

        int nCount = db.update(TABLE_ACCOUNT_INFORMATION, newValues, COL_EMAIL + " = ?",
                new String[] { String.valueOf(account.getEmail()) });

        return nCount == 1;
    }

    public boolean updateSignedOutOption(AccountInformation account) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues newValues = new ContentValues();
        newValues.put(COL_SIGNED_OUT, account.getIfSignedOut());

        int nCount = db.update(TABLE_ACCOUNT_INFORMATION, newValues, COL_EMAIL + " = ?",
                new String[] { String.valueOf(account.getEmail()) });

        return nCount == 1;
    }

    // Method to get the account information
    public AccountInformation getAccountInfo(String strMailID) {

        try {
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = db.query(TABLE_ACCOUNT_INFORMATION, new String[]{COL_EMAIL, COL_NAME, COL_PASSWORD,
                            COL_SALT, COL_ALWAYS_SIGN_ON, COL_SIGNED_OUT}, COL_EMAIL + "=?",
                    new String[]{String.valueOf(strMailID)}, null, null, null, null);

            if (cursor != null) {

                if(cursor.moveToFirst()) {
                    AccountInformation accountInfo = new AccountInformation();
                    accountInfo.setEmail(cursor.getString(0));
                    accountInfo.setName(cursor.getString(1));
                    accountInfo.setPassword(cursor.getString(2));
                    accountInfo.setSaltValue(cursor.getString(3));

                    int nValue = Integer.parseInt(cursor.getString(4));
                    if (nValue == 1) {
                        accountInfo.setAlwaysSignIn(true);
                    } else {
                        accountInfo.setAlwaysSignIn(false);
                    }

                    nValue = Integer.parseInt(cursor.getString(5));
                    if (nValue == 1) {
                        accountInfo.setSignedOut(true);
                    } else {
                        accountInfo.setSignedOut(false);
                    }

                    cursor.close();
                    return accountInfo;
                }
            }

            return null;

        } catch(SQLException ex) {
            return null;
        }
    }
}
