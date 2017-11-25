package ru.shuttlecar.shuttlecar;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
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
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.shuttlecar.shuttlecar.models.Constants;
import ru.shuttlecar.shuttlecar.models.MarkerItem;
import ru.shuttlecar.shuttlecar.models.OrderItem;
import ru.shuttlecar.shuttlecar.models.PassengerItem;
import ru.shuttlecar.shuttlecar.models.TimePicker;
import ru.shuttlecar.shuttlecar.network.MapRequestInterface;
import ru.shuttlecar.shuttlecar.network.Order;
import ru.shuttlecar.shuttlecar.network.Passenger;
import ru.shuttlecar.shuttlecar.network.ServerRequest;
import ru.shuttlecar.shuttlecar.network.ServerRequestInterface;
import ru.shuttlecar.shuttlecar.network.ServerResponse;
import ru.shuttlecar.shuttlecar.network.User;


public class FindOrderFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener, ClusterManager.OnClusterClickListener<MarkerItem>,
        ClusterManager.OnClusterInfoWindowClickListener<MarkerItem>, ClusterManager.OnClusterItemClickListener<MarkerItem>,
        ClusterManager.OnClusterItemInfoWindowClickListener<MarkerItem>, Animation.AnimationListener, View.OnClickListener {

    private TextView tv_timepick, tv_display;
    private ImageButton btn_find, btn_clear;
    private CoordinatorLayout layout;

    private ProgressBar load;


    private String time_db;
    private SimpleDateFormat DateForm_db, DateForm_order;
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

        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(R.string.subtitle_find);

        View view = inflater.inflate(R.layout.fragment_find_order, container, false);
        initViews(view);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.find_order_map);

        mapFragment.getMapAsync(this);
        return view;
    }


    @SuppressLint("SimpleDateFormat")
    private void initViews(final View view) {
        DateForm_db = new SimpleDateFormat(getContext().getString(R.string.form_date_db));
        DateForm_order = new SimpleDateFormat(getContext().getString(R.string.form_date_text));

        pref = getActivity().getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);

        layout_tools = (LinearLayout) view.findViewById(R.id.find_order_layout_tools);

        HelpMethods.initNumberPicker(((ImageView) view.findViewById(R.id.find_order_np_iv_minus)),
                ((ImageView) view.findViewById(R.id.find_order_np_iv_plus)),
                ((TextView) view.findViewById(R.id.find_order_np_tv_display)));

        tv_timepick = (TextView) view.findViewById(R.id.find_order_tv_timepicker);
        btn_find = (ImageButton) view.findViewById(R.id.find_order_btn_find);
        btn_clear = (ImageButton) view.findViewById(R.id.find_order_btn_clear);

        load = (ProgressBar) view.findViewById(R.id.find_order_pb_load);

        tv_display = (TextView) view.findViewById(R.id.find_order_np_tv_display);
        layout = (CoordinatorLayout) view.findViewById(R.id.find_order);

        tv_timepick.setOnClickListener(this);
        btn_find.setOnClickListener(this);
        btn_clear.setOnClickListener(this);
    }

    private void findOrder() {
        ServerRequestInterface serverRequestInterface = HelpMethods.getRetrofit().create(ServerRequestInterface.class);

        final Order order = new Order();
        order.setPdis(markers.get(0).getTitle());
        order.setPdel(markers.get(1).getTitle());
        order.setTime(time_db);
        order.setCount_Place(HelpMethods.getValueFromNumberPicker(tv_display));

        ServerRequest request = new ServerRequest();
        if (!pref.getString(Constants.TELEPHONE, "").isEmpty()) {
            final User user = new User();
            user.setTel(pref.getString(Constants.TELEPHONE, ""));
            request.setUser(user);
        }
        request.setOperation(Constants.FIND_ORDER_OPERATION);
        request.setOrder(order);
        Call<ServerResponse> response = serverRequestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse resp = response.body();

                if (Constants.RESULT_SUCCESS.equals(resp.getResult())) {

                    ArrayList<OrderItem> items = new ArrayList<>();
                    Order[] orders = resp.getOrders();

                    for (Order order : orders) {
                        boolean reserve = false;
                        User user = order.getDriver();
                        Passenger[] passengers = order.getPassengers();
                        ArrayList<PassengerItem> passengerItems = new ArrayList<>();

                        int id = order.getId(),
                                count_place = order.getCount_Place();
                        String[] places = order.getPlaces();
                        String lugg = order.getLuggage(),
                                time = order.getTime(),
                                name = user.getName(),
                                tel = user.getTel(),
                                image_person = user.getImage_person(),
                                date = order.getDate();
                        try {
                            date = DateForm_order.format(DateForm_db.parse(date));
                        } catch (ParseException ignored) {
                        }
                        float rating = user.getRating();

                        final OrderItem orderitem = new OrderItem(id, places, lugg, count_place,
                                date, time);
                        orderitem.setName(name);
                        orderitem.setTel(tel);
                        orderitem.setImage_person(image_person);
                        orderitem.setRating(rating);
                        orderitem.setImage_car(user.getImage_car());
                        orderitem.setCar_brand(user.getCar_brand());
                        orderitem.setCar_model(user.getCar_model());
                        orderitem.setCount_pass(order.getCount_pass());

                        if (passengers != null) {
                            for (Passenger passenger : passengers) {
                                String name_pass = passenger.getName();
                                String image_pass = passenger.getImage_person();
                                int id_pass = passenger.getId();

                                if (pref.getInt(Constants.ID, 0) == id) {
                                    reserve = true;
                                }

                                PassengerItem passengerItem = new PassengerItem(id_pass, name_pass, image_pass);
                                passengerItems.add(passengerItem);
                            }
                            orderitem.setPassengers(passengerItems);
                        }
                        orderitem.setReserve(reserve);

                        items.add(orderitem);
                    }

                    if (!items.isEmpty()) {
                        goToFoundOrders(items);
                    } else {
                        Snackbar.make(layout, Constants.RESULT_ERROR, Snackbar.LENGTH_LONG).show();
                        btn_find.setVisibility(View.VISIBLE);
                        load.setVisibility(View.GONE);
                    }
                } else {
                    Snackbar.make(layout, resp.getMessage(), Snackbar.LENGTH_LONG).show();
                    btn_find.setVisibility(View.VISIBLE);
                    load.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Snackbar.make(layout, t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                btn_find.setVisibility(View.VISIBLE);
                load.setVisibility(View.GONE);

            }


        });
    }

    private void goToFoundOrders(ArrayList<OrderItem> items) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(Constants.ORDER_ITEMS_LIST, items);
        Fragment fragment = new FoundOrderFragment();
        fragment.setArguments(bundle);
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment, fragment.getClass().toString())
                .commit();
    }

    protected void showPath() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.MAP_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MapRequestInterface requestInterface = retrofit.create(MapRequestInterface.class);

        String origin = String.valueOf(markers.get(0).getPosition().latitude) +
                "," + String.valueOf(markers.get(0).getPosition().longitude);

        String desc = String.valueOf(markers.get(1).getPosition().latitude) +
                "," + String.valueOf(markers.get(1).getPosition().longitude);

        Call<ServerResponse> response = requestInterface.data(origin, desc, "", "driving",
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

            btn_clear.setVisibility(View.VISIBLE);

            markers.add(marker);
            count++;
            temp_markers.add(mMap.addMarker(new MarkerOptions().position(marker.getPosition()).title("Пункт A")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))));

        } else if (count == 1) {

            markers.add(marker);
            temp_markers.add(mMap.addMarker(new MarkerOptions().position(marker.getPosition()).title("Пункт B")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))));
            count++;
            showPath();

            if (!anim_complete) {
                anim = AnimationUtils.loadAnimation(getContext(), R.anim.layout_order_slide);
                anim.setAnimationListener(this);
                layout_tools.startAnimation(anim);
                layout_tools.setVisibility(View.VISIBLE);
                anim_complete = true;
            }

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.find_order_tv_timepicker:
                final TimePicker timePicker = new TimePicker();
                final Dialog dialog = timePicker.get(getContext(), getActivity().getLayoutInflater());
                timePicker.setListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        time_db = timePicker.getTime();
                        tv_timepick.setText("Время: " + time_db);
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
            case R.id.find_order_btn_find:
                if (temp_markers.size() == 2) {

                    btn_find.setVisibility(View.GONE);
                    load.setVisibility(View.VISIBLE);
                    findOrder();

                } else {
                    Snackbar.make(layout, Constants.PLACE_ERROR_EMPTY, BaseTransientBottomBar.LENGTH_LONG).show();
                }
                break;
            case R.id.find_order_btn_clear:
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
