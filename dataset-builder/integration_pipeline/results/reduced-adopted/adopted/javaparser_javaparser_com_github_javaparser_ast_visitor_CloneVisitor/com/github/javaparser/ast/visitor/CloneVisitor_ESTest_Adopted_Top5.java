package com.github.javaparser.ast.visitor;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.RecordDeclaration;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import org.junit.Test;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
public class CloneVisitor_ESTest_Adopted_Top5 {
    @Test(timeout = 4000)
    public void visitForStmt_withBlockCommentListContext_returnsEqualButNotSame() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        ForStmt forStmt = new ForStmt();
        BlockComment[] commentArray = new BlockComment[0];
        NodeList<BlockComment> comments = new NodeList<BlockComment>(commentArray);
        Visitable cloned = visitor.visit(forStmt, ((Object) (comments)));
        assertNotSame(cloned, forStmt);
        assertTrue(cloned.equals(((Object) (forStmt))));
    }

    @Test(timeout = 4000)
    public void visitForEachStmt_withObject_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        ForEachStmt forEach = new ForEachStmt();
        Object context = new Object();
        Visitable cloned = visitor.visit(forEach, context);
        assertNotSame(cloned, forEach);
    }

    @Test(timeout = 4000)
    public void visitRecordDeclaration_withModifiersContext_returnsEqual() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        Modifier[] modifiersArr = new Modifier[3];
        Modifier volatileModifier = Modifier.volatileModifier();
        modifiersArr[0] = volatileModifier;
        modifiersArr[1] = volatileModifier;
        modifiersArr[2] = modifiersArr[0];
        NodeList<Modifier> modifiers = new NodeList<Modifier>(modifiersArr);
        RecordDeclaration record = new RecordDeclaration(modifiers, "RECEIVER_PARAMETER");
        Visitable cloned = visitor.visit(record, ((Object) (modifiers)));
        assertTrue(cloned.equals(((Object) (record))));
    }

    @Test(timeout = 4000)
    public void visitCatchClause_withSelfContext_returnsEqualButNotSame() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        CatchClause catchClause = new CatchClause();
        Visitable cloned = visitor.visit(catchClause, ((Object) (catchClause)));
        assertTrue(cloned.equals(((Object) (catchClause))));
        assertNotSame(cloned, catchClause);
    }

    @Test(timeout = 4000)
    public void visitAssignExpr_withSelfContext_returnsEqualButNotSame() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        AssignExpr assign = new AssignExpr();
        Visitable cloned = visitor.visit(assign, ((Object) (assign)));
        assertNotSame(cloned, assign);
        assertTrue(cloned.equals(((Object) (assign))));
    }
}