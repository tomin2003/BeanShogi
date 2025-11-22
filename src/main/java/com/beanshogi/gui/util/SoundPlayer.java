package com.beanshogi.gui.util;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

// TODO: introduce BGM

public class SoundPlayer {
    private static float volume = 0.5f; // Default volume (0.0 to 1.0)
    private static byte[] pieceAudioData;
    private static AudioFormat pieceAudioFormat;
    
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

            applyVolume(clip, volume);
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

            try (InputStream resourceStream = SoundPlayer.class.getResourceAsStream("/sound/piece_se.wav")) {
                if (resourceStream == null) {
                    System.err.println("Sound file not found: /sound/piece_se.wav");
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
}
