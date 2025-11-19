package com.beanshogi.gui.util;

import javax.sound.sampled.*;
import java.io.InputStream;

public class SoundPlayer {
    private static float volume = 0.5f; // Default volume (0.0 to 1.0)
    
    /**
     * Sets the volume for sound playback.
     * @param vol Volume level from 0.0 (mute) to 1.0 (max)
     */
    public static void setVolume(float vol) {
        volume = Math.max(0.0f, Math.min(1.0f, vol));
    }
    
    /**
     * Gets the current volume level.
     * @return Volume level from 0.0 to 1.0
     */
    public static float getVolume() {
        return volume;
    }
    
    public static void playPieceSfx() {
        try {
            InputStream audioStream = SoundPlayer.class.getResourceAsStream("/sound/piece_se.wav");
            if (audioStream == null) {
                System.err.println("Sound file not found: /sound/piece_se.wav");
                return;
            }
            
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(audioStream);

            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            
            // Apply volume control
            applyVolume(clip, volume);
            
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Applies volume to a clip.
     * @param clip The audio clip
     * @param volume Volume level from 0.0 to 1.0
     */
    private static void applyVolume(Clip clip, float volume) {
        try {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            
            // Use logarithmic scaling for more natural volume control
            float dB;
            if (volume <= 0.0f) {
                dB = gainControl.getMinimum();
            } else {
                dB = 20f * (float) Math.log10(volume);
                // Clamp to valid range
                dB = Math.max(gainControl.getMinimum(), Math.min(gainControl.getMaximum(), dB));
            }
            
            gainControl.setValue(dB);
        } catch (Exception e) {
            // Volume control not supported
            System.err.println("Volume control not supported for this audio clip");
        }
    }
}
