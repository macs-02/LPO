package visitors.files;

public class Test {

	public static void main(String[] args) {
		var folder = new Folder(new File("a", 10), new Folder(new File("b",2), new File("c",21)));
		assert folder.accept(new Size()) == 33; // 10+2+21
		assert folder.accept(new Find("c"));
		assert ! folder.accept(new Find("d"));
	}
}
