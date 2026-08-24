package parsers.ast;

/**
 * generic abstract class for the AST of literals of primitive type
 * @param <T> the type of the literals, expected to be a wrapper
 *           class of primitive types
 */
public abstract class AtomicLiteral<T> implements Exp {

	protected final T value;

	public AtomicLiteral(T n) {
		this.value = n;
	}

	@Override
	public String toString() {
		return String.format("%s(%s)", getClass().getSimpleName(), value);
	}
}
