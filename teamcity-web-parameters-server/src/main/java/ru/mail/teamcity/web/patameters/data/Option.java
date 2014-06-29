package ru.mail.teamcity.web.patameters.data;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;

/**
 * User: g.chernyshev
 * Date: 29/06/14
 * Time: 13:47
 */
@XmlRootElement
public final class Option<K, V> implements Map.Entry<K, V> {
    private final K key;
    private V value;

    public Option(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public V setValue(V value) {
        V old = this.value;
        this.value = value;
        return old;
    }
}