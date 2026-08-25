package parsers;

import java.io.IOException;

public interface TokenizerInterface extends AutoCloseable {

    TokenType next() throws TokenizerException;

    TokenType tokenType();

    String tokenString();

    int intValue();

    void close() throws IOException;

    int getLineNumber();

}