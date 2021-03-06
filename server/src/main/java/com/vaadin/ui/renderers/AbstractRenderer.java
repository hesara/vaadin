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
package com.vaadin.ui.renderers;

import java.util.Objects;

import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.AbstractExtension;
import com.vaadin.server.JsonCodec;
import com.vaadin.ui.Grid.Column;

import elemental.json.JsonValue;

/**
 * An abstract base class for server-side
 * {@link com.vaadin.ui.renderers.Renderer Grid renderers}. This class currently
 * extends the AbstractExtension superclass, but this fact should be regarded as
 * an implementation detail and subject to change in a future major or minor
 * Vaadin version.
 *
 * @param <T>
 *            the type this renderer knows how to present
 */
public abstract class AbstractRenderer<T> extends AbstractExtension
        implements Renderer<T> {
    private final Class<T> presentationType;

    private final String nullRepresentation;

    /**
     * Creates a new renderer with the given presentation type and null
     * representation.
     *
     * @param presentationType
     *            the data type that this renderer displays, not
     *            <code>null</code>
     * @param nullRepresentation
     *            a string that will be sent to the client instead of a regular
     *            value in case the actual cell value is <code>null</code>. May
     *            be <code>null</code>.
     */
    protected AbstractRenderer(Class<T> presentationType,
            String nullRepresentation) {
        Objects.requireNonNull(presentationType,
                "Presentation type cannot be null");
        this.presentationType = presentationType;
        this.nullRepresentation = nullRepresentation;
    }

    /**
     * Creates a new renderer with the given presentation type. No null
     * representation will be used.
     *
     * @param presentationType
     *            the data type that this renderer displays, not
     *            <code>null</code>
     */
    protected AbstractRenderer(Class<T> presentationType) {
        this(presentationType, null);
    }

    /**
     * This method is inherited from AbstractExtension but should never be
     * called directly with an AbstractRenderer.
     */
    @Deprecated
    @Override
    @SuppressWarnings("rawtypes")
    protected Class<Column> getSupportedParentType() {
        return Column.class;
    }

    /**
     * This method is inherited from AbstractExtension but should never be
     * called directly with an AbstractRenderer.
     */
    @Deprecated
    @Override
    protected void extend(AbstractClientConnector target) {
        super.extend(target);
    }

    @Override
    public Class<T> getPresentationType() {
        return presentationType;
    }

    @Override
    public JsonValue encode(T value) {
        if (value == null) {
            return encode(getNullRepresentation(), String.class);
        } else {
            return encode(value, getPresentationType());
        }
    }

    /**
     * Null representation for the renderer
     *
     * @return a textual representation of {@code null}
     */
    protected String getNullRepresentation() {
        return nullRepresentation;
    }

    /**
     * Encodes the given value to JSON.
     * <p>
     * This is a helper method that can be invoked by an {@link #encode(Object)
     * encode(T)} override if serializing a value of type other than
     * {@link #getPresentationType() the presentation type} is desired. For
     * instance, a {@code Renderer<Date>} could first turn a date value into a
     * formatted string and return {@code encode(dateString, String.class)}.
     *
     * @param value
     *            the value to be encoded
     * @param type
     *            the type of the value
     * @return a JSON representation of the given value
     */
    protected <U> JsonValue encode(U value, Class<U> type) {
        return JsonCodec
                .encode(value, null, type, getUI().getConnectorTracker())
                .getEncodedValue();
    }
}