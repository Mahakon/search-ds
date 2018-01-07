package ru.mail.polis;

import java.util.*;



public class AVLTree<E extends Comparable<E>> extends AbstractSet<E> implements BalancedSortedSet<E> {

    class AVLNode {
        AVLNode left;
        AVLNode right;
        AVLNode parent;
        E key;

        AVLNode(E k) {
            left = null;
            right = null;
            parent = null;
            key = k;
        }
    }

    private final Comparator<E> comparator;
    private AVLNode root;
    private int size;

    public AVLTree() {
        this(null);
    }

    public AVLTree(Comparator<E> comparator) {
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
            throw new NullPointerException("Input value is null");
        } else if (contains(value)) {
            return false;
        }
        AVLNode node = new AVLNode(value);

        insertAVL(this.root, node);
        size++;
        return true;
    }

    private void insertAVL(AVLNode p, AVLNode q) {
        if (p == null) {
            this.root = q;
        } else {
            if (compare(q.key, p.key) == -1) {
                if (p.left == null) {
                    p.left = q;
                    q.parent = p;

                    recursiveBalance(p);
                } else {
                    insertAVL(p.left, q);
                }
            } else if (compare(q.key, p.key) == 1) {
                if (p.right == null) {
                    p.right = q;
                    q.parent = p;

                    recursiveBalance(p);
                } else {
                    insertAVL(p.right, q);
                }
            }
        }
    }

    private void recursiveBalance(AVLNode cur) {
        int balance = getBalance(cur);

        if (balance == -2) {
            if (height(cur.left.left) >= height(cur.left.right)) {
                cur = rotateRight(cur);
            } else {
                cur = bigRotateRight(cur);
            }

        } else if (balance == 2) {
            if (height(cur.right.right) >= height(cur.right.left)) {
                cur = rotateLeft(cur);
            } else {
                cur = bigRotateLeft(cur);
            }
        }

        if (cur.parent != null) {
            recursiveBalance(cur.parent);
        } else {
            this.root = cur;
        }
    }

    // Вычисляет высоту для ноды.
    private int height(AVLNode cur) {
        if (cur == null) {
            return -1;
        }
        if (cur.left == null && cur.right == null) {
            return 0;
        } else if (cur.left == null) {
            return 1 + height(cur.right);
        } else if (cur.right == null) {
            return 1 + height(cur.left);
        } else {
            return 1 + maximum(height(cur.left), height(cur.right));
        }
    }

    private int maximum(int a, int b) {
        return a >= b ? a : b;
    }

    private int getBalance(AVLNode cur) {
        return height(cur.right) - height(cur.left);
    }

    private AVLNode rotateLeft(AVLNode a) {
        AVLNode b = a.right;
        b.parent = a.parent;

        a.right = b.left;

        if (a.right != null) {
            a.right.parent = a;
        }

        b.left = a;
        a.parent = b;

        if (b.parent != null) {
            if (b.parent.right == a) {
                b.parent.right = b;
            } else if (b.parent.left == a) {
                b.parent.left = b;
            }
        }

        return b;
    }

    private AVLNode rotateRight(AVLNode b) {
        // Тоже просто делаем малый правый поворот как на вики итмо.
        AVLNode a = b.left;
        a.parent = b.parent;

        b.left = a.right;

        if (b.left != null) {
            b.left.parent = b;
        }

        a.right = b;
        b.parent = a;

        if (a.parent != null) {
            if (a.parent.right == b) {
                a.parent.right = a;
            } else if (a.parent.left == b) {
                a.parent.left = a;
            }
        }

        return a;
    }

    private AVLNode bigRotateRight(AVLNode u) {
        u.left = rotateLeft(u.left);
        return rotateRight(u);
    }

    private AVLNode bigRotateLeft(AVLNode u) {
        u.right = rotateRight(u.right);
        return rotateLeft(u);
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

        if (value == null) {
            throw new NullPointerException("Input value is null");
        } else if (!contains(value)) {
            return false;
        }

        removeAVL(root, value);
        size--;
        return true;
    }

    private void removeAVL(AVLNode p, E q) {
        if (compare(p.key, q) == 1) {

            removeAVL(p.left, q);
        } else if (compare(p.key, q) == -1) {

            removeAVL(p.right, q);
        } else if (compare(p.key, q) == 0) {

            removeFoundNode(p);
        }
    }

    /**
     * Удаляем ноду из дерева, балансировка будет при необходимости.
     *
     * @param q нода, которую удаляем.
     */
    private void removeFoundNode(AVLNode q) {
        AVLNode r;

        if (q.left == null || q.right == null) {

            if (q.parent == null) {
                if (q.left != null) {

                    this.root = q.left;
                    q.left.parent = null;
                } else if (q.right != null) {

                    this.root = q.right;
                    q.right.parent = null;
                } else {

                    this.root = null;
                }

                q = null;
                return;
            }
            r = q;
        } else {

            r = successor(q);
            q.key = r.key;
        }

        AVLNode p;
        if (r.left != null) {
            p = r.left;
        } else {
            p = r.right;
        }

        if (p != null) {
            p.parent = r.parent;
        }

        if (r.parent == null) {
            this.root = p;
        } else {
            if (r == r.parent.left) {
                r.parent.left = p;
            } else {
                r.parent.right = p;
            }
            recursiveBalance(r.parent);
        }

        r = null;
    }

    private AVLNode successor(AVLNode x) {
        if (x.left != null) {
            AVLNode q = x.left;
            while (q.right != null) {
                q = q.right;
            }
            return q;
        }
        AVLNode y = x.parent;
        while (y != null && x == y.left) {
            x = y;
            y = y.parent;
        }
        return y;
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

    private boolean checkContains(AVLNode node, E value) {
        if (node == null || node.key == null) {
            return false;
        } else if (compare(node.key, value) == 0) {
            return true;
        } else if (compare(node.key, value) == 1) {
            return checkContains(node.left, value);
        } else if (compare(node.key, value) == -1) {
            return checkContains(node.right, value);
        } else {
            return false;
        }
    }

    /**
     * Ищет наименьший элемент в дереве
     * @return Возвращает наименьший элемент в дереве
     * @throws NoSuchElementException если дерево пустое
     */
    @Override
    public E first() {
        if (root == null) {
            throw new NoSuchElementException("AVLTree is empty, no first element");
        } else {
            AVLNode min = root;
            while (min.left != null) {
                min = min.left;
            }
            return min.key;
        }
    }

    /**
     * Ищет наибольший элемент в дереве
     * @return Возвращает наибольший элемент в дереве
     * @throws NoSuchElementException если дерево пустое
     */
    @Override
    public E last() {
        if (root == null) {
            throw new NoSuchElementException("AVLTree is empty, no last element");
        } else {
            AVLNode max = root;
            while (max.right != null) {
                max = max.right;
            }
            return max.key;
        }
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
        if (size < 0) {
            return Integer.MAX_VALUE;
        } else {
            return size;
        }
    }

    @Override
    public String toString() {
        return "AVLTree{" +
                "tree=" + root +
                "size=" + size + ", " +
                '}';
    }

    @Override
    public SortedSet<E> subSet(E fromElement, E toElement) {
        SortedSet<E> set = new TreeSet<>();
        getSet(root, set);
        return set.subSet(fromElement, toElement);
    }

    @Override
    public SortedSet<E> headSet(E toElement) {
        SortedSet<E> set = new TreeSet<>();
        getSet(root, set);
        return set.headSet(toElement);
    }

    @Override
    public SortedSet<E> tailSet(E fromElement) {
        SortedSet<E> set = new TreeSet<>();
        getSet(root, set);
        return set.tailSet(fromElement);
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private List<E> list;
            private int curr = 0;
            private int fixedSize;

            {
                list = new ArrayList<>();
                getList(root, list);
                fixedSize = list.size();
            }

            @Override
            public boolean hasNext() {
                return curr < fixedSize;
            }

            @Override
            public E next() {
                return list.get(curr++);
            }
        };
    }

    /**
     * Обходит дерево и проверяет что высоты двух поддеревьев
     * различны по высоте не более чем на 1
     *
     * @throws NotBalancedTreeException если высоты отличаются более чем на один
     */
    @Override
    public void checkBalanced() throws NotBalancedTreeException {
        traverseTreeAndCheckBalanced(root);
    }

    private int traverseTreeAndCheckBalanced(AVLNode curr) throws NotBalancedTreeException {
        if (curr == null) {
            return 1;
        }
        int leftHeight = traverseTreeAndCheckBalanced(curr.left);
        int rightHeight = traverseTreeAndCheckBalanced(curr.right);
        if (Math.abs(leftHeight - rightHeight) > 1) {
            throw NotBalancedTreeException.create("The heights of the two child subtrees of any node must be differ by at most one",
                    leftHeight, rightHeight, curr.toString());
        }
        return Math.max(leftHeight, rightHeight) + 1;
    }

    private void getSet(AVLNode curr, Set<E> set) {
        if (curr == null) return;
        getSet(curr.left, set);
        set.add(curr.key);
        getSet(curr.right, set);
    }

    private void getList(AVLNode curr, List<E> list) {
        if (curr == null) return;
        getList(curr.left, list);
        list.add(curr.key);
        getList(curr.right, list);
    }

    public static void main(String[] args) throws NotBalancedTreeException {
        AVLTree<Integer> tree =  new AVLTree<>();
        tree.add(10);
        tree.add(20);
        tree.add(30);
        tree.add(40);
        tree.add(50);
        tree.add(60);
        tree.add(70);

        for (Iterator<Integer> it = tree.iterator(); it.hasNext(); ) {
            System.out.println(it.next());
        }
    }

}
