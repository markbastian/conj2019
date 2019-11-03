package conj2019.horsemen.misc;

import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FamiliarityRequest {
    public static void main(String[] args) throws IOException {
        final HttpUriRequest request = new HttpGet("http://localhost:3000/weapons?name=Famine");
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        InputStream content = response.getEntity().getContent();
        Reader reader = new InputStreamReader(content, "UTF-8");
        Map<String, Collection<String>> result = new Gson().fromJson(reader, HashMap.class);
        System.out.println(result);
    }
}
