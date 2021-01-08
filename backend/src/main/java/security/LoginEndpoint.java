package security;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nimbusds.jose.JOSEException;
import facades.UserFacade;
import java.util.logging.Level;
import java.util.logging.Logger;
import entities.User;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import security.errorhandling.AuthenticationException;
import errorhandling.GenericExceptionMapper;
import facades.DateFacade;
import java.text.ParseException;
import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import utils.EMF_Creator;
import utils.Env;
import utils.TokenUtils;

@Path("login")
public class LoginEndpoint {

    private static Env env = Env.GetEnv();
    public static final int TOKEN_EXPIRE_TIME = 1000 * 30 * 1; //30sec min
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    public static final UserFacade USER_FACADE = UserFacade.getUserFacade(EMF);
    public static final DateFacade dateFacade = DateFacade.getDateFacade("dd-MM-yyyy HH:mm:ss");

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(String jsonString) throws AuthenticationException, ParseException {
        JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
        String username = json.get("username").getAsString();
        String password = json.get("password").getAsString();

        User user = null;
        try {
            user = USER_FACADE.getVeryfiedUser(username, password);

            if (!USER_FACADE.isCountExpired(user.getCount())) {
                USER_FACADE.giveNewCountToUserDB(user);
            }
            String count = AES.encrypt(user.getCount(), env.aseWeb);
            String token = TokenUtils.createToken(username, user.getRolesAsStrings());
            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("username", username);
            responseJson.addProperty("token", token);
            responseJson.addProperty("count", count);

            return Response.ok(new Gson().toJson(responseJson)).build();

        } catch (JOSEException | AuthenticationException ex) {
            if (ex instanceof AuthenticationException) {
                throw (AuthenticationException) ex;
            }
            Logger.getLogger(GenericExceptionMapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new AuthenticationException("Invalid username or password! Please try again");
    }
}
