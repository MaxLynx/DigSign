package digsign.controller;


import digsign.signature.Model;
import digsign.signature.Signing;
import digsign.signature.User;

public class Controller {
    private Model model;

    public Controller(){
        this.model = new Model();
    }

    public boolean login(String username, String password){
        User user = model.findUser(username, password);
        if(user != null){
            model.setCurrentUser(user);
            return true;
        }
        else{
            return false;
        }
    }

    public boolean createUser(String username, String password){
        User newUser = model.addUser(username, password);
        if(newUser != null) {
            model.setCurrentUser(newUser);
            return true;
        }
        else{
            return false;
        }
    }

    public void removeUser(){
        model.removeUser(model.getCurrentUser());
    }


    public boolean verifySignature(String username, String signature, String text){
        return Signing.verifySignature(model.findUserUnsafely(username), signature, text);
    }

    public String putSignature(String text){
        return Signing.createSignature(model.getCurrentUser(), text);
    }

    public String getCurrentUsername(){
        return model.getCurrentUser().getUsername();
    }
}
