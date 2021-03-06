package com.flipkart.android.proteus.toolbox;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * IdGeneratorImpl
 *
 * <p>
 *     An built in implementation of {@link IdGenerator} interface
 * </p>
 *
 * @author aditya.sharat
 */
public class IdGeneratorImpl implements IdGenerator {
    private static final ClassLoader ID_MAP_CLASS_LOADER = new TypeToken<Map<String, Integer>>() {
    }.getClass().getClassLoader();
    private final HashMap<String, Integer> idMap = new HashMap<>();
    private final AtomicInteger sNextGeneratedId;

    public IdGeneratorImpl() {
        sNextGeneratedId = new AtomicInteger(1);
    }

    public IdGeneratorImpl(Parcel source) {
        sNextGeneratedId = new AtomicInteger(source.readInt());
        source.readMap(idMap, ID_MAP_CLASS_LOADER);
    }

    public final static Parcelable.Creator<IdGeneratorImpl> CREATOR = new Creator<IdGeneratorImpl>() {
        @Override
        public IdGeneratorImpl createFromParcel(Parcel source) {
            return new IdGeneratorImpl(source);
        }

        @Override
        public IdGeneratorImpl[] newArray(int size) {
            return new IdGeneratorImpl[size];
        }
    };

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(sNextGeneratedId.get());
        dest.writeMap(idMap);
    }

    /**
     * Generates and returns a unique id, for the given key.
     * If key exists, returns old value.
     * Ensure that all
     *
     * @param idKey
     * @return a unique ID integer for use with {@link android.view.View#setId(int)}.
     */
    @Override
    public synchronized int getUnique(String idKey) {
        Integer existingId = idMap.get(idKey);
        if (existingId == null) {
            int newId = generateViewId();
            idMap.put(idKey, newId);
            existingId = newId;
        }
        return existingId;
    }

    /**
     * Taken from Android View Source code API 17+
     * <p/>
     * Generate a value suitable for use.
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */
    private int generateViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();

            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) {
                newValue = 1; // Roll over to 1, not 0.
            }
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable's
     * marshalled representation.
     *
     * @return a bitmask indicating the set of special object types marshalled
     * by the Parcelable.
     */
    @Override
    public int describeContents() {
        return 0;
    }
}
