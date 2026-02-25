package com.github.javaparser.ast.visitor;
import com.github.javaparser.ast.visitor.CloneVisitor_ESTest_scaffolding;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.InitializerDeclaration;
import com.github.javaparser.ast.expr.SimpleName;
import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
public class CloneVisitor_ESTest_Top5 extends CloneVisitor_ESTest_scaffolding {
    @Test(timeout = 4000)
    public void testVisitTaking1And1AndVisitTaking1And1AndVisitTaking1And1ReturningNonNull26() throws Throwable {
        CloneVisitor cloneVisitor0 = new CloneVisitor();
        Object object0 = new Object();
        SimpleName simpleName0 = new SimpleName();
        Visitable visitable0 = cloneVisitor0.visit(simpleName0, object0);
        assertNotSame(visitable0, simpleName0);
    }

    @Test(timeout = 4000)
    public void testVisitTaking1And1AndVisitTaking1And1AndVisitTaking1And1ReturningNonNull76() throws Throwable {
        CloneVisitor cloneVisitor0 = new CloneVisitor();
        CompilationUnit compilationUnit0 = new CompilationUnit();
        Visitable visitable0 = cloneVisitor0.visit(compilationUnit0, ((Object) (compilationUnit0)));
        assertNotSame(visitable0, compilationUnit0);
    }

    @Test(timeout = 4000)
    public void testVisitTaking1And1AndVisitTaking1And1AndVisitTaking1And1ReturningNonNull82() throws Throwable {
        CloneVisitor cloneVisitor0 = new CloneVisitor();
        EnumConstantDeclaration enumConstantDeclaration0 = new EnumConstantDeclaration();
        Visitable visitable0 = cloneVisitor0.visit(enumConstantDeclaration0, ((Object) (enumConstantDeclaration0)));
        assertNotSame(visitable0, enumConstantDeclaration0);
    }

    @Test(timeout = 4000)
    public void testVisitTaking1And1AndVisitTaking1And1AndVisitTaking1And1ReturningNonNull98() throws Throwable {
        CloneVisitor cloneVisitor0 = new CloneVisitor();
        InitializerDeclaration initializerDeclaration0 = new InitializerDeclaration();
        Visitable visitable0 = cloneVisitor0.visit(initializerDeclaration0, ((Object) (initializerDeclaration0)));
        assertNotSame(visitable0, initializerDeclaration0);
    }

    @Test(timeout = 4000)
    public void testVisitTaking1And1AndVisitTaking1And1AndVisitTaking1And1ReturningNonNull101() throws Throwable {
        CloneVisitor cloneVisitor0 = new CloneVisitor();
        ConstructorDeclaration constructorDeclaration0 = new ConstructorDeclaration();
        ConstructorDeclaration constructorDeclaration1 = ((ConstructorDeclaration) (cloneVisitor0.visit(constructorDeclaration0, ((Object) (constructorDeclaration0)))));
        assertFalse(constructorDeclaration1.isEnumDeclaration());
    }
}
