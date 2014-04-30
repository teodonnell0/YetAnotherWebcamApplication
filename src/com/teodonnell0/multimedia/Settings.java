/*
 * Copyright (c) 2014.
 * Travis O'Donnell
 * Frostburg State University
 * Computer Science
 */

package com.teodonnell0.multimedia;

import java.net.InetAddress;

/**
 * Created by travis on 3/23/14.
 */
public class Settings {

    public static final int FRAME_WIDTH = 640;
    public static final int FRAME_HEIGHT = 480;

    public static final int VIDEO_PORT = 48107;
    public static final int AUDIO_PORT = 38107;

    public static final int SAMPLE_RATE = 44100;
    public static final int SAMPLE_INTERVAL = 20;
    public static final int SAMPLE_SIZE = 2;
    public static final int BUFFER_SIZE = SAMPLE_INTERVAL * SAMPLE_INTERVAL * SAMPLE_SIZE * 2;

}
