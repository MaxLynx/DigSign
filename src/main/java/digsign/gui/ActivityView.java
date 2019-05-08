package digsign.gui;

import digsign.controller.Controller;
import digsign.signature.Model;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;


public class ActivityView extends JFrame {
    private JPanel panel1;
    private JButton завантажитиДокументButton;
    private JTextField textField1;
    private JButton button1;
    private JButton вийтиButton;
    private JLabel userLabel;
    private JButton видалитиКористувачаButton;
    private JLabel verifyingLabel;
    private Controller controller;

    final JFileChooser fc = new JFileChooser();

    public ActivityView(Controller controller) {
        this.controller = controller;

        setContentPane(panel1);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setVisible(true);
        userLabel.setText("Ви увійшли як користувач " + controller.getCurrentUsername());
        завантажитиДокументButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fc.showOpenDialog(ActivityView.this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();

                    String result = "";

                    Path path = file.toPath();

                    try (InputStream in = new BufferedInputStream(
                            Files.newInputStream(path))) {
                        result = new BufferedReader(new InputStreamReader(in))
                                .lines().collect(Collectors.joining("\n"));
                    } catch (IOException x) {
                        System.err.println(x.getMessage());
                    }
                    int index = result.indexOf(Model.DIGITAL_SIGNATURE_STARTING_MARKER);
                    if(controller.verifySignature(textField1.getText(),
                            result.substring(index + Model.DIGITAL_SIGNATURE_STARTING_MARKER.length()),
                            result.substring(0, index)
                    )){
                        verifyingLabel.setText("Документ був підписаний користувачем " + textField1.getText()
                                + " та з того часу незмінний");
                    }
                    else{
                        verifyingLabel.setText("Документ не був підписаний користувачем " + textField1.getText()
                                + " або змінився з моменту підпису");
                    }
                }
            }
        });
        вийтиButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fc.showOpenDialog(ActivityView.this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    String text = Model.EMPTY_FILE_MARKER;
                    Path path = file.toPath();
                    try (InputStream in = new BufferedInputStream(
                            Files.newInputStream(path))) {
                        text = new BufferedReader(new InputStreamReader(in))
                                .lines().collect(Collectors.joining("\n"));
                    }
                    catch(IOException ex){
                        System.err.println(ex.getMessage());
                    }
                    try(FileWriter fileWriter = new FileWriter(file, true)) {
                        fileWriter.write(Model.DIGITAL_SIGNATURE_STARTING_MARKER
                                + controller.putSignature(text));
                    }
                    catch(IOException ex){
                        System.err.println(ex.getMessage());
                    }

                }
            }
        });
        видалитиКористувачаButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.removeUser();
                dispose();
            }
        });
    }
}
