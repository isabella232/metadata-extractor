package com.drew.metadata.mp4;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.mp4.boxes.Box;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A top-level object that holds the box values extracted from an MP4 file.
 * <p>
 * Box objects may contain zero or more {@link Box} objects.  Each directory may contain zero or more fields.
 *
 * This is Metadata.java refactored for use with QuickTime-based files
 *
 * @author Drew Noakes https://drewnoakes.com
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

    public Container(Box box)
    {
        this.type = box.getType();
        this.size = box.getSize();
    }

    /**
     * The list of {@link Box} instances in this container, in the order they were added.
     */
    @NotNull
    private final List<Box> _boxes = new ArrayList<Box>();

    /**
     * The list of {@link Container} instances in this container, in the order they were added.
     */
    @NotNull
    private final List<Container> _containers = new ArrayList<Container>();

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

    /**
     * Returns an iterable set of the {@link Container} instances contained in this container.
     *
     * @return an iterable set of containers
     */
    @NotNull
    public Iterable<Container> getContainers()
    {
        return _containers;
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

    @NotNull
    @SuppressWarnings("unchecked")
    public Collection<Container> getContainersOfType(String type)
    {
        List<Container> containers = new ArrayList<Container>();
        for (Container container : _containers) {
            if (container.getType().equals(type)) {
                containers.add(container);
            }
        }
        return containers;
    }

    /**
     * Returns the count of boxes in this box collection.
     *
     * @return the number of unique box types set for this box collection
     */
    public int getBoxCount()
    {
        return _boxes.size();
    }

    /**
     * Returns the count of containers in this container.
     *
     * @return the number of unique container types set for this container
     */
    public int getContainerCount()
    {
        return _containers.size();
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
     * Adds a Container to this container.
     *
     * @param container the {@link Container} to add into this container.
     */
    public void addContainer(@NotNull Container container)
    {
        _containers.add(container);
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
     * Gets the first {@link Container} of the specified type contained within this container.
     * If no instances of this type are present, <code>null</code> is returned.
     *
     * @param type the Container type
     * @return the first Container of Type type in this container, or <code>null</code> if none exist
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public Container getFistContainerOfType(@NotNull String type)
    {
        for (Container container : _containers) {
            if (container.getType().equals(type)) {
                return container;
            }
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

    /**
     * Indicates whether an instance of the given Box type exists in this box instance.
     *
     * @param type the {@link Box} type
     * @return <code>true</code> if a {@link Box} of the specified type exists, otherwise <code>false</code>
     */
    public boolean containsContainerOfType(String type)
    {
        for (Container container : _containers) {
            if (container.getType().equals(type)) {
                return true;
            }
        }
        return false;
    }

    public void printContainer()
    {
        int tabCount = 0;
        for (Container container : this.getContainers()) {
            printContainerHelper(container, tabCount);
        }
    }

    private void printContainerHelper(Container parent, int tabCount)
    {
        for (Box box : parent.getBoxes()) {
            for (int i = 0; i < tabCount; i++) {
                System.out.print("   " + i + "   |");
            }
            System.out.println("  " + box.getType());
        }
        for (Container container : parent.getContainers()) {
            for (int i = 0; i < tabCount; i++) {
                System.out.print("   " + i + "   |");
            }
            System.out.println(" [" + container.getType() + "]");
            tabCount++;

            printContainerHelper(container, tabCount);

            tabCount--;
        }
    }
}
