/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

/**
 *
 * @author marcg
 */
public class Env {

    private static Env intance = null;

    public static String jokeURL1 = "https://geek-jokes.sameerkumar.website/api?format=json";
    public static String jokeURL2 = "https://matchilling-tronald-dump-v1.p.rapidapi.com/random/quote";
    public static String jokeURL3 = "https://sv443.net/jokeapi/v2/joke/Any?format=txt&type=single";
    public static String jokeURL4 = "https://api.chucknorris.io/jokes/random";
    public static String jokeURL5 = "https://icanhazdadjoke.com";
    public static String secret = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static String aseDatabae = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static String aseWeb = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";

    public Env() {
        if (System.getenv("DEPLOYED") != null) {
            jokeURL1 = System.getenv("JOKE_URL_1");
            jokeURL2 = System.getenv("JOKE_URL_2");
            jokeURL3 = System.getenv("JOKE_URL_3");
            jokeURL4 = System.getenv("JOKE_URL_4");
            jokeURL5 = System.getenv("JOKE_URL_5");
            secret = System.getenv("SECRET");
            aseDatabae = System.getenv("DATABASE_SECRET");
            aseWeb = System.getenv("WEB_SECRET");
//                    System.getenv("JOKE_URL_5");

            System.out.println(jokeURL1);
        }
    }

    public static Env GetEnv() {
        if (intance == null) {
            intance = new Env();
        }
        return intance;
    }
}
