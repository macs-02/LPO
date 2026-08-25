package parsers.ast;

/**
 * AST for binary multiplication
 */
public class Mul extends BinaryOp {
	public Mul(Exp left, Exp right) {
		super(left, right);
	}

}
