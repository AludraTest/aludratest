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
package org.aludratest.service.jms.impl;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.aludratest.exception.AccessFailure;
import org.aludratest.exception.AutomationException;
import org.aludratest.exception.TechnicalException;
import org.aludratest.service.AbstractAludraService;
import org.aludratest.service.jms.AludraJMS;
import org.aludratest.service.jms.JMSCondition;
import org.aludratest.service.jms.JMSInteraction;
import org.aludratest.service.jms.JMSVerification;

/**
 * Default implementation for the {@link AludraJMS} interface.
 * @author Volker Bergmann
 * @deprecated The implementation is not yet functional; implementation was suspended in favour of other tasks!
 */

@Deprecated
public class AludraJMSImpl extends AbstractAludraService implements AludraJMS {

    private InitialContext initialContext;

    private Connection connection;
    private Session session;

    private JMSActionImpl action;

    @Override
    public void initService() {
        try {
            this.initialContext = new InitialContext();
        } catch (NamingException e) {
            throw new AutomationException("Error creating initial context", e);
        }
        ConnectionFactory connectionFactory = (ConnectionFactory) lookup("jms/ConnectionFactory");
        try {
            this.connection = connectionFactory.createConnection();
        } catch (JMSException e) {
            throw new AccessFailure("Error creating JMS connection", e);
        }
        try {
            int acknowledgeMode = Session.AUTO_ACKNOWLEDGE;
            boolean transacted = true;
            this.session = connection.createSession(transacted, acknowledgeMode);
        } catch (JMSException e) {
            throw new TechnicalException("Error creating JMS session", e);
        }
        this.action = new JMSActionImpl(this);
    }

    @Override
    public String getDescription() {
        return "JMS Service";
    }

    @Override
    public void close() {
        try {
            session.close();
            connection.close();
        } catch (JMSException e) {
            throw new TechnicalException("Error closing JMS resources", e);
        }
    }

    /** @return the {@link #session} */
    public Session getSession() {
        return session;
    }

    @Override
    public JMSInteraction perform() {
        return action;
    }

    @Override
    public JMSVerification verify() {
        return action;
    }

    @Override
    public JMSCondition check() {
        return action;
    }

    // package-visible helpers -----------------------------------------------------------------------------------------

    Destination getDestination(String destinationName) {
        return (Destination) lookup(destinationName);
    }

    Object lookup(String name) {
        try {
            return initialContext.lookup(name);
        } catch (NamingException e) {
            throw new AutomationException("Managed object not found", e);
        }
    }

}
