package br.ufpe.cin.if688.minijava.visitor;

import br.ufpe.cin.if688.minijava.Antlr.MiniJavaGrammarParser;
import br.ufpe.cin.if688.minijava.Antlr.MiniJavaGrammarParser.StatementContext;
import br.ufpe.cin.if688.minijava.Antlr.MiniJavaGrammarVisitor;
import br.ufpe.cin.if688.minijava.ast.*;
import br.ufpe.cin.if688.minijava.symboltable.Method;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;


public class MiniJavaVisitor implements MiniJavaGrammarVisitor {
    public Object visit(ParseTree parseTree) {
        return parseTree.accept(this);
    }

    public Object visitChildren(RuleNode ruleNode) {
        return null;
    }

    public Object visitTerminal(TerminalNode terminalNode) {
        return null;
    }

    public Object visitErrorNode(ErrorNode errorNode) {
        return null;
    }

    public Object visitGoal(MiniJavaGrammarParser.GoalContext ctx) {
        MainClass main = (MainClass) ctx.mainClass().accept(this);
        ClassDeclList cdl = new ClassDeclList();
        for(MiniJavaGrammarParser.ClassDeclarationContext c : ctx.classDeclaration()){
            cdl.addElement((ClassDecl) c.accept(this));
        }
        return new Program(main,cdl);
    }



    public Object visitMainClass(MiniJavaGrammarParser.MainClassContext ctx) {
        Identifier ident1 = new Identifier(ctx.IDENTIFIER(0).getText());
        Identifier ident2 = new Identifier(ctx.IDENTIFIER(1).getText());
        Statement stm = (Statement) ctx.statement().accept(this);
        return new MainClass(ident1,ident2,stm);
    }

    public Object visitClassDeclaration(MiniJavaGrammarParser.ClassDeclarationContext ctx) {
        Identifier ident1 = new Identifier(ctx.IDENTIFIER(0).getText());
        VarDeclList vdl = new VarDeclList();
        MethodDeclList mdl = new MethodDeclList();
        for(MiniJavaGrammarParser.VarDeclarationContext vr : ctx.varDeclaration()){
            vdl.addElement((VarDecl) vr.accept(this));
        }
        for(MiniJavaGrammarParser.MethodDeclarationContext m : ctx.methodDeclaration()){
            mdl.addElement((MethodDecl) m.accept(this));
        }
        if(ctx.IDENTIFIER().size() == 1){
            return new ClassDeclSimple(ident1,vdl,mdl);
        }else {
            Identifier ident2 = new Identifier(ctx.IDENTIFIER(1).getText());
            return new ClassDeclExtends(ident1,ident2,vdl,mdl);
        }
    }

    public Object visitVarDeclaration(MiniJavaGrammarParser.VarDeclarationContext ctx) {
        Type t = (Type) ctx.type().accept(this);
        Identifier ident1 = new Identifier(ctx.IDENTIFIER().getText());
        return new VarDecl(t,ident1);
    }

    public Object visitMethodDeclaration(MiniJavaGrammarParser.MethodDeclarationContext ctx) {
        Type t = (Type) ctx.type(0).accept(this);
        Identifier id = new Identifier(ctx.IDENTIFIER(0).getText());
        FormalList fl = new FormalList();
        VarDeclList vdl = new VarDeclList();
        StatementList sl = new StatementList();
        for(int i = 1; i < ctx.type().size(); i++) {
            fl.addElement(new Formal((Type) ctx.type(i).accept(this), new Identifier(ctx.IDENTIFIER(i).getText())));
        }
        for(MiniJavaGrammarParser.VarDeclarationContext vr : ctx.varDeclaration()){
            vdl.addElement((VarDecl) vr.accept(this));
        }
        for(StatementContext sc : ctx.statement()){
            sl.addElement((Statement) sc.accept(this));
        }
        Exp expr = (Exp) ctx.expression().accept(this);
        return new MethodDecl(t,id,fl,vdl,sl,expr);
    }

    public Object visitType(MiniJavaGrammarParser.TypeContext ctx) {
        String tipo = ctx.getText();
        if(tipo.equals("boolean")){
            return new BooleanType();
        }else if(tipo.equals("int")){
            return new IntegerType();
        }else if(tipo.equals("int[]")){
            return new IntArrayType();
        }else{
            return new IdentifierType(tipo);
        }
    }

    public Object visitStatement(StatementContext ctx) {
        String tipo = ctx.getStart().getText();
        if(tipo.equals("{")){
            StatementList sl = new StatementList();
            for(StatementContext sc : ctx.statement()){
                sl.addElement((Statement) sc.accept(this));
            }
            return new Block(sl);
        }else if(tipo.equals("if")){
            Exp expr = (Exp) ctx.expression(0).accept(this);
            Statement stm = (Statement) ctx.statement(0).accept(this);
            Statement stm1 = (Statement) ctx.statement(1).accept(this);
            return new If(expr,stm,stm1);
        }else if(tipo.equals("while")){
            Exp expr = (Exp) ctx.expression(0).accept(this);
            Statement stm = (Statement) ctx.statement(0).accept(this);
            return new While(expr,stm);
        }else if(tipo.equals("System.out.println")){
            Exp expr = (Exp) ctx.expression(0).accept(this);
            return new Print(expr);
        }else if(ctx.expression().size() > 1){
            //Tomar cuidado com identifierExp
            Identifier id = new Identifier(ctx.IDENTIFIER().getText());
            Exp expr1 = (Exp) ctx.expression(0).accept(this);
            Exp expr2 = (Exp) ctx.expression(0).accept(this);
            return new ArrayAssign(id,expr1,expr2);
        }else{
            Identifier id = new Identifier(ctx.IDENTIFIER().getText());
            Exp expr1 = (Exp) ctx.expression(0).accept(this);
            return new Assign(id,expr1);
        }
    }

    public Object visitExpression(MiniJavaGrammarParser.ExpressionContext ctx) {
        String start = ctx.getStart().getText(); //pegar o primeiro simbolo, pra saber 'new', 'int'...
        int expSize = ctx.expression().size(); //saber qual a qtd de expression
        int qtdChildren = ctx.getChildCount(); //saber o tamanho da expression

        //expression '.' identifier '(' ( expression ( ',' expression )* )? ')'
        if(qtdChildren >= 5 && ctx.getChild(1).getText().equals(".")) {
            Exp exp = (Exp) ctx.expression(0).accept(this);
            Identifier id = new Identifier(ctx.IDENTIFIER().getText());

            ExpList listExp = new ExpList();
            for(int i = 1; i < expSize; i++) {
                listExp.addElement((Exp) ctx.expression(i).accept(this));
            }

            return new Call(exp, id, listExp);
        } else if(expSize == 2) { //expression ( '&&' | '<' | '+' | '-' | '*' ) expression
            Exp ae1 = (Exp) ctx.expression(0).accept(this);
            Exp ae2 = (Exp) ctx.expression(1).accept(this);

            if(ctx.getChild(1).getText().equals("&&")){
                return new And(ae1, ae2);
            }else if(ctx.getChild(1).getText().equals("<")){
                return new LessThan(ae1, ae2);
            }else if(ctx.getChild(1).getText().equals("+")){
                return new Plus(ae1, ae2);
            }else if(ctx.getChild(1).getText().equals("-")){
                return new Minus(ae1, ae2);
            }else if(ctx.getChild(1).getText().equals("*")){
                return new Times(ae1, ae2);
            }else {
                return new ArrayLookup(ae1, ae2);
            }
        }
        else if(expSize == 1) {
            Exp ae = (Exp) ctx.expression(0).accept(this);
            if(start.equals("!")) {
                return new Not(ae);
            }else if(start.equals("(")) {
                return ae;
            }else if(start.equals("new")) {
                return new NewArray(ae);
            }else {
                return new ArrayLength(ae);
            }
        } else {
            if(start.equals("true")){
                return new True();
            }else if(start.equals("false")){
                return new False();
            }else if(start.equals("this")){
                return new This();
            }else if(start.equals("new")){
                return new NewObject(new Identifier(ctx.IDENTIFIER().getText()));
            }else{
                if(start.matches("\\d+")) {
                    return new IntegerLiteral(Integer.parseInt(ctx.getStart().getText()));
                } else {
                    return new IdentifierExp(ctx.IDENTIFIER().getText());
                }
            }
        }
    }
}