/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DataProcesses;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.Map;

/**
 *
 * @author marcg
 */
public class SingleValue implements Processes{
    private static Gson GSON = new Gson();

    @Override
    public String process(String joke, String identifier) {        
//        System.out.println(joke);
        String res = GSON.fromJson(joke, JsonObject.class).get(identifier).toString().split("\"")[1];
        System.out.println(res + " : " + identifier);
        return res;
    }
    
}
