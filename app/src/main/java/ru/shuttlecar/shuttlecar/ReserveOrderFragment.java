package ru.shuttlecar.shuttlecar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.shuttlecar.shuttlecar.models.Constants;
import ru.shuttlecar.shuttlecar.models.FoundOrderAdapter;
import ru.shuttlecar.shuttlecar.models.OrderItem;
import ru.shuttlecar.shuttlecar.models.PassengerItem;
import ru.shuttlecar.shuttlecar.network.Order;
import ru.shuttlecar.shuttlecar.network.Passenger;
import ru.shuttlecar.shuttlecar.network.ServerRequest;
import ru.shuttlecar.shuttlecar.network.ServerRequestInterface;
import ru.shuttlecar.shuttlecar.network.ServerResponse;
import ru.shuttlecar.shuttlecar.network.User;

public class ReserveOrderFragment extends Fragment {
    private SharedPreferences pref;
    private ListView list;
    private ProgressBar pb;
    private CoordinatorLayout layout;
    private FoundOrderAdapter adapter;

    private ArrayList<OrderItem> items = new ArrayList<>();
    private SimpleDateFormat DateForm_order, date_db;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(R.string.subtitle_reserve);

        View view = inflater.inflate(R.layout.fragment_reserve_order, container, false);
        initViews(view);
        return view;
    }

    @SuppressLint("SimpleDateFormat")
    private void initViews(View view) {
        DateForm_order = new SimpleDateFormat(getContext().getString(R.string.form_date_text));
        date_db = new SimpleDateFormat(getContext().getString(R.string.form_date_db));

        layout = (CoordinatorLayout) view.findViewById(R.id.reserve_order);
        list = (ListView) view.findViewById(R.id.reserve_order_lv_orders);
        pb = (ProgressBar) view.findViewById(R.id.reserve_order_pb_load);
        pb.setIndeterminate(true);
        pb.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);

        loadReserveOrders();

    }

    private void loadReserveOrders() {
        pref = getActivity().getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
        ServerRequestInterface serverRequestInterface = HelpMethods.getRetrofit().create(ServerRequestInterface.class);

        final User user = new User();
        user.setEmail(pref.getString(Constants.EMAIL, ""));
        user.setUnique_id(pref.getString(Constants.UNIQUE_ID, ""));

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.FIND_RESERVE_ORDER_OPERATION);
        request.setUser(user);
        Call<ServerResponse> response = serverRequestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse resp = response.body();

                if (Constants.RESULT_SUCCESS.equals(resp.getResult())) {
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
                                date = null;
                        float rating = user.getRating();

                        try {
                            date = DateForm_order.format(date_db.parse(order.getDate()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (date != null) {
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
                    }
                    if (!items.isEmpty() && items != null) {
                        pb.setVisibility(View.GONE);
                        adapter = new FoundOrderAdapter(getActivity(), items);
                        list.setAdapter(adapter);
                        list.setVisibility(View.VISIBLE);
                        LayoutAnimationController controller = AnimationUtils
                                .loadLayoutAnimation(getActivity(), R.anim.my_order_list);
                        list.setLayoutAnimation(controller);
                    }
                } else {
                    if (getView() != null) {
                        pb.setVisibility(View.GONE);
                        if (resp.getMessage() != null) {
                            Snackbar.make(layout, resp.getMessage(), Snackbar.LENGTH_LONG).show();
                        } else {
                            Snackbar.make(layout, Constants.RESULT_ERROR, Snackbar.LENGTH_LONG).show();
                        }
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