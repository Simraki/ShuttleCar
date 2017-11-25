package ru.shuttlecar.shuttlecar;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.dd.CircularProgressButton;

import retrofit2.Call;
import retrofit2.Callback;
import ru.shuttlecar.shuttlecar.models.Constants;
import ru.shuttlecar.shuttlecar.models.State_logged_in;
import ru.shuttlecar.shuttlecar.network.ServerRequest;
import ru.shuttlecar.shuttlecar.network.ServerRequestInterface;
import ru.shuttlecar.shuttlecar.network.ServerResponse;
import ru.shuttlecar.shuttlecar.network.User;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private SharedPreferences pref;

    private CircularProgressButton btn_login;
    private TextInputLayout til_email, til_pass;
    private EditText et_email, et_pass;
    private LinearLayout layout;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        initViews(view);
        return view;
    }

    private void initViews(final View view) {
        pref = getActivity().getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);

        layout = (LinearLayout) view.findViewById(R.id.login);
        btn_login = (CircularProgressButton) view.findViewById(R.id.login_cbtn_enter);
        til_email = (TextInputLayout) view.findViewById(R.id.login_til_email);
        til_pass = (TextInputLayout) view.findViewById(R.id.login_til_password);
        et_email = (EditText) view.findViewById(R.id.login_et_email);
        et_pass = (EditText) view.findViewById(R.id.login_et_password);

        layout.clearFocus();

        btn_login.setOnClickListener(this);
        view.findViewById(R.id.login_rlayout_toregister).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_rlayout_toregister:
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, new RegisterFragment(), null)
                        .commit();
                break;

            case R.id.login_cbtn_enter:
                String email = et_email.getText().toString();
                String pass = et_pass.getText().toString();

                layout.clearFocus();

                if (email.isEmpty()) {
                    til_email.setError(Constants.ERROR_EMPTY);
                } else {
                    til_email.setError(Constants.COMPLETE);
                }
                if (pass.isEmpty()) {
                    til_pass.setError(Constants.ERROR_EMPTY);
                } else {
                    til_pass.setError(Constants.COMPLETE);
                }
                if (!email.isEmpty() && !pass.isEmpty()) {
                    btn_login.setIndeterminateProgressMode(true);
                    btn_login.setProgress(50);
                    btn_login.setClickable(false);
                    login(email, pass);
                } else {
                    btn_login.setIndeterminateProgressMode(true);
                    btn_login.setProgress(50);
                    btn_login.setClickable(false);
                    HelpMethods.errorShow(btn_login);
                }
                break;
        }
    }

    private void login(String email, String pass) {
        ServerRequestInterface serverRequestInterface = HelpMethods.getRetrofit().create(ServerRequestInterface.class);

        User user = new User();
        user.setEmail(email);
        user.setPassword(pass);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.LOGIN_OPERATION);
        request.setUser(user);
        Call<ServerResponse> response = serverRequestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {

                ServerResponse resp = response.body();

                if (resp.getResult().equals(Constants.RESULT_SUCCESS)) {

                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean(Constants.IS_LOGGED_IN, true);

                    editor.putString(Constants.UNIQUE_ID, resp.getUser().getUnique_id());
                    editor.putString(Constants.EMAIL, resp.getUser().getEmail());
                    editor.putString(Constants.NAME, resp.getUser().getName());
                    editor.putString(Constants.PHOTO_PERSON, resp.getUser().getImage_person());
                    editor.putString(Constants.UIU, resp.getUser().getUiu());
                    editor.putString(Constants.CAR_BRAND, resp.getUser().getCar_brand());
                    editor.putString(Constants.CAR_MODEL, resp.getUser().getCar_model());
                    editor.putString(Constants.TELEPHONE, resp.getUser().getTel());
                    editor.putString(Constants.PHOTO_CAR, resp.getUser().getImage_car());
                    editor.putInt(Constants.ID, resp.getUser().getId());

                    editor.apply();

                    btn_login.setProgress(100);

                    logged();
                } else {
                    if (resp.getMessage() != null) {
                        Snackbar.make(layout, resp.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                    HelpMethods.errorShow(btn_login);
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                HelpMethods.errorShow(btn_login);
            }
        });
    }

    private void logged() {
        try {
            pref.edit().putBoolean(Constants.IS_LOGGED_IN, true).apply();

            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, new FindOrderFragment(), FindOrderFragment.class.toString())
                    .commit();
            ((State_logged_in) getActivity()).setState(true);
        } catch (Exception ignored) {
        }
    }

}