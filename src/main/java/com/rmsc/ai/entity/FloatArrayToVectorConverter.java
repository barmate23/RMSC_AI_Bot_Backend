package com.rmsc.ai.entity;

import com.pgvector.PGvector;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA {@link AttributeConverter} that transparently maps between
 * Java {@code float[]} and the PostgreSQL {@code VECTOR} type
 * (represented as {@link PGvector} in the JDBC layer).
 *
 * <p>Without this converter, Hibernate would not know how to
 * serialize a float[] to the pgvector binary format expected
 * by the database driver.
 *
 * <p>{@code autoApply = true} means Hibernate will use this
 * converter automatically for every {@code float[]} column
 * without needing {@code @Convert} on each field.
 */
@Converter(autoApply = true)
public class FloatArrayToVectorConverter implements AttributeConverter<float[], Object> {

    /**
     * Converts a Java {@code float[]} to a {@link PGvector} instance
     * for database persistence.
     *
     * @param floats the in-memory vector representation
     * @return a {@link PGvector} wrapping the same values
     */
    @Override
    public Object convertToDatabaseColumn(float[] floats) {
        if (floats == null) {
            return null;
        }
        return new PGvector(floats);
    }

    /**
     * Converts the raw JDBC value (returned as {@link PGvector}
     * by the pgvector driver) back to a plain Java {@code float[]}.
     *
     * @param dbData the value returned from the ResultSet
     * @return a Java float[] representation of the vector
     */
    @Override
    public float[] convertToEntityAttribute(Object dbData) {
        if (dbData == null) {
            return null;
        }
        if (dbData instanceof PGvector pgVector) {
            return pgVector.toArray();
        }
        if (dbData instanceof String str) {
            try {
                return new PGvector(str).toArray();
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to parse PGvector from string: " + str, e);
            }
        }
        throw new IllegalArgumentException(
                "Cannot convert DB value of type " + dbData.getClass().getName() + " to float[]");
    }
}
