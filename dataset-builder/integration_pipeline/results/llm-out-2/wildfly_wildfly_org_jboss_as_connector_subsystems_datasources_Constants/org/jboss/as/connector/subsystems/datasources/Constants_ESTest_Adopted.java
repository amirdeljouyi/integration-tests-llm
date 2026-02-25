/*
 * Copyright The WildFly Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.as.connector.subsystems.datasources;

import Constants_ESTest_scaffolding;
import org.junit.Test;

public class Constants_ESTest_Adopted extends Constants_ESTest_scaffolding {

    @Test(timeout = 4000)
    public void shouldInstantiateConstants() throws Throwable {
        // Ensure the Constants class can be instantiated without errors
        Constants constants = new Constants();
    }
}