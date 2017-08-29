package com.drew.metadata.heif;

import com.drew.imaging.quicktime.QtContainer;
import com.drew.imaging.quicktime.QtContainerHandler;
import com.drew.lang.SequentialReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.heif.boxes.*;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Payton Garland
 */
public class HeifContainerHandler implements QtContainerHandler<Box, QtContainer>
{
    @Override
    public void addMetadata(Metadata metadata, QtContainer qtContainer)
    {
        HeifDirectory directory = new HeifDirectory();
        metadata.addDirectory(directory);

        if (qtContainer.containsQtAtomOfType(FileTypeBox.class)) {
            FileTypeBox fileTypeBox = qtContainer.getFirstQtAtomOfType(FileTypeBox.class);
            fileTypeBox.addMetadata(directory);
            if (!fileTypeBox.compatibleBrands.contains("mif1")) {
                directory.addError("File Type Box does not contain required brand, mif1");
            }
        }


    }

    @Override
    public boolean isKnownContainer(Box box)
    {
        for (String s : _containerList) {
            if (box.getType().contains(s)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isKnownQtAtom(Box box)
    {
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
    public Box getQtAtom(SequentialReader reader, Box box, QtContainer qtContainer)
    {
        try {
            if (box.getType().equals(BOX_FILE_TYPE)) {
                return new FileTypeBox(reader, box);
            } else if (box.getType().equals(BOX_PRIMARY_ITEM)) {
                return new PrimaryItemBox(reader, box);
            } else if (box.getType().equals(BOX_ITEM_PROTECTION)) {
                return new ItemProtectionBox(reader, box);
            } else if (box.getType().equals(BOX_ITEM_INFO)) {
                return new ItemInfoBox(reader, box);
            } else if (box.getType().equals(BOX_ITEM_LOCATION)) {
                return new ItemLocationBox(reader, box);
            } else if (box.getType().equals(BOX_HANDLER)) {
                return new HandlerBox(reader, box);
            } else if (box.getType().equals(BOX_HVC1)) {
                return new HEVCDecoderConfigurationRecord(reader, box);
            } else if (box.getType().equals(BOX_IMAGE_SPATIAL_EXTENTS)) {
                return new ImageSpatialExtentsProperty(reader, box);
            } else if (box.getType().equals(BOX_AUXILIARY_TYPE_PROPERTY)) {
                return new AuxiliaryTypeProperty(reader, box);
            } else if (box.getType().equals(BOX_HEVC_CONFIGURATION)) {
                return new HEVCDecoderConfigurationRecord(reader, box);
            }
        } catch (IOException ignored) {

        }
        return box;
    }

    public static final String BOX_FILE_TYPE                        = "ftyp";
    public static final String BOX_PRIMARY_ITEM                     = "pitm";
    public static final String BOX_ITEM_PROTECTION                  = "ipro";
    public static final String BOX_ITEM_INFO                        = "iinf";
    public static final String BOX_ITEM_LOCATION                    = "iloc";
    public static final String BOX_HANDLER                          = "hdlr";
    public static final String BOX_HVC1                             = "hvc1";
    public static final String BOX_IMAGE_SPATIAL_EXTENTS            = "ispe";
    public static final String BOX_AUXILIARY_TYPE_PROPERTY          = "auxC";
    public static final String BOX_HEVC_CONFIGURATION               = "hvcC";

    public static ArrayList<String> _boxList = new ArrayList<String>();

    static {
        _boxList.add(BOX_FILE_TYPE);
        _boxList.add(BOX_ITEM_PROTECTION);
        _boxList.add(BOX_PRIMARY_ITEM);
        _boxList.add(BOX_ITEM_INFO);
        _boxList.add(BOX_ITEM_LOCATION);
        _boxList.add(BOX_HANDLER);
        _boxList.add(BOX_HVC1);
        _boxList.add(BOX_IMAGE_SPATIAL_EXTENTS);
        _boxList.add(BOX_AUXILIARY_TYPE_PROPERTY);
        _boxList.add(BOX_HEVC_CONFIGURATION);
    }

    public static final String BOX_METADATA                         = "meta";
    public static final String BOX_IMAGE_PROPERTY                   = "iprp";
    public static final String BOX_ITEM_PROPERTY                    = "ipco";

    public static ArrayList<String> _containerList = new ArrayList<String>();

    static {
        _containerList.add(BOX_METADATA);
        _containerList.add(BOX_IMAGE_PROPERTY);
        _containerList.add(BOX_ITEM_PROPERTY);
    }
}
