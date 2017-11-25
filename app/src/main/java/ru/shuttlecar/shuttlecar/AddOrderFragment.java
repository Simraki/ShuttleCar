package ru.shuttlecar.shuttlecar;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.shuttlecar.shuttlecar.models.Constants;
import ru.shuttlecar.shuttlecar.models.MarkerItem;
import ru.shuttlecar.shuttlecar.models.TimePicker;
import ru.shuttlecar.shuttlecar.network.MapRequestInterface;
import ru.shuttlecar.shuttlecar.network.Order;
import ru.shuttlecar.shuttlecar.network.ServerRequest;
import ru.shuttlecar.shuttlecar.network.ServerRequestInterface;
import ru.shuttlecar.shuttlecar.network.ServerResponse;
import ru.shuttlecar.shuttlecar.network.User;


public class AddOrderFragment extends Fragment implements Animation.AnimationListener, View.OnClickListener, OnMapReadyCallback,
        ClusterManager.OnClusterClickListener<MarkerItem>,
        ClusterManager.OnClusterInfoWindowClickListener<MarkerItem>, ClusterManager.OnClusterItemClickListener<MarkerItem>,
        ClusterManager.OnClusterItemInfoWindowClickListener<MarkerItem>, GoogleMap.OnInfoWindowClickListener {

    private MaterialBetterSpinner bs_lugg;
    private CoordinatorLayout layout;
    private TextView tv_display, tv_timepick;
    private ProgressBar load;
    private ImageButton btn_add, btn_clear;

    private String time;

    private SharedPreferences pref;

    private Animation anim;
    private GoogleMap mMap;
    private ArrayList<Marker> markers = new ArrayList<>();
    private ArrayList<Marker> temp_markers = new ArrayList<>();
    private int count = 0;
    private boolean anim_complete;
    private ClusterManager<MarkerItem> cluster;

    private LinearLayout layout_tools;

    private Polyline polyline;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(R.string.subtitle_add);

        View view = inflater.inflate(R.layout.fragment_add_order, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.add_order_map);

        initViews(view);

        mapFragment.getMapAsync(this);
        return view;
    }

    @SuppressLint("SimpleDateFormat")
    private void initViews(final View view) {
        pref = getActivity().getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);

        String[] list_lugg = getResources().getStringArray(R.array.array_luggage_size);
        ArrayAdapter<String> adapter_luggage = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, list_lugg);

        bs_lugg = (MaterialBetterSpinner) view.findViewById(R.id.add_order_bs_luggsize);
        bs_lugg.setAdapter(adapter_luggage);


        tv_display = (TextView) view.findViewById(R.id.add_order_np_tv_display);
        btn_add = (ImageButton) view.findViewById(R.id.add_order_btn_add);
        btn_clear = (ImageButton) view.findViewById(R.id.add_order_btn_clear);
        tv_timepick = (TextView) view.findViewById(R.id.add_order_tv_timepicker);

        layout_tools = (LinearLayout) view.findViewById(R.id.add_order_layout_tools);

        load = (ProgressBar) view.findViewById(R.id.add_order_pb_load);
        load.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary),
                PorterDuff.Mode.SRC_ATOP);


        layout = (CoordinatorLayout) view.findViewById(R.id.add_order);


        HelpMethods.initNumberPicker(((ImageView) view.findViewById(R.id.add_order_np_iv_minus)),
                ((ImageView) view.findViewById(R.id.add_order_np_iv_plus)),
                tv_display);

        btn_add.setOnClickListener(this);
        btn_clear.setOnClickListener(this);
        tv_timepick.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.add_order_tv_timepicker:
                final TimePicker timePicker = new TimePicker();
                final Dialog dialog = timePicker.get(getContext(), getActivity().getLayoutInflater());
                timePicker.setListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        time = timePicker.getTime();
                        tv_timepick.setText("Время: " + time);
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;

            case R.id.add_order_btn_add:
                if (!pref.getString(Constants.TELEPHONE, "").isEmpty()) {

                    if (temp_markers.size() >= 2) {

                        if (!findMatches()) {

                            btn_add.setVisibility(View.GONE);
                            load.setVisibility(View.VISIBLE);
                            addOrder();

                        } else {
                            Snackbar.make(layout, Constants.PLACE_ERROR_SIM, BaseTransientBottomBar.LENGTH_LONG).show();
                        }
                    } else {
                        Snackbar.make(layout, Constants.PLACE_ERROR_EMPTY, BaseTransientBottomBar.LENGTH_LONG).show();
                    }
                } else {
                    Snackbar.make(layout, Constants.NO_TEL_ERROR, BaseTransientBottomBar.LENGTH_LONG).show();
                }

                break;
            case R.id.add_order_btn_clear:
                if (polyline != null) {
                    polyline.remove();
                }
                for (Marker mark : temp_markers) {
                    mark.remove();
                }
                if (temp_markers != null) {
                    temp_markers.clear();
                }
                if (markers != null) {
                    markers.clear();
                }
                count = 0;
                btn_clear.setVisibility(View.GONE);
                break;
        }
    }

    private boolean findMatches() {
        boolean flag = false;

        if (temp_markers.get(0).getPosition().equals(temp_markers.get(1).getPosition())) {
            flag = true;
        }

        if (temp_markers.size() > 2 && temp_markers.get(0).getPosition().equals(temp_markers.get(2).getPosition())) {
            flag = true;
        }
        if (temp_markers.size() > 2 && temp_markers.get(1).getPosition().equals(temp_markers.get(2).getPosition())) {
            flag = true;
        }

        if (temp_markers.size() > 3 && temp_markers.get(0).getPosition().equals(temp_markers.get(3).getPosition())) {
            flag = true;
        }
        if (temp_markers.size() > 3 && temp_markers.get(1).getPosition().equals(temp_markers.get(3).getPosition())) {
            flag = true;
        }
        if (temp_markers.size() > 3 && temp_markers.get(2).getPosition().equals(temp_markers.get(3).getPosition())) {
            flag = true;
        }

        if (temp_markers.size() > 4 && temp_markers.get(0).getPosition().equals(temp_markers.get(4).getPosition())) {
            flag = true;
        }
        if (temp_markers.size() > 4 && temp_markers.get(1).getPosition().equals(temp_markers.get(4).getPosition())) {
            flag = true;
        }
        if (temp_markers.size() > 4 && temp_markers.get(2).getPosition().equals(temp_markers.get(4).getPosition())) {
            flag = true;
        }
        if (temp_markers.size() > 4 && temp_markers.get(3).getPosition().equals(temp_markers.get(4).getPosition())) {
            flag = true;
        }

        return flag;
    }

    private void showPath() {
        String[] places = new String[5];
        Arrays.fill(places, "");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.MAP_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MapRequestInterface requestInterface = retrofit.create(MapRequestInterface.class);

        for (int i = 0; i < count; i++) {
            places[i] = String.valueOf(markers.get(i).getPosition().latitude) +
                    "," + String.valueOf(markers.get(i).getPosition().longitude);
        }

        String waypoints = "";
        if (count == 3) {
            waypoints = places[1];
        } else if (count == 4) {
            waypoints = places[1] + "|" + places[2];
        } else if (count == 5) {
            waypoints = places[1] + "|" + places[2] + "|" + places[3];
        }

        Call<ServerResponse> response = requestInterface.data(places[0], places[count - 1], waypoints, "driving",
                "ru", "AIzaSyDuaOC4HiNc-u-ozrIAWaBgfPHWRWC5s8M");


        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {

                String points = response.body().getRoutes()[0].getPolyline().getPoints();

                List<LatLng> list = PolyUtil.decode(points);

                polyline = mMap.addPolyline(new PolylineOptions().addAll(list));
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
            }
        });


    }

    private void addOrder() {
        ArrayList<String> temp_list = new ArrayList<>();
        temp_list.add(markers.get(0).getTitle());

        if (count == 3) {
            temp_list.add(markers.get(1).getTitle());
        } else if (count == 4) {
            temp_list.add(markers.get(1).getTitle());
            temp_list.add(markers.get(2).getTitle());
        } else if (count == 5) {
            temp_list.add(markers.get(1).getTitle());
            temp_list.add(markers.get(2).getTitle());
            temp_list.add(markers.get(3).getTitle());
        }
        temp_list.add(markers.get(count - 1).getTitle());

        String[] places = new String[temp_list.size()];
        places = temp_list.toArray(places);

        ServerRequestInterface serverRequestInterface = HelpMethods.getRetrofit().create(ServerRequestInterface.class);

        User user = new User();
        user.setEmail(pref.getString(Constants.EMAIL, ""));
        user.setUnique_id(pref.getString(Constants.UNIQUE_ID, ""));

        Order order = new Order();
        order.setPlaces(places);
        order.setTime(time);
        order.setCount_Place(HelpMethods.getValueFromNumberPicker(tv_display));

        if (!bs_lugg.getText().toString().isEmpty()) {
            order.setLugg(bs_lugg.getText().toString());
        }

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.ADD_ORDER_OPERATION);
        request.setUser(user);
        request.setOrder(order);
        Call<ServerResponse> response = serverRequestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse resp = response.body();

                if (Constants.RESULT_SUCCESS.equals(resp.getResult())) {
                    load.setVisibility(View.GONE);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.content_frame, new AddOrderFragment(), AddOrderFragment.class.toString())
                                    .commit();
                        }
                    }, 500);

                    for (Marker mark : temp_markers) {
                        mark.remove();
                    }

                    temp_markers.clear();
                    markers.clear();


                } else {
                    Snackbar.make(layout, resp.getMessage(), Snackbar.LENGTH_LONG).show();
                    btn_add.setVisibility(View.VISIBLE);
                    load.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Snackbar.make(layout, t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                btn_add.setVisibility(View.VISIBLE);
                load.setVisibility(View.GONE);
            }
        });
    }


    @Override
    public boolean onClusterClick(Cluster<MarkerItem> cluster) {
        return false;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<MarkerItem> cluster) {

    }

    @Override
    public boolean onClusterItemClick(MarkerItem markerItem) {
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(MarkerItem markerItem) {

    }


    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (animation == anim) {
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        if (polyline != null) {
            polyline.remove();
        }

        if (count == 0) {

            markers.add(marker);
            count++;
            temp_markers.add(mMap.addMarker(new MarkerOptions().position(marker.getPosition()).title("Пункт A")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))));
            btn_clear.setVisibility(View.VISIBLE);

        } else if (count == 1) {

            markers.add(marker);
            temp_markers.add(mMap.addMarker(new MarkerOptions().position(marker.getPosition()).title("Пункт B")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))));
            count++;


            if (!anim_complete) {
                anim = AnimationUtils.loadAnimation(getContext(), R.anim.layout_order_slide);
                anim.setAnimationListener(this);
                layout_tools.startAnimation(anim);
                layout_tools.setVisibility(View.VISIBLE);
                anim_complete = true;
            }
            showPath();

        } else if (count == 2) {

            temp_markers.get(1).setTitle("Промежуточный пункт 1");
            temp_markers.get(1).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

            count++;
            markers.add(marker);
            temp_markers.add(mMap.addMarker(new MarkerOptions().position(marker.getPosition()).title("Пункт B")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))));
            showPath();

        } else if (count == 3) {

            temp_markers.get(2).setTitle("Промежуточный пункт 2");
            temp_markers.get(2).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

            count++;
            markers.add(marker);
            temp_markers.add(mMap.addMarker(new MarkerOptions().position(marker.getPosition()).title("Пункт B")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))));
            showPath();

        } else if (count == 4) {

            temp_markers.get(3).setTitle("Промежуточный пункт 3");
            temp_markers.get(3).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

            count++;
            markers.add(marker);
            temp_markers.add(mMap.addMarker(new MarkerOptions().position(marker.getPosition()).title("Пункт B")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))));
            showPath();

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(temp_markers.get(temp_markers.size() - 1).getPosition(), 10));
        } else {

            for (Marker mark : temp_markers) {
                mark.remove();
            }

            temp_markers.clear();
            markers.clear();

            markers.add(marker);
            count = 1;
            temp_markers.add(mMap.addMarker(new MarkerOptions().position(marker.getPosition()).title("Пункт A")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))));

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        cluster = new ClusterManager<>(getContext(), mMap);
        cluster.setRenderer(new Renderer());
        mMap.setOnCameraIdleListener(cluster);
        mMap.setOnMarkerClickListener(cluster);
        mMap.setOnInfoWindowClickListener(cluster);
        cluster.setOnClusterClickListener(this);
        cluster.setOnClusterInfoWindowClickListener(this);
        cluster.setOnClusterItemClickListener(this);
        cluster.setOnClusterItemInfoWindowClickListener(this);

        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                getContext(), R.raw.map_style));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(55.344331, 86.064463), 10));

        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        } else {
            mMap.setMyLocationEnabled(true);
        }
        List<MarkerItem> items = HelpMethods.getStations();
        cluster.addItems(items);

        mMap.getUiSettings().setMapToolbarEnabled(false);


        mMap.setOnInfoWindowClickListener(this);
        cluster.cluster();
    }


    private class Renderer extends DefaultClusterRenderer<MarkerItem> {
        private final IconGenerator mIconGenerator = new IconGenerator(getActivity().getApplicationContext());
        private final IconGenerator mClusterIconGenerator = new IconGenerator(getActivity().getApplicationContext());
        private final ImageView mImageView;
        private final ImageView mClusterImageView;

        Renderer() {
            super(getActivity().getApplicationContext(), mMap, cluster);

            View multiProfile = getActivity().getLayoutInflater().inflate(R.layout.map_info_window, null);
            mClusterIconGenerator.setContentView(multiProfile);
            mClusterIconGenerator.setBackground(ContextCompat.getDrawable(getActivity().getApplicationContext(), android.R.color.transparent));

            mClusterImageView = (ImageView) multiProfile.findViewById(R.id.map_icon);
            mImageView = new ImageView(getActivity().getApplicationContext());
            mIconGenerator.setContentView(mImageView);
        }

        @Override
        protected void onBeforeClusterItemRendered(MarkerItem markerItem, MarkerOptions markerOptions) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_station)).title(markerItem.getTitle());
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<MarkerItem> cluster, MarkerOptions markerOptions) {
            mClusterImageView.setImageResource(R.mipmap.ic_station);
            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            return cluster.getSize() > 1;
        }
    }
}