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

import java.util.HashMap;
import java.util.Map;

import org.aludratest.exception.TechnicalException;
import org.aludratest.service.edifactfile.data.EdifactData;
import org.aludratest.service.file.FileStream;
import org.aludratest.service.flatfile.FlatFileService;
import org.databene.edifatto.EdiFormatSymbols;
import org.databene.edifatto.model.Interchange;

/**
 * Generic parent class for all specific Edifact file writers.
 * @author Volker Bergmann
 * @param <C> The java type of the content class
 * @param <E> Place holder for the child class type
 */
public class EdifactFileWriter<C extends EdifactData, E extends EdifactFileWriter<C, E>> 
        extends FileStream<EdifactFileWriter<C, E>> {
    
    protected final String templateUri;
    protected final boolean overwrite;
    protected final EdifactFileService service;
    
    private State state;
    
    /**
     * Constructor
     * @param filePath The file path of the Edifact file to create
     * @param overwrite A flag indicating if an existing files of the same name may be overwritten 
     * @param templateUri the URI of the template to use for file formatting
     * @param service A reference to the underlying {@link FlatFileService}
     */
    public EdifactFileWriter(String filePath, boolean overwrite, String templateUri, EdifactFileService service) {
        super(filePath, service.getFileService());
        this.overwrite = overwrite;
        this.templateUri = templateUri;
        this.service = service;
        this.state = State.CREATED;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public E verifyState() {
        // nothing to do here, the state is checked within each method call
        return (E) this;
    }
    
    /**
     * Writes to content to the related file and closes this writer.
     * @param content the content to write
     * @return a reference to itself (this)
     */
    @SuppressWarnings("unchecked")
    public E writeContentAndClose(C content) {
        assertState(State.CREATED);
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("interchange", content);
        Interchange interchange = service.perform().createInterchange(
                elementType, null, templateUri, EdiFormatSymbols.EDIFACT, variables);
        service.perform().writeInterchange(elementType, null, interchange, filePath, overwrite);
        this.state = State.CLOSED;
        return (E) this;
    }
    
    /** @return a reference to itself (this) */
    @Override
    @SuppressWarnings("unchecked")
    public E waitUntilNotExists() {
        assertState(State.CLOSED);
        service.perform().waitUntilNotExists(elementType, null, filePath);
        return (E) this;
    }
    
    
    // private helper methods --------------------------------------------------
    
    private void assertState(State expected) {
        if (state != expected) {
            throw new TechnicalException("Illegal state, found: " + this.state +", " +
            		"while expecting: " + expected);
        }
    }
    
    private enum State {
        CREATED, CLOSED
    }
    
}
