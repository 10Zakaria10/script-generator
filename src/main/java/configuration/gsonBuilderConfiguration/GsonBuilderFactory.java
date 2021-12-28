package configuration.gsonBuilderConfiguration;

import com.google.gson.*;
import io.vavr.gson.VavrGson;

import java.lang.reflect.Type;
import java.time.Instant;

public class GsonBuilderFactory {
    public static GsonBuilder gsonBuilder() {
        GsonBuilder builder = new GsonBuilder()
                .registerTypeAdapterFactory(new RecordTypeAdapterFactory())
                .registerTypeAdapter(Instant.class, new InstantDeserializer());
        VavrGson.registerAll(builder);
        return builder;
    }

    public static class InstantDeserializer implements JsonDeserializer<Instant>, JsonSerializer<Instant> {
        @Override
        public JsonElement serialize(Instant src, Type srcType, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }

        @Override
        public Instant deserialize(JsonElement json, Type type, JsonDeserializationContext context)
                throws JsonParseException {
            return Instant.parse(json.getAsJsonPrimitive().getAsString());
        }
    }
}
