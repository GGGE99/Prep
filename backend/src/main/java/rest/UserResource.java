package rest;

import DTOs.UserDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import entities.User;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import security.UserPrincipal;
import utils.EMF_Creator;

/**
 * @author lam@cphbusiness.dk
 */
@Path("user")
public class UserResource {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    @Context
    private UriInfo context;

    @Context
    SecurityContext securityContext;

    //Just to verify if the database is setup
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("all")
    @RolesAllowed("admin")
    public String allUsers() {
        EntityManager em = EMF.createEntityManager();
        ArrayList<UserDTO> userDTOs = new ArrayList();
        try {
            TypedQuery<User> query = em.createQuery("select u from User u", entities.User.class);
            List<User> users = query.getResultList();
            for (User user : users) {
                userDTOs.add(new UserDTO(user));
            }
            return GSON.toJson(userDTOs);
        } finally {
            em.close();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("user")
    public String getUser() {
        String thisuser = securityContext.getUserPrincipal().getName();
        JsonObject obj = new JsonObject();
        obj.addProperty("name", thisuser);
        JsonArray array = new JsonArray();
        array.add("user");
        boolean s = securityContext.isUserInRole("admin");
        if (s) {
            array.add("admin");
        }
        obj.add("roles", array);

        return obj.toString();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("user")
    @RolesAllowed("user")
    public String getFromUser() {
        String thisuser = securityContext.getUserPrincipal().getName();
        JsonObject obj = new JsonObject();
        obj.addProperty("name", thisuser);
        JsonArray array = new JsonArray();
        array.add("user");
        boolean s = securityContext.isUserInRole("admin");
        if (s) {
            array.add("admin");
        }
        obj.add("roles", array);

        return obj.toString();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("admin")
    @RolesAllowed("admin")
    public String getFromAdmin() {
        String thisuser = securityContext.getUserPrincipal().getName();
        JsonObject obj = new JsonObject();
        obj.addProperty("name", thisuser);
        JsonArray array = new JsonArray();
        array.add("admin");
        boolean s = securityContext.isUserInRole("user");
        if (s) {
            array.add("user");
        }
        obj.add("roles", array);

        return obj.toString();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("edit-role")
    @RolesAllowed("admin")
    public String editRole() {
        String thisuser = securityContext.getUserPrincipal().getName();
        EntityManager em = EMF.createEntityManager();
        try{
            User user = em.find(User.class, thisuser);
            System.out.println(user);
        } catch (Exception e) {
            
        }
        
        JsonObject obj = new JsonObject();
        obj.addProperty("name", thisuser);
        JsonArray array = new JsonArray();
        array.add("admin");
        boolean s = securityContext.isUserInRole("user");
        if (s) {
            array.add("user");
        }
        obj.add("roles", array);

        return obj.toString();
    }
}
