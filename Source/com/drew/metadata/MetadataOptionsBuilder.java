package com.drew.metadata;

import com.drew.lang.annotations.NotNull;

/**
 * @author Payton Garland
 */
public class MetadataOptionsBuilder
{
    boolean extractIccProfile;
    boolean displayXmp;

    public MetadataOptionsBuilder() {

    }

    public MetadataOptionsBuilder setExtractIccProfile(@NotNull boolean option) {
        this.extractIccProfile = option;
        return this;
    }

    public MetadataOptionsBuilder setDisplayXmp(@NotNull boolean option) {
        this.displayXmp = option;
        return this;
    }

    public MetadataOptions create() {
        return new MetadataOptions(
            extractIccProfile,
            displayXmp
        );
    }
}
