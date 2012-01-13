/*
 * Copyright (C) 2012 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package kiss;

/**
 * <p>
 * Lifestyle manages the instance in the specific context. Sinobu provides four commonly used
 * lifestyles ({@link Prototype}, {@link Singleton}, {@link ThreadSpecific} and {@link Preference}).
 * </p>
 * <p>
 * There are two ways to specify {@link Lifestyle} for the class.
 * </p>
 * <p>
 * The one is {@link Manageable} annotation. This way is useful if the target class is under your
 * control. If the lifestyle is not specified, Sinobu uses {@link Prototype} lifestyle as default.
 * The following is example.
 * </p>
 * 
 * <pre>
 * &#064;Manageable(lifestyle = Singleton.class)
 * public class TargetClass {
 * }
 * </pre>
 * <p>
 * The other is defining custom {@link Lifestyle}. Sinobu recognizes it automatically if your custom
 * lifestyle class is loaded or unloaded by {@link I#load(java.nio.file.Path)} and
 * {@link I#unload(java.nio.file.Path)} methods. The following is example.
 * </p>
 * 
 * <pre>
 * public class CustomLifestyle implements Lifestyle&lt;ClassNotUnderYourControl&gt; {
 * 
 *     public ClassNotUnderYourControl resolve() {
 *         return new ClassNotUnderYourControl();
 *     }
 * }
 * </pre>
 * 
 * @see Prototype
 * @see Singleton
 * @see ThreadSpecific
 * @see Preference
 * @see Manageable#lifestyle()
 * @version 2011/11/04 0:03:20
 */
public interface Lifestyle<M> extends Extensible {

    /**
     * <p>
     * Return the instance which is assosiated with the specific context.
     * </p>
     * 
     * @return A retrieved object.
     */
    public M resolve();
}