package layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.support.v4.app.DialogFragment;

import androidapp.uturn.view.R;

/**
 * created by Rakesh on 20/02/2016
 */
public class ForgotPasswordFragment extends DialogFragment implements Button.OnClickListener {

    public interface ResetPasswordDialogListener {

        void onFinishResetDialog(String strMailID);
    }

    private EditText m_tfAccountMailID;

    public ForgotPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forgot_password, container);

        // Show soft keyboard automatically
        m_tfAccountMailID = (EditText) view.findViewById(R.id.tfAccountMailID);
        Button m_btnSendMail = (Button) view.findViewById(R.id.btnSendMail);

        getDialog().setTitle(R.string.resetPasswordLabel);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        m_tfAccountMailID.requestFocus();
        m_btnSendMail.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {

        // Check if the e-mail format is right
        String strMail = m_tfAccountMailID.getText().toString();
        if (strMail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(strMail).matches()) {

            m_tfAccountMailID.setError("Enter a valid email address");
        } else {

            m_tfAccountMailID.setError(null);

            // Return input mail text to the implementing activity
            ResetPasswordDialogListener activity = (ResetPasswordDialogListener) getActivity();
            activity.onFinishResetDialog(strMail);
            this.dismiss();
        }
    }
}
