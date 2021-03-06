package rest;

import DTOs.UserDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.nimbusds.jose.JOSEException;
import errorhandling.InvalidInputException;
import facades.UserFacade;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import utils.EMF_Creator;
import utils.TokenUtils;

@Path("signup")
public class SignupEndpoint {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final UserFacade FACADE = UserFacade.getUserFacade(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

//    @Context
//    private UriInfo context;
//
//    @Context
//    SecurityContext securityContext;
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getInfoForAll() {
        return "{\"msg\":\"Hello anonymous\"}";
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String make(String user) throws InvalidInputException, JOSEException {
        List<String> roles = new ArrayList();
        roles.add("user");
        UserDTO userDTO = GSON.fromJson(user, UserDTO.class);
        userDTO = new UserDTO(userDTO.getName(), userDTO.getPassword(), roles);
        userDTO = FACADE.addUser(userDTO);

        String token = TokenUtils.createToken(userDTO.getName(), userDTO.getRoles());

        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("username", userDTO.getName());
        responseJson.addProperty("token", token);
        return GSON.toJson(responseJson);
    }
}
