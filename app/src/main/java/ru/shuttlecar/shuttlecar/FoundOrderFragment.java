package ru.shuttlecar.shuttlecar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import ru.shuttlecar.shuttlecar.models.Constants;
import ru.shuttlecar.shuttlecar.models.FoundOrderAdapter;
import ru.shuttlecar.shuttlecar.models.OrderItem;

public class FoundOrderFragment extends Fragment {
    private ArrayList<OrderItem> items;
    private FoundOrderAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(R.string.subtitle_found);

        Bundle bundle = getArguments();
        if (!bundle.isEmpty()) {
            items = bundle.getParcelableArrayList(Constants.ORDER_ITEMS_LIST);
        }

        View view = inflater.inflate(R.layout.fragment_found_order, null);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        adapter = new FoundOrderAdapter(getActivity(), items);

        ListView list = (ListView) view.findViewById(R.id.list_found_order);
        list.setAdapter(adapter);
    }
}
