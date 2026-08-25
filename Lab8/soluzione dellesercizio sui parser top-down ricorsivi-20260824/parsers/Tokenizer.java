package parsers;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static parsers.TokenType.*;

public class Tokenizer implements TokenizerInterface {

    private record Token(TokenType type, String string, Integer intValue) {
    }

    private static final String regEx; // the regular expression including all valid lexemes

    /* symbol table */
    private static final Map<String, TokenType> symbols = new TreeMap<>();

    private final LineNumberReader reader; // the numbered buffered reader used by the buffered tokenizer
    private String line; // currently processed line
    private final Matcher matcher = Pattern.compile(regEx).matcher(""); // the matcher used by the tokenizer
    private Token token; // the current token of the match

    static { // initialization of the symbol table: symbols are singleton lexical categories

        symbols.put(")", CLOSE_PAR);
        symbols.put("(", OPEN_PAR);
        symbols.put("+", PLUS);
        symbols.put("*", TIMES);

    }

    static {
        /* definition of the regular expressions of all valid lexemes */

        /*
         * builds the regular expression for symbols 
         */

        final var symbolList = new LinkedList<String>(); // symbols used in the regular expression
        for (var s : symbols.keySet())
            symbolList.addFirst(String.format("\\%s", String.join("\\", s.split("")))); // every char is pre-pended with
        // "\\" to avoid regular expression syntax problems
        final var symbolRegEx = String.format("(?<%s>%s)", SYMBOL.name(), String.join("|", symbolList)); // symbols
        /*
         * builds the regular expressions for the other groups 
         */
        final var skipRegEx = String.format("(?<%s>\\s+|//.*)", SKIP.name()); // white spaces or single line comments to be skipped
        final var numRegEx = String.format("(?<%s>0|[1-9][0-9]*)", NUM.name()); // radix 10 natural numbers
        /*
         * builds the complete regular expression as union of the different groups
         */
        regEx = String.join("|", symbolRegEx, skipRegEx, numRegEx);
    }

    public Tokenizer(Reader br) {
        this.reader = new LineNumberReader(br);
    }

    private boolean hasNext() throws TokenizerException { // checks whether there are still lexemes
        if (matcher.regionEnd() > matcher.regionStart()) // the matcher has still to complete the current line
            return true;
        while (true) { // reads the next non-empty line, if any
            try {
                line = reader.readLine();
            } catch (IOException e) {
                throw new TokenizerException(e);
            }
            if (line == null) return false; // EOF reached
            if (line.isEmpty()) // lines can be empty!
                continue;
            matcher.reset(line); // reset the matcher with the new non-empty line
            return true;
        }
    }

    // returns the token type corresponding to the group name that matched
    // pre-condition: matcher.lookingAt() returned true
    private String retrievedGroupName(MatchResult result) {
        for (var groupName : result.namedGroups().keySet())
            if (result.group(groupName) != null) return groupName;
        throw new AssertionError("Fatal error: could not determine the token type!");
    }

    private Token retrievedToken(MatchResult result) { // pre-condition: matcher.lookingAt() returned true
        var tokenString = result.group();
        var rawType = TokenType.valueOf(retrievedGroupName(result));
        var tokenType = switch (rawType) {
            case SYMBOL -> symbols.get(result.group());
            default -> rawType;
        };
        var intValue = tokenType == NUM ? Integer.decode(tokenString) : null;
        return new Token(tokenType, tokenString, intValue);
    }

    // returns the token type corresponding to the recognized lexeme
    // pre-condition: matcher.lookingAt() returned true
    public TokenType tokenType() { // pre-condition: matcher.lookingAt() returned true
        checkLegalState();
        return token.type;
    }

    private void resetState() {
        token = null;
    }

    private void unrecognizedToken() throws TokenizerException {
        throw new TokenizerException(String.format("on line %s unrecognized token starting at '%s'", reader.getLineNumber(), line.substring(matcher.regionStart())));
    }

    public TokenType next() throws TokenizerException {
        resetState();
        do {
            if (!hasNext()) { // builds the EOF token
                token = new Token(EOF, null, null);
                return EOF;
            }
            if (!matcher.lookingAt()) unrecognizedToken();
            token = retrievedToken(matcher);
            matcher.region(matcher.end(), matcher.regionEnd()); // advances in the matcher
        } while (token.type == SKIP); // keeps advancing when skippable tokens are recognized
        return token.type;
    }

    private void checkLegalState() {
        if (token == null) throw new IllegalStateException("No token was recognized");
    }

    private void checkLegalState(TokenType tokenType) {
        checkLegalState();
        if (token.type != tokenType)
            throw new IllegalStateException(String.format("No token of type %s was recognized", tokenType));
    }

    @Override
    public int intValue() { // integer value of the most recently recognized token, if of type NUM
        checkLegalState(NUM);
        return token.intValue;
    }

    @Override
    public String tokenString() { // lexeme of the most recently recognized token, if any
        checkLegalState();
        return token.string;
    }

    @Override
    public int getLineNumber() {
        return reader.getLineNumber();
    }

    @Override
    public void close() throws IOException { // tokenizers are auto-closeable
        reader.close();
    }

}
