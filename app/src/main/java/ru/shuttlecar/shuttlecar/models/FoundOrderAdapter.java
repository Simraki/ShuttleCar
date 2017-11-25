package ru.shuttlecar.shuttlecar.models;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ru.shuttlecar.shuttlecar.FoundOrderActivity;
import ru.shuttlecar.shuttlecar.HelpMethods;
import ru.shuttlecar.shuttlecar.R;

public class FoundOrderAdapter extends ArrayAdapter<OrderItem> {
    private ViewHolder viewHold;


    public FoundOrderAdapter(Context context, List<OrderItem> objects) {
        super(context, 0, objects);
    }

    private static class ViewHolder {
        int id_order;

        LinearLayout layout;

        TextView tv_date,
                tv_time,
                tv_places,
                tv_passengers,
                tv_countplace,
                tv_name;

        CircleImageView image;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;
        final OrderItem item = getItem(position);

        if (convertView == null) {
            viewHolder = new ViewHolder();
            viewHold = viewHolder;
            LayoutInflater vi = LayoutInflater.from(getContext());
            convertView = vi.inflate(R.layout.found_order_title, parent, false);

            if (item != null) {
                viewHolder.id_order = item.getId();
            }

            viewHolder.layout = (LinearLayout) convertView.findViewById(R.id.found_order_title);
            viewHolder.layout.setClickable(true);

            viewHolder.tv_date = (TextView) convertView.findViewById(R.id.found_order_title_tv_date);
            viewHolder.tv_time = (TextView) convertView.findViewById(R.id.found_order_title_tv_time);
            viewHolder.tv_places = (TextView) convertView.findViewById(R.id.found_order_title_tv_places);
            viewHolder.tv_passengers = (TextView) convertView.findViewById(R.id.found_order_title_tv_passengers);
            viewHolder.tv_countplace = (TextView) convertView.findViewById(R.id.found_order_title_tv_countplace);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.found_order_title_tv_name);

            viewHolder.image = (CircleImageView) convertView.findViewById(R.id.found_order_title_civ_person);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String places_text = null;
        String[] places = new String[0];
        if (item != null) {
            places = item.getPlaces();
        }

        for (int i = 0; i < places.length; i++) {
            if (i != 0) {
                places_text = places_text + " >> " + places[i];
            } else {
                places_text = places[0];
            }
        }

        if (item != null) {
            HelpMethods.downloadImage(getContext(), item.getImage_person(), viewHolder.image);
            viewHolder.tv_time.setText(item.getTime());
            viewHolder.tv_passengers.setText("Пассажиров: " + String.valueOf(item.getCount_pass()));
            viewHolder.tv_countplace.setText("Мест: " + String.valueOf(item.getCount_place()));
            viewHolder.tv_name.setText(item.getName());
        } else {
            viewHolder.tv_time.setText("Error");
            viewHolder.tv_passengers.setText("Error");
            viewHolder.tv_countplace.setText("Error");
            viewHolder.tv_name.setText("Error");
        }
        viewHolder.tv_date.setVisibility(View.GONE);
        viewHolder.tv_places.setText(places_text);

        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FoundOrderActivity.class);
                intent.putExtra(Constants.ORDER_ITEM, item);
                getContext().startActivity(intent);
            }
        });

        return convertView;
    }
}