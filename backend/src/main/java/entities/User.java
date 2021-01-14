package entities;

import facades.DateFacade;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.mindrot.jbcrypt.BCrypt;
import security.AES;
import utils.Env;

@Entity
@Table(name = "users")
public class User implements Serializable {

    private static Env env = Env.GetEnv();
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "user_name", length = 25)
    private String userName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "count", unique = true)
    private String count;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "user_pass")
    private String userPass;
    @JoinTable(name = "user_roles", joinColumns = {
        @JoinColumn(name = "user_name", referencedColumnName = "user_name")}, inverseJoinColumns = {
        @JoinColumn(name = "role_name", referencedColumnName = "role_name")})
    @ManyToMany
    private List<Role> roleList = new ArrayList<>();
    private static DateFacade dateFacade = DateFacade.getDateFacade("dd-MM-yyyy HH:mm:ss");

    public List<String> getRolesAsStrings() {
        if (roleList.isEmpty()) {
            return null;
        }
        List<String> rolesAsStrings = new ArrayList<>();
        roleList.forEach((role) -> {
            rolesAsStrings.add(role.getRoleName());
        });
        return rolesAsStrings;
    }

    public User() {
    }

    //TODO Change when password is hashed
    public boolean verifyPassword(String pw) {
        return (BCrypt.checkpw(pw, userPass));
    }

    public User(String userName, String userPass) {
        this.userName = userName;

        this.userPass = BCrypt.hashpw(userPass, BCrypt.gensalt());
        newCount();
    }

    public void newCount() {
        this.count = AES.encrypt(UUID.randomUUID().toString() + "." + dateFacade.makeDate(0, 0, 30, 0, 0, 0), env.aseDatabae);
    }

    public String getCount() {
        return AES.decrypt(count, env.aseDatabae);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPass() {
        return this.userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }

    public void addRole(Role userRole) {
        roleList.add(userRole);
    }

    public void removeRole(Role userRole) {
        roleList.remove(userRole);
        userRole.removeUser(this);
    }

    public void removeAllRoles() {
        for (Role role : roleList) {
            roleList.remove(role);
            role.removeUser(this);
        }
    }

    public boolean isRoleInRoleList(Role role) {
        for (Role role1 : roleList) {
            if (role1.getRoleName() == role.getRoleName()) {
                return true;
            }
        }
        return false;
    }

    public Role getRoleByName(String role) {
        for (Role role1 : roleList) {
            if (role1.getRoleName() == role) {
                return role1;
            }
        }
        return null;
    }

}
