package projectLabo.parser.ast;

public class VarStmt extends AbstractAssignStmt {

	public VarStmt(Variable var, Exp exp) {
		super(var, exp);
	}

	@Override
	public <T> T accept(projectLabo.visitors.Visitor<T> visitor) {
		return visitor.visit(this);
	}

	public Variable getIdent() { return var; }
	public Exp getExp() { return exp; }
}
