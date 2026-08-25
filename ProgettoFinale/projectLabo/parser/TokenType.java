package projectLabo.parser;

public enum TokenType {
	// used internally by the tokenizer, should never be accessed by the parser
	KEYWORD, SKIP, SYMBOL,
	// non singleton categories
	IDENT, NUM,
	// end-of-file
	EOF,
	// symbols
	ASSIGN, CLOSE_BLOCK, CLOSE_PAR, CLOSE_VECT, EQ, MINUS, OPEN_BLOCK, OPEN_PAR, OPEN_VECT, PAIR_OP, PLUS, STMT_SEP, TIMES, CAT, ZIP, FLATTEN, AND, NOT,
	// keywords
	ASSERT, BOOL, ELSE, FOR, FST, IF, IN, PRINT, SND, VAR,
}
