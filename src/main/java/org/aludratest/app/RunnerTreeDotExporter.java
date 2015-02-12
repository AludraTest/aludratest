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
package org.aludratest.app;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.aludratest.AludraTest;
import org.aludratest.impl.AludraTestConstants;
import org.aludratest.scheduler.RunnerTree;
import org.aludratest.scheduler.RunnerTreeBuilder;
import org.aludratest.scheduler.node.RunnerGroup;
import org.aludratest.scheduler.node.RunnerLeaf;
import org.aludratest.scheduler.node.RunnerNode;
import org.aludratest.scheduler.util.RunnerTreeUtil;
import org.databene.commons.Encodings;
import org.databene.formats.dot.ArrowShape;
import org.databene.formats.dot.DefaultDotGraphModel;
import org.databene.formats.dot.DotGraph;
import org.databene.formats.dot.DotNode;
import org.databene.formats.dot.DotWriter;
import org.databene.formats.dot.NodeShape;
import org.databene.formats.dot.RankDir;
import org.slf4j.LoggerFactory;

/**
 * Exports a {@link RunnerTree} structure as DOT diagram file.
 * @author Volker Bergmann
 */
public class RunnerTreeDotExporter {

    private RunnerTreeDotExporter() { }

    /**
     * Main method that interprets the first parameter as class name,
     * parses its internal test suite structure and exports it to a DOT
     * file named '&lt;classname&gt;.dot'.
     * @param args the command line parameters
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        // check preconditions
        if (args.length != 1) {
            System.out.println("Please provide the name of the test class or test suite"); //NOSONAR
            System.exit(AludraTestConstants.EXIT_ILLEGAL_ARGUMENT);
        }
        // execute
        String resourceName = args[0];

        AludraTest aludraTest = AludraTest.startFramework();
        try {
            RunnerTreeBuilder builder = aludraTest.getServiceManager().newImplementorInstance(RunnerTreeBuilder.class);
            RunnerTree tree = builder.buildRunnerTree(AppUtil.classForPotentialResourceName(resourceName));
            RunnerTreeUtil.debugSubTree(tree.getRoot(), LoggerFactory.getLogger(RunnerTreeDotExporter.class), "");
            export(tree, resourceName + ".dot");
        }
        finally {
            aludraTest.stopFramework();
        }
    }

    /**
     * Exports a {@link RunnerTree} structure as DOT diagram file.
     * @param tree the tree structure to export
     * @param fileName the name of the file to create
     * @throws FileNotFoundException if the file could not be vreated on the file system
     */
    public static void export(RunnerTree tree, String fileName) throws FileNotFoundException {
        DotGraph graph = createGraph(tree);
        DefaultDotGraphModel model = new DefaultDotGraphModel(graph);
        DotWriter.persist(model, new FileOutputStream(fileName), Encodings.UTF_8);
    }


    // private helper methods --------------------------------------------------

    private static DotGraph createGraph(RunnerTree tree) {
        DotGraph graph = DotGraph.newDirectedGraph("nodes").withRankDir(RankDir.LR).withNodeShape(NodeShape.record);
        addRunnerGroup(tree.getRoot(), graph);
        return graph;
    }

    private static DotNode addRunnerGroup(RunnerGroup runnerGroup, DotGraph graph) {
        DotNode dotGroup = graph.newNode("\"" + runnerGroup.getName() + "\"");
        DotNode connectionPoint = dotGroup;
        // iterate all children
        for (RunnerNode child : runnerGroup.getChildren()) {
            // create child node
            DotNode dotChild;
            if (child instanceof RunnerGroup) {
                dotChild = addRunnerGroup((RunnerGroup) child, graph);
            } else {
                dotChild = addRunnerLeaf((RunnerLeaf) child, graph);
            }
            // create edge to new node
            if (runnerGroup.isParallel() || connectionPoint == dotGroup) { //NOSONAR
                dotGroup.newEdgeTo(dotChild).withArrowTail(ArrowShape.ediamond).withArrowHead(ArrowShape.open);
            } else {
                connectionPoint.newEdgeTo(dotChild).withArrowHead(ArrowShape.open);
            }
            if (!runnerGroup.isParallel()) {
                connectionPoint = dotChild;
            }
        }
        return dotGroup;
    }

    private static DotNode addRunnerLeaf(RunnerLeaf leaf, DotGraph graph) {
        DotNode node = graph.newNode("\"" + leaf.getName() + "\"");
        return node;
    }

}
