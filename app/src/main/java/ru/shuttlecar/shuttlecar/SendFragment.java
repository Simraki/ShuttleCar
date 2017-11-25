package ru.shuttlecar.shuttlecar;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.dd.CircularProgressButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.shuttlecar.shuttlecar.models.Constants;
import ru.shuttlecar.shuttlecar.network.Message;
import ru.shuttlecar.shuttlecar.network.ServerRequest;
import ru.shuttlecar.shuttlecar.network.ServerRequestInterface;
import ru.shuttlecar.shuttlecar.network.ServerResponse;

public class SendFragment extends Fragment {
    private CircularProgressButton btn_send;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_send);

        View view = inflater.inflate(R.layout.fragment_send, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        final EditText et_message = (EditText) view.findViewById(R.id.send_et_message);

        btn_send = (CircularProgressButton) view.findViewById(R.id.send_cbtn_send);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_send.setIndeterminateProgressMode(true);
                if (!et_message.getText().toString().isEmpty()) {
                    btn_send.setClickable(false);
                    btn_send.setProgress(50);
                    sendMessage(et_message.getText().toString());
                } else {
                    btn_send.setClickable(false);
                    btn_send.setProgress(50);
                    HelpMethods.errorShow(btn_send);
                }
            }
        });
    }

    private void sendMessage(String text) {
        ServerRequestInterface serverRequestInterface = HelpMethods.getRetrofit().create(ServerRequestInterface.class);

        Message message = new Message();
        message.setMessage(text);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.SEND_MESSAGE);
        request.setMessage(message);

        Call<ServerResponse> response = serverRequestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse resp = response.body();

                if (Constants.RESULT_SUCCESS.equals(resp.getResult())) {
                    Snackbar.make(getView(), Constants.RESULT_COMPLETE, Snackbar.LENGTH_LONG).show();
                    btn_send.setProgress(100);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            btn_send.setProgress(0);
                            btn_send.setClickable(true);
                            getActivity().onBackPressed();
                        }
                    }, 500);
                } else {
                    Snackbar.make(getView(), Constants.RESULT_ERROR, Snackbar.LENGTH_LONG).show();
                    HelpMethods.errorShow(btn_send);
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Snackbar.make(getView(), Constants.RESULT_ERROR, Snackbar.LENGTH_LONG).show();
                HelpMethods.errorShow(btn_send);
            }
        });
    }
}
