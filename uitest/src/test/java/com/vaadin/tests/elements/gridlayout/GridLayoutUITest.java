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
package com.vaadin.tests.elements.gridlayout;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.GridLayoutElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class GridLayoutUITest extends SingleBrowserTest {

    @Test
    public void getRows() {
        openTestURL();
        Assert.assertEquals(1, $(GridLayoutElement.class)
                .id(GridLayoutUI.ONE_ROW_ONE_COL).getRowCount());
        Assert.assertEquals(10, $(GridLayoutElement.class)
                .id(GridLayoutUI.TEN_ROWS_TEN_COLS).getRowCount());
    }

    @Test
    public void getColumns() {
        openTestURL();
        Assert.assertEquals(1, $(GridLayoutElement.class)
                .id(GridLayoutUI.ONE_ROW_ONE_COL).getColumnCount());
        Assert.assertEquals(10, $(GridLayoutElement.class)
                .id(GridLayoutUI.TEN_ROWS_TEN_COLS).getColumnCount());
    }
}
