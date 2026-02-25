/*
 * Copyright The WildFly Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.as.connector.subsystems.datasources;

import Constants_ESTest_scaffolding;
import org.junit.Test;

/**
 * Verifies that the Constants class Constants_ESTest_Adopted be instantiated.
 */
public class Constants_ESTest_Top100 extends Constants_ESTest_scaffolding {

    @Test(timeout = 4000)
    public void shouldInstantiateConstants() throws Throwable {
        Constants constantsInstance = new Constants();
    }
}