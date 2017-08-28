package com.drew.metadata.mov;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.TagDescriptor;

import java.util.ArrayList;
import java.util.Arrays;

public class QtDescriptor extends TagDescriptor<QtDirectory> {

    public QtDescriptor(@NotNull QtDirectory directory)
    {
        super(directory);
    }

    @Override
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case (QtDirectory.TAG_MAJOR_BRAND):
                return getMajorBrandDescription(tagType);
            case (QtDirectory.TAG_COMPATIBLE_BRANDS):
                return getCompatibleBrandsDescription(tagType);
            case (QtDirectory.TAG_DURATION):
                return getDurationDescription();
            default:
                return _directory.getString(tagType);
        }
    }

    private String getMajorBrandDescription(int tagType)
    {
        String majorBrandKey = new String(_directory.getByteArray(tagType));
        String majorBrandValue = QtDictionary.lookup(QtDirectory.TAG_MAJOR_BRAND, majorBrandKey);
        if (majorBrandValue != null) {
            return majorBrandValue;
        } else {
            return majorBrandKey;
        }
    }

    private String getCompatibleBrandsDescription(int tagType)
    {
        String[] compatibleBrandKeys = _directory.getStringArray(tagType);
        ArrayList<String> compatibleBrandsValues = new ArrayList<String>();
        for (String compatibleBrandsKey : compatibleBrandKeys) {
            String compatibleBrandsValue = QtDictionary.lookup(QtDirectory.TAG_MAJOR_BRAND, compatibleBrandsKey);
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
        Long durationObject = _directory.getLongObject(QtDirectory.TAG_DURATION);
        if (durationObject == null)
            return null;
        long duration = (long)durationObject;

        Integer hours = (int) duration / (int) (Math.pow(60, 2));
        Integer minutes = ((int) duration / (int) (Math.pow(60, 1))) - (hours * 60);
        Integer seconds = (int) Math.ceil((duration / (Math.pow(60, 0))) - (minutes * 60));
        return String.format("%1$02d:%2$02d:%3$02d", hours, minutes, seconds);
    }
}
