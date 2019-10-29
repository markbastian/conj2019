package hello1;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

public class GreetingMain {
    public static void main(String[] args) throws IOException {
        GreetingController greetingController = new GreetingController();
        System.out.println(greetingController.greeting("Mark"));

        final HttpUriRequest request = new HttpGet("http://localhost:8083/greeting?name=543");
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        System.out.println(response.getEntity().getContent());
    }
}
