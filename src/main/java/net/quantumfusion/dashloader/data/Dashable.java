package net.quantumfusion.dashloader.data;

import net.quantumfusion.dashloader.DashRegistry;

/**
 * Represents a type that can be (de-)serialized with ActiveJ, and is convertible to its original type,
 * sometimes referred to as the "Undash" type.
 *
 * @param <U> the "Undash" type
 */
public interface Dashable<U> {
    U toUndash(DashRegistry registry);
}
