package parsers;

public enum TokenType {
    // used internally by the tokenizer, should never be accessed by the parser
    SKIP, SYMBOL,
    // non singleton categories
    NUM,
    // end-of-file
    EOF,
    // symbols
    CLOSE_PAR, OPEN_PAR, PLUS, TIMES,
}
