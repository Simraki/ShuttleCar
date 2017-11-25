package ru.shuttlecar.shuttlecar.models;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.shuttlecar.shuttlecar.HelpMethods;
import ru.shuttlecar.shuttlecar.R;
import ru.shuttlecar.shuttlecar.network.Order;
import ru.shuttlecar.shuttlecar.network.ServerRequest;
import ru.shuttlecar.shuttlecar.network.ServerRequestInterface;
import ru.shuttlecar.shuttlecar.network.ServerResponse;
import ru.shuttlecar.shuttlecar.network.User;

public class MyOrderAdapter extends ArrayAdapter<OrderItem> {
    private List<OrderItem> list;


    public MyOrderAdapter(Context context, List<OrderItem> objects) {
        super(context, 0, objects);
        list = objects;
    }

    private static class ViewHolder {
        int id_order;

        TextView tv_datetime,
                tv_pdis,
                tv_interone,
                tv_intertwo,
                tv_interthree,
                tv_pdel,
                tv_countplace,
                tv_lugg;

        ImageButton btn_delete;
        ListView lv_passengers;
        LinearLayout layout_passengers;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;
        final OrderItem item = getItem(position);

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater vi = LayoutInflater.from(getContext());
            convertView = vi.inflate(R.layout.my_order, parent, false);

            viewHolder.tv_datetime = (TextView) convertView.findViewById(R.id.my_order_tv_datetime);
            viewHolder.tv_pdis = (TextView) convertView.findViewById(R.id.my_order_tv_pdis);
            viewHolder.tv_interone = (TextView) convertView.findViewById(R.id.my_order_tv_interone);
            viewHolder.tv_intertwo = (TextView) convertView.findViewById(R.id.my_order_tv_intertwo);
            viewHolder.tv_interthree = (TextView) convertView.findViewById(R.id.my_order_tv_interthree);
            viewHolder.tv_pdel = (TextView) convertView.findViewById(R.id.my_order_tv_pdel);
            viewHolder.tv_countplace = (TextView) convertView.findViewById(R.id.my_order_tv_countplace);
            viewHolder.tv_lugg = (TextView) convertView.findViewById(R.id.my_order_tv_lugg);
            viewHolder.lv_passengers = (ListView) convertView.findViewById(R.id.my_order_lv_passengers);
            viewHolder.layout_passengers = (LinearLayout) convertView.findViewById(R.id.my_order_layout_passengers);

            viewHolder.btn_delete = (ImageButton) convertView.findViewById(R.id.my_order_btn_delete);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        int count;
        if (item != null) {
            count = item.getCount_place() - item.getCount_pass();
            viewHolder.id_order = item.getId();
            viewHolder.tv_datetime.setText(item.getTime());
            viewHolder.tv_countplace.setText(String.valueOf(count) + " из " + String.valueOf(item.getCount_place()));
            viewHolder.tv_lugg.setText(item.getLuggage_size());

            String[] places = item.getPlaces();
            setPlaces(places, viewHolder);

            if (item.getPassengers() != null && !item.getPassengers().isEmpty()) {
                viewHolder.layout_passengers.setVisibility(View.VISIBLE);
                PassengersAdapter passengersAdapter = new PassengersAdapter(getContext(), item.getPassengers());
                viewHolder.lv_passengers.setAdapter(passengersAdapter);
            }
        } else {
            viewHolder.tv_datetime.setText("Error");
            viewHolder.tv_countplace.setText("Error");
            viewHolder.tv_lugg.setText("Error");
        }


        viewHolder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDeleteOrder(position, viewHolder);
            }
        });

        return convertView;
    }

    private void setPlaces(String[] places, ViewHolder viewHolder) {
        String pdis = null, pdel = null, inter_one = null, inter_two = null, inter_three = null;

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

        viewHolder.tv_pdis.setText(pdis);
        if (inter_one != null) {
            viewHolder.tv_interone.setText(inter_one);
        } else {
            viewHolder.tv_interone.setVisibility(View.GONE);
        }
        if (inter_two != null) {
            viewHolder.tv_intertwo.setText(inter_two);
        } else {
            viewHolder.tv_intertwo.setVisibility(View.GONE);
        }
        if (inter_three != null) {
            viewHolder.tv_interthree.setText(inter_three);
        } else {
            viewHolder.tv_interthree.setVisibility(View.GONE);
        }
        viewHolder.tv_pdel.setText(pdel);

    }

    private void dialogDeleteOrder(final int position, final ViewHolder holder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.dialog_delete_order);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteOrder(holder, position);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteOrder(final ViewHolder holder, final int position) {
        final AppCompatActivity activity = ((AppCompatActivity) getContext());
        SharedPreferences pref = activity.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
        ServerRequestInterface serverRequestInterface = HelpMethods.getRetrofit().create(ServerRequestInterface.class);

        User user = new User();
        user.setEmail(pref.getString(Constants.EMAIL, ""));
        user.setUnique_id(pref.getString(Constants.UNIQUE_ID, ""));

        Order order = new Order();
        order.setId(holder.id_order);

        Log.d("Lol", String.valueOf(holder.id_order));

        final ServerRequest request = new ServerRequest();
        request.setOperation(Constants.DELETE_ORDER_OPERATION);
        request.setOrder(order);
        request.setUser(user);
        Call<ServerResponse> response = serverRequestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                list.remove(position);
                notifyDataSetChanged();
                Log.d("Lol", "OK");
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                notifyDataSetChanged();
                Log.i("Lol", "err", t);
            }
        });

    }

}