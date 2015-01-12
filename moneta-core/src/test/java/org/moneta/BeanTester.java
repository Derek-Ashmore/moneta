/*
 * This software is licensed under the Apache License, Version 2.0
 * (the "License") agreement; you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.moneta;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.Assert;
import org.mockito.Mockito;

public class BeanTester {
    
    private Map<Class,Object[]> valueMap = new HashMap<Class,Object[]> ();
    private Set<String> fieldExclusionSet = new HashSet<String>();
    
    public BeanTester() {
        fieldExclusionSet.add("class"); // Object.class shouldn't be tested.
    }
    
    public void addTestValueSet(Class type, Object[] valueSet) {
        Validate.notNull(type, "Null type not allowed.");
        Validate.notNull(valueSet, "Null valueSet not allowed.");
        this.valueMap.put(type, valueSet);
    }
    
    public void addExcludedField(String fieldName) {
        Validate.notEmpty(fieldName, "Null or blank fieldName not allowed.");
        fieldExclusionSet.add(fieldName);
    }
    
    public void testBean(Class beanClass) 
            throws NoSuchMethodException, IllegalAccessException, 
                InvocationTargetException, InstantiationException {
        Validate.notNull(beanClass, "Null beanClass not allowed.");
        testBean(beanClass.newInstance());
    }
    
    public void testBean(Object bean) 
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Validate.notNull(bean, "Null bean not allowed.");
        
        try {performBeanTests(bean);}
        catch (Exception e) {
            throw new ContextedRuntimeException(e)
            .addContextValue("bean type", bean.getClass().getName());
        }
        
        for (PropertyDescriptor descriptor: PropertyUtils.getPropertyDescriptors(bean)) {
            if ( !fieldExclusionSet.contains(descriptor.getName())) {
                testProperty(bean, descriptor);
            }
        }
    }
    
    public void testProperty(Class beanClass, String fieldName) 
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Validate.notNull(beanClass, "Null beanClass not allowed.");
        testProperty(beanClass.newInstance(), fieldName);
    }
    
    public void testProperty(Object bean, String fieldName) 
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Validate.notNull(bean, "Null bean not allowed.");
        Validate.notEmpty(fieldName, "Null or blank fieldName not allowed.");
        testProperty(bean, PropertyUtils.getPropertyDescriptor(bean, fieldName));
    }
    
    private void testProperty(Object bean, PropertyDescriptor descriptor) 
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        
        try {
            performNullTest(bean, descriptor);
            for (Object value: generateValues(descriptor.getPropertyType())) {
                performValueTest(bean, descriptor, value);
            }
        }
        catch (ContextedRuntimeException are) {
            throw are.addContextValue("bean type", bean.getClass().getName())
                .addContextValue("field", descriptor.getName());
        }
        catch (Exception e) {
            throw new ContextedRuntimeException(e)
            .addContextValue("bean type", bean.getClass().getName())
            .addContextValue("field", descriptor.getName());
        }
    }
    
    private Object[] generateValues(Class type) {
        if (this.valueMap.containsKey(type)) {
            return this.valueMap.get(type);
        }
        else if (type.isInterface()) {
        	return new Object[]{Mockito.mock(type)};
        }
        else if (type.isPrimitive()) {
            if (type.getName().equals("boolean")) {
                return new Object[]{Boolean.FALSE, Boolean.TRUE};
            }
            else if (type.getName().equals("long")) {
                return new Object[]{Long.valueOf(0)};
            }
            else if (type.getName().equals("int")) {
                return new Object[]{Integer.valueOf(0)};
            }
            else if (type.getName().equals("double")) {
                return new Object[]{Double.valueOf(0)};
            }
            else if (type.getName().equals("byte")) {
                return new Object[]{Byte.valueOf("1")};
            }
            else if (type.getName().equals("float")) {
                return new Object[]{Float.valueOf("1")};
            }
            else if (type.getName().equals("short")) {
                return new Object[]{Short.valueOf("1")};
            }
            else if (type.getName().equals("char")) {
                return new Object[]{Character.valueOf('A')};
            }
        }
        else if (Boolean.class.equals(type)) {
            return new Object[]{Boolean.FALSE, Boolean.TRUE};
        }
        else if (Long.class.equals(type)) {
            return new Object[]{Long.valueOf(0)};
        }
        else if (Integer.class.equals(type)) {
            return new Object[]{Integer.valueOf(0)};
        }
        else if (Double.class.equals(type)) {
            return new Object[]{Double.valueOf(0)};
        }
        else if (Byte.class.equals(type)) {
            return new Object[]{Byte.valueOf("1")};
        }
        else if (Float.class.equals(type)) {
            return new Object[]{Float.valueOf("1")};
        }
        else if (Short.class.equals(type)) {
            return new Object[]{Short.valueOf("1")};
        }
        else if (Character.class.equals(type)) {
            return new Object[]{Character.valueOf('A')};
        }
        else if (type.isEnum()) {
            return type.getEnumConstants();
        }
        else if (type.isArray()) {
            Array.newInstance(type.getComponentType(), 0);
        }
        else if (Class.class.equals(type)) {
        	return new Object[]{Object.class};
        }
        else {
            try {
                Object value = type.newInstance();
                return new Object[]{value};
            }
            catch (Exception e) {
                throw new ContextedRuntimeException("Error invoking null constructor", e).addContextValue("class", type.getName());
            }
        }
        
        return new Object[0];
    }
    
    private void performBeanTests(Object bean) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Assert.assertTrue("Equals test on class " + bean.getClass().getName() + " did not pass", bean.equals(bean));
        Assert.assertTrue("Hashcode test on class " + bean.getClass().getName() + " did not pass", bean.hashCode() == bean.hashCode());
        Assert.assertTrue("ToString test on class " + bean.getClass().getName() + " did not pass", bean.toString() != null);
        
        if (bean instanceof Cloneable) {
            Assert.assertTrue("Cloneable test on class " + bean.getClass().getName() + " did not pass"
                    , MethodUtils.invokeMethod(bean, "clone", null) != null);
        }
    }
    
    private void performValueTest(Object bean,
            PropertyDescriptor descriptor, Object value) throws IllegalAccessException,
            InvocationTargetException {
        
        if (descriptor.getWriteMethod() != null) {
            descriptor.getWriteMethod().invoke(bean, new Object[]{value});
            
            if (descriptor.getReadMethod() != null) {
                System.out.println("Performing Null Test.  Class=" + bean.getClass().getName() + " Field=" + descriptor.getName() + " Value=" + value.toString());
                Assert.assertTrue("Field " + descriptor.getName() + " on class " + bean.getClass().getName() + " did not pass null test"
                        ,value.equals( descriptor.getReadMethod().invoke(bean) ) );
            }
        }
    }

    private void performNullTest(Object bean,
            PropertyDescriptor descriptor) throws IllegalAccessException,
            InvocationTargetException {
        
        if (descriptor.getWriteMethod() != null) {
            descriptor.getWriteMethod().invoke(bean, new Object[1]);
            
            if (descriptor.getReadMethod() != null) {
                System.out.println("Performing Null Test.  Class=" + bean.getClass().getName() + " Field=" + descriptor.getName());
                Assert.assertTrue("Field " + descriptor.getName() + " on class " + bean.getClass().getName() + " did not pass null test", descriptor.getReadMethod().invoke(bean) == null);
            }
        }
    }

}
