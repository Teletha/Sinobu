/*
 * Copyright (C) 2010 Nameless Production Committee.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *         http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ezbean.sample.bean;

/**
 * @version 2009/04/12 16:26:19
 */
public class Person {

    private int age;

    private String firstName;

    private String lastName;

    /**
     * Get the age property of this {@link Person}.
     * 
     * @return The age prperty.
     */
    public int getAge() {
        return age;
    }

    /**
     * Set the age property of this {@link Person}.
     * 
     * @param age The age value to set.
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * Get the firstName property of this {@link Person}.
     * 
     * @return The firstName prperty.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Set the firstName property of this {@link Person}.
     * 
     * @param firstName The firstName value to set.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Get the lastName property of this {@link Person}.
     * 
     * @return The lastName prperty.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Set the lastName property of this {@link Person}.
     * 
     * @param lastName The lastName value to set.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
