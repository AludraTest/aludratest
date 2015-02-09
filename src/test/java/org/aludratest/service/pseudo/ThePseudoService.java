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
package org.aludratest.service.pseudo;

import org.aludratest.service.AbstractAludraService;
import org.aludratest.service.Condition;
import org.aludratest.service.Verification;

/**
 * Pseudo service implementation for testing.
 * @author Volker Bergmann
 */
public class ThePseudoService extends AbstractAludraService implements PseudoService {

    /** The {@link PseudoInteraction} to return on calls to {@link #perform()}. */
    private PseudoInteraction interaction;

    /** Default constructor. */
    public ThePseudoService() {
        // nothing special to do yet
    }

    @Override
    public void initService() {
        this.interaction = new ThePseudoInteraction();
    }

    @Override
    public String getDescription() {
        return "Pseudo service";
    }

    /** Returns the {@link #interaction} */
    @Override
    public PseudoInteraction perform() {
        return interaction;
    }

    /** Dummy implementation returning null. */
    @Override
    public Verification verify() {
        return null;
    }

    /** Dummy implementation returning null. */
    @Override
    public Condition check() {
        return null;
    }

    /** Closes the service. */
    @Override
    public void close() {
    }

}
