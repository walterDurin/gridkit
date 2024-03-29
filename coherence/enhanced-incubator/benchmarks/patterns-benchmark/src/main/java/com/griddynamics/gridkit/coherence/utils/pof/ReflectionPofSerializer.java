/**
 * Copyright 2008-2009 Grid Dynamics Consulting Services, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.griddynamics.gridkit.coherence.utils.pof;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofSerializer;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;

/**
 * An implementation of generic {@link PofSerializer} capable to serialize any object using
 * reflection.
 * 
 * Important: serializable classes still should be registered in pof-config.xml.
 * 
 * @author Alexey Ragozin (alexey.ragozin@gmail.com)
 */
public class ReflectionPofSerializer implements PofSerializer {
    
    private final static Map<Class<?>, ObjectPropCodec> CODEC = new HashMap<Class<?>, ObjectPropCodec>();
    {
        CODEC.put(Object.class, new ObjectObjectPropCodec());
        
        CODEC.put(String.class, new StringPropCodec());

        CODEC.put(byte.class, new BytePropCodec());
        CODEC.put(Byte.class, new BytePropCodec());
        CODEC.put(short.class, new ShortPropCodec());
        CODEC.put(Short.class, new ShortPropCodec());
        CODEC.put(int.class, new IntegerPropCodec());
        CODEC.put(Integer.class, new IntegerPropCodec());
        CODEC.put(long.class, new LongPropCodec());
        CODEC.put(Long.class, new LongPropCodec());
        
        CODEC.put(byte[].class, new ByteArrayPropCodec());
    }

    private final static ConcurrentMap<String, Object> WELL_KNOWN_OBJECTS = new ConcurrentHashMap<String, Object>();  
    private final static ConcurrentMap<Object, String> WELL_KNOWN_OBJECTS_REV = new ConcurrentHashMap<Object, String>();
    
    /**
     * If you have singleton object in your application. This works similar to readReslve or enumeration serialization logic.
     * @param name unique name of singleton object
     * @param object instance of singleton object
     */
    public static void registerWellKnownObject(String name, Object object) {
        if (WELL_KNOWN_OBJECTS.putIfAbsent(name, object) == null) {
            WELL_KNOWN_OBJECTS_REV.put(object, name);
        }
        else {
            throw new IllegalStateException("Name '" + name + "' is already in use");
        }
    }
    
    private Map<Class<?>, ObjectFormat> formats = new ConcurrentHashMap<Class<?>, ObjectFormat>();
    
    
    @Override
    public Object deserialize(PofReader in) throws IOException {
        Object result = internalDeserialize(in);
        return result;
    }

    protected Object internalDeserialize(PofReader in) throws IOException {
        Class<?> type = in.getPofContext().getClass(in.getUserTypeId());
        ObjectFormat format = formats.get(type);
        if (format == null) {
            try {
                format = new ObjectFormat(type);
            } catch (Exception e) {
                throw new IOException("Failed to create reflection format for " + type.getName(), e);
            }
            formats.put(type, format);
        }
        Object result = resolve(format.deserialize(in));
        return result;
    }

    @Override
    public void serialize(PofWriter out, Object origValue) throws IOException {
        internalSerialize(out, origValue);
    }

    protected void internalSerialize(PofWriter out, Object origValue) throws IOException {
        Object value = replace(origValue);
        Class<?> type = value.getClass();
        ObjectFormat format = formats.get(type);
        if (format == null) {
            try {
                format = new ObjectFormat(type);
            } catch (Exception e) {
                throw new IOException("Failed to create reflection format for " + type.getName(), e);
            }
            formats.put(type, format);
        }
        format.serialize(out, value);
    }

    private static Object resolve(Object deserialized) {
        if (deserialized.getClass() == WKO.class) {
            return WELL_KNOWN_OBJECTS.get(((WKO)deserialized).objectRef);
        }
        else {
            return deserialized;
        }
    }

    private static Object replace(Object value) {
        String ref = WELL_KNOWN_OBJECTS_REV.get(value);
        if (ref != null) {
            return new WKO(ref);
        }
        else {
            return value;
        }
    }

    private static ObjectPropCodec getCodec(Field field) {
        ObjectPropCodec codec = CODEC.get(Object.class);
        if (Object[].class.isAssignableFrom(field.getType())) {
            codec = new ObjectArrayPropCodec((Object[])Array.newInstance(field.getType().getComponentType(), 0));
        }
        else if (field.getType().isEnum()) {
            codec = new EnumPropCodec(field.getType());
        }
        else {
            codec = CODEC.get(field.getType());
        }
        if (codec == null) {
            codec = CODEC.get(Object.class);
        }
        return codec;
    }
    
    private static class ObjectFormat implements PofSerializer {
        private Constructor<?> constructor;
        private ObjectFieldCodec[] propCodec;
        
        public ObjectFormat(Class<?> type) throws SecurityException, NoSuchMethodException {
            List<ObjectFieldCodec> list = new ArrayList<ObjectFieldCodec>();
            
            constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);            
            init(list, type);            
            propCodec = list.toArray(new ObjectFieldCodec[list.size()]);            
        }

        private void init(List<ObjectFieldCodec> list, Class<?> type) {
            Class<?> parent = type.getSuperclass();
            if (parent != Object.class) {
                init(list, parent);
            }
            
            Field[] fields = type.getDeclaredFields();
            for(int i = 0; i != fields.length; ++i) {
                Field field = fields[i];
                int mod = field.getModifiers();
                if (!Modifier.isFinal(mod) 
                        && !Modifier.isStatic(mod)
                        && !Modifier.isTransient(mod)) {

                    field.setAccessible(true);
                    ObjectPropCodec codec = getCodec(field);
                    ObjectFieldCodec fc = new ObjectFieldCodec(field, codec);
                    list.add(fc);
                }
            }            
        }

        @Override
        public Object deserialize(PofReader in) throws IOException {
            Object val;
            try {
                val = constructor.newInstance();
            } catch (Exception e) {
                throw new IOException("Cannot init new object", e);
            };
            int propId = 0;
            boolean[] nulls = in.readBooleanArray(propId++);
            
            for(int i = 0; i != propCodec.length; ++i) {
                if (!nulls[i]) {
                    ObjectFieldCodec codec = propCodec[i];
                    try {
                        codec.field.set(val, codec.codec.readProp(in, propId++, codec.field));
                    } catch (Exception e) {
                        throw new IOException("Deserialization failed (" + e.getMessage() + ")", e);
                    }
                }
            }
            in.readRemainder();
            return val;
        }

        @Override
        public void serialize(PofWriter out, Object val) throws IOException {
            try {                    
                Object[] snapshot = new Object[propCodec.length];
                boolean[] nulls = new boolean[propCodec.length];
                for(int i = 0; i != propCodec.length; ++i) {
                    Object fv = propCodec[i].field.get(val);
                    snapshot[i] = fv;
                    nulls[i] = fv == null;
                }
                int propId = 0;
                out.writeBooleanArray(propId++, nulls);
                for(int i = 0; i != propCodec.length; ++i) {
                    if (snapshot[i] != null) {
                        propCodec[i].codec.writeProp(out, propId++, snapshot[i], propCodec[i].field);
                    }
                }
                
                out.writeRemainder(null);
            } catch (Exception e) {
                throw new IOException("Serialization failed (" + e.getMessage() + ")", e);
            }
        }
    }
    
    private static class  ObjectFieldCodec {
        final Field field;
        final ObjectPropCodec codec;
        
        public ObjectFieldCodec(Field field, ObjectPropCodec codec) {
            this.field = field;
            this.codec = codec;
        }        
    }

    private static interface ObjectPropCodec {
        public Object readProp(PofReader reader, int propId, Field field) throws IOException;
        public void writeProp(PofWriter writer, int propId, Object obj, Field field) throws IOException;
    }
    
    private static class ObjectArrayPropCodec implements ObjectPropCodec {

        private final Object[] proto;
        
        public ObjectArrayPropCodec(Object[] proto) {
            this.proto = proto;
        }

        @Override
        public Object readProp(PofReader reader, int propId, Field field) throws IOException {
            return reader.readObjectArray(propId, proto);
        }
        
        @Override
        public void writeProp(PofWriter writer, int propId, Object obj, Field field) throws IOException {
            writer.writeObjectArray(propId, (Object[]) obj);
        }
    }

    private static class EnumPropCodec implements ObjectPropCodec {
        
        private final EnumSet<?> universe;
        
        @SuppressWarnings("unchecked")
        public EnumPropCodec(Class<?> type) {
            this.universe = EnumSet.allOf((Class)type);
        }

        @Override
        @SuppressWarnings("unchecked")
        public Object readProp(PofReader reader, int propId, Field field) throws IOException {
            int val = reader.readShort(propId);
            for(Object x: universe) {
                if (((Enum)x).ordinal() == val) {
                    return x;
                }
            }
            throw new IOException("Illegal " + field.getType().getName() + " ordinal " + val);
        }
        
        @Override
        @SuppressWarnings("unchecked")
        public void writeProp(PofWriter writer, int propId, Object obj, Field field) throws IOException {
            writer.writeShort(propId, (short) ((Enum)obj).ordinal());
        }
    }

    private static class ObjectObjectPropCodec implements ObjectPropCodec {
        @Override
        public Object readProp(PofReader reader, int propId, Field field) throws IOException {
            return resolve(reader.readObject(propId));
        }
        
        @Override
        public void writeProp(PofWriter writer, int propId, Object obj, Field field) throws IOException {
            writer.writeObject(propId, replace(obj));
        }
    }

    private static class StringPropCodec implements ObjectPropCodec {
        @Override
        public Object readProp(PofReader reader, int propId, Field field) throws IOException {
            return reader.readString(propId);
        }
        
        @Override
        public void writeProp(PofWriter writer, int propId, Object obj, Field field) throws IOException {
            writer.writeString(propId, (String) obj);
        }
    }

    private static class BytePropCodec implements ObjectPropCodec {
        @Override
        public Object readProp(PofReader reader, int propId, Field field) throws IOException {
            return reader.readByte(propId);
        }
        
        @Override
        public void writeProp(PofWriter writer, int propId, Object obj, Field field) throws IOException {
            writer.writeByte(propId, (Byte) obj);
        }
    }

    private static class ShortPropCodec implements ObjectPropCodec {
        @Override
        public Object readProp(PofReader reader, int propId, Field field) throws IOException {
            return reader.readShort(propId);
        }
        
        @Override
        public void writeProp(PofWriter writer, int propId, Object obj, Field field) throws IOException {
            writer.writeShort(propId, (Short) obj);
        }
    }

    private static class IntegerPropCodec implements ObjectPropCodec {
        @Override
        public Object readProp(PofReader reader, int propId, Field field) throws IOException {
            return reader.readInt(propId);
        }
        
        @Override
        public void writeProp(PofWriter writer, int propId, Object obj, Field field) throws IOException {
            writer.writeInt(propId, (Integer) obj);
        }
    }

    private static class LongPropCodec implements ObjectPropCodec {
        @Override
        public Object readProp(PofReader reader, int propId, Field field) throws IOException {
            return reader.readLong(propId);
        }
        
        @Override
        public void writeProp(PofWriter writer, int propId, Object obj, Field field) throws IOException {
            writer.writeLong(propId, (Long) obj);
        }
    }

    private static class ByteArrayPropCodec implements ObjectPropCodec {
        @Override
        public Object readProp(PofReader reader, int propId, Field field) throws IOException {
            return reader.readByteArray(propId);
        }
        
        @Override
        public void writeProp(PofWriter writer, int propId, Object obj, Field field) throws IOException {
            writer.writeByteArray(propId, (byte[]) obj);
        }
    }
    
    /**
     * WKO object place holder.
     * Should be included in pof-config.xml if wko is used in application.
     * 
     * @author Alexey Ragozin (alexey.ragozin@gmail.com)
     *
     */
    public static class WKO implements Serializable, PortableObject {

        private static final long serialVersionUID = 20090715L;
        
        private String objectRef;
        
        public WKO() {
        }
        
        public WKO(String objectRef) {
            this.objectRef = objectRef;
        }

        @Override
        public void readExternal(PofReader in) throws IOException {
            objectRef = in.readString(0);
        }

        @Override
        public void writeExternal(PofWriter out) throws IOException {
            out.writeString(0, objectRef);            
        }
    }
}