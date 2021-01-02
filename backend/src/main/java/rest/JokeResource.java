/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import DTOs.CombinedJokeDTO;
import DTOs.JokeDTO;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import entities.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import utils.EMF_Creator;
import utils.HttpUtils;
import utils.JokeFinder;

/**
 *
 * @author marcg
 */
@Path("jokes")
public class JokeResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static ExecutorService es = Executors.newCachedThreadPool();
    private static Gson GSON = new Gson();
    @Context
    private UriInfo context;

    @Context
    SecurityContext securityContext;

//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    public String getInfoForAll() {
//        return "{\"msg\":\"Hello anonymous\"}";
//    }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
//    @Path("jokes")
//    @RolesAllowed("user")
    public String getJokes() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        List<String> URLS = new ArrayList();
        URLS.add("https://geek-jokes.sameerkumar.website/api?format=json");
        URLS.add("https://matchilling-tronald-dump-v1.p.rapidapi.com/random/quote");
        URLS.add("https://sv443.net/jokeapi/v2/joke/Any?format=txt&type=single");
        URLS.add("https://api.chucknorris.io/jokes/random");
        URLS.add("https://icanhazdadjoke.com");

        List<JokeFinder> urls = new ArrayList();
        for (String string : URLS) {
            urls.add(new JokeFinder(string));
        }

        List<Future<String>> futures = new ArrayList();
        for (JokeFinder jf : urls) {
            JokeHandler jh = new JokeHandler(jf);
            futures.add(es.submit(jh));
        }
        List<String> results = new ArrayList();
        for (Future<String> f : futures) {
            results.add(f.get(10, TimeUnit.SECONDS));
            System.out.println(f.get());
        }
        JokeDTO joke = new JokeDTO(GSON.fromJson(results.get(0), JsonObject.class).get("joke").toString().split("\"")[1], URLS.get(0));
        JokeDTO joke1 = new JokeDTO(GSON.fromJson(results.get(1), JsonObject.class).get("value").toString().split("\"")[1], URLS.get(1));
        JokeDTO joke2 = new JokeDTO(results.get(2), URLS.get(2));
        JokeDTO joke3 = new JokeDTO(GSON.fromJson(results.get(3), JsonObject.class).get("value").toString().split("\"")[1], URLS.get(3));
        JokeDTO joke4 = new JokeDTO(GSON.fromJson(results.get(4), JsonObject.class).get("joke").toString().split("\"")[1], URLS.get(4));

        CombinedJokeDTO combi = new CombinedJokeDTO(joke, joke1, joke2, joke3, joke4);

        return GSON.toJson(combi);
    }

    class JokeHandler implements Callable<String> {

        JokeFinder tc;

        JokeHandler(JokeFinder tc) {
            this.tc = tc;
        }

        @Override
        public String call() throws Exception {
            tc.get();
            return new String(tc.getJson());
        }
    }
}
