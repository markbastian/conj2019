package conj2019.hello_client;

import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class GetGreeting {
    public static void main(String[] args) throws IOException {
        final HttpUriRequest request = new HttpGet("http://localhost:3000/greeting?name=543");
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        InputStream content = response.getEntity().getContent();
        Reader reader = new InputStreamReader(content, StandardCharsets.UTF_8);
        Map result = new Gson().fromJson(reader, HashMap.class);
        System.out.println(result);
    }
}