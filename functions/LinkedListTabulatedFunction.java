package functions;

public class LinkedListTabulatedFunction implements TabulatedFunction {

    // Класс для узла списка
    protected class FunctionNode {
        public FunctionPoint point;
        public FunctionNode prev;
        public FunctionNode next;

        public FunctionNode(FunctionPoint point) {
            this.point = point;
            this.prev = null;
            this.next = null;
        }

        public FunctionNode(FunctionPoint point, FunctionNode prev, FunctionNode next) {
            this.point = point;
            this.prev = prev;
            this.next = next;
        }
    }

    private FunctionNode head;
    private FunctionNode lastAccessed;
    private int lastIndex;
    private int pointsCount;

    // Конструкторы
    public LinkedListTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }

        // Создание головы
        head = new FunctionNode(null);
        head.next = head;
        head.prev = head;

        this.pointsCount = 0;
        this.lastAccessed = null;
        this.lastIndex = -1;

        double step = (rightX - leftX) / (pointsCount - 1);

        // Создание точек
        for (int i = 0; i < pointsCount; i++) {
            FunctionPoint point = new FunctionPoint(leftX + i * step, 0);
            addNodeToTail().point = point;
        }
    }

    public LinkedListTabulatedFunction(double leftX, double rightX, double[] values) {
        this(leftX, rightX, values.length);

        // Устанавливаем значения Y
        FunctionNode current = head.next;
        for (int i = 0; i < values.length; i++) {
            current.point.setY(values[i]);
            current = current.next;
        }
    }

    // Методы работы со списком
    private FunctionNode getNodeByIndex(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(
                    "Индекс " + index + " вне границ [0, " + (pointsCount - 1) + "]"
            );
        }

        // Используем кэш если близко
        if (lastAccessed != null && Math.abs(index - lastIndex) <= 1) {
            if (index == lastIndex) return lastAccessed;
            if (index == lastIndex + 1) {
                lastAccessed = lastAccessed.next;
                lastIndex = index;
                return lastAccessed;
            }
            if (index == lastIndex - 1) {
                lastAccessed = lastAccessed.prev;
                lastIndex = index;
                return lastAccessed;
            }
        }

        // Быстрый доступ к граничным элементам
        if (index == 0) {
            lastAccessed = head.next;
            lastIndex = 0;
            return lastAccessed;
        }
        if (index == pointsCount - 1) {
            lastAccessed = head.prev;
            lastIndex = pointsCount - 1;
            return lastAccessed;
        }

        // Выбираем направление обхода от ближайшей точки
        FunctionNode current;
        int startIndex;
        if (lastAccessed != null && Math.abs(index - lastIndex) < pointsCount / 2) {
            current = lastAccessed;
            startIndex = lastIndex;
        } else {
            current = head.next;
            startIndex = 0;
        }

        if (index > startIndex) {
            for (int i = startIndex; i < index; i++) {
                current = current.next;
            }
        } else {
            for (int i = startIndex; i > index; i--) {
                current = current.prev;
            }
        }

        lastAccessed = current;
        lastIndex = index;

        return current;
    }

    private FunctionNode addNodeToTail() {
        FunctionNode newNode = new FunctionNode(null, head.prev, head);
        head.prev.next = newNode;
        head.prev = newNode;
        pointsCount++;
        lastAccessed = null;
        return newNode;
    }

    private FunctionNode addNodeByIndex(int index) {
        if (index < 0 || index > pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(
                    "Индекс " + index + " вне границ [0, " + pointsCount + "]"
            );
        }

        if (index == pointsCount) {
            return addNodeToTail();
        }

        FunctionNode nodeAtIndex = getNodeByIndex(index);
        FunctionNode newNode = new FunctionNode(null, nodeAtIndex.prev, nodeAtIndex);

        nodeAtIndex.prev.next = newNode;
        nodeAtIndex.prev = newNode;

        pointsCount++;
        lastAccessed = null;
        return newNode;
    }

    private FunctionNode deleteNodeByIndex(int index) {
        if (pointsCount < 3) {
            throw new IllegalStateException("Нельзя удалить точку: останется меньше 2 точек");
        }

        FunctionNode nodeToDelete = getNodeByIndex(index);

        nodeToDelete.prev.next = nodeToDelete.next;
        nodeToDelete.next.prev = nodeToDelete.prev;

        pointsCount--;
        lastAccessed = null;

        return nodeToDelete;
    }

    // Реализация методов интерфейса
    @Override
    public double getLeftDomainBorder() {
        return head.next.point.getX();
    }

    @Override
    public double getRightDomainBorder() {
        return head.prev.point.getX();
    }

    @Override
    public double getFunctionValue(double x) {
        double leftBorder = getLeftDomainBorder();
        double rightBorder = getRightDomainBorder();

        if (x < leftBorder || x > rightBorder) {
            return Double.NaN;
        }

        // Если x близко к границам
        if (Math.abs(x - leftBorder) < 1e-10) {
            return head.next.point.getY();
        }
        if (Math.abs(x - rightBorder) < 1e-10) {
            return head.prev.point.getY();
        }

        // Ищем интервал
        FunctionNode current = head.next;
        while (current != head && current.point.getX() < x) {
            current = current.next;
        }

        // Если x равен координате точки
        if (Math.abs(current.point.getX() - x) < 1e-10) {
            return current.point.getY();
        }

        // Линейная интерполяция
        FunctionPoint left = current.prev.point;
        FunctionPoint right = current.point;
        return left.getY() + (right.getY() - left.getY()) *
                (x - left.getX()) / (right.getX() - left.getX());
    }

    @Override
    public int getPointsCount() {
        return pointsCount;
    }

    @Override
    public FunctionPoint getPoint(int index) {
        FunctionNode node = getNodeByIndex(index);
        return new FunctionPoint(node.point);
    }

    @Override
    public void setPoint(int index, FunctionPoint point)
            throws InappropriateFunctionPointException {

        FunctionNode node = getNodeByIndex(index);

        // Проверяем порядок
        if ((index > 0 && point.getX() <= node.prev.point.getX()) ||
                (index < pointsCount - 1 && point.getX() >= node.next.point.getX())) {
            throw new InappropriateFunctionPointException("Нарушение порядка точек по X");
        }

        node.point = new FunctionPoint(point);
    }

    @Override
    public double getPointX(int index) {
        return getNodeByIndex(index).point.getX();
    }

    @Override
    public void setPointX(int index, double x)
            throws InappropriateFunctionPointException {

        FunctionNode node = getNodeByIndex(index);

        // Проверяем порядок
        if ((index > 0 && x <= node.prev.point.getX()) ||
                (index < pointsCount - 1 && x >= node.next.point.getX())) {
            throw new InappropriateFunctionPointException("Нарушение порядка точек по X");
        }

        node.point.setX(x);
    }

    @Override
    public double getPointY(int index) {
        return getNodeByIndex(index).point.getY();
    }

    @Override
    public void setPointY(int index, double y) {
        getNodeByIndex(index).point.setY(y);
    }

    @Override
    public void deletePoint(int index) {
        deleteNodeByIndex(index);
    }

    @Override
    public void addPoint(FunctionPoint point)
            throws InappropriateFunctionPointException {

        if (point.getX() < getLeftDomainBorder()) {
            FunctionNode newNode = new FunctionNode(new FunctionPoint(point), head, head.next);
            head.next.prev = newNode;
            head.next = newNode;
            pointsCount++;
            lastAccessed = null;
            return;
        }

        if (point.getX() > getRightDomainBorder()) {
            FunctionNode newNode = new FunctionNode(new FunctionPoint(point), head.prev, head);
            head.prev.next = newNode;
            head.prev = newNode;
            pointsCount++;
            lastAccessed = null;
            return;
        }

        // Проверяем существование точки с таким X
        FunctionNode current = head.next;
        while (current != head) {
            if (Math.abs(current.point.getX() - point.getX()) < 1e-10) {
                throw new InappropriateFunctionPointException("Точка с таким X уже существует");
            }
            if (current.point.getX() > point.getX()) {
                break;
            }
            current = current.next;
        }

        // Вставляем перед curr
        FunctionNode newNode = new FunctionNode(new FunctionPoint(point), current.prev, current);
        current.prev.next = newNode;
        current.prev = newNode;
        pointsCount++;
        lastAccessed = null;
    }

    // Дополнительные оптимизированные методы
    public FunctionPoint[] getPointsRange(int startIndex, int count) {
        if (startIndex < 0 || startIndex + count > pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException();
        }

        FunctionPoint[] result = new FunctionPoint[count];
        FunctionNode current = getNodeByIndex(startIndex);

        for (int i = 0; i < count; i++) {
            result[i] = new FunctionPoint(current.point);
            current = current.next;
        }

        return result;
    }

    // Для теста
    public void printPoints() {
        System.out.println("Связный список точек (всего " + pointsCount + "):");
        FunctionNode current = head.next;
        int i = 0;
        while (current != head) {
            System.out.printf("[%d]: [%.2f; %.2f]\n",
                    i++, current.point.getX(), current.point.getY());
            current = current.next;
        }
    }
}