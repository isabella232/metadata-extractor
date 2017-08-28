package com.drew.metadata;

import com.drew.lang.annotations.NotNull;

/**
 * @author Payton Garland
 */
public class MetadataOptions
{
    private final boolean extractIccProfile;
    private final boolean displayXmp;

    public MetadataOptions(@NotNull final boolean extractIccProfile,
                           @NotNull final boolean displayXmp) {
        this.extractIccProfile = extractIccProfile;
        this.displayXmp = displayXmp;
    }

    public boolean shouldExtractIccProfile() {
        return extractIccProfile;
    }

    public boolean shouldDisplayXmp() {
        return displayXmp;
    }
}
