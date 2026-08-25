package projectLabo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import projectLabo.parser.Parser;
import projectLabo.parser.ParserException;
import projectLabo.parser.Tokenizer;
import projectLabo.parser.TokenizerException;
import projectLabo.visitors.DynamicSemanticsException;
import projectLabo.visitors.DynamicSemanticsVisitor;
import projectLabo.visitors.StaticSemanticsException;
import projectLabo.visitors.StaticSemanticsVisitor;
import projectLabo.visitors.environment.EnvironmentException;

import static java.lang.System.err;

public class Main {

	// option flags
	private static final String IN = "-i";
	private static final String OUT = "-o";
	private static final String NTC = "-ntc";

	private record Option(boolean hasArg, String value) {
	};

	private static Map<String, Option> createOptions() {
		var map = new HashMap<String, Option>();
		map.put(IN, new Option(true, null)); // input option, has argument, default value is null
		map.put(OUT, new Option(true, null)); // output option, has argument, default value is null
		map.put(NTC, new Option(false, null)); // no type checking option, no argument
		return map;
	}

	// handles option errors
	private static void optionError(String msg) {
		System.err.println(msg);
		System.exit(1);
	}

	private static void processArgs(String[] args, Map<String, Option> options) {
		for (var i = 0; i < args.length; i++) {
			var optionString = args[i];
			var currentOption = options.get(optionString);
			if (currentOption == null)
				optionError("Option error.\nValid options:\n\t" + IN + " <input>\n\t" + OUT + " <output>\n\t" + NTC);
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
		if (outputPath == null) {
			return new PrintWriter(System.out) {
				@Override
				public void close() {
					flush();
				}
			};
		}
		return new PrintWriter(outputPath);
	}

	public static void main(String[] args) {
		var options = createOptions();
		processArgs(args, options);
		boolean noTypeChecking = options.get(NTC).value() != null;

		try (final var reader = openInput(options.get(IN).value);
				final var writer = openOutput(options.get(OUT).value);
				final var tokenizer = new Tokenizer(reader);
				final var parser = new Parser(tokenizer)) {

			var prog = parser.parseProg();

			// Static semantics (type checking)
			if (!noTypeChecking) {
				try {
					var staticVisitor = new StaticSemanticsVisitor();
					prog.accept(staticVisitor);
				} catch (StaticSemanticsException | EnvironmentException e) {
					writer.println("Static error: " + e.getMessage());
					writer.flush();
					return;
				}
			}

			// Dynamic semantics (execution)
			try {
				var dynamicVisitor = new DynamicSemanticsVisitor();
				prog.accept(dynamicVisitor);
				writer.print(dynamicVisitor.getOutput());
				writer.flush();
			} catch (DynamicSemanticsException | EnvironmentException e) {
				writer.println("Dynamic error: " + e.getMessage());
				writer.flush();
			}

		} catch (IOException exc) {
			err.println("I/O error: " + exc.getMessage());
		} catch (TokenizerException exc) {
			err.println("Lexical error: " + exc.getMessage());
		} catch (ParserException exc) {
			err.println("Syntax error: " + exc.getMessage());
		} catch (Exception exc) {
			err.println("Unexpected error: " + exc.getMessage());
		}
	}

}
