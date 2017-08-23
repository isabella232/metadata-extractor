package com.drew.imaging;

import com.drew.lang.annotations.NotNull;

/**
 * @author Payton Garland
 */
public class MetadataOptions
{
    private final boolean extractIccProfile;

    public MetadataOptions(@NotNull final boolean extractIccProfile) {
        this.extractIccProfile = extractIccProfile;
    }

    public boolean shouldExtractIccProfile() {
        return extractIccProfile;
    }

    public static class MetadataOptionsBuilder
    {
        boolean extractIccProfile;

        public MetadataOptionsBuilder() {

        }

        public MetadataOptionsBuilder setExtractIccProfile(@NotNull boolean option) {
            this.extractIccProfile = option;
            return this;
        }

        public MetadataOptions create() {
            return new MetadataOptions(
                extractIccProfile
            );
        }
    }
}
