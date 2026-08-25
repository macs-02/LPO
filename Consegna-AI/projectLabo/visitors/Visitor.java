package projectLabo.visitors;

import projectLabo.parser.ast.*;

public interface Visitor<T> {
	// Programs
	T visitProg(ExpProg prog);

	// Statements
	T visitStmtSeq(NonEmptyStmtSeq seq);
	T visitEmptyStmtSeq(EmptyStmtSeq seq);
	T visitBlock(Block block);
	T visitVarStmt(VarStmt stmt);
	T visitAssignStmt(AssignStmt stmt);
	T visitPrintStmt(PrintStmt stmt);
	T visitIfStmt(IfStmt stmt);
	T visitAssertStmt(AssertStmt stmt);
	T visitForEachStmt(ForEachStmt stmt);

	// Expressions
	T visitAdd(Add exp);
	T visitMul(Mul exp);
	T visitAnd(And exp);
	T visitEq(Eq exp);
	T visitPairLit(PairLit exp);
	T visitFst(Fst exp);
	T visitSnd(Snd exp);
	T visitMinus(Minus exp);
	T visitNot(Not exp);
	T visitIntLiteral(IntLiteral exp);
	T visitBoolLiteral(BoolLiteral exp);
	T visitVariable(Variable exp);
	T visitVector(Vector exp);
	T visitCat(Cat exp);
	T visitZip(Zip exp);
	T visitFlatten(Flatten exp);
}
