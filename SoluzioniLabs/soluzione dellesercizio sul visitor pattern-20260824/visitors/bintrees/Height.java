package visitors.bintrees;

public class Height implements Visitor<Integer> {

	@Override
	public Integer visitEmpty() {
		return 0;
	}

	@Override
	public Integer visitNonEmpty(int label, BinTree left, BinTree right) {
		return Math.max(left.accept(this), right.accept(this)) + 1;
	}

}
