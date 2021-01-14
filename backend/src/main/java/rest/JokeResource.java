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
import facades.APIFacade;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import utils.EMF_Creator;
import utils.Env;
import utils.FetchFinder;

/**
 *
 * @author marcg
 */
@Path("jokes")
public class JokeResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static ExecutorService es = Executors.newCachedThreadPool();
    private static Gson GSON = new Gson();
    private static APIFacade api = APIFacade.getUserFacade(es);
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
        Env ENV = Env.GetEnv();
        
        HashMap<String, ArrayList<String>> map = new HashMap();
        map.put("1", arrayMakker("joke", ENV.jokeURL1));
        map.put("2", arrayMakker("value", ENV.jokeURL2));
        map.put("3", arrayMakker(null, ENV.jokeURL3));
        map.put("4", arrayMakker("value", ENV.jokeURL4));
        map.put("5", arrayMakker("joke", ENV.jokeURL5));
        Map<String, String> data = api.getProcessedData(map);
        
        return GSON.toJson(data);
    }

    private ArrayList<String> arrayMakker(String val1, String val2) {
        ArrayList<String> arr = new ArrayList();
        arr.add(val1);
        arr.add(val2);
        return arr;
    }
}
