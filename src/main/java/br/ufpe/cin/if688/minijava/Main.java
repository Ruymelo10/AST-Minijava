package br.ufpe.cin.if688.minijava;

import br.ufpe.cin.if688.minijava.Antlr.MiniJavaGrammarLexer;
import br.ufpe.cin.if688.minijava.Antlr.MiniJavaGrammarParser;
import br.ufpe.cin.if688.minijava.ast.Program;
import br.ufpe.cin.if688.minijava.visitor.BuildSymbolTableVisitor;
import br.ufpe.cin.if688.minijava.visitor.MiniJavaVisitor;
import br.ufpe.cin.if688.minijava.visitor.TypeCheckVisitor;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;

public class Main {
    //    /home/mario/Documents/git/CompilersIudex/#4_MiniJavaAst/MiniJava/src/main/java/br/ufpe/cin/if688/ANTLR/tests
///home/mario/Documents/git/CompilersIudex/#4_MiniJavaAst/MiniJava/src/main/java/br/ufpe/cin/if688/ANTLR/MainTest.java
    public static void main(String[] args) throws IOException {
        // Creating AST
        Program program = (Program) new MiniJavaVisitor().visit(new MiniJavaGrammarParser(new CommonTokenStream(new MiniJavaGrammarLexer(CharStreams.fromStream(System.in)))).goal());

        // Activity 5 test - Building SymbolTable and checking types
        BuildSymbolTableVisitor buildSymbolTableVisitor = new BuildSymbolTableVisitor();
        buildSymbolTableVisitor.visit(program);

        System.out.println(buildSymbolTableVisitor.getSymbolTable());

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor(buildSymbolTableVisitor.getSymbolTable());
        typeCheckVisitor.visit(program);
    }
}