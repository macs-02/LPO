package visitors.bintrees;

public interface BinTree {
	<T> T accept(Visitor<T> v);
}
