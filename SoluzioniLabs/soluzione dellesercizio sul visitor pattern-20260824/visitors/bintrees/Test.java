package visitors.bintrees;

public class Test {
	public static void main(String[] args) {
		var emptyT = new EmptyTree();
		var t1 = new NonEmptyTree(1, emptyT, emptyT);
		var t2 = new NonEmptyTree(2, t1, emptyT);
		var t3 = new NonEmptyTree(3, emptyT, emptyT);
		var t4 = new NonEmptyTree(4, t2, t3);
		var t5 = new NonEmptyTree(5, t4, t3);
		var height = new Height();
		assert emptyT.accept(height) == 0 && t1.accept(height) == 1 && t2.accept(height) == 2
				&& t3.accept(height) == 1 && t4.accept(height) == 3 && t5.accept(height) == 4;
	}
}
