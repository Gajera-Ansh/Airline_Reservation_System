package airline.ds;

interface Map<K, V> {
    void put(K key, V value);

    V get(K key);

    boolean containsKey(K key);

    int size();
}

public class HashMap<K, V> implements Map<K, V> {
    private static final int INITIAL_CAPACITY = 16;
    private static final double LOAD_FACTOR = 0.75;
    private Entry<K, V>[] table;
    private int size;

    public HashMap() {
        table = new Entry[INITIAL_CAPACITY];
        size = 0;
    }

    @Override
    public void put(K key, V value) {
        if (key == null) return;
        if (size >= table.length * LOAD_FACTOR) resize();

        int index = hash(key);
        Entry<K, V> entry = new Entry<>(key, value);

        if (table[index] == null) {
            table[index] = entry;
        } else {
            Entry<K, V> current = table[index];
            while (current.next != null) {
                if (current.key.equals(key)) {
                    current.value = value;
                    return;
                }
                current = current.next;
            }
            current.next = entry;
        }
        size++;
    }

    @Override
    public V get(K key) {
        if (key == null) return null;
        int index = hash(key);
        Entry<K, V> current = table[index];

        while (current != null) {
            if (current.key.equals(key)) return current.value;
            current = current.next;
        }
        return null;
    }

    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    @Override
    public int size() {
        return size;
    }

    public int hash(K key) {
        return Math.abs(key.hashCode()) % table.length;
    }

    public void resize() {
        Entry<K, V>[] oldTable = table;
        table = new Entry[table.length * 2];
        size = 0;

        for (Entry<K, V> entry : oldTable) {
            while (entry != null) {
                put(entry.key, entry.value);
                entry = entry.next;
            }
        }
    }

    public void putIfAbsent(K key, V value) {
        if (!containsKey(key)) {
            put(key, value);
        }
    }

    public static class Entry<K, V> {
        K key;
        V value;
        Entry<K, V> next;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}
