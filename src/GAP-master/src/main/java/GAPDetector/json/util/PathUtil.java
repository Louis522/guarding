package GAPDetector.json.util;

public class PathUtil {

    /**
     * To resolve the classpath or import statement.
     * To parse the file and module
     *
     * @param str "a/b/c"
     * @return subStr "a/b"
     */
    public static String deleteLastStrByPathDelimiter(String str) {
        StringBuilder subStr;
        String[] components = str.split("/");
        subStr = new StringBuilder(components[0]);
        for (int index = 1; index < components.length - 1; index++) {
            subStr.append("/");
            subStr.append(components[index]);
        }
        return subStr.toString();
    }

    /**
     * To get the last string in the classpath or import path
     *
     * @param str "a/b/c"
     * @return "c"
     */
    public static String getLastStrByPathDelimiter(String str) {
        String[] components = str.split("/");
        return components[components.length - 1];
    }

    /**
     * Unify filepath into a same mode "a/b/c"
     *
     * @param path "a\\b\\c" or "a/b/c"
     * @return "a/b/c"
     */
    public static String unifyPath(String path) {
        if (path.contains("\\")) {
            String[] components = path.split("\\\\");
            return String.join("/", components);
        }
        if (path.contains("//")) {
            return path.replace("//", "/");
        }
        return path;
    }

    /**
     * To resolve the classpath or import statement.
     * To parse the file and module
     *
     * @param str "a.b.c"
     * @return subStr "a.b"
     */
    public static String deleteLastStrByDot(String str) {
        StringBuilder subStr;
        String[] components = str.split("\\.");
        subStr = new StringBuilder(components[0]);
        for (int index = 1; index < components.length - 1; index++) {
            subStr.append(".");
            subStr.append(components[index]);
        }
        return subStr.toString();
    }

    /**
     * To get the last string in the classpath or import path
     *
     * @param str "a.b.c"
     * @return "c"
     */
    public static String getLastStrByDot(String str) {
        String[] components = str.split("\\.");
        return components[components.length - 1];
    }
    /**
     * To get the last 2 string in the classpath or import path
     *
     * @param str "a.b.c"
     * @return "b.c"
     */
    public static String getLast2StrByDot(String str) {
        String[] components = str.split("\\.");
        return components[components.length - 2] + "." + components[components.length - 1];
    }

}
