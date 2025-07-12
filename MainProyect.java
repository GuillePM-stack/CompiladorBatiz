
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

import java.awt.event.ActionEvent;

public class MainProyect extends JFrame {
    private JButton fileButton, analisisButton;
    private JFileChooser fileChooser = new JFileChooser();
    private FileNameExtensionFilter filter;
    private JTextArea textArea;
    private ImageIcon imageIcon = new ImageIcon("img/NiggaPink.png"); // Ruta del icono 

    MainProyect() {
        hazInterfaz();
        hazEscucha();
    }

    void hazInterfaz() {
        // Configuración de la ventana
        setTitle("Compilador");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setIconImage(imageIcon.getImage()); // Icono 

        fileButton = new JButton("Selecciona tu archivo");
        fileButton.setBounds(getXButton(), (int) (getHeight() * 0.85), getWidthButton(), getHeightButton());
        filter = new FileNameExtensionFilter("Archivos de Texto (*.txt)", "txt");
        fileChooser.setFileFilter(filter);
        add(fileButton);

        // Componentes de la ventana
        textArea = new JTextArea();
        textArea.setBounds(0, 0, (int) (getWidth() * 0.75), (int) getHeight());
        textArea.setEditable(false);
        add(textArea);
        analisisButton = new JButton("Analizar");

        analisisButton.setBounds(getXButton(), (int) (getHeight() * 0.20), getWidthButton(), getHeightButton());
        add(analisisButton);

        analisisButton.setEnabled(false);
        setVisible(true);

    }

    void hazEscucha() {
        fileButton.addActionListener(this::ActionsButtons);
        analisisButton.addActionListener(this::ActionsButtons);
    }

    // Métodos para obtener dimensiones y posiciones de los botones
    int getHeightButton() {
        return (int) (getHeight() * 0.1);
    }

    int getWidthButton() {
        return (int) (getWidth() * 0.25);
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
