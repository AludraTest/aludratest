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
package org.aludratest.service.jms;

import org.aludratest.service.AludraService;

/**
 * Specialization of the {@link AludraService} interface 
 * which provides access to features specific for JMS.
 * @deprecated The implementation is not yet functional; implementation was suspended in favour of other tasks!
 * @author Volker Bergmann
 */
@Deprecated
public interface AludraJMS extends AludraService {

    /** Provides a {@link JMSInteraction} instance 
     *  for performing JMS interactions. */
    @Override
    public JMSInteraction perform();

    /** Provides a JMSVerification instance 
     *  for performing JMS related verifications */
    @Override
    public JMSVerification verify();

    /** Provides a JMSCondition instance 
     *  for performing JMS related checks */
    @Override
    public JMSCondition check();

}
