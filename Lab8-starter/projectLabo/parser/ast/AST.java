package projectLabo.parser.ast;

import projectLabo.visitors.Visitor;

public interface AST {
	// accept verrà definito nel prossimo laboratorio
	default <T> T accept(Visitor<T> visitor) {
		throw new UnsupportedOperationException();
	}
}
