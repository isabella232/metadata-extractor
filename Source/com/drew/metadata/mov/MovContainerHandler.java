package com.drew.metadata.mov;

import com.drew.imaging.quicktime.QtContainer;
import com.drew.imaging.quicktime.QtContainerHandler;
import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.atoms.*;
import com.drew.metadata.mov.media.QtSoundDirectory;
import com.drew.metadata.mov.media.QtVideoDirectory;
import com.drew.metadata.mp4.TrackType;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Payton Garland
 */
@SuppressWarnings("Duplicates")
public class MovContainerHandler implements QtContainerHandler<Atom, QtContainer>
{
    @Override
    public void addMetadata(Metadata metadata, QtContainer root)
    {
        QtDirectory directory = new QtDirectory();
        metadata.addDirectory(directory);
        if (root.containsQtAtomOfType(FileTypeCompatibilityAtom.class)) {
            FileTypeCompatibilityAtom ftyp = root.getFirstQtAtomOfType(FileTypeCompatibilityAtom.class);
            ftyp.addMetadata(directory);
        }
        if (root.containsQtContainerOfType("moov")) {
            QtContainer moov = root.getFirstQtContainerOfType("moov");

            if (moov.containsQtAtomOfType(MovieHeaderAtom.class)) {
                MovieHeaderAtom mvhd = moov.getFirstQtAtomOfType(MovieHeaderAtom.class);
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
    }

    @Override
    public boolean isKnownContainer(Atom atom)
    {
        for (String s : _containerList) {
            if (atom.getType().contains(s)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isKnownQtAtom(Atom atom)
    {
        for (String s : _atomList) {
            if (atom.getType().equals(s)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Atom next(SequentialReader reader) throws IOException
    {
        return new Atom(reader);
    }

    @Override
    public Atom getQtAtom(SequentialReader reader, Atom atom, QtContainer container) {
        try {
            if (atom.getType().equals(ATOM_FILE_TYPE)) {
                return new FileTypeCompatibilityAtom(reader, atom);
            } else if (atom.getType().equals(ATOM_MOVIE_HEADER)) {
                return new MovieHeaderAtom(reader, atom);
            } else if (atom.getType().equals(ATOM_VIDEO_MEDIA_INFO)) {
                return new VideoInformationMediaHeaderAtom(reader, atom);
            } else if (atom.getType().equals(ATOM_SOUND_MEDIA_INFO)) {
                return new SoundInformationMediaHeaderAtom(reader, atom);
            } else if (atom.getType().equals(ATOM_HANDLER)) {
                return new HandlerReferenceAtom(reader, atom);
            } else if (atom.getType().equals(ATOM_SAMPLE_DESCRIPTION)) {
                if (container != null &&
                    container.getParent() != null &&
                    container.getParent().getParent() != null &&
                    container.getParent().getParent().getType().equals("mdia")) {
                    HandlerReferenceAtom hdlr = container.getParent().getParent().getFirstQtAtomOfType(HandlerReferenceAtom.class);
                    if (hdlr.getComponentSubtype().equals("vide")) {
                        return new VideoSampleDescriptionAtom(reader, atom);
                    } else if (hdlr.getComponentSubtype().equals("soun")) {
                        return new SoundSampleDescriptionAtom(reader, atom);
                    } else if (hdlr.getComponentSubtype().equals("time")) {

                    }
                }
            } else if (atom.getType().equals(ATOM_TIME_TO_SAMPLE)) {
                return new TimeToSampleAtom(reader, atom);
            } else if (atom.getType().equals(ATOM_MEDIA_HEADER)) {
                return new MediaHeaderAtom(reader, atom);
            }
        } catch (IOException ignored) {

        }
        return atom;
    }

    private QtSoundDirectory getSoundDirectory(@NotNull QtContainer track)
    {
        QtSoundDirectory directory = new QtSoundDirectory();

        MediaHeaderAtom mdhd = null;
        HandlerReferenceAtom hdlr = null;
        SoundInformationMediaHeaderAtom smhd = null;
        SoundSampleDescriptionAtom stsd = null;
        TimeToSampleAtom stts = null;

        if (track.containsQtAtomOfType(MediaHeaderAtom.class)) {

        }

        if (track.containsQtAtomOfType(HandlerReferenceAtom.class)) {

        }

        if (track.containsQtAtomOfType(SoundInformationMediaHeaderAtom.class)) {
            smhd = track.getFirstQtAtomOfType(SoundInformationMediaHeaderAtom.class);
            smhd.addMetadata(directory);
        }

        if (track.containsQtAtomOfType(SoundSampleDescriptionAtom.class)) {
            stsd = track.getFirstQtAtomOfType(SoundSampleDescriptionAtom.class);
            stsd.addMetadata(directory);
        }

        if (track.containsQtAtomOfType(TimeToSampleAtom.class)) {
            stts = track.getFirstQtAtomOfType(TimeToSampleAtom.class);
            stts.addMetadata(directory);
        }

        return directory;
    }

    private QtVideoDirectory getVideoDirectory(@NotNull QtContainer track)
    {
        QtVideoDirectory directory = new QtVideoDirectory();

        VideoInformationMediaHeaderAtom vmhd = null;
        VideoSampleDescriptionAtom stsd = null;
        TimeToSampleAtom stts = null;

        if (track.containsQtAtomOfType(VideoInformationMediaHeaderAtom.class)) {
            vmhd = track.getFirstQtAtomOfType(VideoInformationMediaHeaderAtom.class);
            vmhd.addMetadata(directory);
        }

        if (track.containsQtAtomOfType(VideoSampleDescriptionAtom.class)) {
            stsd = track.getFirstQtAtomOfType(VideoSampleDescriptionAtom.class);
            stsd.addMetadata(directory);
        }

        if (track.containsQtAtomOfType(TimeToSampleAtom.class)) {
            stts = track.getFirstQtAtomOfType(TimeToSampleAtom.class);
            stts.addMetadata(directory);
        }

        return directory;
    }

    private TrackType getTrackType(QtContainer track)
    {
        HandlerReferenceAtom hdlr;
        if (!track.getType().equals("trak"))
            return TrackType.Unknown;

        if (track.containsQtAtomOfType(HandlerReferenceAtom.class))
            hdlr = track.getFirstQtAtomOfType(HandlerReferenceAtom.class);
        else
            return TrackType.Unknown;

        if (hdlr.getComponentSubtype().equals("vide")) {
            return TrackType.Video;
        } else if (hdlr.getComponentSubtype().equals("soun")) {
            return TrackType.Sound;
        } else if (hdlr.getComponentSubtype().equals("hint")) {
            return TrackType.Hint;
        } else if (hdlr.getComponentSubtype().equals("text")) {
            return TrackType.Text;
        } else if (hdlr.getComponentSubtype().equals("meta")) {
            return TrackType.Meta;
        } else {
            return TrackType.Unknown;
        }
    }

    public static final String ATOM_FILE_TYPE                = "ftyp";
    public static final String ATOM_MOVIE_HEADER             = "mvhd";
    public static final String ATOM_VIDEO_MEDIA_INFO         = "vmhd";
    public static final String ATOM_SOUND_MEDIA_INFO         = "smhd";
    public static final String ATOM_BASE_MEDIA_INFO          = "gmhd";
    public static final String ATOM_TIMECODE_MEDIA_INFO      = "tcmi";
    public static final String ATOM_HANDLER                  = "hdlr";
    public static final String ATOM_KEYS                     = "keys";
    public static final String ATOM_DATA                     = "data";
    public static final String ATOM_SAMPLE_DESCRIPTION       = "stsd";
    public static final String ATOM_TIME_TO_SAMPLE           = "stts";
    public static final String ATOM_MEDIA_HEADER             = "mdhd";

    public static ArrayList<String> _atomList = new ArrayList<String>();

    static {
        _atomList.add(ATOM_FILE_TYPE);
        _atomList.add(ATOM_MOVIE_HEADER);
        _atomList.add(ATOM_VIDEO_MEDIA_INFO);
        _atomList.add(ATOM_SOUND_MEDIA_INFO);
        _atomList.add(ATOM_BASE_MEDIA_INFO);
        _atomList.add(ATOM_TIMECODE_MEDIA_INFO);
        _atomList.add(ATOM_HANDLER);
        _atomList.add(ATOM_KEYS);
        _atomList.add(ATOM_DATA);
        _atomList.add(ATOM_SAMPLE_DESCRIPTION);
        _atomList.add(ATOM_TIME_TO_SAMPLE);
        _atomList.add(ATOM_MEDIA_HEADER);
    }

    public static final String ATOM_MOVIE                       = "moov";
    public static final String ATOM_USER_DATA                   = "udta";
    public static final String ATOM_TRACK                       = "trak";
    public static final String ATOM_MEDIA                       = "mdia";
    public static final String ATOM_MEDIA_INFORMATION           = "minf";
    public static final String ATOM_SAMPLE_TABLE                = "stbl";
    public static final String ATOM_METADATA_LIST               = "ilst";
    public static final String ATOM_METADATA                    = "meta";
    public static final String ATOM_COMPRESSED_MOVIE            = "cmov";
    public static final String ATOM_MEDIA_TEXT                  = "text";
    public static final String ATOM_MEDIA_SUBTITLE              = "sbtl";
    public static final String ATOM_MEDIA_BASE                  = "gmhd";

    public static ArrayList<String> _containerList = new ArrayList<String>();

    static {
        _containerList.add(ATOM_MOVIE);
        _containerList.add(ATOM_USER_DATA);
        _containerList.add(ATOM_TRACK);
        _containerList.add(ATOM_MEDIA);
        _containerList.add(ATOM_MEDIA_INFORMATION);
        _containerList.add(ATOM_SAMPLE_TABLE);
        _containerList.add(ATOM_METADATA);
        _containerList.add(ATOM_METADATA_LIST);
        _containerList.add(ATOM_COMPRESSED_MOVIE);
        _containerList.add(ATOM_MEDIA_TEXT);
        _containerList.add(ATOM_MEDIA_SUBTITLE);
        _containerList.add(ATOM_MEDIA_BASE);
    }
}
