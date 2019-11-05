package conj2019.horsemen.misc;

import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class FamiliarityRequest {
    public static void main(String[] args) throws IOException, URISyntaxException {
        URIBuilder builder = new URIBuilder("http://localhost:3000/weapons")
                .addParameter("name", "Famine");
        final HttpUriRequest request = new HttpGet(builder.build());
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        InputStream content = response.getEntity().getContent();
        Reader reader = new InputStreamReader(content, StandardCharsets.UTF_8);
        Map result = new Gson().fromJson(reader, HashMap.class);
        System.out.println(result);
    }
}
