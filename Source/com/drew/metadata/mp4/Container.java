package com.drew.metadata.mp4;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.mp4.boxes.Box;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Payton Garland
 */
@Getter
public final class Container
{
    public final String type;
    public final long size;

    public Container(String type, long size)
    {
        this.type = type;
        this.size = size;
    }

    /**
     * The list of {@link Box} instances in this container, in the order they were added.
     */
    @NotNull
    private final List<Box> _boxes = new ArrayList<Box>();

    /**
     * Returns an iterable set of the {@link Box} instances contained in this box collection.
     *
     * @return an iterable set of boxes
     */
    @NotNull
    public Iterable<Box> getBoxes()
    {
        return _boxes;
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public <T extends Box> Collection<T> getBoxesOfType(Class<T> type)
    {
        List<T> boxes = new ArrayList<T>();
        for (Box box : _boxes) {
            if (type.isAssignableFrom(box.getClass())) {
                boxes.add((T)box);
            }
        }
        return boxes;
    }

    /**
     * Returns the count of boxes in this box collection.
     *
     * @return the number of unique box types set for this box collection
     */
    public int getDirectoryCount()
    {
        return _boxes.size();
    }

    /**
     * Adds a Box to this box collection.
     *
     * @param box the {@link Box} to add into this box collection.
     */
    public <T extends Box> void addBox(@NotNull T box)
    {
        _boxes.add(box);
    }

    /**
     * Gets the first {@link Box} of the specified type contained within this box collection.
     * If no instances of this type are present, <code>null</code> is returned.
     *
     * @param type the Box type
     * @param <T> the Box type
     * @return the first Box of type T in this box collection, or <code>null</code> if none exist
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends Box> T getFirstBoxOfType(@NotNull Class<T> type)
    {
        for (Box box : _boxes) {
            if (type.isAssignableFrom(box.getClass()))
                return (T)box;
        }
        return null;
    }

    /**
     * Indicates whether an instance of the given Box type exists in this box instance.
     *
     * @param type the {@link Box} type
     * @return <code>true</code> if a {@link Box} of the specified type exists, otherwise <code>false</code>
     */
    public boolean containsBoxOfType(Class<? extends Box> type)
    {
        for (Box box : _boxes) {
            if (type.isAssignableFrom(box.getClass()))
                return true;
        }
        return false;
    }

    @Override
    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder();
        for (Box box : _boxes) {
            stringBuilder.append(box.getType() + ", ");
        }
        stringBuilder.deleteCharAt(stringBuilder.length()-1);
        return stringBuilder.toString();
    }
}
