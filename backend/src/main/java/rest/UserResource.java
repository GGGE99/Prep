package rest;

import DTOs.UserDTO;
import DTOs.UsersDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import entities.Role;
import entities.User;
import errorhandling.DatabaseException;
import errorhandling.NotFoundException;
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

/**
 * @author lam@cphbusiness.dk
 */
@Path("user")
public class UserResource {

    private static Env env = Env.GetEnv();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static UserFacade userFacade = UserFacade.getUserFacade(EMF);
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
        System.out.println(input);

        JsonObject obj = GSON.fromJson(input, JsonObject.class);

        EntityManager em = EMF.createEntityManager();
        User user = null;
        Role role = null;
        String userString = obj.get("username").toString();
        String roleString = obj.get("role").toString();
        System.out.println(userString + " : " + roleString);

        try {
            user = em.createQuery("SELECT u FROM User u WHERE u.userName = " + userString, User.class).getSingleResult();
            role = em.createQuery("SELECT r FROM Role r WHERE r.roleName = " + roleString, Role.class).getSingleResult();
            System.out.println(role + " : " + user);

            em.getTransaction().begin();
            if (user.getRoleList().contains(roleString)) {
                user.removeRole(role);
            } else {
                user.addRole(role);
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

        try {
            user = em.createQuery("SELECT u FROM User u WHERE u.userName = " + userString, User.class).getSingleResult();
            em.getTransaction().begin();

            em.remove(em);
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
    @Path("test")
//    @RolesAllowed("admin")
    public String test(@HeaderParam("x-access-token") String token, String reg) throws NotFoundException, DatabaseException {
        EntityManager em = EMF.createEntityManager();
        if (token == null) {
            throw new NotFoundException("No authention token found");
        }
        if (reg == null) {
            throw new NotFoundException("No refresh token found");
        }
        String refreshToken = GSON.fromJson(reg, JsonObject.class).get("refreshToken").toString();
        refreshToken = refreshToken.substring(1, refreshToken.length() - 1);
        User user = null;
        try {
            UserPrincipal userPrincipal = getUserPrincipalFromTokenIfValid(token);
//            user = em.find(User.class, userPrincipal.getName());
            user = userFacade.findUser(userPrincipal.getName());
            System.out.println(user.getCount());
            System.out.println(refreshToken.substring(1, refreshToken.length() - 1));
            System.out.println(AES.decrypt(refreshToken, env.aseWeb));
            System.out.println(AES.decrypt(refreshToken, env.aseWeb).equals(user.getCount()));

            //What if the client had logged out????
        } catch (AuthenticationException | ParseException | JOSEException ex) {
            System.out.println("err");
            Logger.getLogger(JWTAuthenticationFilter.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
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
