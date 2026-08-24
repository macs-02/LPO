package visitors.bintrees;

import static java.util.Objects.requireNonNull;

public class NonEmptyTree implements BinTree {

	private final int label;
	private final BinTree left, right;

	public NonEmptyTree(int label, BinTree left, BinTree right) {
		this.label = label;
		this.left = requireNonNull(left);
		this.right = requireNonNull(right);
	}

	@Override
	public <T> T accept(Visitor<T> v) {
		return v.visitNonEmpty(label, left, right);
	}

}
