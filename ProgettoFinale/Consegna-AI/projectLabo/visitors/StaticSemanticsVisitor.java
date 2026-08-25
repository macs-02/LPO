package projectLabo.visitors;

import projectLabo.parser.ast.*;
import projectLabo.visitors.environment.Environment;
import projectLabo.visitors.type.*;

public class StaticSemanticsVisitor implements Visitor<StaticType> {

	private record VectorTypeInfo(StaticType elemType, int size) {
	}

	private static final String PAIR_TYPE_NAME = "PairType";
	private static final String VECTOR_TYPE_NAME = "VectorType";

	private final Environment<StaticType> env = new Environment<>();

	// --- programs, statement sequences, blocks ---

	@Override
	public StaticType visitProg(ExpProg prog) {
		prog.getStmtSeq().accept(this);
		return null;
	}

	@Override
	public StaticType visitStmtSeq(NonEmptyStmtSeq seq) {
		seq.getFirst().accept(this);
		seq.getRest().accept(this);
		return null;
	}

	@Override
	public StaticType visitEmptyStmtSeq(EmptyStmtSeq seq) {
		return null;
	}

	@Override
	public StaticType visitBlock(Block block) {
		env.enterLevel();
		block.getStmtSeq().accept(this);
		env.exitLevel();
		return null;
	}

	// --- statements ---

	@Override
	public StaticType visitVarStmt(VarStmt stmt) {
		var type = stmt.getExp().accept(this);
		env.declare(stmt.getVar().getName(), type);
		return null;
	}

	@Override
	public StaticType visitAssignStmt(AssignStmt stmt) {
		var expected = env.lookup(stmt.getVar().getName());
		checkHasType(expected, stmt.getExp());
		return null;
	}

	@Override
	public StaticType visitPrintStmt(PrintStmt stmt) {
		stmt.getExp().accept(this);
		return null;
	}

	@Override
	public StaticType visitIfStmt(IfStmt stmt) {
		checkHasType(BoolType.INSTANCE, stmt.getExp());
		stmt.getThenBlock().accept(this);
		var elseBlock = stmt.getElseBlock();
		if (elseBlock != null) {
			elseBlock.accept(this);
		}
		return null;
	}

	@Override
	public StaticType visitAssertStmt(AssertStmt stmt) {
		checkHasType(BoolType.INSTANCE, stmt.getExp());
		return null;
	}

	@Override
	public StaticType visitForEachStmt(ForEachStmt stmt) {
		var vecInfo = getVectorTypeSize(stmt.getExp());
		env.enterLevel();
		env.declare(stmt.getVar().getName(), vecInfo.elemType());
		stmt.getBlock().accept(this);
		env.exitLevel();
		return null;
	}

	// --- expressions ---

	@Override
	public StaticType visitAdd(Add exp) {
		var type1 = exp.getLeft().accept(this);
		if (type1 instanceof IntType
				|| (type1 instanceof VectorType vt && vt.elemType() instanceof IntType)) {
			return checkHasType(type1, exp.getRight());
		}
		throw new StaticSemanticsException(
				"Found " + type1.nameOfType() + ", expected INT or INT[]");
	}

	@Override
	public StaticType visitMul(Mul exp) {
		var type1 = exp.getLeft().accept(this);
		if (type1 instanceof IntType) {
			return checkHasType(type1, exp.getRight());
		} else if (type1 instanceof VectorType vt && vt.elemType() instanceof IntType) {
			var type2 = exp.getRight().accept(this);
			if (type2 instanceof VectorType vt2 && vt2.elemType() instanceof IntType) {
				return new VectorType(new VectorType(IntType.INSTANCE, vt.size()), vt2.size());
			}
			throw new StaticSemanticsException("Found " + type2.nameOfType() + ", expected INT[]");
		}
		throw new StaticSemanticsException(
				"Found " + type1.nameOfType() + ", expected INT or INT[]");
	}

	@Override
	public StaticType visitAnd(And exp) {
		checkHasType(BoolType.INSTANCE, exp.getLeft());
		return checkHasType(BoolType.INSTANCE, exp.getRight());
	}

	@Override
	public StaticType visitEq(Eq exp) {
		var type1 = exp.getLeft().accept(this);
		checkHasType(type1, exp.getRight());
		return BoolType.INSTANCE;
	}

	@Override
	public StaticType visitFst(Fst exp) {
		var type1 = exp.getExp().accept(this);
		if (type1 instanceof PairType pt) {
			return pt.first();
		} else if (type1 instanceof VectorType vt) {
			if (vt.elemType() instanceof PairType pt) {
				return new VectorType(pt.first(), vt.size());
			}
			throw new StaticSemanticsException("Found " + vt.elemType().nameOfType()
					+ "[], expected " + PAIR_TYPE_NAME + "[]");
		}
		throw new StaticSemanticsException("Found " + type1.nameOfType()
				+ ", expected " + PAIR_TYPE_NAME + " or " + PAIR_TYPE_NAME + "[]");
	}

	@Override
	public StaticType visitSnd(Snd exp) {
		var type1 = exp.getExp().accept(this);
		if (type1 instanceof PairType pt) {
			return pt.second();
		} else if (type1 instanceof VectorType vt) {
			if (vt.elemType() instanceof PairType pt) {
				return new VectorType(pt.second(), vt.size());
			}
			throw new StaticSemanticsException("Found " + vt.elemType().nameOfType()
					+ "[], expected " + PAIR_TYPE_NAME + "[]");
		}
		throw new StaticSemanticsException("Found " + type1.nameOfType()
				+ ", expected " + PAIR_TYPE_NAME + " or " + PAIR_TYPE_NAME + "[]");
	}

	@Override
	public StaticType visitMinus(Minus exp) {
		return checkHasType(IntType.INSTANCE, exp.getExp());
	}

	@Override
	public StaticType visitNot(Not exp) {
		return checkHasType(BoolType.INSTANCE, exp.getExp());
	}

	@Override
	public StaticType visitIntLiteral(IntLiteral exp) {
		return IntType.INSTANCE;
	}

	@Override
	public StaticType visitBoolLiteral(BoolLiteral exp) {
		return BoolType.INSTANCE;
	}

	@Override
	public StaticType visitPairLit(PairLit exp) {
		var type1 = exp.getLeft().accept(this);
		var type2 = exp.getRight().accept(this);
		return new PairType(type1, type2);
	}

	@Override
	public StaticType visitVariable(Variable exp) {
		return env.lookup(exp.getName());
	}

	@Override
	public StaticType visitVector(Vector exp) {
		return new VectorType(exp.getExp().accept(this), 1);
	}

	@Override
	public StaticType visitCat(Cat exp) {
		var leftInfo = getVectorTypeSize(exp.getLeft());
		var type2 = exp.getRight().accept(this);
		if (type2 instanceof VectorType vt) {
			if (vt.elemType().equals(leftInfo.elemType())) {
				return new VectorType(leftInfo.elemType(), leftInfo.size() + vt.size());
			}
			throw new StaticSemanticsException("Found " + vt.elemType().nameOfType() + "[]"
					+ ", expected " + leftInfo.elemType().nameOfType() + "[]");
		}
		throw new StaticSemanticsException("Found " + type2.nameOfType() + ", expected VectorType");
	}

	@Override
	public StaticType visitFlatten(Flatten exp) {
		var type1 = exp.getExp().accept(this);
		if (type1 instanceof VectorType outer) {
			if (outer.elemType() instanceof VectorType inner) {
				return new VectorType(inner.elemType(), inner.size() * outer.size());
			}
			throw new StaticSemanticsException("Found " + outer.elemType().nameOfType() + "[]"
					+ ", expected " + VECTOR_TYPE_NAME + "[]");
		}
		throw new StaticSemanticsException("Found " + type1.nameOfType() + ", expected VectorType");
	}

	@Override
	public StaticType visitZip(Zip exp) {
		var leftInfo = getVectorTypeSize(exp.getLeft());
		var rightInfo = getVectorTypeSize(exp.getRight());
		if (leftInfo.size() == rightInfo.size()) {
			return new VectorType(
					new PairType(leftInfo.elemType(), rightInfo.elemType()), leftInfo.size());
		}
		throw new StaticSemanticsException("Found " + VECTOR_TYPE_NAME + "[" + rightInfo.size() + "]"
				+ ", expected " + VECTOR_TYPE_NAME + "[" + leftInfo.size() + "]");
	}

	// --- auxiliary checks (mirroring checkHasType / getVectorTypeSize in F#) ---

	private StaticType checkHasType(StaticType expected, Exp exp) {
		var found = exp.accept(this);
		if (found.equals(expected)) return found;
		throw new StaticSemanticsException(
				"Found " + found.nameOfType() + ", expected " + expected.nameOfType());
	}

	private VectorTypeInfo getVectorTypeSize(Exp exp) {
		var type = exp.accept(this);
		if (type instanceof VectorType vt) {
			return new VectorTypeInfo(vt.elemType(), vt.size());
		}
		throw new StaticSemanticsException(
				"Found " + type.nameOfType() + ", expected " + VECTOR_TYPE_NAME);
	}
}
