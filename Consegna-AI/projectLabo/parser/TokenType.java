package projectLabo.parser;

public enum TokenType {
	// used internally by the tokenizer, should never be accessed by the parser
	KEYWORD, SKIP, SYMBOL,
	// non singleton categories
	IDENT, NUM,
	// end-of-file
	EOF,
	// symbols
	AND, ASSIGN, CAT, CLOSE_BLOCK, CLOSE_PAR, CLOSE_VECT, EQ, FLATTEN, MINUS, NOT, OPEN_BLOCK, OPEN_PAR, OPEN_VECT, PAIR_OP, PLUS, STMT_SEP, TIMES, ZIP,
	// keywords
	ASSERT, BOOL, ELSE, FOR, FST, IF, IN, PRINT, SND, VAR,
}
