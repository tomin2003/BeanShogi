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
    private static byte[] pieceAudioData;
    private static AudioFormat pieceAudioFormat;
    
    private static byte[] musicAudioData;
    private static AudioFormat musicAudioFormat;
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
     * Sets the volume for sound playback (legacy method for SFX).
     * @param vol Volume level from 0.0 (mute) to 1.0 (max)
     * @deprecated Use {@link #setSfxVolume(float)} instead
     */
    @Deprecated
    public static void setVolume(float vol) {
        setSfxVolume(vol);
    }
    
    /**
     * Gets the current volume level (legacy method for SFX).
     * @return Volume level from 0.0 to 1.0
     * @deprecated Use {@link #getSfxVolume()} instead
     */
    @Deprecated
    public static float getVolume() {
        return getSfxVolume();
    }
    
    /**
     * Plays the piece movement sound effect.
     * Creates a new clip each time to allow overlapping sounds.
     */
    public static void playPieceSfx() {
        try {
            AudioInputStream audioStream = createPieceStream();
            if (audioStream == null) {
                return;
            }

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

    private static AudioInputStream createPieceStream() throws IOException, UnsupportedAudioFileException {
        loadPieceAudio();
        if (pieceAudioData == null || pieceAudioFormat == null) {
            return null;
        }

        ByteArrayInputStream byteStream = new ByteArrayInputStream(pieceAudioData);
        long frameLength = pieceAudioData.length / pieceAudioFormat.getFrameSize();
        return new AudioInputStream(byteStream, pieceAudioFormat, frameLength);
    }

    private static void loadPieceAudio() throws IOException, UnsupportedAudioFileException {
        if (pieceAudioData != null) {
            return;
        }

        synchronized (SoundPlayer.class) {
            if (pieceAudioData != null) {
                return;
            }

            try (InputStream resourceStream = SoundPlayer.class.getResourceAsStream(PIECE_SOUND_FILE)) {
                if (resourceStream == null) {
                    System.err.println("Sound file not found: " + PIECE_SOUND_FILE);
                    return;
                }

                try (AudioInputStream audioIn = AudioSystem.getAudioInputStream(resourceStream);
                     ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
                    pieceAudioFormat = audioIn.getFormat();

                    byte[] data = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = audioIn.read(data)) != -1) {
                        buffer.write(data, 0, bytesRead);
                    }

                    pieceAudioData = buffer.toByteArray();
                }
            }
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
     * Plays the music file defined in BACKGROUND_MUSIC_FILE constant.
     */
    public static void playBackgroundMusic() {
        try {
            stopBackgroundMusic(); // Stop any currently playing music
            
            AudioInputStream audioStream = createMusicStream();
            if (audioStream == null) {
                return;
            }

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

    private static AudioInputStream createMusicStream() throws IOException, UnsupportedAudioFileException {
        loadMusicAudio();
        if (musicAudioData == null || musicAudioFormat == null) {
            return null;
        }

        ByteArrayInputStream byteStream = new ByteArrayInputStream(musicAudioData);
        long frameLength = musicAudioData.length / musicAudioFormat.getFrameSize();
        return new AudioInputStream(byteStream, musicAudioFormat, frameLength);
    }

    private static void loadMusicAudio() throws IOException, UnsupportedAudioFileException {
        if (musicAudioData != null) {
            return;
        }

        synchronized (SoundPlayer.class) {
            if (musicAudioData != null) {
                return;
            }

            try (InputStream resourceStream = SoundPlayer.class.getResourceAsStream(BACKGROUND_MUSIC_FILE)) {
                if (resourceStream == null) {
                    System.err.println("Music file not found: " + BACKGROUND_MUSIC_FILE);
                    return;
                }

                try (AudioInputStream audioIn = AudioSystem.getAudioInputStream(resourceStream);
                     ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
                    musicAudioFormat = audioIn.getFormat();

                    byte[] data = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = audioIn.read(data)) != -1) {
                        buffer.write(data, 0, bytesRead);
                    }

                    musicAudioData = buffer.toByteArray();
                }
            }
        }
    }
}
