package net.skullian.zenith.core.logging;

/**
 * Defines logging levels that indicate the severity or importance of a logged message.
 * <p>
 * The levels are designed to categorise log messages in a structured manner.
 * It allows log statements to be filtered based on the desired verbosity or criticality.
 * <ul>
 *   <li>DEBUG - Used for detailed information, typically of interest only during development or troubleshooting.</li>
 *   <li>INFO - Represents general operational messages indicating the progress or state of the application.</li>
 *   <li>WARN - Indicates potentially harmful situations that should be noted but do not stop the application.</li>
 *   <li>ERROR - Signals error events that might still allow the application to continue running but require attention.</li>
 * </ul>
 */
public enum LogLevel {
    INFO,
    WARN,
    ERROR,
    DEBUG
}
