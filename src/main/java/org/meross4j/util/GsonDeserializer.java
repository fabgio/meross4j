package org.meross4j.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

/**
 * the {@link GsonDeserializer} is implemented for generics' deserialization purposes
 * @param <T>  the source as String being deserialized
 */
//TODO: Javadoc
public  class GsonDeserializer <T> {
    public T  deserialize(String jsonString){
        Type type = new TypeToken<T>(){}.getType();
        return  new Gson().fromJson(jsonString, type);
    }
}
