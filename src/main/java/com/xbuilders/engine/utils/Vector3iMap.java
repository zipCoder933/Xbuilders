package com.xbuilders.engine.utils;

import com.xbuilders.engine.world.chunk.Chunk;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Vector3iMap<T> {
    final Map<Vector3i, T> chunks = new HashMap<>();
    final ArrayList<T> data = new ArrayList<>();

    public T get(Vector3i v) {
//        return chunks.get(v);
        for(int i = 0; i < data.size(); i++) {
            if(data.get(i).equals(chunks.get(v))) return data.get(i);
        }
        return null;
    }

    public void put(Vector3i v, T t) {
        chunks.put(v, t);
        if(!chunks.containsKey(v)) data.add(t);
    }

    public T remove(Vector3i v) {
        T chunk = chunks.remove(v);
        data.remove(chunk);
        return chunk;
    }

    public void clear() {
        chunks.clear();
        data.clear();
    }

    public int size() {
        return chunks.size();
    }

    public boolean containsKey(Vector3i v) {
        return chunks.containsKey(v);
    }

    public void forEach(BiConsumer<Vector3i, T> consumer) {
        chunks.forEach((k, v) -> consumer.accept(k, v));
    }

    public void forEach(Consumer<T> consumer) {
        chunks.forEach((k, v) -> consumer.accept(v));
    }

    public Collection<T> values() {
        return chunks.values();
    }
}
