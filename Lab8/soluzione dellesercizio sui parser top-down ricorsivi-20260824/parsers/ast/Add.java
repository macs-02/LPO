package parsers.ast;

/**
 * AST for binary addition
 */
public class Add extends BinaryOp {
	public Add(Exp left, Exp right) {
		super(left, right);
	}
}
