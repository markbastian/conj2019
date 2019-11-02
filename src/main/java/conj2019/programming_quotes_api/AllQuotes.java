package conj2019.programming_quotes_api;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

public class AllQuotes {
    public static void main(String[] args) throws IOException {
        final HttpUriRequest request = new HttpGet("https://programming-quotes-api.herokuapp.com/quotes");
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        System.out.println(response.getEntity().getContent());
    }
}
