package com.beanshogi.gui.util;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Manages sound effects and background music playback for the game.
 * Handles volume control and audio clip management.
 */
public class SoundPlayer {
    private static final String PIECE_SOUND_FILE = "/sound/piece_se.wav";
    private static final String BACKGROUND_MUSIC_FILE = "/sound/bgm_loop.wav";
    
    private static float sfxVolume = 0.5f; // Default SFX volume (0.0 to 1.0)
    private static float bgmVolume = 0.5f; // Default BGM volume (0.0 to 1.0)
    
    private static Clip musicClip;
    
    /**
     * Sets the volume for sound effects.
     * @param vol Volume level from 0.0 (mute) to 1.0 (max)
     */
    public static void setSfxVolume(float vol) {
        sfxVolume = Math.max(0.0f, Math.min(1.0f, vol));
    }
    
    /**
     * Gets the current SFX volume level.
     * @return Volume level from 0.0 to 1.0
     */
    public static float getSfxVolume() {
        return sfxVolume;
    }
    
    /**
     * Sets the volume for background music.
     * @param vol Volume level from 0.0 (mute) to 1.0 (max)
     */
    public static void setBgmVolume(float vol) {
        bgmVolume = Math.max(0.0f, Math.min(1.0f, vol));
        updateMusicVolume(); // Update background music volume if playing
    }
    
    /**
     * Gets the current BGM volume level.
     * @return Volume level from 0.0 to 1.0
     */
    public static float getBgmVolume() {
        return bgmVolume;
    }
    
    /**
     * Plays the piece movement sound effect.
     * Creates a new clip each time to allow overlapping sounds.
     */
     public static void playPieceSfx() {
        try {
            AudioInputStream audioStream = createAudioStream(PIECE_SOUND_FILE, true);
            if (audioStream == null) return;

            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            audioStream.close();

            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP || event.getType() == LineEvent.Type.CLOSE) {
                    clip.close();
                }
            });

            applyVolume(clip, sfxVolume);
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

    /**
     * Starts playing background music in a loop.
     * The music will continue playing until stopped.
     */
    public static void playBackgroundMusic() {
        try {
            stopBackgroundMusic();

            AudioInputStream audioStream = createAudioStream(BACKGROUND_MUSIC_FILE, false);
            if (audioStream == null) return;

            musicClip = AudioSystem.getClip();
            musicClip.open(audioStream);
            audioStream.close();

            applyVolume(musicClip, bgmVolume);
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
            musicClip.start();
        } catch (Exception ex) {
            System.err.println("Error playing background music: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Stops the currently playing background music.
     */
    public static void stopBackgroundMusic() {
        if (musicClip != null && musicClip.isRunning()) {
            musicClip.stop();
            musicClip.close();
            musicClip = null;
        }
    }

    /**
     * Updates the volume of currently playing background music.
     */
    public static void updateMusicVolume() {
        if (musicClip != null && musicClip.isOpen()) {
            applyVolume(musicClip, bgmVolume);
        }
    }

    /**
     * Creates an AudioInputStream from a resource path.
     * @param resourcePath Path to the audio resource in classpath
     * @param isSfx Whether the audio is a sound effect
     * @return The created AudioInputStream, or null if an error occurred
     * @throws IOException If an I/O error occurs
     * @throws UnsupportedAudioFileException If the audio file format is not supported
     */
    private static AudioInputStream createAudioStream(String resourcePath, boolean isSfx) throws IOException, UnsupportedAudioFileException {
        byte[] audioData = loadAudioBytes(resourcePath);
        if (audioData == null) return null;

        ByteArrayInputStream byteStream = new ByteArrayInputStream(audioData);
        AudioInputStream audioIn = AudioSystem.getAudioInputStream(byteStream);
        return audioIn;
    }

    /**
     * Loads audio data from a resource path into a byte array.
     * @param resourcePath Path to the audio resource in classpath
     * @return Byte array of audio data, or null if an error occurred
     * @throws IOException If an I/O error occurs
     * @throws UnsupportedAudioFileException If the audio file format is not supported
     */
    private static byte[] loadAudioBytes(String resourcePath) throws IOException, UnsupportedAudioFileException {
        try (InputStream resourceStream = SoundPlayer.class.getResourceAsStream(resourcePath)) {
            if (resourceStream == null) {
                System.err.println("Audio file not found: " + resourcePath);
                return null;
            }

            try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
                byte[] data = new byte[4096];
                int bytesRead;
                while ((bytesRead = resourceStream.read(data)) != -1) {
                    buffer.write(data, 0, bytesRead);
                }
                return buffer.toByteArray();
            }
        }
    }
}
