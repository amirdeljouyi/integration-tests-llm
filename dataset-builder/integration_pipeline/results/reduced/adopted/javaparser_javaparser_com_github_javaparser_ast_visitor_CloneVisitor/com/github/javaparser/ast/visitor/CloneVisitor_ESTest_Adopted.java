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
public class CloneVisitor_ESTest_Adopted {
    @org.junit.Test(timeout = 4000)
    public void visitWildcardType_withBlockComment_returnsEqualButNotSame() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.type.TypeParameter bound = new com.github.javaparser.ast.type.TypeParameter("]0Cjw}+`@oK`WO2B");
        com.github.javaparser.ast.NodeList<com.github.javaparser.ast.expr.AnnotationExpr> annotations = new com.github.javaparser.ast.NodeList<com.github.javaparser.ast.expr.AnnotationExpr>();
        com.github.javaparser.ast.type.WildcardType wildcard = new com.github.javaparser.ast.type.WildcardType(bound, bound, annotations);
        com.github.javaparser.ast.comments.BlockComment contextComment = new com.github.javaparser.ast.comments.BlockComment();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(wildcard, ((java.lang.Object) (contextComment)));
        org.junit.Assert.assertTrue(cloned.equals(((java.lang.Object) (wildcard))));
        org.junit.Assert.assertNotSame(cloned, wildcard);
    }

    @org.junit.Test(timeout = 4000)
    public void visitVarType_withObject_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.type.VarType varType = new com.github.javaparser.ast.type.VarType();
        java.lang.Object context = new java.lang.Object();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(varType, context);
        org.junit.Assert.assertNotSame(cloned, varType);
    }

    @org.junit.Test(timeout = 4000)
    public void visitUnionType_withObject_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.type.UnionType unionType = new com.github.javaparser.ast.type.UnionType();
        java.lang.Object context = new java.lang.Object();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(unionType, context);
        org.junit.Assert.assertNotSame(cloned, unionType);
    }

    @org.junit.Test(timeout = 4000)
    public void visitPrimitiveType_withObject_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        java.lang.Object context = new java.lang.Object();
        com.github.javaparser.ast.type.PrimitiveType shortType = com.github.javaparser.ast.type.PrimitiveType.shortType();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(shortType, context);
        org.junit.Assert.assertNotSame(cloned, shortType);
    }

    @org.junit.Test(timeout = 4000)
    public void visitIntersectionType_withObject_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.NodeList<com.github.javaparser.ast.type.ReferenceType> elements = new com.github.javaparser.ast.NodeList<com.github.javaparser.ast.type.ReferenceType>();
        com.github.javaparser.ast.type.IntersectionType intersection = new com.github.javaparser.ast.type.IntersectionType(elements);
        java.lang.Object context = new java.lang.Object();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(intersection, context);
        org.junit.Assert.assertNotSame(cloned, intersection);
    }

    @org.junit.Test(timeout = 4000)
    public void visitYieldStmt_withBlockComment_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.stmt.YieldStmt yieldStmt = new com.github.javaparser.ast.stmt.YieldStmt();
        com.github.javaparser.JavaToken invalidToken = com.github.javaparser.JavaToken.INVALID;
        com.github.javaparser.TokenRange invalidRange = new com.github.javaparser.TokenRange(invalidToken, invalidToken);
        com.github.javaparser.ast.comments.BlockComment contextComment = new com.github.javaparser.ast.comments.BlockComment(invalidRange, "Corresponding declaration not available for unsolved symbol.");
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(yieldStmt, ((java.lang.Object) (contextComment)));
        org.junit.Assert.assertNotSame(cloned, yieldStmt);
    }

    @org.junit.Test(timeout = 4000)
    public void visitTryStmt_withObject_returnsEqualButNotSame() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.stmt.TryStmt tryStmt = new com.github.javaparser.ast.stmt.TryStmt();
        java.lang.Object context = new java.lang.Object();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(tryStmt, context);
        org.junit.Assert.assertTrue(cloned.equals(((java.lang.Object) (tryStmt))));
        org.junit.Assert.assertNotSame(cloned, tryStmt);
    }

    @org.junit.Test(timeout = 4000)
    public void visitThrowStmt_withSelfContext_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.stmt.ThrowStmt throwStmt = new com.github.javaparser.ast.stmt.ThrowStmt();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(throwStmt, ((java.lang.Object) (throwStmt)));
        org.junit.Assert.assertNotSame(cloned, throwStmt);
    }

    @org.junit.Test(timeout = 4000)
    public void visitSynchronizedStmt_withBlockComment_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.comments.BlockComment contextComment = new com.github.javaparser.ast.comments.BlockComment();
        com.github.javaparser.ast.stmt.SynchronizedStmt syncStmt = new com.github.javaparser.ast.stmt.SynchronizedStmt();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(syncStmt, ((java.lang.Object) (contextComment)));
        org.junit.Assert.assertNotSame(cloned, syncStmt);
    }

    @org.junit.Test(timeout = 4000)
    public void visitSwitchEntry_withExpressionsAndStatements_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.JavaToken invalid = com.github.javaparser.JavaToken.INVALID;
        com.github.javaparser.TokenRange range = new com.github.javaparser.TokenRange(invalid, invalid);
        com.github.javaparser.ast.NodeList<com.github.javaparser.ast.expr.Expression> labels = new com.github.javaparser.ast.NodeList<com.github.javaparser.ast.expr.Expression>();
        com.github.javaparser.ast.stmt.SwitchEntry.Type entryType = com.github.javaparser.ast.type.Type.STATEMENT_GROUP;
        java.util.LinkedList<com.github.javaparser.ast.stmt.Statement> statementList = new java.util.LinkedList<com.github.javaparser.ast.stmt.Statement>();
        com.github.javaparser.ast.NodeList<com.github.javaparser.ast.stmt.Statement> statements = com.github.javaparser.ast.NodeList.nodeList(statementList);
        com.github.javaparser.ast.stmt.SwitchEntry entry = new com.github.javaparser.ast.stmt.SwitchEntry(range, labels, entryType, statements);
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(entry, ((java.lang.Object) (labels)));
        org.junit.Assert.assertNotSame(cloned, entry);
    }

    @org.junit.Test(timeout = 4000)
    public void visitReturnStmt_withSelfContext_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.stmt.ReturnStmt returnStmt = new com.github.javaparser.ast.stmt.ReturnStmt();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(returnStmt, ((java.lang.Object) (returnStmt)));
        org.junit.Assert.assertNotSame(cloned, returnStmt);
    }

    @org.junit.Test(timeout = 4000)
    public void visitLabeledStmt_withModifiersContext_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.Modifier[] modifiersArray = new com.github.javaparser.ast.Modifier[3];
        com.github.javaparser.ast.NodeList<com.github.javaparser.ast.Modifier> modifiers = new com.github.javaparser.ast.NodeList<com.github.javaparser.ast.Modifier>(modifiersArray);
        com.github.javaparser.ast.stmt.LabeledStmt labeledStmt = new com.github.javaparser.ast.stmt.LabeledStmt();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(labeledStmt, ((java.lang.Object) (modifiers)));
        org.junit.Assert.assertNotSame(cloned, labeledStmt);
    }

    @org.junit.Test(timeout = 4000)
    public void visitIfStmt_withoutElse_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.stmt.IfStmt originalIf = new com.github.javaparser.ast.stmt.IfStmt();
        java.lang.Object context = new java.lang.Object();
        com.github.javaparser.ast.stmt.IfStmt clonedIf = ((com.github.javaparser.ast.stmt.IfStmt) (visitor.visit(originalIf, context)));
        org.junit.Assert.assertFalse(clonedIf.hasElseBranch());
        org.junit.Assert.assertNotSame(clonedIf, originalIf);
    }

    @org.junit.Test(timeout = 4000)
    public void visitForStmt_withBlockCommentListContext_returnsEqualButNotSame() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.stmt.ForStmt forStmt = new com.github.javaparser.ast.stmt.ForStmt();
        com.github.javaparser.ast.comments.BlockComment[] commentArray = new com.github.javaparser.ast.comments.BlockComment[0];
        com.github.javaparser.ast.NodeList<com.github.javaparser.ast.comments.BlockComment> comments = new com.github.javaparser.ast.NodeList<com.github.javaparser.ast.comments.BlockComment>(commentArray);
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(forStmt, ((java.lang.Object) (comments)));
        org.junit.Assert.assertNotSame(cloned, forStmt);
        org.junit.Assert.assertTrue(cloned.equals(((java.lang.Object) (forStmt))));
    }

    @org.junit.Test(timeout = 4000)
    public void visitForEachStmt_withObject_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.stmt.ForEachStmt forEach = new com.github.javaparser.ast.stmt.ForEachStmt();
        java.lang.Object context = new java.lang.Object();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(forEach, context);
        org.junit.Assert.assertNotSame(cloned, forEach);
    }

    @org.junit.Test(timeout = 4000)
    public void visitExpressionStmt_withObject_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.stmt.ExpressionStmt exprStmt = new com.github.javaparser.ast.stmt.ExpressionStmt();
        java.lang.Object context = new java.lang.Object();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(exprStmt, context);
        org.junit.Assert.assertNotSame(cloned, exprStmt);
    }

    @org.junit.Test(timeout = 4000)
    public void visitExplicitConstructorInvocation_withBlockComment_returnsEqualButNotSame() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.comments.BlockComment contextComment = new com.github.javaparser.ast.comments.BlockComment();
        com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt invocation = new com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(invocation, ((java.lang.Object) (contextComment)));
        org.junit.Assert.assertTrue(cloned.equals(((java.lang.Object) (invocation))));
        org.junit.Assert.assertNotSame(cloned, invocation);
    }

    @org.junit.Test(timeout = 4000)
    public void visitBreakStmt_withObject_returnsEqualButNotSame() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        java.lang.Object context = new java.lang.Object();
        com.github.javaparser.ast.stmt.BreakStmt breakStmt = new com.github.javaparser.ast.stmt.BreakStmt("&");
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(breakStmt, context);
        org.junit.Assert.assertNotSame(cloned, breakStmt);
        org.junit.Assert.assertTrue(cloned.equals(((java.lang.Object) (breakStmt))));
    }

    @org.junit.Test(timeout = 4000)
    public void visitBlockStmt_fromRecordInitializer_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.body.RecordDeclaration record = new com.github.javaparser.ast.body.RecordDeclaration();
        com.github.javaparser.ast.stmt.BlockStmt staticInitializer = record.addStaticInitializer();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(staticInitializer, ((java.lang.Object) (record)));
        org.junit.Assert.assertNotSame(cloned, staticInitializer);
    }

    @org.junit.Test(timeout = 4000)
    public void visitModuleUsesDirective_withNullContext_returnsEqualButNotSame() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.JavaToken token = new com.github.javaparser.JavaToken(0);
        com.github.javaparser.TokenRange range = new com.github.javaparser.TokenRange(token, token);
        com.github.javaparser.ast.expr.Name serviceName = new com.github.javaparser.ast.expr.Name();
        com.github.javaparser.ast.modules.ModuleUsesDirective usesDirective = new com.github.javaparser.ast.modules.ModuleUsesDirective(range, serviceName);
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(usesDirective, ((java.lang.Object) (null)));
        org.junit.Assert.assertTrue(cloned.equals(((java.lang.Object) (usesDirective))));
        org.junit.Assert.assertNotSame(cloned, usesDirective);
    }

    @org.junit.Test(timeout = 4000)
    public void visitModuleRequiresDirective_withNullContext_returnsEqualButNotSame() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.modules.ModuleRequiresDirective requires = new com.github.javaparser.ast.modules.ModuleRequiresDirective();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(requires, ((java.lang.Object) (null)));
        org.junit.Assert.assertTrue(cloned.equals(((java.lang.Object) (requires))));
        org.junit.Assert.assertNotSame(cloned, requires);
    }

    @org.junit.Test(timeout = 4000)
    public void visitVariableDeclarationExpr_withOwnListContext_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.body.VariableDeclarator[] vars = new com.github.javaparser.ast.body.VariableDeclarator[0];
        com.github.javaparser.ast.NodeList<com.github.javaparser.ast.body.VariableDeclarator> declarators = new com.github.javaparser.ast.NodeList<com.github.javaparser.ast.body.VariableDeclarator>(vars);
        com.github.javaparser.ast.expr.VariableDeclarationExpr varExpr = new com.github.javaparser.ast.expr.VariableDeclarationExpr(declarators);
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(varExpr, ((java.lang.Object) (declarators)));
        org.junit.Assert.assertNotSame(cloned, varExpr);
    }

    @org.junit.Test(timeout = 4000)
    public void visitThisExpr_withObject_returnsEqualButNotSame() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.expr.Name qualifier = new com.github.javaparser.ast.expr.Name();
        com.github.javaparser.ast.expr.ThisExpr thisExpr = new com.github.javaparser.ast.expr.ThisExpr(qualifier);
        java.lang.Object context = new java.lang.Object();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(thisExpr, context);
        org.junit.Assert.assertNotSame(cloned, thisExpr);
        org.junit.Assert.assertTrue(cloned.equals(((java.lang.Object) (thisExpr))));
    }

    @org.junit.Test(timeout = 4000)
    public void visitTextBlockLiteralExpr_withBlockComment_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.comments.BlockComment contextComment = new com.github.javaparser.ast.comments.BlockComment();
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.expr.TextBlockLiteralExpr textBlock = new com.github.javaparser.ast.expr.TextBlockLiteralExpr();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(textBlock, ((java.lang.Object) (contextComment)));
        org.junit.Assert.assertNotSame(cloned, textBlock);
    }

    @org.junit.Test(timeout = 4000)
    public void visitSimpleName_withObject_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        java.lang.Object context = new java.lang.Object();
        com.github.javaparser.ast.expr.SimpleName simpleName = new com.github.javaparser.ast.expr.SimpleName();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(simpleName, context);
        org.junit.Assert.assertNotSame(cloned, simpleName);
    }

    @org.junit.Test(timeout = 4000)
    public void visitNameExpr_withNullContext_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.expr.SimpleName identifier = new com.github.javaparser.ast.expr.SimpleName();
        com.github.javaparser.ast.expr.NameExpr nameExpr = new com.github.javaparser.ast.expr.NameExpr(identifier);
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(nameExpr, ((java.lang.Object) (null)));
        org.junit.Assert.assertNotSame(cloned, nameExpr);
    }

    @org.junit.Test(timeout = 4000)
    public void visitMatchAllPatternExpr_withModifiers_returnsEqualButNotSame() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.comments.BlockComment contextComment = new com.github.javaparser.ast.comments.BlockComment();
        // Modifier[] modifierKeywords = new Modifier.Keyword[4];
        com.github.javaparser.ast.Modifier.Keyword volatileKeyword = Modifier.Keyword.VOLATILE;
        // modifierKeywords[0] = volatileKeyword;
        // modifierKeywords[1] = modifierKeywords[0];
        // modifierKeywords[2] = modifierKeywords[1];
        // modifierKeywords[3] = modifierKeywords[2];
        // NodeList<Modifier> modifiers = Modifier.createModifierList(modifierKeywords);
        // MatchAllPatternExpr pattern = new MatchAllPatternExpr(modifiers);
        // Visitable cloned = visitor.visit(pattern, ((Object) (contextComment)));
        // assertNotSame(cloned, pattern);
        // assertTrue(cloned.equals(((Object) (pattern))));
    }

    @org.junit.Test(timeout = 4000)
    public void visitLongLiteralExpr_withNullContext_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.expr.LongLiteralExpr literal = new com.github.javaparser.ast.expr.LongLiteralExpr("\"class\"");
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(literal, ((java.lang.Object) (null)));
        org.junit.Assert.assertNotSame(cloned, literal);
    }

    @org.junit.Test(timeout = 4000)
    public void visitInstanceOfExpr_withBlockComment_returnsEqualButNotSame() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.expr.InstanceOfExpr instanceOfExpr = new com.github.javaparser.ast.expr.InstanceOfExpr();
        com.github.javaparser.ast.comments.BlockComment contextComment = new com.github.javaparser.ast.comments.BlockComment();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(instanceOfExpr, ((java.lang.Object) (contextComment)));
        org.junit.Assert.assertTrue(cloned.equals(((java.lang.Object) (instanceOfExpr))));
        org.junit.Assert.assertNotSame(cloned, instanceOfExpr);
    }

    @org.junit.Test(timeout = 4000)
    public void visitFieldAccessExpr_withObject_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        java.lang.Object context = new java.lang.Object();
        com.github.javaparser.ast.expr.FieldAccessExpr fieldAccess = new com.github.javaparser.ast.expr.FieldAccessExpr();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(fieldAccess, context);
        org.junit.Assert.assertNotSame(cloned, fieldAccess);
    }

    @org.junit.Test(timeout = 4000)
    public void visitEnclosedExpr_withBlockComment_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.comments.BlockComment contextComment = new com.github.javaparser.ast.comments.BlockComment();
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.expr.EnclosedExpr enclosed = new com.github.javaparser.ast.expr.EnclosedExpr();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(enclosed, ((java.lang.Object) (contextComment)));
        org.junit.Assert.assertNotSame(cloned, enclosed);
    }

    @org.junit.Test(timeout = 4000)
    public void visitConditionalExpr_withObject_returnsEqualButNotSame() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.expr.ConditionalExpr conditional = new com.github.javaparser.ast.expr.ConditionalExpr();
        java.lang.Object context = new java.lang.Object();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(conditional, context);
        org.junit.Assert.assertNotSame(cloned, conditional);
        org.junit.Assert.assertTrue(cloned.equals(((java.lang.Object) (conditional))));
    }

    @org.junit.Test(timeout = 4000)
    public void visitClassExpr_withBlockCommentListContext_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.type.TypeParameter typeParam = new com.github.javaparser.ast.type.TypeParameter("4)oF?x7_I'eKf+j`G<~");
        com.github.javaparser.ast.expr.ClassExpr classExpr = new com.github.javaparser.ast.expr.ClassExpr(typeParam);
        com.github.javaparser.ast.comments.BlockComment[] blockComments = new com.github.javaparser.ast.comments.BlockComment[4];
        com.github.javaparser.ast.NodeList<com.github.javaparser.ast.comments.BlockComment> contextComments = new com.github.javaparser.ast.NodeList<com.github.javaparser.ast.comments.BlockComment>(blockComments);
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(classExpr, ((java.lang.Object) (contextComments)));
        org.junit.Assert.assertNotSame(cloned, classExpr);
    }

    @org.junit.Test(timeout = 4000)
    public void visitCharLiteralExpr_withBlockCommentListContext_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.expr.CharLiteralExpr charLiteral = com.github.javaparser.ast.expr.CharLiteralExpr.escape("");
        com.github.javaparser.ast.NodeList<com.github.javaparser.ast.comments.BlockComment> comments = new com.github.javaparser.ast.NodeList<com.github.javaparser.ast.comments.BlockComment>();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(charLiteral, ((java.lang.Object) (comments)));
        org.junit.Assert.assertNotSame(cloned, charLiteral);
    }

    @org.junit.Test(timeout = 4000)
    public void visitBooleanLiteralExpr_withBlockComment_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.expr.BooleanLiteralExpr literal = new com.github.javaparser.ast.expr.BooleanLiteralExpr(true);
        com.github.javaparser.ast.comments.BlockComment contextComment = new com.github.javaparser.ast.comments.BlockComment("JAVA_1_0");
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(literal, ((java.lang.Object) (contextComment)));
        org.junit.Assert.assertNotSame(cloned, literal);
    }

    @org.junit.Test(timeout = 4000)
    public void visitBinaryExpr_withObject_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.expr.BinaryExpr binaryExpr = new com.github.javaparser.ast.expr.BinaryExpr();
        java.lang.Object context = new java.lang.Object();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(binaryExpr, context);
        org.junit.Assert.assertNotSame(cloned, binaryExpr);
    }

    @org.junit.Test(timeout = 4000)
    public void visitArrayCreationExpr_withObject_returnsEqualButNotSame() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.type.UnknownType unknown = new com.github.javaparser.ast.type.UnknownType();
        java.lang.Object context = new java.lang.Object();
        com.github.javaparser.ast.NodeList<com.github.javaparser.ast.ArrayCreationLevel> levels = new com.github.javaparser.ast.NodeList<com.github.javaparser.ast.ArrayCreationLevel>();
        com.github.javaparser.ast.expr.ArrayInitializerExpr initializer = new com.github.javaparser.ast.expr.ArrayInitializerExpr();
        com.github.javaparser.ast.expr.ArrayCreationExpr arrayCreation = new com.github.javaparser.ast.expr.ArrayCreationExpr(unknown, levels, initializer);
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(arrayCreation, context);
        org.junit.Assert.assertTrue(cloned.equals(((java.lang.Object) (arrayCreation))));
        org.junit.Assert.assertNotSame(cloned, arrayCreation);
    }

    @org.junit.Test(timeout = 4000)
    public void visitLineComment_withObject_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.TokenRange invalid = com.github.javaparser.TokenRange.INVALID;
        com.github.javaparser.ast.comments.LineComment lineComment = new com.github.javaparser.ast.comments.LineComment(invalid, "w*gWr 2b\u0007:X");
        java.lang.Object context = new java.lang.Object();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(lineComment, context);
        org.junit.Assert.assertNotSame(cloned, lineComment);
    }

    @org.junit.Test(timeout = 4000)
    public void visitBlockComment_withBlockCommentListContext_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.comments.BlockComment blockComment = new com.github.javaparser.ast.comments.BlockComment("");
        com.github.javaparser.ast.comments.BlockComment[] blockComments = new com.github.javaparser.ast.comments.BlockComment[8];
        com.github.javaparser.ast.NodeList<com.github.javaparser.ast.comments.BlockComment> context = com.github.javaparser.ast.NodeList.nodeList(blockComments);
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(blockComment, ((java.lang.Object) (context)));
        org.junit.Assert.assertNotSame(cloned, blockComment);
    }

    @org.junit.Test(timeout = 4000)
    public void visitVariableDeclarator_withObject_returnsEqualButNotSame() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.body.VariableDeclarator variable = new com.github.javaparser.ast.body.VariableDeclarator();
        java.lang.Object context = new java.lang.Object();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(variable, context);
        org.junit.Assert.assertTrue(cloned.equals(((java.lang.Object) (variable))));
        org.junit.Assert.assertNotSame(cloned, variable);
    }

    @org.junit.Test(timeout = 4000)
    public void visitReceiverParameter_withWhileStmtListContext_returnsEqualButNotSame() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.TokenRange invalid = com.github.javaparser.TokenRange.INVALID;
        com.github.javaparser.ast.type.UnknownType receiverType = new com.github.javaparser.ast.type.UnknownType(invalid);
        com.github.javaparser.ast.body.ReceiverParameter receiver = new com.github.javaparser.ast.body.ReceiverParameter(receiverType, "\"<<\"");
        com.github.javaparser.ast.NodeList<com.github.javaparser.ast.stmt.WhileStmt> context = new com.github.javaparser.ast.NodeList<com.github.javaparser.ast.stmt.WhileStmt>();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(receiver, ((java.lang.Object) (context)));
        org.junit.Assert.assertNotSame(cloned, receiver);
        org.junit.Assert.assertTrue(cloned.equals(((java.lang.Object) (receiver))));
    }

    @org.junit.Test(timeout = 4000)
    public void visitEnumDeclaration_withBlockComment_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.comments.BlockComment contextComment = new com.github.javaparser.ast.comments.BlockComment();
        com.github.javaparser.ast.body.EnumDeclaration enumDecl = new com.github.javaparser.ast.body.EnumDeclaration();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(enumDecl, ((java.lang.Object) (contextComment)));
        org.junit.Assert.assertNotSame(cloned, enumDecl);
    }

    @org.junit.Test(timeout = 4000)
    public void visitCompactConstructorDeclaration_withNullContext_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.body.CompactConstructorDeclaration compactCtor = new com.github.javaparser.ast.body.CompactConstructorDeclaration();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(compactCtor, ((java.lang.Object) (null)));
        org.junit.Assert.assertNotSame(cloned, compactCtor);
    }

    @org.junit.Test(timeout = 4000)
    public void visitAnnotationMemberDeclaration_withNullContext_returnsEqualButNotSame() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.body.AnnotationMemberDeclaration member = new com.github.javaparser.ast.body.AnnotationMemberDeclaration();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(member, ((java.lang.Object) (null)));
        org.junit.Assert.assertTrue(cloned.equals(((java.lang.Object) (member))));
        org.junit.Assert.assertNotSame(cloned, member);
    }

    @org.junit.Test(timeout = 4000)
    public void visitAnnotationDeclaration_withObject_returnsEqualButNotSame() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.Modifier[] modifiersArr = new com.github.javaparser.ast.Modifier[6];
        com.github.javaparser.ast.Modifier modifier = new com.github.javaparser.ast.Modifier();
        modifiersArr[0] = modifier;
        modifiersArr[1] = modifiersArr[0];
        modifiersArr[2] = modifiersArr[1];
        modifiersArr[3] = modifiersArr[1];
        modifiersArr[4] = modifier;
        modifiersArr[5] = modifiersArr[2];
        com.github.javaparser.ast.NodeList<com.github.javaparser.ast.Modifier> modifiers = com.github.javaparser.ast.NodeList.nodeList(modifiersArr);
        com.github.javaparser.ast.body.AnnotationDeclaration annotationDecl = new com.github.javaparser.ast.body.AnnotationDeclaration(modifiers, "g/hJ7smB#{,MTz%");
        java.lang.Object context = new java.lang.Object();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(annotationDecl, context);
        org.junit.Assert.assertTrue(cloned.equals(((java.lang.Object) (annotationDecl))));
        org.junit.Assert.assertNotSame(cloned, annotationDecl);
    }

    @org.junit.Test(timeout = 4000)
    public void visitImportDeclaration_withBlockComment_returnsEqualButNotSameNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.comments.BlockComment contextComment = new com.github.javaparser.ast.comments.BlockComment();
        com.github.javaparser.JavaToken token = new com.github.javaparser.JavaToken(1, "");
        com.github.javaparser.TokenRange range = new com.github.javaparser.TokenRange(token, token);
        com.github.javaparser.ast.expr.Name name = new com.github.javaparser.ast.expr.Name();
        com.github.javaparser.ast.ImportDeclaration importDecl = new com.github.javaparser.ast.ImportDeclaration(range, name, false, false, false);
        com.github.javaparser.ast.Node cloned = visitor.visit(importDecl, ((java.lang.Object) (contextComment)));
        org.junit.Assert.assertNotSame(cloned, importDecl);
        org.junit.Assert.assertTrue(cloned.equals(((java.lang.Object) (importDecl))));
    }

    @org.junit.Test(timeout = 4000)
    public void cloneNode_withEmptyOptional_returnsNull() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.Modifier[] modifiersArr = new com.github.javaparser.ast.Modifier[2];
        com.github.javaparser.ast.Modifier sync = com.github.javaparser.ast.Modifier.synchronizedModifier();
        modifiersArr[0] = sync;
        modifiersArr[1] = sync;
        com.github.javaparser.ast.NodeList<com.github.javaparser.ast.Modifier> modifiers = com.github.javaparser.ast.NodeList.nodeList(modifiersArr);
        com.github.javaparser.ast.body.RecordDeclaration record = new com.github.javaparser.ast.body.RecordDeclaration(modifiers, "/e>G`0p");
        java.util.Optional<com.github.javaparser.ast.body.MethodDeclaration> maybeMethod = record.toMethodDeclaration();
        java.lang.Object context = new java.lang.Object();
        com.github.javaparser.ast.body.MethodDeclaration cloned = visitor.cloneNode(maybeMethod, context);
        org.junit.Assert.assertNull(cloned);
    }

    @org.junit.Test(timeout = 4000)
    public void cloneNode_withPresentOptional_returnsNewInstance() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.stmt.YieldStmt yieldStmt = new com.github.javaparser.ast.stmt.YieldStmt();
        java.util.Optional<com.github.javaparser.ast.stmt.YieldStmt> maybeYield = yieldStmt.toYieldStmt();
        com.github.javaparser.ast.stmt.YieldStmt cloned = visitor.cloneNode(maybeYield, yieldStmt);
        org.junit.Assert.assertNotSame(cloned, yieldStmt);
        org.junit.Assert.assertNotNull(cloned);
    }

    @org.junit.Test(timeout = 4000)
    public void clone_onContinueStmt_returnsDeepCopy() throws java.lang.Throwable {
        com.github.javaparser.ast.stmt.ContinueStmt original = new com.github.javaparser.ast.stmt.ContinueStmt("%s is not MethodCallExpr, it is %s");
        com.github.javaparser.ast.stmt.ContinueStmt cloned = original.clone();
        org.junit.Assert.assertNotSame(cloned, original);
        org.junit.Assert.assertTrue(cloned.equals(((java.lang.Object) (original))));
    }

    @org.junit.Test(timeout = 4000)
    public void visitNodeList_withBlockComment_returnsListOfSameSize() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.NodeList<com.github.javaparser.ast.comments.BlockComment> comments = new com.github.javaparser.ast.NodeList<com.github.javaparser.ast.comments.BlockComment>();
        com.github.javaparser.ast.comments.BlockComment comment = new com.github.javaparser.ast.comments.BlockComment();
        com.github.javaparser.ast.NodeList<com.github.javaparser.ast.comments.BlockComment> afterAdd = comments.addLast(comment);
        com.github.javaparser.ast.NodeList visited = ((com.github.javaparser.ast.NodeList) (visitor.visit(((com.github.javaparser.ast.NodeList) (afterAdd)), ((java.lang.Object) (comment)))));
        org.junit.Assert.assertEquals(1, visited.size());
    }

    @org.junit.Test(timeout = 4000)
    public void visitPackageDeclaration_withSelfContext_returnsEqualButNotSame() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.PackageDeclaration pkg = new com.github.javaparser.ast.PackageDeclaration();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(pkg, ((java.lang.Object) (pkg)));
        org.junit.Assert.assertNotSame(cloned, pkg);
        org.junit.Assert.assertTrue(cloned.equals(((java.lang.Object) (pkg))));
    }

    @org.junit.Test(timeout = 4000)
    public void visitArrayCreationLevel_withBlockComment_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.comments.BlockComment contextComment = new com.github.javaparser.ast.comments.BlockComment();
        com.github.javaparser.ast.NodeList<com.github.javaparser.ast.expr.AnnotationExpr> annotations = new com.github.javaparser.ast.NodeList<com.github.javaparser.ast.expr.AnnotationExpr>();
        com.github.javaparser.ast.ArrayCreationLevel level = new com.github.javaparser.ast.ArrayCreationLevel(((com.github.javaparser.ast.expr.Expression) (null)), annotations);
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(level, ((java.lang.Object) (contextComment)));
        org.junit.Assert.assertNotSame(cloned, level);
    }

    @org.junit.Test(timeout = 4000)
    public void visitIntegerLiteralExpr_withBlockComment_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.comments.BlockComment contextComment = new com.github.javaparser.ast.comments.BlockComment();
        com.github.javaparser.ast.expr.IntegerLiteralExpr literal = new com.github.javaparser.ast.expr.IntegerLiteralExpr();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(literal, ((java.lang.Object) (contextComment)));
        org.junit.Assert.assertNotSame(cloned, literal);
    }

    @org.junit.Test(timeout = 4000)
    public void visitClassOrInterfaceDeclaration_withLocalRecordListContext_returnsNotConstructor() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.body.ClassOrInterfaceDeclaration typeDecl = new com.github.javaparser.ast.body.ClassOrInterfaceDeclaration();
        com.github.javaparser.ast.stmt.LocalRecordDeclarationStmt[] localRecords = new com.github.javaparser.ast.stmt.LocalRecordDeclarationStmt[0];
        com.github.javaparser.ast.NodeList<com.github.javaparser.ast.stmt.LocalRecordDeclarationStmt> context = com.github.javaparser.ast.NodeList.nodeList(localRecords);
        com.github.javaparser.ast.body.ClassOrInterfaceDeclaration cloned = ((com.github.javaparser.ast.body.ClassOrInterfaceDeclaration) (visitor.visit(typeDecl, ((java.lang.Object) (context)))));
        org.junit.Assert.assertFalse(cloned.isConstructorDeclaration());
    }

    @org.junit.Test(timeout = 4000)
    public void visitArrayInitializerExpr_withBlockComment_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.JavaToken invalid = com.github.javaparser.JavaToken.INVALID;
        com.github.javaparser.TokenRange range = new com.github.javaparser.TokenRange(invalid, invalid);
        com.github.javaparser.ast.comments.BlockComment contextComment = new com.github.javaparser.ast.comments.BlockComment();
        com.github.javaparser.ast.NodeList<com.github.javaparser.ast.expr.Expression> values = new com.github.javaparser.ast.NodeList<com.github.javaparser.ast.expr.Expression>();
        com.github.javaparser.ast.expr.ArrayInitializerExpr initializer = new com.github.javaparser.ast.expr.ArrayInitializerExpr(range, values);
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(initializer, ((java.lang.Object) (contextComment)));
        org.junit.Assert.assertNotSame(cloned, initializer);
    }

    @org.junit.Test(timeout = 4000)
    public void visitModifier_withSelfContext_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.Modifier abstractModifier = com.github.javaparser.ast.Modifier.abstractModifier();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(abstractModifier, ((java.lang.Object) (abstractModifier)));
        org.junit.Assert.assertNotSame(cloned, abstractModifier);
    }

    @org.junit.Test(timeout = 4000)
    public void visitBoxedPrimitiveType_withBlockComment_returnsEqualButNotSame() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.type.PrimitiveType primitiveShort = com.github.javaparser.ast.type.PrimitiveType.shortType();
        com.github.javaparser.ast.type.ClassOrInterfaceType boxed = primitiveShort.toBoxedType();
        com.github.javaparser.ast.comments.BlockComment contextComment = new com.github.javaparser.ast.comments.BlockComment("");
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(boxed, ((java.lang.Object) (contextComment)));
        org.junit.Assert.assertTrue(cloned.equals(((java.lang.Object) (boxed))));
        org.junit.Assert.assertNotSame(cloned, boxed);
    }

    @org.junit.Test(timeout = 4000)
    public void visitName_withBlockComment_returnsUnqualifiedNewInstance() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.comments.BlockComment contextComment = new com.github.javaparser.ast.comments.BlockComment("");
        com.github.javaparser.ast.expr.Name original = new com.github.javaparser.ast.expr.Name("u(il_%]h");
        com.github.javaparser.ast.expr.Name cloned = ((com.github.javaparser.ast.expr.Name) (visitor.visit(original, ((java.lang.Object) (contextComment)))));
        org.junit.Assert.assertNotSame(cloned, original);
        org.junit.Assert.assertFalse(cloned.hasQualifier());
    }

    @org.junit.Test(timeout = 4000)
    public void visitStringLiteralExpr_withNullContext_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.JavaToken token = new com.github.javaparser.JavaToken(1323, ">Pn^p");
        com.github.javaparser.TokenRange range = new com.github.javaparser.TokenRange(token, token);
        com.github.javaparser.ast.expr.StringLiteralExpr literal = new com.github.javaparser.ast.expr.StringLiteralExpr(range, "");
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(literal, ((java.lang.Object) (null)));
        org.junit.Assert.assertNotSame(cloned, literal);
    }

    @org.junit.Test(timeout = 4000)
    public void visitRecordDeclaration_withModifiersContext_returnsEqual() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.Modifier[] modifiersArr = new com.github.javaparser.ast.Modifier[3];
        com.github.javaparser.ast.Modifier volatileModifier = com.github.javaparser.ast.Modifier.volatileModifier();
        modifiersArr[0] = volatileModifier;
        modifiersArr[1] = volatileModifier;
        modifiersArr[2] = modifiersArr[0];
        com.github.javaparser.ast.NodeList<com.github.javaparser.ast.Modifier> modifiers = new com.github.javaparser.ast.NodeList<com.github.javaparser.ast.Modifier>(modifiersArr);
        com.github.javaparser.ast.body.RecordDeclaration record = new com.github.javaparser.ast.body.RecordDeclaration(modifiers, "RECEIVER_PARAMETER");
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(record, ((java.lang.Object) (modifiers)));
        org.junit.Assert.assertTrue(cloned.equals(((java.lang.Object) (record))));
    }

    @org.junit.Test(timeout = 4000)
    public void visitTypePatternExpr_withSelfContext_returnsEqualButNotSame() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.expr.TypePatternExpr typePattern = new com.github.javaparser.ast.expr.TypePatternExpr();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(typePattern, ((java.lang.Object) (typePattern)));
        org.junit.Assert.assertTrue(cloned.equals(((java.lang.Object) (typePattern))));
        org.junit.Assert.assertNotSame(cloned, typePattern);
    }

    @org.junit.Test(timeout = 4000)
    public void visitModuleDeclaration_withSelfContext_returnsEqualButNotSame() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.modules.ModuleDeclaration moduleDecl = new com.github.javaparser.ast.modules.ModuleDeclaration();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(moduleDecl, ((java.lang.Object) (moduleDecl)));
        org.junit.Assert.assertTrue(cloned.equals(((java.lang.Object) (moduleDecl))));
        org.junit.Assert.assertNotSame(cloned, moduleDecl);
    }

    @org.junit.Test(timeout = 4000)
    public void visitObjectCreationExpr_withSelfContext_returnsEqualButNotSame() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.expr.ObjectCreationExpr creationExpr = new com.github.javaparser.ast.expr.ObjectCreationExpr();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(creationExpr, ((java.lang.Object) (creationExpr)));
        org.junit.Assert.assertNotSame(cloned, creationExpr);
        org.junit.Assert.assertTrue(cloned.equals(((java.lang.Object) (creationExpr))));
    }

    @org.junit.Test(timeout = 4000)
    public void visitSingleMemberAnnotationExpr_withSelfContext_returnsEqualButNotSame() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.expr.SingleMemberAnnotationExpr annotation = new com.github.javaparser.ast.expr.SingleMemberAnnotationExpr();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(annotation, ((java.lang.Object) (annotation)));
        org.junit.Assert.assertTrue(cloned.equals(((java.lang.Object) (annotation))));
        org.junit.Assert.assertNotSame(cloned, annotation);
    }

    @org.junit.Test(timeout = 4000)
    public void visitTraditionalJavadocComment_withSelfContext_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.comments.TraditionalJavadocComment javadoc = new com.github.javaparser.ast.comments.TraditionalJavadocComment();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(javadoc, ((java.lang.Object) (javadoc)));
        org.junit.Assert.assertNotSame(cloned, javadoc);
    }

    @org.junit.Test(timeout = 4000)
    public void visitEmptyStmt_withSelfContext_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.stmt.EmptyStmt emptyStmt = new com.github.javaparser.ast.stmt.EmptyStmt();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(emptyStmt, ((java.lang.Object) (emptyStmt)));
        org.junit.Assert.assertNotSame(cloned, emptyStmt);
    }

    @org.junit.Test(timeout = 4000)
    public void visitMarkerAnnotationExpr_withSelfContext_returnsEqualButNotSame() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.expr.MarkerAnnotationExpr marker = new com.github.javaparser.ast.expr.MarkerAnnotationExpr();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(marker, ((java.lang.Object) (marker)));
        org.junit.Assert.assertTrue(cloned.equals(((java.lang.Object) (marker))));
        org.junit.Assert.assertNotSame(cloned, marker);
    }

    @org.junit.Test(timeout = 4000)
    public void visitNormalAnnotationExpr_withSelfContext_returnsEqualButNotSame() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.expr.NormalAnnotationExpr annotation = new com.github.javaparser.ast.expr.NormalAnnotationExpr();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(annotation, ((java.lang.Object) (annotation)));
        org.junit.Assert.assertNotSame(cloned, annotation);
        org.junit.Assert.assertTrue(cloned.equals(((java.lang.Object) (annotation))));
    }

    @org.junit.Test(timeout = 4000)
    public void visitUnaryExpr_withSelfContext_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.expr.UnaryExpr unary = new com.github.javaparser.ast.expr.UnaryExpr();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(unary, ((java.lang.Object) (unary)));
        org.junit.Assert.assertNotSame(cloned, unary);
    }

    @org.junit.Test(timeout = 4000)
    public void visitVoidType_withSelfContext_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.type.VoidType voidType = new com.github.javaparser.ast.type.VoidType();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(voidType, ((java.lang.Object) (voidType)));
        org.junit.Assert.assertNotSame(cloned, voidType);
    }

    @org.junit.Test(timeout = 4000)
    public void visitSwitchExpr_withSelfContext_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.expr.SwitchExpr switchExpr = new com.github.javaparser.ast.expr.SwitchExpr();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(switchExpr, ((java.lang.Object) (switchExpr)));
        org.junit.Assert.assertNotSame(cloned, switchExpr);
    }

    @org.junit.Test(timeout = 4000)
    public void visitSwitchStmt_withSelfContext_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.stmt.SwitchStmt switchStmt = new com.github.javaparser.ast.stmt.SwitchStmt();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(switchStmt, ((java.lang.Object) (switchStmt)));
        org.junit.Assert.assertNotSame(cloned, switchStmt);
    }

    @org.junit.Test(timeout = 4000)
    public void visitSuperExpr_withSelfContext_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.expr.SuperExpr superExpr = new com.github.javaparser.ast.expr.SuperExpr();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(superExpr, ((java.lang.Object) (superExpr)));
        org.junit.Assert.assertNotSame(cloned, superExpr);
    }

    @org.junit.Test(timeout = 4000)
    public void visitCompilationUnit_withSelfContext_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.CompilationUnit unit = new com.github.javaparser.ast.CompilationUnit();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(unit, ((java.lang.Object) (unit)));
        org.junit.Assert.assertNotSame(cloned, unit);
    }

    @org.junit.Test(timeout = 4000)
    public void visitCatchClause_withSelfContext_returnsEqualButNotSame() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.stmt.CatchClause catchClause = new com.github.javaparser.ast.stmt.CatchClause();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(catchClause, ((java.lang.Object) (catchClause)));
        org.junit.Assert.assertTrue(cloned.equals(((java.lang.Object) (catchClause))));
        org.junit.Assert.assertNotSame(cloned, catchClause);
    }

    @org.junit.Test(timeout = 4000)
    public void visitNullLiteralExpr_withSelfContext_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.expr.NullLiteralExpr nullLiteral = new com.github.javaparser.ast.expr.NullLiteralExpr();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(nullLiteral, ((java.lang.Object) (nullLiteral)));
        org.junit.Assert.assertNotSame(cloned, nullLiteral);
    }

    @org.junit.Test(timeout = 4000)
    public void visitMemberValuePair_withSelfContext_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.expr.MemberValuePair pair = new com.github.javaparser.ast.expr.MemberValuePair();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(pair, ((java.lang.Object) (pair)));
        org.junit.Assert.assertNotSame(cloned, pair);
    }

    @org.junit.Test(timeout = 4000)
    public void visitAssertStmt_withSelfContext_returnsEqualButNotSame() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.stmt.AssertStmt assertStmt = new com.github.javaparser.ast.stmt.AssertStmt();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(assertStmt, ((java.lang.Object) (assertStmt)));
        org.junit.Assert.assertNotSame(cloned, assertStmt);
        org.junit.Assert.assertTrue(cloned.equals(((java.lang.Object) (assertStmt))));
    }

    @org.junit.Test(timeout = 4000)
    public void visitUnparsableStmt_withSelfContext_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.stmt.UnparsableStmt unparsable = new com.github.javaparser.ast.stmt.UnparsableStmt();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(unparsable, ((java.lang.Object) (unparsable)));
        org.junit.Assert.assertNotSame(cloned, unparsable);
    }

    @org.junit.Test(timeout = 4000)
    public void visitEnumConstantDeclaration_withSelfContext_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.body.EnumConstantDeclaration enumConstant = new com.github.javaparser.ast.body.EnumConstantDeclaration();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(enumConstant, ((java.lang.Object) (enumConstant)));
        org.junit.Assert.assertNotSame(cloned, enumConstant);
    }

    @org.junit.Test(timeout = 4000)
    public void cloneNode_onBlockComment_returnsNewInstance() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.comments.BlockComment original = new com.github.javaparser.ast.comments.BlockComment();
        com.github.javaparser.ast.comments.BlockComment cloned = visitor.cloneNode(original, original);
        org.junit.Assert.assertNotSame(cloned, original);
    }

    @org.junit.Test(timeout = 4000)
    public void visitModuleProvidesDirective_withSelfContext_returnsEqualButNotSame() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.modules.ModuleProvidesDirective provides = new com.github.javaparser.ast.modules.ModuleProvidesDirective();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(provides, ((java.lang.Object) (provides)));
        org.junit.Assert.assertTrue(cloned.equals(((java.lang.Object) (provides))));
        org.junit.Assert.assertNotSame(cloned, provides);
    }

    @org.junit.Test(timeout = 4000)
    public void visitTypeExpr_withSelfContext_returnsEqualButNotSame() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.expr.TypeExpr typeExpr = new com.github.javaparser.ast.expr.TypeExpr();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(typeExpr, ((java.lang.Object) (typeExpr)));
        org.junit.Assert.assertTrue(cloned.equals(((java.lang.Object) (typeExpr))));
        org.junit.Assert.assertNotSame(cloned, typeExpr);
    }

    @org.junit.Test(timeout = 4000)
    public void visitContinueStmt_withSelfContext_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.stmt.ContinueStmt cont = new com.github.javaparser.ast.stmt.ContinueStmt();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(cont, ((java.lang.Object) (cont)));
        org.junit.Assert.assertNotSame(cloned, cont);
    }

    @org.junit.Test(timeout = 4000)
    public void visitMethodDeclaration_withSelfContext_returnsEqual() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.body.MethodDeclaration method = new com.github.javaparser.ast.body.MethodDeclaration();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(method, ((java.lang.Object) (method)));
        org.junit.Assert.assertTrue(cloned.equals(((java.lang.Object) (method))));
    }

    @org.junit.Test(timeout = 4000)
    public void visitLocalRecordDeclarationStmt_withSelfContext_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.stmt.LocalRecordDeclarationStmt localRecord = new com.github.javaparser.ast.stmt.LocalRecordDeclarationStmt();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(localRecord, ((java.lang.Object) (localRecord)));
        org.junit.Assert.assertNotSame(cloned, localRecord);
    }

    @org.junit.Test(timeout = 4000)
    public void visitArrayAccessExpr_withSelfContext_returnsEqualButNotSame() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.expr.ArrayAccessExpr arrayAccess = new com.github.javaparser.ast.expr.ArrayAccessExpr();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(arrayAccess, ((java.lang.Object) (arrayAccess)));
        org.junit.Assert.assertTrue(cloned.equals(((java.lang.Object) (arrayAccess))));
        org.junit.Assert.assertNotSame(cloned, arrayAccess);
    }

    @org.junit.Test(timeout = 4000)
    public void visitDoStmt_withSelfContext_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.stmt.DoStmt doStmt = new com.github.javaparser.ast.stmt.DoStmt();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(doStmt, ((java.lang.Object) (doStmt)));
        org.junit.Assert.assertNotSame(cloned, doStmt);
    }

    @org.junit.Test(timeout = 4000)
    public void visitUnknownType_withSelfContext_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.type.UnknownType unknown = new com.github.javaparser.ast.type.UnknownType();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(unknown, ((java.lang.Object) (unknown)));
        org.junit.Assert.assertNotSame(cloned, unknown);
    }

    @org.junit.Test(timeout = 4000)
    public void visitMethodReferenceExpr_withSelfContext_returnsEqualButNotSame() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.expr.MethodReferenceExpr reference = new com.github.javaparser.ast.expr.MethodReferenceExpr();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(reference, ((java.lang.Object) (reference)));
        org.junit.Assert.assertTrue(cloned.equals(((java.lang.Object) (reference))));
        org.junit.Assert.assertNotSame(cloned, reference);
    }

    @org.junit.Test(timeout = 4000)
    public void visitRecordPatternExpr_withSelfContext_returnsEqualButNotSame() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.expr.RecordPatternExpr recordPattern = new com.github.javaparser.ast.expr.RecordPatternExpr();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(recordPattern, ((java.lang.Object) (recordPattern)));
        org.junit.Assert.assertTrue(cloned.equals(((java.lang.Object) (recordPattern))));
        org.junit.Assert.assertNotSame(cloned, recordPattern);
    }

    @org.junit.Test(timeout = 4000)
    public void visitModuleOpensDirective_withSelfContext_returnsEqualButNotSame() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.modules.ModuleOpensDirective opens = new com.github.javaparser.ast.modules.ModuleOpensDirective();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(opens, ((java.lang.Object) (opens)));
        org.junit.Assert.assertTrue(cloned.equals(((java.lang.Object) (opens))));
        org.junit.Assert.assertNotSame(cloned, opens);
    }

    @org.junit.Test(timeout = 4000)
    public void visitAssignExpr_withSelfContext_returnsEqualButNotSame() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.expr.AssignExpr assign = new com.github.javaparser.ast.expr.AssignExpr();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(assign, ((java.lang.Object) (assign)));
        org.junit.Assert.assertNotSame(cloned, assign);
        org.junit.Assert.assertTrue(cloned.equals(((java.lang.Object) (assign))));
    }

    @org.junit.Test(timeout = 4000)
    public void visitLocalClassDeclarationStmt_withSelfContext_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.stmt.LocalClassDeclarationStmt localClass = new com.github.javaparser.ast.stmt.LocalClassDeclarationStmt();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(localClass, ((java.lang.Object) (localClass)));
        org.junit.Assert.assertNotSame(cloned, localClass);
    }

    @org.junit.Test(timeout = 4000)
    public void visitModuleExportsDirective_withSelfContext_returnsEqualButNotSame() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.modules.ModuleExportsDirective exports = new com.github.javaparser.ast.modules.ModuleExportsDirective();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(exports, ((java.lang.Object) (exports)));
        org.junit.Assert.assertNotSame(cloned, exports);
        org.junit.Assert.assertTrue(cloned.equals(((java.lang.Object) (exports))));
    }

    @org.junit.Test(timeout = 4000)
    public void visitInitializerDeclaration_withSelfContext_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.body.InitializerDeclaration initializer = new com.github.javaparser.ast.body.InitializerDeclaration();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(initializer, ((java.lang.Object) (initializer)));
        org.junit.Assert.assertNotSame(cloned, initializer);
    }

    @org.junit.Test(timeout = 4000)
    public void visitTypeParameter_withSelfContext_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.type.TypeParameter typeParam = new com.github.javaparser.ast.type.TypeParameter();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(typeParam, ((java.lang.Object) (typeParam)));
        org.junit.Assert.assertNotSame(cloned, typeParam);
    }

    @org.junit.Test(timeout = 4000)
    public void visitDoubleLiteralExpr_withSelfContext_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.expr.DoubleLiteralExpr doubleLiteral = new com.github.javaparser.ast.expr.DoubleLiteralExpr();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(doubleLiteral, ((java.lang.Object) (doubleLiteral)));
        org.junit.Assert.assertNotSame(cloned, doubleLiteral);
    }

    @org.junit.Test(timeout = 4000)
    public void visitConstructorDeclaration_withSelfContext_preservesNotEnum() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.body.ConstructorDeclaration ctor = new com.github.javaparser.ast.body.ConstructorDeclaration();
        com.github.javaparser.ast.body.ConstructorDeclaration cloned = ((com.github.javaparser.ast.body.ConstructorDeclaration) (visitor.visit(ctor, ((java.lang.Object) (ctor)))));
        org.junit.Assert.assertFalse(cloned.isEnumDeclaration());
    }

    @org.junit.Test(timeout = 4000)
    public void visitMarkdownComment_withSelfContext_returnsClonedNode() throws java.lang.Throwable {
        com.github.javaparser.ast.visitor.CloneVisitor visitor = new com.github.javaparser.ast.visitor.CloneVisitor();
        com.github.javaparser.ast.comments.MarkdownComment markdown = new com.github.javaparser.ast.comments.MarkdownComment();
        com.github.javaparser.ast.visitor.Visitable cloned = visitor.visit(markdown, ((java.lang.Object) (markdown)));
        org.junit.Assert.assertNotSame(cloned, markdown);
    }

    @org.junit.Test(timeout = 4000)
    public void cloneCompilationUnit_preservesJavadocOnMembers() throws java.lang.Throwable {
        com.github.javaparser.ast.NodeList<com.github.javaparser.ast.body.BodyDeclaration<?>> bodyDeclarationList = new com.github.javaparser.ast.NodeList<com.github.javaparser.ast.body.BodyDeclaration<?>>();
        bodyDeclarationList.add(new com.github.javaparser.ast.body.AnnotationMemberDeclaration().setJavadocComment("javadoc"));
        bodyDeclarationList.add(new com.github.javaparser.ast.body.ConstructorDeclaration().setJavadocComment("javadoc"));
        bodyDeclarationList.add(new com.github.javaparser.ast.body.EnumConstantDeclaration().setJavadocComment("javadoc"));
        bodyDeclarationList.add(new com.github.javaparser.ast.body.FieldDeclaration().setJavadocComment("javadoc"));
        bodyDeclarationList.add(new com.github.javaparser.ast.body.InitializerDeclaration().setJavadocComment("javadoc"));
        bodyDeclarationList.add(new com.github.javaparser.ast.body.MethodDeclaration().setJavadocComment("javadoc"));
        com.github.javaparser.ast.NodeList<com.github.javaparser.ast.body.TypeDeclaration<?>> typeDeclarationList = new com.github.javaparser.ast.NodeList<com.github.javaparser.ast.body.TypeDeclaration<?>>();
        com.github.javaparser.ast.body.AnnotationDeclaration annotationDeclaration = new com.github.javaparser.ast.body.AnnotationDeclaration();
        annotationDeclaration.setName("nnotationDeclarationTest");
        typeDeclarationList.add(annotationDeclaration.setJavadocComment("javadoc"));
        com.github.javaparser.ast.body.ClassOrInterfaceDeclaration classOrInterfaceDeclaration2 = new com.github.javaparser.ast.body.ClassOrInterfaceDeclaration();
        classOrInterfaceDeclaration2.setName("emptyTypeDeclarationTest");
        typeDeclarationList.add(classOrInterfaceDeclaration2.setJavadocComment("javadoc"));
        com.github.javaparser.ast.body.EnumDeclaration enumDeclaration = new com.github.javaparser.ast.body.EnumDeclaration();
        enumDeclaration.setName("enumDeclarationTest");
        typeDeclarationList.add(enumDeclaration.setJavadocComment("javadoc"));
        com.github.javaparser.ast.body.ClassOrInterfaceDeclaration classOrInterfaceDeclaration = new com.github.javaparser.ast.body.ClassOrInterfaceDeclaration();
        classOrInterfaceDeclaration.setName("classOrInterfaceDeclarationTest");
        typeDeclarationList.add(classOrInterfaceDeclaration.setJavadocComment("javadoc"));
        com.github.javaparser.ast.body.ClassOrInterfaceDeclaration classOrInterfaceDeclaration1 = new com.github.javaparser.ast.body.ClassOrInterfaceDeclaration();
        classOrInterfaceDeclaration1.setName("emptyTypeDeclarationTest1");
        typeDeclarationList.add(classOrInterfaceDeclaration2.setMembers(bodyDeclarationList));
        com.github.javaparser.ast.CompilationUnit cu = new com.github.javaparser.ast.CompilationUnit();
        cu.setTypes(typeDeclarationList);
        com.github.javaparser.ast.CompilationUnit cuClone = ((com.github.javaparser.ast.CompilationUnit) (new com.github.javaparser.ast.visitor.CloneVisitor().visit(cu, ((java.lang.Object) (null)))));
        for (com.github.javaparser.ast.body.TypeDeclaration<?> typeDeclaration : cuClone.getTypes()) {
            if (typeDeclaration.getMembers() == null) {
                org.junit.Assert.assertEquals("javadoc", typeDeclaration.getComment().get().getContent());
            } else {
                for (com.github.javaparser.ast.body.BodyDeclaration<?> bodyDeclaration : typeDeclaration.getMembers()) {
                    org.junit.Assert.assertEquals("javadoc", bodyDeclaration.getComment().get().getContent());
                }
            }
        }
    }

    @org.junit.Test(timeout = 4000)
    public void cloneAnnotationOnWildcardTypeArgument_preservesToString() throws java.lang.Throwable {
        com.github.javaparser.ast.type.Type type = com.github.javaparser.StaticJavaParser.parseType("List<@C ? extends Object>").clone();
        org.junit.Assert.assertEquals("List<@C ? extends Object>", type.toString());
    }
}