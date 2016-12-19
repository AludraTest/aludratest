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
package org.aludratest.scheduler.util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.aludratest.scheduler.TestClassFilter;

/** Parses a filter string.
 * 
 * @author falbrech */
public class FilterParser {

    /** Parses the given filter string.
     * 
     * @param inputString Filter string to parse.
     * @return A test class filter representing the filter string.
     * 
     * @throws ParseException If a parsing error occurs, i.e. the input filter string is invalid. */
    public TestClassFilter parse(String inputString) throws ParseException {
        List<TokenDeclaration> tokens = new FilterTokenizer().tokenize(inputString);
        return parseOrFilter(tokens);
    }

    private TestClassFilter parseOrFilter(List<TokenDeclaration> tokens) throws ParseException {
        List<TestClassFilter> filters = new ArrayList<TestClassFilter>();

        while (!tokens.isEmpty()) {
            filters.add(parseAndFilter(tokens));
            if (!tokens.isEmpty()) {
                consumeToken(tokens, FilterTokenizer.TOKEN_OR);
                assertNotEmpty(tokens);
            }
        }

        return filters.size() == 1 ? filters.get(0) : new OrTestClassFilter(filters);
    }

    private TestClassFilter parseAndFilter(List<TokenDeclaration> tokens) throws ParseException {
        assertNotEmpty(tokens);

        List<TestClassFilter> filters = new ArrayList<TestClassFilter>();

        while (!tokens.isEmpty() && tokens.get(0).token != FilterTokenizer.TOKEN_OR) {
            // get attribute name
            TokenDeclaration token = consumeToken(tokens, FilterTokenizer.TOKEN_IDENTIFIER);
            String attributeName = token.sequence.trim();
            token = consumeToken(tokens, 0);
            boolean invert = false;
            if (token.token == FilterTokenizer.TOKEN_NOT_EQUAL) {
                invert = true;
            }
            else {
                assertToken(token, FilterTokenizer.TOKEN_EQUAL);
            }
            List<String> values = parseValues(tokens);
            filters.add(new AttributeBasedTestClassFilter(attributeName, values, invert));
            if (!tokens.isEmpty() && tokens.get(0).token != FilterTokenizer.TOKEN_OR) {
                consumeToken(tokens, FilterTokenizer.TOKEN_SEMICOLON);
            }
        }

        if (filters.isEmpty() && !tokens.isEmpty()) {
            throw new ParseException("Unexpected token " + tokens.get(0) + " at offset " + tokens.get(0).offset,
                    tokens.get(0).offset);
        }

        return new AndTestClassFilter(filters);
    }

    private List<String> parseValues(List<TokenDeclaration> tokens) throws ParseException {
        assertNotEmpty(tokens);

        // either bracket, or single value
        TokenDeclaration token = consumeToken(tokens, 0);
        if (token.token == FilterTokenizer.TOKEN_OPEN_BRACKET) {
            List<String> values = parseValueList(tokens);
            consumeToken(tokens, FilterTokenizer.TOKEN_CLOSE_BRACKET);
            return values;
        }

        assertToken(token, FilterTokenizer.TOKEN_IDENTIFIER);
        return Collections.singletonList(token.sequence.trim());
    }

    private List<String> parseValueList(List<TokenDeclaration> tokens) throws ParseException {
        assertNotEmpty(tokens);

        List<String> result = new ArrayList<String>();

        while (tokens.get(0).token != FilterTokenizer.TOKEN_CLOSE_BRACKET) {
            TokenDeclaration token = consumeToken(tokens, FilterTokenizer.TOKEN_IDENTIFIER);
            result.add(token.sequence.trim());
            assertNotEmpty(tokens);
            if (tokens.get(0).token != FilterTokenizer.TOKEN_CLOSE_BRACKET) {
                consumeToken(tokens, FilterTokenizer.TOKEN_COMMA);
            }
        }

        return result;
    }

    private TokenDeclaration consumeToken(List<TokenDeclaration> tokens, int expectedType) throws ParseException {
        assertNotEmpty(tokens);

        TokenDeclaration token = tokens.remove(0);
        if (expectedType > 0) {
            assertToken(token, expectedType);
        }
        return token;
    }

    private void assertToken(TokenDeclaration token, int expectedType) throws ParseException {
        if (token.token != expectedType) {
            throw new ParseException("Unexpected token " + token.sequence + " at offset " + token.offset, token.offset);
        }
    }

    private void assertNotEmpty(List<TokenDeclaration> tokens) throws ParseException {
        if (tokens.isEmpty()) {
            throw new ParseException("Unexpected end of filter string", 0);
        }
    }

    private static class FilterTokenizer {

        private static final int TOKEN_OR = 1;

        private static final int TOKEN_SEMICOLON = 2;

        private static final int TOKEN_OPEN_BRACKET = 3;

        private static final int TOKEN_CLOSE_BRACKET = 4;

        private static final int TOKEN_IDENTIFIER = 5;

        private static final int TOKEN_EQUAL = 6;

        private static final int TOKEN_NOT_EQUAL = 7;

        private static final int TOKEN_COMMA = 8;

        private List<TokenInfo> tokenInfos = new ArrayList<TokenInfo>();

        public FilterTokenizer() {
            addTokenInfo("\\|", TOKEN_OR);
            addTokenInfo(";", TOKEN_SEMICOLON);
            addTokenInfo(",", TOKEN_COMMA);
            addTokenInfo("\\(", TOKEN_OPEN_BRACKET);
            addTokenInfo("\\)", TOKEN_CLOSE_BRACKET);
            addTokenInfo("!=", TOKEN_NOT_EQUAL);
            addTokenInfo("=", TOKEN_EQUAL);
            addTokenInfo("([^\\|;=!\\(\\)\\[\\],]+)|\\[\\]", TOKEN_IDENTIFIER);
        }

        public List<TokenDeclaration> tokenize(String text) throws ParseException {
            List<TokenDeclaration> result = new ArrayList<TokenDeclaration>();
            String input = text;
            int offset = 0;
            while (!"".equals(input)) {
                // remove spaces before and after each token
                while (input.startsWith(" ")) {
                    input = input.substring(1);
                    offset++;
                }
                if ("".equals(input)) {
                    break;
                }

                boolean match = false;
                for (TokenInfo info : tokenInfos) {
                    Matcher m = info.regex.matcher(input);
                    if (m.find()) {
                        match = true;
                        String tok = m.group();
                        result.add(new TokenDeclaration(info.token, tok, offset));
                        input = m.replaceFirst("");
                        offset += tok.length();
                        break;
                    }
                }

                if (!match) {
                    throw new ParseException("Unexpected character '" + input.charAt(0) + "' at offset " + offset, offset);
                }
            }

            return result;
        }

        private void addTokenInfo(String regex, int token) {
            tokenInfos.add(new TokenInfo(Pattern.compile("^(" + regex + ")"), token));
        }
    }

    private static class TokenInfo {

        private final Pattern regex;

        private final int token;

        public TokenInfo(Pattern regex, int token) {
            this.regex = regex;
            this.token = token;
        }
    }

    private static class TokenDeclaration {
        public final int token;
        public final String sequence;
        public final int offset;

        public TokenDeclaration(int token, String sequence, int offset) {
            this.token = token;
            this.sequence = sequence;
            this.offset = offset;
        }
    }
}
