/*
 * Copyright (C) 2010-2014 Hamburg Sud and the contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.aludratest.scheduler.util;

import org.aludratest.scheduler.node.RunnerGroup;
import org.aludratest.scheduler.node.RunnerNode;
import org.slf4j.Logger;

public final class RunnerTreeUtil {

    private RunnerTreeUtil() {
    }

    /** Prints the sub tree of a node to a given {@link Logger} in DEBUG level. Applied to the root node, it logs the full tree
     * hierarchy
     * @param node Node to debug to the logger, including the complete substructure.
     * @param logger The logger to debug the tree structure to.
     * @param indent Indent to use for output, e.g. a string with two spaces, or an empty string. Never <code>null</code>! */
    public static void debugSubTree(RunnerNode node, Logger logger, String indent) {
        logger.debug("{}{}", indent, node);
        if (node instanceof RunnerGroup) {
            String subIndent = indent + "  ";
            RunnerGroup group = (RunnerGroup) node;
            for (RunnerNode child : group.getChildren()) {
                debugSubTree(child, logger, subIndent);
            }
        }
    }

}
