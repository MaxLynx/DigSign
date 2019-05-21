package digsign.dao;


public class PrivateData {
    private String username;
    private String password;
    private String keys;

    public PrivateData(String username, String password, String keys) {
        this.username = username;
        this.password = password;
        this.keys = keys;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getKeys() {
        return keys;
    }

    public void setKeys(String keys) {
        this.keys = keys;
    }
}
