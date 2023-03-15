import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class AudioCaptchaGUI {

    private static AudioCaptcha captcha = new AudioCaptcha();
    private static String captchaCode;
    private static int highestFrequencyNotePosition;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Audio CAPTCHA");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.getContentPane().add(panel);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        JLabel label = new JLabel("Click \"Play CAPTCHA\" to start.", SwingConstants.CENTER);
        panel.add(label, constraints);

        constraints.gridy = 1;
        constraints.anchor = GridBagConstraints.CENTER;

        JButton playButton = new JButton("Play CAPTCHA");
        panel.add(playButton, constraints);

        constraints.gridy = 2;

        JTextField inputField = new JTextField();
        inputField.setVisible(false);
        panel.add(inputField, constraints);

        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    captchaCode = captcha.generateCaptcha();
                    highestFrequencyNotePosition = captcha.getHighestFrequencyNotePosition(captchaCode);
                    captcha.generateAudio(captchaCode, new File("captcha.mid"));
                    playMidi(new File("captcha.mid"));
                    label.setText("Enter the position of the note with the highest frequency:");
                    inputField.setVisible(true);
                    frame.revalidate();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int userInput = Integer.parseInt(inputField.getText());
                    if (userInput == highestFrequencyNotePosition) {
                        JOptionPane.showMessageDialog(frame, "CAPTCHA verified!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Incorrect. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid input. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        frame.setVisible(true);
    }

    private static void playMidi(File midiFile) {
        try {
            Sequencer sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequencer.setSequence(MidiSystem.getSequence(midiFile));
            sequencer.start();

            while (sequencer.isRunning()) {
                Thread.sleep(1000);
            }

            sequencer.stop();
            sequencer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
