/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.IOException;

/**
 *
 * @author marcg
 */
public class JokeFinder {
    private String url;
    private String json;

    public JokeFinder(String url) {
        this.url = url;
    }
    
    public void get() throws IOException {
        json = HttpUtils.fetchData(url);
    }

    public String getUrl() {
        return url;
    }

    public String getJson() {
        return json;
    }
    
    
    
    
}
