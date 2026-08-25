package projectLabo.parser.ast;

import static java.util.Objects.requireNonNull;

import projectLabo.visitors.Visitor;

public class IfStmt implements Stmt {
	private final Exp exp; // non-optional field
	private final Block thenBlock; // non-optional field
	private final Block elseBlock; // optional field

	public IfStmt(Exp exp, Block thenBlock, Block elseBlock) {
		this.exp = requireNonNull(exp);
		this.thenBlock = requireNonNull(thenBlock);
		this.elseBlock = elseBlock;
	}

	public IfStmt(Exp exp, Block thenBlock) {
		this(exp, thenBlock, null);
	}

	public Exp getExp() {
		return exp;
	}

	public Block getThenBlock() {
		return thenBlock;
	}

	public Block getElseBlock() {
		return elseBlock;
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitIfStmt(this);
	}

	@Override
	public String toString() {
		return String.format("%s(%s,%s%s)", getClass().getSimpleName(), exp, thenBlock,
				elseBlock != null ? "," + elseBlock : "");
	}

}
