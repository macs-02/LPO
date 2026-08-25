package projectLabo.visitors.typechecking;

import projectLabo.parser.ast.*;
import projectLabo.visitors.Visitor;
import projectLabo.visitors.environments.Environment;

public class TypeChecker implements Visitor<Type> {

	private final Environment<Type> env;

	public TypeChecker(Environment<Type> env) {
		this.env = env;
	}

	private void checkType(Type expected, Type found) {
		expected.checkEqual(found);
	}

	@Override
	public Type visit(Add exp) {
		Type leftType = exp.getLeft().accept(this);
		Type rightType = exp.getRight().accept(this);

		if (leftType instanceof VectorType && rightType instanceof VectorType) {
			VectorType vl = (VectorType) leftType;
			VectorType vr = (VectorType) rightType;
			checkType(IntType.INSTANCE, vl.getElemType());
			checkType(IntType.INSTANCE, vr.getElemType());
			if (vl.getSize() != vr.getSize()) {
				throw new RuntimeException("Add: vectors must have the same size");
			}
			return new VectorType(IntType.INSTANCE, vl.getSize());
		}

		checkType(IntType.INSTANCE, leftType);
		checkType(IntType.INSTANCE, rightType);
		return IntType.INSTANCE;
	}

	@Override
	public Type visit(And exp) {
		Type left = exp.getLeft().accept(this);
		checkType(BoolType.INSTANCE, left);
		Type right = exp.getRight().accept(this);
		checkType(BoolType.INSTANCE, right);
		return BoolType.INSTANCE;
	}

	@Override
	public Type visit(BoolLiteral exp) {
		return BoolType.INSTANCE;
	}

	@Override
	public Type visit(Cat exp) {
		Type left = exp.getLeft().accept(this);
		Type right = exp.getRight().accept(this);
		
		if (left instanceof VectorType && right instanceof VectorType) {
			VectorType vl = (VectorType) left;
			VectorType vr = (VectorType) right;
			checkType(vl.getElemType(), vr.getElemType());
			return new VectorType(vl.getElemType(), vl.getSize() + vr.getSize());
		}
		throw new RuntimeException("Cat: expected vectors");
	}

	@Override
	public Type visit(Eq exp) {
		Type left = exp.getLeft().accept(this);
		Type right = exp.getRight().accept(this);
		checkType(left, right);
		return BoolType.INSTANCE;
	}

	@Override
	public Type visit(Flatten exp) {
		Type type = exp.getExp().accept(this);
		if (type instanceof VectorType) {
			VectorType outer = (VectorType) type;
			if (outer.getElemType() instanceof VectorType) {
				VectorType inner = (VectorType) outer.getElemType();
				return new VectorType(inner.getElemType(), outer.getSize() * inner.getSize());
			}
		}
		throw new RuntimeException("Flatten: expected vector of vectors");
	}

	@Override
	public Type visit(Fst exp) {
		Type type = exp.getExp().accept(this);
		if (type instanceof PairType) {
			return ((PairType) type).getFstType();
		}
		if (type instanceof VectorType) {
			VectorType vt = (VectorType) type;
			if (vt.getElemType() instanceof PairType) {
				PairType pt = (PairType) vt.getElemType();
				return new VectorType(pt.getFstType(), vt.getSize());
			}
		}
		throw new RuntimeException("Fst: expected pair or vector of pairs");
	}

	@Override
	public Type visit(IntLiteral exp) {
		return IntType.INSTANCE;
	}

	@Override
	public Type visit(Minus exp) {
		Type type = exp.getExp().accept(this);
		checkType(IntType.INSTANCE, type);
		return IntType.INSTANCE;
	}

	@Override
	public Type visit(Mul exp) {
		Type leftType = exp.getLeft().accept(this);
		Type rightType = exp.getRight().accept(this);

		if (leftType instanceof VectorType && rightType instanceof VectorType) {
			VectorType vl = (VectorType) leftType;
			VectorType vr = (VectorType) rightType;
			checkType(IntType.INSTANCE, vl.getElemType());
			checkType(IntType.INSTANCE, vr.getElemType());
			return new VectorType(new VectorType(IntType.INSTANCE, vl.getSize()), vr.getSize());
		}

		checkType(IntType.INSTANCE, leftType);
		checkType(IntType.INSTANCE, rightType);
		return IntType.INSTANCE;
	}

	@Override
	public Type visit(Not exp) {
		Type type = exp.getExp().accept(this);
		checkType(BoolType.INSTANCE, type);
		return BoolType.INSTANCE;
	}

	@Override
	public Type visit(PairLit exp) {
		Type left = exp.getLeft().accept(this);
		Type right = exp.getRight().accept(this);
		return new PairType(left, right);
	}

	@Override
	public Type visit(SingleVect exp) {
		Type type = exp.getExp().accept(this);
		return new VectorType(type, 1);
	}

	@Override
	public Type visit(Snd exp) {
		Type type = exp.getExp().accept(this);
		if (type instanceof PairType) {
			return ((PairType) type).getSndType();
		}
		if (type instanceof VectorType) {
			VectorType vt = (VectorType) type;
			if (vt.getElemType() instanceof PairType) {
				PairType pt = (PairType) vt.getElemType();
				return new VectorType(pt.getSndType(), vt.getSize());
			}
		}
		throw new RuntimeException("Snd: expected pair or vector of pairs");
	}

	@Override
	public Type visit(Variable exp) {
		return env.lookup(exp.name());
	}

	@Override
	public Type visit(Zip exp) {
		Type left = exp.getLeft().accept(this);
		Type right = exp.getRight().accept(this);
		
		if (left instanceof VectorType && right instanceof VectorType) {
			VectorType vl = (VectorType) left;
			VectorType vr = (VectorType) right;
			if (vl.getSize() != vr.getSize()) {
				throw new RuntimeException("Zip: vectors must have same size");
			}
			return new VectorType(new PairType(vl.getElemType(), vr.getElemType()), vl.getSize());
		}
		throw new RuntimeException("Zip: expected vectors");
	}

	@Override
	public Type visit(AssertStmt stmt) {
		Type type = stmt.getExp().accept(this);
		checkType(BoolType.INSTANCE, type);
		return null;
	}

	@Override
	public Type visit(AssignStmt stmt) {
		Type expType = stmt.getExp().accept(this);
		Type varType = env.lookup(stmt.getIdent().name());
		checkType(varType, expType);
		return null;
	}

	@Override
	public Type visit(Block stmt) {
		env.enterScope();
		stmt.getStmtSeq().accept(this);
		env.exitScope();
		return null;
	}

	@Override
	public Type visit(ForEachStmt stmt) {
		Type expType = stmt.getExp().accept(this);
		if (!(expType instanceof VectorType)) {
			throw new RuntimeException("ForEach: expression must be a vector");
		}
		VectorType vt = (VectorType) expType;
		
		env.enterScope(); // scope intermedio per la variabile del ciclo
		env.dec(stmt.getIdent().name(), vt.getElemType());
		stmt.getBlock().accept(this); // il blocco creerà il suo scope interno
		env.exitScope();
		
		return null;
	}

	@Override
	public Type visit(IfStmt stmt) {
		Type cond = stmt.getExp().accept(this);
		checkType(BoolType.INSTANCE, cond);
		stmt.getThenBlock().accept(this);
		if (stmt.getElseBlock() != null) {
			stmt.getElseBlock().accept(this);
		}
		return null;
	}

	@Override
	public Type visit(PrintStmt stmt) {
		stmt.getExp().accept(this);
		return null;
	}

	@Override
	public Type visit(VarStmt stmt) {
		Type expType = stmt.getExp().accept(this);
		env.dec(stmt.getIdent().name(), expType);
		return null;
	}

	@Override
	public Type visit(EmptyStmtSeq seq) {
		return null;
	}

	@Override
	public Type visit(NonEmptyStmtSeq seq) {
		seq.getFirst().accept(this);
		seq.getRest().accept(this);
		return null;
	}

	@Override
	public Type visit(ExpProg prog) {
		env.enterScope();
		prog.getStmtSeq().accept(this);
		env.exitScope();
		return null;
	}
}
