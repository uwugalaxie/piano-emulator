package org.example;

import org.junit.Before;
import org.junit.Test;
import javax.sound.midi.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import static org.junit.Assert.*;

public class AppTest {
    private Robot robot;
    private Synthesizer synthesizer;
    private MidiChannel midiChannel;
    private Piano piano;

    @Before
    public void setUp() {
        try {
            robot = new Robot();

            synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
            midiChannel = synthesizer.getChannels()[0];

            piano = new Piano();
            piano.AppGUI();
            piano.keyboardSupport();
        } catch (AWTException | MidiUnavailableException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void buttonTest() throws InterruptedException {
        int[] keyCodes = { KeyEvent.VK_Z, KeyEvent.VK_S, KeyEvent.VK_X, KeyEvent.VK_D,
                KeyEvent.VK_C, KeyEvent.VK_V, KeyEvent.VK_G, KeyEvent.VK_B,
                KeyEvent.VK_H, KeyEvent.VK_N, KeyEvent.VK_J, KeyEvent.VK_M };

        for (int keyCode : keyCodes) {
            keyPress(keyCode);
            Thread.sleep(500);
            assertTrue(isSoundPlaying());

            Thread.sleep(1000);

            keyRelease(keyCode);
            Thread.sleep(1000);
            assertFalse(isSoundPlaying());
        }
    }

    @Test
    public void octaveTest() throws InterruptedException {
        buttonPress(KeyEvent.VK_DOWN);
        assertEquals(3, getCurrentOctave());
        keyPress(KeyEvent.VK_Z);
        Thread.sleep(500);
        keyRelease(KeyEvent.VK_Z);

        Thread.sleep(1000);

        buttonPress(KeyEvent.VK_UP);
        assertEquals(4, getCurrentOctave());
        keyPress(KeyEvent.VK_Z);
        Thread.sleep(500);
        keyRelease(KeyEvent.VK_Z);

        Thread.sleep(1000);

        buttonPress(KeyEvent.VK_UP);
        assertEquals(5, getCurrentOctave());
        keyPress(KeyEvent.VK_Z);
        Thread.sleep(500);
        keyRelease(KeyEvent.VK_Z);

        Thread.sleep(1000);
    }

    private void keyPress(int keyCode) {
        robot.keyPress(keyCode);
    }

    private void keyRelease(int keyCode) {
        robot.keyRelease(keyCode);
    }

    private void buttonPress(int keyCode) throws InterruptedException {
        robot.keyPress(keyCode);
        Thread.sleep(500);
        robot.keyRelease(keyCode);
    }

    private boolean isSoundPlaying() {
        return piano.isPlaying;
    }

    private int getCurrentOctave() {
        return piano.currentOctave;
    }
}