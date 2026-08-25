package parsers;

import parsers.ast.Prog;

public interface ParserInterface extends AutoCloseable {

	Prog parseProg() throws ParserException;

}