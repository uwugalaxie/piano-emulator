package org.example;

import javax.sound.midi.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Piano {
    private static final int keysNum = 12;
    private static final String[] noteNames = {
            "Z", "S", "X", "D", "C", "V", "G", "B", "H", "N", "J", "M"
    };
    private Synthesizer synthesizer;
    private MidiChannel midiChannel;
    private int currentOctave;
    private int volume;

    public void AppGUI() {
        JFrame frame = new JFrame("Piano emulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());

        JPanel pianoPanel = new JPanel();

        currentOctave = 4;
        volume = 80;

        for (int i = 0; i < keysNum; i++) {
            final int keyIndex = i;

            if (isBlackKey(i)) {
                JButton blackKey = new JButton(getButtonLabel(keyIndex));
                blackKey.setBackground(Color.BLACK);
                blackKey.setForeground(Color.GRAY);
                blackKey.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                blackKey.setPreferredSize(new Dimension(20, 100));

                blackKey.addMouseListener(new MouseAdapter() {
                    public void mousePressed(MouseEvent event) {
                        playSound(keyIndex);
                    }

                    public void mouseReleased(MouseEvent event) {
                        stopSound(keyIndex);
                    }
                });

                pianoPanel.add(blackKey);
            } else {
                JButton whiteKey = new JButton(getButtonLabel(keyIndex));
                whiteKey.setBackground(Color.WHITE);
                whiteKey.setForeground(Color.GRAY);
                whiteKey.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                whiteKey.setPreferredSize(new Dimension(40, 100));

                whiteKey.addMouseListener(new MouseAdapter() {
                    public void mousePressed(MouseEvent event) {
                        playSound(keyIndex);
                    }

                    public void mouseReleased(MouseEvent event) {
                        stopSound(keyIndex);
                    }
                });

                pianoPanel.add(whiteKey);
            }

            frame.add(pianoPanel, BorderLayout.CENTER);
            frame.pack();
            frame.setVisible(true);
            pianoPanel.requestFocus();
        }

        JPanel settings = new JPanel();
        JButton octaveUpButton = new JButton("Octave Up");
        JButton octaveDownButton = new JButton("Octave Down");

        octaveUpButton.addActionListener(e -> increaseOctave());
        octaveDownButton.addActionListener(e -> decreaseOctave());

        settings.add(octaveDownButton);
        settings.add(octaveUpButton);

        JSlider volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 127, volume);
        volumeSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if (!source.getValueIsAdjusting()) {
                    volume = source.getValue();
                }
            }
        });

        settings.add(new JLabel("Volume: "));
        settings.add(volumeSlider);

        frame.add(settings, BorderLayout.SOUTH);
        frame.pack();
    }

    private void keyboardSupport() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                switch (e.getID()) {
                    case KeyEvent.KEY_PRESSED:
                        keyboardKeyPress(e.getKeyCode());
                        break;
                    case KeyEvent.KEY_RELEASED:
                        keyboardKeyRelease(e.getKeyCode());
                        break;
                }
                return false;
            }

            private void keyboardKeyPress(int keyCode) {
                if (keyCode == KeyEvent.VK_UP) {
                    increaseOctave();
                } else if (keyCode == KeyEvent.VK_DOWN) {
                    decreaseOctave();
                } else {
                    int keyIndex = keyIndex(keyCode);
                    if (keyIndex != -1) {
                        playSound(keyIndex);
                    }
                }
            }

            private void keyboardKeyRelease(int keyCode) {
                int keyIndex = keyIndex(keyCode);
                if (keyIndex != -1) {
                    stopSound(keyIndex);
                }
            }

            private int keyIndex(int keyCode) {
                switch (keyCode) {
                    case KeyEvent.VK_M:
                        return 11;
                    case KeyEvent.VK_J:
                        return 10;
                    case KeyEvent.VK_N:
                        return 9;
                    case KeyEvent.VK_H:
                        return 8;
                    case KeyEvent.VK_B:
                        return 7;
                    case KeyEvent.VK_G:
                        return 6;
                    case KeyEvent.VK_V:
                        return 5;
                    case KeyEvent.VK_C:
                        return 4;
                    case KeyEvent.VK_D:
                        return 3;
                    case KeyEvent.VK_X:
                        return 2;
                    case KeyEvent.VK_S:
                        return 1;
                    case KeyEvent.VK_Z:
                        return 0;
                    default:
                        return -1;
                }
            }
        });
    }

    private boolean isBlackKey(int keyIndex) {
        int indexInOctave = keyIndex % 12;
        return indexInOctave == 1 ||
                indexInOctave == 3 ||
                indexInOctave == 6 ||
                indexInOctave == 8 ||
                indexInOctave == 10;
    }

    private void playSound(int keyIndex) {
        if (synthesizer == null || !synthesizer.isOpen()) {
            try {
                synthesizer = MidiSystem.getSynthesizer();
                synthesizer.open();
                midiChannel = synthesizer.getChannels()[0];
            } catch (MidiUnavailableException e) {
                e.printStackTrace();
                return;
            }
        }

        int velocity = volume;
        int midiNote = (currentOctave * keysNum) + keyIndex;
        midiChannel.noteOn(midiNote, velocity);
    }

    private void stopSound(int keyIndex) {
        if (synthesizer != null && synthesizer.isOpen()) {
            int midiNote = (currentOctave * keysNum) + keyIndex;
            midiChannel.noteOff(midiNote);
        }
    }

    private void increaseOctave() {
        if (currentOctave < 9) {
            currentOctave++;
        }
    }

    private void decreaseOctave() {
        if (currentOctave > 0) {
            currentOctave--;
        }
    }

    private String getButtonLabel(int keyIndex) {
        int indexInOctave = keyIndex % 12;
        return noteNames[indexInOctave];
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Piano app = new Piano();
            app.AppGUI();
            app.keyboardSupport();
        });
    }
}