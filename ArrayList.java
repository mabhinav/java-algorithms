import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/*
 * Resizing array implementation - to understand java.util.ArrayList Implementation
 */
public class ArrayList<E> {
  private static final int DEFAULT_CAPACITY = 10;
  private Object[] arr;
  private int size;
  private volatile int modCount;

  public ArrayList() {
    arr = new Object[DEFAULT_CAPACITY];
    size = 0;
  }

  public ArrayList(int capacity) {
    arr = new Object[capacity];
    size = 0;
  }

  public int size() {
    return size;
  }

  public boolean isEmpty() {
    return size == 0;
  }

  public void add(E item) {
    if (size == arr.length) {
      resize(2 * arr.length);
    }
    arr[size++] = item;
    ++modCount;
  }

  public void add(int index, E item) {
    checkIndexRange(index);

    if (size == arr.length) {
      resize(2 * arr.length);
    }

    for (int i = size - 1; i >= index; --i) {
      arr[i + 1] = arr[i];
    }

    arr[index] = item;
    ++size;
    ++modCount;
  }

  public void remove(int index) {
    checkIndexRange(index);

    for (int i = index; i < size - 1; ++i) {
      arr[i] = arr[i + 1];
    }

    arr[size--] = null;

    if (size > 0 && size == arr.length / 4) {
      resize(arr.length / 2);
    }
    ++modCount;
  }

  public void print() {
    for (int i = 0; i < size - 1; ++i) {
      System.out.print(arr[i] + ", ");
    }
    System.out.println(arr[size - 1]);
  }

  @SuppressWarnings("unchecked")
  public E get(int index) {
    checkIndexRange(index);

    return (E) arr[index];
  }

  public Iterator<E> iterator() {
    return new Itr();
  }

  private void checkIndexRange(int index) {
    if (index < 0 || index > size) {
      throw new IndexOutOfBoundsException();
    }
  }

  private void resize(int n) {
    Object[] tempArr = new Object[n];
    for (int i = 0; i < size; ++i) {
      tempArr[i] = arr[i];
    }
    arr = tempArr;
  }

  private class Itr implements Iterator<E> {
    // index of next element to return
    int cursor;

    // index of last element returned; -1 if no such
    int lastRet = -1;

    int expectedModCount = modCount;

    @Override
    public boolean hasNext() {
      return cursor != size;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E next() {
      checkForConcurrentModification();
      int i = cursor;
      if (i >= size) {
        throw new NoSuchElementException();
      }

      Object[] arr = ArrayList.this.arr;
      if (i >= arr.length) {
        throw new ConcurrentModificationException();
      }
      cursor = i + 1;
      return (E) arr[lastRet = i];
    }

    @Override
    public void remove() {
      if (lastRet < 0) {
        throw new IllegalStateException();
      }

      checkForConcurrentModification();

      try {
        ArrayList.this.remove(lastRet);
        cursor = lastRet;
        lastRet = -1;
        expectedModCount = modCount;
      } catch (IndexOutOfBoundsException e) {
        throw new ConcurrentModificationException();
      }
    }

    private void checkForConcurrentModification() {
      if (expectedModCount != modCount) {
        throw new ConcurrentModificationException();
      }
    }

  }

  public static void main(String[] args) {
    ArrayList<Integer> test = new ArrayList<Integer>();
    test.add(1);
    test.add(3);
    test.add(4);
    test.add(7);

    test.print();

    test.add(1, 2);
    test.add(4, 5);
    test.add(5, 6);

    test.print();

    test.remove(3);

    test.print();

    Iterator<Integer> it = test.iterator();
    while (it.hasNext()) {
      System.out.print(it.next() + ", ");
      it.remove();
    }

    Iterator<Integer> it1 = test.iterator();
    while (it1.hasNext()) {
      System.out.println("Not printed");
    }
  }
}
