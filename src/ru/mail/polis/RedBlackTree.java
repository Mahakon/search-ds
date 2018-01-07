package ru.mail.polis;

import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;

public class RedBlackTree<E extends Comparable<E>> extends AbstractSet<E> implements BalancedSortedSet<E> {

    enum Color {RED, BLACK}
    private Node root = null;
    private Node NIL = new Node(null, null, null, null, Color.BLACK);
    private int size = 0;
    private final Comparator<E> comparator;

    private class Node {
        Color color;
        Node left, right, parent;
        E data;

        Node(E data, Node left, Node right, Node parent, Color color) {
            this.data = data;
            this.left = left;
            this.right = right;
            this.parent = parent;
            this.color = Color.BLACK;
        }

        @Override
        public boolean equals(Object o) {
            if (!Node.class.isInstance(o)) {
                return false;
            }

            Node temp = (Node) o;
            return data == temp.data && left == temp.left &&
                    right == temp.right && parent == temp.parent &&
                    color == temp.color;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("N{");
            sb.append("d=").append(data);
            if (left != null) {
                sb.append(", l=").append(left);
            }
            if (right != null) {
                sb.append(", r=").append(right);
            }
            sb.append('}');
            return sb.toString();
        }
    }


    public RedBlackTree() {
        this(null);
    }

    public RedBlackTree(Comparator<E> comparator) {
        this.comparator = comparator;
    }

    /**
     * Вставляет элемент в дерево.
     * Инвариант: на вход всегда приходит NotNull объект, который имеет корректный тип
     *
     * @param value элемент который необходимо вставить
     * @return true, если элемент в дереве отсутствовал
     */
    @Override
    public boolean add(E value) {
        if (value == null) {
            throw new NullPointerException();
        }

        Node put = new Node(value, NIL,NIL, null, Color.BLACK);
        if (root == null) {
            root = put;
        } else {
            // contains
            Node curr = root;
            while (curr != null && !curr.equals(NIL)) {
                int num = compare(curr.data, value);
                if (num == 0) {
                    break;
                }

                if (compare(curr.data, value) < 0) {
                    curr = curr.right;
                } else {
                    curr = curr.left;
                }
            }

            if (curr != null && !curr.equals(NIL) && compare(curr.data, value) == 0) {
                return false;
            }

            RBInsert(put);
        }

        size++;
        return true;
    }

    private void RBInsert(Node z) {
        Node y = NIL;
        Node x = root;

        while (!x.equals(NIL)) {
            y = x;
            if (compare(z.data, x.data) < 0) {
                x = x.left;
            } else {
                x = x.right;
            }
        }

        z.parent = y;
        if (y.equals(NIL)) {
            root = z;
        } else if (compare(z.data, y.data) < 0) {
            y.left = z;
        } else {
            y.right = z;
        }

        z.left = NIL;
        z.right = NIL;
        z.color = Color.RED;
        RBInsertFix(z);
    }

    private void RBInsertFix(Node z) {
        Node y;
        while (z.parent != null && z.parent.color == Color.RED) {
            if (z.parent.equals(z.parent.parent.left)) {
                y = z.parent.parent.right;
                if (y.color == Color.RED) {
                    z.parent.color = Color.BLACK;
                    y.color = Color.BLACK;
                    z.parent.parent.color = Color.RED;
                    z = z.parent.parent;
                } else {
                    if (z.equals(z.parent.right)) {
                        z = z.parent;
                        leftRotate(z);
                    }

                    z.parent.color = Color.BLACK;
                    z.parent.parent.color = Color.RED;
                    rightRotate(z.parent.parent);
                }
            } else {
                y = z.parent.parent.left;
                if(y.color == Color.RED) {
                    z.parent.color = Color.BLACK;
                    y.color = Color.BLACK;
                    z.parent.parent.color = Color.RED;
                    z = z.parent.parent;
                } else {
                    if (z.equals(z.parent.left)) {
                        z = z.parent;
                        rightRotate(z);
                    }

                    z.parent.color = Color.BLACK;
                    z.parent.parent.color = Color.RED;
                    leftRotate(z.parent.parent);
                }
            }
        }
        root.color = Color.BLACK;
    }

    private void leftRotate(Node x) {
        Node y = x.right;
        x.right = y.left;

        if (!y.left.equals(NIL)) {
            y.left.parent = x;
        }

        y.parent = x.parent;
        if (x.parent == null) {
            root = y;
        } else if (x.equals(x.parent.left)) {
            x.parent.left = y;
        } else {
            x.parent.right = y;
        }

        y.left = x;
        x.parent = y;
    }

    private void rightRotate(Node y) {
        Node x = y.left;
        y.left = x.right;

        if (!x.right.equals(NIL)) {
            x.right.parent = y;
        }

        x.parent = y.parent;
        if (y.parent == null) {
            root = x;
        } else if (y.equals(y.parent.left)) {
            y.parent.left = x;
        } else {
            y.parent.right = x;
        }

        x.right = y;
        y.parent = x;
    }

    private Node getElement(Node node, E key) {
        if (node != null) {
            if (compare(node.data, key) < 0) {
                return getElement(node.right, key);
            } else if (compare(node.data, key) > 0) {
                return getElement(node.left, key);
            } else {
                return node;
            }
        }

        return null;
    }

    private void RBDelete(Node z) {
        Node x,y;
        if (z.left.equals(NIL) || z.right.equals(NIL)) {
            y = z;
        } else {
            y = TreeSuccessor(z);
        }

        if (!y.left.equals(NIL)) {
            x = y.left;
        } else {
            x = y.right;
        }

        x.parent = y.parent;
        if (y.parent == null) {
            root = x;
        } else if (y.equals(y.parent.left)) {
            y.parent.left = x;
        } else {
            y.parent.right = x;
        }

        if (!y.equals(z)) {
            z.data = y.data;
        }

        if (y.color == Color.BLACK) {
            RBDeleteFixUp(x);
        }

        //return y;
    }

    private void RBDeleteFixUp(Node x) {
        Node w;
        while(!x.equals(root) && x.color == Color.BLACK) {
            if (x.equals(x.parent.left)) {
                w = x.parent.right;
                if (w.color == Color.RED) {
                    w.color = Color.BLACK;
                    x.parent.color = Color.RED;
                    leftRotate(x.parent);
                    w = x.parent.right;
                }

                if (w.left.color == Color.BLACK && w.right.color == Color.BLACK){
                    w.color = Color.RED;
                    x = x.parent;
                } else {
                    if (w.right.color == Color.BLACK) {
                        w.left.color = Color.BLACK;
                        w.color = Color.RED;
                        rightRotate(w);
                        w = x.parent.right;
                    }

                    w.color = x.parent.color;
                    x.parent.color = Color.BLACK;
                    w.right.color = Color.BLACK;
                    leftRotate(x.parent);
                    x = root;
                }
            } else {
                w = x.parent.left;
                if (w.color == Color.RED){
                    w.color = Color.BLACK;
                    x.parent.color = Color.RED;
                    rightRotate(x.parent);
                    w = x.parent.left;
                }

                if (w.right.color == Color.BLACK && w.left.color == Color.BLACK) {
                    w.color = Color.RED;
                    x = x.parent;
                } else {
                    if (w.left.color == Color.BLACK) {
                        w.right.color = Color.BLACK;
                        w.color = Color.RED;
                        leftRotate(w);
                        w = x.parent.left;
                    }

                    w.color = x.parent.color;
                    x.parent.color = Color.BLACK;
                    w.left.color = Color.BLACK;
                    rightRotate(x.parent);
                    x = root;
                }
            }
        }

        x.color = Color.BLACK;
    }

    private Node TreeSuccessor(Node x) {
        Node y;
        if (!x.right.equals(NIL)) {
            return minimumm(x.right);
        }

        y = x.parent;
        while(!y.equals(NIL) && x.equals(y.right)) {
            x = y;
            y = y.parent;
        }

        return y;
    }

    private Node minimumm(Node x) {
        while (!x.left.equals(NIL)) {
            x = x.left;
        }

        return x;
    }

    /**
     * Удаляет элемент с таким же значением из дерева.
     * Инвариант: на вход всегда приходит NotNull объект, который имеет корректный тип
     *
     * @param object элемент который необходимо вставить
     * @return true, если элемент содержался в дереве
     */
    @Override
    public boolean remove(Object object) {
        E value = (E) object;

        if(value == null) {
            throw new NullPointerException();
        }

        Node curr = root;
        while (curr != null && !curr.equals(NIL)) {
            int num = compare(curr.data, value);
            if (num == 0) {
                break;
            }

            if (compare(curr.data, value) < 0) {
                curr = curr.right;
            } else {
                curr = curr.left;
            }
        }

        if (curr == null || curr.equals(NIL)) {
            return false;
        }

        if(size == 1) {
            root = null;
        } else {
            RBDelete(curr);
        }

        size--;
        return true;
    }

    /**
     * Ищет элемент с таким же значением в дереве.
     * Инвариант: на вход всегда приходит NotNull объект, который имеет корректный тип
     *
     * @param object элемент который необходимо поискать
     * @return true, если такой элемент содержится в дереве
     */
    @Override
    public boolean contains(Object object) {
        E value = (E) object;
        if (value == null) {
            throw new NullPointerException("Input value is null");
        } else {
            return checkContains(root, value);
        }
    }

    private boolean checkContains(Node node, E value) {
        if (node == null || node.data == null) {
            return false;
        } else if (compare(node.data, value) == 0) {
            return true;
        } else if (compare(node.data, value) == 1) {
            return checkContains(node.left, value);
        } else if (compare(node.data, value) == -1) {
            return checkContains(node.right, value);
        }

        return false;
    }

    /**
     * Ищет наименьший элемент в дереве
     * @return Возвращает наименьший элемент в дереве
     * @throws NoSuchElementException если дерево пустое
     */
    @Override
    public E first() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }

        Node curr = root;
        while (curr.left != null && !curr.left.equals(NIL)) {
            curr = curr.left;
        }

        if (root.left == null && root.right != null) {
            return root.right.data;
        } else if (root.left == null && root.right == null) {
            return root.data;
        }

        return curr.data;
    }

    /**
     * Ищет наибольший элемент в дереве
     * @return Возвращает наибольший элемент в дереве
     * @throws NoSuchElementException если дерево пустое
     */
    @Override
    public E last() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }

        Node curr = root;
        while (curr.right != null && !curr.right.equals(NIL)) {
            curr = curr.right;
        }

        if (root.right == null && root.left != null) {
            return root.left.data;
        } else if (root.right == null && root.left == null) {
            return root.data;
        }

        return curr.data;
    }

    private int compare(E v1, E v2) {
        return comparator == null ? v1.compareTo(v2) : comparator.compare(v1, v2);
    }

    @Override
    public Comparator<? super E> comparator() {
        return comparator;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public String toString() {
        return "RBTree{" +
                "size=" + size + ", " +
                "tree=" + root +
                '}';
    }

    @Override
    public SortedSet<E> subSet(E fromElement, E toElement) {
        throw new UnsupportedOperationException("subSet");
    }

    @Override
    public SortedSet<E> headSet(E toElement) {
        throw new UnsupportedOperationException("headSet");
    }

    @Override
    public SortedSet<E> tailSet(E fromElement) {
        throw new UnsupportedOperationException("tailSet");
    }

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException("iterator");
    }

    /**
     * Обходит дерево и проверяет выполнение свойств сбалансированного красно-чёрного дерева
     * <p>
     * 1) Корень всегда чёрный.
     * 2) Если узел красный, то его потомки должны быть чёрными (обратное не всегда верно)
     * 3) Все пути от узла до листьев содержат одинаковое количество чёрных узлов (чёрная высота)
     *
     * @throws NotBalancedTreeException если какое-либо свойство невыполнено
     */
    @Override
    public void checkBalanced() throws NotBalancedTreeException {
        if (root != null) {
            if (root.color != Color.BLACK) {
                throw new NotBalancedTreeException("Root must be black");
            }
            traverseTreeAndCheckBalanced(root);
        }
    }

    private int traverseTreeAndCheckBalanced(Node node) throws NotBalancedTreeException {
        if (node == null) {
            return 1;
        }
        int leftBlackHeight = traverseTreeAndCheckBalanced(node.left);
        int rightBlackHeight = traverseTreeAndCheckBalanced(node.right);
        if (leftBlackHeight != rightBlackHeight) {
            throw NotBalancedTreeException.create("Black height must be equal.", leftBlackHeight, rightBlackHeight, node.toString());
        }
        if (node.color == Color.RED) {
            checkRedNodeRule(node);
            return leftBlackHeight;
        }
        return leftBlackHeight + 1;
    }

    private void checkRedNodeRule(Node node) throws NotBalancedTreeException {
        if (node.left != null && node.left.color != Color.BLACK) {
            throw new NotBalancedTreeException("If a node is red, then left child must be black.\n" + node.toString());
        }
        if (node.right != null && node.right.color != Color.BLACK) {
            throw new NotBalancedTreeException("If a node is red, then right child must be black.\n" + node.toString());
        }
    }
}