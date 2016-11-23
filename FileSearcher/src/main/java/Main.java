import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

// Apache Commons CLI
import org.apache.commons.cli.*;

public class Main {

    public static void main(String[] args) {
        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        options.addOption("h", "help", false, "print this help message");
        options.addOption("d", "date", false, "output change date");
        options.addOption("v", "volume", false, "output file volume in bytes");

        String pattern = null;
        CommandLine line = null;
        HelpFormatter helpFormatter = new HelpFormatter();
        try {
            line = parser.parse(options, args);
            pattern = line.getArgs()[0];
        }
        catch( ParseException parse ) {
            System.err.println("Parsing failed. Reason: " + parse.getMessage());
        }
        catch (IndexOutOfBoundsException ind) {
            helpFormatter.printHelp("FileSearcher.jar <regexp>", options);
        }

        //System.out.println(args[0]);
        FileSystem myFS = java.nio.file.FileSystems.getDefault();
        PathMatcher matcher = myFS.getPathMatcher("regex:" + pattern);
        FileVisitor<Path> checker = new Main.MyChecker(matcher, line.hasOption("date"), line.hasOption("volume"));
        Path workDir = myFS.getPath(System.getProperty("user.dir"));
        try {
            Files.walkFileTree(workDir, checker);
        }
        catch (IOException io) {
            System.err.println(io.getMessage());
        }
    }

    static private class MyChecker extends SimpleFileVisitor<Path> {
        private PathMatcher matcher;
        private boolean date = false;
        private boolean volume = false;

        MyChecker (PathMatcher extMatcher, boolean date, boolean volume) {
            this.matcher = extMatcher;
            this.date = date;
            this.volume = volume;
        }

        private String setAttribs (BasicFileAttributes attrs) {
            StringBuilder output = new StringBuilder();
            if (date) {
                output.append(attrs.lastModifiedTime());
                output.append(' ');
            }
            if (volume) {
                output.append(attrs.size());
                output.append(" bytes ");
            }
            return output.toString();
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
            if (matcher.matches(dir.getFileName())) {
                System.out.printf("d " + setAttribs(attrs) + "%s %s\n", dir.getFileName(), dir.getParent());// System.out.println(dir);
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile (Path file, BasicFileAttributes attrs) {
            if (matcher.matches(file.getFileName())) {
                setAttribs(attrs);
                System.out.printf("- " + setAttribs(attrs) + "%s %s\n", file.getFileName(), file.getParent());
            }
            return FileVisitResult.CONTINUE;
        }
    }
}
