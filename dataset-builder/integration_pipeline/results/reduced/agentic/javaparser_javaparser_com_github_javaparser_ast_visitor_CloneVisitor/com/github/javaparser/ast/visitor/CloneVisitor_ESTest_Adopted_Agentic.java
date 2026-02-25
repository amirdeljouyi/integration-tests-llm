/* Copyright (C) 2007-2010 Júlio Vilmar Gesser.
Copyright (C) 2011, 2013-2024 The JavaParser Team.

This file is part of JavaParser.

JavaParser can be used either under the terms of
a) the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
b) the terms of the Apache License

You should have received a copy of both licenses in LICENCE.LGPL and
LICENCE.APACHE. Please refer to those files for details.

JavaParser is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.
 */
package com.github.javaparser.ast.visitor;
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
import static com.github.javaparser.StaticJavaParser.parseType;
class CloneVisitor_ESTest_Adopted_Agentic {
    com.github.javaparser.ast.CompilationUnit cu;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        cu = new com.github.javaparser.ast.CompilationUnit();
    }

    @org.junit.jupiter.api.AfterEach
    void teardown() {
        cu = null;
    }

    @org.junit.jupiter.api.Test
    void cloneJavaDocTest() {
        com.github.javaparser.ast.NodeList<BodyDeclaration<?>> bodyDeclarationList = new com.github.javaparser.ast.NodeList<>();
        bodyDeclarationList.add(new AnnotationMemberDeclaration().setJavadocComment("javadoc"));
        bodyDeclarationList.add(new ConstructorDeclaration().setJavadocComment("javadoc"));
        bodyDeclarationList.add(new EnumConstantDeclaration().setJavadocComment("javadoc"));
        bodyDeclarationList.add(new FieldDeclaration().setJavadocComment("javadoc"));
        bodyDeclarationList.add(new InitializerDeclaration().setJavadocComment("javadoc"));
        bodyDeclarationList.add(new MethodDeclaration().setJavadocComment("javadoc"));
        com.github.javaparser.ast.NodeList<TypeDeclaration<?>> typeDeclarationList = new com.github.javaparser.ast.NodeList<>();
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
        cu.setTypes(typeDeclarationList);
        com.github.javaparser.ast.CompilationUnit cuClone = ((com.github.javaparser.ast.CompilationUnit) (new CloneVisitor().visit(cu, null)));
        com.github.javaparser.ast.NodeList<TypeDeclaration<?>> typeDeclarationListClone = cuClone.getTypes();
        java.util.Iterator<TypeDeclaration<?>> typeItr = typeDeclarationListClone.iterator();
        TypeDeclaration<?> typeDeclaration;
        while (typeItr.hasNext()) {
            typeDeclaration = typeItr.next();
            if (typeDeclaration.getMembers() == null) {
                org.junit.jupiter.api.Assertions.assertEquals("javadoc", typeDeclaration.getComment().get().getContent());
            } else {
                java.util.Iterator<BodyDeclaration<?>> bodyItr = typeDeclaration.getMembers().iterator();
                while (bodyItr.hasNext()) {
                    BodyDeclaration<?> bodyDeclaration = bodyItr.next();
                    org.junit.jupiter.api.Assertions.assertEquals("javadoc", bodyDeclaration.getComment().get().getContent());
                } 
            }
        } 
    }

    @org.junit.jupiter.api.Test
    void cloneAnnotationOnWildcardTypeArgument() {
        Type type = com.github.javaparser.StaticJavaParser.parseType("List<@C ? extends Object>").clone();
        org.junit.jupiter.api.Assertions.assertEquals("List<@C ? extends Object>", type.toString());
    }

    @org.junit.jupiter.api.Test
    void visitWildcardType_withBlockComment_returnsEqualButNotSame() {
        CloneVisitor visitor = new CloneVisitor();
        TypeParameter bound = new TypeParameter("]0Cjw}+`@oK`WO2B");
        com.github.javaparser.ast.NodeList<AnnotationExpr> annotations = new com.github.javaparser.ast.NodeList<>();
        WildcardType wildcard = new WildcardType(bound, bound, annotations);
        com.github.javaparser.ast.comments.BlockComment contextComment = new com.github.javaparser.ast.comments.BlockComment();
        Visitable cloned = visitor.visit(wildcard, contextComment);
        org.junit.jupiter.api.Assertions.assertTrue(cloned.equals(wildcard));
        org.junit.jupiter.api.Assertions.assertNotSame(cloned, wildcard);
    }

    @org.junit.jupiter.api.Test
    void visitVarType_withObject_returnsClonedNode() {
        CloneVisitor visitor = new CloneVisitor();
        VarType varType = new VarType();
        java.lang.Object context = new java.lang.Object();
        Visitable cloned = visitor.visit(varType, context);
        org.junit.jupiter.api.Assertions.assertNotSame(cloned, varType);
    }

    @org.junit.jupiter.api.Test
    void visitUnionType_withObject_returnsClonedNode() {
        CloneVisitor visitor = new CloneVisitor();
        UnionType unionType = new UnionType();
        java.lang.Object context = new java.lang.Object();
        Visitable cloned = visitor.visit(unionType, context);
        org.junit.jupiter.api.Assertions.assertNotSame(cloned, unionType);
    }

    @org.junit.jupiter.api.Test
    void visitPrimitiveType_withObject_returnsClonedNode() {
        CloneVisitor visitor = new CloneVisitor();
        java.lang.Object context = new java.lang.Object();
        PrimitiveType shortType = PrimitiveType.shortType();
        Visitable cloned = visitor.visit(shortType, context);
        org.junit.jupiter.api.Assertions.assertNotSame(cloned, shortType);
    }

    @org.junit.jupiter.api.Test
    void visitIntersectionType_withObject_returnsClonedNode() {
        CloneVisitor visitor = new CloneVisitor();
        com.github.javaparser.ast.NodeList<ReferenceType> elements = new com.github.javaparser.ast.NodeList<>();
        IntersectionType intersection = new IntersectionType(elements);
        java.lang.Object context = new java.lang.Object();
        Visitable cloned = visitor.visit(intersection, context);
        org.junit.jupiter.api.Assertions.assertNotSame(cloned, intersection);
    }

    @org.junit.jupiter.api.Test
    void visitYieldStmt_withBlockComment_returnsClonedNode() {
        CloneVisitor visitor = new CloneVisitor();
        YieldStmt yieldStmt = new YieldStmt();
        com.github.javaparser.JavaToken invalidToken = com.github.javaparser.JavaToken.INVALID;
        com.github.javaparser.TokenRange invalidRange = new com.github.javaparser.TokenRange(invalidToken, invalidToken);
        com.github.javaparser.ast.comments.BlockComment contextComment = new com.github.javaparser.ast.comments.BlockComment(invalidRange, "Corresponding declaration not available for unsolved symbol.");
        Visitable cloned = visitor.visit(yieldStmt, contextComment);
        org.junit.jupiter.api.Assertions.assertNotSame(cloned, yieldStmt);
    }

    @org.junit.jupiter.api.Test
    void visitTryStmt_withObject_returnsEqualButNotSame() {
        CloneVisitor visitor = new CloneVisitor();
        TryStmt tryStmt = new TryStmt();
        java.lang.Object context = new java.lang.Object();
        Visitable cloned = visitor.visit(tryStmt, context);
        org.junit.jupiter.api.Assertions.assertTrue(cloned.equals(tryStmt));
        org.junit.jupiter.api.Assertions.assertNotSame(cloned, tryStmt);
    }

    @org.junit.jupiter.api.Test
    void visitThrowStmt_withSelfContext_returnsClonedNode() {
        CloneVisitor visitor = new CloneVisitor();
        ThrowStmt throwStmt = new ThrowStmt();
        Visitable cloned = visitor.visit(throwStmt, throwStmt);
        org.junit.jupiter.api.Assertions.assertNotSame(cloned, throwStmt);
    }

    @org.junit.jupiter.api.Test
    void visitSynchronizedStmt_withBlockComment_returnsClonedNode() {
        CloneVisitor visitor = new CloneVisitor();
        com.github.javaparser.ast.comments.BlockComment contextComment = new com.github.javaparser.ast.comments.BlockComment();
        SynchronizedStmt syncStmt = new SynchronizedStmt();
        Visitable cloned = visitor.visit(syncStmt, contextComment);
        org.junit.jupiter.api.Assertions.assertNotSame(cloned, syncStmt);
    }

    @org.junit.jupiter.api.Test
    void visitSwitchEntry_withExpressionsAndStatements_returnsClonedNode() {
        CloneVisitor visitor = new CloneVisitor();
        com.github.javaparser.JavaToken invalid = com.github.javaparser.JavaToken.INVALID;
        com.github.javaparser.TokenRange range = new com.github.javaparser.TokenRange(invalid, invalid);
        com.github.javaparser.ast.NodeList<Expression> labels = new com.github.javaparser.ast.NodeList<>();
        SwitchEntry.Type entryType = SwitchEntry.Type.STATEMENT_GROUP;
        java.util.LinkedList<Statement> statementList = new java.util.LinkedList<>();
        com.github.javaparser.ast.NodeList<Statement> statements = com.github.javaparser.ast.NodeList.nodeList(statementList);
        SwitchEntry entry = new SwitchEntry(range, labels, entryType, statements);
        Visitable cloned = visitor.visit(entry, labels);
        org.junit.jupiter.api.Assertions.assertNotSame(cloned, entry);
    }

    @org.junit.jupiter.api.Test
    void visitReturnStmt_withSelfContext_returnsClonedNode() {
        CloneVisitor visitor = new CloneVisitor();
        ReturnStmt returnStmt = new ReturnStmt();
        Visitable cloned = visitor.visit(returnStmt, returnStmt);
        org.junit.jupiter.api.Assertions.assertNotSame(cloned, returnStmt);
    }

    @org.junit.jupiter.api.Test
    void visitLabeledStmt_withModifiersContext_returnsClonedNode() {
        CloneVisitor visitor = new CloneVisitor();
        com.github.javaparser.ast.Modifier[] modifiersArray = new com.github.javaparser.ast.Modifier[3];
        com.github.javaparser.ast.NodeList<com.github.javaparser.ast.Modifier> modifiers = new com.github.javaparser.ast.NodeList<>(modifiersArray);
        LabeledStmt labeledStmt = new LabeledStmt();
        Visitable cloned = visitor.visit(labeledStmt, modifiers);
        org.junit.jupiter.api.Assertions.assertNotSame(cloned, labeledStmt);
    }

    @org.junit.jupiter.api.Test
    void visitIfStmt_withoutElse_returnsClonedNode() {
        CloneVisitor visitor = new CloneVisitor();
        IfStmt originalIf = new IfStmt();
        java.lang.Object context = new java.lang.Object();
        IfStmt clonedIf = ((IfStmt) (visitor.visit(originalIf, context)));
        org.junit.jupiter.api.Assertions.assertFalse(clonedIf.hasElseBranch());
        org.junit.jupiter.api.Assertions.assertNotSame(clonedIf, originalIf);
    }

    @org.junit.jupiter.api.Test
    void visitForStmt_withBlockCommentListContext_returnsEqualButNotSame() {
        CloneVisitor visitor = new CloneVisitor();
        ForStmt forStmt = new ForStmt();
        com.github.javaparser.ast.comments.BlockComment[] commentArray = new com.github.javaparser.ast.comments.BlockComment[0];
        com.github.javaparser.ast.NodeList<com.github.javaparser.ast.comments.BlockComment> comments = new com.github.javaparser.ast.NodeList<>(commentArray);
        Visitable cloned = visitor.visit(forStmt, comments);
        org.junit.jupiter.api.Assertions.assertNotSame(cloned, forStmt);
        org.junit.jupiter.api.Assertions.assertTrue(cloned.equals(forStmt));
    }

    @org.junit.jupiter.api.Test
    void visitForEachStmt_withObject_returnsClonedNode() {
        CloneVisitor visitor = new CloneVisitor();
        ForEachStmt forEach = new ForEachStmt();
        java.lang.Object context = new java.lang.Object();
        Visitable cloned = visitor.visit(forEach, context);
        org.junit.jupiter.api.Assertions.assertNotSame(cloned, forEach);
    }

    @org.junit.jupiter.api.Test
    void visitExpressionStmt_withObject_returnsClonedNode() {
        CloneVisitor visitor = new CloneVisitor();
        ExpressionStmt exprStmt = new ExpressionStmt();
        java.lang.Object context = new java.lang.Object();
        Visitable cloned = visitor.visit(exprStmt, context);
        org.junit.jupiter.api.Assertions.assertNotSame(cloned, exprStmt);
    }

    @org.junit.jupiter.api.Test
    void visitExplicitConstructorInvocation_withBlockComment_returnsEqualButNotSame() {
        CloneVisitor visitor = new CloneVisitor();
        com.github.javaparser.ast.comments.BlockComment contextComment = new com.github.javaparser.ast.comments.BlockComment();
        ExplicitConstructorInvocationStmt invocation = new ExplicitConstructorInvocationStmt();
        Visitable cloned = visitor.visit(invocation, contextComment);
        org.junit.jupiter.api.Assertions.assertTrue(cloned.equals(invocation));
        org.junit.jupiter.api.Assertions.assertNotSame(cloned, invocation);
    }

    @org.junit.jupiter.api.Test
    void visitBreakStmt_withObject_returnsEqualButNotSame() {
        CloneVisitor visitor = new CloneVisitor();
        java.lang.Object context = new java.lang.Object();
        BreakStmt breakStmt = new BreakStmt("&");
        Visitable cloned = visitor.visit(breakStmt, context);
        org.junit.jupiter.api.Assertions.assertNotSame(cloned, breakStmt);
        org.junit.jupiter.api.Assertions.assertTrue(cloned.equals(breakStmt));
    }

    @org.junit.jupiter.api.Test
    void visitBlockStmt_fromRecordInitializer_returnsClonedNode() {
        CloneVisitor visitor = new CloneVisitor();
        RecordDeclaration record = new RecordDeclaration();
        BlockStmt staticInitializer = record.addStaticInitializer();
        Visitable cloned = visitor.visit(staticInitializer, record);
        org.junit.jupiter.api.Assertions.assertNotSame(cloned, staticInitializer);
    }

    @org.junit.jupiter.api.Test
    void visitModuleUsesDirective_withNullContext_returnsEqualButNotSame() {
        CloneVisitor visitor = new CloneVisitor();
        com.github.javaparser.JavaToken token = new com.github.javaparser.JavaToken(0);
        com.github.javaparser.TokenRange range = new com.github.javaparser.TokenRange(token, token);
        Name serviceName = new Name();
        com.github.javaparser.ast.modules.ModuleUsesDirective usesDirective = new com.github.javaparser.ast.modules.ModuleUsesDirective(range, serviceName);
        Visitable cloned = visitor.visit(usesDirective, null);
        org.junit.jupiter.api.Assertions.assertTrue(cloned.equals(usesDirective));
        org.junit.jupiter.api.Assertions.assertNotSame(cloned, usesDirective);
    }

    @org.junit.jupiter.api.Test
    void visitModuleRequiresDirective_withNullContext_returnsEqualButNotSame() {
        CloneVisitor visitor = new CloneVisitor();
        com.github.javaparser.ast.modules.ModuleRequiresDirective requires = new com.github.javaparser.ast.modules.ModuleRequiresDirective();
        Visitable cloned = visitor.visit(requires, null);
        org.junit.jupiter.api.Assertions.assertTrue(cloned.equals(requires));
        org.junit.jupiter.api.Assertions.assertNotSame(cloned, requires);
    }

    @org.junit.jupiter.api.Test
    void visitVariableDeclarationExpr_withOwnListContext_returnsClonedNode() {
        CloneVisitor visitor = new CloneVisitor();
        VariableDeclarator[] vars = new VariableDeclarator[0];
        com.github.javaparser.ast.NodeList<VariableDeclarator> declarators = new com.github.javaparser.ast.NodeList<>(vars);
        VariableDeclarationExpr varExpr = new VariableDeclarationExpr(declarators);
        Visitable cloned = visitor.visit(varExpr, declarators);
        org.junit.jupiter.api.Assertions.assertNotSame(cloned, varExpr);
    }

    @org.junit.jupiter.api.Test
    void visitThisExpr_withObject_returnsEqualButNotSame() {
        CloneVisitor visitor = new CloneVisitor();
        Name qualifier = new Name();
        ThisExpr thisExpr = new ThisExpr(qualifier);
        java.lang.Object context = new java.lang.Object();
        Visitable cloned = visitor.visit(thisExpr, context);
        org.junit.jupiter.api.Assertions.assertNotSame(cloned, thisExpr);
        org.junit.jupiter.api.Assertions.assertTrue(cloned.equals(thisExpr));
    }

    @org.junit.jupiter.api.Test
    void visitTextBlockLiteralExpr_withBlockComment_returnsClonedNode() {
        com.github.javaparser.ast.comments.BlockComment contextComment = new com.github.javaparser.ast.comments.BlockComment();
        CloneVisitor visitor = new CloneVisitor();
        TextBlockLiteralExpr textBlock = new TextBlockLiteralExpr();
        Visitable cloned = visitor.visit(textBlock, contextComment);
        org.junit.jupiter.api.Assertions.assertNotSame(cloned, textBlock);
    }

    @org.junit.jupiter.api.Test
    void visitSimpleName_withObject_returnsClonedNode() {
        CloneVisitor visitor = new CloneVisitor();
        java.lang.Object context = new java.lang.Object();
        SimpleName simpleName = new SimpleName();
        Visitable cloned = visitor.visit(simpleName, context);
        org.junit.jupiter.api.Assertions.assertNotSame(cloned, simpleName);
    }

    @org.junit.jupiter.api.Test
    void visitNameExpr_withNullContext_returnsClonedNode() {
        CloneVisitor visitor = new CloneVisitor();
        SimpleName identifier = new SimpleName();
        NameExpr nameExpr = new NameExpr(identifier);
        Visitable cloned = visitor.visit(nameExpr, null);
        org.junit.jupiter.api.Assertions.assertNotSame(cloned, nameExpr);
    }

    @org.junit.jupiter.api.Test
    void visitMatchAllPatternExpr_withModifiers_returnsEqualButNotSame() {
        CloneVisitor visitor = new CloneVisitor();
        com.github.javaparser.ast.comments.BlockComment contextComment = new com.github.javaparser.ast.comments.BlockComment();
        com.github.javaparser.ast.Modifier[] modifierKeywords = new com.github.javaparser.ast.Modifier.Keyword[4];
        com.github.javaparser.ast.Modifier.Keyword volatileKeyword = Modifier.Keyword.VOLATILE;
        modifierKeywords[0] = volatileKeyword;
        modifierKeywords[1] = modifierKeywords[0];
        modifierKeywords[2] = modifierKeywords[1];
        modifierKeywords[3] = modifierKeywords[2];
        com.github.javaparser.ast.NodeList<com.github.javaparser.ast.Modifier> modifiers = com.github.javaparser.ast.Modifier.createModifierList(modifierKeywords);
        MatchAllPatternExpr pattern = new MatchAllPatternExpr(modifiers);
        Visitable cloned = visitor.visit(pattern, contextComment);
        org.junit.jupiter.api.Assertions.assertNotSame(cloned, pattern);
        org.junit.jupiter.api.Assertions.assertTrue(cloned.equals(pattern));
    }

    @org.junit.jupiter.api.Test
    void visitLongLiteralExpr_withNullContext_returnsClonedNode() {
        CloneVisitor visitor = new CloneVisitor();
        LongLiteralExpr literal = new LongLiteralExpr("\"class\"");
        Visitable cloned = visitor.visit(literal, null);
        org.junit.jupiter.api.Assertions.assertNotSame(cloned, literal);
    }

    @org.junit.jupiter.api.Test
    void visitInstanceOfExpr_withBlockComment_returnsEqualButNotSame() {
        CloneVisitor visitor = new CloneVisitor();
        InstanceOfExpr instanceOfExpr = new InstanceOfExpr();
        com.github.javaparser.ast.comments.BlockComment contextComment = new com.github.javaparser.ast.comments.BlockComment();
        Visitable cloned = visitor.visit(instanceOfExpr, contextComment);
        org.junit.jupiter.api.Assertions.assertTrue(cloned.equals(instanceOfExpr));
        org.junit.jupiter.api.Assertions.assertNotSame(cloned, instanceOfExpr);
    }

    @org.junit.jupiter.api.Test
    void visitFieldAccessExpr_withObject_returnsClonedNode() {
        CloneVisitor visitor = new CloneVisitor();
        java.lang.Object context = new java.lang.Object();
        FieldAccessExpr fieldAccess = new FieldAccessExpr();
        Visitable cloned = visitor.visit(fieldAccess, context);
        org.junit.jupiter.api.Assertions.assertNotSame(cloned, fieldAccess);
    }

    @org.junit.jupiter.api.Test
    void visitEnclosedExpr_withBlockComment_returnsClonedNode() {
        com.github.javaparser.ast.comments.BlockComment contextComment = new com.github.javaparser.ast.comments.BlockComment();
        CloneVisitor visitor = new CloneVisitor();
        EnclosedExpr enclosed = new EnclosedExpr();
        Visitable cloned = visitor.visit(enclosed, contextComment);
        org.junit.jupiter.api.Assertions.assertNotSame(cloned, enclosed);
    }

    @org.junit.jupiter.api.Test
    void visitConditionalExpr_withObject_returnsEqualButNotSame() {
        CloneVisitor visitor = new CloneVisitor();
        ConditionalExpr conditional = new ConditionalExpr();
        java.lang.Object context = new java.lang.Object();
        Visitable cloned = visitor.visit(conditional, context);
        org.junit.jupiter.api.Assertions.assertNotSame(cloned, conditional);
        org.junit.jupiter.api.Assertions.assertTrue(cloned.equals(conditional));
    }

    @org.junit.jupiter.api.Test
    void visitClassExpr_withBlockCommentListContext_returnsClonedNode() {
        CloneVisitor visitor = new CloneVisitor();
        TypeParameter typeParam = new TypeParameter("4)oF?x7_I'eKf+j`G<~");
        ClassExpr classExpr = new ClassExpr(typeParam);
        com.github.javaparser.ast.comments.BlockComment[] blockComments = new com.github.javaparser.ast.comments.BlockComment[4];
        com.github.javaparser.ast.NodeList<com.github.javaparser.ast.comments.BlockComment> contextComments = new com.github.javaparser.ast.NodeList<>(blockComments);
        Visitable cloned = visitor.visit(classExpr, contextComments);
        org.junit.jupiter.api.Assertions.assertNotSame(cloned, classExpr);
    }

    @org.junit.jupiter.api.Test
    void visitCharLiteralExpr_withBlockCommentListContext_returnsClonedNode() {
        CloneVisitor visitor = new CloneVisitor();
        CharLiteralExpr charLiteral = CharLiteralExpr.escape("");
        com.github.javaparser.ast.NodeList<com.github.javaparser.ast.comments.BlockComment> comments = new com.github.javaparser.ast.NodeList<>();
        Visitable cloned = visitor.visit(charLiteral, comments);
        org.junit.jupiter.api.Assertions.assertNotSame(cloned, charLiteral);
    }

    @org.junit.jupiter.api.Test
    void visitBooleanLiteralExpr_withBlockComment_returnsClonedNode() {
        CloneVisitor visitor = new CloneVisitor();
        BooleanLiteralExpr literal = new BooleanLiteralExpr(true);
        com.github.javaparser.ast.comments.BlockComment contextComment = new com.github.javaparser.ast.comments.BlockComment("JAVA_1_0");
        Visitable cloned = visitor.visit(literal, contextComment);
        org.junit.jupiter.api.Assertions.assertNotSame(cloned, literal);
    }

    @org.junit.jupiter.api.Test
    void visitBinaryExpr_withObject_returnsClonedNode() {
        CloneVisitor visitor = new CloneVisitor();
        BinaryExpr binaryExpr = new BinaryExpr();
        java.lang.Object context = new java.lang.Object();
        Visitable cloned = visitor.visit(binaryExpr, context);
        org.junit.jupiter.api.Assertions.assertNotSame(cloned, binaryExpr);
    }

    @org.junit.jupiter.api.Test
    void visitArrayCreationExpr_withObject_returnsEqualButNotSame() {
        CloneVisitor visitor = new CloneVisitor();
        UnknownType unknown = new UnknownType();
        java.lang.Object context = new java.lang.Object();
        com.github.javaparser.ast.NodeList<com.github.javaparser.ast.ArrayCreationLevel> levels = new com.github.javaparser.ast.NodeList<>();
        ArrayInitializerExpr initializer = new ArrayInitializerExpr();
        ArrayCreationExpr arrayCreation = new ArrayCreationExpr(unknown, levels, initializer);
        Visitable cloned = visitor.visit(arrayCreation, context);
        org.junit.jupiter.api.Assertions.assertTrue(cloned.equals(arrayCreation));
        org.junit.jupiter.api.Assertions.assertNotSame(cloned, arrayCreation);
    }

    @org.junit.jupiter.api.Test
    void visitLineComment_withObject_returnsClonedNode() {
        CloneVisitor visitor = new CloneVisitor();
        com.github.javaparser.TokenRange invalid = com.github.javaparser.TokenRange.INVALID;
        com.github.javaparser.ast.comments.LineComment lineComment = new com.github.javaparser.ast.comments.LineComment(invalid, "w*gWr 2b\u0007:X");
        java.lang.Object context = new java.lang.Object();
        Visitable cloned = visitor.visit(lineComment, context);
        org.junit.jupiter.api.Assertions.assertNotSame(cloned, lineComment);
    }

    @org.junit.jupiter.api.Test
    void visitBlockComment_withBlockCommentListContext_returnsClonedNode() {
        CloneVisitor visitor = new CloneVisitor();
        com.github.javaparser.ast.comments.BlockComment blockComment = new com.github.javaparser.ast.comments.BlockComment("");
        com.github.javaparser.ast.comments.BlockComment[] blockComments = new com.github.javaparser.ast.comments.BlockComment[8];
        com.github.javaparser.ast.NodeList<com.github.javaparser.ast.comments.BlockComment> context = com.github.javaparser.ast.NodeList.nodeList(blockComments);
        Visitable cloned = visitor.visit(blockComment, context);
        org.junit.jupiter.api.Assertions.assertNotSame(cloned, blockComment);
    }

    @org.junit.jupiter.api.Test
    void visitVariableDeclarator_withObject_returnsEqualButNotSame() {
        CloneVisitor visitor = new CloneVisitor();
        VariableDeclarator variable = new VariableDeclarator();
        java.lang.Object context = new java.lang.Object();
        Visitable cloned = visitor.visit(variable, context);
        org.junit.jupiter.api.Assertions.assertTrue(cloned.equals(variable));
        org.junit.jupiter.api.Assertions.assertNotSame(cloned, variable);
    }

    @org.junit.jupiter.api.Test
    void visitReceiverParameter_withWhileStmtListContext_returnsEqualButNotSame() {
        CloneVisitor visitor = new CloneVisitor();
        com.github.javaparser.TokenRange invalid = com.github.javaparser.TokenRange.INVALID;
        UnknownType receiverType = new UnknownType(invalid);
        ReceiverParameter receiver = new ReceiverParameter(receiverType, "\"<<\"");
        com.github.javaparser.ast.NodeList<WhileStmt> context = new com.github.javaparser.ast.NodeList<>();
        Visitable cloned = visitor.visit(receiver, context);
        org.junit.jupiter.api.Assertions.assertNotSame(cloned, receiver);
        org.junit.jupiter.api.Assertions.assertTrue(cloned.equals(receiver));
    }

    @org.junit.jupiter.api.Test
    void visitEnumDeclaration_withBlockComment_returnsClonedNode() {
        CloneVisitor visitor = new CloneVisitor();
        com.github.javaparser.ast.comments.BlockComment contextComment = new com.github.javaparser.ast.comments.BlockComment();
        EnumDeclaration enumDecl = new EnumDeclaration();
        Visitable cloned = visitor.visit(enumDecl, contextComment);
        org.junit.jupiter.api.Assertions.assertNotSame(cloned, enumDecl);
    }

    @org.junit.jupiter.api.Test
    void visitCompactConstructorDeclaration_withNullContext_returnsClonedNode() {
        CloneVisitor visitor = new CloneVisitor();
        CompactConstructorDeclaration compactCtor = new CompactConstructorDeclaration();
        Visitable cloned = visitor.visit(compactCtor, null);
        org.junit.jupiter.api.Assertions.assertNotSame(cloned, compactCtor);
    }

    @org.junit.jupiter.api.Test
    void visitAnnotationMemberDeclaration_withNullContext_returnsEqualButNotSame() {
        CloneVisitor visitor = new CloneVisitor();
        AnnotationMemberDeclaration member = new AnnotationMemberDeclaration();
        Visitable cloned = visitor.visit(member, null);
        org.junit.jupiter.api.Assertions.assertTrue(cloned.equals(member));
        org.junit.jupiter.api.Assertions.assertNotSame(cloned, member);
    }

    @org.junit.jupiter.api.Test
    void visitAnnotationDeclaration_withObject_returnsEqualButNotSame() {
        CloneVisitor visitor = new CloneVisitor();
        com.github.javaparser.ast.Modifier[] modifiersArr = new com.github.javaparser.ast.Modifier[6];
        com.github.javaparser.ast.Modifier modifier = new com.github.javaparser.ast.Modifier();
        modifiersArr[0] = modifier;
        modifiersArr[1] = modifiersArr[0];
        modifiersArr[2] = modifiersArr[1];
        modifiersArr[3] = modifiersArr[1];
        modifiersArr[4] = modifier;
        modifiersArr[5] = modifiersArr[2];
        com.github.javaparser.ast.NodeList<com.github.javaparser.ast.Modifier> modifiers = com.github.javaparser.ast.NodeList.nodeList(modifiersArr);
        AnnotationDeclaration annotationDecl = new AnnotationDeclaration(modifiers, "g/hJ7smB#{,MTz%");
        java.lang.Object context = new java.lang.Object();
        Visitable cloned = visitor.visit(annotationDecl, context);
        org.junit.jupiter.api.Assertions.assertTrue(cloned.equals(annotationDecl));
        org.junit.jupiter.api.Assertions.assertNotSame(cloned, annotationDecl);
    }

    @org.junit.jupiter.api.Test
    void visitImportDeclaration_withBlockComment_returnsEqualButNotSameNode() {
        CloneVisitor visitor = new CloneVisitor();
        com.github.javaparser.ast.comments.BlockComment contextComment = new com.github.javaparser.ast.comments.BlockComment();
        com.github.javaparser.JavaToken token = new com.github.javaparser.JavaToken(1, "");
        com.github.javaparser.TokenRange range = new com.github.javaparser.TokenRange(token, token);
        Name name = new Name();
        com.github.javaparser.ast.ImportDeclaration importDecl = new com.github.javaparser.ast.ImportDeclaration(range, name, false, false, false);
        com.github.javaparser.ast.Node cloned = visitor.visit(importDecl, contextComment);
        org.junit.jupiter.api.Assertions.assertNotSame(cloned, importDecl);
        org.junit.jupiter.api.Assertions.assertTrue(cloned.equals(importDecl));
    }

    @org.junit.jupiter.api.Test
    void cloneNode_withEmptyOptional_returnsNull() {
        CloneVisitor visitor = new CloneVisitor();
        com.github.javaparser.ast.Modifier[] modifiersArr = new com.github.javaparser.ast.Modifier[2];
        com.github.javaparser.ast.Modifier sync = com.github.javaparser.ast.Modifier.synchronizedModifier();
        modifiersArr[0] = sync;
        modifiersArr[1] = sync;
        com.github.javaparser.ast.NodeList<com.github.javaparser.ast.Modifier> modifiers = com.github.javaparser.ast.NodeList.nodeList(modifiersArr);
        RecordDeclaration record = new RecordDeclaration(modifiers, "/e>G`0p");
        java.util.Optional<MethodDeclaration> maybeMethod = record.toMethodDeclaration();
        java.lang.Object context = new java.lang.Object();
        MethodDeclaration cloned = visitor.cloneNode(maybeMethod, context);
        org.junit.jupiter.api.Assertions.assertNull(cloned);
    }

    @org.junit.jupiter.api.Test
    void cloneNode_withPresentOptional_returnsNewInstance() {
        CloneVisitor visitor = new CloneVisitor();
        YieldStmt yieldStmt = new YieldStmt();
        java.util.Optional<YieldStmt> maybeYield = yieldStmt.toYieldStmt();
        YieldStmt cloned = visitor.cloneNode(maybeYield, yieldStmt);
        org.junit.jupiter.api.Assertions.assertNotSame(cloned, yieldStmt);
        org.junit.jupiter.api.Assertions.assertNotNull(cloned);
    }

    @org.junit.jupiter.api.Test
    void clone_onContinueStmt_returnsDeepCopy() {
        ContinueStmt original = new ContinueStmt("%s is not MethodCallExpr, it is %s");
        ContinueStmt cloned = original.clone();
        org.junit.jupiter.api.Assertions.assertNotSame(cloned, original);
        org.junit.jupiter.api.Assertions.assertTrue(cloned.equals(original));
    }

    @org.junit.jupiter.api.Test
    void visitNodeList_withBlockComment_returnsListOfSameSize() {
        CloneVisitor visitor = new CloneVisitor();
        com.github.javaparser.ast.NodeList<com.github.javaparser.ast.comments.BlockComment> comments = new com.github.javaparser.ast.NodeList<>();
        comments.add(new com.github.javaparser.ast.comments.BlockComment("a"));
        comments.add(new com.github.javaparser.ast.comments.BlockComment("b"));
        com.github.javaparser.ast.comments.BlockComment contextComment = new com.github.javaparser.ast.comments.BlockComment();
        com.github.javaparser.ast.NodeList<com.github.javaparser.ast.comments.BlockComment> cloned = ((com.github.javaparser.ast.NodeList<com.github.javaparser.ast.comments.BlockComment>) (visitor.visit(comments, contextComment)));
        org.junit.jupiter.api.Assertions.assertEquals(comments.size(), cloned.size());
        org.junit.jupiter.api.Assertions.assertNotSame(cloned, comments);
    }
}