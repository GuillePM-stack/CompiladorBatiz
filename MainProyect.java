
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.ImageIcon;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

public class MainProyect extends JFrame {
    private JButton fileButton, analisisButton;
    private JFileChooser fileChooser = new JFileChooser();
    private FileNameExtensionFilter filter;
    private JTextArea textArea;
    private ImageIcon imageIcon = new ImageIcon("img/blackPink.png");

    MainProyect() {
        hazInterfaz();
        hazEscucha();
    }

    void hazInterfaz() {
        setTitle("Compilador");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(600,600));
        setIconImage(imageIcon.getImage());
        getContentPane().setBackground(Color.DARK_GRAY);

        fileButton = new JButton("Selecciona tu archivo");
        fileButton.setBounds(getXButton(), (int) (getHeight() * 0.80), getWidthButton(), getHeightButton());
        filter = new FileNameExtensionFilter("Archivos de Texto (*.txt)", "txt");
        fileChooser.setFileFilter(filter);
        fileButton.setBackground(Color.GRAY);
        fileButton.setForeground(Color.BLACK);
        add(fileButton);

        textArea = new JTextArea();
        textArea.setBounds(0, 0, (int) (getWidth() * 0.75), (int) getHeight());
        textArea.setEditable(false);
        textArea.setBackground(Color.LIGHT_GRAY);
        add(textArea);
        analisisButton = new JButton("Analizar");

        analisisButton.setBounds(getXButton(), (int) (getHeight() * 0.20), getWidthButton(), getHeightButton());
        add(analisisButton);

        analisisButton.setEnabled(false);
        setVisible(true);

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent evt) {
                adaptComponents();
            }
        });
    }

    void adaptComponents() {
        int width = getWidth();
        int height = getHeight();

        textArea.setBounds(
                0,
                0,
                (int) (width * 0.75),
                height);

        fileButton.setBounds(
                (int) (width * 0.77),
                (int) (height * 0.80),
                (int) (width * 0.20),
                (int) (height * 0.05));

        analisisButton.setBounds(
                (int) (width * 0.77),
                (int) (height * 0.20),
                (int) (width * 0.20),
                (int) (height * 0.05));
    }

    void hazEscucha() {
        fileButton.addActionListener(this::ActionsButtons);
        analisisButton.addActionListener(this::ActionsButtons);
    }

    int getHeightButton() {
        return (int) (getHeight() * 0.05);
    }

    int getWidthButton() {
        return (int) (getWidth() * 0.20);
    }

    int getXButton() {
        return (int) (getWidth() * 0.75);
    }

    private void ActionsButtons(ActionEvent e) {

        if (e.getSource() == fileButton) {

            int returnVal = fileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();

                try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                    StringBuilder content = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        content.append(line).append("\n");
                    }

                    textArea.setText(content.toString());
                    analisisButton.setEnabled(true);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    textArea.setText("Error al leer el archivo: " + ex.getMessage());
                }

            } else {
                JOptionPane.showMessageDialog(this, "No se ha seleccionado nungún archivo.", "Advertencia",
                        JOptionPane.WARNING_MESSAGE);
            }
        } else if (e.getSource() == analisisButton) {
            System.out.println(textArea.getText());
           new Parser(textArea.getText());            
        }

    }

    public static void main(String[] args) throws Exception {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        new MainProyect();

    }
}
