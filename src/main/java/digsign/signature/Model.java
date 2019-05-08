package digsign.signature;


import java.util.ArrayList;
import java.util.List;

public class Model {
    private List<User> users;
    private User currentUser;

    public static String DIGITAL_SIGNATURE_STARTING_MARKER = "<DIG SIGN>";
    public static String EMPTY_FILE_MARKER = "<EMPTY FILE>";

    public Model(){
        users = new ArrayList<User>();
    }

    public Model(List<User> users){
        this.users = users;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public User findUser(String login, String password){
        for(User user : users){
            if(user.getUsername().equals(login)
                    && user.getPassword().equals(password)){
                return user;
            }
        }
        return null;
    }

    public User findUserUnsafely(String login){
        for(User user : users){
            if(user.getUsername().equals(login)){
                return user;
            }
        }
        return null;
    }

    public User addUser(String login, String password){
        for(User user : users){
            if(user.getUsername().equals(login)){
                return null;
            }
        }
        User newUser = new User(login, password);
        users.add(newUser);
        return newUser;
    }

    public void removeUser(User user){
        users.remove(user);
    }
}
