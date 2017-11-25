package ru.shuttlecar.shuttlecar.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.shuttlecar.shuttlecar.models.Constants;

public interface MapRequestInterface {
    @GET(Constants.MAP_REQUEST)
    Call<ServerResponse> data(@Query("origin") String origin, @Query("destination") String destination, @Query("waypoints") String waypoints,
                              @Query("mode") String mode,  @Query("language") String language, @Query("key") String key);


}
