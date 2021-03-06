package org.open311.android.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;

import org.open311.android.MainActivity;
import org.open311.android.R;

import static org.open311.android.helpers.Utils.getSettings;
import static org.open311.android.helpers.Utils.hideKeyBoard;
import static org.open311.android.helpers.Utils.saveSetting;

/**
 * Profile {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private static final String LOG_TAG = "ProfileFragment";
    private SharedPreferences settings;
    private EditText inputName, inputEmail, inputPhone;
    private TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutPhone;
    private FloatingActionButton mHideKeyboard;
    private FloatingActionButton mSubmitBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = getSettings(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView");
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);

        inputLayoutName = (TextInputLayout) view.findViewById(R.id.input_layout_name);
        inputLayoutEmail = (TextInputLayout) view.findViewById(R.id.input_layout_email);
        inputLayoutPhone = (TextInputLayout) view.findViewById(R.id.input_layout_phone);

        inputName = (EditText) view.findViewById(R.id.input_name);
        inputEmail = (EditText) view.findViewById(R.id.input_email);
        inputPhone = (EditText) view.findViewById(R.id.input_phone);

        inputName.setText(settings.getString("profile_name", null));
        inputEmail.setText(settings.getString("profile_email", null));
        inputPhone.setText(settings.getString("profile_phone", null));

        inputName.addTextChangedListener(new MyTextWatcher(inputName));
        inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));
        inputPhone.addTextChangedListener(new MyTextWatcher(inputPhone));

        mSubmitBtn = (FloatingActionButton) view.findViewById(R.id.profile_submit);
        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmitButtonClicked(v);
            }
        });
        mHideKeyboard = (FloatingActionButton) view.findViewById(R.id.profile_keyboard_close);
        mHideKeyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View parent = getActivity().findViewById(R.id.fragment_profile_ll);
                parent.clearFocus();
                hideKeyBoard(getActivity());
            }
        });
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                view.getWindowVisibleDisplayFrame(r);
                int screenHeight = view.getRootView().getHeight();

                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;

                Log.d(LOG_TAG, "keypadHeight = " + keypadHeight);

                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                    // keyboard is opened
                    mHideKeyboard.setVisibility(View.VISIBLE);
                } else {
                    // keyboard is closed
                    mHideKeyboard.setVisibility(View.INVISIBLE);
                }
            }
        });

        return view;
    }

    private void onSubmitButtonClicked(View v) {
        hideKeyBoard(getActivity());
        String result;
        if (!validate()) {
            return;
        }

        saveSetting(getActivity(), "profile_name", inputName.getText().toString());
        saveSetting(getActivity(), "profile_phone", inputPhone.getText().toString());
        result = saveSetting(getActivity(), "profile_email", inputEmail.getText().toString());
        Snackbar.make(v, result, Snackbar.LENGTH_SHORT)
                .show();
    }

    private boolean validate() {
        if (inputName.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(getString(R.string.invalid_name));
            mSubmitBtn.setVisibility(View.INVISIBLE);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }
        String strEmail = inputEmail.getText().toString().trim();

        if (strEmail.isEmpty() || !isValidEmail(strEmail)) {
            inputLayoutEmail.setError(getString(R.string.invalid_email));
            mSubmitBtn.setVisibility(View.INVISIBLE);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }
        if (inputPhone.getText().toString().trim().isEmpty()) {
            inputLayoutPhone.setError(getString(R.string.invalid_phone));
            mSubmitBtn.setVisibility(View.INVISIBLE);
            return false;
        } else {
            inputLayoutPhone.setErrorEnabled(false);
        }
        mSubmitBtn.setVisibility(View.VISIBLE);
        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            validate();
        }
    }
}
