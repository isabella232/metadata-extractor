package com.drew.metadata.mp4.boxes;

import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.mp4.Container;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Payton Garland
 */
@SuppressWarnings("Duplicates")
public class BoxFactory
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
    public static final String BOX_TRACK_HEADER                     = "tkhd";

    public static String currentType = "";

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
        _boxList.add(BOX_TRACK_HEADER);
    }

    public static Box getBox(@NotNull SequentialReader reader, @NotNull Box box, @NotNull Container parent)
    {
        try {
            if (box.getType().equals(BOX_FILE_TYPE)) {
                return new FileTypeBox(reader, box);
            } else if (box.getType().equals(BOX_MOVIE_HEADER)) {
                return new MovieHeaderBox(reader, box);
            } else if (box.getType().equals(BOX_VIDEO_MEDIA_INFO)) {
                return new VideoMediaHeaderBox(reader, box);
            } else if (box.getType().equals(BOX_SOUND_MEDIA_INFO)) {
                return new SoundMediaHeaderBox(reader, box);
            } else if (box.getType().equals(BOX_HINT_MEDIA_INFO)) {
                return new HintMediaHeaderBox(reader, box);
            } else if (box.getType().equals(BOX_NULL_MEDIA_INFO)) {
                return box;
            } else if (box.getType().equals(BOX_HANDLER)) {
                return new HandlerBox(reader, box);
            } else if (box.getType().equals(BOX_SAMPLE_DESCRIPTION)) {
                if (parent.getParent() != null &&
                    parent.getParent().getParent() != null) {
                    HandlerBox handlerBox = parent.getParent().getParent().getFirstBoxOfType(HandlerBox.class);
                    if (handlerBox.getHandlerType().equals("vide")) {
                        return new VisualSampleEntry(reader, box);
                    } else if (handlerBox.getHandlerType().equals("soun")) {
                        return new AudioSampleEntry(reader, box);
                    } else {
                        return new SampleEntry(reader, box);
                    }
                }
            } else if (box.getType().equals(BOX_TIME_TO_SAMPLE)) {
                return new TimeToSampleBox(reader, box);
            } else if (box.getType().equals(BOX_MEDIA_HEADER)) {
                return new MediaHeaderBox(reader, box);
            } else if (box.getType().equals(BOX_TRACK_HEADER)) {
                return new TrackHeaderBox(reader, box);
            } else if (box.getType().equals(BOX_TIME_TO_SAMPLE)) {
                return new TimeToSampleBox(reader, box);
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
