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
package org.aludratest.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** This annotation marks an <b>Interface</b> as Aludra Service interface, i.e. it can be used in a call to
 * {@link org.aludratest.testcase.AludraTestCase#getService(ComponentId)} as interface class in the component ID. <br>
 * The annotation provides a human-readable name and a description of the service which will be used for documentation purposes
 * (e.g. the Maven site of AludraTest). It has no technical effect. <br>
 * This annotation should only be applied to interfaces extending {@link org.aludratest.service.AludraService}.
 * 
 * @author falbrech */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ServiceInterface {

    public String name();

    public String description();

}
