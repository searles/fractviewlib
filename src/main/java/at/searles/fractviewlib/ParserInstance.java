package at.searles.fractviewlib;

import at.searles.meelan.compiler.Ast;
import at.searles.meelan.optree.Tree;
import at.searles.meelan.parser.MeelanEnv;
import at.searles.meelan.parser.MeelanParser;
import at.searles.parsing.ParserStream;

public class ParserInstance {

    private final MeelanEnv env;

    private static class Holder {
        static final MeelanParser PARSER = new MeelanParser();
    }

    public ParserInstance() {
        this.env = new MeelanEnv();
    }

    public Tree parseExpr(String sourceCode) {
        ParserStream stream = ParserStream.fromString(sourceCode);

        Tree tree = Holder.PARSER.parseExpr(env, stream);

        if(!stream.isEmpty()) { // FIXME 2019-07-27 add method in parsing
            // FIXME some kind of warning that it was not fully parsed?
        }

        return tree;
    }

    public Ast parseSource(String sourceCode) {
        ParserStream stream = ParserStream.fromString(sourceCode);

        return Ast.parse(env, stream);

//        if(!stream.isEmpty()) {
//            // TODO 2018-07-11: There should be some warning in this case
//            // TODO but no exception because of backwards compatibility.
//            // throw new MeelanException("not fully parsed!", null);
//        }
    }
}
