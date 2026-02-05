package com.orientechnologies.orient.core.sql.parser;
import org.junit.Test;
public class OMatchStatement_ESTest_Adopted_Top5 {
    @Test
    public void testBasicMatch() {
        checkRightSyntax("MATCH {class: 'V', as: foo} RETURN foo");
    }

    @Test
    public void testArrowsNoBrackets() {
        checkWrongSyntax("MATCH {}-->-->{as:foo} RETURN foo");
    }

    @Test
    public void testDepthAlias() {
        checkRightSyntax("MATCH {class: 'V', as: foo}-->{as:bar, while:($depth < 2), depthAlias: depth} RETURN" + " depth");
    }

    @Test
    public void testClusterTarget() {
        checkRightSyntax("MATCH {cluster:v, as: foo} RETURN $elements");
        checkRightSyntax("MATCH {cluster:12, as: foo} RETURN $elements");
        checkRightSyntax("MATCH {cluster: v, as: foo} RETURN $elements");
        checkRightSyntax("MATCH {cluster: `v`, as: foo} RETURN $elements");
        checkRightSyntax("MATCH {cluster:`v`, as: foo} RETURN $elements");
        checkRightSyntax("MATCH {cluster: 12, as: foo} RETURN $elements");
        checkWrongSyntax("MATCH {cluster: 12.1, as: foo} RETURN $elements");
    }

    @Test
    public void testFieldTraversal() {
        checkRightSyntax("MATCH {class: 'V', as: foo}.toBar{as:bar} RETURN foo.name, bar.name skip 10 limit 10");
        checkRightSyntax("MATCH {class: 'V', as: foo}.toBar{as:bar}.out(){as:c} RETURN foo.name, bar.name skip 10" + " limit 10");
        checkRightSyntax("MATCH {class: 'V', as: foo}.toBar.baz{as:bar} RETURN foo.name, bar.name skip 10 limit 10");
        checkRightSyntax("MATCH {class: 'V', as: foo}.toBar.out(){as:bar} RETURN foo.name, bar.name skip 10 limit" + " 10");
    }
}