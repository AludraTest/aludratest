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
package org.aludratest.service.gui.web;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

import org.databene.commons.Base64Codec;

public class HttpHeaderFormat extends Format {

    private static final long serialVersionUID = -1496577708711169338L;

    @Override
    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        if (obj == null) {
            return toAppendTo;
        }

        if (!(obj instanceof String)) {
            toAppendTo.append(obj.toString());
            return toAppendTo;
        }

        String value = (String) obj;
        if (value.startsWith("Basic ")) {
            String authValue = value.substring("Basic ".length());
            try {
                authValue = new String(Base64Codec.decode(authValue), "ISO-8859-1");
            }
            catch (Exception e) {
                // ignore; do not convert
            }
            toAppendTo.append("Basic ").append(authValue);
        }

        return toAppendTo;
    }

    @Override
    public Object parseObject(String source, ParsePosition pos) {
        throw new UnsupportedOperationException();
    }

}
