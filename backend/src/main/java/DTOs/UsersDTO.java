package DTOs;

import entities.User;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author marcg
 */
public class UsersDTO {
    private ArrayList<UserDTO> usersDTO = new ArrayList();

    public UsersDTO(List<User> users) {
        for (User user : users) {
            usersDTO.add(new UserDTO(user));
        }
    }

    public ArrayList<UserDTO> getUsersDTO() {
        return usersDTO;
    }
    
    
    
}
