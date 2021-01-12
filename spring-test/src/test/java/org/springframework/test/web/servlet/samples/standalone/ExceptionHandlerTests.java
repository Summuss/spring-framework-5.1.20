/*
 * Copyright 2002-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.test.web.servlet.samples.standalone;

import org.junit.Test;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

/**
 * Exception handling via {@code @ExceptionHandler} method.
 *
 * @author Rossen Stoyanchev
 * @author Sam Brannen
 */
public class ExceptionHandlerTests {

    @Test
    public void mvcLocalExceptionHandlerMethod() throws Exception {
        standaloneSetup(new PersonController())
                .build()
                .perform(get("/person/Clyde"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("errorView"));
    }

    @Test
    public void mvcGlobalExceptionHandlerMethod() throws Exception {
        standaloneSetup(new PersonController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build()
                .perform(get("/person/Bonnie"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("globalErrorView"));
    }

    @Test
    public void mvcGlobalExceptionHandlerMethodUsingClassArgument() throws Exception {
        standaloneSetup(PersonController.class)
                .setControllerAdvice(GlobalExceptionHandler.class)
                .build()
                .perform(get("/person/Bonnie"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("globalErrorView"));
    }

    @Test
    public void restNoException() throws Exception {
        standaloneSetup(RestPersonController.class)
                .setControllerAdvice(
                        RestGlobalExceptionHandler.class,
                        RestPersonControllerExceptionHandler.class)
                .build()
                .perform(get("/person/Yoda").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Yoda"));
    }

    @Test
    public void restLocalExceptionHandlerMethod() throws Exception {
        standaloneSetup(RestPersonController.class)
                .setControllerAdvice(
                        RestGlobalExceptionHandler.class,
                        RestPersonControllerExceptionHandler.class)
                .build()
                .perform(get("/person/Luke").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value("local - IllegalArgumentException"));
    }

    @Test
    public void restGlobalExceptionHandlerMethod() throws Exception {
        standaloneSetup(RestPersonController.class)
                .setControllerAdvice(RestGlobalExceptionHandler.class)
                .build()
                .perform(get("/person/Leia").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value("global - IllegalStateException"));
    }

    @Test
    public void
            restGlobalRestPersonControllerExceptionHandlerTakesPrecedenceOverGlobalExceptionHandler()
                    throws Exception {
        standaloneSetup(RestPersonController.class)
                .setControllerAdvice(
                        RestGlobalExceptionHandler.class,
                        RestPersonControllerExceptionHandler.class)
                .build()
                .perform(get("/person/Leia").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.error")
                                .value("globalPersonController - IllegalStateException"));
    }

    @Test // gh-25520
    public void restNoHandlerFound() throws Exception {
        standaloneSetup(RestPersonController.class)
                .setControllerAdvice(
                        RestGlobalExceptionHandler.class,
                        RestPersonControllerExceptionHandler.class)
                .addDispatcherServletCustomizer(
                        dispatcherServlet ->
                                dispatcherServlet.setThrowExceptionIfNoHandlerFound(true))
                .build()
                .perform(get("/bogus").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value("global - NoHandlerFoundException"));
    }

    @Controller
    private static class PersonController {

        @GetMapping("/person/{name}")
        public String show(@PathVariable String name) {
            if (name.equals("Clyde")) {
                throw new IllegalArgumentException("simulated exception");
            } else if (name.equals("Bonnie")) {
                throw new IllegalStateException("simulated exception");
            }
            return "person/show";
        }

        @ExceptionHandler
        public String handleException(IllegalArgumentException exception) {
            return "errorView";
        }
    }

    @ControllerAdvice
    private static class GlobalExceptionHandler {

        @ExceptionHandler
        public String handleException(IllegalStateException exception) {
            return "globalErrorView";
        }
    }

    @RestController
    private static class RestPersonController {

        @GetMapping("/person/{name}")
        Person get(@PathVariable String name) {
            switch (name) {
                case "Luke":
                    throw new IllegalArgumentException();
                case "Leia":
                    throw new IllegalStateException();
                default:
                    return new Person("Yoda");
            }
        }

        @ExceptionHandler
        Error handleException(IllegalArgumentException exception) {
            return new Error("local - " + exception.getClass().getSimpleName());
        }
    }

    @RestControllerAdvice(assignableTypes = RestPersonController.class)
    @Order(Ordered.HIGHEST_PRECEDENCE)
    private static class RestPersonControllerExceptionHandler {

        @ExceptionHandler
        Error handleException(Throwable exception) {
            return new Error("globalPersonController - " + exception.getClass().getSimpleName());
        }
    }

    @RestControllerAdvice
    @Order(Ordered.LOWEST_PRECEDENCE)
    private static class RestGlobalExceptionHandler {

        @ExceptionHandler
        Error handleException(Throwable exception) {
            return new Error("global - " + exception.getClass().getSimpleName());
        }
    }

    static class Person {

        private final String name;

        Person(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    static class Error {

        private final String error;

        Error(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
    }
}
