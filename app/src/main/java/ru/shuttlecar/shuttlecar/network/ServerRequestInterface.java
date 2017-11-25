package ru.shuttlecar.shuttlecar.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import ru.shuttlecar.shuttlecar.models.Constants;

public interface ServerRequestInterface {

    @POST(Constants.BASE_URL)
    Call<ServerResponse> operation(@Body ServerRequest request);
}
