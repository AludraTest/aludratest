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
package org.aludratest.service.gui.component.base;

import static org.junit.Assert.assertEquals;

import org.aludratest.service.gui.component.Button;
import org.aludratest.service.gui.web.AludraWebGUI;
import org.aludratest.service.gui.web.uimap.UIMap;
import org.aludratest.service.locator.element.IdLocator;
import org.junit.Test;

@SuppressWarnings("javadoc")
public abstract class AbstractIFrameTest extends GUITest {

    @Override
    protected String getTestPageUrl() {
        return "http://localhost:8080/iframe-master.html";
    }

    @Test
    public void testIFrames() {
        assertEquals("Find", new IFrameUIMap(aludraWebGUI).findButton().getText());
        aludraWebGUI.perform().switchToIFrame(new IdLocator("testframe"));
        assertEquals("Find #1", new IFrameUIMap(aludraWebGUI).findButton().getText());
        aludraWebGUI.perform().switchToIFrame(new IdLocator("testframe"));
        assertEquals("Find #3", new IFrameUIMap(aludraWebGUI).findButton().getText());
        aludraWebGUI.perform().switchToIFrame(null);
        assertEquals("Find", new IFrameUIMap(aludraWebGUI).findButton().getText());
        aludraWebGUI.perform().switchToIFrame(new IdLocator("testframe2"));
        assertEquals("Find #2", new IFrameUIMap(aludraWebGUI).findButton().getText());
    }

    private static class IFrameUIMap extends UIMap {

        public IFrameUIMap(AludraWebGUI aludraWebGUI) {
            super(aludraWebGUI);
        }

        public Button findButton() {
            return new Button(aludraGUI, new IdLocator("findbutton"));
        }

    }

}
