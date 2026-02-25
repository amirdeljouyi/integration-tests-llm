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
import static org.junit.jupiter.api.Assertions.*;

import com.github.javaparser.JavaToken;
import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.modules.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.*;
import com.github.javaparser.ast.type.Type;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CloneVisitor_ESTest_Adopted {
    CompilationUnit cu;

    @BeforeEach
    void setUp() {
        cu = new CompilationUnit();
    }

    @AfterEach
    void teardown() {
        cu = null;
    }

    @Test
    void cloneJavaDocTest() {
        NodeList<BodyDeclaration<?>> bodyDeclarationList = new NodeList<>();
        bodyDeclarationList.add(new AnnotationMemberDeclaration().setJavadocComment("javadoc"));
        bodyDeclarationList.add(new ConstructorDeclaration().setJavadocComment("javadoc"));
        bodyDeclarationList.add(new EnumConstantDeclaration().setJavadocComment("javadoc"));
        bodyDeclarationList.add(new FieldDeclaration().setJavadocComment("javadoc"));
        bodyDeclarationList.add(new InitializerDeclaration().setJavadocComment("javadoc"));
        bodyDeclarationList.add(new MethodDeclaration().setJavadocComment("javadoc"));

        NodeList<TypeDeclaration<?>> typeDeclarationList = new NodeList<>();
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
        CompilationUnit cuClone = (CompilationUnit) new CloneVisitor().visit(cu, null);

        NodeList<TypeDeclaration<?>> typeDeclarationListClone = cuClone.getTypes();
        Iterator<TypeDeclaration<?>> typeItr = typeDeclarationListClone.iterator();
        TypeDeclaration<?> typeDeclarationCloned;
        while (typeItr.hasNext()) {
            typeDeclarationCloned = typeItr.next();
            if (typeDeclarationCloned.getMembers() == null) {
                assertEquals("javadoc", typeDeclarationCloned.getComment().get().getContent());
            } else {
                Iterator<BodyDeclaration<?>> bodyItr =
                        typeDeclarationCloned.getMembers().iterator();
                while (bodyItr.hasNext()) {
                    BodyDeclaration<?> bodyDeclaration = bodyItr.next();
                    assertEquals("javadoc", bodyDeclaration.getComment().get().getContent());
                }
            }
        }
    }

    @Test
    void cloneAnnotationOnWildcardTypeArgument() {
        Type type = parseType("List<@C ? extends Object>").clone();
        assertEquals("List<@C ? extends Object>", type.toString());
    }

    @Test
    void visitWildcardType_returnsEqualButDistinctInstance() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        TypeParameter bound = new TypeParameter("]0Cjw}+`@oK`WO2B");
        NodeList<AnnotationExpr> annotations = new NodeList<AnnotationExpr>();
        WildcardType wildcardType = new WildcardType(bound, bound, annotations);
        BlockComment context = new BlockComment();
        Visitable cloned = visitor.visit(wildcardType, ((Object) (context)));
        assertTrue(cloned.equals(((Object) (wildcardType))));
        assertNotSame(cloned, wildcardType);
    }

    @Test
    void visitVarType_returnsDistinctInstance() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        VarType varType = new VarType();
        Object context = new Object();
        Visitable cloned = visitor.visit(varType, context);
        assertNotSame(cloned, varType);
    }

    @Test
    void visitUnionType_returnsDistinctInstance() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        UnionType unionType = new UnionType();
        Object context = new Object();
        Visitable cloned = visitor.visit(unionType, context);
        assertNotSame(cloned, unionType);
    }

    @Test
    void visitPrimitiveType_returnsDistinctInstance() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        Object context = new Object();
        PrimitiveType shortType = PrimitiveType.shortType();
        Visitable cloned = visitor.visit(shortType, context);
        assertNotSame(cloned, shortType);
    }

    @Test
    void visitIntersectionType_returnsDistinctInstance() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        NodeList<ReferenceType> elements = new NodeList<ReferenceType>();
        IntersectionType intersectionType = new IntersectionType(elements);
        Object context = new Object();
        Visitable cloned = visitor.visit(intersectionType, context);
        assertNotSame(cloned, intersectionType);
    }

    @Test
    void visitYieldStmt_withBlockComment_returnsDistinctInstance() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        YieldStmt yieldStmt = new YieldStmt();
        JavaToken invalid = JavaToken.INVALID;
        TokenRange range = new TokenRange(invalid, invalid);
        BlockComment context = new BlockComment(range, "Corresponding declaration not available for unsolved symbol.");
        Visitable cloned = visitor.visit(yieldStmt, ((Object) (context)));
        assertNotSame(cloned, yieldStmt);
    }

    @Test
    void visitTryStmt_returnsEqualButDistinctInstance() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        TryStmt tryStmt = new TryStmt();
        Object context = new Object();
        Visitable cloned = visitor.visit(tryStmt, context);
        assertTrue(cloned.equals(((Object) (tryStmt))));
        assertNotSame(cloned, tryStmt);
    }

    @Test
    void visitThrowStmt_returnsDistinctInstance() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        ThrowStmt throwStmt = new ThrowStmt();
        Visitable cloned = visitor.visit(throwStmt, ((Object) (throwStmt)));
        assertNotSame(cloned, throwStmt);
    }

    @Test
    void visitSynchronizedStmt_returnsDistinctInstance() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        BlockComment context = new BlockComment();
        SynchronizedStmt syncStmt = new SynchronizedStmt();
        Visitable cloned = visitor.visit(syncStmt, ((Object) (context)));
        assertNotSame(cloned, syncStmt);
    }

    @Test
    void visitSwitchEntry_returnsDistinctInstance() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        JavaToken invalid = JavaToken.INVALID;
        TokenRange range = new TokenRange(invalid, invalid);
        NodeList<Expression> labels = new NodeList<Expression>();
        SwitchEntry.Type type = SwitchEntry.Type.STATEMENT_GROUP;
        LinkedList<Statement> statementsList = new LinkedList<Statement>();
        NodeList<Statement> statements = NodeList.nodeList(statementsList);
        SwitchEntry switchEntry = new SwitchEntry(range, labels, type, statements);
        Visitable cloned = visitor.visit(switchEntry, ((Object) (labels)));
        assertNotSame(cloned, switchEntry);
    }

    @Test
    void visitReturnStmt_returnsDistinctInstance() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        ReturnStmt returnStmt = new ReturnStmt();
        Visitable cloned = visitor.visit(returnStmt, ((Object) (returnStmt)));
        assertNotSame(cloned, returnStmt);
    }

    @Test
    void visitLabeledStmt_returnsDistinctInstance() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        Modifier[] modifiersArray = new Modifier[3];
        NodeList<Modifier> modifiers = new NodeList<Modifier>(modifiersArray);
        LabeledStmt labeledStmt = new LabeledStmt();
        Visitable cloned = visitor.visit(labeledStmt, ((Object) (modifiers)));
        assertNotSame(cloned, labeledStmt);
    }

    @Test
    void visitIfStmt_returnsCloneWithoutElse() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        IfStmt original = new IfStmt();
        Object context = new Object();
        IfStmt cloned = ((IfStmt) (visitor.visit(original, context)));
        assertFalse(cloned.hasElseBranch());
        assertNotSame(cloned, original);
    }

    @Test
    void visitForStmt_returnsEqualButDistinctInstance() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        ForStmt forStmt = new ForStmt();
        BlockComment[] emptyComments = new BlockComment[0];
        NodeList<BlockComment> comments = new NodeList<BlockComment>(emptyComments);
        Visitable cloned = visitor.visit(forStmt, ((Object) (comments)));
        assertNotSame(cloned, forStmt);
        assertTrue(cloned.equals(((Object) (forStmt))));
    }

    @Test
    void visitForEachStmt_returnsDistinctInstance() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        ForEachStmt forEachStmt = new ForEachStmt();
        Object context = new Object();
        Visitable cloned = visitor.visit(forEachStmt, context);
        assertNotSame(cloned, forEachStmt);
    }

    @Test
    void visitExpressionStmt_returnsDistinctInstance() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        ExpressionStmt expressionStmt = new ExpressionStmt();
        Object context = new Object();
        Visitable cloned = visitor.visit(expressionStmt, context);
        assertNotSame(cloned, expressionStmt);
    }

    @Test
    void visitExplicitConstructorInvocation_returnsEqualButDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        BlockComment context = new BlockComment();
        ExplicitConstructorInvocationStmt invocation = new ExplicitConstructorInvocationStmt();
        Visitable cloned = visitor.visit(invocation, ((Object) (context)));
        assertTrue(cloned.equals(((Object) (invocation))));
        assertNotSame(cloned, invocation);
    }

    @Test
    void visitBreakStmt_returnsEqualButDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        Object context = new Object();
        BreakStmt breakStmt = new BreakStmt("&");
        Visitable cloned = visitor.visit(breakStmt, context);
        assertNotSame(cloned, breakStmt);
        assertTrue(cloned.equals(((Object) (breakStmt))));
    }

    @Test
    void visitBlockStmt_fromRecordInitializer_returnsDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        RecordDeclaration record = new RecordDeclaration();
        BlockStmt staticInitializer = record.addStaticInitializer();
        Visitable cloned = visitor.visit(staticInitializer, ((Object) (record)));
        assertNotSame(cloned, staticInitializer);
    }

    @Test
    void visitModuleUsesDirective_returnsEqualButDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        JavaToken token = new JavaToken(0);
        TokenRange range = new TokenRange(token, token);
        Name service = new Name();
        ModuleUsesDirective usesDirective = new ModuleUsesDirective(range, service);
        Visitable cloned = visitor.visit(usesDirective, ((Object) (null)));
        assertTrue(cloned.equals(((Object) (usesDirective))));
        assertNotSame(cloned, usesDirective);
    }

    @Test
    void visitModuleRequiresDirective_returnsEqualButDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        ModuleRequiresDirective requiresDirective = new ModuleRequiresDirective();
        Visitable cloned = visitor.visit(requiresDirective, ((Object) (null)));
        assertTrue(cloned.equals(((Object) (requiresDirective))));
        assertNotSame(cloned, requiresDirective);
    }

    @Test
    void visitVariableDeclarationExpr_returnsDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        VariableDeclarator[] emptyDeclarators = new VariableDeclarator[0];
        NodeList<VariableDeclarator> variables = new NodeList<VariableDeclarator>(emptyDeclarators);
        VariableDeclarationExpr varDeclExpr = new VariableDeclarationExpr(variables);
        Visitable cloned = visitor.visit(varDeclExpr, ((Object) (variables)));
        assertNotSame(cloned, varDeclExpr);
    }

    @Test
    void visitThisExpr_returnsEqualButDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        Name qualifier = new Name();
        ThisExpr thisExpr = new ThisExpr(qualifier);
        Object context = new Object();
        Visitable cloned = visitor.visit(thisExpr, context);
        assertNotSame(cloned, thisExpr);
        assertTrue(cloned.equals(((Object) (thisExpr))));
    }

    @Test
    void visitTextBlockLiteralExpr_returnsDistinct() throws Throwable {
        BlockComment context = new BlockComment();
        CloneVisitor visitor = new CloneVisitor();
        TextBlockLiteralExpr textBlock = new TextBlockLiteralExpr();
        Visitable cloned = visitor.visit(textBlock, ((Object) (context)));
        assertNotSame(cloned, textBlock);
    }

    @Test
    void visitSimpleName_returnsDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        Object context = new Object();
        SimpleName simpleName = new SimpleName();
        Visitable cloned = visitor.visit(simpleName, context);
        assertNotSame(cloned, simpleName);
    }

    @Test
    void visitNameExpr_returnsDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        SimpleName identifier = new SimpleName();
        NameExpr nameExpr = new NameExpr(identifier);
        Visitable cloned = visitor.visit(nameExpr, ((Object) (null)));
        assertNotSame(cloned, nameExpr);
    }

    @Test
    void visitMatchAllPatternExpr_returnsEqualButDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        BlockComment context = new BlockComment();
        Modifier.Keyword[] keywords = new Modifier.Keyword[4];
        Modifier.Keyword volatileKeyword = Modifier.Keyword.VOLATILE;
        keywords[0] = volatileKeyword;
        keywords[1] = keywords[0];
        keywords[2] = keywords[1];
        keywords[3] = keywords[2];
        NodeList<Modifier> modifiers = Modifier.createModifierList(keywords);
        MatchAllPatternExpr pattern = new MatchAllPatternExpr(modifiers);
        Visitable cloned = visitor.visit(pattern, ((Object) (context)));
        assertNotSame(cloned, pattern);
        assertTrue(cloned.equals(((Object) (pattern))));
    }

    @Test
    void visitLongLiteralExpr_returnsDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        LongLiteralExpr value = new LongLiteralExpr("\"class\"");
        Visitable cloned = visitor.visit(value, ((Object) (null)));
        assertNotSame(cloned, value);
    }

    @Test
    void visitInstanceOfExpr_returnsEqualButDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        InstanceOfExpr instanceOfExpr = new InstanceOfExpr();
        BlockComment context = new BlockComment();
        Visitable cloned = visitor.visit(instanceOfExpr, ((Object) (context)));
        assertTrue(cloned.equals(((Object) (instanceOfExpr))));
        assertNotSame(cloned, instanceOfExpr);
    }

    @Test
    void visitFieldAccessExpr_returnsDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        Object context = new Object();
        FieldAccessExpr fieldAccess = new FieldAccessExpr();
        Visitable cloned = visitor.visit(fieldAccess, context);
        assertNotSame(cloned, fieldAccess);
    }

    @Test
    void visitEnclosedExpr_returnsDistinct() throws Throwable {
        BlockComment context = new BlockComment();
        CloneVisitor visitor = new CloneVisitor();
        EnclosedExpr enclosedExpr = new EnclosedExpr();
        Visitable cloned = visitor.visit(enclosedExpr, ((Object) (context)));
        assertNotSame(cloned, enclosedExpr);
    }

    @Test
    void visitConditionalExpr_returnsEqualButDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        ConditionalExpr conditional = new ConditionalExpr();
        Object context = new Object();
        Visitable cloned = visitor.visit(conditional, context);
        assertNotSame(cloned, conditional);
        assertTrue(cloned.equals(((Object) (conditional))));
    }

    @Test
    void visitClassExpr_returnsDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        TypeParameter typeParam = new TypeParameter("4)oF?x7_I'eKf+j`G<~");
        ClassExpr classExpr = new ClassExpr(typeParam);
        BlockComment[] emptyComments = new BlockComment[4];
        NodeList<BlockComment> comments = new NodeList<BlockComment>(emptyComments);
        Visitable cloned = visitor.visit(classExpr, ((Object) (comments)));
        assertNotSame(cloned, classExpr);
    }

    @Test
    void visitCharLiteralExpr_returnsDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        CharLiteralExpr charLiteral = CharLiteralExpr.escape("");
        NodeList<BlockComment> comments = new NodeList<BlockComment>();
        Visitable cloned = visitor.visit(charLiteral, ((Object) (comments)));
        assertNotSame(cloned, charLiteral);
    }

    @Test
    void visitBooleanLiteralExpr_returnsDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        BooleanLiteralExpr booleanLiteral = new BooleanLiteralExpr(true);
        BlockComment context = new BlockComment("JAVA_1_0");
        Visitable cloned = visitor.visit(booleanLiteral, ((Object) (context)));
        assertNotSame(cloned, booleanLiteral);
    }

    @Test
    void visitBinaryExpr_returnsDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        BinaryExpr binaryExpr = new BinaryExpr();
        Object context = new Object();
        Visitable cloned = visitor.visit(binaryExpr, context);
        assertNotSame(cloned, binaryExpr);
    }

    @Test
    void visitArrayCreationExpr_returnsEqualButDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        UnknownType elementType = new UnknownType();
        Object context = new Object();
        NodeList<ArrayCreationLevel> levels = new NodeList<ArrayCreationLevel>();
        ArrayInitializerExpr initializer = new ArrayInitializerExpr();
        ArrayCreationExpr arrayCreation = new ArrayCreationExpr(elementType, levels, initializer);
        Visitable cloned = visitor.visit(arrayCreation, context);
        assertTrue(cloned.equals(((Object) (arrayCreation))));
        assertNotSame(cloned, arrayCreation);
    }

    @Test
    void visitLineComment_returnsDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        TokenRange invalid = TokenRange.INVALID;
        LineComment lineComment = new LineComment(invalid, "w*gWr 2b\u0007:X");
        Object context = new Object();
        Visitable cloned = visitor.visit(lineComment, context);
        assertNotSame(cloned, lineComment);
    }

    @Test
    void visitBlockComment_returnsDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        BlockComment comment = new BlockComment("");
        BlockComment[] commentArray = new BlockComment[8];
        NodeList<BlockComment> comments = NodeList.nodeList(commentArray);
        Visitable cloned = visitor.visit(comment, ((Object) (comments)));
        assertNotSame(cloned, comment);
    }

    @Test
    void visitVariableDeclarator_returnsEqualButDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        VariableDeclarator variable = new VariableDeclarator();
        Object context = new Object();
        Visitable cloned = visitor.visit(variable, context);
        assertTrue(cloned.equals(((Object) (variable))));
        assertNotSame(cloned, variable);
    }

    @Test
    void visitReceiverParameter_returnsEqualButDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        TokenRange invalid = TokenRange.INVALID;
        UnknownType type = new UnknownType(invalid);
        ReceiverParameter receiver = new ReceiverParameter(type, "\"<<\"");
        NodeList<WhileStmt> whileStmts = new NodeList<WhileStmt>();
        Visitable cloned = visitor.visit(receiver, ((Object) (whileStmts)));
        assertNotSame(cloned, receiver);
        assertTrue(cloned.equals(((Object) (receiver))));
    }

    @Test
    void visitEnumDeclaration_returnsDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        BlockComment context = new BlockComment();
        EnumDeclaration enumDecl = new EnumDeclaration();
        Visitable cloned = visitor.visit(enumDecl, ((Object) (context)));
        assertNotSame(cloned, enumDecl);
    }

    @Test
    void visitCompactConstructorDeclaration_returnsDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        CompactConstructorDeclaration compactCtor = new CompactConstructorDeclaration();
        Visitable cloned = visitor.visit(compactCtor, ((Object) (null)));
        assertNotSame(cloned, compactCtor);
    }

    @Test
    void visitAnnotationMemberDeclaration_returnsEqualButDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        AnnotationMemberDeclaration memberDecl = new AnnotationMemberDeclaration();
        Visitable cloned = visitor.visit(memberDecl, ((Object) (null)));
        assertTrue(cloned.equals(((Object) (memberDecl))));
        assertNotSame(cloned, memberDecl);
    }

    @Test
    void visitAnnotationDeclaration_returnsEqualButDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        Modifier[] modifiersArray = new Modifier[6];
        Modifier modifier = new Modifier();
        modifiersArray[0] = modifier;
        modifiersArray[1] = modifiersArray[0];
        modifiersArray[2] = modifiersArray[1];
        modifiersArray[3] = modifiersArray[1];
        modifiersArray[4] = modifier;
        modifiersArray[5] = modifiersArray[2];
        NodeList<Modifier> modifiers = NodeList.nodeList(modifiersArray);
        AnnotationDeclaration annotationDecl = new AnnotationDeclaration(modifiers, "g/hJ7smB#{,MTz%");
        Object context = new Object();
        Visitable cloned = visitor.visit(annotationDecl, context);
        assertTrue(cloned.equals(((Object) (annotationDecl))));
        assertNotSame(cloned, annotationDecl);
    }

    @Test
    void visitImportDeclaration_returnsEqualButDistinctAsNode() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        BlockComment context = new BlockComment();
        JavaToken token = new JavaToken(1, "");
        TokenRange range = new TokenRange(token, token);
        Name name = new Name();
        ImportDeclaration importDecl = new ImportDeclaration(range, name, false, false, false);
        Node cloned = visitor.visit(importDecl, ((Object) (context)));
        assertNotSame(cloned, importDecl);
        assertTrue(cloned.equals(((Object) (importDecl))));
    }

    @Test
    void cloneNode_optionalMethodDeclaration_returnsNull() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        Modifier[] modifiersArray = new Modifier[2];
        Modifier sync = Modifier.synchronizedModifier();
        modifiersArray[0] = sync;
        modifiersArray[1] = sync;
        NodeList<Modifier> modifiers = NodeList.nodeList(modifiersArray);
        RecordDeclaration record = new RecordDeclaration(modifiers, "/e>G`0p");
        Optional<MethodDeclaration> maybeMethod = record.toMethodDeclaration();
        Object context = new Object();
        MethodDeclaration result = visitor.cloneNode(maybeMethod, context);
        assertNull(result);
    }

    @Test
    void cloneNode_optionalYieldStmt_returnsClone() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        YieldStmt yieldStmt = new YieldStmt();
        Optional<YieldStmt> maybeYield = yieldStmt.toYieldStmt();
        YieldStmt clone = visitor.cloneNode(maybeYield, yieldStmt);
        assertNotSame(clone, yieldStmt);
        assertNotNull(clone);
    }

    @Test
    void continueStmt_clone_returnsEqualButDistinct() throws Throwable {
        ContinueStmt original = new ContinueStmt("%s is not MethodCallExpr, it is %s");
        ContinueStmt clone = original.clone();
        assertNotSame(clone, original);
        assertTrue(clone.equals(((Object) (original))));
    }

    @Test
    void visitNodeList_blockComment_returnsSizeUnchanged() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        NodeList<BlockComment> comments = new NodeList<BlockComment>();
        BlockComment comment = new BlockComment();
        NodeList<BlockComment> updatedComments = comments.addLast(comment);
        NodeList clonedList = ((NodeList) (visitor.visit(((NodeList) (updatedComments)), ((Object) (comment)))));
        assertEquals(1, clonedList.size());
    }

    @Test
    void visitPackageDeclaration_returnsEqualButDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        PackageDeclaration pkg = new PackageDeclaration();
        Visitable cloned = visitor.visit(pkg, ((Object) (pkg)));
        assertNotSame(cloned, pkg);
        assertTrue(cloned.equals(((Object) (pkg))));
    }

    @Test
    void visitArrayCreationLevel_returnsDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        BlockComment context = new BlockComment();
        NodeList<AnnotationExpr> annotations = new NodeList<AnnotationExpr>();
        ArrayCreationLevel level = new ArrayCreationLevel(((Expression) (null)), annotations);
        Visitable cloned = visitor.visit(level, ((Object) (context)));
        assertNotSame(cloned, level);
    }

    @Test
    void visitIntegerLiteralExpr_returnsDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        BlockComment context = new BlockComment();
        IntegerLiteralExpr intLiteral = new IntegerLiteralExpr();
        Visitable cloned = visitor.visit(intLiteral, ((Object) (context)));
        assertNotSame(cloned, intLiteral);
    }

    @Test
    void visitClassOrInterfaceDeclaration_returnsNotConstructor() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        ClassOrInterfaceDeclaration typeDecl = new ClassOrInterfaceDeclaration();
        LocalRecordDeclarationStmt[] emptyLocalRecords = new LocalRecordDeclarationStmt[0];
        NodeList<LocalRecordDeclarationStmt> localRecords = NodeList.nodeList(emptyLocalRecords);
        ClassOrInterfaceDeclaration cloned = ((ClassOrInterfaceDeclaration) (visitor.visit(typeDecl, ((Object) (localRecords)))));
        assertFalse(cloned.isConstructorDeclaration());
    }

    @Test
    void visitArrayInitializerExpr_returnsDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        JavaToken invalid = JavaToken.INVALID;
        TokenRange range = new TokenRange(invalid, invalid);
        BlockComment context = new BlockComment();
        NodeList<Expression> values = new NodeList<Expression>();
        ArrayInitializerExpr initializer = new ArrayInitializerExpr(range, values);
        Visitable cloned = visitor.visit(initializer, ((Object) (context)));
        assertNotSame(cloned, initializer);
    }

    @Test
    void visitModifier_returnsDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        Modifier abstractMod = Modifier.abstractModifier();
        Visitable cloned = visitor.visit(abstractMod, ((Object) (abstractMod)));
        assertNotSame(cloned, abstractMod);
    }

    @Test
    void visitClassOrInterfaceType_returnsEqualButDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        PrimitiveType primitiveShort = PrimitiveType.shortType();
        ClassOrInterfaceType boxedShort = primitiveShort.toBoxedType();
        BlockComment context = new BlockComment("");
        Visitable cloned = visitor.visit(boxedShort, ((Object) (context)));
        assertTrue(cloned.equals(((Object) (boxedShort))));
        assertNotSame(cloned, boxedShort);
    }

    @Test
    void visitName_returnsDistinctWithoutQualifier() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        BlockComment context = new BlockComment("");
        Name name = new Name("u(il_%]h");
        Name cloned = ((Name) (visitor.visit(name, ((Object) (context)))));
        assertNotSame(cloned, name);
        assertFalse(cloned.hasQualifier());
    }

    @Test
    void visitStringLiteralExpr_returnsDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        JavaToken token = new JavaToken(1323, ">Pn^p");
        TokenRange range = new TokenRange(token, token);
        StringLiteralExpr stringLiteral = new StringLiteralExpr(range, "");
        Visitable cloned = visitor.visit(stringLiteral, ((Object) (null)));
        assertNotSame(cloned, stringLiteral);
    }

    @Test
    void visitRecordDeclaration_returnsEqual() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        Modifier[] modifiersArray = new Modifier[3];
        Modifier volatileModifier = Modifier.volatileModifier();
        modifiersArray[0] = volatileModifier;
        modifiersArray[1] = volatileModifier;
        modifiersArray[2] = modifiersArray[0];
        NodeList<Modifier> modifiers = new NodeList<Modifier>(modifiersArray);
        RecordDeclaration record = new RecordDeclaration(modifiers, "RECEIVER_PARAMETER");
        Visitable cloned = visitor.visit(record, ((Object) (modifiers)));
        assertTrue(cloned.equals(((Object) (record))));
    }

    @Test
    void visitTypePatternExpr_returnsEqualButDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        TypePatternExpr typePattern = new TypePatternExpr();
        Visitable cloned = visitor.visit(typePattern, ((Object) (typePattern)));
        assertTrue(cloned.equals(((Object) (typePattern))));
        assertNotSame(cloned, typePattern);
    }

    @Test
    void visitModuleDeclaration_returnsEqualButDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        ModuleDeclaration moduleDecl = new ModuleDeclaration();
        Visitable cloned = visitor.visit(moduleDecl, ((Object) (moduleDecl)));
        assertTrue(cloned.equals(((Object) (moduleDecl))));
        assertNotSame(cloned, moduleDecl);
    }

    @Test
    void visitObjectCreationExpr_returnsEqualButDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        ObjectCreationExpr creation = new ObjectCreationExpr();
        Visitable cloned = visitor.visit(creation, ((Object) (creation)));
        assertNotSame(cloned, creation);
        assertTrue(cloned.equals(((Object) (creation))));
    }

    @Test
    void visitSingleMemberAnnotationExpr_returnsEqualButDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        SingleMemberAnnotationExpr annotation = new SingleMemberAnnotationExpr();
        Visitable cloned = visitor.visit(annotation, ((Object) (annotation)));
        assertTrue(cloned.equals(((Object) (annotation))));
        assertNotSame(cloned, annotation);
    }

    @Test
    void visitTraditionalJavadocComment_returnsDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        TraditionalJavadocComment javadoc = new TraditionalJavadocComment();
        Visitable cloned = visitor.visit(javadoc, ((Object) (javadoc)));
        assertNotSame(cloned, javadoc);
    }

    @Test
    void visitEmptyStmt_returnsDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        EmptyStmt emptyStmt = new EmptyStmt();
        Visitable cloned = visitor.visit(emptyStmt, ((Object) (emptyStmt)));
        assertNotSame(cloned, emptyStmt);
    }

    @Test
    void visitMarkerAnnotationExpr_returnsEqualButDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        MarkerAnnotationExpr marker = new MarkerAnnotationExpr();
        Visitable cloned = visitor.visit(marker, ((Object) (marker)));
        assertTrue(cloned.equals(((Object) (marker))));
        assertNotSame(cloned, marker);
    }

    @Test
    void visitNormalAnnotationExpr_returnsEqualButDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        NormalAnnotationExpr annotation = new NormalAnnotationExpr();
        Visitable cloned = visitor.visit(annotation, ((Object) (annotation)));
        assertNotSame(cloned, annotation);
        assertTrue(cloned.equals(((Object) (annotation))));
    }

    @Test
    void visitUnaryExpr_returnsDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        UnaryExpr unaryExpr = new UnaryExpr();
        Visitable cloned = visitor.visit(unaryExpr, ((Object) (unaryExpr)));
        assertNotSame(cloned, unaryExpr);
    }

    @Test
    void visitVoidType_returnsDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        VoidType voidType = new VoidType();
        Visitable cloned = visitor.visit(voidType, ((Object) (voidType)));
        assertNotSame(cloned, voidType);
    }

    @Test
    void visitSwitchExpr_returnsDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        SwitchExpr switchExpr = new SwitchExpr();
        Visitable cloned = visitor.visit(switchExpr, ((Object) (switchExpr)));
        assertNotSame(cloned, switchExpr);
    }

    @Test
    void visitSwitchStmt_returnsDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        SwitchStmt switchStmt = new SwitchStmt();
        Visitable cloned = visitor.visit(switchStmt, ((Object) (switchStmt)));
        assertNotSame(cloned, switchStmt);
    }

    @Test
    void visitSuperExpr_returnsDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        SuperExpr superExpr = new SuperExpr();
        Visitable cloned = visitor.visit(superExpr, ((Object) (superExpr)));
        assertNotSame(cloned, superExpr);
    }

    @Test
    void visitCompilationUnit_returnsDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        CompilationUnit unit = new CompilationUnit();
        Visitable cloned = visitor.visit(unit, ((Object) (unit)));
        assertNotSame(cloned, unit);
    }

    @Test
    void visitCatchClause_returnsEqualButDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        CatchClause catchClause = new CatchClause();
        Visitable cloned = visitor.visit(catchClause, ((Object) (catchClause)));
        assertTrue(cloned.equals(((Object) (catchClause))));
        assertNotSame(cloned, catchClause);
    }

    @Test
    void visitNullLiteralExpr_returnsDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        NullLiteralExpr nullLiteral = new NullLiteralExpr();
        Visitable cloned = visitor.visit(nullLiteral, ((Object) (nullLiteral)));
        assertNotSame(cloned, nullLiteral);
    }

    @Test
    void visitMemberValuePair_returnsDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        MemberValuePair pair = new MemberValuePair();
        Visitable cloned = visitor.visit(pair, ((Object) (pair)));
        assertNotSame(cloned, pair);
    }

    @Test
    void visitAssertStmt_returnsEqualButDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        AssertStmt assertStmt = new AssertStmt();
        Visitable cloned = visitor.visit(assertStmt, ((Object) (assertStmt)));
        assertNotSame(cloned, assertStmt);
        assertTrue(cloned.equals(((Object) (assertStmt))));
    }

    @Test
    void visitUnparsableStmt_returnsDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        UnparsableStmt unparsable = new UnparsableStmt();
        Visitable cloned = visitor.visit(unparsable, ((Object) (unparsable)));
        assertNotSame(cloned, unparsable);
    }

    @Test
    void visitEnumConstantDeclaration_returnsDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        EnumConstantDeclaration enumConstant = new EnumConstantDeclaration();
        Visitable cloned = visitor.visit(enumConstant, ((Object) (enumConstant)));
        assertNotSame(cloned, enumConstant);
    }

    @Test
    void cloneNode_blockComment_returnsClone() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        BlockComment comment = new BlockComment();
        BlockComment clone = visitor.cloneNode(comment, comment);
        assertNotSame(clone, comment);
    }

    @Test
    void visitModuleProvidesDirective_returnsEqualButDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        ModuleProvidesDirective provides = new ModuleProvidesDirective();
        Visitable cloned = visitor.visit(provides, ((Object) (provides)));
        assertTrue(cloned.equals(((Object) (provides))));
        assertNotSame(cloned, provides);
    }

    @Test
    void visitTypeExpr_returnsEqualButDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        TypeExpr typeExpr = new TypeExpr();
        Visitable cloned = visitor.visit(typeExpr, ((Object) (typeExpr)));
        assertTrue(cloned.equals(((Object) (typeExpr))));
        assertNotSame(cloned, typeExpr);
    }

    @Test
    void visitContinueStmt_returnsDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        ContinueStmt continueStmt = new ContinueStmt();
        Visitable cloned = visitor.visit(continueStmt, ((Object) (continueStmt)));
        assertNotSame(cloned, continueStmt);
    }

    @Test
    void visitMethodDeclaration_returnsEqual() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        MethodDeclaration method = new MethodDeclaration();
        Visitable cloned = visitor.visit(method, ((Object) (method)));
        assertTrue(cloned.equals(((Object) (method))));
    }

    @Test
    void visitLocalRecordDeclarationStmt_returnsDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        LocalRecordDeclarationStmt localRecord = new LocalRecordDeclarationStmt();
        Visitable cloned = visitor.visit(localRecord, ((Object) (localRecord)));
        assertNotSame(cloned, localRecord);
    }

    @Test
    void visitArrayAccessExpr_returnsEqualButDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        ArrayAccessExpr arrayAccess = new ArrayAccessExpr();
        Visitable cloned = visitor.visit(arrayAccess, ((Object) (arrayAccess)));
        assertTrue(cloned.equals(((Object) (arrayAccess))));
        assertNotSame(cloned, arrayAccess);
    }

    @Test
    void visitDoStmt_returnsDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        DoStmt doStmt = new DoStmt();
        Visitable cloned = visitor.visit(doStmt, ((Object) (doStmt)));
        assertNotSame(cloned, doStmt);
    }

    @Test
    void visitUnknownType_returnsDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        UnknownType unknownType = new UnknownType();
        Visitable cloned = visitor.visit(unknownType, ((Object) (unknownType)));
        assertNotSame(cloned, unknownType);
    }

    @Test
    void visitMethodReferenceExpr_returnsEqualButDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        MethodReferenceExpr methodRef = new MethodReferenceExpr();
        Visitable cloned = visitor.visit(methodRef, ((Object) (methodRef)));
        assertTrue(cloned.equals(((Object) (methodRef))));
        assertNotSame(cloned, methodRef);
    }

    @Test
    void visitRecordPatternExpr_returnsEqualButDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        RecordPatternExpr recordPattern = new RecordPatternExpr();
        Visitable cloned = visitor.visit(recordPattern, ((Object) (recordPattern)));
        assertTrue(cloned.equals(((Object) (recordPattern))));
        assertNotSame(cloned, recordPattern);
    }

    @Test
    void visitModuleOpensDirective_returnsEqualButDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        ModuleOpensDirective opens = new ModuleOpensDirective();
        Visitable cloned = visitor.visit(opens, ((Object) (opens)));
        assertTrue(cloned.equals(((Object) (opens))));
        assertNotSame(cloned, opens);
    }

    @Test
    void visitAssignExpr_returnsEqualButDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        AssignExpr assignExpr = new AssignExpr();
        Visitable cloned = visitor.visit(assignExpr, ((Object) (assignExpr)));
        assertNotSame(cloned, assignExpr);
        assertTrue(cloned.equals(((Object) (assignExpr))));
    }

    @Test
    void visitLocalClassDeclarationStmt_returnsDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        LocalClassDeclarationStmt localClass = new LocalClassDeclarationStmt();
        Visitable cloned = visitor.visit(localClass, ((Object) (localClass)));
        assertNotSame(cloned, localClass);
    }

    @Test
    void visitModuleExportsDirective_returnsEqualButDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        ModuleExportsDirective exports = new ModuleExportsDirective();
        Visitable cloned = visitor.visit(exports, ((Object) (exports)));
        assertNotSame(cloned, exports);
        assertTrue(cloned.equals(((Object) (exports))));
    }

    @Test
    void visitInitializerDeclaration_returnsDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        InitializerDeclaration initializer = new InitializerDeclaration();
        Visitable cloned = visitor.visit(initializer, ((Object) (initializer)));
        assertNotSame(cloned, initializer);
    }

    @Test
    void visitTypeParameter_returnsDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        TypeParameter typeParam = new TypeParameter();
        Visitable cloned = visitor.visit(typeParam, ((Object) (typeParam)));
        assertNotSame(cloned, typeParam);
    }

    @Test
    void visitDoubleLiteralExpr_returnsDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        DoubleLiteralExpr doubleLiteral = new DoubleLiteralExpr();
        Visitable cloned = visitor.visit(doubleLiteral, ((Object) (doubleLiteral)));
        assertNotSame(cloned, doubleLiteral);
    }

    @Test
    void visitConstructorDeclaration_returnsNotEnum() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        ConstructorDeclaration ctor = new ConstructorDeclaration();
        ConstructorDeclaration cloned = ((ConstructorDeclaration) (visitor.visit(ctor, ((Object) (ctor)))));
        assertFalse(cloned.isEnumDeclaration());
    }

    @Test
    void visitMarkdownComment_returnsDistinct() throws Throwable {
        CloneVisitor visitor = new CloneVisitor();
        MarkdownComment markdown = new MarkdownComment();
        Visitable cloned = visitor.visit(markdown, ((Object) (markdown)));
        assertNotSame(cloned, markdown);
    }
}