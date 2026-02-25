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
import com.github.javaparser.ast.Visitable;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.modules.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.*;
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
        TypeDeclaration<?> typeDeclaration;
        while (typeItr.hasNext()) {
            typeDeclaration = typeItr.next();
            if (typeDeclaration.getMembers() == null) {
                assertEquals("javadoc", typeDeclaration.getComment().get().getContent());
            } else {
                Iterator<BodyDeclaration<?>> bodyItr =
                        typeDeclaration.getMembers().iterator();
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
    void visit_WildcardType_withBlockComment_returnsEqualButDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        TypeParameter typeParameter = new TypeParameter("]0Cjw}+`@oK`WO2B");
        NodeList<AnnotationExpr> annotations = new NodeList<AnnotationExpr>();
        WildcardType wildcardType = new WildcardType(typeParameter, typeParameter, annotations);
        BlockComment blockComment = new BlockComment();
        Visitable cloned = cloneVisitor.visit(wildcardType, ((Object) (blockComment)));
        assertTrue(cloned.equals(((Object) (wildcardType))));
        assertNotSame(cloned, wildcardType);
    }

    @Test
    void visit_VarType_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        VarType varType = new VarType();
        Object payload = new Object();
        Visitable cloned = cloneVisitor.visit(varType, payload);
        assertNotSame(cloned, varType);
    }

    @Test
    void visit_UnionType_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        UnionType unionType = new UnionType();
        Object payload = new Object();
        Visitable cloned = cloneVisitor.visit(unionType, payload);
        assertNotSame(cloned, unionType);
    }

    @Test
    void visit_PrimitiveType_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        Object payload = new Object();
        PrimitiveType primitiveType = PrimitiveType.shortType();
        Visitable cloned = cloneVisitor.visit(primitiveType, payload);
        assertNotSame(cloned, primitiveType);
    }

    @Test
    void visit_IntersectionType_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        NodeList<ReferenceType> referenceTypes = new NodeList<ReferenceType>();
        IntersectionType intersectionType = new IntersectionType(referenceTypes);
        Object payload = new Object();
        Visitable cloned = cloneVisitor.visit(intersectionType, payload);
        assertNotSame(cloned, intersectionType);
    }

    @Test
    void visit_YieldStmt_withBlockComment_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        YieldStmt yieldStmt = new YieldStmt();
        JavaToken invalidToken = JavaToken.INVALID;
        TokenRange tokenRange = new TokenRange(invalidToken, invalidToken);
        BlockComment blockComment = new BlockComment(tokenRange, "Corresponding declaration not available for unsolved symbol.");
        Visitable cloned = cloneVisitor.visit(yieldStmt, ((Object) (blockComment)));
        assertNotSame(cloned, yieldStmt);
    }

    @Test
    void visit_TryStmt_returnsEqualButDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        TryStmt tryStmt = new TryStmt();
        Object payload = new Object();
        Visitable cloned = cloneVisitor.visit(tryStmt, payload);
        assertTrue(cloned.equals(((Object) (tryStmt))));
        assertNotSame(cloned, tryStmt);
    }

    @Test
    void visit_ThrowStmt_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        ThrowStmt throwStmt = new ThrowStmt();
        Visitable cloned = cloneVisitor.visit(throwStmt, ((Object) (throwStmt)));
        assertNotSame(cloned, throwStmt);
    }

    @Test
    void visit_SynchronizedStmt_withBlockComment_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        BlockComment blockComment = new BlockComment();
        SynchronizedStmt synchronizedStmt = new SynchronizedStmt();
        Visitable cloned = cloneVisitor.visit(synchronizedStmt, ((Object) (blockComment)));
        assertNotSame(cloned, synchronizedStmt);
    }

    @Test
    void visit_SwitchEntry_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        JavaToken invalidToken = JavaToken.INVALID;
        TokenRange tokenRange = new TokenRange(invalidToken, invalidToken);
        NodeList<Expression> expressions = new NodeList<Expression>();
        SwitchEntry.Type entryType = SwitchEntry.Type.STATEMENT_GROUP;
        LinkedList<Statement> statementsLinked = new LinkedList<Statement>();
        NodeList<Statement> statements = NodeList.nodeList(statementsLinked);
        SwitchEntry switchEntry = new SwitchEntry(tokenRange, expressions, entryType, statements);
        Visitable cloned = cloneVisitor.visit(switchEntry, ((Object) (expressions)));
        assertNotSame(cloned, switchEntry);
    }

    @Test
    void visit_ReturnStmt_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        ReturnStmt returnStmt = new ReturnStmt();
        Visitable cloned = cloneVisitor.visit(returnStmt, ((Object) (returnStmt)));
        assertNotSame(cloned, returnStmt);
    }

    @Test
    void visit_LabeledStmt_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        Modifier[] modifiersArray = new Modifier[3];
        NodeList<Modifier> modifiers = new NodeList<Modifier>(modifiersArray);
        LabeledStmt labeledStmt = new LabeledStmt();
        Visitable cloned = cloneVisitor.visit(labeledStmt, ((Object) (modifiers)));
        assertNotSame(cloned, labeledStmt);
    }

    @Test
    void visit_IfStmt_returnsDistinctInstanceWithoutElseBranch() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        IfStmt originalIf = new IfStmt();
        Object payload = new Object();
        IfStmt clonedIf = ((IfStmt) (cloneVisitor.visit(originalIf, payload)));
        assertFalse(clonedIf.hasElseBranch());
        assertNotSame(clonedIf, originalIf);
    }

    @Test
    void visit_ForStmt_returnsEqualButDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        ForStmt forStmt = new ForStmt();
        BlockComment[] commentsArray = new BlockComment[0];
        NodeList<BlockComment> comments = new NodeList<BlockComment>(commentsArray);
        Visitable cloned = cloneVisitor.visit(forStmt, ((Object) (comments)));
        assertNotSame(cloned, forStmt);
        assertTrue(cloned.equals(((Object) (forStmt))));
    }

    @Test
    void visit_ForEachStmt_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        ForEachStmt forEachStmt = new ForEachStmt();
        Object payload = new Object();
        Visitable cloned = cloneVisitor.visit(forEachStmt, payload);
        assertNotSame(cloned, forEachStmt);
    }

    @Test
    void visit_ExpressionStmt_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        ExpressionStmt expressionStmt = new ExpressionStmt();
        Object payload = new Object();
        Visitable cloned = cloneVisitor.visit(expressionStmt, payload);
        assertNotSame(cloned, expressionStmt);
    }

    @Test
    void visit_ExplicitConstructorInvocation_withBlockComment_returnsEqualButDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        BlockComment blockComment = new BlockComment();
        ExplicitConstructorInvocationStmt explicitConstructorInvocation = new ExplicitConstructorInvocationStmt();
        Visitable cloned = cloneVisitor.visit(explicitConstructorInvocation, ((Object) (blockComment)));
        assertTrue(cloned.equals(((Object) (explicitConstructorInvocation))));
        assertNotSame(cloned, explicitConstructorInvocation);
    }

    @Test
    void visit_BreakStmt_returnsEqualButDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        Object payload = new Object();
        BreakStmt breakStmt = new BreakStmt("&");
        Visitable cloned = cloneVisitor.visit(breakStmt, payload);
        assertNotSame(cloned, breakStmt);
        assertTrue(cloned.equals(((Object) (breakStmt))));
    }

    @Test
    void visit_BlockStmt_fromRecordInitializer_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        RecordDeclaration recordDecl = new RecordDeclaration();
        BlockStmt initializer = recordDecl.addStaticInitializer();
        Visitable cloned = cloneVisitor.visit(initializer, ((Object) (recordDecl)));
        assertNotSame(cloned, initializer);
    }

    @Test
    void visit_ModuleUsesDirective_returnsEqualButDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        JavaToken token = new JavaToken(0);
        TokenRange tokenRange = new TokenRange(token, token);
        Name serviceName = new Name();
        ModuleUsesDirective usesDirective = new ModuleUsesDirective(tokenRange, serviceName);
        Visitable cloned = cloneVisitor.visit(usesDirective, ((Object) (null)));
        assertTrue(cloned.equals(((Object) (usesDirective))));
        assertNotSame(cloned, usesDirective);
    }

    @Test
    void visit_ModuleRequiresDirective_returnsEqualButDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        ModuleRequiresDirective requiresDirective = new ModuleRequiresDirective();
        Visitable cloned = cloneVisitor.visit(requiresDirective, ((Object) (null)));
        assertTrue(cloned.equals(((Object) (requiresDirective))));
        assertNotSame(cloned, requiresDirective);
    }

    @Test
    void visit_VariableDeclarationExpr_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        VariableDeclarator[] vars = new VariableDeclarator[0];
        NodeList<VariableDeclarator> variables = new NodeList<VariableDeclarator>(vars);
        VariableDeclarationExpr varDeclExpr = new VariableDeclarationExpr(variables);
        Visitable cloned = cloneVisitor.visit(varDeclExpr, ((Object) (variables)));
        assertNotSame(cloned, varDeclExpr);
    }

    @Test
    void visit_ThisExpr_returnsEqualButDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        Name scope = new Name();
        ThisExpr thisExpr = new ThisExpr(scope);
        Object payload = new Object();
        Visitable cloned = cloneVisitor.visit(thisExpr, payload);
        assertNotSame(cloned, thisExpr);
        assertTrue(cloned.equals(((Object) (thisExpr))));
    }

    @Test
    void visit_TextBlockLiteralExpr_returnsDistinctInstance() {
        BlockComment blockComment = new BlockComment();
        CloneVisitor cloneVisitor = new CloneVisitor();
        TextBlockLiteralExpr textBlock = new TextBlockLiteralExpr();
        Visitable cloned = cloneVisitor.visit(textBlock, ((Object) (blockComment)));
        assertNotSame(cloned, textBlock);
    }

    @Test
    void visit_SimpleName_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        Object payload = new Object();
        SimpleName simpleName = new SimpleName();
        Visitable cloned = cloneVisitor.visit(simpleName, payload);
        assertNotSame(cloned, simpleName);
    }

    @Test
    void visit_NameExpr_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        SimpleName id = new SimpleName();
        NameExpr nameExpr = new NameExpr(id);
        Visitable cloned = cloneVisitor.visit(nameExpr, ((Object) (null)));
        assertNotSame(cloned, nameExpr);
    }

    @Test
    void visit_MatchAllPatternExpr_withVolatileModifiers_returnsEqualButDistinctInstance() {
        BlockComment blockComment = new BlockComment();
        CloneVisitor cloneVisitor = new CloneVisitor();
        Modifier[] modifierKeywordsArray = new Modifier.Keyword[4];
        Modifier.Keyword volatileKeyword = Modifier.Keyword.VOLATILE;
        modifierKeywordsArray[0] = volatileKeyword;
        modifierKeywordsArray[1] = modifierKeywordsArray[0];
        modifierKeywordsArray[2] = modifierKeywordsArray[1];
        modifierKeywordsArray[3] = modifierKeywordsArray[2];
        NodeList<Modifier> modifiers = Modifier.createModifierList(modifierKeywordsArray);
        MatchAllPatternExpr matchAllPattern = new MatchAllPatternExpr(modifiers);
        Visitable cloned = cloneVisitor.visit(matchAllPattern, ((Object) (blockComment)));
        assertNotSame(cloned, matchAllPattern);
        assertTrue(cloned.equals(((Object) (matchAllPattern))));
    }

    @Test
    void visit_LongLiteralExpr_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        LongLiteralExpr longLiteral = new LongLiteralExpr("\"class\"");
        Visitable cloned = cloneVisitor.visit(longLiteral, ((Object) (null)));
        assertNotSame(cloned, longLiteral);
    }

    @Test
    void visit_InstanceOfExpr_returnsEqualButDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        InstanceOfExpr instanceOfExpr = new InstanceOfExpr();
        BlockComment blockComment = new BlockComment();
        Visitable cloned = cloneVisitor.visit(instanceOfExpr, ((Object) (blockComment)));
        assertTrue(cloned.equals(((Object) (instanceOfExpr))));
        assertNotSame(cloned, instanceOfExpr);
    }

    @Test
    void visit_FieldAccessExpr_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        Object payload = new Object();
        FieldAccessExpr fieldAccess = new FieldAccessExpr();
        Visitable cloned = cloneVisitor.visit(fieldAccess, payload);
        assertNotSame(cloned, fieldAccess);
    }

    @Test
    void visit_EnclosedExpr_returnsDistinctInstance() {
        BlockComment blockComment = new BlockComment();
        CloneVisitor cloneVisitor = new CloneVisitor();
        EnclosedExpr enclosed = new EnclosedExpr();
        Visitable cloned = cloneVisitor.visit(enclosed, ((Object) (blockComment)));
        assertNotSame(cloned, enclosed);
    }

    @Test
    void visit_ConditionalExpr_returnsEqualButDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        ConditionalExpr conditional = new ConditionalExpr();
        Object payload = new Object();
        Visitable cloned = cloneVisitor.visit(conditional, payload);
        assertNotSame(cloned, conditional);
        assertTrue(cloned.equals(((Object) (conditional))));
    }

    @Test
    void visit_ClassExpr_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        TypeParameter typeParameter = new TypeParameter("4)oF?x7_I'eKf+j`G<~");
        ClassExpr classExpr = new ClassExpr(typeParameter);
        BlockComment[] commentsArray = new BlockComment[4];
        NodeList<BlockComment> comments = new NodeList<BlockComment>(commentsArray);
        Visitable cloned = cloneVisitor.visit(classExpr, ((Object) (comments)));
        assertNotSame(cloned, classExpr);
    }

    @Test
    void visit_CharLiteralExpr_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        CharLiteralExpr charLiteral = CharLiteralExpr.escape("");
        NodeList<BlockComment> comments = new NodeList<BlockComment>();
        Visitable cloned = cloneVisitor.visit(charLiteral, ((Object) (comments)));
        assertNotSame(cloned, charLiteral);
    }

    @Test
    void visit_BooleanLiteralExpr_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        BooleanLiteralExpr boolLiteral = new BooleanLiteralExpr(true);
        BlockComment blockComment = new BlockComment("JAVA_1_0");
        Visitable cloned = cloneVisitor.visit(boolLiteral, ((Object) (blockComment)));
        assertNotSame(cloned, boolLiteral);
    }

    @Test
    void visit_BinaryExpr_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        BinaryExpr binaryExpr = new BinaryExpr();
        Object payload = new Object();
        Visitable cloned = cloneVisitor.visit(binaryExpr, payload);
        assertNotSame(cloned, binaryExpr);
    }

    @Test
    void visit_ArrayCreationExpr_returnsEqualButDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        UnknownType unknownType = new UnknownType();
        Object payload = new Object();
        NodeList<ArrayCreationLevel> levels = new NodeList<ArrayCreationLevel>();
        ArrayInitializerExpr initializer = new ArrayInitializerExpr();
        ArrayCreationExpr arrayCreation = new ArrayCreationExpr(unknownType, levels, initializer);
        Visitable cloned = cloneVisitor.visit(arrayCreation, payload);
        assertTrue(cloned.equals(((Object) (arrayCreation))));
        assertNotSame(cloned, arrayCreation);
    }

    @Test
    void visit_LineComment_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        TokenRange invalidRange = TokenRange.INVALID;
        LineComment lineComment = new LineComment(invalidRange, "w*gWr 2b\u0007:X");
        Object payload = new Object();
        Visitable cloned = cloneVisitor.visit(lineComment, payload);
        assertNotSame(cloned, lineComment);
    }

    @Test
    void visit_BlockComment_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        BlockComment blockComment = new BlockComment("");
        BlockComment[] commentsArray = new BlockComment[8];
        NodeList<BlockComment> comments = NodeList.nodeList(commentsArray);
        Visitable cloned = cloneVisitor.visit(blockComment, ((Object) (comments)));
        assertNotSame(cloned, blockComment);
    }

    @Test
    void visit_VariableDeclarator_returnsEqualButDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        VariableDeclarator variable = new VariableDeclarator();
        Object payload = new Object();
        Visitable cloned = cloneVisitor.visit(variable, payload);
        assertTrue(cloned.equals(((Object) (variable))));
        assertNotSame(cloned, variable);
    }

    @Test
    void visit_ReceiverParameter_returnsEqualButDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        TokenRange invalidRange = TokenRange.INVALID;
        UnknownType unknownType = new UnknownType(invalidRange);
        ReceiverParameter receiver = new ReceiverParameter(unknownType, "\"<<\"");
        NodeList<WhileStmt> whileStmts = new NodeList<WhileStmt>();
        Visitable cloned = cloneVisitor.visit(receiver, ((Object) (whileStmts)));
        assertNotSame(cloned, receiver);
        assertTrue(cloned.equals(((Object) (receiver))));
    }

    @Test
    void visit_EnumDeclaration_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        BlockComment blockComment = new BlockComment();
        EnumDeclaration enumDecl = new EnumDeclaration();
        Visitable cloned = cloneVisitor.visit(enumDecl, ((Object) (blockComment)));
        assertNotSame(cloned, enumDecl);
    }

    @Test
    void visit_CompactConstructorDeclaration_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        CompactConstructorDeclaration compactConstructor = new CompactConstructorDeclaration();
        Visitable cloned = cloneVisitor.visit(compactConstructor, ((Object) (null)));
        assertNotSame(cloned, compactConstructor);
    }

    @Test
    void visit_AnnotationMemberDeclaration_returnsEqualButDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        AnnotationMemberDeclaration memberDecl = new AnnotationMemberDeclaration();
        Visitable cloned = cloneVisitor.visit(memberDecl, ((Object) (null)));
        assertTrue(cloned.equals(((Object) (memberDecl))));
        assertNotSame(cloned, memberDecl);
    }

    @Test
    void visit_AnnotationDeclaration_withModifiers_returnsEqualButDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
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
        Object payload = new Object();
        Visitable cloned = cloneVisitor.visit(annotationDecl, payload);
        assertTrue(cloned.equals(((Object) (annotationDecl))));
        assertNotSame(cloned, annotationDecl);
    }

    @Test
    void visit_ImportDeclaration_returnsEqualButDistinctNode() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        BlockComment blockComment = new BlockComment();
        JavaToken token = new JavaToken(1, "");
        TokenRange tokenRange = new TokenRange(token, token);
        Name name = new Name();
        ImportDeclaration importDecl = new ImportDeclaration(tokenRange, name, false, false, false);
        Node clonedNode = cloneVisitor.visit(importDecl, ((Object) (blockComment)));
        assertNotSame(clonedNode, importDecl);
        assertTrue(clonedNode.equals(((Object) (importDecl))));
    }

    @Test
    void cloneNode_optionalMethodDeclaration_returnsNull() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        Modifier[] modifiersArray = new Modifier[2];
        Modifier synchronizedModifier = Modifier.synchronizedModifier();
        modifiersArray[0] = synchronizedModifier;
        modifiersArray[1] = synchronizedModifier;
        NodeList<Modifier> modifiers = NodeList.nodeList(modifiersArray);
        RecordDeclaration recordDecl = new RecordDeclaration(modifiers, "/e>G`0p");
        Optional<MethodDeclaration> optionalMethod = recordDecl.toMethodDeclaration();
        Object payload = new Object();
        MethodDeclaration clonedMethod = cloneVisitor.cloneNode(optionalMethod, payload);
        assertNull(clonedMethod);
    }

    @Test
    void cloneNode_optionalYieldStmt_returnsDistinctNonNull() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        YieldStmt yieldStmt = new YieldStmt();
        Optional<YieldStmt> optionalYield = yieldStmt.toYieldStmt();
        YieldStmt clonedYield = cloneVisitor.cloneNode(optionalYield, yieldStmt);
        assertNotSame(clonedYield, yieldStmt);
        assertNotNull(clonedYield);
    }

    @Test
    void clone_ContinueStmt_createsEqualButDistinctCopy() {
        ContinueStmt original = new ContinueStmt("%s is not MethodCallExpr, it is %s");
        ContinueStmt cloned = original.clone();
        assertNotSame(cloned, original);
        assertTrue(cloned.equals(((Object) (original))));
    }

    @Test
    void visit_NodeListOfBlockComment_preservesSize() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        NodeList<BlockComment> comments = new NodeList<BlockComment>();
        BlockComment comment = new BlockComment();
        NodeList<BlockComment> updatedList = comments.addLast(comment);
        NodeList clonedList = ((NodeList) (cloneVisitor.visit(((NodeList) (updatedList)), ((Object) (comment)))));
        assertEquals(1, clonedList.size());
    }

    @Test
    void visit_PackageDeclaration_returnsEqualButDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        PackageDeclaration pkgDecl = new PackageDeclaration();
        Visitable cloned = cloneVisitor.visit(pkgDecl, ((Object) (pkgDecl)));
        assertNotSame(cloned, pkgDecl);
        assertTrue(cloned.equals(((Object) (pkgDecl))));
    }

    @Test
    void visit_ArrayCreationLevel_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        BlockComment blockComment = new BlockComment();
        NodeList<AnnotationExpr> annotations = new NodeList<AnnotationExpr>();
        ArrayCreationLevel level = new ArrayCreationLevel(((Expression) (null)), annotations);
        Visitable cloned = cloneVisitor.visit(level, ((Object) (blockComment)));
        assertNotSame(cloned, level);
    }

    @Test
    void visit_IntegerLiteralExpr_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        BlockComment blockComment = new BlockComment();
        IntegerLiteralExpr intLiteral = new IntegerLiteralExpr();
        Visitable cloned = cloneVisitor.visit(intLiteral, ((Object) (blockComment)));
        assertNotSame(cloned, intLiteral);
    }

    @Test
    void visit_ClassOrInterfaceDeclaration_returnsNonConstructor() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        ClassOrInterfaceDeclaration classOrInterface = new ClassOrInterfaceDeclaration();
        LocalRecordDeclarationStmt[] localRecordsArray = new LocalRecordDeclarationStmt[0];
        NodeList<LocalRecordDeclarationStmt> localRecords = NodeList.nodeList(localRecordsArray);
        ClassOrInterfaceDeclaration cloned = ((ClassOrInterfaceDeclaration) (cloneVisitor.visit(classOrInterface, ((Object) (localRecords)))));
        assertFalse(cloned.isConstructorDeclaration());
    }

    @Test
    void visit_ArrayInitializerExpr_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        JavaToken invalidToken = JavaToken.INVALID;
        TokenRange tokenRange = new TokenRange(invalidToken, invalidToken);
        BlockComment blockComment = new BlockComment();
        NodeList<Expression> values = new NodeList<Expression>();
        ArrayInitializerExpr initializer = new ArrayInitializerExpr(tokenRange, values);
        Visitable cloned = cloneVisitor.visit(initializer, ((Object) (blockComment)));
        assertNotSame(cloned, initializer);
    }

    @Test
    void visit_Modifier_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        Modifier abstractModifier = Modifier.abstractModifier();
        Visitable cloned = cloneVisitor.visit(abstractModifier, ((Object) (abstractModifier)));
        assertNotSame(cloned, abstractModifier);
    }

    @Test
    void visit_ClassOrInterfaceType_boxedFromPrimitive_returnsEqualButDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        PrimitiveType primitiveShort = PrimitiveType.shortType();
        ClassOrInterfaceType boxedType = primitiveShort.toBoxedType();
        BlockComment blockComment = new BlockComment("");
        Visitable cloned = cloneVisitor.visit(boxedType, ((Object) (blockComment)));
        assertTrue(cloned.equals(((Object) (boxedType))));
        assertNotSame(cloned, boxedType);
    }

    @Test
    void visit_Name_returnsDistinctWithoutQualifier() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        BlockComment blockComment = new BlockComment("");
        Name name = new Name("u(il_%]h");
        Name cloned = ((Name) (cloneVisitor.visit(name, ((Object) (blockComment)))));
        assertNotSame(cloned, name);
        assertFalse(cloned.hasQualifier());
    }

    @Test
    void visit_StringLiteralExpr_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        JavaToken token = new JavaToken(1323, ">Pn^p");
        TokenRange tokenRange = new TokenRange(token, token);
        StringLiteralExpr stringLiteral = new StringLiteralExpr(tokenRange, "");
        Visitable cloned = cloneVisitor.visit(stringLiteral, ((Object) (null)));
        assertNotSame(cloned, stringLiteral);
    }

    @Test
    void visit_RecordDeclaration_withModifiers_returnsEqualInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        Modifier[] modifiersArray = new Modifier[3];
        Modifier volatileModifier = Modifier.volatileModifier();
        modifiersArray[0] = volatileModifier;
        modifiersArray[1] = volatileModifier;
        modifiersArray[2] = modifiersArray[0];
        NodeList<Modifier> modifiers = new NodeList<Modifier>(modifiersArray);
        RecordDeclaration recordDecl = new RecordDeclaration(modifiers, "RECEIVER_PARAMETER");
        Visitable cloned = cloneVisitor.visit(recordDecl, ((Object) (modifiers)));
        assertTrue(cloned.equals(((Object) (recordDecl))));
    }

    @Test
    void visit_TypePatternExpr_returnsEqualButDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        TypePatternExpr typePattern = new TypePatternExpr();
        Visitable cloned = cloneVisitor.visit(typePattern, ((Object) (typePattern)));
        assertTrue(cloned.equals(((Object) (typePattern))));
        assertNotSame(cloned, typePattern);
    }

    @Test
    void visit_ModuleDeclaration_returnsEqualButDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        ModuleDeclaration moduleDecl = new ModuleDeclaration();
        Visitable cloned = cloneVisitor.visit(moduleDecl, ((Object) (moduleDecl)));
        assertTrue(cloned.equals(((Object) (moduleDecl))));
        assertNotSame(cloned, moduleDecl);
    }

    @Test
    void visit_ObjectCreationExpr_returnsEqualButDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        ObjectCreationExpr newExpr = new ObjectCreationExpr();
        Visitable cloned = cloneVisitor.visit(newExpr, ((Object) (newExpr)));
        assertNotSame(cloned, newExpr);
        assertTrue(cloned.equals(((Object) (newExpr))));
    }

    @Test
    void visit_SingleMemberAnnotationExpr_returnsEqualButDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        SingleMemberAnnotationExpr singleMemberAnnotation = new SingleMemberAnnotationExpr();
        Visitable cloned = cloneVisitor.visit(singleMemberAnnotation, ((Object) (singleMemberAnnotation)));
        assertTrue(cloned.equals(((Object) (singleMemberAnnotation))));
        assertNotSame(cloned, singleMemberAnnotation);
    }

    @Test
    void visit_TraditionalJavadocComment_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        TraditionalJavadocComment javadoc = new TraditionalJavadocComment();
        Visitable cloned = cloneVisitor.visit(javadoc, ((Object) (javadoc)));
        assertNotSame(cloned, javadoc);
    }

    @Test
    void visit_EmptyStmt_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        EmptyStmt emptyStmt = new EmptyStmt();
        Visitable cloned = cloneVisitor.visit(emptyStmt, ((Object) (emptyStmt)));
        assertNotSame(cloned, emptyStmt);
    }

    @Test
    void visit_MarkerAnnotationExpr_returnsEqualButDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        MarkerAnnotationExpr markerAnnotation = new MarkerAnnotationExpr();
        Visitable cloned = cloneVisitor.visit(markerAnnotation, ((Object) (markerAnnotation)));
        assertTrue(cloned.equals(((Object) (markerAnnotation))));
        assertNotSame(cloned, markerAnnotation);
    }

    @Test
    void visit_NormalAnnotationExpr_returnsEqualButDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        NormalAnnotationExpr normalAnnotation = new NormalAnnotationExpr();
        Visitable cloned = cloneVisitor.visit(normalAnnotation, ((Object) (normalAnnotation)));
        assertNotSame(cloned, normalAnnotation);
        assertTrue(cloned.equals(((Object) (normalAnnotation))));
    }

    @Test
    void visit_UnaryExpr_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        UnaryExpr unaryExpr = new UnaryExpr();
        Visitable cloned = cloneVisitor.visit(unaryExpr, ((Object) (unaryExpr)));
        assertNotSame(cloned, unaryExpr);
    }

    @Test
    void visit_VoidType_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        VoidType voidType = new VoidType();
        Visitable cloned = cloneVisitor.visit(voidType, ((Object) (voidType)));
        assertNotSame(cloned, voidType);
    }

    @Test
    void visit_SwitchExpr_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        SwitchExpr switchExpr = new SwitchExpr();
        Visitable cloned = cloneVisitor.visit(switchExpr, ((Object) (switchExpr)));
        assertNotSame(cloned, switchExpr);
    }

    @Test
    void visit_SwitchStmt_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        SwitchStmt switchStmt = new SwitchStmt();
        Visitable cloned = cloneVisitor.visit(switchStmt, ((Object) (switchStmt)));
        assertNotSame(cloned, switchStmt);
    }

    @Test
    void visit_SuperExpr_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        SuperExpr superExpr = new SuperExpr();
        Visitable cloned = cloneVisitor.visit(superExpr, ((Object) (superExpr)));
        assertNotSame(cloned, superExpr);
    }

    @Test
    void visit_CompilationUnit_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        CompilationUnit compilationUnit = new CompilationUnit();
        Visitable cloned = cloneVisitor.visit(compilationUnit, ((Object) (compilationUnit)));
        assertNotSame(cloned, compilationUnit);
    }

    @Test
    void visit_CatchClause_returnsEqualButDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        CatchClause catchClause = new CatchClause();
        Visitable cloned = cloneVisitor.visit(catchClause, ((Object) (catchClause)));
        assertTrue(cloned.equals(((Object) (catchClause))));
        assertNotSame(cloned, catchClause);
    }

    @Test
    void visit_NullLiteralExpr_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        NullLiteralExpr nullLiteral = new NullLiteralExpr();
        Visitable cloned = cloneVisitor.visit(nullLiteral, ((Object) (nullLiteral)));
        assertNotSame(cloned, nullLiteral);
    }

    @Test
    void visit_MemberValuePair_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        MemberValuePair pair = new MemberValuePair();
        Visitable cloned = cloneVisitor.visit(pair, ((Object) (pair)));
        assertNotSame(cloned, pair);
    }

    @Test
    void visit_AssertStmt_returnsEqualButDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        AssertStmt assertStmt = new AssertStmt();
        Visitable cloned = cloneVisitor.visit(assertStmt, ((Object) (assertStmt)));
        assertNotSame(cloned, assertStmt);
        assertTrue(cloned.equals(((Object) (assertStmt))));
    }

    @Test
    void visit_UnparsableStmt_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        UnparsableStmt unparsableStmt = new UnparsableStmt();
        Visitable cloned = cloneVisitor.visit(unparsableStmt, ((Object) (unparsableStmt)));
        assertNotSame(cloned, unparsableStmt);
    }

    @Test
    void visit_EnumConstantDeclaration_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        EnumConstantDeclaration enumConstant = new EnumConstantDeclaration();
        Visitable cloned = cloneVisitor.visit(enumConstant, ((Object) (enumConstant)));
        assertNotSame(cloned, enumConstant);
    }

    @Test
    void cloneNode_BlockComment_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        BlockComment blockComment = new BlockComment();
        BlockComment cloned = cloneVisitor.cloneNode(blockComment, blockComment);
        assertNotSame(cloned, blockComment);
    }

    @Test
    void visit_ModuleProvidesDirective_returnsEqualButDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        ModuleProvidesDirective providesDirective = new ModuleProvidesDirective();
        Visitable cloned = cloneVisitor.visit(providesDirective, ((Object) (providesDirective)));
        assertTrue(cloned.equals(((Object) (providesDirective))));
        assertNotSame(cloned, providesDirective);
    }

    @Test
    void visit_TypeExpr_returnsEqualButDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        TypeExpr typeExpr = new TypeExpr();
        Visitable cloned = cloneVisitor.visit(typeExpr, ((Object) (typeExpr)));
        assertTrue(cloned.equals(((Object) (typeExpr))));
        assertNotSame(cloned, typeExpr);
    }

    @Test
    void visit_ContinueStmt_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        ContinueStmt continueStmt = new ContinueStmt();
        Visitable cloned = cloneVisitor.visit(continueStmt, ((Object) (continueStmt)));
        assertNotSame(cloned, continueStmt);
    }

    @Test
    void visit_MethodDeclaration_returnsEqualInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        MethodDeclaration methodDecl = new MethodDeclaration();
        Visitable cloned = cloneVisitor.visit(methodDecl, ((Object) (methodDecl)));
        assertTrue(cloned.equals(((Object) (methodDecl))));
    }

    @Test
    void visit_LocalRecordDeclarationStmt_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        LocalRecordDeclarationStmt localRecordStmt = new LocalRecordDeclarationStmt();
        Visitable cloned = cloneVisitor.visit(localRecordStmt, ((Object) (localRecordStmt)));
        assertNotSame(cloned, localRecordStmt);
    }

    @Test
    void visit_ArrayAccessExpr_returnsEqualButDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        ArrayAccessExpr arrayAccess = new ArrayAccessExpr();
        Visitable cloned = cloneVisitor.visit(arrayAccess, ((Object) (arrayAccess)));
        assertTrue(cloned.equals(((Object) (arrayAccess))));
        assertNotSame(cloned, arrayAccess);
    }

    @Test
    void visit_DoStmt_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        DoStmt doStmt = new DoStmt();
        Visitable cloned = cloneVisitor.visit(doStmt, ((Object) (doStmt)));
        assertNotSame(cloned, doStmt);
    }

    @Test
    void visit_UnknownType_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        UnknownType unknownType = new UnknownType();
        Visitable cloned = cloneVisitor.visit(unknownType, ((Object) (unknownType)));
        assertNotSame(cloned, unknownType);
    }

    @Test
    void visit_MethodReferenceExpr_returnsEqualButDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        MethodReferenceExpr methodRef = new MethodReferenceExpr();
        Visitable cloned = cloneVisitor.visit(methodRef, ((Object) (methodRef)));
        assertTrue(cloned.equals(((Object) (methodRef))));
        assertNotSame(cloned, methodRef);
    }

    @Test
    void visit_RecordPatternExpr_returnsEqualButDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        RecordPatternExpr recordPattern = new RecordPatternExpr();
        Visitable cloned = cloneVisitor.visit(recordPattern, ((Object) (recordPattern)));
        assertTrue(cloned.equals(((Object) (recordPattern))));
        assertNotSame(cloned, recordPattern);
    }

    @Test
    void visit_ModuleOpensDirective_returnsEqualButDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        ModuleOpensDirective opensDirective = new ModuleOpensDirective();
        Visitable cloned = cloneVisitor.visit(opensDirective, ((Object) (opensDirective)));
        assertTrue(cloned.equals(((Object) (opensDirective))));
        assertNotSame(cloned, opensDirective);
    }

    @Test
    void visit_AssignExpr_returnsEqualButDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        AssignExpr assignExpr = new AssignExpr();
        Visitable cloned = cloneVisitor.visit(assignExpr, ((Object) (assignExpr)));
        assertNotSame(cloned, assignExpr);
        assertTrue(cloned.equals(((Object) (assignExpr))));
    }

    @Test
    void visit_LocalClassDeclarationStmt_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        LocalClassDeclarationStmt localClassStmt = new LocalClassDeclarationStmt();
        Visitable cloned = cloneVisitor.visit(localClassStmt, ((Object) (localClassStmt)));
        assertNotSame(cloned, localClassStmt);
    }

    @Test
    void visit_ModuleExportsDirective_returnsEqualButDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        ModuleExportsDirective exportsDirective = new ModuleExportsDirective();
        Visitable cloned = cloneVisitor.visit(exportsDirective, ((Object) (exportsDirective)));
        assertNotSame(cloned, exportsDirective);
        assertTrue(cloned.equals(((Object) (exportsDirective))));
    }

    @Test
    void visit_InitializerDeclaration_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        InitializerDeclaration initializer = new InitializerDeclaration();
        Visitable cloned = cloneVisitor.visit(initializer, ((Object) (initializer)));
        assertNotSame(cloned, initializer);
    }

    @Test
    void visit_TypeParameter_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        TypeParameter typeParameter = new TypeParameter();
        Visitable cloned = cloneVisitor.visit(typeParameter, ((Object) (typeParameter)));
        assertNotSame(cloned, typeParameter);
    }

    @Test
    void visit_DoubleLiteralExpr_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        DoubleLiteralExpr doubleLiteral = new DoubleLiteralExpr();
        Visitable cloned = cloneVisitor.visit(doubleLiteral, ((Object) (doubleLiteral)));
        assertNotSame(cloned, doubleLiteral);
    }

    @Test
    void visit_ConstructorDeclaration_returnsNonEnum() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        ConstructorDeclaration constructorDecl = new ConstructorDeclaration();
        ConstructorDeclaration cloned = ((ConstructorDeclaration) (cloneVisitor.visit(constructorDecl, ((Object) (constructorDecl)))));
        assertFalse(cloned.isEnumDeclaration());
    }

    @Test
    void visit_MarkdownComment_returnsDistinctInstance() {
        CloneVisitor cloneVisitor = new CloneVisitor();
        MarkdownComment markdown = new MarkdownComment();
        Visitable cloned = cloneVisitor.visit(markdown, ((Object) (markdown)));
        assertNotSame(cloned, markdown);
    }
}