package conj2019.maven;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.net.URISyntaxException;

public class Application {
    public static void main(String[] args) throws IOException, URISyntaxException {
        URIBuilder builder = new URIBuilder("http://search.maven.org/solrsearch/select");
        builder.setParameter("q", "g:org.clojure a:clojure");

        final HttpUriRequest request = new HttpGet(builder.build());
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        System.out.println(response);
        System.out.println(response.getEntity());
        System.out.println(response.getEntity().getContent());
    }
}
