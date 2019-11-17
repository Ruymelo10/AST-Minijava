package br.ufpe.cin.if688.minijava.visitor;

import br.ufpe.cin.if688.minijava.ast.*;
import br.ufpe.cin.if688.minijava.symboltable.Class;
import br.ufpe.cin.if688.minijava.symboltable.Method;
import br.ufpe.cin.if688.minijava.symboltable.SymbolTable;

public class BuildSymbolTableVisitor implements IVisitor<Void> {

	SymbolTable symbolTable;

	public BuildSymbolTableVisitor() {
		symbolTable = new SymbolTable();
	}

	public SymbolTable getSymbolTable() {
		return symbolTable;
	}

	private Class currClass;
	private Method currMethod;
	private boolean fromMethod = false;

	// MainClass m;
	// ClassDeclList cl;
	public Void visit(Program n) {
		n.m.accept(this);
		for (int i = 0; i < n.cl.size(); i++) {
			n.cl.elementAt(i).accept(this);
		}
		return null;
	}

	// Identifier i1,i2;
	// Statement s;
	public Void visit(MainClass n) {
		this.symbolTable.addClass(n.i1.toString(), null);
		this.currClass = this.symbolTable.getClass(n.i1.toString());
		this.currClass.addMethod("main", null);
		Type t = new IntegerType();
		this.currClass.getMethod("main").addParam(n.i2.toString(), t);
		n.i1.accept(this);
		n.i2.accept(this);
		n.s.accept(this);
		return null;
	}

	// Identifier i;
	// VarDeclList vl;
	// MethodDeclList ml;
	public Void visit(ClassDeclSimple n) {
		if(this.symbolTable.addClass(n.i.toString(), null)){
			this.currClass = this.symbolTable.getClass(n.i.toString());
			n.i.accept(this);
			for (int i = 0; i < n.vl.size(); i++) {
				n.vl.elementAt(i).accept(this);
			}
			for (int i = 0; i < n.ml.size(); i++) {
				n.ml.elementAt(i).accept(this);
			}
		}else{
			System.out.println("Class " + n.i.toString() + " is already defined in program.");
		}
		return null;
	}

	// Identifier i;
	// Identifier j;
	// VarDeclList vl;
	// MethodDeclList ml;
	public Void visit(ClassDeclExtends n) {
		if(this.symbolTable.addClass(n.i.toString(), n.j.toString())){
			n.i.accept(this);
			n.j.accept(this);
			for (int i = 0; i < n.vl.size(); i++) {
				n.vl.elementAt(i).accept(this);
			}
			for (int i = 0; i < n.ml.size(); i++) {
				n.ml.elementAt(i).accept(this);
			}
		}else{
			System.out.println("Class " + n.i.toString() + " is already defined in program.");
		}
		return null;
	}

	// Type t;
	// Identifier i;
	public Void visit(VarDecl n) {
		n.t.accept(this);
		n.i.accept(this);
		if(this.fromMethod){
			if(!this.currMethod.addVar(n.i.toString(), n.t)){
				System.out.println("Variable " + n.i.toString() + " is already defined in method " + this.currMethod.getId()+".");
			}
		}else if(!this.currClass.addVar(n.i.toString(), n.t)){
			System.out.println("Variable " + n.i.toString() + " is already defined in class " + this.currClass.getId()+".");
		}
		return null;
	}

	// Type t;
	// Identifier i;
	// FormalList fl;
	// VarDeclList vl;
	// StatementList sl;
	// Exp e;
	public Void visit(MethodDecl n) {
		this.fromMethod = true;
		if(this.currClass.addMethod(n.i.toString(), n.t)){
			this.currMethod = this.currClass.getMethod(n.i.toString());
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
			n.e.accept(this);
		}else{
			System.out.println("Method " + n.i.toString() + " is already defined in class " + this.currClass.getId() + ".");
		}
		this.fromMethod = true;
		return null;
	}

	// Type t;
	// Identifier i;
	public Void visit(Formal n) {
		n.t.accept(this);
		n.i.accept(this);
		if(!this.currMethod.addParam(n.i.toString(),n.t)) {
			System.out.println("Variable " + n.i.toString() + " is already defined in method " + this.currMethod.getId()+".");
		}
		return null;
	}

	public Void visit(IntArrayType n) {
		return null;
	}

	public Void visit(BooleanType n) {
		return null;
	}

	public Void visit(IntegerType n) {
		return null;
	}

	// String s;
	public Void visit(IdentifierType n) {
		return null;
	}

	// StatementList sl;
	public Void visit(Block n) {
		for (int i = 0; i < n.sl.size(); i++) {
			n.sl.elementAt(i).accept(this);
		}
		return null;
	}

	// Exp e;
	// Statement s1,s2;
	public Void visit(If n) {
		n.e.accept(this);
		n.s1.accept(this);
		n.s2.accept(this);
		return null;
	}

	// Exp e;
	// Statement s;
	public Void visit(While n) {
		n.e.accept(this);
		n.s.accept(this);
		return null;
	}

	// Exp e;
	public Void visit(Print n) {
		n.e.accept(this);
		return null;
	}

	// Identifier i;
	// Exp e;
	public Void visit(Assign n) {
		n.i.accept(this);
		n.e.accept(this);
		return null;
	}

	// Identifier i;
	// Exp e1,e2;
	public Void visit(ArrayAssign n) {
		n.i.accept(this);
		n.e1.accept(this);
		n.e2.accept(this);
		return null;
	}

	// Exp e1,e2;
	public Void visit(And n) {
		n.e1.accept(this);
		n.e2.accept(this);
		return null;
	}

	// Exp e1,e2;
	public Void visit(LessThan n) {
		n.e1.accept(this);
		n.e2.accept(this);
		return null;
	}

	// Exp e1,e2;
	public Void visit(Plus n) {
		n.e1.accept(this);
		n.e2.accept(this);
		return null;
	}

	// Exp e1,e2;
	public Void visit(Minus n) {
		n.e1.accept(this);
		n.e2.accept(this);
		return null;
	}

	// Exp e1,e2;
	public Void visit(Times n) {
		n.e1.accept(this);
		n.e2.accept(this);
		return null;
	}

	// Exp e1,e2;
	public Void visit(ArrayLookup n) {
		n.e1.accept(this);
		n.e2.accept(this);
		return null;
	}

	// Exp e;
	public Void visit(ArrayLength n) {
		n.e.accept(this);
		return null;
	}

	// Exp e;
	// Identifier i;
	// ExpList el;
	public Void visit(Call n) {
		n.e.accept(this);
		n.i.accept(this);
		for (int i = 0; i < n.el.size(); i++) {
			n.el.elementAt(i).accept(this);
		}
		return null;
	}

	// int i;
	public Void visit(IntegerLiteral n) {
		return null;
	}

	public Void visit(True n) {
		return null;
	}

	public Void visit(False n) {
		return null;
	}

	// String s;
	public Void visit(IdentifierExp n) {
		return null;
	}

	public Void visit(This n) {
		return null;
	}

	// Exp e;
	public Void visit(NewArray n) {
		n.e.accept(this);
		return null;
	}

	// Identifier i;
	public Void visit(NewObject n) {
		return null;
	}

	// Exp e;
	public Void visit(Not n) {
		n.e.accept(this);
		return null;
	}

	// String s;
	public Void visit(Identifier n) {
		return null;
	}
}
