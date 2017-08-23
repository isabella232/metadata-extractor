package com.drew.imaging;

/**
 * @author Payton Garland
 */
public class MetadataOptions
{
    boolean extractIccProfile;

    public MetadataOptions() {
        this.extractIccProfile = false;
    }

    public void setExtractIccProfile(boolean option) {
        this.extractIccProfile = option;
    }

    public void reset()
    {
        this.extractIccProfile = false;
    }
}
