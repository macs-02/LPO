package projectLabo.parser.ast;

import static java.util.Objects.requireNonNull;

public class ForEachStmt implements Stmt {
	private final Variable ident;
	private final Exp exp;
	private final Block block;

	public ForEachStmt(Variable ident, Exp exp, Block block) {
		this.ident = requireNonNull(ident);
		this.exp = requireNonNull(exp);
		this.block = requireNonNull(block);
	}

	public Variable getIdent() {
		return ident;
	}

	public Exp getExp() {
		return exp;
	}

	public Block getBlock() {
		return block;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + ident + "," + exp + "," + block + ")";
	}

	@Override
	public <T> T accept(projectLabo.visitors.Visitor<T> visitor) {
		return visitor.visit(this);
	}
}
