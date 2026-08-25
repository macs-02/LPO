package projectLabo.visitors.evaluation;

import projectLabo.parser.ast.*;
import projectLabo.visitors.Visitor;
import projectLabo.visitors.environments.Environment;
import java.util.ArrayList;
import java.util.List;

import java.io.PrintWriter;

public class Evaluator implements Visitor<Value> {

	private final Environment<Value> env;
	private final PrintWriter out;

	public Evaluator(Environment<Value> env, PrintWriter out) {
		this.env = env;
		this.out = out;
	}

	@Override
	public Value visit(Add exp) {
		Value left = exp.getLeft().accept(this);
		Value right = exp.getRight().accept(this);

		if (left instanceof VectorValue && right instanceof VectorValue) {
			List<Value> lList = ((VectorValue) left).getElements();
			List<Value> rList = ((VectorValue) right).getElements();
			List<Value> result = new ArrayList<>();
			for (int i = 0; i < lList.size(); i++) {
				int l = ((IntValue) lList.get(i)).getValue();
				int r = ((IntValue) rList.get(i)).getValue();
				result.add(new IntValue(l + r));
			}
			return new VectorValue(result);
		}

		int l = ((IntValue) left).getValue();
		int r = ((IntValue) right).getValue();
		return new IntValue(l + r);
	}

	@Override
	public Value visit(And exp) {
		Value left = exp.getLeft().accept(this);
		if (!((BoolValue) left).getValue()) return new BoolValue(false);
		Value right = exp.getRight().accept(this);
		return new BoolValue(((BoolValue) right).getValue());
	}

	@Override
	public Value visit(BoolLiteral exp) {
		return new BoolValue(exp.getValue());
	}

	@Override
	public Value visit(Cat exp) {
		VectorValue left = (VectorValue) exp.getLeft().accept(this);
		VectorValue right = (VectorValue) exp.getRight().accept(this);
		List<Value> res = new ArrayList<>(left.getElements());
		res.addAll(right.getElements());
		return new VectorValue(res);
	}

	@Override
	public Value visit(Eq exp) {
		Value left = exp.getLeft().accept(this);
		Value right = exp.getRight().accept(this);
		return new BoolValue(left.equals(right));
	}

	@Override
	public Value visit(Flatten exp) {
		VectorValue vv = (VectorValue) exp.getExp().accept(this);
		List<Value> res = new ArrayList<>();
		for (Value v : vv.getElements()) {
			res.addAll(((VectorValue) v).getElements());
		}
		return new VectorValue(res);
	}

	@Override
	public Value visit(Fst exp) {
		Value v = exp.getExp().accept(this);
		if (v instanceof PairValue) {
			return ((PairValue) v).getFstValue();
		}
		if (v instanceof VectorValue) {
			List<Value> res = new ArrayList<>();
			for (Value e : ((VectorValue) v).getElements()) {
				res.add(((PairValue) e).getFstValue());
			}
			return new VectorValue(res);
		}
		throw new RuntimeException("Fst: expected Pair or Vector of Pairs");
	}

	@Override
	public Value visit(IntLiteral exp) {
		return new IntValue(exp.getValue());
	}

	@Override
	public Value visit(Minus exp) {
		IntValue v = (IntValue) exp.getExp().accept(this);
		return new IntValue(-v.getValue());
	}

	@Override
	public Value visit(Mul exp) {
		Value left = exp.getLeft().accept(this);
		Value right = exp.getRight().accept(this);

		if (left instanceof VectorValue && right instanceof VectorValue) {
			List<Value> lList = ((VectorValue) left).getElements(); // dimensione m
			List<Value> rList = ((VectorValue) right).getElements(); // dimensione n
			List<Value> result = new ArrayList<>(); // avrà dimensione n e conterrà vettori di dimensione m
			
			for (int j = 0; j < rList.size(); j++) {
				List<Value> col = new ArrayList<>();
				int rVal = ((IntValue) rList.get(j)).getValue();
				for (int i = 0; i < lList.size(); i++) {
					int lVal = ((IntValue) lList.get(i)).getValue();
					col.add(new IntValue(rVal * lVal));
				}
				result.add(new VectorValue(col));
			}
			return new VectorValue(result);
		}

		int l = ((IntValue) left).getValue();
		int r = ((IntValue) right).getValue();
		return new IntValue(l * r);
	}

	@Override
	public Value visit(Not exp) {
		BoolValue b = (BoolValue) exp.getExp().accept(this);
		return new BoolValue(!b.getValue());
	}

	@Override
	public Value visit(PairLit exp) {
		Value fst = exp.getLeft().accept(this);
		Value snd = exp.getRight().accept(this);
		return new PairValue(fst, snd);
	}

	@Override
	public Value visit(SingleVect exp) {
		Value v = exp.getExp().accept(this);
		List<Value> res = new ArrayList<>();
		res.add(v);
		return new VectorValue(res);
	}

	@Override
	public Value visit(Snd exp) {
		Value v = exp.getExp().accept(this);
		if (v instanceof PairValue) {
			return ((PairValue) v).getSndValue();
		}
		if (v instanceof VectorValue) {
			List<Value> res = new ArrayList<>();
			for (Value e : ((VectorValue) v).getElements()) {
				res.add(((PairValue) e).getSndValue());
			}
			return new VectorValue(res);
		}
		throw new RuntimeException("Snd: expected Pair or Vector of Pairs");
	}

	@Override
	public Value visit(Variable exp) {
		return env.lookup(exp.name());
	}

	@Override
	public Value visit(Zip exp) {
		VectorValue left = (VectorValue) exp.getLeft().accept(this);
		VectorValue right = (VectorValue) exp.getRight().accept(this);
		List<Value> lList = left.getElements();
		List<Value> rList = right.getElements();
		List<Value> res = new ArrayList<>();
		for (int i = 0; i < lList.size(); i++) {
			res.add(new PairValue(lList.get(i), rList.get(i)));
		}
		return new VectorValue(res);
	}

	@Override
	public Value visit(AssertStmt stmt) {
		BoolValue b = (BoolValue) stmt.getExp().accept(this);
		if (!b.getValue()) {
			throw new RuntimeException("Assertion failed");
		}
		return null;
	}

	@Override
	public Value visit(AssignStmt stmt) {
		Value val = stmt.getExp().accept(this);
		env.update(stmt.getIdent().name(), val);
		return null;
	}

	@Override
	public Value visit(Block stmt) {
		env.enterScope();
		stmt.getStmtSeq().accept(this);
		env.exitScope();
		return null;
	}

	@Override
	public Value visit(ForEachStmt stmt) {
		VectorValue vv = (VectorValue) stmt.getExp().accept(this);
		String varName = stmt.getIdent().name();
		
		for (Value val : vv.getElements()) {
			env.enterScope();
			env.dec(varName, val);
			stmt.getBlock().accept(this);
			env.exitScope();
		}
		return null;
	}

	@Override
	public Value visit(IfStmt stmt) {
		BoolValue cond = (BoolValue) stmt.getExp().accept(this);
		if (cond.getValue()) {
			stmt.getThenBlock().accept(this);
		} else if (stmt.getElseBlock() != null) {
			stmt.getElseBlock().accept(this);
		}
		return null;
	}

	@Override
	public Value visit(PrintStmt stmt) {
		Value val = stmt.getExp().accept(this);
		out.println(val);
		return null;
	}

	@Override
	public Value visit(VarStmt stmt) {
		Value val = stmt.getExp().accept(this);
		env.dec(stmt.getIdent().name(), val);
		return null;
	}

	@Override
	public Value visit(EmptyStmtSeq seq) {
		return null;
	}

	@Override
	public Value visit(NonEmptyStmtSeq seq) {
		seq.getFirst().accept(this);
		seq.getRest().accept(this);
		return null;
	}

	@Override
	public Value visit(ExpProg prog) {
		env.enterScope();
		prog.getStmtSeq().accept(this);
		env.exitScope();
		return null;
	}
}
