package ru.shuttlecar.shuttlecar;


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
import ru.shuttlecar.shuttlecar.network.ServerRequest;
import ru.shuttlecar.shuttlecar.network.ServerRequestInterface;
import ru.shuttlecar.shuttlecar.network.ServerResponse;
import ru.shuttlecar.shuttlecar.network.User;

public class RegisterFragment extends Fragment implements View.OnClickListener {

    private CircularProgressButton btn_reg;
    private TextInputLayout til_name, til_email, til_pass;
    private EditText et_name, et_email, et_pass;
    private LinearLayout layout;

    private SharedPreferences pref;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_register, container, false);
        initViews(view);
        return view;
    }

    private void initViews(final View view) {
        pref = getActivity().getPreferences(0);
        layout = (LinearLayout) view.findViewById(R.id.register);

        btn_reg = (CircularProgressButton) view.findViewById(R.id.register_cbtn_register);
        til_name = (TextInputLayout) view.findViewById(R.id.register_til_name);
        til_email = (TextInputLayout) view.findViewById(R.id.register_til_email);
        til_pass = (TextInputLayout) view.findViewById(R.id.register_til_password);
        et_name = (EditText) view.findViewById(R.id.register_et_name);
        et_email = (EditText) view.findViewById(R.id.register_et_email);
        et_pass = (EditText) view.findViewById(R.id.register_et_password);

        layout.clearFocus();

        btn_reg.setOnClickListener(this);
        view.findViewById(R.id.register_rlayout_tologin).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_rlayout_tologin:
                goToLogin();
                break;

            case R.id.register_cbtn_register:
                String name = et_name.getText().toString();
                String email = et_email.getText().toString();
                String pass = et_pass.getText().toString();

                layout.clearFocus();

                til_name.setError(HelpMethods.name_verification(name));
                til_pass.setError(HelpMethods.password_verification(pass));
                til_email.setError(HelpMethods.email_verification(email));

                if (pass.equals(name)) {
                    til_pass.setError(Constants.PASS_ERROR_SIM_NAME);
                }

                if (pass.equals(email)) {
                    til_pass.setError(Constants.PASS_ERROR_SIM_EMAIL);
                }

                if (!name.isEmpty() && !pass.isEmpty() && !email.isEmpty()
                        && til_pass.getError().toString().isEmpty()
                        && til_email.getError().toString().isEmpty()
                        && til_name.getError().toString().isEmpty()) {

                    btn_reg.setIndeterminateProgressMode(true);
                    btn_reg.setProgress(50);
                    btn_reg.setClickable(false);
                    register(name, email, pass);

                } else {
                    btn_reg.setIndeterminateProgressMode(true);
                    btn_reg.setProgress(50);
                    btn_reg.setClickable(false);
                    HelpMethods.errorShow(btn_reg);
                }
                break;
        }
    }

    private void register(String name, String email, String pass) {
        ServerRequestInterface serverRequestInterface = HelpMethods.getRetrofit().create(ServerRequestInterface.class);

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(pass);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.REGISTER_OPERATION);
        request.setUser(user);

        Call<ServerResponse> response = serverRequestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();

                if (resp.getResult().equals(Constants.RESULT_SUCCESS)) {

                    btn_reg.setProgress(100);
                    goToLogin();

                } else {
                    if (resp.getMessage() != null) {
                        Snackbar.make(layout, resp.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                    HelpMethods.errorShow(btn_reg);
                }

            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                HelpMethods.errorShow(btn_reg);
            }
        });
    }

    private void goToLogin() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, new LoginFragment(), null)
                .commit();
    }
}