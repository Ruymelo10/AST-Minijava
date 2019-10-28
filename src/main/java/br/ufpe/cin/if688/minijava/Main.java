package br.ufpe.cin.if688.minijava;

import br.ufpe.cin.if688.minijava.*;
import br.ufpe.cin.if688.minijava.Antlr.MiniJavaGrammarLexer;
import br.ufpe.cin.if688.minijava.Antlr.MiniJavaGrammarParser;
import br.ufpe.cin.if688.minijava.ast.Program;
import br.ufpe.cin.if688.minijava.visitor.MiniJavaVisitor;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import java.io.IOException;


public class Main {

    public static void main(String[] args) throws IOException{

        Program program = (Program) new MiniJavaVisitor().visit(new MiniJavaGrammarParser(new CommonTokenStream(new MiniJavaGrammarLexer(CharStreams.fromFileName("C:\\Faculdade\\AST-Minijava\\src\\main\\java\\br\\ufpe\\cin\\if688\\minijava\\test.txt")))).goal());
        /*MainClass main = new MainClass(
                new Identifier("Teste"),
                new Identifier("Testando"),
                new Print(new IntegerLiteral(2))
        );

        VarDeclList vdl1 = new VarDeclList();
        vdl1.addElement(new VarDecl(
                new BooleanType(),
                new Identifier("flag")
        ));
        vdl1.addElement(new VarDecl(
                new IntegerType(),
                new Identifier("num")
        ));

        MethodDeclList mdl = new MethodDeclList();

        ClassDeclSimple A = new ClassDeclSimple(
                new Identifier("A"), vdl1, mdl
        );

        ClassDeclExtends B = new ClassDeclExtends(
                new Identifier("B"), new Identifier("A"),
                new VarDeclList(), new MethodDeclList()
        );

        VarDeclList vdl2 = new VarDeclList();
        vdl2.addElement(new VarDecl(
                new IdentifierType("A"),
                new Identifier("obj")
        ));
        ClassDeclSimple C = new ClassDeclSimple(
                new Identifier("C"), vdl2, new MethodDeclList()
        );

        ClassDeclList cdl = new ClassDeclList();
        cdl.addElement(A);
        cdl.addElement(B);
        cdl.addElement(C);

        Program p = new Program(main, cdl);

        PrettyPrintVisitor ppv = new PrettyPrintVisitor();
        ppv.visit(p);*/
    }

}