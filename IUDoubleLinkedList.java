import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class IUDoubleLinkedList<E> implements IndexedUnsortedList<E> {

    private int count, modCount;
    private BidirectionalNode<E> front, rear;
    @Override
    public void addToFront(E element) {
        BidirectionalNode<E> newNode = new BidirectionalNode<E>(element);
		newNode.setNext(front);
		front = newNode;
		if (rear == null) {
			rear = newNode;
		}
		count++;
		modCount++;
    }

    @Override
    public void addToRear(E element) {
        BidirectionalNode<E> newNode = new BidirectionalNode<E>(element);
		if (rear != null) {
			rear.setNext(newNode);
		} else {
			front = newNode;
		}
		rear = newNode;
		count++;
		modCount++;
    }

    @Override
    public void add(E element) {
        BidirectionalNode<E> newNode = new BidirectionalNode<E>(element);
		if (rear != null) {
			rear.setNext(newNode);
		} else {
			front = newNode;
		}
		modCount++;
		count++;
		rear = newNode;
    }

    @Override
	public void addAfter(E element, E target) {
		BidirectionalNode<E> current = front;
		while (current != null && !current.getElement().equals(target)) {
			current = current.getNext();
		}
		// Target not found
		if (current == null) {
			throw new NoSuchElementException();
		}
		BidirectionalNode<E> newNode = new BidirectionalNode<E>(element);
		newNode.setNext(current.getNext());
		current.setNext(newNode);
		if (newNode.getNext() == null) {
			rear = newNode;
		}
		count++;
		modCount++;
		
	}

	@Override
	public void add(int index, E element) {
		BidirectionalNode<E> newNode = new BidirectionalNode<E>(element);
		if (index < 0 || index > size()) {
			throw new IndexOutOfBoundsException();
		}
		if (index == 0) {
			newNode.setNext(front);
			front = newNode;
			if (rear == null) {
				rear = newNode;
			}
		} else {
			BidirectionalNode<E> current = front, previous = null;
			for (int i = 0; i < index; i++) {
				previous = current;
				current = current.getNext();
			}
			previous.setNext(newNode);
			newNode.setNext(current);
			if (newNode.getNext() == null) {
				rear = newNode;
			}
		}
		count++;
		modCount++;
		
	}

	@Override
	public E removeFirst() {
		if (isEmpty()) {
			throw new NoSuchElementException();
		}
		return removeElement(null, front);
	}

	@Override
	public E removeLast() {
		if (isEmpty()) {
			throw new NoSuchElementException();
		}
		BidirectionalNode<E> current = front, previous = null;
		while (current.getNext() != null) {
			previous = current;
			current = current.getNext();
		}
		return removeElement(previous, current);
	}

	@Override
	public E remove(E element) {
		if (isEmpty()) {
			throw new NoSuchElementException();
		}
		BidirectionalNode<E> current = front, previous = null;
		while (current != null && !current.getElement().equals(element)) {
			previous = current;
			current = current.getNext();
		}
		// Matching element not found
		if (current == null) {
			throw new NoSuchElementException();
		}
		return removeElement(previous, current);		
	}

    @Override
    public E remove(int index) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'remove'");
    }

    @Override
    public void set(int index, E element) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'set'");
    }

    @Override
    public E get(int index) {
        BidirectionalNode<E> node = this.front;
        if(index < 0 || index > size()) throw new IndexOutOfBoundsException();
        for(int i = 0; i < index; i++) {
            node = node.getNext();
        }
        return node.getElement();
    }

    @Override
    public int indexOf(E element) {
        BidirectionalNode<E> current = front;
		int index = 0;
		while (current != null) {
			if (current.getElement().equals(element)) {
				return index;
			}
			current = current.getNext();
			index++;
		}
		return -1;
    }

    @Override
    public E first() {
        if(isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.front.getElement();
    }

    @Override
    public E last() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return rear.getElement();
    }

    @Override
    public boolean contains(E target) {
        BidirectionalNode<E> current = front;
		while (current != null) {
			if (current.getElement().equals(target)) {
				return true;
			}
			current = current.getNext();
		}
		return false;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0 ? true : false;
    }

    @Override
    public int size() {
        return this.count;
    }

    private E removeElement(BidirectionalNode<E> previous, BidirectionalNode<E> current) {
		// Grab element
		E result = current.getElement();
		// If not the first element in the list
		if (previous != null) {
			previous.setNext(current.getNext());
		} else { // If the first element in the list
			front = current.getNext();
		}
		// If the last element in the list
		if (current.getNext() == null) {
			rear = previous;
		}
		count--;
		modCount++;

		return result;
	}

    @Override
    public Iterator<E> iterator() {
        return new DLLIterator();
    }

    private class DLLIterator implements Iterator<E> {
		private BidirectionalNode<E> previous;
		private BidirectionalNode<E> current;
		private BidirectionalNode<E> next;
		private int iterModCount;
		private boolean removeable;
		
		/** Creates a new iterator for the list */
		public DLLIterator() {
			previous = null;
			current = null;
			next = front;
			iterModCount = modCount;
		}

		@Override
		public boolean hasNext() {
            if (iterModCount != modCount) {
                throw new ConcurrentModificationException();
            }
            return next != null;
        }

		@Override
		public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            if (current != null) {
                previous = current;
            }
            current = next;
            next = next.getNext();
            removeable = true;
            return current.getElement();
        }
		
		@Override
		public void remove() {
            if (iterModCount != modCount) {
                throw new ConcurrentModificationException();
            }
			if (!removeable) {
				throw new IllegalStateException();
			}
            if (current == null) {
                throw new IllegalStateException();
            }
            if (previous == null) {
                front = next;
            } else {
                previous.setNext(next);
            }
            if (current == rear) {
                rear = previous;
            }
            current = null;
            removeable = false;
            count--;
            modCount++;
            iterModCount++;
        }

        public E previous() {
            return null;
        }
	}

	// IGNORE THE FOLLOWING CODE
	// DON'T DELETE ME, HOWEVER!!!
	@Override
	public ListIterator<E> listIterator() {
		return null;
	}

	@Override
	public ListIterator<E> listIterator(int startingIndex) {
		throw new UnsupportedOperationException();
	}

    
}
