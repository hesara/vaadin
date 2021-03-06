/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.components.combobox;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.v7.testbench.customelements.ComboBoxElement;

@SuppressWarnings("serial")
public class ComboSelectedValueBeyondTheFirstDropdownPageTest
        extends MultiBrowserTest {

    @Test
    public void valueOnSecondPageIsSelected() {
        openTestURL();

        ComboBoxElement comboBoxWebElement = $(ComboBoxElement.class).first();

        comboBoxWebElement.openNextPage();
        comboBoxWebElement.selectByText("Item 19");

        assertThat($(LabelElement.class).id("value").getText(), is("Item 19"));
    }
}
