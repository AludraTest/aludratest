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
package org.aludratest.service.gui.component;

import java.io.InputStream;

import org.aludratest.service.gui.component.impl.ValueComponent;

/** Represents a file field in a GUI.
 * @author Volker Bergmann */
public interface FileField extends Element<FileField>, ValueComponent {

    /** Saves the {@link InputStream}'s content in a new file with the given name.
     * @param fileName the name by which to save the file
     * @param in the provider of the file content to save */
    public void setResourceNameAndContent(String fileName, InputStream in);

}
