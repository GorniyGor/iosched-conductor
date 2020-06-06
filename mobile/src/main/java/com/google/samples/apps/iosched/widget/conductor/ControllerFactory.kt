/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.apps.iosched.widget.conductor

import android.os.Bundle
import androidx.collection.SimpleArrayMap
import com.bluelinelabs.conductor.Controller
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException

//TODO("we need it 'object' ?")
object ControllerFactory {
    private val sClassMap = SimpleArrayMap<String, Class<*>>()

    /**
     * Create a new instance of a Controller with the given class name. This uses
     * [.loadControllerClass] and the empty
     * constructor of the resulting Class by default.
     *
     * @param classLoader The default classloader to use for instantiation
     * @param className The class name of the controller to instantiate.
     * @return Returns a new controller instance.
     * @throws InstantiationException If there is a failure in instantiating
     * the given controller class.  This is a runtime exception; it is not
     * normally expected to happen.
     */
    fun instantiate( classLoader: ClassLoader, className: String, args: Bundle?): Controller {
        return try {
            val cls =
                loadControllerClass(
                    classLoader, className)
            args?.let {
                getBundleConstructor(
                    cls.constructors)?.let {
                    it.newInstance(args) as Controller
                }
            } ?: cls.getConstructor().newInstance()!!
        } catch (e: InstantiationException) {
            throw InstantiationException(
                "Unable to instantiate controller " + className
                    + ": make sure class name exists, is public, and has an"
                    + " empty constructor that is public", e)
        } catch (e: IllegalAccessException) {
            throw InstantiationException(
                "Unable to instantiate controller " + className
                    + ": make sure class name exists, is public, and has an"
                    + " empty constructor that is public", e)
        } catch (e: NoSuchMethodException) {
            throw InstantiationException(
                "Unable to instantiate controller " + className
                    + ": could not find Controller constructor", e)
        } catch (e: InvocationTargetException) {
            throw InstantiationException(
                "Unable to instantiate controller " + className
                    + ": calling Controller constructor caused an exception", e)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun loadControllerClass(classLoader: ClassLoader, className: String): Class<out Controller?> {
        return try {
            val clazz =
                loadClass(
                    classLoader, className)
            clazz as Class<out Controller?>
        } catch (e: ClassNotFoundException) {
            throw InstantiationException(
                "Unable to instantiate controller " + className
                    + ": make sure class name exists", e)
        } catch (e: ClassCastException) {
            throw InstantiationException(
                "Unable to instantiate controller " + className
                    + ": make sure class is a valid subclass of Controller", e)
        }
    }

    @Throws(ClassNotFoundException::class)
    private fun loadClass(classLoader: ClassLoader, className: String): Class<*> {
        var clazz = sClassMap[className]
        if (clazz == null) { // Class not found in the cache, see if it's real, and try to add it
            clazz = Class.forName(className, false, classLoader)
            sClassMap.put(className, clazz)
        }
        return clazz
    }

    /**
     * Thrown by [ControllerFactory.instantiate] when
     * there is an instantiation failure.
     */
    class InstantiationException(msg: String, cause: Exception?) : RuntimeException(msg, cause)

    //Utils
    private fun getBundleConstructor(constructors: Array<Constructor<*>>): Constructor<*>? {
        for (constructor in constructors) {
            if (constructor.parameterTypes.size == 1 && constructor.parameterTypes[0] == Bundle::class.java) {
                return constructor
            }
        }
        return null
    }
}