package com.github.javaparser.ast.visitor;
import com.github.javaparser.JavaToken;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.ArrayCreationLevel;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.AnnotationMemberDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.CompactConstructorDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.InitializerDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.ReceiverParameter;
import com.github.javaparser.ast.body.RecordDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.comments.MarkdownComment;
import com.github.javaparser.ast.comments.TraditionalJavadocComment;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.ArrayAccessExpr;
import com.github.javaparser.ast.expr.ArrayCreationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.CharLiteralExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.InstanceOfExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.MatchAllPatternExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.RecordPatternExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.SuperExpr;
import com.github.javaparser.ast.expr.SwitchExpr;
import com.github.javaparser.ast.expr.TextBlockLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.TypeExpr;
import com.github.javaparser.ast.expr.TypePatternExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.modules.ModuleDeclaration;
import com.github.javaparser.ast.modules.ModuleExportsDirective;
import com.github.javaparser.ast.modules.ModuleOpensDirective;
import com.github.javaparser.ast.modules.ModuleProvidesDirective;
import com.github.javaparser.ast.modules.ModuleRequiresDirective;
import com.github.javaparser.ast.modules.ModuleUsesDirective;
import com.github.javaparser.ast.stmt.AssertStmt;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.BreakStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ContinueStmt;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.EmptyStmt;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.LabeledStmt;
import com.github.javaparser.ast.stmt.LocalClassDeclarationStmt;
import com.github.javaparser.ast.stmt.LocalRecordDeclarationStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.SynchronizedStmt;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.stmt.UnparsableStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.stmt.YieldStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.IntersectionType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.TypeParameter;
import com.github.javaparser.ast.type.UnionType;
import com.github.javaparser.ast.type.UnknownType;
import com.github.javaparser.ast.type.VarType;
import com.github.javaparser.ast.type.VoidType;
import com.github.javaparser.ast.type.WildcardType;
import java.util.LinkedList;
import java.util.Optional;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
public class CloneVisitor_ESTest_Adopted_Top5 {

    /**
     * This test added target-class coverage 2.26% for com.github.javaparser.ast.visitor.CloneVisitor (20/886 lines).
     * Delta details: +40 methods, +53 branches, +704 instructions.
     * Full version of the covered block is here: <a href="https://github.com/javaparser/javaparser/blob/ef5de98c0681b6ecc24be128b9aed6761bbbf6a0/javaparser-symbol-solver-testing/src/test/test_sourcecode/javaparser_src/proper_source/com/github/javaparser/ast/visitor/CloneVisitor.java#L279-L290">CloneVisitor.java (lines 279-290)</a>
     * Covered Lines:
     * <pre><code>
     *         JavadocComment javaDoc = cloneNodes(_n.getJavaDoc(), _arg);
     *         List&lt;AnnotationExpr&gt; annotations = visit(_n.getAnnotations(), _arg);
     *         List&lt;TypeParameter&gt; typeParameters = visit(_n.getTypeParameters(), _arg);
     *         Type type_ = cloneNodes(_n.getType(), _arg);
     *         List&lt;Parameter&gt; parameters = visit(_n.getParameters(), _arg);
     *         List&lt;NameExpr&gt; throws_ = visit(_n.getThrows(), _arg);
     *         BlockStmt block = cloneNodes(_n.getBody(), _arg);
     *         Comment comment = cloneNodes(_n.getComment(), _arg);
     * 
     *         MethodDeclaration r = new MethodDeclaration(
     *                 _n.getBeginLine(), _n.getBeginColumn(), _n.getEndLine(), _n.getEndColumn(),
     *                  _n.getModifiers(), annotations, typeParameters, type_, _n.getName(), parameters, _n.getArrayCount(), throws_, block
     * </code></pre>
     * Other newly covered ranges to check: 1042-1049
     */
    @Test(timeout = 4000)
    public void visitCatchClause_withSelfContext_returnsEqualButNotSame() throws Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        CatchClause catchClause = new CatchClause();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(catchClause, ((Object) (catchClause)));
        assertTrue(cloned.equals(((Object) (catchClause))));
        assertNotSame(cloned, catchClause);
    }

    /**
     * This test added target-class coverage 2.37% for com.github.javaparser.ast.visitor.CloneVisitor (21/886 lines).
     * Delta details: +38 methods, +31 branches, +632 instructions.
     * Full version of the covered block is here: <a href="https://github.com/javaparser/javaparser/blob/ef5de98c0681b6ecc24be128b9aed6761bbbf6a0/javaparser-symbol-solver-testing/src/test/test_sourcecode/javaparser_src/proper_source/com/github/javaparser/ast/visitor/CloneVisitor.java#L463-L470">CloneVisitor.java (lines 463-470)</a>
     * Covered Lines:
     * <pre><code>
     *         if (_n.getInitializer() != null) {// ArrayCreationExpr has two mutually
     *             // exclusive constructors
     *             r.setInitializer(cloneNodes(_n.getInitializer(), _arg));
     *         }
     *         List&lt;List&lt;AnnotationExpr&gt;&gt; arraysAnnotations = _n.getArraysAnnotations();
     *         List&lt;List&lt;AnnotationExpr&gt;&gt; _arraysAnnotations = null;
     *         if(arraysAnnotations != null){
     *             _arraysAnnotations = new LinkedList&lt;List&lt;AnnotationExpr&gt;&gt;();
     * </code></pre>
     * Other newly covered ranges to check: 560-565;644-650
     */
    @Test(timeout = 4000)
    public void visitAssignExpr_withSelfContext_returnsEqualButNotSame() throws Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        AssignExpr assign = new AssignExpr();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(assign, ((Object) (assign)));
        assertNotSame(cloned, assign);
        assertTrue(cloned.equals(((Object) (assign))));
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
    @Test(timeout = 4000)
    public void visitForStmt_withBlockCommentListContext_returnsEqualButNotSame() throws Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        ForStmt forStmt = new ForStmt();
        BlockComment[] commentArray = new BlockComment[0];
        NodeList<BlockComment> comments = new NodeList<BlockComment>(commentArray);
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(forStmt, ((Object) (comments)));
        assertNotSame(cloned, forStmt);
        assertTrue(cloned.equals(((Object) (forStmt))));
    }

    /**
     * This test added target-class coverage 2.37% for com.github.javaparser.ast.visitor.CloneVisitor (21/886 lines).
     * Delta details: +35 methods, +42 branches, +597 instructions.
     * Full version of the covered block is here: <a href="https://github.com/javaparser/javaparser/blob/ef5de98c0681b6ecc24be128b9aed6761bbbf6a0/javaparser-symbol-solver-testing/src/test/test_sourcecode/javaparser_src/proper_source/com/github/javaparser/ast/visitor/CloneVisitor.java#L1316-L1326">CloneVisitor.java (lines 1316-1326)</a>
     * Covered Lines:
     * <pre><code>
     * </code></pre>
     * Other newly covered ranges to check: 1254-1259;1335-1338
     */
    @Test(timeout = 4000)
    public void visitRecordDeclaration_withModifiersContext_returnsEqual() throws Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        Modifier[] modifiersArr = new Modifier[3];
        Modifier volatileModifier = Modifier.volatileModifier();
        modifiersArr[0] = volatileModifier;
        modifiersArr[1] = volatileModifier;
        modifiersArr[2] = modifiersArr[0];
        NodeList<Modifier> modifiers = new NodeList<Modifier>(modifiersArr);
        RecordDeclaration record = new RecordDeclaration(modifiers, "RECEIVER_PARAMETER");
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(record, ((Object) (modifiers)));
        assertTrue(cloned.equals(((Object) (record))));
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
    @Test(timeout = 4000)
    public void visitForEachStmt_withObject_returnsClonedNode() throws Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        ForEachStmt forEach = new ForEachStmt();
        Object context = new Object();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(forEach, context);
        assertNotSame(cloned, forEach);
    }
}
