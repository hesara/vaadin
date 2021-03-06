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
package com.vaadin.data;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.Binder.Binding;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.server.AbstractErrorMessage;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Slider;
import com.vaadin.ui.TextField;

/**
 * Book of Vaadin tests.
 *
 * @author Vaadin Ltd
 *
 */
public class BinderBookOfVaadinTest {

    private static class BookPerson {
        private String lastName;
        private String email, phone, title;
        private int yearOfBirth, salaryLevel;

        public BookPerson(int yearOfBirth, int salaryLevel) {
            this.yearOfBirth = yearOfBirth;
            this.salaryLevel = salaryLevel;
        }

        public BookPerson(BookPerson origin) {
            this(origin.yearOfBirth, origin.salaryLevel);
            lastName = origin.lastName;
            email = origin.email;
            phone = origin.phone;
            title = origin.title;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public int getYearOfBirth() {
            return yearOfBirth;
        }

        public void setYearOfBirth(int yearOfBirth) {
            this.yearOfBirth = yearOfBirth;
        }

        public int getSalaryLevel() {
            return salaryLevel;
        }

        public void setSalaryLevel(int salaryLevel) {
            this.salaryLevel = salaryLevel;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

    }

    public static class Trip {
        private Date returnDate;

        public Date getReturnDate() {
            return returnDate;
        }

        public void setReturnDate(Date returnDate) {
            this.returnDate = returnDate;
        }
    }

    private Binder<BookPerson> binder;

    private TextField field;
    private TextField phoneField;
    private TextField emailField;

    @Before
    public void setUp() {
        binder = new Binder<>();
        field = new TextField();
        phoneField = new TextField();
        emailField = new TextField();
    }

    @Test
    public void simpleEmailValidator() {
        binder.forField(field)
                // Explicit validator instance
                .withValidator(new EmailValidator(
                        "This doesn't look like a valid email address"))
                .bind(BookPerson::getEmail, BookPerson::setEmail);

        field.setValue("not-email");
        List<ValidationError<?>> errors = binder.validate();
        Assert.assertEquals(1, errors.size());
        Assert.assertEquals("This doesn't look like a valid email address",
                errors.get(0).getMessage());
        Assert.assertEquals("This doesn't look like a valid email address",
                ((AbstractErrorMessage) field.getErrorMessage()).getMessage());

        field.setValue("abc@vaadin.com");
        errors = binder.validate();
        Assert.assertEquals(0, errors.size());
        Assert.assertNull(field.getErrorMessage());
    }

    @Test
    public void nameLengthTest() {
        binder.forField(field)
                // Validator defined based on a lambda and an error message
                .withValidator(name -> name.length() >= 3,
                        "Last name must contain at least three characters")
                .bind(BookPerson::getLastName, BookPerson::setLastName);

        field.setValue("a");
        List<ValidationError<?>> errors = binder.validate();
        Assert.assertEquals(1, errors.size());
        Assert.assertEquals("Last name must contain at least three characters",
                errors.get(0).getMessage());
        Assert.assertEquals("Last name must contain at least three characters",
                ((AbstractErrorMessage) field.getErrorMessage()).getMessage());

        field.setValue("long last name");
        errors = binder.validate();
        Assert.assertEquals(0, errors.size());
        Assert.assertNull(field.getErrorMessage());
    }

    @Test
    public void chainedEmailValidator() {
        binder.forField(field)
                // Explicit validator instance
                .withValidator(new EmailValidator(
                        "This doesn't look like a valid email address"))
                .withValidator(email -> email.endsWith("@acme.com"),
                        "Only acme.com email addresses are allowed")
                .bind(BookPerson::getEmail, BookPerson::setEmail);

        field.setValue("not-email");
        List<ValidationError<?>> errors = binder.validate();
        // Only one error per field should be reported
        Assert.assertEquals(1, errors.size());
        Assert.assertEquals("This doesn't look like a valid email address",
                errors.get(0).getMessage());
        Assert.assertEquals("This doesn't look like a valid email address",
                ((AbstractErrorMessage) field.getErrorMessage()).getMessage());

        field.setValue("abc@vaadin.com");
        errors = binder.validate();
        Assert.assertEquals(1, errors.size());
        Assert.assertEquals("Only acme.com email addresses are allowed",
                errors.get(0).getMessage());
        Assert.assertEquals("Only acme.com email addresses are allowed",
                ((AbstractErrorMessage) field.getErrorMessage()).getMessage());

        field.setValue("abc@acme.com");
        errors = binder.validate();
        Assert.assertEquals(0, errors.size());
        Assert.assertNull(field.getErrorMessage());
    }

    @Test
    public void converterBookOfVaadinExample1() {
        TextField yearOfBirthField = new TextField();
        // Slider for integers between 1 and 10
        Slider salaryLevelField = new Slider("Salary level", 1, 10);

        Binding<BookPerson, String, String> b1 = binder
                .forField(yearOfBirthField);
        Binding<BookPerson, String, Integer> b2 = b1.withConverter(
                new StringToIntegerConverter("Must enter a number"));
        b2.bind(BookPerson::getYearOfBirth, BookPerson::setYearOfBirth);

        Binding<BookPerson, Double, Double> salaryBinding1 = binder
                .forField(salaryLevelField);
        Binding<BookPerson, Double, Integer> salaryBinding2 = salaryBinding1
                .withConverter(Double::intValue, Integer::doubleValue);
        salaryBinding2.bind(BookPerson::getSalaryLevel,
                BookPerson::setSalaryLevel);

        // Test that the book code works
        BookPerson bookPerson = new BookPerson(1972, 4);
        binder.bind(bookPerson);
        Assert.assertEquals(4.0, salaryLevelField.getValue().doubleValue(), 0);
        Assert.assertEquals("1,972", yearOfBirthField.getValue());

        bookPerson.setSalaryLevel(8);
        binder.load(bookPerson);
        Assert.assertEquals(8.0, salaryLevelField.getValue().doubleValue(), 0);
        bookPerson.setYearOfBirth(123);
        binder.load(bookPerson);
        Assert.assertEquals("123", yearOfBirthField.getValue());

        yearOfBirthField.setValue("2016");
        salaryLevelField.setValue(1.0);
        Assert.assertEquals(2016, bookPerson.getYearOfBirth());
        Assert.assertEquals(1, bookPerson.getSalaryLevel());
    }

    @Test
    public void converterBookOfVaadinExample2() {
        TextField yearOfBirthField = new TextField();

        binder.forField(yearOfBirthField)
                .withConverter(Integer::valueOf, String::valueOf,
                        // Text to use instead of the NumberFormatException
                        // message
                        "Please enter a number")
                .bind(BookPerson::getYearOfBirth, BookPerson::setYearOfBirth);

        binder.bind(new BookPerson(1900, 5));
        yearOfBirthField.setValue("abc");
        binder.validate();
        Assert.assertEquals("Please&#32;enter&#32;a&#32;number",
                yearOfBirthField.getComponentError().getFormattedHtmlMessage());
    }

    @Test
    public void crossFieldValidation_validateUsingBinder() {
        Binder<Trip> binder = new Binder<>();
        PopupDateField departing = new PopupDateField("Departing");
        PopupDateField returning = new PopupDateField("Returning");

        Binding<Trip, Date, Date> returnBinding = binder.forField(returning)
                .withValidator(
                        returnDate -> !returnDate.before(departing.getValue()),
                        "Cannot return before departing");

        returnBinding.bind(Trip::getReturnDate, Trip::setReturnDate);
        departing.addValueChangeListener(event -> returnBinding.validate());

        Calendar calendar = Calendar.getInstance();
        Date past = calendar.getTime();
        calendar.add(1, Calendar.DAY_OF_YEAR);
        Date before = calendar.getTime();
        calendar.add(1, Calendar.DAY_OF_YEAR);
        Date after = calendar.getTime();

        departing.setValue(before);
        returning.setValue(after);

        List<ValidationError<?>> errors = binder.validate();
        Assert.assertTrue(errors.isEmpty());
        Assert.assertNull(departing.getComponentError());
        Assert.assertNull(returning.getComponentError());

        // update returning => validation is done against this field
        returning.setValue(past);
        errors = binder.validate();

        Assert.assertFalse(errors.isEmpty());
        Assert.assertNotNull(returning.getComponentError());
        Assert.assertNull(departing.getComponentError());

        // set correct value back
        returning.setValue(before);
        errors = binder.validate();

        Assert.assertTrue(errors.isEmpty());
        Assert.assertNull(departing.getComponentError());
        Assert.assertNull(returning.getComponentError());

        // update departing => validation is done because of listener added
        departing.setValue(after);
        errors = binder.validate();

        Assert.assertFalse(errors.isEmpty());
        Assert.assertNotNull(returning.getComponentError());
        Assert.assertNull(departing.getComponentError());

    }

    @Test
    public void crossFieldValidation_validateUsingBinding() {
        Binder<Trip> binder = new Binder<>();
        PopupDateField departing = new PopupDateField("Departing");
        PopupDateField returning = new PopupDateField("Returning");

        Binding<Trip, Date, Date> returnBinding = binder.forField(returning)
                .withValidator(
                        returnDate -> !returnDate.before(departing.getValue()),
                        "Cannot return before departing");

        returnBinding.bind(Trip::getReturnDate, Trip::setReturnDate);
        departing.addValueChangeListener(event -> returnBinding.validate());

        Calendar calendar = Calendar.getInstance();
        Date past = calendar.getTime();
        calendar.add(1, Calendar.DAY_OF_YEAR);
        Date before = calendar.getTime();
        calendar.add(1, Calendar.DAY_OF_YEAR);
        Date after = calendar.getTime();

        departing.setValue(before);
        returning.setValue(after);

        Result<Date> result = returnBinding.validate();
        Assert.assertFalse(result.isError());
        Assert.assertNull(departing.getComponentError());

        // update returning => validation is done against this field
        returning.setValue(past);
        result = returnBinding.validate();

        Assert.assertTrue(result.isError());
        Assert.assertNotNull(returning.getComponentError());

        // set correct value back
        returning.setValue(before);
        result = returnBinding.validate();

        Assert.assertFalse(result.isError());
        Assert.assertNull(departing.getComponentError());

        // update departing => validation is done because of listener added
        departing.setValue(after);
        result = returnBinding.validate();

        Assert.assertTrue(result.isError());
        Assert.assertNotNull(returning.getComponentError());
    }

    @Test
    public void withStatusLabelExample() {
        Label emailStatus = new Label();

        String msg = "This doesn't look like a valid email address";
        binder.forField(field).withValidator(new EmailValidator(msg))
                .withStatusLabel(emailStatus)
                .bind(BookPerson::getEmail, BookPerson::setEmail);

        field.setValue("foo");
        binder.validate();

        Assert.assertTrue(emailStatus.isVisible());
        Assert.assertEquals(msg, emailStatus.getValue());

        field.setValue("foo@vaadin.com");
        binder.validate();

        Assert.assertFalse(emailStatus.isVisible());
        Assert.assertEquals("", emailStatus.getValue());
    }

    @Test
    public void withStatusChangeHandlerExample() {
        Label nameStatus = new Label();
        AtomicReference<ValidationStatusChangeEvent> event = new AtomicReference<>();

        String msg = "Full name must contain at least three characters";
        binder.forField(field).withValidator(name -> name.length() >= 3, msg)
                .withStatusChangeHandler(statusChange -> {
                    nameStatus.setValue(statusChange.getMessage().orElse(""));
                    // Only show the label when validation has failed
                    boolean error = statusChange
                            .getStatus() == ValidationStatus.ERROR;
                    nameStatus.setVisible(error);
                    event.set(statusChange);
                }).bind(BookPerson::getLastName, BookPerson::setLastName);

        field.setValue("aa");
        binder.validate();

        Assert.assertTrue(nameStatus.isVisible());
        Assert.assertEquals(msg, nameStatus.getValue());
        Assert.assertNotNull(event.get());
        ValidationStatusChangeEvent evt = event.get();
        Assert.assertEquals(ValidationStatus.ERROR, evt.getStatus());
        Assert.assertEquals(msg, evt.getMessage().get());
        Assert.assertEquals(field, evt.getSource());

        field.setValue("foo");
        binder.validate();

        Assert.assertFalse(nameStatus.isVisible());
        Assert.assertEquals("", nameStatus.getValue());
        Assert.assertNotNull(event.get());
        evt = event.get();
        Assert.assertEquals(ValidationStatus.OK, evt.getStatus());
        Assert.assertFalse(evt.getMessage().isPresent());
        Assert.assertEquals(field, evt.getSource());
    }

    @Test
    public void binder_saveIfValid() {
        BeanBinder<BookPerson> binder = new BeanBinder<BookPerson>(
                BookPerson.class);

        // Phone or email has to be specified for the bean
        Validator<BookPerson> phoneOrEmail = Validator.from(
                personBean -> !"".equals(personBean.getPhone())
                        || !"".equals(personBean.getEmail()),
                "A person must have either a phone number or an email address");
        binder.withValidator(phoneOrEmail);

        binder.forField(emailField).bind("email");
        binder.forField(phoneField).bind("phone");

        // Person person = // e.g. JPA entity or bean from Grid
        BookPerson person = new BookPerson(1900, 5);
        person.setEmail("Old Email");
        // Load person data to a form
        binder.load(person);

        Button saveButton = new Button("Save", event -> {
            // Using saveIfValid to avoid the try-catch block that is
            // needed if using the regular save method
            if (binder.saveIfValid(person)) {
                // Person is valid and updated
                // TODO Store in the database
            }
        });

        emailField.setValue("foo@bar.com");
        Assert.assertTrue(binder.saveIfValid(person));
        // Person updated
        Assert.assertEquals("foo@bar.com", person.getEmail());

        emailField.setValue("");
        Assert.assertFalse(binder.saveIfValid(person));
        // Person updated because phone and email are both empty
        Assert.assertEquals("foo@bar.com", person.getEmail());
    }

    @Test
    public void manyConvertersAndValidators() throws ValidationException {
        TextField yearOfBirthField = new TextField();
        binder.forField(yearOfBirthField)
                // Validator will be run with the String value of the field
                .withValidator(text -> text.length() == 4,
                        "Doesn't look like a year")
                // Converter will only be run for strings with 4 characters
                .withConverter(
                        new StringToIntegerConverter("Must enter a number"))
                // Validator will be run with the converted value
                .withValidator(year -> year >= 1900 && year <= 2000,
                        "Person must be born in the 20th century")
                .bind(BookPerson::getYearOfBirth, BookPerson::setYearOfBirth);

        yearOfBirthField.setValue("abc");
        Assert.assertEquals("Doesn't look like a year",
                binder.validate().get(0).getMessage());
        yearOfBirthField.setValue("abcd");
        Assert.assertEquals("Must enter a number",
                binder.validate().get(0).getMessage());
        yearOfBirthField.setValue("1200");
        Assert.assertEquals("Person must be born in the 20th century",
                binder.validate().get(0).getMessage());

        yearOfBirthField.setValue("1950");
        Assert.assertTrue(binder.validate().isEmpty());
        BookPerson person = new BookPerson(1500, 12);
        binder.save(person);
        Assert.assertEquals(1950, person.getYearOfBirth());
    }

    class MyConverter implements Converter<String, Integer> {
        @Override
        public Result<Integer> convertToModel(String fieldValue,
                Locale locale) {
            // Produces a converted value or an error
            try {
                // ok is a static helper method that creates a Result
                return Result.ok(Integer.valueOf(fieldValue));
            } catch (NumberFormatException e) {
                // error is a static helper method that creates a Result
                return Result.error("Please enter a number");
            }
        }

        @Override
        public String convertToPresentation(Integer integer, Locale locale) {
            // Converting to the field type should always succeed,
            // so there is no support for returning an error Result.
            return String.valueOf(integer);
        }
    }

    @Test
    public void bindUsingCustomConverter() {
        Binder<BookPerson> binder = new Binder<>();
        TextField yearOfBirthField = new TextField();

        // Using the converter
        binder.forField(yearOfBirthField).withConverter(new MyConverter())
                .bind(BookPerson::getYearOfBirth, BookPerson::setYearOfBirth);

        BookPerson p = new BookPerson(1500, 12);
        binder.bind(p);

        yearOfBirthField.setValue("abc");
        Assert.assertEquals("Please enter a number",
                binder.validate().get(0).getMessage());

        yearOfBirthField.setValue("123");
        Assert.assertTrue(binder.validate().isEmpty());

        p.setYearOfBirth(12500);
        binder.load(p);
        Assert.assertEquals("12500", yearOfBirthField.getValue());
    }
}
