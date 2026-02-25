/*
 * Copyright (C) 2007-2010 Júlio Vilmar Gesser.
 * Copyright (C) 2011, 2013-2024 The JavaParser Team.
 *
 * This file is part of JavaParser.
 *
 * JavaParser can be used either under the terms of
 * a) the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * b) the terms of the Apache License
 *
 * You should have received a copy of both licenses in LICENCE.LGPL and
 * LICENCE.APACHE. Please refer to those files for details.
 *
 * JavaParser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 */

package com.github.javaparser.ast.visitor;

import static com.github.javaparser.StaticJavaParser.parseType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.javaparser.JavaToken;
import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.ArrayCreationLevel;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.modules.ModuleRequiresDirective;
import com.github.javaparser.ast.modules.ModuleUsesDirective;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Corresponding manual test: {@link com.github.javaparser.ast.visitor.CloneVisitorTest}.
 * Manual test source on GitHub: <a href="https://github.com/javaparser/javaparser/blob/ef5de98c0681b6ecc24be128b9aed6761bbbf6a0/javaparser-core-testing/src/test/java/com/github/javaparser/ast/visitor/CloneVisitorTest.java">CloneVisitorTest</a>.
 * @see com.github.javaparser.ast.visitor.CloneVisitorTest
 */
class CloneVisitor_ESTest_Adopted_Agentic_Top5 {
    CompilationUnit cu;

    @BeforeEach
    void setUp() {
        cu = new CompilationUnit();
    }

    @AfterEach
    void teardown() {
        cu = null;
    }

    /**
     * This test added target-class coverage 2.60% for com.github.javaparser.ast.visitor.CloneVisitor (23/886 lines).
     * Delta details: +40 methods, +38 branches, +591 instructions.
     * Full version of the covered block is here: <a href="https://github.com/javaparser/javaparser/blob/ef5de98c0681b6ecc24be128b9aed6761bbbf6a0/javaparser-symbol-solver-testing/src/test/test_sourcecode/javaparser_src/proper_source/com/github/javaparser/ast/visitor/CloneVisitor.java#L991-L1000">CloneVisitor.java (lines 991-1000)</a>
     * Covered Lines:
     * <pre><code>
     *                 _n.getId()
     *         );
     *         r.setComment(comment);
     *         return r;
     *     }
     * 
     *     @Override
     *     public Node visit(ReturnStmt _n, Object _arg) {
     *         Expression expr = cloneNodes(_n.getExpr(), _arg);
     *         Comment comment = cloneNodes(_n.getComment(), _arg);
     * </code></pre>
     * Other newly covered ranges to check: 610-615;919-925
     */
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

    /**
     * This test added target-class coverage 3.72% for com.github.javaparser.ast.visitor.CloneVisitor (33/886 lines).
     * Delta details: +31 methods, +15 branches, +517 instructions.
     * Full version of the covered block is here: <a href="https://github.com/javaparser/javaparser/blob/ef5de98c0681b6ecc24be128b9aed6761bbbf6a0/javaparser-symbol-solver-testing/src/test/test_sourcecode/javaparser_src/proper_source/com/github/javaparser/ast/visitor/CloneVisitor.java#L726-L735">CloneVisitor.java (lines 726-735)</a>
     * Covered Lines:
     * <pre><code>
     *         return r;
     *     }
     * 
     *     @Override
     *     public Node visit(ObjectCreationExpr _n, Object _arg) {
     *         Expression scope = cloneNodes(_n.getScope(), _arg);
     *         ClassOrInterfaceType type_ = cloneNodes(_n.getType(), _arg);
     *         List&lt;Type&gt; typeArgs = visit(_n.getTypeArgs(), _arg);
     *         List&lt;Expression&gt; args = visit(_n.getArgs(), _arg);
     *         List&lt;BodyDeclaration&gt; anonymousBody = visit(_n.getAnonymousClassBody(), _arg);
     * </code></pre>
     * Other newly covered ranges to check: 644-650;919-925;978-986
     */
    @Test
    void visitForEachStmt_withObject_returnsClonedNode() {
        CloneVisitor visitor = new CloneVisitor();
        ForEachStmt forEach = new ForEachStmt();
        Object context = new Object();
        Visitable cloned = visitor.visit(forEach, context);
        assertNotSame(cloned, forEach);
    }

    /**
     * This test added target-class coverage 2.60% for com.github.javaparser.ast.visitor.CloneVisitor (23/886 lines).
     * Delta details: +34 methods, +34 branches, +519 instructions.
     * Full version of the covered block is here: <a href="https://github.com/javaparser/javaparser/blob/ef5de98c0681b6ecc24be128b9aed6761bbbf6a0/javaparser-symbol-solver-testing/src/test/test_sourcecode/javaparser_src/proper_source/com/github/javaparser/ast/visitor/CloneVisitor.java#L439-L447">CloneVisitor.java (lines 439-447)</a>
     * Covered Lines:
     * <pre><code>
     *         return r;
     *     }
     * 
     *     @Override
     *     public Node visit(ArrayAccessExpr _n, Object _arg) {
     *         Expression name = cloneNodes(_n.getName(), _arg);
     *         Expression index = cloneNodes(_n.getIndex(), _arg);
     *         Comment comment = cloneNodes(_n.getComment(), _arg);
     * 
     * </code></pre>
     * Other newly covered ranges to check: 416-422;452-458
     */
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

    /**
     * This test added target-class coverage 2.37% for com.github.javaparser.ast.visitor.CloneVisitor (21/886 lines).
     * Delta details: +35 methods, +29 branches, +507 instructions.
     * Full version of the covered block is here: <a href="https://github.com/javaparser/javaparser/blob/ef5de98c0681b6ecc24be128b9aed6761bbbf6a0/javaparser-symbol-solver-testing/src/test/test_sourcecode/javaparser_src/proper_source/com/github/javaparser/ast/visitor/CloneVisitor.java#L510-L518">CloneVisitor.java (lines 510-518)</a>
     * Covered Lines:
     * <pre><code>
     *         Expression right = cloneNodes(_n.getRight(), _arg);
     *         Comment comment = cloneNodes(_n.getComment(), _arg);
     * 
     *         BinaryExpr r = new BinaryExpr(
     *                 _n.getBeginLine(), _n.getBeginColumn(), _n.getEndLine(), _n.getEndColumn(),
     *                 left, right, _n.getOperator()
     *         );
     *         r.setComment(comment);
     *         return r;
     * </code></pre>
     * Other newly covered ranges to check: 560-565;610-615
     */
    @Test
    void visitConditionalExpr_withObject_returnsEqualButNotSame() {
        CloneVisitor visitor = new CloneVisitor();
        ConditionalExpr conditional = new ConditionalExpr();
        Object context = new Object();
        Visitable cloned = visitor.visit(conditional, context);
        assertNotSame(cloned, conditional);
        assertTrue(cloned.equals(conditional));
    }

    /**
     * This test added target-class coverage 1.81% for com.github.javaparser.ast.visitor.CloneVisitor (16/886 lines).
     * Delta details: +33 methods, +39 branches, +508 instructions.
     * Full version of the covered block is here: <a href="https://github.com/javaparser/javaparser/blob/ef5de98c0681b6ecc24be128b9aed6761bbbf6a0/javaparser-symbol-solver-testing/src/test/test_sourcecode/javaparser_src/proper_source/com/github/javaparser/ast/visitor/CloneVisitor.java#L547-L555">CloneVisitor.java (lines 547-555)</a>
     * Covered Lines:
     * <pre><code>
     * 
     *     @Override
     *     public Node visit(ConditionalExpr _n, Object _arg) {
     *         Expression condition = cloneNodes(_n.getCondition(), _arg);
     *         Expression thenExpr = cloneNodes(_n.getThenExpr(), _arg);
     *         Expression elseExpr = cloneNodes(_n.getElseExpr(), _arg);
     *         Comment comment = cloneNodes(_n.getComment(), _arg);
     * 
     *         ConditionalExpr r = new ConditionalExpr(
     * </code></pre>
     * Other newly covered ranges to check: 644-650
     */
    @Test
    void visitInstanceOfExpr_withBlockComment_returnsEqualButNotSame() {
        CloneVisitor visitor = new CloneVisitor();
        InstanceOfExpr instanceOfExpr = new InstanceOfExpr();
        BlockComment contextComment = new BlockComment();
        Visitable cloned = visitor.visit(instanceOfExpr, contextComment);
        assertTrue(cloned.equals(instanceOfExpr));
        assertNotSame(cloned, instanceOfExpr);
    }
}
