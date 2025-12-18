package ua.kpi.comsys.test2.implementation;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.*;

import ua.kpi.comsys.test2.NumberList;

/**
 * Custom implementation of NumberList interface.
 * Variant: 3416
 * - List Type: Circular Doubly Linked List
 * - Base: Ternary (3)
 * - Operation: Addition
 * - Group^ IO-34
 * - Change Scale: Octal (8)
 *
 * @author Paniushkin Vladislav (Variant 3416)
 */
public class NumberListImpl implements NumberList {

    private Node head;
    private int size;
    private int base; // System base (3 for main, 8 for changed scale)

    /**
     * Inner class for Doubly Linked Node.
     */
    private class Node {
        byte value;
        Node next;
        Node prev;

        Node(byte value) {
            this.value = value;
        }
    }

    /**
     * Default constructor. Returns empty <tt>NumberListImpl</tt>
     * Default base is 3.
     */
    public NumberListImpl() {
        this.head = null;
        this.size = 0;
        this.base = 3;
    }

    /**
     * Private constructor for internal use (changing scale).
     */
    private NumberListImpl(int base) {
        this.head = null;
        this.size = 0;
        this.base = base;
    }

    /**
     * Constructs new <tt>NumberListImpl</tt> by <b>decimal</b> number
     * from file, defined in string format.
     *
     * @param file - file where number is stored.
     */
    public NumberListImpl(File file) {
        this();
        try (Scanner scanner = new Scanner(file)) {
            if (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                initFromDecimalString(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructs new <tt>NumberListImpl</tt> by <b>decimal</b> number
     * in string notation.
     *
     * @param value - number in string notation.
     */
    public NumberListImpl(String value) {
        this();
        initFromDecimalString(value);
    }

    /**
     * Helper to initialize list from a decimal string.
     * Converts Decimal String -> BigInteger -> Base X String -> List
     */
    private void initFromDecimalString(String value) {
        if (value == null || value.isEmpty()) return;
        try {
            BigInteger decimal = new BigInteger(value);
            if (decimal.compareTo(BigInteger.ZERO) < 0) {
                throw new IllegalArgumentException("Number must be positive");
            }
            // Convert to target base string
            String baseString = decimal.toString(this.base);
            for (char c : baseString.toCharArray()) {
                this.add((byte) Character.getNumericValue(c));
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format");
        }
    }

    /**
     * Saves the number, stored in the list, into specified file
     * in <b>decimal</b> scale of notation.
     *
     * @param file - file where number has to be stored.
     */
    public void saveList(File file) {
        try (java.io.PrintWriter pw = new java.io.PrintWriter(file)) {
            pw.print(this.toDecimalString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns student's record book number.
     *
     * @return student's record book number.
     */
    public static int getRecordBookNumber() {
        return 3416;
    }

    /**
     * Returns new <tt>NumberListImpl</tt> which represents the same number
     * in Octal scale of notation (Base 8).
     *
     * @return <tt>NumberListImpl</tt> in Octal scale.
     */
    public NumberListImpl changeScale() {
        // Convert current number to BigInteger
        BigInteger val = new BigInteger(this.toDecimalString());
        
        // Create new list with Base 8
        NumberListImpl newList = new NumberListImpl(8);
        
        String octalString = val.toString(8);
        for (char c : octalString.toCharArray()) {
            newList.add((byte) Character.getNumericValue(c));
        }
        
        return newList;
    }

    /**
     * Returns new <tt>NumberListImpl</tt> which represents the result of
     * ADDITION of two numbers.
     *
     * @param arg - second argument (another NumberList)
     * @return result of addition (new list).
     */
    public NumberListImpl additionalOperation(NumberList arg) {
        NumberListImpl other = (NumberListImpl) arg;
        NumberListImpl result = new NumberListImpl(this.base);

        // We need to traverse both lists from end (least significant digit) to start.
        // Since it's a circular list, head.prev is the last element.

        Node ptr1 = this.head != null ? this.head.prev : null;
        Node ptr2 = other.head != null ? other.head.prev : null;

        int count1 = this.size;
        int count2 = other.size;

        int carry = 0;

        // Loop backwards
        while (count1 > 0 || count2 > 0 || carry > 0) {
            int val1 = (count1 > 0) ? ptr1.value : 0;
            int val2 = (count2 > 0) ? ptr2.value : 0;

            int sum = val1 + val2 + carry;
            
            byte remainder = (byte) (sum % this.base);
            carry = sum / this.base;

            // We prepend digits to result because we calculate from end
            result.add(0, remainder); 

            if (count1 > 0) {
                ptr1 = ptr1.prev;
                count1--;
            }
            if (count2 > 0) {
                ptr2 = ptr2.prev;
                count2--;
            }
        }

        return result;
    }

    /**
     * Returns string representation of number in <b>decimal</b> scale.
     */
    public String toDecimalString() {
        if (isEmpty()) return "0";
        
        BigInteger result = BigInteger.ZERO;
        BigInteger bigBase = BigInteger.valueOf(this.base);

        Node current = head;
        // Logic: result = result * base + digit
        for (int i = 0; i < size; i++) {
            result = result.multiply(bigBase).add(BigInteger.valueOf(current.value));
            current = current.next;
        }
        return result.toString();
    }

    @Override
    public String toString() {
        if (isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        Node current = head;
        for (int i = 0; i < size; i++) {
            sb.append(current.value);
            current = current.next;
        }
        return sb.toString();
    }

    // --- List Interface Implementation ---

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    @Override
    public Iterator<Byte> iterator() {
        return new Iterator<Byte>() {
            private Node current = head;
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < size;
            }

            @Override
            public Byte next() {
                if (!hasNext()) throw new NoSuchElementException();
                Byte val = current.value;
                current = current.next;
                index++;
                return val;
            }
        };
    }

    @Override
    public Object[] toArray() {
        Object[] arr = new Object[size];
        Node current = head;
        for (int i = 0; i < size; i++) {
            arr[i] = current.value;
            current = current.next;
        }
        return arr;
    }

    // According to task, this one is NOT implemented
    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("Not implemented as per assignment.");
    }

    @Override
    public boolean add(Byte e) {
        if (e == null) throw new NullPointerException();
        if (e < 0 || e >= base) throw new IllegalArgumentException("Digit " + e + " is not valid for base " + base);

        Node newNode = new Node(e);
        if (head == null) {
            head = newNode;
            head.next = head;
            head.prev = head;
        } else {
            Node tail = head.prev;
            tail.next = newNode;
            newNode.prev = tail;
            newNode.next = head;
            head.prev = newNode;
        }
        size++;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (head == null) return false;
        Node current = head;
        for (int i = 0; i < size; i++) {
            if (o.equals(current.value)) {
                removeNode(current);
                return true;
            }
            current = current.next;
        }
        return false;
    }

    private void removeNode(Node node) {
        if (size == 1) {
            head = null;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
            if (node == head) {
                head = node.next;
            }
        }
        size--;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object e : c) {
            if (!contains(e)) return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends Byte> c) {
        boolean modified = false;
        for (Byte e : c) {
            if (add(e)) modified = true;
        }
        return modified;
    }

    @Override
    public boolean addAll(int index, Collection<? extends Byte> c) {
        // Simple implementation: insert one by one
        // Note: This is O(N*M) which is acceptable for this lab scope
        boolean modified = false;
        int currentIndex = index;
        for (Byte e : c) {
            add(currentIndex++, e);
            modified = true;
        }
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        for (Object e : c) {
            while (contains(e)) {
                remove(e);
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        Node current = head;
        int count = 0;
        while (count < size) {
            if (!c.contains(current.value)) {
                Node next = current.next;
                removeNode(current);
                current = next; // size decremented in removeNode
                modified = true;
                // do not increment count as size decreased
            } else {
                current = current.next;
                count++;
            }
        }
        return modified;
    }

    @Override
    public void clear() {
        head = null;
        size = 0;
    }

    @Override
    public Byte get(int index) {
        checkIndex(index);
        Node current = getNode(index);
        return current.value;
    }

    @Override
    public Byte set(int index, Byte element) {
        checkIndex(index);
        if (element < 0 || element >= base) throw new IllegalArgumentException("Invalid digit");
        Node current = getNode(index);
        Byte old = current.value;
        current.value = element;
        return old;
    }

    @Override
    public void add(int index, Byte element) {
        if (index < 0 || index > size) throw new IndexOutOfBoundsException();
        if (element < 0 || element >= base) throw new IllegalArgumentException("Invalid digit");

        if (index == size) {
            add(element);
        } else {
            Node newNode = new Node(element);
            Node current = (index == 0) ? head : getNode(index);
            
            // Insert before current
            Node prevNode = current.prev;
            
            prevNode.next = newNode;
            newNode.prev = prevNode;
            newNode.next = current;
            current.prev = newNode;
            
            if (index == 0) {
                head = newNode;
            }
            size++;
        }
    }

    @Override
    public Byte remove(int index) {
        checkIndex(index);
        Node current = getNode(index);
        Byte val = current.value;
        removeNode(current);
        return val;
    }

    @Override
    public int indexOf(Object o) {
        if (head == null) return -1;
        Node current = head;
        for (int i = 0; i < size; i++) {
            if (o.equals(current.value)) return i;
            current = current.next;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        if (head == null) return -1;
        Node current = head.prev; // start from tail
        for (int i = size - 1; i >= 0; i--) {
            if (o.equals(current.value)) return i;
            current = current.prev;
        }
        return -1;
    }

    @Override
    public ListIterator<Byte> listIterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<Byte> listIterator(int index) {
        // Implementing full list iterator is complex, returning basic one or throwing is often standard for labs
        // unless strictly required. Providing a stub here.
        throw new UnsupportedOperationException("ListIterator not fully implemented");
    }

    @Override
    public List<Byte> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException("subList not implemented");
    }

    // --- Specific Custom Methods ---

    @Override
    public boolean swap(int index1, int index2) {
        if (index1 < 0 || index1 >= size || index2 < 0 || index2 >= size) return false;
        if (index1 == index2) return true;

        Node n1 = getNode(index1);
        Node n2 = getNode(index2);

        byte temp = n1.value;
        n1.value = n2.value;
        n2.value = temp;

        return true;
    }

    @Override
    public void sortAscending() {
        if (size <= 1) return;
        // Simple bubble sort for linked list
        for (int i = 0; i < size; i++) {
            Node current = head;
            for (int j = 0; j < size - 1 - i; j++) {
                if (current.value > current.next.value) {
                    byte temp = current.value;
                    current.value = current.next.value;
                    current.next.value = temp;
                }
                current = current.next;
            }
        }
    }

    @Override
    public void sortDescending() {
        if (size <= 1) return;
        for (int i = 0; i < size; i++) {
            Node current = head;
            for (int j = 0; j < size - 1 - i; j++) {
                if (current.value < current.next.value) {
                    byte temp = current.value;
                    current.value = current.next.value;
                    current.next.value = temp;
                }
                current = current.next;
            }
        }
    }

    @Override
    public void shiftLeft() {
        if (size > 1) {
            head = head.next;
        }
    }

    @Override
    public void shiftRight() {
        if (size > 1) {
            head = head.prev;
        }
    }

    // --- Private Helpers ---

    private void checkIndex(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }

    private Node getNode(int index) {
        // Optimization: traverse from head or tail
        if (index < (size / 2)) {
            Node x = head;
            for (int i = 0; i < index; i++)
                x = x.next;
            return x;
        } else {
            Node x = head.prev;
            for (int i = size - 1; i > index; i--)
                x = x.prev;
            return x;
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NumberList)) return false;
        List<?> that = (List<?>) o;
        if (this.size() != that.size()) return false;
        Iterator<Byte> i1 = this.iterator();
        Iterator<?> i2 = that.iterator();
        while (i1.hasNext()) {
            if (!Objects.equals(i1.next(), i2.next())) return false;
        }
        return true;
    }
}
