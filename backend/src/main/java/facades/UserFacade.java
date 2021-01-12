package facades;

import DTOs.UserDTO;
import entities.Role;
import entities.User;
import errorhandling.DatabaseException;
import errorhandling.InvalidInputException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import static security.LoginEndpoint.USER_FACADE;
import static security.LoginEndpoint.dateFacade;
import security.errorhandling.AuthenticationException;
import utils.EMF_Creator;
import utils.Env;

public class UserFacade {

    private static EntityManagerFactory emf;
    private static UserFacade instance;
    private static Env env = Env.GetEnv();
    public static final DateFacade dateFacade = DateFacade.getDateFacade("dd-MM-yyyy HH:mm:ss");

    /**
     *
     * @param _emf
     * @return the instance of this facade.
     */
    public static UserFacade getUserFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new UserFacade();
        }
        return instance;
    }

    public String getDateFromCount(String count) {
        String[] arr = count.split("\\.");
        return arr[arr.length - 1];
    }

    public boolean isCountExpired(String count) throws ParseException {
        Date dateNow = new Date();
        Date dateExpire = dateFacade.getDate(getDateFromCount(count));
        return dateNow.before(dateExpire);
    }

    public void giveNewCountToUserDB(User user) throws ParseException {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        user.newCount();
        em.merge(user);
        em.getTransaction().commit();
    }

    public User getVeryfiedUser(String username, String password) throws AuthenticationException {
        EntityManager em = emf.createEntityManager();
        User user;
        try {
            user = em.find(User.class, username);
            if (user == null || !user.verifyPassword(password)) {
                throw new AuthenticationException("Invalid user name or password");
            }
        } finally {
            em.close();
        }
        return user;
    }

    public UserDTO addUser(UserDTO userDTO) throws InvalidInputException {
        EntityManager em = emf.createEntityManager();
        String name = null;
        try {
            Query query = em.createQuery("SELECT u.userName FROM User u WHERE u.userName = :name");
            query.setParameter("name", userDTO.getName());
            name = (String) query.getSingleResult();
        } catch (Exception e) {
        }

        if (name != null) {
            throw new InvalidInputException(String.format("The name %s is already taken", name));
        }

        User user = new User(userDTO.getName(), userDTO.getPassword());
        for (String role : userDTO.getRoles()) {
            user.addRole(new Role(role));
        }
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();

        return new UserDTO(user);
    }

    public User findUser(String username) throws DatabaseException {
        EntityManager em = emf.createEntityManager();
        System.out.println(username);
        try {
            User user = null;
            user = em.find(User.class, username);

            return user;

        } catch (Exception ex) {
            throw new DatabaseException(String.format("User with username (%s) was not found in database", username));
        }
    }

    public Role findRole(String roleName) throws DatabaseException {
        EntityManager em = emf.createEntityManager();

        try {
            Role role = em.find(Role.class, roleName);
            return role;
        } catch (Exception ex) {
            throw new DatabaseException(String.format("User with username (%s) was not found in database", roleName));
        }
    }

    public List<User> getAllUsers() throws DatabaseException {
        EntityManager em = emf.createEntityManager();
        ArrayList<UserDTO> userDTOs = new ArrayList();
        try {
            TypedQuery<User> query = em.createQuery("select u from User u", User.class);
            return query.getResultList();
        } catch (Exception ex) {
            throw new DatabaseException("Could not get users from database");
        }
    }

}
