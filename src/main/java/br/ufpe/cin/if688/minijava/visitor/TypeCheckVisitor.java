package br.ufpe.cin.if688.minijava.visitor;

import br.ufpe.cin.if688.minijava.ast.And;
import br.ufpe.cin.if688.minijava.ast.ArrayAssign;
import br.ufpe.cin.if688.minijava.ast.ArrayLength;
import br.ufpe.cin.if688.minijava.ast.ArrayLookup;
import br.ufpe.cin.if688.minijava.ast.Assign;
import br.ufpe.cin.if688.minijava.ast.Block;
import br.ufpe.cin.if688.minijava.ast.BooleanType;
import br.ufpe.cin.if688.minijava.ast.Call;
import br.ufpe.cin.if688.minijava.ast.ClassDeclExtends;
import br.ufpe.cin.if688.minijava.ast.ClassDeclSimple;
import br.ufpe.cin.if688.minijava.ast.False;
import br.ufpe.cin.if688.minijava.ast.Formal;
import br.ufpe.cin.if688.minijava.ast.Identifier;
import br.ufpe.cin.if688.minijava.ast.IdentifierExp;
import br.ufpe.cin.if688.minijava.ast.IdentifierType;
import br.ufpe.cin.if688.minijava.ast.If;
import br.ufpe.cin.if688.minijava.ast.IntArrayType;
import br.ufpe.cin.if688.minijava.ast.IntegerLiteral;
import br.ufpe.cin.if688.minijava.ast.IntegerType;
import br.ufpe.cin.if688.minijava.ast.LessThan;
import br.ufpe.cin.if688.minijava.ast.MainClass;
import br.ufpe.cin.if688.minijava.ast.MethodDecl;
import br.ufpe.cin.if688.minijava.ast.Minus;
import br.ufpe.cin.if688.minijava.ast.NewArray;
import br.ufpe.cin.if688.minijava.ast.NewObject;
import br.ufpe.cin.if688.minijava.ast.Not;
import br.ufpe.cin.if688.minijava.ast.Plus;
import br.ufpe.cin.if688.minijava.ast.Print;
import br.ufpe.cin.if688.minijava.ast.Program;
import br.ufpe.cin.if688.minijava.ast.This;
import br.ufpe.cin.if688.minijava.ast.Times;
import br.ufpe.cin.if688.minijava.ast.True;
import br.ufpe.cin.if688.minijava.ast.Type;
import br.ufpe.cin.if688.minijava.ast.VarDecl;
import br.ufpe.cin.if688.minijava.ast.While;
import br.ufpe.cin.if688.minijava.exceptions.PrintException;
import br.ufpe.cin.if688.minijava.symboltable.Class;
import br.ufpe.cin.if688.minijava.symboltable.Method;
import br.ufpe.cin.if688.minijava.symboltable.SymbolTable;

public class TypeCheckVisitor implements IVisitor<Type> {

	private SymbolTable symbolTable;
	private Class currentClass;
	private Method currentMethod;

	public TypeCheckVisitor(SymbolTable st) {
		symbolTable = st;
	}

	// MainClass m;
	// ClassDeclList cl;
	public Type visit(Program n) {
		n.m.accept(this);
		for (int i = 0; i < n.cl.size(); i++) {
			n.cl.elementAt(i).accept(this);
		}
		return null;
	}

	// Identifier i1,i2;
	// Statement s;
	public Type visit(MainClass n) {
	    this.currentClass = symbolTable.getClass(n.i1.s);
	    this.currentMethod = currentClass.getMethod("main");
		n.i1.accept(this);
		n.i2.accept(this);
		n.s.accept(this);
		this.currentClass = null;
		this.currentMethod = null;
		return null;
	}

	// Identifier i;
	// VarDeclList vl;
	// MethodDeclList ml;
	public Type visit(ClassDeclSimple n) {
		this.currentClass = symbolTable.getClass(n.i.s);
		n.i.accept(this);
		for (int i = 0; i < n.vl.size(); i++) {
			n.vl.elementAt(i).accept(this);
		}
		for (int i = 0; i < n.ml.size(); i++) {
			n.ml.elementAt(i).accept(this);
		}
		this.currentClass = null;
		return null;
	}

	// Identifier i;
	// Identifier j;
	// VarDeclList vl;
	// MethodDeclList ml;
	public Type visit(ClassDeclExtends n) {
		this.currentClass = symbolTable.getClass(n.i.s);
		if(symbolTable.getClass(n.j.s) == null){
			PrintException.idNotFound(n.j.s);
		}
		n.i.accept(this);
		n.j.accept(this);
		for (int i = 0; i < n.vl.size(); i++) {
			n.vl.elementAt(i).accept(this);
		}
		for (int i = 0; i < n.ml.size(); i++) {
			n.ml.elementAt(i).accept(this);
		}
		this.currentClass = null;
		return null;
	}

	// Type t;
	// Identifier i;
	public Type visit(VarDecl n) {
		n.t.accept(this);
		n.i.accept(this);
		return n.t;
	}

	// Type t;
	// Identifier i;
	// FormalList fl;
	// VarDeclList vl;
	// StatementList sl;
	// Exp e;
	public Type visit(MethodDecl n) {
		this.currentMethod = symbolTable.getMethod(n.i.s,this.currentClass.getId());
		n.t.accept(this);
		n.i.accept(this);
		for (int i = 0; i < n.fl.size(); i++) {
			n.fl.elementAt(i).accept(this);
		}
		for (int i = 0; i < n.vl.size(); i++) {
			n.vl.elementAt(i).accept(this);
		}
		for (int i = 0; i < n.sl.size(); i++) {
			n.sl.elementAt(i).accept(this);
		}
		Type t = n.e.accept(this);
		if(!symbolTable.compareTypes(t,n.t)){
			//PrintException.typeMatch(n.t,t);
		}
		this.currentMethod = null;
		return null;
	}

	// Type t;
	// Identifier i;
	public Type visit(Formal n) {
		n.t.accept(this);
		n.i.accept(this);
		return null;
	}

	public Type visit(IntArrayType n) {
		return n;
	}

	public Type visit(BooleanType n) {
		return n;
	}

	public Type visit(IntegerType n) {
		return n;
	}

	// String s;
	public Type visit(IdentifierType n) {
		if(symbolTable.getClass(n.s) == null){
			PrintException.idNotFound(n.s);
		}
		return n;
	}

	// StatementList sl;
	public Type visit(Block n) {
		for (int i = 0; i < n.sl.size(); i++) {
			n.sl.elementAt(i).accept(this);
		}
		return null;
	}

	// Exp e;
	// Statement s1,s2;
	public Type visit(If n) {
		Type t = n.e.accept(this);
		resolveBool(t);
		n.s1.accept(this);
		n.s2.accept(this);
		return null;
	}

	// Exp e;
	// Statement s;
	public Type visit(While n) {
		Type t = n.e.accept(this);
		resolveBool(t);
		n.s.accept(this);
		return null;
	}

	// Exp e;
	public Type visit(Print n) {
		n.e.accept(this);
		return null;
	}

	// Identifier i;
	// Exp e;
	public Type visit(Assign n) {
		//cuidado aqui
		Type t = n.i.accept(this);
		Type e = n.e.accept(this);
		if(symbolTable.compareTypes(t,e)){
			PrintException.typeMatch(t,e);
		}
		return t;
	}

	// Identifier i;
	// Exp e1,e2;
	public Type visit(ArrayAssign n) {
		n.i.accept(this);
		Type e = n.e1.accept(this);
		Type e2 = n.e2.accept(this);
		if (!(symbolTable.getVarType(this.currentMethod, this.currentClass, n.i.s) instanceof IntArrayType)) {
			PrintException.typeMatch(new IntArrayType(), symbolTable.getVarType(this.currentMethod, this.currentClass, n.i.s));
		}
		if(!(e instanceof IntegerType)) {
			PrintException.typeMatch(new IntegerType(), e);
		}
		if (!(e2 instanceof IntegerType)) {
			PrintException.typeMatch(new IntegerType(), e2);
		}
		return null;
	}

	// Exp e1,e2;
	public Type visit(And n) {
		Type t = n.e1.accept(this);
		Type e = n.e2.accept(this);
		if(!(t instanceof  BooleanType)){
			PrintException.typeMatch(new BooleanType(),t);
		}
		if(!(e instanceof  BooleanType)){
			PrintException.typeMatch(new BooleanType(),e);
		}
		return new BooleanType();
	}

	// Exp e1,e2;
	public Type visit(LessThan n) {
		Type t = n.e1.accept(this);
		Type e = n.e2.accept(this);
		if(!(t instanceof  IntegerType)){
			PrintException.typeMatch(new IntegerType(),t);
		}
		if(!(e instanceof  IntegerType)){
			PrintException.typeMatch(new IntegerType(),e);
		}
		return new IntegerType();
	}

	// Exp e1,e2;
	public Type visit(Plus n) {
		Type t = n.e1.accept(this);
		Type e = n.e2.accept(this);
		if(!(t instanceof  IntegerType)){
			PrintException.typeMatch(new IntegerType(),t);
		}
		if(!(e instanceof  IntegerType)){
			PrintException.typeMatch(new IntegerType(),e);
		}
		return new IntegerType();
	}

	// Exp e1,e2;
	public Type visit(Minus n) {
		Type t = n.e1.accept(this);
		Type e = n.e2.accept(this);
		if(!(t instanceof  IntegerType)){
			PrintException.typeMatch(new IntegerType(),t);
		}
		if(!(e instanceof  IntegerType)){
			PrintException.typeMatch(new IntegerType(),e);
		}
		return new IntegerType();
	}

	// Exp e1,e2;
	public Type visit(Times n) {
		Type t = n.e1.accept(this);
		Type e = n.e2.accept(this);
		if(!(t instanceof  IntegerType)){
			PrintException.typeMatch(new IntegerType(),t);
		}
		if(!(e instanceof  IntegerType)){
			PrintException.typeMatch(new IntegerType(),e);
		}
		return new IntegerType();
	}

	// Exp e1,e2;
	public Type visit(ArrayLookup n) {
		Type t = n.e1.accept(this);
		Type e = n.e2.accept(this);
		if(!(t instanceof IntArrayType)){
			PrintException.typeMatch(new IntArrayType(),t);
		}
		if(!(e instanceof IntegerType)){
			PrintException.typeMatch(new IntegerType(),e);
		}
		return new IntegerType();
	}

	// Exp e;
	public Type visit(ArrayLength n) {
		Type t = n.e.accept(this);
		if(!(t instanceof IntArrayType)){
			PrintException.typeMatch(new IntArrayType(),t);
		}
		return t;
	}

	// Exp e;
	// Identifier i;
	// ExpList el;
	public Type visit(Call n) {
		n.e.accept(this);
		n.i.accept(this);
		for (int i = 0; i < n.el.size(); i++) {
			n.el.elementAt(i).accept(this);
		}

		return null;
	}

	// int i;
	public Type visit(IntegerLiteral n) {
		return null;
	}

	public Type visit(True n) {
		return new BooleanType();
	}

	public Type visit(False n) {
		return null;
	}

	// String s;
	public Type visit(IdentifierExp n) {
		return null;
	}

	public Type visit(This n) {
		return null;
	}

	// Exp e;
	public Type visit(NewArray n) {
		n.e.accept(this);
		return null;
	}

	// Identifier i;
	public Type visit(NewObject n) {
		return null;
	}

	// Exp e;
	public Type visit(Not n) {
		n.e.accept(this);
		return null;
	}

	// String s;
	public Type visit(Identifier n) {
		return null;
	}

	public void resolveBool(Type t){
		if(!(t instanceof  BooleanType)){
			PrintException.typeMatch(new BooleanType(),t);
		}
	}
}
