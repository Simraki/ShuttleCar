package ru.shuttlecar.shuttlecar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.shuttlecar.shuttlecar.models.Constants;
import ru.shuttlecar.shuttlecar.network.ServerRequest;
import ru.shuttlecar.shuttlecar.network.ServerRequestInterface;
import ru.shuttlecar.shuttlecar.network.ServerResponse;
import ru.shuttlecar.shuttlecar.network.User;

public class ChangeProfileFragment extends Fragment implements View.OnClickListener {

    private CircularProgressButton btn_change;
    private TextInputLayout til_name, til_carbrand, til_carmodel, til_tel;
    private EditText et_name, et_carbrand, et_carmodel, et_phone;
    private ScrollView layout;
    private CircleImageView civ_person, civ_car;
    private boolean person, car;

    private SharedPreferences pref;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_changeprofile);

        View view = inflater.inflate(R.layout.fragment_change_profile, container, false);
        initViews(view);
        return view;
    }

    private void initViews(final View view) {
        pref = getActivity().getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);

        layout = (ScrollView) view.findViewById(R.id.change_profile);

        civ_person = (CircleImageView) view.findViewById(R.id.change_profile_civ_person);
        civ_car = (CircleImageView) view.findViewById(R.id.change_profile_civ_car);
        btn_change = (CircularProgressButton) view.findViewById(R.id.change_profile_cbtn_changeprofile);
        et_name = (EditText) view.findViewById(R.id.change_profile_et_name);
        et_carmodel = (EditText) view.findViewById(R.id.change_profile_et_carmodel);
        et_carbrand = (EditText) view.findViewById(R.id.change_profile_et_carbrand);
        et_phone = (EditText) view.findViewById(R.id.change_profile_et_tel);

        til_name = (TextInputLayout) view.findViewById(R.id.change_profile_til_name);
        til_carbrand = (TextInputLayout) view.findViewById(R.id.change_profile_til_carbrand);
        til_carmodel = (TextInputLayout) view.findViewById(R.id.change_profile_til_carmodel);

        civ_person = (CircleImageView) view.findViewById(R.id.change_profile_civ_person);
        civ_car = (CircleImageView) view.findViewById(R.id.change_profile_civ_car);

        et_phone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        layout.clearFocus();

        if (pref.getString(Constants.PHOTO_PERSON, "").isEmpty()) {
            civ_person.setImageResource(R.drawable.ic_default_profile);
        } else {
            HelpMethods.downloadMainImage(getActivity(), pref.getString(Constants.PHOTO_PERSON, ""), civ_person);
        }

        if (pref.getString(Constants.PHOTO_CAR, "").isEmpty()) {
            civ_car.setImageResource(R.drawable.ic_default_car);
        } else {
            HelpMethods.downloadMainImage(getActivity(), pref.getString(Constants.PHOTO_CAR, ""), civ_car);
        }

        et_name.setText(pref.getString(Constants.NAME, ""));
        et_phone.setText(pref.getString(Constants.TELEPHONE, ""));
        et_carbrand.setText(pref.getString(Constants.CAR_BRAND, ""));
        et_carmodel.setText(pref.getString(Constants.CAR_MODEL, ""));

        civ_person.setOnClickListener(this);
        civ_car.setOnClickListener(this);
        btn_change.setOnClickListener(this);
        view.findViewById(R.id.change_profile_tv_changepassword).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change_profile_tv_changepassword:
                showDialogChangePassword();
                break;
            case R.id.change_profile_civ_person:
                person = true;
                startCrop();
                break;
            case R.id.change_profile_civ_car:
                car = true;
                startCrop();
                break;
            case R.id.change_profile_cbtn_changeprofile:
                layout.clearFocus();

                String name = et_name.getText().toString();
                String car_brand = et_carbrand.getText().toString();
                String car_model = et_carmodel.getText().toString();

                til_name.setError(HelpMethods.name_verification(name));
                til_carbrand.setError(HelpMethods.car_verification(car_brand));
                til_carmodel.setError(HelpMethods.car_verification(car_model));

                btn_change.setIndeterminateProgressMode(true);
                btn_change.setProgress(50);
                btn_change.setClickable(false);

                if (til_name.getError().toString().isEmpty()
                        && til_carbrand.getError().toString().isEmpty()
                        && til_carmodel.getError().toString().isEmpty()) {

                    changeProfile();
                } else {

                    HelpMethods.errorShow(btn_change);
                }
                break;
        }
    }

    private void changeProfile() {
        final Context context = getContext();
        ServerRequestInterface serverRequestInterface = HelpMethods.getRetrofit().create(ServerRequestInterface.class);

        User user = new User();

        user.setEmail(pref.getString(Constants.EMAIL, ""));
        user.setUnique_id(pref.getString(Constants.UNIQUE_ID, ""));

        user.setName(et_name.getText().toString());
        user.setCar_brand(et_carbrand.getText().toString());
        user.setCar_model(et_carmodel.getText().toString());
        user.setTel(et_phone.getText().toString());

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.CHANGE_PROFILE_OPERATION);
        request.setUser(user);
        Call<ServerResponse> response = serverRequestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {

                ServerResponse resp = response.body();

                if (resp.getResult().equals(Constants.RESULT_SUCCESS)) {

                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString(Constants.NAME, et_name.getText().toString());
                    editor.putString(Constants.CAR_BRAND, et_carbrand.getText().toString());
                    editor.putString(Constants.CAR_MODEL, et_carmodel.getText().toString());
                    editor.putString(Constants.TELEPHONE, et_phone.getText().toString());
                    editor.apply();

                    btn_change.setProgress(100);
                } else {
                    HelpMethods.errorShow(btn_change);
                    Toast.makeText(context, resp.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                HelpMethods.errorShow(btn_change);
            }
        });

    }

    private void showDialogChangePassword() {
        final AlertDialog dialog;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_change_password, null);

        final EditText et_old_password = (EditText) view.findViewById(R.id.change_password_et_oldpassword);
        final EditText et_new_password_first = (EditText) view.findViewById(R.id.change_password_et_newpassword);
        final EditText et_new_password_second = (EditText) view.findViewById(R.id.change_password_et_verfnewpassword);
        final TextInputLayout til_change_password_check_first = (TextInputLayout) view.findViewById(R.id.change_password_til_newpassword);
        final TextInputLayout til_change_password_check_second = (TextInputLayout) view.findViewById(R.id.change_password_til_verfnewpassword);
        final TextInputLayout til_change_password_old_check = (TextInputLayout) view.findViewById(R.id.change_password_til_oldpassword);
        final ProgressBar pb = (ProgressBar) view.findViewById(R.id.change_password_pb_load);

        builder.setView(view);
        builder.setPositiveButton(R.string.change, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String old_password = et_old_password.getText().toString();
                String new_password_first = et_new_password_first.getText().toString();
                String new_password_second = et_new_password_second.getText().toString();

                til_change_password_old_check.setError(HelpMethods.password_verification(old_password));
                til_change_password_check_first.setError(HelpMethods.password_verification(new_password_first));
                til_change_password_check_second.setError(HelpMethods.password_verification(new_password_second));

                if (!old_password.isEmpty() && !new_password_first.isEmpty() && !new_password_second.isEmpty()
                        && til_change_password_old_check.getError().toString().isEmpty()
                        && til_change_password_check_first.getError().toString().isEmpty()
                        && til_change_password_check_second.getError().toString().isEmpty()) {

                    if (old_password.equals(new_password_first) || old_password.equals(new_password_second)) {

                        til_change_password_old_check.setError(Constants.PASS_ERROR_SIM_PASS);

                    } else {

                        if (new_password_first.equals(new_password_second)) {
                            pb.setVisibility(View.VISIBLE);
                            pb.setIndeterminate(true);
                            pb.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                            changePassword(pref.getString(Constants.EMAIL, ""), old_password, new_password_second, dialog, pb);

                        } else {

                            til_change_password_check_first.setError(Constants.PASS_NO_SIM);
                            til_change_password_check_second.setError(Constants.PASS_NO_SIM);

                        }

                    }
                }
            }
        });
    }

    private void changePassword(String email, String old_password, String new_password, final AlertDialog dialog, final ProgressBar pb) {
        ServerRequestInterface serverRequestInterface = HelpMethods.getRetrofit().create(ServerRequestInterface.class);

        User user = new User();
        user.setEmail(email);
        user.setOld_password(old_password);
        user.setNew_password(new_password);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.CHANGE_PASSWORD_OPERATION);
        request.setUser(user);
        Call<ServerResponse> response = serverRequestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                pb.setVisibility(View.GONE);

                ServerResponse resp = response.body();
                if (resp.getResult().equals(Constants.RESULT_SUCCESS)) {
                    dialog.dismiss();
                }
                if (resp.getMessage() != null) {
                    Snackbar.make(getView(), resp.getMessage(), Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                pb.setVisibility(View.GONE);
            }
        });
    }

    private void startCrop() {
        Handler handler = new Handler();
        civ_person.setClickable(false);
        civ_car.setClickable(false);
        startActivityForResult(CropImage.getPickImageChooserIntent(getContext()), CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                civ_person.setClickable(true);
                civ_car.setClickable(true);
            }
        }, 100);
    }

    @Override
    @SuppressLint("NewApi")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(getContext(), data);
            if (CropImage.isReadExternalStoragePermissionsRequired(getActivity(), imageUri)) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            } else {
                startCropImageActivity(imageUri);
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == Activity.RESULT_OK && person) {
                civ_person.setImageURI(result.getUri());
                new UploadImage().execute(pref.getString(Constants.EMAIL, ""), pref.getString(Constants.UNIQUE_ID, ""),
                        Constants.IMAGE_PERSON);
            } else if (resultCode == Activity.RESULT_OK && car) {
                civ_car.setImageURI(result.getUri());
                new UploadImage().execute(pref.getString(Constants.EMAIL, ""), pref.getString(Constants.UNIQUE_ID, ""),
                        Constants.IMAGE_CAR);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Snackbar.make(layout, Constants.RESULT_ERROR, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    public void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.OFF)
                .setCropShape(CropImageView.CropShape.OVAL)
                .setBorderCornerThickness(0)
                .setActivityTitle("Ваша миниатюра")
                .setAspectRatio(1, 1)
                .setSnapRadius(5)
                .setInitialCropWindowPaddingRatio(0)
                .setFixAspectRatio(true)
                .setAutoZoomEnabled(true)
                .setMultiTouchEnabled(true)
                .setAutoZoomEnabled(true)
                .setBorderCornerLength(0)
                .setActivityMenuIconColor(Color.WHITE)
                .setBackgroundColor(Color.argb(120, 255, 255, 255))
                .setBorderLineColor(Color.parseColor("#4caf50"))
                .start(getContext(), this);
    }

    private class UploadImage extends AsyncTask<String, Void, Void> {

        BitmapDrawable drawable;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (person) {
                drawable = (BitmapDrawable) civ_person.getDrawable();
            } else if (car) {
                drawable = (BitmapDrawable) civ_car.getDrawable();
            }
        }

        @Override
        protected Void doInBackground(String... params) {
            final Context context = getContext();
            String email = params[0];
            String un_id = params[1];
            final Boolean type = Boolean.valueOf(params[2]);
            ServerRequestInterface serverRequestInterface = HelpMethods.getRetrofit().create(ServerRequestInterface.class);

            User user = new User();
            user.setEmail(email);
            user.setUnique_id(un_id);


            Bitmap bitmap = drawable.getBitmap();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String image = Base64.encodeToString(byteArray, Base64.DEFAULT);

            if (type) {
                user.setImage_car(image);
            } else {
                user.setImage_person(image);
            }

            final ServerRequest request = new ServerRequest();
            request.setOperation(Constants.CHANGE_IMAGE_OPERATION);
            request.setUser(user);
            request.setType(type);
            Call<ServerResponse> response = serverRequestInterface.operation(request);

            response.enqueue(new Callback<ServerResponse>() {
                @Override
                public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                    ServerResponse resp = response.body();

                    if (resp.getResult().equals(Constants.RESULT_SUCCESS)) {

                        String url;
                        if (type) {
                            url = resp.getUser().getImage_car();
                            pref.edit().putString(Constants.PHOTO_CAR, url).apply();
                        } else {
                            url = resp.getUser().getImage_person();
                            pref.edit().putString(Constants.PHOTO_PERSON, url).apply();
                        }

                        ImageLoader imageLoader = ImageLoader.getInstance();
                        imageLoader.init(ImageLoaderConfiguration.createDefault(context));

                        if (MemoryCacheUtils.findCachedBitmapsForImageUri(url, imageLoader.getMemoryCache()) != null) {
                            imageLoader.clearDiskCache();
                            imageLoader.clearMemoryCache();
                        }

                    }
                    if (resp.getMessage() != null) {
                        Toast.makeText(context, resp.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ServerResponse> call, Throwable t) {
                    Toast.makeText(context, Constants.RESULT_ERROR, Toast.LENGTH_SHORT).show();
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            person = false;
            car = false;
        }
    }
}