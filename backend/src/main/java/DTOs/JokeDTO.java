/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DTOs;

/**
 *
 * @author Mads
 */
public class JokeDTO {
    String joke;
    String reference;
    public JokeDTO(String joke, String reference) {
        this.joke = joke;
        this.reference = reference;
    }

    public String getJoke() {
        return joke;
    }

    public String getReference() {
        return reference;
    }
}
