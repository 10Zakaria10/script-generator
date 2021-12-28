package client;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.List;

public interface ByTable {
    @GET("{baseUrl}/restaurants/{restaurantId}/floor_plan")
    Call<List<IcgTable>> getAll(@Path(value = "baseUrl", encoded = true) String baseUrl,
                               @Path(value = "restaurantId", encoded = true) String restaurantId
    );
}
