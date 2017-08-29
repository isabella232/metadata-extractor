package com.drew.imaging.quicktime;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A top-level object that holds the atoms extracted from a file extended from QuickTime.
 * <p>
 * QtContainer objects may contain zero or more {@link QtAtom} objects.  Each directory may contain zero or more fields.
 *
 * This is the Metadata class refactored for use with QtAtoms.
 *
 * @author Drew Noakes https://drewnoakes.com
 * @author Payton Garland
 */
@Getter
public final class QtContainer
{
    private final String type;
    private final long size;
    private final QtContainer parent;

    public QtContainer(String type, long size, QtContainer parent)
    {
        this.type = type;
        this.size = size;
        this.parent = parent;
    }

    public QtContainer(QtAtom qtAtom, QtContainer parent)
    {
        this.type = qtAtom.getType();
        this.size = qtAtom.getSize();
        this.parent = parent;
    }

    /**
     * The list of {@link QtAtom} instances in this container, in the order they were added.
     */
    @NotNull
    private final List<QtAtom> _qtAtoms = new ArrayList<QtAtom>();

    /**
     * The list of {@link QtContainer} instances in this container, in the order they were added.
     */
    @NotNull
    private final List<QtContainer> _qtContainers = new ArrayList<QtContainer>();

    /**
     * Returns an iterable set of the {@link QtAtom} instances contained in this qtAtom collection.
     *
     * @return an iterable set of qtAtoms
     */
    @NotNull
    public <T extends QtAtom> Iterable<T> getQtAtoms()
    {
        return (Iterable<T>)_qtAtoms;
    }

    /**
     * Returns an iterable set of the {@link QtContainer} instances contained in this container.
     *
     * @return an iterable set of containers
     */
    @NotNull
    public Iterable<QtContainer> getQtContainers()
    {
        return _qtContainers;
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public <T extends QtAtom> Collection<T> getQtAtomsOfType(Class<T> type)
    {
        List<T> qtAtoms = new ArrayList<T>();
        for (QtAtom qtAtom : _qtAtoms) {
            if (type.isAssignableFrom(qtAtom.getClass())) {
                qtAtoms.add((T)qtAtom);
            }
        }
        return qtAtoms;
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public Collection<QtContainer> getQtContainersOfType(String type)
    {
        List<QtContainer> qtContainers = new ArrayList<QtContainer>();
        for (QtContainer qtContainer : _qtContainers) {
            if (qtContainer.getType().equals(type)) {
                qtContainers.add(qtContainer);
            }
        }
        return qtContainers;
    }

    /**
     * Returns the count of qtAtoms in this qtAtom collection.
     *
     * @return the number of unique qtAtom types set for this qtAtom collection
     */
    public int getQtAtomCount()
    {
        return _qtAtoms.size();
    }

    /**
     * Returns the count of containers in this container.
     *
     * @return the number of unique container types set for this container
     */
    public int getQtContainerCount()
    {
        return _qtContainers.size();
    }

    /**
     * Adds a QtAtom to this qtAtom collection.
     *
     * @param qtAtom the {@link QtAtom} to add into this qtAtom collection.
     */
    public <T extends QtAtom> void addQtAtom(@NotNull T qtAtom)
    {
        _qtAtoms.add(qtAtom);
    }

    /**
     * Adds a QtContainer to this qtContainer.
     *
     * @param qtContainer the {@link QtContainer} to add into this qtContainer.
     */
    public void addQtContainer(@NotNull QtContainer qtContainer)
    {
        _qtContainers.add(qtContainer);
    }

    /**
     * Gets the first {@link QtAtom} of the specified type contained within this qtAtom collection.
     * If no instances of this type are present, <code>null</code> is returned.
     *
     * @param type the QtAtom type
     * @param <T> the QtAtom type
     * @return the first QtAtom of type T in this qtAtom collection, or <code>null</code> if none exist
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends QtAtom> T getFirstQtAtomOfType(@NotNull Class<T> type)
    {
        return getFirstQtAtomOfTypeHelper(type, this);
    }

    private <T extends QtAtom> T getFirstQtAtomOfTypeHelper(@NotNull Class<T> type, @NotNull QtContainer parent)
    {
        for (QtAtom qtAtom : parent.getQtAtoms()) {
            if (type.isAssignableFrom(qtAtom.getClass()))
                return (T)qtAtom;
        }

        for (QtContainer qtContainer : parent.getQtContainers()) {
            return getFirstQtAtomOfTypeHelper(type, qtContainer);
        }

        return null;
    }

    /**
     * Gets the first {@link QtContainer} of the specified type contained within this container.
     * If no instances of this type are present, <code>null</code> is returned.
     *
     * @param type the QtContainer type
     * @return the first QtContainer of Type type in this container, or <code>null</code> if none exist
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public QtContainer getFirstQtContainerOfType(@NotNull String type)
    {
        for (QtContainer qtContainer : _qtContainers) {
            if (qtContainer.getType().equals(type)) {
                return qtContainer;
            }
        }
        return null;
    }

    /**
     * Indicates whether an instance of the given QtAtom type exists in this qtAtom instance.
     *
     * @param type the {@link QtAtom} type
     * @return <code>true</code> if a {@link QtAtom} of the specified type exists, otherwise <code>false</code>
     */
    public boolean containsQtAtomOfType(Class<? extends QtAtom> type)
    {
        return containsQtAtomOfTypeHelper(type, this);
    }

    private boolean containsQtAtomOfTypeHelper(Class<? extends QtAtom> type, QtContainer parent)
    {
        for (QtAtom qtAtom : parent.getQtAtoms()) {
            if (type.isAssignableFrom(qtAtom.getClass()))
                return true;
        }

        for (QtContainer qtContainer : parent.getQtContainers()) {
            return containsQtAtomOfTypeHelper(type, qtContainer);
        }

        return false;
    }

    /**
     * Indicates whether an instance of the given QtAtom type exists in this qtAtom instance.
     *
     * @param type the {@link QtAtom} type
     * @return <code>true</code> if a {@link QtAtom} of the specified type exists, otherwise <code>false</code>
     */
    public boolean containsQtContainerOfType(String type)
    {
        for (QtContainer qtContainer : _qtContainers) {
            if (qtContainer.getType().equals(type)) {
                return true;
            }
        }
        return false;
    }

    public void printContainer()
    {
        int tabCount = 0;
        for (QtAtom qtAtom : this.getQtAtoms()) {
            for (int i = 0; i < tabCount; i++) {
                System.out.print("   " + i + "   |");
            }
            System.out.println("  " + qtAtom.getType());
        }
        for (QtContainer qtContainer : this.getQtContainers()) {
            for (int i = 0; i < tabCount; i++) {
                System.out.print("   " + i + "   |");
            }
            System.out.println(" [" + qtContainer.getType() + "]");
            tabCount++;
            printContainerHelper(qtContainer, tabCount);
            tabCount--;
        }
    }

    private void printContainerHelper(QtContainer parent, int tabCount)
    {
        for (QtAtom qtAtom : parent.getQtAtoms()) {
            for (int i = 0; i < tabCount; i++) {
                System.out.print("   " + i + "   |");
            }
            System.out.println("  " + qtAtom.getType());
        }
        for (QtContainer qtContainer : parent.getQtContainers()) {
            for (int i = 0; i < tabCount; i++) {
                System.out.print("   " + i + "   |");
            }
            System.out.println(" [" + qtContainer.getType() + "]");
            tabCount++;

            printContainerHelper(qtContainer, tabCount);

            tabCount--;
        }
    }

    enum TrackType
    {
        Video,
        Sound,
        Hint,
        Meta,
        Text,
        Unknown
    }
}
