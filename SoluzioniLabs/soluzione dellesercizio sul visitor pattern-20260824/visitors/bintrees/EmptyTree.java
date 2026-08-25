package visitors.bintrees;

public class EmptyTree implements BinTree {

	@Override
	public <T> T accept(Visitor<T> v) {
		return v.visitEmpty();
	}

}
