package com.drew.metadata.mp4;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.TagDescriptor;
import static com.drew.metadata.mp4.Mp4Directory.*;

import java.util.ArrayList;
import java.util.Arrays;

public class Mp4Descriptor<T extends Directory> extends TagDescriptor<Mp4Directory> {

    public Mp4Descriptor(@NotNull Mp4Directory directory)
    {
        super(directory);
    }

    @Override
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case TAG_MAJOR_BRAND:
                return getMajorBrandDescription();
            case TAG_COMPATIBLE_BRANDS:
                return getCompatibleBrandsDescription();
            case TAG_DURATION:
                return getDurationDescription();
            default:
                return _directory.getString(tagType);
        }
    }

    private String getMajorBrandDescription()
    {
        String majorBrandKey = new String(_directory.getByteArray(TAG_MAJOR_BRAND));
        String majorBrandValue = Mp4Dictionary.lookup(TAG_MAJOR_BRAND, majorBrandKey);
        if (majorBrandValue != null) {
            return majorBrandValue;
        } else {
            return majorBrandKey;
        }
    }

    private String getCompatibleBrandsDescription()
    {
        String[] compatibleBrandKeys = _directory.getStringArray(TAG_COMPATIBLE_BRANDS);
        ArrayList<String> compatibleBrandsValues = new ArrayList<String>();
        for (String compatibleBrandsKey : compatibleBrandKeys) {
            String compatibleBrandsValue = Mp4Dictionary.lookup(TAG_MAJOR_BRAND, compatibleBrandsKey);
            if (compatibleBrandsValue != null) {
                compatibleBrandsValues.add(compatibleBrandsValue);
            } else {
                compatibleBrandsValues.add(compatibleBrandsKey);
            }
        }
        return Arrays.toString(compatibleBrandsValues.toArray());
    }

    private String getDurationDescription()
    {
        Long durationObject = _directory.getLongObject(TAG_DURATION);
        if (durationObject == null)
            return null;
        long duration = durationObject;

        Integer hours = (int)duration / (int)(Math.pow(60, 2));
        Integer minutes = ((int)duration / (int)(Math.pow(60, 1))) - (hours * 60);
        Integer seconds = (int)Math.ceil((duration / (Math.pow(60, 0))) - (minutes * 60));
        return String.format("%1$02d:%2$02d:%3$02d", hours, minutes, seconds);
    }
}
