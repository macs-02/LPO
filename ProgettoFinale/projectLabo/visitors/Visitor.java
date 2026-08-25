package projectLabo.visitors;

import projectLabo.parser.ast.*;

public interface Visitor<T> {
	T visit(Add exp);
	T visit(And exp);
	T visit(BoolLiteral exp);
	T visit(Cat exp);
	T visit(Eq exp);
	T visit(Flatten exp);
	T visit(Fst exp);
	T visit(IntLiteral exp);
	T visit(Minus exp);
	T visit(Mul exp);
	T visit(Not exp);
	T visit(PairLit exp);
	T visit(SingleVect exp);
	T visit(Snd exp);
	T visit(Variable exp);
	T visit(Zip exp);
	
	T visit(AssertStmt stmt);
	T visit(AssignStmt stmt);
	T visit(Block stmt);
	T visit(ForEachStmt stmt);
	T visit(IfStmt stmt);
	T visit(PrintStmt stmt);
	T visit(VarStmt stmt);
	
	T visit(EmptyStmtSeq seq);
	T visit(NonEmptyStmtSeq seq);
	
	T visit(ExpProg prog);
}
