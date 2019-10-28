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
        return null;
    }
}