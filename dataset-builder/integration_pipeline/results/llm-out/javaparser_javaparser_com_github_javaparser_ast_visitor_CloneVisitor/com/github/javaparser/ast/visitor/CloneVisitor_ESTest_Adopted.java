package com.github.javaparser.ast.visitor;
import CloneVisitor_ESTest_scaffolding;
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
public class CloneVisitor_ESTest_Adopted extends CloneVisitor_ESTest_scaffolding {
    @Test(timeout = 4000)
    public void visitWildcardType_withBlockComment_returnsEqualButNotSame() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        TypeParameter bound = new TypeParameter("]0Cjw}+`@oK`WO2B");
        NodeList<AnnotationExpr> annotations = new NodeList<AnnotationExpr>();
        WildcardType wildcard = new WildcardType(bound, bound, annotations);
        BlockComment contextComment = new BlockComment();
        Visitable cloned = visitor.visit(wildcard, ((Object) (contextComment)));
        assertTrue(cloned.equals(((Object) (wildcard))));
        assertNotSame(cloned, wildcard);
    }

    @Test(timeout = 4000)
    public void visitVarType_withObject_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        VarType varType = new VarType();
        Object context = new Object();
        Visitable cloned = visitor.visit(varType, context);
        assertNotSame(cloned, varType);
    }

    @Test(timeout = 4000)
    public void visitUnionType_withObject_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        UnionType unionType = new UnionType();
        Object context = new Object();
        Visitable cloned = visitor.visit(unionType, context);
        assertNotSame(cloned, unionType);
    }

    @Test(timeout = 4000)
    public void visitPrimitiveType_withObject_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        Object context = new Object();
        PrimitiveType shortType = PrimitiveType.shortType();
        Visitable cloned = visitor.visit(shortType, context);
        assertNotSame(cloned, shortType);
    }

    @Test(timeout = 4000)
    public void visitIntersectionType_withObject_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        NodeList<ReferenceType> elements = new NodeList<ReferenceType>();
        IntersectionType intersection = new IntersectionType(elements);
        Object context = new Object();
        Visitable cloned = visitor.visit(intersection, context);
        assertNotSame(cloned, intersection);
    }

    @Test(timeout = 4000)
    public void visitYieldStmt_withBlockComment_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        YieldStmt yieldStmt = new YieldStmt();
        JavaToken invalidToken = JavaToken.INVALID;
        TokenRange invalidRange = new TokenRange(invalidToken, invalidToken);
        BlockComment contextComment = new BlockComment(invalidRange, "Corresponding declaration not available for unsolved symbol.");
        Visitable cloned = visitor.visit(yieldStmt, ((Object) (contextComment)));
        assertNotSame(cloned, yieldStmt);
    }

    @Test(timeout = 4000)
    public void visitTryStmt_withObject_returnsEqualButNotSame() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        TryStmt tryStmt = new TryStmt();
        Object context = new Object();
        Visitable cloned = visitor.visit(tryStmt, context);
        assertTrue(cloned.equals(((Object) (tryStmt))));
        assertNotSame(cloned, tryStmt);
    }

    @Test(timeout = 4000)
    public void visitThrowStmt_withSelfContext_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        ThrowStmt throwStmt = new ThrowStmt();
        Visitable cloned = visitor.visit(throwStmt, ((Object) (throwStmt)));
        assertNotSame(cloned, throwStmt);
    }

    @Test(timeout = 4000)
    public void visitSynchronizedStmt_withBlockComment_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        BlockComment contextComment = new BlockComment();
        SynchronizedStmt syncStmt = new SynchronizedStmt();
        Visitable cloned = visitor.visit(syncStmt, ((Object) (contextComment)));
        assertNotSame(cloned, syncStmt);
    }

    @Test(timeout = 4000)
    public void visitSwitchEntry_withExpressionsAndStatements_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        JavaToken invalid = JavaToken.INVALID;
        TokenRange range = new TokenRange(invalid, invalid);
        NodeList<Expression> labels = new NodeList<Expression>();
        SwitchEntry.Type entryType = SwitchEntry.Type.STATEMENT_GROUP;
        LinkedList<Statement> statementList = new LinkedList<Statement>();
        NodeList<Statement> statements = NodeList.nodeList(statementList);
        SwitchEntry entry = new SwitchEntry(range, labels, entryType, statements);
        Visitable cloned = visitor.visit(entry, ((Object) (labels)));
        assertNotSame(cloned, entry);
    }

    @Test(timeout = 4000)
    public void visitReturnStmt_withSelfContext_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        ReturnStmt returnStmt = new ReturnStmt();
        Visitable cloned = visitor.visit(returnStmt, ((Object) (returnStmt)));
        assertNotSame(cloned, returnStmt);
    }

    @Test(timeout = 4000)
    public void visitLabeledStmt_withModifiersContext_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        Modifier[] modifiersArray = new Modifier[3];
        NodeList<Modifier> modifiers = new NodeList<Modifier>(modifiersArray);
        LabeledStmt labeledStmt = new LabeledStmt();
        Visitable cloned = visitor.visit(labeledStmt, ((Object) (modifiers)));
        assertNotSame(cloned, labeledStmt);
    }

    @Test(timeout = 4000)
    public void visitIfStmt_withoutElse_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        IfStmt originalIf = new IfStmt();
        Object context = new Object();
        IfStmt clonedIf = ((IfStmt) (visitor.visit(originalIf, context)));
        assertFalse(clonedIf.hasElseBranch());
        assertNotSame(clonedIf, originalIf);
    }

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
    public void visitExpressionStmt_withObject_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        ExpressionStmt exprStmt = new ExpressionStmt();
        Object context = new Object();
        Visitable cloned = visitor.visit(exprStmt, context);
        assertNotSame(cloned, exprStmt);
    }

    @Test(timeout = 4000)
    public void visitExplicitConstructorInvocation_withBlockComment_returnsEqualButNotSame() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        BlockComment contextComment = new BlockComment();
        ExplicitConstructorInvocationStmt invocation = new ExplicitConstructorInvocationStmt();
        Visitable cloned = visitor.visit(invocation, ((Object) (contextComment)));
        assertTrue(cloned.equals(((Object) (invocation))));
        assertNotSame(cloned, invocation);
    }

    @Test(timeout = 4000)
    public void visitBreakStmt_withObject_returnsEqualButNotSame() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        Object context = new Object();
        BreakStmt breakStmt = new BreakStmt("&");
        Visitable cloned = visitor.visit(breakStmt, context);
        assertNotSame(cloned, breakStmt);
        assertTrue(cloned.equals(((Object) (breakStmt))));
    }

    @Test(timeout = 4000)
    public void visitBlockStmt_fromRecordInitializer_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        RecordDeclaration record = new RecordDeclaration();
        BlockStmt staticInitializer = record.addStaticInitializer();
        Visitable cloned = visitor.visit(staticInitializer, ((Object) (record)));
        assertNotSame(cloned, staticInitializer);
    }

    @Test(timeout = 4000)
    public void visitModuleUsesDirective_withNullContext_returnsEqualButNotSame() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        JavaToken token = new JavaToken(0);
        TokenRange range = new TokenRange(token, token);
        Name serviceName = new Name();
        ModuleUsesDirective usesDirective = new ModuleUsesDirective(range, serviceName);
        Visitable cloned = visitor.visit(usesDirective, ((Object) (null)));
        assertTrue(cloned.equals(((Object) (usesDirective))));
        assertNotSame(cloned, usesDirective);
    }

    @Test(timeout = 4000)
    public void visitModuleRequiresDirective_withNullContext_returnsEqualButNotSame() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
            ModuleRequiresDirective requires = new ModuleRequiresDirective();
        Visitable cloned = visitor.visit(requires, ((Object) (null)));
        assertTrue(cloned.equals(((Object) (requires))));
        assertNotSame(cloned, requires);
    }

    @Test(timeout = 4000)
    public void visitVariableDeclarationExpr_withOwnListContext_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        VariableDeclarator[] vars = new VariableDeclarator[0];
        NodeList<VariableDeclarator> declarators = new NodeList<VariableDeclarator>(vars);
        VariableDeclarationExpr varExpr = new VariableDeclarationExpr(declarators);
        Visitable cloned = visitor.visit(varExpr, ((Object) (declarators)));
        assertNotSame(cloned, varExpr);
    }

    @Test(timeout = 4000)
    public void visitThisExpr_withObject_returnsEqualButNotSame() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        Name qualifier = new Name();
        ThisExpr thisExpr = new ThisExpr(qualifier);
        Object context = new Object();
        Visitable cloned = visitor.visit(thisExpr, context);
        assertNotSame(cloned, thisExpr);
        assertTrue(cloned.equals(((Object) (thisExpr))));
    }

    @Test(timeout = 4000)
    public void visitTextBlockLiteralExpr_withBlockComment_returnsClonedNode() throws Throwable {
        BlockComment contextComment = new BlockComment();
        CloneVisitor visitor = new CloneVisitor();
        TextBlockLiteralExpr textBlock = new TextBlockLiteralExpr();
        Visitable cloned = visitor.visit(textBlock, ((Object) (contextComment)));
        assertNotSame(cloned, textBlock);
    }

    @Test(timeout = 4000)
    public void visitSimpleName_withObject_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        Object context = new Object();
        SimpleName simpleName = new SimpleName();
        Visitable cloned = visitor.visit(simpleName, context);
        assertNotSame(cloned, simpleName);
    }

    @Test(timeout = 4000)
    public void visitNameExpr_withNullContext_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        SimpleName identifier = new SimpleName();
        NameExpr nameExpr = new NameExpr(identifier);
        Visitable cloned = visitor.visit(nameExpr, ((Object) (null)));
        assertNotSame(cloned, nameExpr);
    }

    @Test(timeout = 4000)
    public void visitMatchAllPatternExpr_withModifiers_returnsEqualButNotSame() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        BlockComment contextComment = new BlockComment();
        Modifier[] modifierKeywords = new Modifier.Keyword[4];
        Modifier.Keyword volatileKeyword = Modifier.Keyword.VOLATILE;
        modifierKeywords[0] = volatileKeyword;
        modifierKeywords[1] = modifierKeywords[0];
        modifierKeywords[2] = modifierKeywords[1];
        modifierKeywords[3] = modifierKeywords[2];
        NodeList<Modifier> modifiers = Modifier.createModifierList(modifierKeywords);
        MatchAllPatternExpr pattern = new MatchAllPatternExpr(modifiers);
        Visitable cloned = visitor.visit(pattern, ((Object) (contextComment)));
        assertNotSame(cloned, pattern);
        assertTrue(cloned.equals(((Object) (pattern))));
    }

    @Test(timeout = 4000)
    public void visitLongLiteralExpr_withNullContext_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        LongLiteralExpr literal = new LongLiteralExpr("\"class\"");
        Visitable cloned = visitor.visit(literal, ((Object) (null)));
        assertNotSame(cloned, literal);
    }

    @Test(timeout = 4000)
    public void visitInstanceOfExpr_withBlockComment_returnsEqualButNotSame() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        InstanceOfExpr instanceOfExpr = new InstanceOfExpr();
        BlockComment contextComment = new BlockComment();
        Visitable cloned = visitor.visit(instanceOfExpr, ((Object) (contextComment)));
        assertTrue(cloned.equals(((Object) (instanceOfExpr))));
        assertNotSame(cloned, instanceOfExpr);
    }

    @Test(timeout = 4000)
    public void visitFieldAccessExpr_withObject_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        Object context = new Object();
        FieldAccessExpr fieldAccess = new FieldAccessExpr();
        Visitable cloned = visitor.visit(fieldAccess, context);
        assertNotSame(cloned, fieldAccess);
    }

    @Test(timeout = 4000)
    public void visitEnclosedExpr_withBlockComment_returnsClonedNode() throws Throwable {
        BlockComment contextComment = new BlockComment();
        CloneVisitor visitor = new CloneVisitor();
        EnclosedExpr enclosed = new EnclosedExpr();
        Visitable cloned = visitor.visit(enclosed, ((Object) (contextComment)));
        assertNotSame(cloned, enclosed);
    }

    @Test(timeout = 4000)
    public void visitConditionalExpr_withObject_returnsEqualButNotSame() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        ConditionalExpr conditional = new ConditionalExpr();
        Object context = new Object();
        Visitable cloned = visitor.visit(conditional, context);
        assertNotSame(cloned, conditional);
        assertTrue(cloned.equals(((Object) (conditional))));
    }

    @Test(timeout = 4000)
    public void visitClassExpr_withBlockCommentListContext_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        TypeParameter typeParam = new TypeParameter("4)oF?x7_I'eKf+j`G<~");
        ClassExpr classExpr = new ClassExpr(typeParam);
        BlockComment[] blockComments = new BlockComment[4];
        NodeList<BlockComment> contextComments = new NodeList<BlockComment>(blockComments);
        Visitable cloned = visitor.visit(classExpr, ((Object) (contextComments)));
        assertNotSame(cloned, classExpr);
    }

    @Test(timeout = 4000)
    public void visitCharLiteralExpr_withBlockCommentListContext_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        CharLiteralExpr charLiteral = CharLiteralExpr.escape("");
        NodeList<BlockComment> comments = new NodeList<BlockComment>();
        Visitable cloned = visitor.visit(charLiteral, ((Object) (comments)));
        assertNotSame(cloned, charLiteral);
    }

    @Test(timeout = 4000)
    public void visitBooleanLiteralExpr_withBlockComment_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        BooleanLiteralExpr literal = new BooleanLiteralExpr(true);
        BlockComment contextComment = new BlockComment("JAVA_1_0");
        Visitable cloned = visitor.visit(literal, ((Object) (contextComment)));
        assertNotSame(cloned, literal);
    }

    @Test(timeout = 4000)
    public void visitBinaryExpr_withObject_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        BinaryExpr binaryExpr = new BinaryExpr();
        Object context = new Object();
        Visitable cloned = visitor.visit(binaryExpr, context);
        assertNotSame(cloned, binaryExpr);
    }

    @Test(timeout = 4000)
    public void visitArrayCreationExpr_withObject_returnsEqualButNotSame() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        UnknownType unknown = new UnknownType();
        Object context = new Object();
        NodeList<ArrayCreationLevel> levels = new NodeList<ArrayCreationLevel>();
        ArrayInitializerExpr initializer = new ArrayInitializerExpr();
        ArrayCreationExpr arrayCreation = new ArrayCreationExpr(unknown, levels, initializer);
        Visitable cloned = visitor.visit(arrayCreation, context);
        assertTrue(cloned.equals(((Object) (arrayCreation))));
        assertNotSame(cloned, arrayCreation);
    }

    @Test(timeout = 4000)
    public void visitLineComment_withObject_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        TokenRange invalid = TokenRange.INVALID;
        LineComment lineComment = new LineComment(invalid, "w*gWr 2b\u0007:X");
        Object context = new Object();
        Visitable cloned = visitor.visit(lineComment, context);
        assertNotSame(cloned, lineComment);
    }

    @Test(timeout = 4000)
    public void visitBlockComment_withBlockCommentListContext_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        BlockComment blockComment = new BlockComment("");
        BlockComment[] blockComments = new BlockComment[8];
        NodeList<BlockComment> context = NodeList.nodeList(blockComments);
        Visitable cloned = visitor.visit(blockComment, ((Object) (context)));
        assertNotSame(cloned, blockComment);
    }

    @Test(timeout = 4000)
    public void visitVariableDeclarator_withObject_returnsEqualButNotSame() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        VariableDeclarator variable = new VariableDeclarator();
        Object context = new Object();
        Visitable cloned = visitor.visit(variable, context);
        assertTrue(cloned.equals(((Object) (variable))));
        assertNotSame(cloned, variable);
    }

    @Test(timeout = 4000)
    public void visitReceiverParameter_withWhileStmtListContext_returnsEqualButNotSame() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        TokenRange invalid = TokenRange.INVALID;
        UnknownType receiverType = new UnknownType(invalid);
        ReceiverParameter receiver = new ReceiverParameter(receiverType, "\"<<\"");
        NodeList<WhileStmt> context = new NodeList<WhileStmt>();
        Visitable cloned = visitor.visit(receiver, ((Object) (context)));
        assertNotSame(cloned, receiver);
        assertTrue(cloned.equals(((Object) (receiver))));
    }

    @Test(timeout = 4000)
    public void visitEnumDeclaration_withBlockComment_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        BlockComment contextComment = new BlockComment();
        EnumDeclaration enumDecl = new EnumDeclaration();
        Visitable cloned = visitor.visit(enumDecl, ((Object) (contextComment)));
        assertNotSame(cloned, enumDecl);
    }

    @Test(timeout = 4000)
    public void visitCompactConstructorDeclaration_withNullContext_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        CompactConstructorDeclaration compactCtor = new CompactConstructorDeclaration();
        Visitable cloned = visitor.visit(compactCtor, ((Object) (null)));
        assertNotSame(cloned, compactCtor);
    }

    @Test(timeout = 4000)
    public void visitAnnotationMemberDeclaration_withNullContext_returnsEqualButNotSame() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        AnnotationMemberDeclaration member = new AnnotationMemberDeclaration();
        Visitable cloned = visitor.visit(member, ((Object) (null)));
        assertTrue(cloned.equals(((Object) (member))));
        assertNotSame(cloned, member);
    }

    @Test(timeout = 4000)
    public void visitAnnotationDeclaration_withObject_returnsEqualButNotSame() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        Modifier[] modifiersArr = new Modifier[6];
        Modifier modifier = new Modifier();
        modifiersArr[0] = modifier;
        modifiersArr[1] = modifiersArr[0];
        modifiersArr[2] = modifiersArr[1];
        modifiersArr[3] = modifiersArr[1];
        modifiersArr[4] = modifier;
        modifiersArr[5] = modifiersArr[2];
        NodeList<Modifier> modifiers = NodeList.nodeList(modifiersArr);
        AnnotationDeclaration annotationDecl = new AnnotationDeclaration(modifiers, "g/hJ7smB#{,MTz%");
        Object context = new Object();
        Visitable cloned = visitor.visit(annotationDecl, context);
        assertTrue(cloned.equals(((Object) (annotationDecl))));
        assertNotSame(cloned, annotationDecl);
    }

    @Test(timeout = 4000)
    public void visitImportDeclaration_withBlockComment_returnsEqualButNotSameNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        BlockComment contextComment = new BlockComment();
        JavaToken token = new JavaToken(1, "");
        TokenRange range = new TokenRange(token, token);
        Name name = new Name();
        ImportDeclaration importDecl = new ImportDeclaration(range, name, false, false, false);
        Node cloned = visitor.visit(importDecl, ((Object) (contextComment)));
        assertNotSame(cloned, importDecl);
        assertTrue(cloned.equals(((Object) (importDecl))));
    }

    @Test(timeout = 4000)
    public void cloneNode_withEmptyOptional_returnsNull() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        Modifier[] modifiersArr = new Modifier[2];
        Modifier sync = Modifier.synchronizedModifier();
        modifiersArr[0] = sync;
        modifiersArr[1] = sync;
        NodeList<Modifier> modifiers = NodeList.nodeList(modifiersArr);
        RecordDeclaration record = new RecordDeclaration(modifiers, "/e>G`0p");
        Optional<MethodDeclaration> maybeMethod = record.toMethodDeclaration();
        Object context = new Object();
        MethodDeclaration cloned = visitor.cloneNode(maybeMethod, context);
        assertNull(cloned);
    }

    @Test(timeout = 4000)
    public void cloneNode_withPresentOptional_returnsNewInstance() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        YieldStmt yieldStmt = new YieldStmt();
        Optional<YieldStmt> maybeYield = yieldStmt.toYieldStmt();
        YieldStmt cloned = visitor.cloneNode(maybeYield, yieldStmt);
        assertNotSame(cloned, yieldStmt);
        assertNotNull(cloned);
    }

    @Test(timeout = 4000)
    public void clone_onContinueStmt_returnsDeepCopy() throws Throwable {
        ContinueStmt original = new ContinueStmt("%s is not MethodCallExpr, it is %s");
        ContinueStmt cloned = original.clone();
        assertNotSame(cloned, original);
        assertTrue(cloned.equals(((Object) (original))));
    }

    @Test(timeout = 4000)
    public void visitNodeList_withBlockComment_returnsListOfSameSize() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        NodeList<BlockComment> comments = new NodeList<BlockComment>();
        BlockComment comment = new BlockComment();
        NodeList<BlockComment> afterAdd = comments.addLast(comment);
        NodeList visited = ((NodeList) (visitor.visit(((NodeList) (afterAdd)), ((Object) (comment)))));
        assertEquals(1, visited.size());
    }

    @Test(timeout = 4000)
    public void visitPackageDeclaration_withSelfContext_returnsEqualButNotSame() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        PackageDeclaration pkg = new PackageDeclaration();
        Visitable cloned = visitor.visit(pkg, ((Object) (pkg)));
        assertNotSame(cloned, pkg);
        assertTrue(cloned.equals(((Object) (pkg))));
    }

    @Test(timeout = 4000)
    public void visitArrayCreationLevel_withBlockComment_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        BlockComment contextComment = new BlockComment();
        NodeList<AnnotationExpr> annotations = new NodeList<AnnotationExpr>();
        ArrayCreationLevel level = new ArrayCreationLevel(((Expression) (null)), annotations);
        Visitable cloned = visitor.visit(level, ((Object) (contextComment)));
        assertNotSame(cloned, level);
    }

    @Test(timeout = 4000)
    public void visitIntegerLiteralExpr_withBlockComment_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        BlockComment contextComment = new BlockComment();
        IntegerLiteralExpr literal = new IntegerLiteralExpr();
        Visitable cloned = visitor.visit(literal, ((Object) (contextComment)));
        assertNotSame(cloned, literal);
    }

    @Test(timeout = 4000)
    public void visitClassOrInterfaceDeclaration_withLocalRecordListContext_returnsNotConstructor() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        ClassOrInterfaceDeclaration typeDecl = new ClassOrInterfaceDeclaration();
        LocalRecordDeclarationStmt[] localRecords = new LocalRecordDeclarationStmt[0];
        NodeList<LocalRecordDeclarationStmt> context = NodeList.nodeList(localRecords);
        ClassOrInterfaceDeclaration cloned = ((ClassOrInterfaceDeclaration) (visitor.visit(typeDecl, ((Object) (context)))));
        assertFalse(cloned.isConstructorDeclaration());
    }

    @Test(timeout = 4000)
    public void visitArrayInitializerExpr_withBlockComment_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        JavaToken invalid = JavaToken.INVALID;
        TokenRange range = new TokenRange(invalid, invalid);
        BlockComment contextComment = new BlockComment();
        NodeList<Expression> values = new NodeList<Expression>();
        ArrayInitializerExpr initializer = new ArrayInitializerExpr(range, values);
        Visitable cloned = visitor.visit(initializer, ((Object) (contextComment)));
        assertNotSame(cloned, initializer);
    }

    @Test(timeout = 4000)
    public void visitModifier_withSelfContext_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        Modifier abstractModifier = Modifier.abstractModifier();
        Visitable cloned = visitor.visit(abstractModifier, ((Object) (abstractModifier)));
        assertNotSame(cloned, abstractModifier);
    }

    @Test(timeout = 4000)
    public void visitBoxedPrimitiveType_withBlockComment_returnsEqualButNotSame() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        PrimitiveType primitiveShort = PrimitiveType.shortType();
        ClassOrInterfaceType boxed = primitiveShort.toBoxedType();
        BlockComment contextComment = new BlockComment("");
        Visitable cloned = visitor.visit(boxed, ((Object) (contextComment)));
        assertTrue(cloned.equals(((Object) (boxed))));
        assertNotSame(cloned, boxed);
    }

    @Test(timeout = 4000)
    public void visitName_withBlockComment_returnsUnqualifiedNewInstance() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        BlockComment contextComment = new BlockComment("");
        Name original = new Name("u(il_%]h");
        Name cloned = ((Name) (visitor.visit(original, ((Object) (contextComment)))));
        assertNotSame(cloned, original);
        assertFalse(cloned.hasQualifier());
    }

    @Test(timeout = 4000)
    public void visitStringLiteralExpr_withNullContext_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        JavaToken token = new JavaToken(1323, ">Pn^p");
        TokenRange range = new TokenRange(token, token);
        StringLiteralExpr literal = new StringLiteralExpr(range, "");
        Visitable cloned = visitor.visit(literal, ((Object) (null)));
        assertNotSame(cloned, literal);
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
    public void visitTypePatternExpr_withSelfContext_returnsEqualButNotSame() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        TypePatternExpr typePattern = new TypePatternExpr();
        Visitable cloned = visitor.visit(typePattern, ((Object) (typePattern)));
        assertTrue(cloned.equals(((Object) (typePattern))));
        assertNotSame(cloned, typePattern);
    }

    @Test(timeout = 4000)
    public void visitModuleDeclaration_withSelfContext_returnsEqualButNotSame() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        ModuleDeclaration moduleDecl = new ModuleDeclaration();
        Visitable cloned = visitor.visit(moduleDecl, ((Object) (moduleDecl)));
        assertTrue(cloned.equals(((Object) (moduleDecl))));
        assertNotSame(cloned, moduleDecl);
    }

    @Test(timeout = 4000)
    public void visitObjectCreationExpr_withSelfContext_returnsEqualButNotSame() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        ObjectCreationExpr creationExpr = new ObjectCreationExpr();
        Visitable cloned = visitor.visit(creationExpr, ((Object) (creationExpr)));
        assertNotSame(cloned, creationExpr);
        assertTrue(cloned.equals(((Object) (creationExpr))));
    }

    @Test(timeout = 4000)
    public void visitSingleMemberAnnotationExpr_withSelfContext_returnsEqualButNotSame() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        SingleMemberAnnotationExpr annotation = new SingleMemberAnnotationExpr();
        Visitable cloned = visitor.visit(annotation, ((Object) (annotation)));
        assertTrue(cloned.equals(((Object) (annotation))));
        assertNotSame(cloned, annotation);
    }

    @Test(timeout = 4000)
    public void visitTraditionalJavadocComment_withSelfContext_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        TraditionalJavadocComment javadoc = new TraditionalJavadocComment();
        Visitable cloned = visitor.visit(javadoc, ((Object) (javadoc)));
        assertNotSame(cloned, javadoc);
    }

    @Test(timeout = 4000)
    public void visitEmptyStmt_withSelfContext_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        EmptyStmt emptyStmt = new EmptyStmt();
        Visitable cloned = visitor.visit(emptyStmt, ((Object) (emptyStmt)));
        assertNotSame(cloned, emptyStmt);
    }

    @Test(timeout = 4000)
    public void visitMarkerAnnotationExpr_withSelfContext_returnsEqualButNotSame() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        MarkerAnnotationExpr marker = new MarkerAnnotationExpr();
        Visitable cloned = visitor.visit(marker, ((Object) (marker)));
        assertTrue(cloned.equals(((Object) (marker))));
        assertNotSame(cloned, marker);
    }

    @Test(timeout = 4000)
    public void visitNormalAnnotationExpr_withSelfContext_returnsEqualButNotSame() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        NormalAnnotationExpr annotation = new NormalAnnotationExpr();
        Visitable cloned = visitor.visit(annotation, ((Object) (annotation)));
        assertNotSame(cloned, annotation);
        assertTrue(cloned.equals(((Object) (annotation))));
    }

    @Test(timeout = 4000)
    public void visitUnaryExpr_withSelfContext_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        UnaryExpr unary = new UnaryExpr();
        Visitable cloned = visitor.visit(unary, ((Object) (unary)));
        assertNotSame(cloned, unary);
    }

    @Test(timeout = 4000)
    public void visitVoidType_withSelfContext_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        VoidType voidType = new VoidType();
        Visitable cloned = visitor.visit(voidType, ((Object) (voidType)));
        assertNotSame(cloned, voidType);
    }

    @Test(timeout = 4000)
    public void visitSwitchExpr_withSelfContext_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        SwitchExpr switchExpr = new SwitchExpr();
        Visitable cloned = visitor.visit(switchExpr, ((Object) (switchExpr)));
        assertNotSame(cloned, switchExpr);
    }

    @Test(timeout = 4000)
    public void visitSwitchStmt_withSelfContext_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        SwitchStmt switchStmt = new SwitchStmt();
        Visitable cloned = visitor.visit(switchStmt, ((Object) (switchStmt)));
        assertNotSame(cloned, switchStmt);
    }

    @Test(timeout = 4000)
    public void visitSuperExpr_withSelfContext_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        SuperExpr superExpr = new SuperExpr();
        Visitable cloned = visitor.visit(superExpr, ((Object) (superExpr)));
        assertNotSame(cloned, superExpr);
    }

    @Test(timeout = 4000)
    public void visitCompilationUnit_withSelfContext_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        CompilationUnit unit = new CompilationUnit();
        Visitable cloned = visitor.visit(unit, ((Object) (unit)));
        assertNotSame(cloned, unit);
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
    public void visitNullLiteralExpr_withSelfContext_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        NullLiteralExpr nullLiteral = new NullLiteralExpr();
        Visitable cloned = visitor.visit(nullLiteral, ((Object) (nullLiteral)));
        assertNotSame(cloned, nullLiteral);
    }

    @Test(timeout = 4000)
    public void visitMemberValuePair_withSelfContext_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        MemberValuePair pair = new MemberValuePair();
        Visitable cloned = visitor.visit(pair, ((Object) (pair)));
        assertNotSame(cloned, pair);
    }

    @Test(timeout = 4000)
    public void visitAssertStmt_withSelfContext_returnsEqualButNotSame() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        AssertStmt assertStmt = new AssertStmt();
        Visitable cloned = visitor.visit(assertStmt, ((Object) (assertStmt)));
        assertNotSame(cloned, assertStmt);
        assertTrue(cloned.equals(((Object) (assertStmt))));
    }

    @Test(timeout = 4000)
    public void visitUnparsableStmt_withSelfContext_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        UnparsableStmt unparsable = new UnparsableStmt();
        Visitable cloned = visitor.visit(unparsable, ((Object) (unparsable)));
        assertNotSame(cloned, unparsable);
    }

    @Test(timeout = 4000)
    public void visitEnumConstantDeclaration_withSelfContext_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        EnumConstantDeclaration enumConstant = new EnumConstantDeclaration();
        Visitable cloned = visitor.visit(enumConstant, ((Object) (enumConstant)));
        assertNotSame(cloned, enumConstant);
    }

    @Test(timeout = 4000)
    public void cloneNode_onBlockComment_returnsNewInstance() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        BlockComment original = new BlockComment();
        BlockComment cloned = visitor.cloneNode(original, original);
        assertNotSame(cloned, original);
    }

    @Test(timeout = 4000)
    public void visitModuleProvidesDirective_withSelfContext_returnsEqualButNotSame() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        ModuleProvidesDirective provides = new ModuleProvidesDirective();
        Visitable cloned = visitor.visit(provides, ((Object) (provides)));
        assertTrue(cloned.equals(((Object) (provides))));
        assertNotSame(cloned, provides);
    }

    @Test(timeout = 4000)
    public void visitTypeExpr_withSelfContext_returnsEqualButNotSame() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        TypeExpr typeExpr = new TypeExpr();
        Visitable cloned = visitor.visit(typeExpr, ((Object) (typeExpr)));
        assertTrue(cloned.equals(((Object) (typeExpr))));
        assertNotSame(cloned, typeExpr);
    }

    @Test(timeout = 4000)
    public void visitContinueStmt_withSelfContext_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        ContinueStmt cont = new ContinueStmt();
        Visitable cloned = visitor.visit(cont, ((Object) (cont)));
        assertNotSame(cloned, cont);
    }

    @Test(timeout = 4000)
    public void visitMethodDeclaration_withSelfContext_returnsEqual() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        MethodDeclaration method = new MethodDeclaration();
        Visitable cloned = visitor.visit(method, ((Object) (method)));
        assertTrue(cloned.equals(((Object) (method))));
    }

    @Test(timeout = 4000)
    public void visitLocalRecordDeclarationStmt_withSelfContext_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        LocalRecordDeclarationStmt localRecord = new LocalRecordDeclarationStmt();
        Visitable cloned = visitor.visit(localRecord, ((Object) (localRecord)));
        assertNotSame(cloned, localRecord);
    }

    @Test(timeout = 4000)
    public void visitArrayAccessExpr_withSelfContext_returnsEqualButNotSame() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        ArrayAccessExpr arrayAccess = new ArrayAccessExpr();
        Visitable cloned = visitor.visit(arrayAccess, ((Object) (arrayAccess)));
        assertTrue(cloned.equals(((Object) (arrayAccess))));
        assertNotSame(cloned, arrayAccess);
    }

    @Test(timeout = 4000)
    public void visitDoStmt_withSelfContext_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        DoStmt doStmt = new DoStmt();
        Visitable cloned = visitor.visit(doStmt, ((Object) (doStmt)));
        assertNotSame(cloned, doStmt);
    }

    @Test(timeout = 4000)
    public void visitUnknownType_withSelfContext_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        UnknownType unknown = new UnknownType();
        Visitable cloned = visitor.visit(unknown, ((Object) (unknown)));
        assertNotSame(cloned, unknown);
    }

    @Test(timeout = 4000)
    public void visitMethodReferenceExpr_withSelfContext_returnsEqualButNotSame() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        MethodReferenceExpr reference = new MethodReferenceExpr();
        Visitable cloned = visitor.visit(reference, ((Object) (reference)));
        assertTrue(cloned.equals(((Object) (reference))));
        assertNotSame(cloned, reference);
    }

    @Test(timeout = 4000)
    public void visitRecordPatternExpr_withSelfContext_returnsEqualButNotSame() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        RecordPatternExpr recordPattern = new RecordPatternExpr();
        Visitable cloned = visitor.visit(recordPattern, ((Object) (recordPattern)));
        assertTrue(cloned.equals(((Object) (recordPattern))));
        assertNotSame(cloned, recordPattern);
    }

    @Test(timeout = 4000)
    public void visitModuleOpensDirective_withSelfContext_returnsEqualButNotSame() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        ModuleOpensDirective opens = new ModuleOpensDirective();
        Visitable cloned = visitor.visit(opens, ((Object) (opens)));
        assertTrue(cloned.equals(((Object) (opens))));
        assertNotSame(cloned, opens);
    }

    @Test(timeout = 4000)
    public void visitAssignExpr_withSelfContext_returnsEqualButNotSame() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        AssignExpr assign = new AssignExpr();
        Visitable cloned = visitor.visit(assign, ((Object) (assign)));
        assertNotSame(cloned, assign);
        assertTrue(cloned.equals(((Object) (assign))));
    }

    @Test(timeout = 4000)
    public void visitLocalClassDeclarationStmt_withSelfContext_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        LocalClassDeclarationStmt localClass = new LocalClassDeclarationStmt();
        Visitable cloned = visitor.visit(localClass, ((Object) (localClass)));
        assertNotSame(cloned, localClass);
    }

    @Test(timeout = 4000)
    public void visitModuleExportsDirective_withSelfContext_returnsEqualButNotSame() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        ModuleExportsDirective exports = new ModuleExportsDirective();
        Visitable cloned = visitor.visit(exports, ((Object) (exports)));
        assertNotSame(cloned, exports);
        assertTrue(cloned.equals(((Object) (exports))));
    }

    @Test(timeout = 4000)
    public void visitInitializerDeclaration_withSelfContext_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        InitializerDeclaration initializer = new InitializerDeclaration();
        Visitable cloned = visitor.visit(initializer, ((Object) (initializer)));
        assertNotSame(cloned, initializer);
    }

    @Test(timeout = 4000)
    public void visitTypeParameter_withSelfContext_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        TypeParameter typeParam = new TypeParameter();
        Visitable cloned = visitor.visit(typeParam, ((Object) (typeParam)));
        assertNotSame(cloned, typeParam);
    }

    @Test(timeout = 4000)
    public void visitDoubleLiteralExpr_withSelfContext_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        DoubleLiteralExpr doubleLiteral = new DoubleLiteralExpr();
        Visitable cloned = visitor.visit(doubleLiteral, ((Object) (doubleLiteral)));
        assertNotSame(cloned, doubleLiteral);
    }

    @Test(timeout = 4000)
    public void visitConstructorDeclaration_withSelfContext_preservesNotEnum() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        ConstructorDeclaration ctor = new ConstructorDeclaration();
        ConstructorDeclaration cloned = ((ConstructorDeclaration) (visitor.visit(ctor, ((Object) (ctor)))));
        assertFalse(cloned.isEnumDeclaration());
    }

    @Test(timeout = 4000)
    public void visitMarkdownComment_withSelfContext_returnsClonedNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        MarkdownComment markdown = new MarkdownComment();
        Visitable cloned = visitor.visit(markdown, ((Object) (markdown)));
        assertNotSame(cloned, markdown);
    }

    @Test(timeout = 4000)
    public void cloneCompilationUnit_preservesJavadocOnMembers() throws Throwable {
        NodeList<BodyDeclaration<?>> bodyDeclarationList = new NodeList<BodyDeclaration<?>>();
        bodyDeclarationList.add(new AnnotationMemberDeclaration().setJavadocComment("javadoc"));
        bodyDeclarationList.add(new ConstructorDeclaration().setJavadocComment("javadoc"));
        bodyDeclarationList.add(new EnumConstantDeclaration().setJavadocComment("javadoc"));
        bodyDeclarationList.add(new FieldDeclaration().setJavadocComment("javadoc"));
        bodyDeclarationList.add(new InitializerDeclaration().setJavadocComment("javadoc"));
        bodyDeclarationList.add(new MethodDeclaration().setJavadocComment("javadoc"));

        NodeList<TypeDeclaration<?>> typeDeclarationList = new NodeList<TypeDeclaration<?>>();
        AnnotationDeclaration annotationDeclaration = new AnnotationDeclaration();
        annotationDeclaration.setName("nnotationDeclarationTest");
        typeDeclarationList.add(annotationDeclaration.setJavadocComment("javadoc"));

        ClassOrInterfaceDeclaration classOrInterfaceDeclaration2 = new ClassOrInterfaceDeclaration();
        classOrInterfaceDeclaration2.setName("emptyTypeDeclarationTest");
        typeDeclarationList.add(classOrInterfaceDeclaration2.setJavadocComment("javadoc"));

        EnumDeclaration enumDeclaration = new EnumDeclaration();
        enumDeclaration.setName("enumDeclarationTest");
        typeDeclarationList.add(enumDeclaration.setJavadocComment("javadoc"));

        ClassOrInterfaceDeclaration classOrInterfaceDeclaration = new ClassOrInterfaceDeclaration();
        classOrInterfaceDeclaration.setName("classOrInterfaceDeclarationTest");
        typeDeclarationList.add(classOrInterfaceDeclaration.setJavadocComment("javadoc"));

        ClassOrInterfaceDeclaration classOrInterfaceDeclaration1 = new ClassOrInterfaceDeclaration();
        classOrInterfaceDeclaration1.setName("emptyTypeDeclarationTest1");
        typeDeclarationList.add(classOrInterfaceDeclaration2.setMembers(bodyDeclarationList));

        CompilationUnit cu = new CompilationUnit();
        cu.setTypes(typeDeclarationList);
        CompilationUnit cuClone = ((CompilationUnit) (new CloneVisitor().visit(cu, ((Object) (null)))));

        for (TypeDeclaration<?> typeDeclaration : cuClone.getTypes()) {
            if (typeDeclaration.getMembers() == null) {
                assertEquals("javadoc", typeDeclaration.getComment().get().getContent());
            } else {
                for (BodyDeclaration<?> bodyDeclaration : typeDeclaration.getMembers()) {
                    assertEquals("javadoc", bodyDeclaration.getComment().get().getContent());
                }
            }
        }
    }

    @Test(timeout = 4000)
    public void cloneAnnotationOnWildcardTypeArgument_preservesToString() throws Throwable {
        Type type = StaticJavaParser.parseType("List<@C ? extends Object>").clone();
        assertEquals("List<@C ? extends Object>", type.toString());
    }
}