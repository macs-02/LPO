package parsers;

import java.io.IOException;
import parsers.ast.Add;
import parsers.ast.Exp;
import parsers.ast.IntLiteral;
import parsers.ast.ExprProg;
import parsers.ast.Mul;
import parsers.ast.Prog;

import static java.util.Objects.requireNonNull;
import static parsers.TokenType.*;


/**
 * parser for the grammar
 * Prog ::= Exp EOF
 * Exp ::= Mul (PLUS Mul)*
 * Mul ::= Atom (TIMES Atom)*
 * Atom ::= NUM | OPEN_PAR Exp CLOSE_PAR
 */

public class Parser implements ParserInterface {

    private final Tokenizer tokenizer; // the tokenizer used by the parser

    // decorates error message with the corresponding line number
    private String lineErrMsg(String msg) {
        return String.format("on line %s: %s", tokenizer.getLineNumber(), msg);
    }

    /**
     * checks whether the token type of the currently recognized token matches
     * 'expected'; if not, it throws a corresponding ParserException
     */
    private void match(TokenType expected) throws ParserException {
        final var found = tokenizer.tokenType();
        if (found != expected)
            throw new ParserException(lineErrMsg(String.format("Expecting %s, found %s('%s')", expected, found, tokenizer.tokenString())));
    }

    /**
     * checks whether the token type of the currently recognized token matches
     * 'expected'; if so, it reads the next token, otherwise it throws a
     * corresponding ParserException
     */
    private void consume(TokenType expected) throws ParserException {
        match(expected);
        tokenizer.next();
    }

    // throws a ParserException because the current token was not expected
    private <T> T unexpectedTokenError() throws ParserException {
        throw new ParserException(lineErrMsg(String.format("Unexpected token %s ('%s')", tokenizer.tokenType(), tokenizer.tokenString())));
    }

    // associates the parser with a corresponding non-null tokenizer
    public Parser(Tokenizer tokenizer) {
        this.tokenizer = requireNonNull(tokenizer);
    }

    @Override
    public void close() throws IOException {
        tokenizer.close();
    }

    /**
     * parses programs
     * Prog ::= Exp EOF
     */
    @Override
    public Prog parseProg() throws ParserException {
        tokenizer.next(); // one look-ahead symbol
        final var res = new ExprProg(parseExp());
        match(EOF); // last token must have type EOF
        return res;
    }

    /**
     * parses expressions generated from Exp
     * Exp ::= Mul (PLUS Exp)?
     */
    private Exp parseExp() throws ParserException {
        var exp = parseMul();
        while (tokenizer.tokenType() == PLUS) {
            tokenizer.next();
            exp = new Add(exp, parseMul());
        }
        return exp;
    }

    /**
     * parses expressions generated from Mul
     * Mul ::= Atom (TIMES Mul)?
     */
    private Exp parseMul() throws ParserException {
        var res = parseAtom();
        while (tokenizer.tokenType() == TIMES) {
            tokenizer.next();
            res = new Mul(res, parseAtom());
        }
        return res;
    }

    /**
     * parses expressions generated from Atom
     * Atom ::= NUM | OPEN_PAR Exp CLOSE_PAR
     */
    private Exp parseAtom() throws ParserException {
        return switch (tokenizer.tokenType()) {
            case NUM -> parseNum();
            case OPEN_PAR -> parseRoundPar();
            default -> unexpectedTokenError();
        };
    }

    /**
     * parses literals of type NUM
     */
    private IntLiteral parseNum() throws ParserException {
        match(NUM);
        final var value = tokenizer.intValue();
        tokenizer.next();
        return new IntLiteral(value);
    }

    /**
     * parses expressions defined by OPEN_PAR Exp CLOSE_PAR
     */
    private Exp parseRoundPar() throws ParserException {
        consume(OPEN_PAR);
        var exp = parseExp();
        consume(CLOSE_PAR);
        return exp;
    }

}