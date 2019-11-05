package conj2019.horsemen.misc;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Visibility {
    public static void try1() throws IOException {
        final HttpUriRequest request = new HttpGet("http://localhost:3000/weapons");
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        System.out.println(response.getEntity().getContent());
    }

    //S/O to the rescue
    //https://stackoverflow.com/questions/18073849/get-a-json-object-from-a-http-response
    //Y U no workey???
    public static void try2() throws IOException {
        final HttpUriRequest request = new HttpGet("http://localhost:3000/weapons");
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
//        JSONObject myObject = new JSONObject(response);
//        System.out.println(json);
    }

    //S/O to the rescue
    //https://stackoverflow.com/questions/18073849/get-a-json-object-from-a-http-response
    //Works...if I want a string. Now what?
    public static void try3() throws IOException {
        final HttpUriRequest request = new HttpGet("http://localhost:3000/weapons");
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        String json = EntityUtils.toString(response.getEntity());
        System.out.println(json);
    }

    //Yet another Google search
    //https://www.programcreek.com/java-api-examples/org.apache.http.HttpResponse
    //Grr, what JsonObject and Json class do I need?
    //IDE pulls in import com.google.gson.JsonObject;
    //Example uses import javax.json.Json; import javax.json.JsonObject;
    //More google
    //https://stackoverflow.com/questions/30085721/inputstream-to-jsonobject-gson
    public static void try4() throws IOException {
        final HttpUriRequest request = new HttpGet("http://localhost:3000/weapons");
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        InputStream content = response.getEntity().getContent();
        //Incompatible...Do I do Gson or javax.json
        JsonObject responseJson = null; //Json.createReader(content).readObject();
    }

    //More google, S/O
    //https://stackoverflow.com/questions/30085721/inputstream-to-jsonobject-gson
    public static void try5() throws IOException {
        final HttpUriRequest request = new HttpGet("http://localhost:3000/weapons");
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        InputStream content = response.getEntity().getContent();
        Reader reader = new InputStreamReader(content, StandardCharsets.UTF_8);
        //Map<String, Collection<String>> result = new Gson().fromJson(reader, AreYouKiddingMe.class);
        Map result = new Gson().fromJson(reader, HashMap.class);
        System.out.println(result);
        System.out.println(result.keySet());
    }

    public static void try6() throws IOException, URISyntaxException {
        URIBuilder builder = new URIBuilder("http://localhost:3000/weapons")
                .addParameter("name", "War");
        final HttpUriRequest request = new HttpGet(builder.build());
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        InputStream content = response.getEntity().getContent();
        Reader reader = new InputStreamReader(content, StandardCharsets.UTF_8);
        Map result = new Gson().fromJson(reader, HashMap.class);
        System.out.println(result);
        System.out.println(result.keySet());
    }

    public static void main(String[] args) throws IOException {
        final HttpUriRequest request = new HttpGet("http://localhost:3000/weapons");
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        System.out.println(response.getEntity().getContent());
    }

//    public static void main(String[] args) throws IOException {
//        //Initial attempt
//        final HttpUriRequest request = new HttpGet("http://localhost:3000/weapons");
//        HttpResponse response = HttpClientBuilder.create().build().execute(request);
//        System.out.println(response.getEntity().getContent());
//
////        try1();
////        try2();
////        try3();
////        try4();
////        try5();
////        try6();
//    }
}
