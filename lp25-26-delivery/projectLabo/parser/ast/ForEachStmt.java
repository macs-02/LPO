package projectLabo.parser.ast;

import static java.util.Objects.requireNonNull;

import projectLabo.visitors.Visitor;

public class ForEachStmt implements Stmt {
	private final Variable var;
	private final Exp exp;
	private final Block block;

	public ForEachStmt(Variable var, Exp exp, Block block) {
		this.var = requireNonNull(var);
		this.exp = requireNonNull(exp);
		this.block = requireNonNull(block);
	}

	public Variable getVar() { return var; }
	public Exp getExp() { return exp; }
	public Block getBlock() { return block; }

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitForEachStmt(this);
	}

	@Override
	public String toString() {
		return String.format("%s(%s,%s,%s)", getClass().getSimpleName(), var, exp, block);
	}
}
