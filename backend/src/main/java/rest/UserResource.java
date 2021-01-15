package rest;

import DTOs.UserDTO;
import DTOs.UsersDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import entities.Role;
import entities.User;
import errorhandling.DatabaseException;
import errorhandling.NotFoundException;
import facades.DateFacade;
import facades.UserFacade;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import security.AES;
import security.JWTAuthenticationFilter;
import security.SharedSecret;
import security.UserPrincipal;
import security.errorhandling.AuthenticationException;
import utils.EMF_Creator;
import utils.Env;
import utils.TokenUtils;

/**
 * @author lam@cphbusiness.dk
 */
@Path("user")
public class UserResource {

    private static Env env = Env.GetEnv();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static UserFacade userFacade = UserFacade.getUserFacade(EMF);
    private static DateFacade dateFacade = DateFacade.getDateFacade("dd-MM-yyyy HH:mm:ss");
    @Context
    private UriInfo context;

    @Context
    SecurityContext securityContext;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getInfoForAll() {
        return "{\"msg\":\"Hello anonymous\"}";
    }

    //Just to verify if the database is setup
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("all")
    @RolesAllowed("admin")
    public String allUsers() throws DatabaseException {
        return GSON.toJson(new UsersDTO(userFacade.getAllUsers()));
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("edit-role")
    @RolesAllowed("admin")
    public String editRole(String input) throws DatabaseException {
        JsonObject obj = GSON.fromJson(input, JsonObject.class);

        EntityManager em = EMF.createEntityManager();
        User user = null;
        Role role = null;
        String userString = obj.get("username").toString();
        String roleString = obj.get("role").toString();

        try {
            user = userFacade.findUser(userString.substring(1, userString.length() - 1));
            role = userFacade.findRole(roleString.substring(1, roleString.length() - 1));

            Role removeRole = user.getRoleByName(role.getRoleName());

            em.getTransaction().begin();
            if (user.getRoleList().contains(removeRole)) {
                user.removeRole(removeRole);

                em.merge(user);
            } else {
                user.addRole(role);
                em.merge(user);
            }

            em.getTransaction().commit();

        } catch (Exception ex) {
            if (user == null) {
                throw new DatabaseException(String.format("User = %s was not found in the database", userString.substring(1, userString.length() - 1)));
            } else if (role == null) {
                throw new DatabaseException(String.format("Role = %s was not found in the database", roleString.substring(1, roleString.length() - 1)));
            }
            throw new DatabaseException("Unkownen DatabaseException: " + ex.getMessage());
        }

        return GSON.toJson(new UserDTO(user));
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("delete")
    @RolesAllowed("admin")
    public String deleteUser(String input) throws DatabaseException {
        JsonObject obj = GSON.fromJson(input, JsonObject.class);
        EntityManager em = EMF.createEntityManager();
        User user = null;
        String userString = obj.get("username").toString();
        userString = userString.substring(1, userString.length() - 1);

        try {
            user = userFacade.findUser(userString);
            em.getTransaction().begin();
            em.createQuery("DELETE FROM User u WHERE u.userName ='" + userString + "'").executeUpdate();
            em.getTransaction().commit();

        } catch (Exception ex) {
            if (user == null) {
                throw new DatabaseException(String.format("User = %s was not found in the database", userString.substring(1, userString.length() - 1)));
            }
            throw new DatabaseException("Unkownen DatabaseException: " + ex.getMessage());
        }
        return GSON.toJson(new UserDTO(user));
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("refresh")
//    @RolesAllowed("admin")
    public String refreshToken(@HeaderParam("x-access-token") String token, String reg) throws NotFoundException, DatabaseException, AuthenticationException {
        EntityManager em = EMF.createEntityManager();
        if (token == null) {
            throw new NotFoundException("No authention token found");
        }
        if (reg == null) {
            throw new NotFoundException("No refresh token found");
        }
        String refreshToken = GSON.fromJson(reg, JsonObject.class).get("count").toString();
        refreshToken = refreshToken.substring(1, refreshToken.length() - 1);
        User user = null;
        try {
            UserPrincipal userPrincipal = getUserPrincipalFromTokenIfValid(token);
            System.out.println(userPrincipal.getName());
            user = userFacade.findUser(userPrincipal.getName());
            System.out.println(user.getUserName());

            if (userFacade.isCountExpired(user.getCount())) {
                String newToken = TokenUtils.createToken(user.getUserName(), user.getRolesAsStrings());
                JsonObject obj = new JsonObject();
                obj.addProperty("token", newToken);
                return obj.toString();
            }
            throw new AuthenticationException("Refresh token has expired, login again");

            //            System.out.println(user.getCount());
//            System.out.println(refreshToken.substring(1, refreshToken.length() - 1));
//            System.out.println(AES.decrypt(refreshToken, env.aseWeb));
//            System.out.println(AES.decrypt(refreshToken, env.aseWeb).equals(user.getCount()));
//            System.out.println(userFacade.getDateFromCount(user.getCount()));
            //What if the client had logged out????
        } catch (AuthenticationException | ParseException | JOSEException ex) {
            if (ex instanceof AuthenticationException) {
                throw (AuthenticationException) ex;
            }
//            Logger.getLogger(JWTAuthenticationFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new NotFoundException("Could not generate new JWT token");
    }

    private UserPrincipal getUserPrincipalFromTokenIfValid(String token)
            throws ParseException, JOSEException, AuthenticationException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        //Is it a valid token (generated with our shared key)
        JWSVerifier verifier = new MACVerifier(SharedSecret.getSharedKey());

        if (signedJWT.verify(verifier)) {
            String roles = signedJWT.getJWTClaimsSet().getClaim("roles").toString();
            String username = signedJWT.getJWTClaimsSet().getClaim("username").toString();

            String[] rolesArray = roles.split(",");

            return new UserPrincipal(username, rolesArray);
//     return new UserPrincipal(username, roles);
        } else {
            throw new JOSEException("User could not be extracted from token");
        }
    }
}
