package configuration.retrofit;

import configuration.gsonBuilderConfiguration.GsonBuilderFactory;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitConfiguration {
    public static Retrofit retrofit(String baseUrl) {
        var client = new OkHttpClient.Builder()
                .build();

        return new Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilderFactory.gsonBuilder()
                        .create()))
                .baseUrl(baseUrl)
                .build();
    }
}
