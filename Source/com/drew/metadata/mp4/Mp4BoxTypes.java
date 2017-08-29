package com.drew.metadata.mp4;

import com.drew.lang.SequentialReader;
import com.drew.metadata.mp4.boxes.Box;
import com.drew.metadata.mp4.boxes.FileTypeBox;
import com.drew.metadata.mp4.boxes.MovieHeaderBox;
import com.drew.metadata.mp4.boxes.VideoMediaHeaderBox;
import org.apache.pdfbox.io.SequentialRead;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Payton Garland
 */
public class Mp4BoxTypes
{
    public static final String BOX_FILE_TYPE                        = "ftyp";
    public static final String BOX_MOVIE_HEADER                     = "mvhd";
    public static final String BOX_VIDEO_MEDIA_INFO                 = "vmhd";
    public static final String BOX_SOUND_MEDIA_INFO                 = "smhd";
    public static final String BOX_HINT_MEDIA_INFO                  = "hmhd";
    public static final String BOX_NULL_MEDIA_INFO                  = "nmhd";
    public static final String BOX_HANDLER                          = "hdlr";
    public static final String BOX_SAMPLE_DESCRIPTION               = "stsd";
    public static final String BOX_TIME_TO_SAMPLE                   = "stts";
    public static final String BOX_MEDIA_HEADER                     = "mdhd";

    public static ArrayList<String> _boxList = new ArrayList<String>();

    static {
        _boxList.add(BOX_FILE_TYPE);
        _boxList.add(BOX_MOVIE_HEADER);
        _boxList.add(BOX_VIDEO_MEDIA_INFO);
        _boxList.add(BOX_SOUND_MEDIA_INFO);
        _boxList.add(BOX_HINT_MEDIA_INFO);
        _boxList.add(BOX_NULL_MEDIA_INFO);
        _boxList.add(BOX_HANDLER);
        _boxList.add(BOX_SAMPLE_DESCRIPTION);
        _boxList.add(BOX_TIME_TO_SAMPLE);
        _boxList.add(BOX_MEDIA_HEADER);
    }

    public static Box createBox(SequentialReader reader, Box box)
    {
        try {
            if (box.getType().equals(BOX_FILE_TYPE)) {
                return new FileTypeBox(reader, box);
            } else if (box.getType().equals(BOX_MOVIE_HEADER)) {
                return new MovieHeaderBox(reader, box);
            } else if (box.getType().equals(BOX_VIDEO_MEDIA_INFO)) {
                return new VideoMediaHeaderBox(reader, box);
            }
        } catch (IOException ignored) {

        }
        return box;
    }

    public static boolean contains(Box box)
    {
        for (String s : _boxList) {
            if (box.getType().equals(s)) {
                return true;
            }
        }
        return false;
    }
}
