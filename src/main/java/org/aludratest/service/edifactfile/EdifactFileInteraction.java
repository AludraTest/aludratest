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
package org.aludratest.service.edifactfile;

import java.io.OutputStream;
import java.util.Map;

import org.aludratest.impl.log4testing.AttachParameter;
import org.aludratest.impl.log4testing.AttachResult;
import org.aludratest.impl.log4testing.ElementName;
import org.aludratest.impl.log4testing.ElementType;
import org.aludratest.impl.log4testing.TechnicalArgument;
import org.aludratest.impl.log4testing.TechnicalLocator;
import org.aludratest.service.Interaction;
import org.databene.edifatto.EdiFormatSymbols;
import org.databene.edifatto.model.Interchange;

/** 
 * Parses and saves EDIFACT and X12 documents from and to streams.
 * @author Volker Bergmann
 */
public interface EdifactFileInteraction extends Interaction {
    
    /** Polls the file system until a file at the given path is found 
     *  or a timeout occurs. 
     *  @param elementType 
     *  @param elementName 
     *  @param filePath */
    void waitUntilExists(
            @ElementType String elementType, 
            @ElementName String elementName, 
            @TechnicalLocator String filePath);
    
    /** Polls the file system until no file is found at the given path.
     *  @param elementType 
     *  @param elementName 
     *  @param filePath */
    void waitUntilNotExists(
            @ElementType String elementType, 
            @ElementName String elementName, 
            @TechnicalLocator String filePath);
    
    /** Deletes a file
     *  @param elementType 
     *  @param elementName 
     *  @param filePath the path of the file to delete */
    void delete(
            @ElementType String elementType, 
            @ElementName String elementName, 
            @TechnicalLocator String filePath);
    
    /** Writes an EDIFACT or X12 interchange to an {@link OutputStream}. 
     *  @param elementType 
     *  @param elementName 
     *  @param interchange the interchange to persist 
     *  @param filePath the path of the file to write
     *  @param overwrite flag that indicates whether a pre-existing file may be overwritten */
    void writeInterchange(
            @ElementType String elementType, 
            @ElementName String elementName, 
            @AttachParameter("Interchange") Interchange interchange, 
            @TechnicalLocator String filePath, 
            @TechnicalArgument boolean overwrite);
    
    /** Creates an {@link Interchange} based on a template file and a variable tree
     *  @param elementType 
     *  @param elementName 
     *  @param templateUri the path of the template file
     *  @param symbols the symbols to use
     *  @param variables the variable tree
     *  @return a new Interchange containing the information from the variable tree */
    @AttachResult("Created Interchange")
    Interchange createInterchange(
            @ElementType String elementType, 
            @ElementName String elementName, 
            @TechnicalLocator String templateUri, 
            @TechnicalArgument EdiFormatSymbols symbols, 
            @TechnicalArgument Map<String, Object> variables);
    
    /** Reads an {@link Interchange} from a file system.
     *  @param elementType 
     *  @param elementName 
     *  @param filePath the full path of the file to read
     *  @return an {@link Interchange} data structure 
     *  	that contains the EDI data mapped to a tree structure */
    @AttachResult("Read Interchange")
    Interchange readInterchange(
            @ElementType String elementType, 
            @ElementName String elementName, 
            @TechnicalLocator String filePath);
    
}
