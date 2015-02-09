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
package org.aludratest.service.file;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;

import org.aludratest.exception.AutomationException;
import org.aludratest.impl.log4testing.ElementType;
import org.aludratest.impl.log4testing.TechnicalLocator;
import org.aludratest.service.Interaction;

/**
 * {@link Interaction} interface of the {@link FileService}.
 * @author Volker Bergmann
 */
public interface FileInteraction extends Interaction {

    /** Provides the root folder of the service instance. 
     *  @return the root folder of the {@link FileService}. */
    String getRootFolder();

    /** Lists all child elements of the given folder. 
     *  @param filePath the path of the file of which to get children 
     *  @return a {@link List} of the child objects of the given file
     */
    List<String> getChildren(@TechnicalLocator String filePath);

    /** Lists all child elements of the given folder which match the given regular expression. 
     *  @param filePath the path of the file of which to get the children 
     *  @param filterRegex 
     *  @return a {@link List} of the child objects of the given file
     */
    List<String> getChildren(@TechnicalLocator String filePath, String filterRegex);

    /** Lists all child elements of the given folder which match the filter. 
     *  @param filePath the path of the file of which to get the children
     *  @param filter 
     *  @return a {@link List} of the child objects of the given file
     */
    List<String> getChildren(@TechnicalLocator String filePath, FileFilter filter);

    /** Creates a directory. 
     *  @param directoryPath the path of the directory to create 
     */
    void createDirectory(@TechnicalLocator String directoryPath);

    /** Renames or moves a file or folder. 
     *  @param fromPath the file/folder to rename/move
     *  @param toPath the new name/location of the file/folder
     *  @param overwrite flag which indicates if an existing file may be overwritten by the operation
     *  @return true if a formerly existing file was overwritten. 
     *  @throws org.aludratest.service.file.exception.FilePresentException if a file was already present and overwriting was disabled. */
    boolean move(@TechnicalLocator String fromPath, String toPath, boolean overwrite);

    /** Copies a file or folder.
     *  @param fromPath the file/folder to copy
     *  @param toPath the name/location of the copy
     *  @param overwrite flag which indicates if an existing file may be overwritten by the operation
     *  @return true if a formerly existing file was overwritten. 
     *  @throws org.aludratest.service.file.exception.FilePresentException if a file was already present and overwriting was disabled. */
    boolean copy(@TechnicalLocator String fromPath, String toPath, boolean overwrite);

    /** Deletes a file or folder. 
     *  @param filePath the path of the file to delete
     */
    void delete(@TechnicalLocator String filePath);

    /** Creates a text file with the provided content.
     *  @param filePath the path of the file to save
     *  @param text the text to save as file content
     *  @param overwrite flag which indicates if an existing file may be overwritten by the operation
     *  @return true if a formerly existing file was overwritten. 
     *  @throws org.aludratest.service.file.exception.FilePresentException if a file was already present and overwriting was disabled. */
    boolean writeTextFile(@TechnicalLocator String filePath, String text, boolean overwrite);

    /** Creates a text file and writes to it all content provided by the source Reader.
     *  @param filePath the path of the file to save
     *  @param source a {@link Reader} which provides the file content
     *  @param overwrite flag which indicates if an existing file may be overwritten by the operation
     *  @return true if a formerly existing file was overwritten. 
     *  @throws org.aludratest.service.file.exception.FilePresentException if a file was already present and overwriting was disabled. */
    boolean writeTextFile(@TechnicalLocator String filePath, Reader source, boolean overwrite);

    /** Creates a binary file with the provided content.
     *  @param filePath the path of the file to save
     *  @param bytes the file content to write
     *  @param overwrite flag which indicates if an existing file may be overwritten by the operation
     *  @return true if a formerly existing file was overwritten. 
     *  @throws org.aludratest.service.file.exception.FilePresentException if a file was already present and overwriting was disabled. */
    boolean writeBinaryFile(@TechnicalLocator String filePath, byte[] bytes, boolean overwrite);

    /** Creates a binary file and writes to it all content provided by the source {@link InputStream}.
     *  @param filePath the path of the file to save
     *  @param source an {@link InputStream} which provides the content to write to the file
     *  @param overwrite flag which indicates if an existing file may be overwritten by the operation
     *  @return true if a formerly existing file was overwritten. 
     *  @throws org.aludratest.service.file.exception.FilePresentException if a file was already present and overwriting was disabled. */
    boolean writeBinaryFile(@TechnicalLocator String filePath, InputStream source, boolean overwrite);

    /** Reads a text file and provides its content as String. 
     *  @param filePath the path of the file to read 
     *  @return the content of the file */
    String readTextFile(@TechnicalLocator String filePath);

    /** Creates a {@link Reader} for accessing the content of a text file. 
     *  @param filePath the path of the file to read 
     *  @return a reader for the text file */
    BufferedReader getReaderForTextFile(@TechnicalLocator String filePath);

    /** Reads a binary file and provides its content as an array of bytes. 
     *  @param filePath the path of the file to read 
     *  @return the file content as byte array */
    byte[] readBinaryFile(@TechnicalLocator String filePath);

    /** Creates an {@link InputStream} for accessing the content of a file. 
     *  @param filePath the path of the file for which to get an input stream 
     *  @return an {@link InputStream} for accessing the file */
    InputStream getInputStreamForFile(@TechnicalLocator String filePath);

    /** Polls the file system for a given file until it is found or a timeout is exceeded.
     *  Timeout and the maximum number of polls are retrieved from the 
     *  {@link org.aludratest.service.file.impl.FileServiceConfiguration}. 
     *  @param elementType 
     *  @param filePath the path of the file for which to wait 
     *  @throws AutomationException if the file was not found within the timeout */
    void waitUntilExists(
            @ElementType String elementType, 
            @TechnicalLocator String filePath);

    /** Polls the file system for a given file until it has disappeared or a timeout is exceeded.
     *  Timeout and the maximum number of polls are retrieved from the 
     *  {@link org.aludratest.service.file.impl.FileServiceConfiguration}. 
     *  @param filePath the path of the file for which to wait until absence 
     *  @throws AutomationException if the file was not found within the timeout */
    void waitUntilNotExists(@TechnicalLocator String filePath);

    /** Polls the given directory until the filter finds a match or a timeout is exceeded.
     *  Timeout and the maximum number of polls are retrieved from the 
     *  {@link org.aludratest.service.file.impl.FileServiceConfiguration}.
     *  @param parentPath the path of the directory in which to search for the file 
     *  @param filter a filter object that decides which file is to be accepted
     *  @return the file path of the first file that was accepted by the filter
     *  @throws AutomationException if the file was not found within the timeout */
    String waitForFirstMatch(@TechnicalLocator String parentPath, FileFilter filter);

}
