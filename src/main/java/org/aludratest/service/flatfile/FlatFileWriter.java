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
package org.aludratest.service.flatfile;

import org.aludratest.content.flat.data.FlatFileBeanData;
import org.aludratest.service.file.FileStream;

/** 
 * BeanFlatFileWriter implementation that serves as business delegate for the test developer. 
 * @param <E> the generic type to return as 'this'
 * @param <C> the parent type of the flat file beans to write
 * @author Volker Bergmann
 */
public abstract class FlatFileWriter<C extends FlatFileBeanData, E extends FlatFileWriter<C, E>> 
            extends FileStream<FlatFileWriter<C, E>> {

    private FlatFileService service;

    /** The writer id used by the service internally. */
    private Object writerId;

    /** Constructor.
     *  @param filePath the file path of the file to create
     *  @param service the {@link FlatFileService} to use. 
     *  @param overwrite Flag for permitting overwriting of existing files */
    public FlatFileWriter(String filePath, FlatFileService service, boolean overwrite) {
        super(filePath, service.getFileService());
        this.service = service;
        this.writerId = service.perform().createWriter(filePath, overwrite);
        verifyState(); //NOSONAR
    }

    /** Formats a bean object as flat file entry and writes it to the associated flat file. */
    protected E writeRow(C bean) {
        this.service.perform().writeRow(bean, this.writerId);
        return this.verifyState();
    }

    /** Closes the writer. 
     *  @return a reference to 'this' */
    public E close() {
        this.service.perform().closeWriter(this.writerId);
        return this.verifyState();
    }

    @SuppressWarnings("unchecked")
    @Override
    public E verifyState() {
        // no possibility of having an invalid state
        return (E) this;
    }

    // java.lang.Object overrides ----------------------------------------------

    /** Creates a string representation of the writer */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "#" + writerId;
    }

}
