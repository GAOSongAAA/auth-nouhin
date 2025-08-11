package com.collaboportal.common.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FieldReflectionUtil {

    // 缓存字段信息以提升性能
    private static final Map<Class<?>, List<FieldInfo>> FIELD_CACHE = new ConcurrentHashMap<>();

    /**
     * 字段信息封装类
     */
    public static class FieldInfo {
        private String name;
        private Class<?> type;
        private Type genericType;
        private Object value;
        private Field field;

        // 构造器、getter、setter...
        public FieldInfo(Field field, Object obj) throws IllegalAccessException {
            this.field = field;
            this.name = field.getName();
            this.type = field.getType();
            this.genericType = field.getGenericType();

            field.setAccessible(true);
            this.value = field.get(obj);
        }

        // getters...
        public String getName() {
            return name;
        }

        public Class<?> getType() {
            return type;
        }

        public Type getGenericType() {
            return genericType;
        }

        public Object getValue() {
            return value;
        }

        public Field getField() {
            return field;
        }

        public boolean isPrimitive() {
            return type.isPrimitive();
        }

        public boolean isCollection() {
            return Collection.class.isAssignableFrom(type);
        }

        public boolean isMap() {
            return Map.class.isAssignableFrom(type);
        }

        public boolean isArray() {
            return type.isArray();
        }

        @Override
        public String toString() {
            return String.format("FieldInfo{name='%s', type=%s, value=%s}",
                    name, type.getSimpleName(), value);
        }
    }

    /**
     * 获取对象所有字段信息
     */
    public static List<FieldInfo> getAllFieldsInfo(Object obj) {
        if (obj == null)
            return new ArrayList<>();

        Class<?> clazz = obj.getClass();
        List<FieldInfo> fieldInfos = new ArrayList<>();

        // 从缓存获取字段列表
        List<FieldInfo> cachedFields = FIELD_CACHE.get(clazz);
        if (cachedFields == null) {
            cachedFields = buildFieldInfoCache(clazz);
            FIELD_CACHE.put(clazz, cachedFields);
        }

        // 为当前对象设置值
        for (FieldInfo cachedField : cachedFields) {
            try {
                FieldInfo fieldInfo = new FieldInfo(cachedField.getField(), obj);
                fieldInfos.add(fieldInfo);
            } catch (IllegalAccessException e) {
                System.err.println("Cannot access field: " + cachedField.getName());
            }
        }

        return fieldInfos;
    }

    /**
     * 构建字段信息缓存
     */
    private static List<FieldInfo> buildFieldInfoCache(Class<?> clazz) {
        List<FieldInfo> fieldInfos = new ArrayList<>();
        Set<String> fieldNames = new HashSet<>();

        Class<?> currentClass = clazz;
        while (currentClass != null && currentClass != Object.class) {
            Field[] fields = currentClass.getDeclaredFields();

            for (Field field : fields) {
                // 跳过静态字段和重复字段
                if (Modifier.isStatic(field.getModifiers()) ||
                        fieldNames.contains(field.getName())) {
                    continue;
                }

                try {
                    // 创建一个临时FieldInfo用于缓存（不包含具体值）
                    FieldInfo fieldInfo = new FieldInfo(field, null);
                    fieldInfos.add(fieldInfo);
                    fieldNames.add(field.getName());
                } catch (Exception e) {
                    // 忽略无法访问的字段
                }
            }

            currentClass = currentClass.getSuperclass();
        }

        return fieldInfos;
    }

    /**
     * 根据条件过滤字段
     */
    public static List<FieldInfo> getFilteredFields(Object obj, FieldFilter filter) {
        List<FieldInfo> allFields = getAllFieldsInfo(obj);
        return allFields.stream()
                .filter(filter::accept)
                .collect(ArrayList::new, (list, item) -> list.add(item), ArrayList::addAll);
    }

    /**
     * 字段过滤器接口
     */
    @FunctionalInterface
    public interface FieldFilter {
        boolean accept(FieldInfo fieldInfo);

        // 预定义过滤器
        FieldFilter NON_NULL = fieldInfo -> fieldInfo.getValue() != null;
        FieldFilter PRIMITIVES_ONLY = FieldInfo::isPrimitive;
        FieldFilter NON_PRIMITIVES = fieldInfo -> !fieldInfo.isPrimitive();
        FieldFilter COLLECTIONS_ONLY = FieldInfo::isCollection;

        // 按名称过滤
        static FieldFilter byNamePattern(String pattern) {
            return fieldInfo -> fieldInfo.getName().matches(pattern);
        }

        // 按类型过滤
        static FieldFilter byType(Class<?> type) {
            return fieldInfo -> type.isAssignableFrom(fieldInfo.getType());
        }

        // 排除特定字段
        static FieldFilter excluding(String... fieldNames) {
            Set<String> excludeSet = new HashSet<>(Arrays.asList(fieldNames));
            return fieldInfo -> !excludeSet.contains(fieldInfo.getName());
        }
    }
}