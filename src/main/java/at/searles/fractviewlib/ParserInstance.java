package at.searles.fractviewlib;

import at.searles.lexer.Lexer;
import at.searles.meelan.compiler.Ast;
import at.searles.meelan.optree.Tree;
import at.searles.meelan.optree.inlined.ExternDeclaration;
import at.searles.meelan.parser.MeelanEnv;
import at.searles.meelan.parser.MeelanParser;
import at.searles.parsing.ParserStream;
import at.searles.parsing.Recognizer;

import java.util.Map;

public class ParserInstance {

    private final MeelanEnv env;

    private static class Holder {
        static final Recognizer EOF = Recognizer.eof(new Lexer());
    }

    public ParserInstance() {
        this.env = new MeelanEnv();
    }

    public Tree parseExpr(String sourceCode) {
        ParserStream stream = ParserStream.fromString(sourceCode);

        Tree tree = MeelanParser.expr().parse(env, stream);

        if(!Holder.EOF.recognize(env, stream)) {
            // XXX (2019-08-02) re-activate.
        }

        return tree;
    }

    public Ast parseSource(String sourceCode) {
        ParserStream stream = ParserStream.fromString(sourceCode);

        Ast ast = Ast.parse(env, stream);

        if(!Holder.EOF.recognize(env, stream)) {
            // XXX (2019-08-02) re-activate.
        }

        return ast;
    }

    public Map<String, ExternDeclaration> getExternDecls() {
        return env.getExternDecls();
    }
}
