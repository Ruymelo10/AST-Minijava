package br.ufpe.cin.if688.minijava;

import br.ufpe.cin.if688.minijava.Antlr.MiniJavaGrammarLexer;
import br.ufpe.cin.if688.minijava.Antlr.MiniJavaGrammarParser;
import br.ufpe.cin.if688.minijava.ast.Identifier;
import br.ufpe.cin.if688.minijava.ast.MainClass;
import br.ufpe.cin.if688.minijava.ast.Print;
import br.ufpe.cin.if688.minijava.ast.*;
import br.ufpe.cin.if688.minijava.ast.Program;
import br.ufpe.cin.if688.minijava.visitor.MiniJavaVisitor;
import br.ufpe.cin.if688.minijava.visitor.PrettyPrintVisitor;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import java.io.IOException;


public class Main {

    public static void main(String[] args) throws IOException{
        CharStream charStream = CharStreams.fromStream(System.in);
        MiniJavaGrammarLexer lexer = new MiniJavaGrammarLexer(charStream);

        CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);
        MiniJavaGrammarParser parser = new MiniJavaGrammarParser(commonTokenStream);

        MiniJavaVisitor miniJava = new MiniJavaVisitor();
        Program program = (Program) miniJava.visit(parser.goal());

        PrettyPrintVisitor printVisitor = new PrettyPrintVisitor();
        printVisitor.visit(program);
    }

}