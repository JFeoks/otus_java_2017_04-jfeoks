package ru.otus.l31;

import java.util.*;

/**
 * Created by JFeoks on 19.04.2017.
 */
public class CustomArrayList<T> implements List<T>{
    private static final int DEFAULT_INITIAL_CAPACITY = 5;
    private static final Object[] EMPTY_ELEMENT_DATA = {};
    private int size;

    private transient Object[] customArrayListElementData;

    public CustomArrayList(int initialCapacity){
        super();
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal Capacity: "+ initialCapacity);
        this.customArrayListElementData = new Object[initialCapacity];
    }

    public CustomArrayList(){
        super();
        this.customArrayListElementData = EMPTY_ELEMENT_DATA;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size==0;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iter();
    }

    @Override
    public Object[] toArray() {
        return Arrays.copyOf(customArrayListElementData, size);
    }

    @Override
    public <E> E[] toArray(E[] a) {
        if (a.length < size) return (E[]) Arrays.copyOf(customArrayListElementData, size, a.getClass());
        System.arraycopy(customArrayListElementData, 0, a, 0, size);
        if (a.length > size) a[size] = null;
        return a;
    }


    public boolean add(T t) {
        increaseCapacity(size + 1);
        customArrayListElementData[size++] = t;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (o == null) {
            for (int i = 0; i < size; i++)
                if (customArrayListElementData[i] == null) {
                    remove(i);
                    return true;
                }
        } else {
            for (int i = 0; i < size; i++)
                if (o.equals(customArrayListElementData[i])) {
                    remove(i);
                    return true;
                }
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object obj : c) {
            if (!contains(obj)) return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        Object[] objs = c.toArray();
        int addingSize = c.toArray().length;
        increaseCapacity(size + addingSize);  // Increments modCount
        System.arraycopy(objs, 0, customArrayListElementData, size, addingSize);
        size += addingSize;
        return addingSize != 0;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        if (index > size || index < 0)
            throw new IndexOutOfBoundsException();
        Object[] a = c.toArray();
        int addingSize = a.length;
        increaseCapacity(size + addingSize);
        int numMoved = size - index;
        if (numMoved > 0) System.arraycopy(customArrayListElementData, index, customArrayListElementData, index + addingSize, numMoved);
        System.arraycopy(a, 0, customArrayListElementData, index, addingSize);
        size += addingSize;
        return addingSize != 0;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean isRemoved = false;
        for (Object obj: c) {
            int index;
            while ((index = indexOf(c)) >= 0){
                isRemoved = true;
                remove(index);
            }
        }
        return isRemoved;
}

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean[] retains = new boolean[size];
        boolean flag = false;
        for (Object obj : c) {
            for (int i = 0; i < customArrayListElementData.length; i++) {
                if (customArrayListElementData[i].equals(obj)) {
                    flag = true;
                    retains[i] = true;
                }
            }
        }
        if (!flag) return false;

        clear();
        CustomArrayList list = new CustomArrayList<>();
        for (int i = 0; i < retains.length; i++) {
            if (retains[i]) list.add(customArrayListElementData[i]);
        }
        customArrayListElementData = list.toArray();
        return true;
    }

    public void clear() {
        for (int i = 0; i < size; i++)
            customArrayListElementData[i] = null;

        size = 0;

    }

    @SuppressWarnings("unchecked")
    public T get(int index) {
        if (index >= size){
            throw new ArrayIndexOutOfBoundsException("array index out of bound exception with index at"+index);
        }
        return (T)customArrayListElementData[index];
    }

    @Override
    public T set(int index, T element) {
        if (index >= size) throw new IndexOutOfBoundsException();
        T oldValue = (T) customArrayListElementData[index];
        customArrayListElementData[index] = element;
        return oldValue;
    }

    public void add(int index, T element) {
        increaseCapacity(size + 1);
        System.arraycopy(customArrayListElementData, index, customArrayListElementData, index + 1,size - index);
        customArrayListElementData[index] = element;
        size++;

    }

    @SuppressWarnings("unchecked")
    public T remove(int i) {
        T oldValue = (T)customArrayListElementData[i];
        int removeIndex = size - i - 1;
        if (removeIndex > 0){
            System.arraycopy(customArrayListElementData, i + 1, customArrayListElementData, i,removeIndex);
        }
        customArrayListElementData[--size] = null;
        return oldValue;
    }

    @Override
    public int indexOf(Object o) {
        if (o == null) {
            for (int i = 0; i < size; i++)
                if (customArrayListElementData[i]==null)
                    return i;
        } else {
            for (int i = 0; i < size; i++)
                if (o.equals(customArrayListElementData[i]))
                    return i;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        if (o == null) {
            for (int i = size-1; i >= 0; i--)
                if (customArrayListElementData[i]==null)
                    return i;
        } else {
            for (int i = size-1; i >= 0; i--)
                if (o.equals(customArrayListElementData[i]))
                    return i;
        }
        return -1;
    }

    @Override
    public ListIterator<T> listIterator() {
        return new ListIter(0);
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        if (index < 0 || index > size())
            throw new IndexOutOfBoundsException();
        return new ListIter(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
      return new SubList(this, fromIndex, toIndex);
    }

    private void expandCustomArrayList(int minCapacity) {
        int oldCapacity = customArrayListElementData.length;
        int newCapacity = oldCapacity + (oldCapacity /2);
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        customArrayListElementData = Arrays.copyOf(customArrayListElementData, newCapacity);
    }


    private void increaseCapacity(int minCapacity) {
        if (customArrayListElementData == EMPTY_ELEMENT_DATA) minCapacity = Math.max(DEFAULT_INITIAL_CAPACITY, minCapacity);
        if (minCapacity - customArrayListElementData.length > 0) expandCustomArrayList(minCapacity);
    }

    private class Iter implements java.util.Iterator<T> {
        int cursor;
        int lastRet = -1;

        public boolean hasNext() {
            return cursor != size;
        }

        @SuppressWarnings("unchecked")
        public T next() {
            int i = cursor;
            if (i >= size) throw new NoSuchElementException();
            Object[] elementData = CustomArrayList.this.customArrayListElementData;
            if (i >= elementData.length) throw new ConcurrentModificationException();
            cursor = i + 1;
            return (T) elementData[lastRet = i];
        }

        public void remove() {
            if (lastRet < 0) throw new IllegalStateException();

            try {
                CustomArrayList.this.remove(lastRet);
                cursor = lastRet;
                lastRet = -1;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }
    }


    private class ListIter extends Iter implements ListIterator<T> {
        ListIter(int index) {
            super();
            cursor = index;
        }

        public boolean hasPrevious() {
            return cursor != 0;
        }

        public int nextIndex() {
            return cursor;
        }

        public int previousIndex() {
            return cursor - 1;
        }

        @SuppressWarnings("unchecked")
        public T previous() {
            int i = cursor - 1;
            if (i < 0)
                throw new NoSuchElementException();
            Object[] elementData = CustomArrayList.this.customArrayListElementData;
            if (i >= elementData.length)
                throw new ConcurrentModificationException();
            cursor = i;
            return (T) elementData[lastRet = i];
        }

        public void set(T e) {
            if (lastRet < 0)
                throw new IllegalStateException();

            try {
                CustomArrayList.this.set(lastRet, e);
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

        public void add(T e) {
            try {
                int i = cursor;
                CustomArrayList.this.add(i, e);
                cursor = i + 1;
                lastRet = -1;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }
    }

    class SubList<E> extends CustomArrayList<E> {
        private final CustomArrayList<E> subList;
        private final int offset;
        private int size;

        SubList(CustomArrayList<E> list, int fromIndex, int toIndex) {
            if (fromIndex < 0 || toIndex > list.size() || fromIndex > toIndex) throw new IndexOutOfBoundsException();

            subList = list;
            offset = fromIndex;
            size = toIndex - fromIndex;
            ;
        }

        private void checkRange(int index) {
            if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        }

        public E set(int index, E element) {
            checkRange(index);
            return subList.set(index + offset, element);
        }

        public E get(int index) {
            checkRange(index);
            return subList.get(index + offset);
        }

        public int size() {
            return size;
        }

        public void add(int index, E element) {
            checkRange(index);
            subList.add(index + offset, element);
            size++;
        }

        public E remove(int index) {
            checkRange(index);
            E result = subList.remove(index + offset);
            size--;
            return result;
        }

        public boolean addAll(Collection<? extends E> c) {
            return addAll(size, c);
        }

        public boolean addAll(int index, Collection<? extends E> c) {
            checkRange(index);
            int cSize = c.size();
            if (cSize == 0)
                return false;

            subList.addAll(offset + index, c);
            size += cSize;
            return true;
        }

        public Iterator<E> iterator() {
            return listIterator();
        }

        public ListIterator<E> listIterator(final int index) {
            checkRange(index);
            return new ListIterator<E>() {
                private final ListIterator<E> i = subList.listIterator(index + offset);

                public boolean hasNext() {
                    return nextIndex() < size;
                }

                public E next() {
                    if (hasNext())
                        return i.next();
                    else
                        throw new NoSuchElementException();
                }

                public boolean hasPrevious() {
                    return previousIndex() >= 0;
                }

                public E previous() {
                    if (hasPrevious())
                        return i.previous();
                    else
                        throw new NoSuchElementException();
                }

                public int nextIndex() {
                    return i.nextIndex() - offset;
                }

                public int previousIndex() {
                    return i.previousIndex() - offset;
                }

                public void remove() {
                    i.remove();
                    size--;
                }

                public void set(E e) {
                    i.set(e);
                }

                public void add(E e) {
                    i.add(e);
                    size++;
                }
            };
        }
    }

    public static void main(String[] args) {
        CustomArrayList<String> strList= new CustomArrayList<>();
        strList.add("str1");
        strList.add("str2");
        System.out.println("after adding elements size ="+strList.size());
        strList.remove(1);
        System.out.println("after removing element size ="+strList.size());
    }
}
