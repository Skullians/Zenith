package net.skullian.zenith.model.library

/**
 * Represents a maven dependency.
 * Adapted for Zenith.
 *
 * https://github.com/Finally-A-Decent/Trashcan/blob/dev/tooling/src/main/java/info/preva1l/trashcan/Dependency.java
 * Thanks, mate <3
 *
 * <p>
 * Created on 11/04/2025
 *
 * @author Preva1l
 */
public data class ZenithLibrary(
    val group: String,
    val artifact: String,
    val version: String,
    val snapshot: String?,
    val remap: Boolean
) {
    /**
     * Returns a clone of this library data class, with the
     * [remap] boolean set to the supplied argument.
     */
    public fun remapped(remap: Boolean): ZenithLibrary = ZenithLibrary(group, artifact, version, snapshot, remap)

    /**
     * Returns the full version name of the library.
     * Appends the [snapshot] version String if present.
     */
    public fun versionName(): String = version.removeSuffix("-SNAPSHOT").let { if (snapshot != null) "$it-$snapshot" else it }

    /**
     * Returns the GAV coordinate of this library.
     *
     * Context:
     * G - Group
     * A - Artifact
     * V - Version
     *
     * Any dependency that you see (in your Gradle files, or Maven files), will typically look like so:
     * "group.name:artifactId:version" - This is your standard GAV coordinate.
     */
    public fun gavCoordinate(): String = "$group:$artifact:$version"
}
