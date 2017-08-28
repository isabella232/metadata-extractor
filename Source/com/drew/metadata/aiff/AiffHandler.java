package com.drew.metadata.aiff;

import com.drew.imaging.iff.IffHandler;
import com.drew.lang.ByteArrayReader;
import com.drew.metadata.Metadata;

import java.io.IOException;

/**
 * Implementation of {@link IffHandler} specialising in Aiff support.
 *
 * Extracts data from chunk types:
 *
 * <ul>
 *     <li><code>"comm"</code>: channel count, sample frame count, sample size, sample rate, and duration</li>
 * </ul>
 *
 * Sources: http://www-mmsp.ece.mcgill.ca/Documents/AudioFormats/AIFF/AIFF.html
 *
 * @author Payton Garland
 */
public class AiffHandler extends IffHandler
{
    public AiffHandler(Metadata metadata, AiffDirectory directory)
    {
        super(metadata, directory);
    }

    @Override
    public boolean shouldAcceptIffIdentifier(String identifier)
    {
        return identifier.equals(AiffDirectory.FORMAT);
    }

    @Override
    public boolean shouldAcceptChunk(String fourCC)
    {
        return fourCC.equals(AiffDirectory.CHUNK_COMMON);
    }

    @Override
    public boolean shouldAcceptList(String fourCC)
    {
        return false;
    }

    @Override
    public void processChunk(String fourCC, byte[] payload)
    {
        try {
            ByteArrayReader reader = new ByteArrayReader(payload);
            if (fourCC.equals(AiffDirectory.CHUNK_COMMON)) {
                _directory.setInt(AiffDirectory.TAG_NUMBER_CHANNELS, reader.getInt16(0));
                _directory.setLong(AiffDirectory.TAG_NUMBER_SAMPLE_FRAMES, reader.getUInt32(2));
                _directory.setInt(AiffDirectory.TAG_SAMPLE_SIZE, reader.getInt16(6));
                long sampleRate = calculateIEEE754FloatingPoint(reader.getBytes(8, 10));
                _directory.setLong(AiffDirectory.TAG_SAMPLE_RATE, sampleRate);

                double duration = (double)reader.getUInt32(2) / (double)sampleRate;
                Integer hours = (int)duration / (int)(Math.pow(60, 2));
                Integer minutes = ((int)duration / (int)(Math.pow(60, 1))) - (hours * 60);
                Integer seconds = (int)Math.round((duration / (Math.pow(60, 0))) - (minutes * 60));
                String time = String.format("%1$02d:%2$02d:%3$02d", hours, minutes, seconds);

                _directory.setString(AiffDirectory.TAG_DURATION, time);
            }
        } catch (IOException ex) {
            _directory.addError("Error processing " + fourCC + " chunk: " + ex.getMessage());
        }
    }

    /**
     * Sample rate is stored in an "80 bit IEEE Standard 754 floating point number (Standard Apple
     * Numeric Environment [SANE] data type Extended)" as per Apple's original documentation.
     */
    private long calculateIEEE754FloatingPoint(byte[] bytes) throws IOException
    {
        ByteArrayReader reader = new ByteArrayReader(bytes);
        boolean positive = ((reader.getUInt16(0) & 0x8000) == 0) ? true : false;
        int exponent = (reader.getUInt16(0) & 0x7FFF) - 16383;
        long mantissa = reader.getInt64(2);

        if (positive) {
            return mantissa >>> (63 - exponent);
        } else {
            return -1 * (mantissa >>> (63 - exponent));
        }
    }
}
