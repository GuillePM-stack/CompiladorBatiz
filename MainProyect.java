import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.awt.event.ActionEvent;

public class MainProyect extends JFrame {
    private JButton fileButton, analisisButton;
    private JFileChooser fileChooser = new JFileChooser();
    private FileNameExtensionFilter filter;
    private JTextArea textArea;

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
        setVisible(true);
        setResizable(false);

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

        // Botones de sintaxis, semántica y código
        analisisButton = new JButton("Analizar");
        // semainticButton = new JButton("Analizar Semántica");
        // codeButton = new JButton("Ejecutar Código");

        analisisButton.setBounds(getXButton(), (int) (getHeight() * 0.20), getWidthButton(), getHeightButton());
        // semainticButton.setBounds(getXButton(), (int) (getHeight() * 0.30), getWidthButton(), getHeightButton());
        // codeButton.setBounds(getXButton(), (int) (getHeight() * 0.40), getWidthButton(), getHeightButton());

        add(analisisButton);
        // add(semainticButton);
        // add(codeButton);

        analisisButton.setEnabled(false);
        // semainticButton.setEnabled(false);
        // codeButton.setEnabled(false);

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
                    // semainticButton.setEnabled(true);
                    // codeButton.setEnabled(true);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    textArea.setText("Error al leer el archivo: " + ex.getMessage());
                }

            } else {
                JOptionPane.showMessageDialog(this, "No se ha seleccionado nungún archivo.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        } else if (e.getSource() == analisisButton) {
            System.out.println(textArea.getText());
            new Parser(textArea.getText());
        }

    }

    public static void main(String[] args) throws Exception {
        new MainProyect();
    }
}
