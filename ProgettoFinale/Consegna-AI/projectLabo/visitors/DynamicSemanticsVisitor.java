package projectLabo.visitors;

import java.util.ArrayList;
import java.util.List;

import projectLabo.parser.ast.Add;
import projectLabo.parser.ast.And;
import projectLabo.parser.ast.AssertStmt;
import projectLabo.parser.ast.AssignStmt;
import projectLabo.parser.ast.Block;
import projectLabo.parser.ast.BoolLiteral;
import projectLabo.parser.ast.Cat;
import projectLabo.parser.ast.EmptyStmtSeq;
import projectLabo.parser.ast.Eq;
import projectLabo.parser.ast.ExpProg;
import projectLabo.parser.ast.Flatten;
import projectLabo.parser.ast.ForEachStmt;
import projectLabo.parser.ast.Fst;
import projectLabo.parser.ast.IfStmt;
import projectLabo.parser.ast.IntLiteral;
import projectLabo.parser.ast.Minus;
import projectLabo.parser.ast.Mul;
import projectLabo.parser.ast.NonEmptyStmtSeq;
import projectLabo.parser.ast.Not;
import projectLabo.parser.ast.PairLit;
import projectLabo.parser.ast.PrintStmt;
import projectLabo.parser.ast.Snd;
import projectLabo.parser.ast.VarStmt;
import projectLabo.parser.ast.Variable;
import projectLabo.parser.ast.Vector;
import projectLabo.parser.ast.Zip;
import projectLabo.visitors.environment.Environment;
import projectLabo.visitors.value.BoolValue;
import projectLabo.visitors.value.IntValue;
import projectLabo.visitors.value.PairValue;
import projectLabo.visitors.value.Value;
import projectLabo.visitors.value.VectorValue;

public class DynamicSemanticsVisitor implements Visitor<Value> {

	private final Environment<Value> env = new Environment<>();
	private final StringBuilder output = new StringBuilder();

	public String getOutput() {
		return output.toString();
	}

	// --- programs, statement sequences, blocks ---

	@Override
	public Value visitProg(ExpProg prog) {
		prog.getStmtSeq().accept(this);
		return null;
	}

	@Override
	public Value visitStmtSeq(NonEmptyStmtSeq seq) {
		seq.getFirst().accept(this);
		seq.getRest().accept(this);
		return null;
	}

	@Override
	public Value visitEmptyStmtSeq(EmptyStmtSeq seq) {
		return null;
	}

	@Override
	public Value visitBlock(Block block) {
		env.enterLevel();
		block.getStmtSeq().accept(this);
		env.exitLevel();
		return null;
	}

	// --- statements ---

	@Override
	public Value visitVarStmt(VarStmt stmt) {
		var statementValue = stmt.getExp().accept(this);
		env.declare(stmt.getVar().getName(), statementValue);
		return null;
	}

	@Override
	public Value visitAssignStmt(AssignStmt stmt) {
		var expectedValue = stmt.getExp().accept(this);
		env.update(stmt.getVar().getName(), expectedValue);
		return null;
	}

	@Override
	public Value visitPrintStmt(PrintStmt stmt) {
		var value = stmt.getExp().accept(this);
		output.append(value.toStringValue()).append("\n");
		return null;
	}

	@Override
	public Value visitIfStmt(IfStmt stmt) {
		var condition = toBool(stmt.getExp().accept(this));
		if (condition) {
			stmt.getThenBlock().accept(this);
		} else {
			var elseBlock = stmt.getElseBlock();
			if (elseBlock != null) {
				elseBlock.accept(this);
			}
		}
		return null;
	}

	@Override
	public Value visitAssertStmt(AssertStmt stmt) {
		var value = stmt.getExp().accept(this);
		if (!toBool(value)) {
			throw new DynamicSemanticsException("assertion failed");
		}
		return null;
	}

	@Override
	/**
	 * viene valutata l'espressione, il cui valore deve essere un vettore [v1,...,vn]... 
	 * la variabile di iterazione viene dichiarata in un nuovo livello di scope e inizializzata con un 
	 * valore qualsiasi (per esempio 0)... nel giro i-mo, prima di eseguire il blocco, 
	 * viene assegnato v_i alla variabile... al termine viene eliminato il livello di scope della variabile
	 * @param stmt
	 * @return
	 */
	public Value visitForEachStmt(ForEachStmt stmt) {
		var vector = toVector(stmt.getExp().accept(this));
		env.enterLevel();
		env.declare(stmt.getVar().getName(), new IntValue(0));
		for (var element : vector) {
			env.update(stmt.getVar().getName(), element);
			stmt.getBlock().accept(this);
		}
		env.exitLevel();
		return null;
	}

	// --- expressions ---

	@Override
	public Value visitAdd(Add exp) {
		var val1 = exp.getLeft().accept(this);
		if (val1 instanceof IntValue int1) {
			return new IntValue(int1.value() + toInt(exp.getRight().accept(this)));
		} else if (val1 instanceof VectorValue vec1) {
			return new VectorValue(vectorAddition(vec1.elements(), toVector(exp.getRight().accept(this))));
		}
		throw new DynamicSemanticsException("Found " + val1.getClass().getSimpleName()
				+ ", expected IntValue or VectorValue<IntValue>");
	}

	@Override
	public Value visitMul(Mul exp) {
		var val1 = exp.getLeft().accept(this);
		if (val1 instanceof IntValue int1) {
			return new IntValue(int1.value() * toInt(exp.getRight().accept(this)));
		} else if (val1 instanceof VectorValue vec1) {
			return new VectorValue(outerProduct(vec1.elements(), toVector(exp.getRight().accept(this))));
		}
		throw new DynamicSemanticsException("Found " + val1.getClass().getSimpleName()
				+ ", expected IntValue or VectorValue<IntValue>");
	}

	@Override
	public Value visitAnd(And exp) {
		var left = toBool(exp.getLeft().accept(this));
		var right = left && toBool(exp.getRight().accept(this));
		return new BoolValue(right);
	}

	@Override
	public Value visitEq(Eq exp) {
		var left = exp.getLeft().accept(this);
		var right = exp.getRight().accept(this);
		return new BoolValue(left.equals(right));
	}

	@Override
	public Value visitFst(Fst exp) {
		var value = exp.getExp().accept(this);
		if (value instanceof PairValue pair) {
			return pair.first();
		} else if (value instanceof VectorValue vec) {
			return new VectorValue(fstVector(vec.elements()));
		}
		throw new DynamicSemanticsException("Found " + value.getClass().getSimpleName()
				+ ", expected PairValue or VectorValue<PairValue>");
	}

	@Override
	public Value visitSnd(Snd exp) {
		var value = exp.getExp().accept(this);
		if (value instanceof PairValue pair) {
			return pair.second();
		} else if (value instanceof VectorValue vec) {
			return new VectorValue(sndVector(vec.elements()));
		}
		throw new DynamicSemanticsException("Found " + value.getClass().getSimpleName()
				+ ", expected PairValue or VectorValue<PairValue>");
	}

	@Override
	public Value visitMinus(Minus exp) {
		var value = exp.getExp().accept(this);
		return new IntValue(-toInt(value));
	}

	@Override
	public Value visitNot(Not exp) {
		var value = exp.getExp().accept(this);
		return new BoolValue(!toBool(value));
	}

	@Override
	public Value visitPairLit(PairLit exp) {
		var left = exp.getLeft().accept(this);
		var right = exp.getRight().accept(this);
		return new PairValue(left, right);
	}

	@Override
	public Value visitIntLiteral(IntLiteral exp) {
		return new IntValue(exp.getValue());
	}

	@Override
	public Value visitBoolLiteral(BoolLiteral exp) {
		return new BoolValue(exp.getValue());
	}

	@Override
	public Value visitVariable(Variable exp) {
		return env.lookup(exp.getName());
	}

	@Override
	public Value visitVector(Vector exp) {
		var value = exp.getExp().accept(this);
		return new VectorValue(List.of(value));
	}

	@Override
	public Value visitCat(Cat exp) {
		var list1 = toVector(exp.getLeft().accept(this));
		var list2 = toVector(exp.getRight().accept(this));
		var result = new ArrayList<>(list1);
		result.addAll(list2);
		return new VectorValue(result);
	}

	@Override
	public Value visitZip(Zip exp) {
		var list1 = toVector(exp.getLeft().accept(this));
		var list2 = toVector(exp.getRight().accept(this));
		checkSameSize(list1, list2);
		var result = new ArrayList<Value>();
		for (int i = 0; i < list1.size(); i++) {
			result.add(new PairValue(list1.get(i), list2.get(i)));
		}
		return new VectorValue(result);
	}

	@Override
	public Value visitFlatten(Flatten exp) {
		var list = toVector(exp.getExp().accept(this));
		var result = new ArrayList<Value>();
		for (var element : list) {
			result.addAll(toVector(element));
		}
		return new VectorValue(result);
	}

	// --- auxiliary functions on vectors ---

	private void checkSameSize(List<Value> list1, List<Value> list2) {
		if (list1.size() != list2.size()) {
			throw new DynamicSemanticsException("vectors must have the same size");
		}
	}

	private List<Value> vectorAddition(List<Value> list1, List<Value> list2) {
		checkSameSize(list1, list2);
		var result = new ArrayList<Value>();
		for (int i = 0; i < list1.size(); i++) {
			result.add(new IntValue(toInt(list1.get(i)) + toInt(list2.get(i))));
		}
		return result;
	}

	private List<Value> outerProduct(List<Value> list1, List<Value> list2) {
		var result = new ArrayList<Value>();
		for (var scalar : list2) {
			var column = new ArrayList<Value>();
			for (var element : list1) {
				column.add(new IntValue(toInt(element) * toInt(scalar)));
			}
			result.add(new VectorValue(column));
		}
		return result;
	}

	private List<Value> fstVector(List<Value> list) {
		var result = new ArrayList<Value>();
		for (var element : list) {
			result.add(toPair(element).first());
		}
		return result;
	}

	private List<Value> sndVector(List<Value> list) {
		var result = new ArrayList<Value>();
		for (var element : list) {
			result.add(toPair(element).second());
		}
		return result;
	}

	// --- dynamic conversions (mirroring the toInt/toBool/toPair/toVector of Semantics.fs) ---

	private int toInt(Value value) {
		if (value instanceof IntValue intVal) {
			return intVal.value();
		}
		throw new DynamicSemanticsException("Found " + value.getClass().getSimpleName() + ", expected IntValue");
	}

	private boolean toBool(Value value) {
		if (value instanceof BoolValue boolVal) {
			return boolVal.value();
		}
		throw new DynamicSemanticsException("Found " + value.getClass().getSimpleName() + ", expected BoolValue");
	}

	private PairValue toPair(Value value) {
		if (value instanceof PairValue pairVal) {
			return pairVal;
		}
		throw new DynamicSemanticsException("Found " + value.getClass().getSimpleName() + ", expected PairValue");
	}

	private List<Value> toVector(Value value) {
		if (value instanceof VectorValue vectorVal) {
			return vectorVal.elements();
		}
		throw new DynamicSemanticsException("Found " + value.getClass().getSimpleName() + ", expected VectorValue");
	}
}
