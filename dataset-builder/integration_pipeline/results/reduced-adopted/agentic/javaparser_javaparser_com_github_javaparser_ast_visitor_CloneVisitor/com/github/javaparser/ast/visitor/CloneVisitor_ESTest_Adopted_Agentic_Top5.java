package com.github.javaparser.ast.visitor;
import ArrayCreationExpr;
import ArrayInitializerExpr;
import CloneVisitor;
import ConditionalExpr;
import ForEachStmt;
import ForStmt;
import InstanceOfExpr;
import UnknownType;
import Visitable;
import com.github.javaparser.ast.ArrayCreationLevel;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.comments.BlockComment;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
class CloneVisitor_ESTest_Adopted_Agentic_Top5 {
    @Test
    void visitForStmt_withBlockCommentListContext_returnsEqualButNotSame() {
        CloneVisitor visitor = new CloneVisitor();
        ForStmt forStmt = new ForStmt();
        BlockComment[] commentArray = new BlockComment[0];
        NodeList<BlockComment> comments = new NodeList<>(commentArray);
        Visitable cloned = visitor.visit(forStmt, comments);
        assertNotSame(cloned, forStmt);
        assertTrue(cloned.equals(forStmt));
    }

    @Test
    void visitForEachStmt_withObject_returnsClonedNode() {
        CloneVisitor visitor = new CloneVisitor();
        ForEachStmt forEach = new ForEachStmt();
        Object context = new Object();
        Visitable cloned = visitor.visit(forEach, context);
        assertNotSame(cloned, forEach);
    }

    @Test
    void visitInstanceOfExpr_withBlockComment_returnsEqualButNotSame() {
        CloneVisitor visitor = new CloneVisitor();
        InstanceOfExpr instanceOfExpr = new InstanceOfExpr();
        BlockComment contextComment = new BlockComment();
        Visitable cloned = visitor.visit(instanceOfExpr, contextComment);
        assertTrue(cloned.equals(instanceOfExpr));
        assertNotSame(cloned, instanceOfExpr);
    }

    @Test
    void visitConditionalExpr_withObject_returnsEqualButNotSame() {
        CloneVisitor visitor = new CloneVisitor();
        ConditionalExpr conditional = new ConditionalExpr();
        Object context = new Object();
        Visitable cloned = visitor.visit(conditional, context);
        assertNotSame(cloned, conditional);
        assertTrue(cloned.equals(conditional));
    }

    @Test
    void visitArrayCreationExpr_withObject_returnsEqualButNotSame() {
        CloneVisitor visitor = new CloneVisitor();
        UnknownType unknown = new UnknownType();
        Object context = new Object();
        NodeList<ArrayCreationLevel> levels = new NodeList<>();
        ArrayInitializerExpr initializer = new ArrayInitializerExpr();
        ArrayCreationExpr arrayCreation = new ArrayCreationExpr(unknown, levels, initializer);
        Visitable cloned = visitor.visit(arrayCreation, context);
        assertTrue(cloned.equals(arrayCreation));
        assertNotSame(cloned, arrayCreation);
    }
}