package facades;

import DTOs.UserDTO;
import DTOs.UsersDTO;
import entities.Role;
import entities.User;
import errorhandling.DatabaseException;
import errorhandling.InvalidInputException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import utils.EMF_Creator;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import static org.hamcrest.MatcherAssert.assertThat;
import org.hamcrest.Matchers;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import org.hamcrest.beans.SamePropertyValuesAs;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import security.errorhandling.AuthenticationException;

//Uncomment the line below, to temporarily disable this test
//@Disabled
public class UserFacadeTest {

    private static EntityManagerFactory emf;
    private static UserFacade facade;
    public static final DateFacade dateFacade = DateFacade.getDateFacade("dd-MM-yyyy HH:mm:ss");

    private static String pass = "1234";
    private static User user = new User("Test", pass);
    private static User both = new User("user_admin", pass);
    private static User admin = new User("Admin", pass);

//    private static Role userRole = new Role("user");
//    private static Role adminRole = new Role("admin");
    public UserFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = UserFacade.getUserFacade(emf);
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        em.createQuery("delete from User u").executeUpdate();
        em.createQuery("delete from Role r").executeUpdate();

        Role userRole = new Role("user");
        Role adminRole = new Role("admin");

        user.addRole(userRole);

        both.addRole(userRole);
        both.addRole(adminRole);

        em.persist(userRole);
        em.persist(adminRole);
        em.persist(both);
        em.persist(user);
        //System.out.println("Saved test data to database");
        em.getTransaction().commit();
    }

    @AfterAll
    public static void tearDownClass() {
//        Clean up database after test is done or use a persistence unit with drop-and-create to start up clean on every test
    }

    // Setup the DataBase in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the code below to use YOUR OWN entity class
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            //Delete existing users and roles to get a "fresh" database
            em.createQuery("delete from User").executeUpdate();

            User tempUser = user;
            User tempUserAdmin = both;

            em.persist(tempUserAdmin);
            em.persist(tempUser);
//            em.persist(both);
            //System.out.println("Saved test data to database");
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Test
    public void testGetVeryfiedUser() throws AuthenticationException {
        UserDTO actual = new UserDTO(user);
        UserDTO expected = new UserDTO(facade.getVeryfiedUser(user.getUserName(), pass));

        assertThat(actual, samePropertyValuesAs(expected));
    }

    @Test
    public void testAddUser() throws InvalidInputException {
        EntityManager em = emf.createEntityManager();
        Role role = em.find(Role.class, "admin");
        admin.addRole(role);
        UserDTO userDTO = new UserDTO(admin);
        UserDTO actual = userDTO;
        facade.addUser(userDTO);
        UserDTO expected = new UserDTO(em.find(User.class, actual.getName()));

        assertThat(actual, samePropertyValuesAs(expected));
    }

    @Test
    public void testFindUser() throws DatabaseException {
        UserDTO actual = new UserDTO(user);
        UserDTO expected = new UserDTO(facade.findUser(actual.getName()));
        assertThat(actual, samePropertyValuesAs(expected));
    }

    @Test
    public void testFindRole() throws DatabaseException {
        EntityManager em = emf.createEntityManager();
        String actual = em.find(Role.class, "admin").getRoleName();
        Role expected = facade.findRole(actual);
        assertEquals(actual, expected.getRoleName());
    }

    @Test
    public void testGetAllUsers() throws DatabaseException {
        EntityManager em = emf.createEntityManager();
        User actual = em.find(User.class, "Test");
        UsersDTO expected = new UsersDTO(facade.getAllUsers());

        assertThat(expected.getUsersDTO(), hasSize(2));
        assertThat(expected.getUsersDTO(), hasItem(samePropertyValuesAs(new UserDTO(actual))));
    }

    @Test
    public void testGiveNewCountToUserDB() throws DatabaseException, ParseException {
        EntityManager em = emf.createEntityManager();

        User user = em.find(User.class, "Test");
        String count = user.getCount();
        facade.giveNewCountToUserDB(user);
        String newCount = em.find(User.class, "Test").getCount();

        System.out.println(count);
        System.out.println(newCount);

        assertThat(count, not(newCount));
        assertThat(null, not(count));
        assertThat(null, not(newCount));

    }

    @Test
    public void testIsCountExpired() throws ParseException {
        EntityManager em = emf.createEntityManager();

        User user = em.find(User.class, "Test");
        String count = user.getCount();
        boolean actual = facade.isCountExpired(count);

        assertEquals(true, actual);

    }

    @Test
    public void getDateFromCount() throws DatabaseException, ParseException {
        EntityManager em = emf.createEntityManager();

        User user = em.find(User.class, "Test");
        String count = user.getCount();
        String Stringdate = facade.getDateFromCount(count);
        Date date = dateFacade.getDate(Stringdate);
        Date newDate = new Date();

        boolean actual = date.after(newDate);
        assertEquals(true, actual);

    }
}
