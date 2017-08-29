package com.drew.metadata.mp4;

import com.drew.imaging.quicktime.QtContainer;
import com.drew.imaging.quicktime.QtContainerHandler;
import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.mp4.boxes.*;
import com.drew.metadata.mp4.media.Mp4SoundDirectory;
import com.drew.metadata.mp4.media.Mp4VideoDirectory;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Payton Garland
 */
public class Mp4ContainerHandler implements QtContainerHandler<Box, QtContainer>
{
    @Override
    public void addMetadata(Metadata metadata, QtContainer root)
    {
        Mp4Directory directory = new Mp4Directory();
        if (root.containsQtAtomOfType(FileTypeBox.class)) {
            FileTypeBox ftyp = root.getFirstQtAtomOfType(FileTypeBox.class);
            ftyp.addMetadata(directory);
        }
        if (root.containsQtContainerOfType("moov")) {
            QtContainer moov = root.getFirstQtContainerOfType("moov");

            if (moov.containsQtAtomOfType(MovieHeaderBox.class)) {
                MovieHeaderBox mvhd = moov.getFirstQtAtomOfType(MovieHeaderBox.class);
                mvhd.addMetadata(directory);
            }

            if (moov.containsQtContainerOfType("trak")) {
                for (QtContainer track : moov.getQtContainersOfType("trak")) {
                    TrackType trackType = getTrackType(track);
                    switch (trackType) {
                        case Video:
                            metadata.addDirectory(getVideoDirectory(track));
                            break;
                        case Sound:
                            metadata.addDirectory(getSoundDirectory(track));
                            break;
                        case Text:
                            break;
                        case Hint:
                            break;
                        case Meta:
                            break;
                        case Unknown:
                            break;
                    }
                }
            }
        }
        metadata.addDirectory(directory);
    }

    @Override
    public boolean isKnownContainer(Box box) {
        for (String s : _containerList) {
            if (box.getType().contains(s)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isKnownQtAtom(Box box) {
        for (String s : _boxList) {
            if (box.getType().equals(s)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Box next(SequentialReader reader) throws IOException
    {
        return new Box(reader);
    }

    @Override
    public Box getQtAtom(SequentialReader reader, Box box, QtContainer container) {
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
                if (container != null &&
                    container.getParent() != null &&
                    container.getParent().getParent() != null &&
                    container.getParent().getParent().getType().equals("mdia")) {
                    HandlerBox handlerBox = container.getParent().getParent().getFirstQtAtomOfType(HandlerBox.class);
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

    private Mp4SoundDirectory getSoundDirectory(@NotNull QtContainer track)
    {
        Mp4SoundDirectory directory = new Mp4SoundDirectory();

        TrackHeaderBox tkhd = null;
        MediaHeaderBox mdhd = null;
        HandlerBox hdlr = null;
        SoundMediaHeaderBox smhd = null;
        AudioSampleEntry stsd = null;
        TimeToSampleBox stts = null;

        if (track.containsQtAtomOfType(TrackHeaderBox.class)) {
            tkhd = track.getFirstQtAtomOfType(TrackHeaderBox.class);
            tkhd.addMetadata(directory);
        }

        if (track.containsQtAtomOfType(MediaHeaderBox.class)) {
            mdhd = track.getFirstQtAtomOfType(MediaHeaderBox.class);
            mdhd.addMetadata(directory);
        }

        if (track.containsQtAtomOfType(HandlerBox.class)) {
            hdlr = track.getFirstQtAtomOfType(HandlerBox.class);
            hdlr.addMetadata(directory);
        }

        if (track.containsQtAtomOfType(SoundMediaHeaderBox.class)) {
            smhd = track.getFirstQtAtomOfType(SoundMediaHeaderBox.class);
            smhd.addMetadata(directory);
        }

        if (track.containsQtAtomOfType(AudioSampleEntry.class)) {
            stsd = track.getFirstQtAtomOfType(AudioSampleEntry.class);
            stsd.addMetadata(directory);
        }

        if (track.containsQtAtomOfType(TimeToSampleBox.class)) {
            stts = track.getFirstQtAtomOfType(TimeToSampleBox.class);
            stts.addMetadata(directory);
        }

        return directory;
    }

    private Mp4VideoDirectory getVideoDirectory(@NotNull QtContainer track)
    {
        Mp4VideoDirectory directory = new Mp4VideoDirectory();

        TrackHeaderBox tkhd = null;
        MediaHeaderBox mdhd = null;
        HandlerBox hdlr = null;
        VideoMediaHeaderBox vmhd = null;
        VisualSampleEntry stsd = null;
        TimeToSampleBox stts = null;

        if (track.containsQtAtomOfType(TrackHeaderBox.class)) {
            tkhd = track.getFirstQtAtomOfType(TrackHeaderBox.class);
            tkhd.addMetadata(directory);
        }

        if (track.containsQtAtomOfType(MediaHeaderBox.class)) {
            mdhd = track.getFirstQtAtomOfType(MediaHeaderBox.class);
            mdhd.addMetadata(directory);
        }

        if (track.containsQtAtomOfType(HandlerBox.class)) {
            hdlr = track.getFirstQtAtomOfType(HandlerBox.class);
            hdlr.addMetadata(directory);
        }

        if (track.containsQtAtomOfType(VideoMediaHeaderBox.class)) {
            vmhd = track.getFirstQtAtomOfType(VideoMediaHeaderBox.class);
            vmhd.addMetadata(directory);
        }

        if (track.containsQtAtomOfType(VisualSampleEntry.class)) {
            stsd = track.getFirstQtAtomOfType(VisualSampleEntry.class);
            stsd.addMetadata(directory);
        }

        if (track.containsQtAtomOfType(TimeToSampleBox.class)) {
            stts = track.getFirstQtAtomOfType(TimeToSampleBox.class);
            stts.addMetadata(directory);
        }

        if (mdhd != null && stts != null) {
            directory.setLong(Mp4VideoDirectory.TAG_FRAME_RATE, mdhd.getTimescale() / stts.getEntries().get(0).getSampleCount());
        }

        return directory;
    }

    private TrackType getTrackType(QtContainer track)
    {
        HandlerBox handlerBox;
        if (!track.getType().equals("trak"))
            return TrackType.Unknown;

        if (track.containsQtAtomOfType(HandlerBox.class))
            handlerBox = track.getFirstQtAtomOfType(HandlerBox.class);
        else
            return TrackType.Unknown;

        if (handlerBox.getHandlerType().equals("vide")) {
            return TrackType.Video;
        } else if (handlerBox.getHandlerType().equals("soun")) {
            return TrackType.Sound;
        } else if (handlerBox.getHandlerType().equals("hint")) {
            return TrackType.Hint;
        } else if (handlerBox.getHandlerType().equals("text")) {
            return TrackType.Text;
        } else if (handlerBox.getHandlerType().equals("meta")) {
            return TrackType.Meta;
        } else {
            return TrackType.Unknown;
        }
    }

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

    public static final String BOX_MOVIE                            = "moov";
    public static final String BOX_USER_DATA                        = "udta";
    public static final String BOX_TRACK                            = "trak";
    public static final String BOX_MEDIA                            = "mdia";
    public static final String BOX_MEDIA_INFORMATION                = "minf";
    public static final String BOX_SAMPLE_TABLE                     = "stbl";
    public static final String BOX_METADATA_LIST                    = "ilst";
    public static final String BOX_METADATA                         = "meta";
    public static final String BOX_COMPRESSED_MOVIE                 = "cmov";
    public static final String BOX_MEDIA_TEXT                       = "text";
    public static final String BOX_MEDIA_SUBTITLE                   = "sbtl";
    public static final String BOX_MEDIA_NULL                       = "nmhd";

    public static ArrayList<String> _containerList = new ArrayList<String>();

    static {
        _containerList.add(BOX_MOVIE);
        _containerList.add(BOX_USER_DATA);
        _containerList.add(BOX_TRACK);
        _containerList.add(BOX_MEDIA);
        _containerList.add(BOX_MEDIA_INFORMATION);
        _containerList.add(BOX_SAMPLE_TABLE);
        _containerList.add(BOX_METADATA);
        _containerList.add(BOX_METADATA_LIST);
        _containerList.add(BOX_COMPRESSED_MOVIE);
        _containerList.add(BOX_MEDIA_TEXT);
        _containerList.add(BOX_MEDIA_SUBTITLE);
        _containerList.add(BOX_MEDIA_NULL);
    }
}
