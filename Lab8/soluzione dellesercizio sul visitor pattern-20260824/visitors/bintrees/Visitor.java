package visitors.bintrees;

public interface Visitor<T> {
	T visitEmpty();

	T visitNonEmpty(int label, BinTree left, BinTree right);
}
