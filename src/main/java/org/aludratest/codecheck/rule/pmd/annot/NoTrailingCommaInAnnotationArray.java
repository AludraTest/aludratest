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
package org.aludratest.codecheck.rule.pmd.annot;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTMemberValueArrayInitializer;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

import org.apache.commons.io.IOUtils;

/**
 * See <code>src/main/resources/pmd-rules-aludra.xml</code> or the project Site for rule description.
 * 
 * @author falbrech
 * 
 */
public class NoTrailingCommaInAnnotationArray extends AbstractJavaRule {

    @Override
    public Object visit(ASTAnnotation node, Object data) {
        // data should always be RuleContext, we need it
        if (!(data instanceof RuleContext)) {
            return super.visit(node, data);
        }

        RuleContext context = (RuleContext) data;

        for (ASTMemberValueArrayInitializer arrayInit : node.findDescendantsOfType(ASTMemberValueArrayInitializer.class)) {
            // the trailing comma is ignored by PMD (which is correct!), so we
            // have to check the source code itself
            checkForTrailingComma(arrayInit, context);
        }

        return super.visit(node, data);
    }

    @SuppressWarnings("resource")
    private InputStream getSourceCodeInputStream(RuleContext context) throws IOException {
        String fn = context.getSourceCodeFilename();
        if (fn.startsWith("file:")) {
            return new URL(fn).openStream();
        }

        File f = new File(fn); // NOSONAR
        return f.isFile() ? new FileInputStream(f) : null;
    }

    private String stripLineComments(String codeLine) {
        if (codeLine.contains("//")) {
            return codeLine.substring(0, codeLine.indexOf("//"));
        }
        return codeLine;
    }

    private void checkForTrailingComma(ASTMemberValueArrayInitializer node, RuleContext context) {
        int beginLine = node.getBeginLine();
        int endLine = node.getEndLine();
        int beginColumn = node.getBeginColumn();
        int endColumn = node.getEndColumn();

        InputStream in = null;
        try {
            File file = context.getSourceCodeFile();
            if (file == null || !file.isFile()) {
                // construct a URL from source code file name
                in = getSourceCodeInputStream(context);
                if (in == null) {
                    context.getReport().addError(
                            new ProcessingError("Cannot find source code file " + context.getSourceCodeFilename(), context
                                    .getSourceCodeFilename()));
                    return;
                }
            }
            else {
                in = new FileInputStream(file);
            }
            List<String> lines = IOUtils.readLines(in);
            if (endLine > lines.size()) {
                throw new IOException("Unexpected end of file " + (file != null ? file.getAbsolutePath() : ""));
            }
            StringBuilder sbContent = new StringBuilder();
            for (int line = beginLine; line <= endLine; line++) {
                String toAppend = null;

                // replace tab by 8 spaces (relatively hard coded in PMD)
                String spaces = "        ";
                String thisLine = lines.get(line - 1).replace("\t", spaces);

                if (line == beginLine && line == endLine) {
                    toAppend = thisLine.substring(beginColumn - 1, endColumn);
                }
                else if (line == beginLine) {
                    toAppend = thisLine.substring(beginColumn - 1);
                }
                else if (line == endLine) {
                    toAppend = thisLine.substring(0, endColumn);
                }
                else {
                    toAppend = thisLine;
                }

                // strip comments from line. Yeah, we have to... and yes, it is not 100% safe.
                // for example: /* // */, MyClass.class, } would be undetected.
                toAppend = stripLineComments(toAppend);

                sbContent.append(toAppend);
            }

            String arrayCode = sbContent.toString();
            // strip comments from code. Yeah, we have to...
            arrayCode = arrayCode.replaceAll("/\\*([^/]|/[^\\*])*\\*/", "");

            if (arrayCode.matches(".*,\\s*\\}\\s*")) {
                addViolationWithMessage(context, node, "Annotation Array initializers should not contain a trailing comma");
            }
        }
        catch (IOException e) {
            context.getReport().addError(new ProcessingError(e.getMessage(), context.getSourceCodeFilename()));
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (IOException ee) {
                // ignore
            }
        }
    }

}
