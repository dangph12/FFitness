package com.example.ffitness.util;

import java.util.regex.Pattern;

/**
 * Utility helpers for instruction text normalization.
 */
public final class InstructionUtils {

    private InstructionUtils() { /* utility */ }

    /**
     * Normalize instruction text for display as plain text.
     * - Converts literal "\\n" sequences into real newlines
     * - Normalizes CRLF/CR to LF
     * - Trims surrounding whitespace
     * - Converts "Steps:" or "Step:" at the start of the text to a plain "Steps" label
     * - Normalizes numbered markers ("1.)", "1)", "1.", "Step 1)") to "1. " at line starts
     * - Collapses excessive blank lines
     */
    public static String normalizeInstructions(String raw) {
        if (raw == null) return "";

        // convert literal backslash-n sequences to real newlines
        String s = raw.replace("\\n", "\n");

        // normalize CRLF and CR to LF
        s = s.replace("\r\n", "\n").replace("\r", "\n");

        s = s.trim();
        if (s.isEmpty()) return "";

        // If it starts with 'Steps' or 'Step', replace the leading label with a plain 'Steps' line
        s = Pattern.compile("(?i)^\\s*steps?\\s*:\\s*", Pattern.MULTILINE)
                .matcher(s)
                .replaceAll("Steps\n\n");

        // Convert numbered markers like '1.)', '1)', '1.' at start of line to '1. '
        s = Pattern.compile("(?m)^[ \\t]*([0-9]+)[\\\\.)]+[ \\t]*")
                .matcher(s)
                .replaceAll("$1. ");

        // Also convert occurrences like 'Step 1)' -> '1. '
        s = Pattern.compile("(?i)(?m)^[ \\t]*step[ \\t]*([0-9]+)[\\\\.):]*[ \\t]*")
                .matcher(s)
                .replaceAll("$1. ");

        // Collapse 3+ blank lines into two
        s = s.replaceAll("\\n{3,}", "\\n\\n");

        return s;
    }
}
