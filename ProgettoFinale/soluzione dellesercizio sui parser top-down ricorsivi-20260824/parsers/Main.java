package parsers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class Main {

	// option flags
	private static final String IN = "-i";
	private static final String OUT = "-o";

	private record Option(boolean hasArg, String value) {
	};

	private static final Map<String, Option> options = new HashMap<>();

	static {
		options.put(IN, new Option(true, null)); // input option, has argument, default value is null
		options.put(OUT, new Option(true, null)); // output option, has argument, default value is null
	}

	// handles option errors
	private static void optionError(String msg) {
		System.err.println(msg);
		System.exit(1);
	}

	private static void processArgs(String[] args) {
		for (var i = 0; i < args.length; i++) {
			var optionString = args[i];
			var currentOption = options.get(optionString);
			if (currentOption == null)
				optionError("Option error.\nValid options:\n\t" + IN + " <input>\n\t" + OUT + " <output>");
			if (currentOption.hasArg()) // sets option with argument
			{
				if (i + 1 == args.length)
					optionError("Missing argument for option " + optionString);
				options.put(optionString, new Option(true, args[++i]));
			} else
				options.put(optionString, new Option(false, "")); // sets the option with no argument, any non null
																	// string works
		}
	}

	// opens the input file `inputPath`, or the standard input
	private static BufferedReader openInput(String inputPath) throws FileNotFoundException {
		return new BufferedReader(inputPath == null ? new InputStreamReader(System.in) : new FileReader(inputPath));
	}

	// opens the output file `outputPath`, or the standard output
	private static PrintWriter openOutput(String outputPath) throws FileNotFoundException {
		return outputPath == null ? new PrintWriter(System.out) : new PrintWriter(outputPath);
	}

	public static void main(String[] args) {
		processArgs(args);
		try (final var reader = openInput(options.get(IN).value);
				final var writer = openOutput(options.get(OUT).value);
				final var tokenizer = new Tokenizer(reader);
				final var parser = new Parser(tokenizer)) {
			var prog = parser.parseProg();
			writer.println(prog);
			System.out.println("Program parsed with no errors");
		} catch (IOException e) {
			System.err.println("I/O error: " + e.getMessage());
		} catch (ParserException e) {
			System.err.println("Parser error: " + e.getMessage());
		} catch (Throwable e) {
			System.err.println("Unexpected error: ");
			e.printStackTrace();
		}
	}

}
