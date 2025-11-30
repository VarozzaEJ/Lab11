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
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException();
        }
        BidirectionalNode<E> current = front, previous = null;
        for (int i = 0; i < index; i++) {
            previous = current;
            current = current.getNext();
        }
        return removeElement(previous, current);
    }

    @Override
    public void set(int index, E element) {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException();
        }
        BidirectionalNode<E> current = front;
        for (int i = 0; i < index; i++) {
            current = current.getNext();
        }
        current.setElement(element);
    }

    @Override
    public E get(int index) {
        BidirectionalNode<E> node = this.front;
        if(index < 0 || index > size()) throw new IndexOutOfBoundsException();
        for(int i = 0; i < index; i++) {
            node = node.getNext();
        }
        if(node == null) throw new IndexOutOfBoundsException();
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

    @Override
	public String toString() {
		String result = "[";
		BidirectionalNode<E> current = front;
		while (current != null) {
			result += current.getElement().toString();
			if (current.getNext() != null) {
				result += ",";
			}
			current = current.getNext();
		}
		result += "]";
		return result;
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
	}

    private class DLLListIterator implements ListIterator<E> {
        private BidirectionalNode<E> previous;
		private BidirectionalNode<E> current;
		private BidirectionalNode<E> next;
		private int iterModCount;
		private boolean removeable;
		
		/** Creates a new iterator for the list */
		public DLLListIterator() {
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

        @Override
        public boolean hasPrevious() {
            if (iterModCount != modCount) {
                throw new ConcurrentModificationException();
            }
            return previous != null;
        }

        @Override
        public E previous() {
            if (!hasPrevious()) {
                throw new NoSuchElementException();
            }
            if (current != null) {
                next = current;
            }
            current = previous;
            // Move previous pointer back
            BidirectionalNode<E> temp = front;
            BidirectionalNode<E> prevPrev = null;
            while (temp != null && temp != current) {
                prevPrev = temp;
                temp = temp.getNext();
            }
            previous = prevPrev;
            removeable = true;
            return current.getElement();
        }
        // @Override
        // public E previous() {
        //     if (!hasPrevious()) {
        //         throw new NoSuchElementException();
        //     }
        //     if (current != null) {
        //         current = previous;
        //     }
        //     current = previous;
        //     next = current.getNext();
        //     removeable = true;
        //     return current.getElement();
        // }

        // if (!hasNext()) {
        //         throw new NoSuchElementException();
        //     }
        //     if (current != null) {
        //         previous = current;
        //     }
        //     current = next;
        //     next = next.getNext();
        //     removeable = true;
        //     return current.getElement();
        @Override
        public int nextIndex() {
            if(indexOf(current.getElement()) == size() - 1) {
                return size();
            }
            return indexOf(current.getElement()) + 1;
        }

        @Override
        public int previousIndex() {
            if(indexOf(current.getElement()) == 0) {
                return -1;
            }
            return indexOf(current.getElement()) - 1;
        }

        @Override
        public void set(E e) {
            if(iterModCount != modCount) {
                throw new ConcurrentModificationException();
            }
            if(!removeable) {
                throw new IllegalStateException();
            }
            current.setElement(e);
            modCount++;
            iterModCount++;
        }

        @Override
        public void add(E e) {
            if(iterModCount != modCount) {
                throw new ConcurrentModificationException();
            }
            BidirectionalNode<E> newNode = new BidirectionalNode<E>(e);
            if(previous == null) {
                newNode.setNext(front);
                front = newNode;
                if(rear == null) {
                    rear = newNode;
                }
            } else {
                previous.setNext(newNode);
                newNode.setNext(current);
                if(newNode.getNext() == null) {
                    rear = newNode;
                }
            }
            count++;
            modCount++;
            iterModCount++;
            previous = newNode;
            removeable = false;
        }
        
    }

	// IGNORE THE FOLLOWING CODE
	// DON'T DELETE ME, HOWEVER!!!
	@Override
	public ListIterator<E> listIterator() {
		return new DLLListIterator();
	}

	@Override
	public ListIterator<E> listIterator(int startingIndex) {
		DLLListIterator iter = new DLLListIterator();
        if (startingIndex < 0 || startingIndex > size()) {
            throw new IndexOutOfBoundsException();
        }
        for (int i = 0; i < startingIndex; i++) {
            iter.next();
        }
        return iter;
	}

    
}
