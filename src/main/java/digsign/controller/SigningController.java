package digsign.controller;


import digsign.signature.Model;
import digsign.signature.Signing;
import digsign.signature.User;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class SigningController {
    private Model model;

    public SigningController(){
        this.model = new Model();
    }

    @RequestMapping("")
    public String home(Map<String, Object> model){
        return "authorization";
    }

    @RequestMapping(value = "", params = {"form"})
    public String homeFormSpecific(@RequestParam String form,
                                    Map<String, Object> model){
        if(form.equals("create")){
            model.put("showCreate", true);
        }
        else{
            model.put("showCreate", false);
        }
        return "authorization";
    }

    @RequestMapping(value = "login", method= RequestMethod.POST)
    public String login(@RequestParam String username,
                         @RequestParam String password,
                         Map<String, Object> viewModel){
        User user = model.findUser(username, password);
        if(user != null){
            model.setCurrentUser(user);
            viewModel.put("currentUsername", username);
            return "activity";
        }
        else{
            viewModel.put("message", "Користувача з такими даними не знайдено! " +
                    "Спробуйте створити нового користувача");
            return "authorization";
        }
    }

    @RequestMapping(value = "create", method= RequestMethod.POST)
    public String createUser(@RequestParam String username,
                              @RequestParam String password,
                             Map<String, Object> viewModel){
        User newUser = model.addUser(username, password);
        if(newUser != null) {
            model.setCurrentUser(newUser);
            viewModel.put("currentUsername", username);
            return "activity";
        }
        else{
            viewModel.put("message", "Користувач з таким логіном вже існує у системі!");
            return "authorization";
        }
    }

    @RequestMapping(value = "delete", method= RequestMethod.POST)
    public String removeUser(Map<String, Object> viewModel){
        model.removeUser(model.getCurrentUser());
        viewModel.put("message", "Користувача "
                + model.getCurrentUser().getUsername() + " було видалено із системи!");
        return "authorization";
    }


    @RequestMapping(value = "verify", method= RequestMethod.POST)
    public String verifySignature(@ModelAttribute("object") Object filebject,
                                   @RequestParam String username,
                                   @RequestParam("file") MultipartFile file, BindingResult bindingResult,
                                   Map<String, Object> viewModel){
        if(!bindingResult.hasErrors()) {

            byte[] buffer = new byte[]{0};
            try {
                buffer = file.getBytes();
            }
            catch(IOException ex){
                System.err.println(ex.getMessage());
                viewModel.put("message", "Документ не був підписаний користувачем " + username);
            }
            String text = Model.EMPTY_FILE_MARKER;
            try (InputStream in = new ByteArrayInputStream(buffer)) {
                text = new BufferedReader(new InputStreamReader(in))
                        .lines().collect(Collectors.joining("\n"));
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
                viewModel.put("message", "Документ не був підписаний користувачем " + username);
            }

            try {
                String[] signatures = ((String)
                        text.subSequence(text.indexOf(Model.DIGITAL_SIGNATURE_STARTING_MARKER) +
                                Model.DIGITAL_SIGNATURE_STARTING_MARKER.length()
                                , text.length())).split(Model.DIGITAL_SIGNATURE_CONTINUATION_MARKER);
                String fileText = (String) text.subSequence(0, text.indexOf(Model.DIGITAL_SIGNATURE_STARTING_MARKER));
                boolean success = false;
                for(String signature : signatures){
                    if (Signing.verifySignature(model.findUserUnsafely(username), signature, fileText)) {
                        viewModel.put("message", "Документ був підписаний користувачем " + username);
                        success = true;
                    }
                }

                if(!success) {
                    viewModel.put("message", "Документ не був підписаний користувачем " + username);
                }
            }
            catch(Exception e){
                viewModel.put("message", "Документ не був підписаний користувачем " + username);
            }
        }
        else {
            viewModel.put("message", "Документ не був підписаний користувачем " + username);
        }
        viewModel.put("currentUsername", model.getCurrentUser().getUsername());
        return "activity";

    }

    @RequestMapping(value = "sign", method= RequestMethod.POST)
    public String putSignature(@ModelAttribute("object") Object fileObject,
                               @RequestParam String path,
            @RequestParam("file") MultipartFile file, BindingResult bindingResult,
                               Map<String, Object> viewModel){

        if(!bindingResult.hasErrors()) {

            byte[] buffer = new byte[]{0};
            try {
                buffer = file.getBytes();
            }
            catch(IOException ex){
                System.err.println(ex.getMessage());
                viewModel.put("message", "Підписати документ не вдалося :(");
            }
            String text = Model.EMPTY_FILE_MARKER;
            try (InputStream in = new ByteArrayInputStream(buffer)) {
                text = new BufferedReader(new InputStreamReader(in))
                        .lines().collect(Collectors.joining("\n"));
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
                viewModel.put("message", "Підписати документ не вдалося :(");
            }
            try (FileWriter fileWriter = new FileWriter(new File(path + file.getOriginalFilename()), false)) {
                if(text.contains(Model.DIGITAL_SIGNATURE_STARTING_MARKER)){
                    String originalText = text.substring(0, text.indexOf(Model.DIGITAL_SIGNATURE_STARTING_MARKER));
                    fileWriter.write(text + Model.DIGITAL_SIGNATURE_CONTINUATION_MARKER
                            + Signing.createSignature(model.getCurrentUser(), originalText));
                }
                else {
                    fileWriter.write(text + Model.DIGITAL_SIGNATURE_STARTING_MARKER
                            + Signing.createSignature(model.getCurrentUser(), text));
                }
                viewModel.put("message", "Документ було успішно підписано!");
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
                viewModel.put("message", "Підписати документ не вдалося :(");
            }
        }
        else{
            viewModel.put("message", "Підписати документ не вдалося :(");
        }
        viewModel.put("currentUsername", model.getCurrentUser().getUsername());
        return "activity";
    }

    public String getCurrentUsername(){
        return model.getCurrentUser().getUsername();
    }
}
