package DTOs;

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
