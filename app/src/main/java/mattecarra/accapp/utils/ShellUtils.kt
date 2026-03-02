package mattecarra.accapp.utils

object ShellUtils {
    /**
     * Format string to quoted and escaped string suitable for shell commands.
     * It wraps the string in single quotes and replaces each single quote with '\''
     */
    fun escape(s: String?): String {
        if (s == null) return "''"
        return "'" + s.replace("'", "'\\''") + "'"
    }
}
