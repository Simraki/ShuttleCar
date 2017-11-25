package ru.shuttlecar.shuttlecar.models;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ru.shuttlecar.shuttlecar.HelpMethods;
import ru.shuttlecar.shuttlecar.R;

public class PassengersAdapter extends ArrayAdapter<PassengerItem> {


    public PassengersAdapter(Context context, List<PassengerItem> objects) {
        super(context, 0, objects);
    }

    private static class ViewHolder {
        TextView tv_name;

        CircleImageView civ_person;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;
        final PassengerItem item = getItem(position);

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater vi = LayoutInflater.from(getContext());
            convertView = vi.inflate(R.layout.passenger_single, parent, false);

            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.passenger_tv_name);
            viewHolder.civ_person = (CircleImageView) convertView.findViewById(R.id.passenger_civ_person);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (item != null) {
            viewHolder.tv_name.setText(item.getName());
        } else {
            viewHolder.tv_name.setText("Error");
        }

        if (item != null && !item.getImage_person().isEmpty()) {
            HelpMethods.downloadImage(getContext(), item.getImage_person(), viewHolder.civ_person);
        } else {
            viewHolder.civ_person.setImageResource(R.drawable.ic_default_profile);
        }

        return convertView;
    }

}