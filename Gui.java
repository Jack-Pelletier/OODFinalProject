import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Gui {
    private int moveCount = 0; // Keeps track of turns

    public Gui() {
        JFrame frame = new JFrame("Tic Tac Toe Board");
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 3));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                final JButton button = new JButton();
                button.setName(i + "" + j);
                button.setText("");
                button.setFont(new Font("Arial", Font.BOLD, 40));
                button.setFocusPainted(false);
                button.setBorder(BorderFactory.createLineBorder(Color.BLACK));

                button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (button.getText().equals("")) {
                            if (moveCount % 2 == 0) {
                                button.setText("X");
                            } else {
                                button.setText("O");
                            }
                            moveCount++;
                        }
                    }
                });

                panel.add(button);
            }
        }

        frame.add(panel);
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new Gui();
    }
}
