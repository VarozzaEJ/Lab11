import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class IUDoubleLinkedList<E> implements IndexedUnsortedList<E> {

    private int count, modCount;
    private BidirectionalNode<E> front, rear;

    // ============================================================
    // ADD METHODS
    // ============================================================

    @Override
    public void addToFront(E element) {
        BidirectionalNode<E> newNode = new BidirectionalNode<E>(element);
        newNode.setNext(front);
        front = newNode;
        if (rear == null) rear = newNode;
        count++;
        modCount++;
    }

    @Override
    public void addToRear(E element) {
        BidirectionalNode<E> newNode = new BidirectionalNode<E>(element);
        if (rear != null) rear.setNext(newNode);
        else front = newNode;
        rear = newNode;
        count++;
        modCount++;
    }

    @Override
    public void add(E element) {
        addToRear(element);
    }

    @Override
    public void addAfter(E element, E target) {
        BidirectionalNode<E> current = front;
        while (current != null && !current.getElement().equals(target)) {
            current = current.getNext();
        }
        if (current == null) throw new NoSuchElementException();
        BidirectionalNode<E> newNode = new BidirectionalNode<E>(element);
        newNode.setNext(current.getNext());
        current.setNext(newNode);
        if (newNode.getNext() == null) rear = newNode;
        count++;
        modCount++;
    }

    @Override
    public void add(int index, E element) {
        if (index < 0 || index > size()) throw new IndexOutOfBoundsException();

        BidirectionalNode<E> newNode = new BidirectionalNode<E>(element);

        if (index == 0) {
            newNode.setNext(front);
            front = newNode;
            if (rear == null) rear = newNode;
        } else {
            BidirectionalNode<E> prev = front;
            for (int i = 0; i < index - 1; i++)
                prev = prev.getNext();

            newNode.setNext(prev.getNext());
            prev.setNext(newNode);
            if (newNode.getNext() == null) rear = newNode;
        }

        count++;
        modCount++;
    }

    // ============================================================
    // REMOVE METHODS
    // ============================================================

    @Override
    public E removeFirst() {
        if (isEmpty()) throw new NoSuchElementException();
        return removeElement(null, front);
    }

    @Override
    public E removeLast() {
        if (isEmpty()) throw new NoSuchElementException();

        BidirectionalNode<E> prev = null, curr = front;
        while (curr.getNext() != null) {
            prev = curr;
            curr = curr.getNext();
        }
        return removeElement(prev, curr);
    }

    @Override
    public E remove(E element) {
        if (isEmpty()) throw new NoSuchElementException();

        BidirectionalNode<E> prev = null, curr = front;
        while (curr != null && !curr.getElement().equals(element)) {
            prev = curr;
            curr = curr.getNext();
        }
        if (curr == null) throw new NoSuchElementException();
        return removeElement(prev, curr);
    }

    @Override
    public E remove(int index) {
        if (index < 0 || index >= size()) throw new IndexOutOfBoundsException();

        BidirectionalNode<E> prev = null, curr = front;
        for (int i = 0; i < index; i++) {
            prev = curr;
            curr = curr.getNext();
        }
        return removeElement(prev, curr);
    }

    private E removeElement(BidirectionalNode<E> previous, BidirectionalNode<E> current) {
        E result = current.getElement();

        if (previous == null) {
            front = current.getNext();
        } else {
            previous.setNext(current.getNext());
        }

        if (current.getNext() == null) {
            rear = previous;
        }

        count--;
        modCount++;
        return result;
    }

    // ============================================================
    // SET / GET / BASIC OPERATIONS
    // ============================================================

    @Override
    public void set(int index, E element) {
        if (index < 0 || index >= size()) throw new IndexOutOfBoundsException();

        BidirectionalNode<E> curr = front;
        for (int i = 0; i < index; i++) curr = curr.getNext();
        curr.setElement(element);

        modCount++;   // <-- required for iterator concurrency
    }

    @Override
    public E get(int index) {
        if (index < 0 || index >= size()) throw new IndexOutOfBoundsException();

        BidirectionalNode<E> curr = front;
        for (int i = 0; i < index; i++) curr = curr.getNext();
        return curr.getElement();
    }

    @Override
    public int indexOf(E element) {
        BidirectionalNode<E> curr = front;
        int index = 0;
        while (curr != null) {
            if (curr.getElement().equals(element)) return index;
            curr = curr.getNext();
            index++;
        }
        return -1;
    }

    @Override public E first() {
        if (isEmpty()) throw new NoSuchElementException();
        return front.getElement();
    }

    @Override public E last() {
        if (isEmpty()) throw new NoSuchElementException();
        return rear.getElement();
    }

    @Override public boolean contains(E e) {
        return indexOf(e) != -1;
    }

    @Override public boolean isEmpty() { return count == 0; }
    @Override public int size() { return count; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        BidirectionalNode<E> curr = front;
        while (curr != null) {
            sb.append(curr.getElement());
            if (curr.getNext() != null) sb.append(",");
            curr = curr.getNext();
        }
        sb.append("]");
        return sb.toString();
    }

    // ============================================================
    // ITERATOR
    // ============================================================

    @Override
    public Iterator<E> iterator() {
        return new DLLIterator();
    }

    private class DLLIterator implements Iterator<E> {
        private BidirectionalNode<E> current = null;
        private BidirectionalNode<E> next = front;
        private int iterModCount = modCount;
        private boolean canRemove = false;

        private void checkMod() {
            if (iterModCount != modCount) throw new ConcurrentModificationException();
        }

        @Override
        public boolean hasNext() {
            checkMod();
            return next != null;
        }

        @Override
        public E next() {
            checkMod();
            if (next == null) throw new NoSuchElementException();

            current = next;
            next = next.getNext();
            canRemove = true;
            return current.getElement();
        }

        @Override
        public void remove() {
            checkMod();
            if (!canRemove || current == null) throw new IllegalStateException();

            BidirectionalNode<E> prev = null, curr = front;
            while (curr != current) {
                prev = curr;
                curr = curr.getNext();
            }

            if (prev == null) front = next;
            else prev.setNext(next);

            if (current == rear) rear = prev;

            count--;
            modCount++;
            iterModCount++;
            canRemove = false;
            current = null;
        }
    }

    // ============================================================
    // LIST ITERATOR (FINALLY FIXED VERSION)
    // ============================================================

    private class DLLListIterator implements ListIterator<E> {
        private BidirectionalNode<E> next;
        private BidirectionalNode<E> lastReturned;
        private int nextIdx;
        private int iterModCount;

        public DLLListIterator() {
            next = front;
            lastReturned = null;
            nextIdx = 0;
            iterModCount = modCount;
        }

        private void checkMod() {
            if (iterModCount != modCount) throw new ConcurrentModificationException();
        }

        @Override
        public boolean hasNext() {
            checkMod();
            return next != null;
        }

        @Override
        public E next() {
            checkMod();
            if (next == null) throw new NoSuchElementException();

            lastReturned = next;
            next = next.getNext();
            nextIdx++;
            return lastReturned.getElement();
        }

        @Override
        public boolean hasPrevious() {
            checkMod();
            return nextIdx > 0;
        }

        @Override
        public E previous() {
            checkMod();
            if (!hasPrevious()) throw new NoSuchElementException();

            int target = nextIdx - 1;
            BidirectionalNode<E> curr = front;
            for (int i = 0; i < target; i++)
                curr = curr.getNext();

            lastReturned = curr;
            next = curr;
            nextIdx--;
            return curr.getElement();
        }

        @Override public int nextIndex() { checkMod(); return nextIdx; }
        @Override public int previousIndex() { checkMod(); return nextIdx - 1; }

        @Override
        public void remove() {
            checkMod();
            if (lastReturned == null) throw new IllegalStateException();

            BidirectionalNode<E> prev = null, curr = front;
            while (curr != lastReturned) {
                prev = curr;
                curr = curr.getNext();
            }

            BidirectionalNode<E> after = lastReturned.getNext();

            if (prev == null) front = after;
            else prev.setNext(after);

            if (lastReturned == rear) rear = prev;

            if (next == lastReturned) next = after;
            else nextIdx--;

            lastReturned = null;
            count--;
            modCount++;
            iterModCount++;
        }

        @Override
        public void set(E e) {
            checkMod();
            if (lastReturned == null) throw new IllegalStateException();
            lastReturned.setElement(e);
            modCount++;
            iterModCount++;
        }

        @Override
        public void add(E e) {
            checkMod();
            BidirectionalNode<E> newNode = new BidirectionalNode<E>(e);

            if (front == null) {
                front = rear = newNode;
            } else if (next == front) {
                newNode.setNext(front);
                front = newNode;
            } else if (next == null) {
                rear.setNext(newNode);
                rear = newNode;
            } else {
                BidirectionalNode<E> prev = null, curr = front;
                while (curr != next) {
                    prev = curr;
                    curr = curr.getNext();
                }
                prev.setNext(newNode);
                newNode.setNext(next);
            }

            count++;
            modCount++;
            iterModCount++;
            nextIdx++;
            lastReturned = null;
        }
    }

    @Override
    public ListIterator<E> listIterator() { return new DLLListIterator(); }

    @Override
    public ListIterator<E> listIterator(int start) {
        if (start < 0 || start > size()) throw new IndexOutOfBoundsException();
        DLLListIterator it = new DLLListIterator();
        for (int i = 0; i < start; i++) it.next();
        return it;
    }
}