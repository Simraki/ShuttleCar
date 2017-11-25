package ru.shuttlecar.shuttlecar;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iarcuschin.simpleratingbar.SimpleRatingBar;

import de.hdodenhof.circleimageview.CircleImageView;
import ru.shuttlecar.shuttlecar.models.Constants;

public class ProfileFragment extends Fragment {

    private SharedPreferences pref;
    private TextView tv_uiu, tv_name, tv_email, tv_tel, tv_carBrand, tv_carModel;
    private RelativeLayout layout_car;
    private CircleImageView image_person, image_car;
    private SimpleRatingBar rating;

    private View v_tel, v_car, v_carBM;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_profile);

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initViews(view);
        return view;
    }

    private void initViews(final View view) {
        pref = getActivity().getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);

        tv_name = (TextView) view.findViewById(R.id.profile_tv_name);
        tv_email = (TextView) view.findViewById(R.id.profile_tv_email);
        tv_tel = (TextView) view.findViewById(R.id.profile_tv_tel);
        tv_uiu = (TextView) view.findViewById(R.id.profile_tv_uiu);
        tv_carBrand = (TextView) view.findViewById(R.id.profile_tv_carBrand);
        tv_carModel = (TextView) view.findViewById(R.id.profile_tv_carModel);

        v_tel = view.findViewById(R.id.profile_v_tel);
        v_car = view.findViewById(R.id.profile_v_car);
        v_carBM = view.findViewById(R.id.profile_v_carBM);

        rating = (SimpleRatingBar) view.findViewById(R.id.profile_srb_rating);

        image_person = (CircleImageView) view.findViewById(R.id.profile_civ_person);
        image_car = (CircleImageView) view.findViewById(R.id.profile_civ_car);

        layout_car = (RelativeLayout) view.findViewById(R.id.profile_layout_car);

        tv_name.setText(pref.getString(Constants.NAME, ""));
        tv_email.setText(pref.getString(Constants.EMAIL, ""));
        tv_uiu.setText("ID: " + pref.getString(Constants.UIU, ""));

        if (pref.getString(Constants.PHOTO_PERSON, "").isEmpty()) {
            image_person.setImageResource(R.drawable.ic_default_profile);
        } else {
            HelpMethods.downloadMainImage(getActivity(), pref.getString(Constants.PHOTO_PERSON, ""), image_person);
        }

        if (pref.getString(Constants.PHOTO_CAR, "").isEmpty()) {
            image_car.setImageResource(R.drawable.ic_default_car);
        } else {
            HelpMethods.downloadMainImage(getActivity(), pref.getString(Constants.PHOTO_CAR, ""), image_car);
        }

        if (!pref.getString(Constants.TELEPHONE, "").isEmpty()) {
            v_tel.setVisibility(View.VISIBLE);
            tv_tel.setVisibility(View.VISIBLE);
            rating.setVisibility(View.VISIBLE);

            tv_tel.setText(pref.getString(Constants.TELEPHONE, ""));
            HelpMethods.setRating(pref, rating);
        }

        if (!pref.getString(Constants.CAR_BRAND, "").isEmpty() ||
                !pref.getString(Constants.CAR_MODEL, "").isEmpty() ||
                !pref.getString(Constants.PHOTO_CAR, "").isEmpty()) {
            v_car.setVisibility(View.VISIBLE);
            layout_car.setVisibility(View.VISIBLE);

            if (pref.getString(Constants.CAR_BRAND, "").isEmpty() && pref.getString(Constants.CAR_MODEL, "").isEmpty()) {
                tv_carBrand.setText("Машина не указана");
            } else {
                tv_carBrand.setText(pref.getString(Constants.CAR_BRAND, ""));
                v_carBM.setVisibility(View.VISIBLE);
                tv_carModel.setVisibility(View.VISIBLE);
                tv_carModel.setText(pref.getString(Constants.CAR_MODEL, ""));
            }

        }
    }
}
