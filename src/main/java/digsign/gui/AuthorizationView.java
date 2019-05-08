package digsign.gui;

import digsign.controller.Controller;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class AuthorizationView extends JFrame {
    private JTextField textField1;
    private JTextField textField2;
    private JButton створитиНовогоКористувачаButton;
    private JButton увійтиButton;
    private JLabel resultLabel;
    private JPanel panel1;
    private JButton вийтиButton;
    private JLabel creationErrorLabel;
    private Controller controller;

    public AuthorizationView() {
        controller = new Controller();
        setContentPane(panel1);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setVisible(true);

        увійтиButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean result = controller.login(textField2.getText(), textField1.getText());
                if(!result){
                    resultLabel.setText("Користувача з такими даними не знайдено! " +
                            "Спробуйте створити нового користувача");
                }
                else{
                    resultLabel.setText("");
                    textField1.setText("");
                    textField2.setText("");
                    creationErrorLabel.setText("");
                    new ActivityView(controller);
                }
            }
        });
        створитиНовогоКористувачаButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(controller.createUser(textField2.getText(), textField1.getText())) {
                    resultLabel.setText("");
                    textField1.setText("");
                    textField2.setText("");
                    creationErrorLabel.setText("");
                    new ActivityView(controller);
                }
                else{
                    resultLabel.setText("");
                    creationErrorLabel.setText("Користувач з таким іменем вже існує у системі!");
                }
            }
        });
        вийтиButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
}
