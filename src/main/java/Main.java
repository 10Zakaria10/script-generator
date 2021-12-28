import client.ByTable;
import client.IcgTable;
import configuration.retrofit.RetrofitConfiguration;
import io.vavr.Tuple;
import io.vavr.Tuple4;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        var tupleOfBaseUrlAndVenueId = getInformation();
        var baseUrl = tupleOfBaseUrlAndVenueId._1;
        var venueId = tupleOfBaseUrlAndVenueId._2;
        var restaurantId = tupleOfBaseUrlAndVenueId._3;
        var path = tupleOfBaseUrlAndVenueId._4;

        var retrofit = RetrofitConfiguration.retrofit(baseUrl);
        var byTable = retrofit.create(ByTable.class);
        var call = byTable.getAll(baseUrl, restaurantId);
        var floorPlan = call.execute().body();
        generateScriptInPath(path, floorPlan, venueId);
    }

    private static Tuple4<String, String, String, String> getInformation() {
        Scanner myObj = new Scanner(System.in);
        System.out.println("Enter baseUrl (by default = beta url)");
        String baseUrl = myObj.nextLine();
        System.out.println("Enter internal internal venueId (by default = random uuid)");
        String venueId = myObj.nextLine();
        System.out.println("Enter external venue id  (by default = 3) ");
        String restaurantId = myObj.nextLine();
        System.out.println("Enter locatiotion of generated script (by default = generated/script.sql)");
        String path = myObj.nextLine();

        venueId = venueId.isEmpty() ? "76768e52-1467-4a8d-a142-fde3b19a47cf" : venueId;
        baseUrl = baseUrl.isEmpty() ? "https://jrygwfma0h.execute-api.eu-west-1.amazonaws.com/beta/" : baseUrl;
        restaurantId = restaurantId.isEmpty() ? "3" : restaurantId;
        path = path.isEmpty() ? "generated/script.sql" : path;

        if(baseUrl.charAt(baseUrl.length()-1) != '/')
            baseUrl = baseUrl.concat("/");

        return Tuple.of(baseUrl, venueId, restaurantId,path);
    }

    private static void generateScriptInPath(String filePath, List<IcgTable> floorPlan, String venueId) throws IOException {
        Path path = Paths.get(filePath);
        Files.createDirectories(path.getParent());
        if (!Files.exists(path))
            Files.createFile(path);

        BufferedWriter writer = Files.newBufferedWriter(path);

        writer.write(("BEGIN;" + System.lineSeparator()));
        for (IcgTable icgTable : floorPlan) {
            String script = getScript(icgTable, venueId);
            writer.write(script + System.lineSeparator());
        }
        writer.write(("COMMIT;"));

        writer.close();
    }

    private static String getScript(IcgTable icgTable, String venueId) {
        String externalTableId = String.valueOf(icgTable.table_id());
        String combinedId = icgTable.room_id() + ":" + externalTableId;
        return String.format("update vpos_table set external_table_id = '%s' where external_table_id= '%s' and venue_id ='%s';", combinedId, externalTableId, venueId);
    }
}
