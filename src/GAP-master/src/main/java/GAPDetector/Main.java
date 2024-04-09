package GAPDetector;

import GAPDetector.utils.commands.*;
import lombok.NoArgsConstructor;
import picocli.CommandLine;

@NoArgsConstructor
@CommandLine.Command(
        name = "java -jar GAP4ENRE.jar",
        subcommands = {Detect.class, DetectMulti.class, AnalyzeDecoupling.class, BuildBenchmarkSample.class, FilteredByCoreFileList.class},
        mixinStandardHelpOptions = true,
        helpCommand = true)
public class Main {

    public static void main(String[] args) {
        final Main main = new Main();
        final CommandLine commandLine = new CommandLine(main);
        try {
            final CommandLine.ParseResult parseResult = commandLine.parseArgs(args);
            checkParamHelp(args.length == 0, commandLine, parseResult);
            if (parseResult.hasSubcommand()) {
                for (CommandLine.ParseResult subCommand : parseResult.subcommands()) {
                    final CommandLine c = subCommand.commandSpec().commandLine();
                    checkParamHelp(args.length == 1, c, subCommand);
                    c.execute(args);
                }
            }
        } catch (CommandLine.ParameterException e) {
            commandLine.usage(System.out);
            for (CommandLine c : commandLine.getSubcommands().values()) {
                c.usage(System.out);
            }
            System.exit(1);
        }
    }

    private static void checkParamHelp(boolean empty, CommandLine commandLine, CommandLine.ParseResult parseResult) {
        if (empty || parseResult.isUsageHelpRequested()) {
            commandLine.usage(System.out);
            System.exit(0);
        }
        if (parseResult.isVersionHelpRequested()) {
            commandLine.printVersionHelp(System.out);
            System.exit(0);
        }
    }

}

