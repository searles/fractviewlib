package at.searles.fractviewlib;

import at.searles.lexer.Lexer;
import at.searles.lexer.TokStream;
import at.searles.meelan.compiler.Ast;
import at.searles.meelan.optree.Tree;
import at.searles.meelan.optree.inlined.ExternDeclaration;
import at.searles.meelan.parser.MeelanParser;
import at.searles.meelan.parser.MeelanStream;
import at.searles.parsing.Recognizer;

import java.util.Map;

public class ParserInstance {

    private Map<String, ExternDeclaration> externDecls;

    private static class Holder {
        static final Recognizer EOF = Recognizer.eof(new Lexer());
    }

    public Tree parseExpr(String sourceCode) {
        MeelanStream stream = new MeelanStream(TokStream.fromString(sourceCode));

        Tree tree = MeelanParser.expr().parse(stream);

        if(!Holder.EOF.recognize(stream)) {
            // XXX (2019-08-02) re-activate.
        }

        this.externDecls = stream.getExternDecls();

        return tree;
    }

    public Ast parseSource(String sourceCode) {
        MeelanStream stream = new MeelanStream(TokStream.fromString(sourceCode));

        Ast ast = Ast.parse(stream);

        if(!Holder.EOF.recognize(stream)) {
            // XXX (2019-08-02) re-activate.
        }

        this.externDecls = stream.getExternDecls();

        return ast;
    }

    /**
     * Extern Declarations of the last parse attempt.
     */
    public Map<String, ExternDeclaration> getExternDecls() {
        return externDecls;
    }
}
