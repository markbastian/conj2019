package conj2019.invoke_endpoint;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

public class GetGreeting {
    public static void main(String[] args) throws IOException {
        final HttpUriRequest request = new HttpGet("http://localhost:8083/greeting?name=543");
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        System.out.println(response.getEntity().getContent());
    }
}
