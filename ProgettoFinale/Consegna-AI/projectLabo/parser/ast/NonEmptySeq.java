package projectLabo.parser.ast;

import static java.util.Objects.requireNonNull;

public abstract class NonEmptySeq<FT,RT> {
	protected final FT first;
	protected final RT rest;

	protected NonEmptySeq(FT first, RT rest) {
		this.first = requireNonNull(first);
		this.rest = requireNonNull(rest);
	}

	public FT getFirst() {
		return first;
	}

	public RT getRest() {
		return rest;
	}

	@Override
	public String toString() {
		return String.format("%s(%s,%s)", getClass().getSimpleName(), first, rest);
	}
}
