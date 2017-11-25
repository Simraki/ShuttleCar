package ru.shuttlecar.shuttlecar;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iarcuschin.simpleratingbar.SimpleRatingBar;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import ru.shuttlecar.shuttlecar.models.Constants;
import ru.shuttlecar.shuttlecar.network.ServerRequest;
import ru.shuttlecar.shuttlecar.network.ServerRequestInterface;
import ru.shuttlecar.shuttlecar.network.ServerResponse;
import ru.shuttlecar.shuttlecar.network.User;

public class ChangeRatingFragment extends Fragment implements View.OnClickListener {

    private EditText et_phone;
    private CoordinatorLayout layout;
    private LinearLayout layout_content;
    private TextView tv_name;
    private CircleImageView image_person;
    private SimpleRatingBar rating;
    private SharedPreferences pref;
    private TextInputLayout til_tel;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(R.string.subtitle_rate);

        View view = inflater.inflate(R.layout.fragment_change_rating, container, false);
        initViews(view);
        return view;
    }

    private void initViews(final View view) {
        pref = getActivity().getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);

        til_tel = (TextInputLayout) view.findViewById(R.id.change_rating_til_tel);
        layout = (CoordinatorLayout) view.findViewById(R.id.change_rating);
        layout_content = (LinearLayout) view.findViewById(R.id.change_rating_layout_content);
        tv_name = (TextView) view.findViewById(R.id.change_rating_tv_name);
        image_person = (CircleImageView) view.findViewById(R.id.change_rating_civ_profile);
        rating = (SimpleRatingBar) view.findViewById(R.id.change_rating_srb_rating);
        Button btn_find = (Button) view.findViewById(R.id.change_rating_btn_find);
        Button btn_changerating = (Button) view.findViewById(R.id.change_rating_btn_changerating);
        et_phone = (EditText) view.findViewById(R.id.change_rating_et_tel);

        layout.clearFocus();
        et_phone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        btn_find.setOnClickListener(this);
        btn_changerating.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change_rating_btn_find:

                layout.clearFocus();

                if (et_phone.getText() == null || et_phone.getText().toString().isEmpty()) {
                    til_tel.setError(Constants.ERROR_EMPTY);
                } else if (et_phone.getText().toString().equals(pref.getString(Constants.TELEPHONE, ""))) {
                    til_tel.setError(Constants.TEL_ERROR_SIM);
                } else if (et_phone.getText().length() >= 15 && et_phone.getText().length() <= 16) {
                    changeRating(Constants.RATING_FIND_USER, et_phone.getText().toString());
                    til_tel.setError(Constants.COMPLETE);
                } else {
                    til_tel.setError(Constants.TEL_ERROR);
                }


                break;
            case R.id.change_rating_btn_changerating:
                layout.clearFocus();
                changeRating(Constants.RATING_ADD_RATING_USER, et_phone.getText().toString());
                break;
        }
    }


    private void changeRating(final Boolean type, String tel) {
        ServerRequestInterface serverRequestInterface = HelpMethods.getRetrofit().create(ServerRequestInterface.class);

        User user = new User();
        user.setTel(tel);
        if (type) {
            user.setRating(rating.getRating());
            user.setEmail(pref.getString(Constants.EMAIL, ""));
            user.setUnique_id(pref.getString(Constants.UNIQUE_ID, ""));
        }
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.ADD_RATING_OPERATION);
        request.setType(type);
        request.setUser(user);
        Call<ServerResponse> response = serverRequestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {

                ServerResponse resp = response.body();

                if (Constants.RESULT_SUCCESS.equals(resp.getResult()) && !type) {
                    layout_content.setVisibility(View.VISIBLE);
                    tv_name.setText(resp.getUser().getName());

                    if (resp.getUser().getImage_person().isEmpty()) {
                        image_person.setImageResource(R.drawable.ic_default_profile);
                    } else {
                        HelpMethods.downloadImage(getActivity(), resp.getUser().getImage_person(), image_person);
                    }

                } else if (Constants.RESULT_SUCCESS.equals(resp.getResult()) && type) {
                    Snackbar.make(layout, Constants.RESULT_COMPLETE, Snackbar.LENGTH_SHORT).show();
                    layout_content.setVisibility(View.GONE);
                    et_phone.setText(Constants.COMPLETE);
                } else {
                    if (!resp.getMessage().equals(Constants.RATING_ERROR)) {
                        til_tel.setError(resp.getMessage());
                        layout_content.setVisibility(View.GONE);
                    } else {
                        Snackbar.make(layout, resp.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Snackbar.make(layout, Constants.RESULT_ERROR, Snackbar.LENGTH_LONG).show();
            }
        });
    }
}