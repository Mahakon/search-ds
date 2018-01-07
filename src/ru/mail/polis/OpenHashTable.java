package ru.mail.polis;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

public class OpenHashTable<E extends OpenHashTableEntity> extends AbstractSet<E> implements Set<E> {

    private final int INIT_SIZE = 8;
    private final float INIT_LOAD_FACTOR = 0.5f;
    private final OpenHashTableEntity DELETED = (tableSize, probId) -> -1;

    private int size; //количество элементов в хеш-таблице
    private int tableSize; //размер хещ-таблицы
    private OpenHashTableEntity[] hashTable;

    public OpenHashTable() {
        size = 0;
        hashTable = new OpenHashTableEntity[INIT_SIZE];
        tableSize = hashTable.length;
    }

    /**
     * Вставляет элемент в хеш-таблицу.
     * Инвариант: на вход всегда приходит NotNull объект, который имеет корректный тип
     *
     * @param value элемент который необходимо вставить
     * @return true, если элемент в хеш-таблице отсутствовал
     */
    @Override
    public boolean add(E value) {
        checkBounds();

        boolean wasInTable = contains(value);

        int hash;
        int prob = 0;
        do {
            hash = value.hashCode(tableSize, prob++);
        } while (hashTable[hash] != DELETED &&
                hashTable[hash] != null
                && prob < tableSize);

        hashTable[hash] = value;
        size++;

        return !wasInTable;
    }

    private void checkBounds() {
        if (Float.compare(size, tableSize * INIT_LOAD_FACTOR) <= 0) {
            return;
        }

        tableSize *= 2;
        OpenHashTableEntity[] arr = new OpenHashTableEntity[tableSize];

        for (OpenHashTableEntity element : hashTable) {

            if (element == null || element == DELETED) {
                continue;
            }

            int hash;
            int prob = 0;
            do {
                hash = element.hashCode(tableSize, prob++);
            } while (arr[hash] != null && prob < tableSize);

            arr[hash] = element;
        }

        hashTable = arr;
    }

    /**
     * Удаляет элемент с таким же значением из хеш-таблицы.
     * Инвариант: на вход всегда приходит NotNull объект, который имеет корректный тип
     *
     * @param object элемент который необходимо вставить
     * @return true, если элемент содержался в хеш-таблице
     */
    @Override
    public boolean remove(Object object) {
        @SuppressWarnings("unchecked")
        OpenHashTableEntity value = (OpenHashTableEntity) object;

        int hash;
        int prob = 0;
        do {
            hash = value.hashCode(tableSize, prob++);
        } while(!value.equals(hashTable[hash]) &&
                hashTable[hash] != null &&
                prob < tableSize);

        if (value.equals(hashTable[hash])) {
            hashTable[hash] = DELETED;
            size--;
            return true;
        }

        return false;
    }

    /**
     * Ищет элемент с таким же значением в хеш-таблице.
     * Инвариант: на вход всегда приходит NotNull объект, который имеет корректный тип
     *
     * @param object элемент который необходимо поискать
     * @return true, если такой элемент содержится в хеш-таблице
     */
    @Override
    public boolean contains(Object object) {
        @SuppressWarnings("unchecked")
        OpenHashTableEntity value = (OpenHashTableEntity) object;

        int hash;
        int prob = 0;
        do {
            hash = value.hashCode(tableSize, prob++);
        } while(!value.equals(hashTable[hash]) &&
                hashTable[hash] != null &&
                prob < tableSize);

        return value.equals(hashTable[hash]);
    }

    @Override
    public int size() {
        return size;
    }

    public int getTableSize() {
        return tableSize;
    }

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException();
    }

    public static void main(String[] args) {
        OpenHashTable<Student> table = new OpenHashTable<>();
        SimpleStudentGenerator gen = SimpleStudentGenerator.getInstance();

        Student s = gen.generate();
        table.add(s);
        for (int i = 0; i < 5; i++) {
            table.add(gen.generate());
        }

        // на Student не работает, т.к. там не реализован метод
        // hashCode(int tableSize, int probId) !!!!
        // там всегда возвращается 0
        System.out.println(table.size);
        System.out.println(table.contains(s));

        System.out.println(table.remove(s));
        System.out.println(table.size);
    }

}