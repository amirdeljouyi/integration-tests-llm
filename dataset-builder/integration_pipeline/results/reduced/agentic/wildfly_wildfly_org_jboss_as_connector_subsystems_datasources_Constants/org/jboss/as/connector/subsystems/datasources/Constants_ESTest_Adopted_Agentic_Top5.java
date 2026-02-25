/*
 * Copyright The WildFly Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.as.connector.subsystems.datasources;

import org.junit.Assert;
import org.junit.Test;

public class Constants_ESTest_Adopted_Agentic_Top5 {
    @Test

    /**
     * This test added coverage 100.00% (2128/2128 added lines among kept tests).
     * Delta details: +384 methods, +239 branches, +9805 instructions.
     * Full version of the covered block is here: <a href="https://github.com/wildfly/wildfly/blob/main/connector/src/main/java/org/jboss/as/connector/subsystems/datasources/Constants.java#L58-L58">Constants.java (lines 58-58)</a>
     * Covered Lines:
     * <pre><code>
     * <span style="background-color:#fff3b0;">public class Constants {</span>
     * </code></pre>
     * Full version of the covered block is here: <a href="https://github.com/wildfly/wildfly/blob/main/connector/src/main/java/org/jboss/as/connector/subsystems/common/pool/Constants.java#L72-L76">Constants.java (lines 72-76)</a>
     * Covered Lines:
     * <pre><code>
     * <span style="background-color:#fff3b0;">    public static final SimpleAttributeDefinition BLOCKING_TIMEOUT_WAIT_MILLIS = new SimpleAttributeDefinitionBuilder(BLOCKING_TIMEOUT_WAIT_MILLIS_NAME, ModelType.LONG, true)</span>
     * <span style="background-color:#fff3b0;">            .setXmlName(TimeOut.Tag.BLOCKING_TIMEOUT_MILLIS.getLocalName())</span>
     * <span style="background-color:#fff3b0;">            .setMeasurementUnit(MeasurementUnit.MILLISECONDS)</span>
     * <span style="background-color:#fff3b0;">            .setAllowExpression(true)</span>
     * <span style="background-color:#fff3b0;">            .build();</span>
     * </code></pre>
     * Additional covered classes omitted: 121
     */
    public void testConstantsInstantiation() {
        org.jboss.as.connector.subsystems.datasources.Constants constants = new org.jboss.as.connector.subsystems.datasources.Constants();
        Assert.assertNotNull(constants);
    }
}
