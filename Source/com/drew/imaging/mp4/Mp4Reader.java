package com.drew.imaging.mp4;

import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.StreamReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.mp4.*;
import com.drew.metadata.mp4.boxes.*;
import com.drew.metadata.mp4.media.Mp4SoundDirectory;
import com.drew.metadata.mp4.media.Mp4VideoDirectory;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;

public class Mp4Reader
{
    private StreamReader reader;

    public void extract(Metadata metadata, InputStream inputStream) throws IOException, DataFormatException
    {
        reader = new StreamReader(inputStream);
        reader.setMotorolaByteOrder(true);

        Container mp4 = new Container("root", 0, null);
        processContainer(reader, -1, mp4);
        mp4.printContainer();

        addMetadata(metadata, mp4);
    }

    private void processContainer(StreamReader reader, long atomEnd, Container parent)
    {
        try {
            while ((atomEnd == -1) ? true : reader.getPosition() < atomEnd) {

                Box box = new Box(reader);

                /*
                 * Determine if fourCC is container/atom and process accordingly
                 * Unknown atoms will be skipped
                 */
                if (Mp4ContainerTypes.contains(box)) {

                    Container container = new Container(box, parent);
                    parent.addContainer(container);
                    processContainer(reader, box.getSize() + reader.getPosition() - 8, container);

                } else if (BoxFactory.contains(box)) {

                    SequentialByteArrayReader byteReader = new SequentialByteArrayReader(reader.getBytes((int)box.getSize() - 8));
                    parent.addBox(BoxFactory.getBox(byteReader, box, parent));

                } else {

                    if (box.getSize() > 1) {
                        reader.skip(box.getSize() - 8);
                    } else if (box.getSize() == -1) {
                        break;
                    }

                    parent.addBox(box);

                }
            }
        } catch (IOException ignored) {
            System.out.println(ignored.getMessage());
        }
    }

    private void addMetadata(@NotNull Metadata metadata, @NotNull Container root)
    {
        Mp4Directory directory = new Mp4Directory();
        if (root.containsBoxOfType(FileTypeBox.class)) {
            FileTypeBox ftyp = root.getFirstBoxOfType(FileTypeBox.class);
            ftyp.addMetadata(directory);
        }
        if (root.containsContainerOfType("moov")) {
            Container moov = root.getFirstContainerOfType("moov");

            if (moov.containsBoxOfType(MovieHeaderBox.class)) {
                MovieHeaderBox mvhd = moov.getFirstBoxOfType(MovieHeaderBox.class);
                mvhd.addMetadata(directory);
            }

            if (moov.containsContainerOfType("trak")) {
                for (Container track : moov.getContainersOfType("trak")) {
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

    private Mp4SoundDirectory getSoundDirectory(@NotNull Container track)
    {
        Mp4SoundDirectory directory = new Mp4SoundDirectory();

        TrackHeaderBox tkhd = null;
        MediaHeaderBox mdhd = null;
        HandlerBox hdlr = null;
        SoundMediaHeaderBox smhd = null;
        AudioSampleEntry stsd = null;
        TimeToSampleBox stts = null;

        if (track.containsBoxOfType(TrackHeaderBox.class)) {
            tkhd = track.getFirstBoxOfType(TrackHeaderBox.class);
            tkhd.addMetadata(directory);
        }

        if (track.containsBoxOfType(MediaHeaderBox.class)) {
            mdhd = track.getFirstBoxOfType(MediaHeaderBox.class);
            mdhd.addMetadata(directory);
        }

        if (track.containsBoxOfType(HandlerBox.class)) {
            hdlr = track.getFirstBoxOfType(HandlerBox.class);
            hdlr.addMetadata(directory);
        }

        if (track.containsBoxOfType(VideoMediaHeaderBox.class)) {
            smhd = track.getFirstBoxOfType(SoundMediaHeaderBox.class);
            smhd.addMetadata(directory);
        }

        if (track.containsBoxOfType(VisualSampleEntry.class)) {
            stsd = track.getFirstBoxOfType(AudioSampleEntry.class);
            stsd.addMetadata(directory);
        }

        if (track.containsBoxOfType(TimeToSampleBox.class)) {
            stts = track.getFirstBoxOfType(TimeToSampleBox.class);
            stts.addMetadata(directory);
        }

        return directory;
    }

    private Mp4VideoDirectory getVideoDirectory(@NotNull Container track)
    {
        Mp4VideoDirectory directory = new Mp4VideoDirectory();

        TrackHeaderBox tkhd = null;
        MediaHeaderBox mdhd = null;
        HandlerBox hdlr = null;
        VideoMediaHeaderBox vmhd = null;
        VisualSampleEntry stsd = null;
        TimeToSampleBox stts = null;

        if (track.containsBoxOfType(TrackHeaderBox.class)) {
            tkhd = track.getFirstBoxOfType(TrackHeaderBox.class);
            tkhd.addMetadata(directory);
        }

        if (track.containsBoxOfType(MediaHeaderBox.class)) {
            mdhd = track.getFirstBoxOfType(MediaHeaderBox.class);
            mdhd.addMetadata(directory);
        }

        if (track.containsBoxOfType(HandlerBox.class)) {
            hdlr = track.getFirstBoxOfType(HandlerBox.class);
            hdlr.addMetadata(directory);
        }

        if (track.containsBoxOfType(VideoMediaHeaderBox.class)) {
            vmhd = track.getFirstBoxOfType(VideoMediaHeaderBox.class);
            vmhd.addMetadata(directory);
        }

        if (track.containsBoxOfType(VisualSampleEntry.class)) {
            stsd = track.getFirstBoxOfType(VisualSampleEntry.class);
            stsd.addMetadata(directory);
        }

        if (track.containsBoxOfType(TimeToSampleBox.class)) {
            stts = track.getFirstBoxOfType(TimeToSampleBox.class);
            stts.addMetadata(directory);
        }

        if (mdhd != null && stts != null) {
            directory.setLong(Mp4VideoDirectory.TAG_FRAME_RATE, mdhd.getTimescale() / stts.getEntries().get(0).getSampleCount());
        }

        return directory;
    }

    private TrackType getTrackType(Container track)
    {
        HandlerBox handlerBox;
        if (!track.getType().equals("trak"))
            return TrackType.Unknown;

        if (track.containsBoxOfType(HandlerBox.class))
            handlerBox = track.getFirstBoxOfType(HandlerBox.class);
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
}
