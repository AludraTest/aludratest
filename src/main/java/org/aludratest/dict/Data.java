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
package org.aludratest.dict;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Stack;

/**
 * Parent class for all data classes.
 * @author Volker Bergmann
 */
public abstract class Data {

    /**
     * Helper method for subclasses to easily implement <code>toString()</code>,
     * without having to adjust it for every additional field which is added to
     * the class. <br>
     * An easy implementation of the <code>toString()</code> method could look
     * like this:
     * 
     * <pre>
     * public String toString() {
     *     return buildDescriptionString(new StringBuilder()).toString();
     * }
     * </pre>
     * 
     * @param sb
     *            A StringBuilder to which the description string for this
     *            object is appended to.
     * 
     * @return The StringBuilder which has been passed as parameter, to allow
     *         fluent programming.
     */
    protected final StringBuilder buildDescriptionString(StringBuilder sb) {
        return buildDescriptionString(sb, new Stack<Class<? extends Data>>());
    }

    private final StringBuilder buildDescriptionString(StringBuilder sb, Stack<Class<? extends Data>> recursionCheck) {
        Class<?> cls = getClass();
        sb.append(cls.getSimpleName());
        sb.append(" [");

        // start with most special fields, work through parent classes afterwards
        do {
            for (Field f : cls.getDeclaredFields()) {
                if (sb.charAt(sb.length() - 1) != '[') {
                    sb.append(", ");
                }
                boolean isAcc = f.isAccessible();
                Object value;
                try {
                    f.setAccessible(true);
                    value = f.get(this);
                }
                catch (Exception e) { // NOSONAR
                    value = e;
                }
                finally {
                    try {
                        f.setAccessible(isAcc);
                    }
                    catch (Throwable t) { // NOSONAR
                        // ignore
                    }
                }

                sb.append(f.getName()).append("=");
                appendObject(sb, value, recursionCheck);
            }
            cls = cls.getSuperclass();
        }
        while (cls != null && cls != Data.class);

        sb.append("]");
        return sb;
    }


    private static void appendObject(StringBuilder sb, Object value, Stack<Class<? extends Data>> recursionCheck) {
        if (value == null) {
            sb.append("null");
        }
        else if (value instanceof String) {
            sb.append("\"").append(value).append("\"");
        }
        else if (value instanceof Data) {
            Data data = (Data) value;
            if (recursionCheck.contains(data.getClass())) {
                sb.append("<endless recursion>");
            }
            else {
                recursionCheck.push(data.getClass());
                data.buildDescriptionString(sb, recursionCheck);
                recursionCheck.pop();
            }
        }
        else if (value instanceof Collection) {
            sb.append("[");
            for (Object o : ((Collection<?>) value)) {
                if (sb.charAt(sb.length() - 1) != '[') {
                    sb.append(", ");
                }
                appendObject(sb, o, recursionCheck);
            }
            sb.append("]");
        }
        else if (value instanceof Exception) {
            sb.append("<access error>");
        }
        else {
            sb.append("<unknown field type>");
        }

    }

}
