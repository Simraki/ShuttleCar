package ru.shuttlecar.shuttlecar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.iarcuschin.simpleratingbar.SimpleRatingBar;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.shuttlecar.shuttlecar.models.Constants;
import ru.shuttlecar.shuttlecar.models.OrderItem;
import ru.shuttlecar.shuttlecar.models.PassengerItem;
import ru.shuttlecar.shuttlecar.models.PassengersAdapter;
import ru.shuttlecar.shuttlecar.network.Order;
import ru.shuttlecar.shuttlecar.network.ServerRequest;
import ru.shuttlecar.shuttlecar.network.ServerRequestInterface;
import ru.shuttlecar.shuttlecar.network.ServerResponse;
import ru.shuttlecar.shuttlecar.network.User;

public class FoundOrderActivity extends AppCompatActivity implements View.OnClickListener {

    private OrderItem item;
    private SharedPreferences pref;
    private Button btn_reserve;
    private ListView lv;
    private LinearLayout linearLayout;
    private TextView tv_countplace;

    private PassengerItem old_item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found_order);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_foundorder);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(this);
        setTitle(R.string.title_order);

        item = getIntent().getParcelableExtra(Constants.ORDER_ITEM);

        initViews();
    }

    private void initViews() {
        String pdis = null, pdel = null, inter_one = null, inter_two = null, inter_three = null;

        String[] places = item.getPlaces();
        int size = places.length;
        if (size == 2) {
            pdis = places[0];
            pdel = places[1];
        } else if (size == 3) {
            pdis = places[0];
            inter_one = places[1];
            pdel = places[2];
        } else if (size == 4) {
            pdis = places[0];
            inter_one = places[1];
            inter_two = places[2];
            pdel = places[3];
        } else if (size == 5) {
            pdis = places[0];
            inter_one = places[1];
            inter_two = places[2];
            inter_three = places[3];
            pdel = places[4];
        }

        TextView tv_name = (TextView) findViewById(R.id.found_order_content_tv_name);
        TextView tv_date = (TextView) findViewById(R.id.found_order_content_tv_datetime);
        TextView tv_pdis = (TextView) findViewById(R.id.found_order_content_tv_pdis);
        TextView tv_interone = (TextView) findViewById(R.id.found_order_content_tv_interone);
        TextView tv_intertwo = (TextView) findViewById(R.id.found_order_content_tv_intertwo);
        TextView tv_interthree = (TextView) findViewById(R.id.found_order_content_tv_interthree);
        TextView tv_pdel = (TextView) findViewById(R.id.found_order_content_tv_pdel);
        TextView tv_carbrand = (TextView) findViewById(R.id.found_order_content_tv_carbrand);
        TextView tv_carmodel = (TextView) findViewById(R.id.found_order_content_tv_carmodel);
        tv_countplace = (TextView) findViewById(R.id.found_order_content_tv_countplace);
        TextView tv_lugg = (TextView) findViewById(R.id.found_order_content_tv_lugg);
        lv = (ListView) findViewById(R.id.found_order_lv_orders);
        linearLayout = (LinearLayout) findViewById(R.id.found_order_layout_pass);

        LinearLayout layout = (LinearLayout) findViewById(R.id.found_order_content_layout_car);
        View view = findViewById(R.id.found_order_content_view_car);

        CircleImageView image_person = (CircleImageView) findViewById(R.id.found_order_content_civ_person);
        CircleImageView image_car = (CircleImageView) findViewById(R.id.found_order_content_civ_car);

        SimpleRatingBar rating = (SimpleRatingBar) findViewById(R.id.found_order_content_srb_rating);

        Button btn_call = (Button) findViewById(R.id.found_order_content_btn_call);
        btn_reserve = (Button) findViewById(R.id.found_order_content_btn_reserve);

        tv_name.setText(item.getName());
        tv_date.setText(item.getTime() + ", " + item.getDate());

        tv_pdis.setText(pdis);
        if (inter_one != null) {
            tv_interone.setText(inter_one);
        } else {
            tv_interone.setVisibility(View.GONE);
        }
        if (inter_two != null) {
            tv_intertwo.setText(inter_two);
        } else {
            tv_intertwo.setVisibility(View.GONE);
        }
        if (inter_three != null) {
            tv_interthree.setText(inter_three);
        } else {
            tv_interthree.setVisibility(View.GONE);
        }
        tv_pdel.setText(pdel);

        if ((item.getCar_model() != null && !item.getCar_model().isEmpty())
                || (item.getCar_brand() != null && !item.getCar_brand().isEmpty())
                || (item.getImage_car() != null && !item.getImage_car().isEmpty())) {
            layout.setVisibility(View.VISIBLE);
            view.setVisibility(View.VISIBLE);
            tv_carbrand.setText(item.getCar_brand());
            tv_carmodel.setText(item.getCar_model());
            if (item.getImage_car() != null && !item.getImage_car().isEmpty()) {
                HelpMethods.downloadImage(this, item.getImage_car(), image_car);
            }
        }

        refresh();
        tv_lugg.setText(item.getLuggage_size());

        HelpMethods.downloadImage(this, item.getImage_person(), image_person);

        rating.setRating(item.getRating());

        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "tel:" + item.getTel();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            }
        });

        if (item.getPassengers() != null && !item.getPassengers().isEmpty()) {
            linearLayout.setVisibility(View.VISIBLE);
            PassengersAdapter adapter = new PassengersAdapter(this, item.getPassengers());

            lv.setAdapter(adapter);
        }

        setState(item.getReserve());

    }

    @Override
    public void onClick(View v) {
        invalidateOptionsMenu();
        onBackPressed();
    }

    private void setState(boolean reserve) {
        if (reserve) {
            btn_reserve.setText("Отменить бронь");
            btn_reserve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteReserveOrder();
                }
            });
        } else {
            btn_reserve.setText("Забронировать");
            btn_reserve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reserveOrder();
                }
            });
        }
    }

    private void deleteReserveOrder() {
        pref = getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE);
        ServerRequestInterface serverRequestInterface = HelpMethods.getRetrofit().create(ServerRequestInterface.class);

        User user = new User();
        user.setEmail(pref.getString(Constants.EMAIL, ""));
        user.setUnique_id(pref.getString(Constants.UNIQUE_ID, ""));

        Order order = new Order();
        order.setId(item.getId());

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.DELETE_RESERVE_ORDER);
        request.setUser(user);
        request.setOrder(order);
        Call<ServerResponse> response = serverRequestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {

                ServerResponse resp = response.body();

                if (Constants.RESULT_SUCCESS.equals(resp.getResult())) {
                    setState(false);
                    deleteMeFromPassengers();
                    Snackbar.make(btn_reserve, Constants.RESULT_COMPLETE, Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(btn_reserve, Constants.RESULT_ERROR, Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Snackbar.make(btn_reserve, Constants.RESULT_ERROR, Snackbar.LENGTH_LONG).show();
            }
        });


    }

    private void reserveOrder() {
        pref = getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE);
        ServerRequestInterface serverRequestInterface = HelpMethods.getRetrofit().create(ServerRequestInterface.class);

        User user = new User();
        user.setEmail(pref.getString(Constants.EMAIL, ""));
        user.setUnique_id(pref.getString(Constants.UNIQUE_ID, ""));

        Order order = new Order();
        order.setId(item.getId());

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.RESERVE_ORDER);
        request.setUser(user);
        request.setOrder(order);
        Call<ServerResponse> response = serverRequestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {

                ServerResponse resp = response.body();

                if (Constants.RESULT_SUCCESS.equals(resp.getResult())) {
                    setState(true);
                    addMeToPassengers();
                    Snackbar.make(btn_reserve, Constants.RESULT_COMPLETE, Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(btn_reserve, Constants.RESULT_ERROR, Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Snackbar.make(btn_reserve, Constants.RESULT_ERROR, Snackbar.LENGTH_LONG).show();
            }
        });


    }

    private void deleteMeFromPassengers() {
        ArrayList<PassengerItem> passengers = item.getPassengers();
        for (PassengerItem passenger : passengers) {
            if (pref.getInt(Constants.ID, 0) == passenger.getId()) {
                old_item = passenger;
                passengers.remove(passenger);
            }
        }

        if (passengers == null || passengers.isEmpty()) {
            linearLayout.setVisibility(View.GONE);
        } else {
            item.setReserve(false);
            item.setCount_pass(item.getCount_pass() - 1);
            item.setPassengers(passengers);
            PassengersAdapter adapter = new PassengersAdapter(this, item.getPassengers());
            lv.setAdapter(adapter);
            refresh();
        }
    }

    private void addMeToPassengers() {
        if (old_item == null) {
            old_item = new PassengerItem(pref.getInt(Constants.ID, 0),
                    pref.getString(Constants.NAME, ""), pref.getString(Constants.PHOTO_PERSON, ""));
        } else {
            ArrayList<PassengerItem> passengers = item.getPassengers();
            passengers.add(old_item);
            item.setReserve(true);
            item.setCount_pass(item.getCount_pass() + 1);
            item.setPassengers(passengers);
            if (linearLayout != null && linearLayout.getVisibility() == View.GONE) {
                linearLayout.setVisibility(View.VISIBLE);
            }
            PassengersAdapter adapter = new PassengersAdapter(this, item.getPassengers());
            lv.setAdapter(adapter);
            refresh();
        }
    }

    private void refresh() {
        tv_countplace.setText(String.valueOf(item.getCount_pass()) + " из " + String.valueOf(item.getCount_place()));
    }
}