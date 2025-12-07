import functions.*;

public class Main {
    public static void main(String[] args) {
        // Тестируем обе реализации через интерфейс
        System.out.println("1. ТЕСТИРОВАНИЕ ArrayTabulatedFunction:");
        testArrayImplementation();

        System.out.println("\n" + "=".repeat(60) + "\n");

        System.out.println("2. ТЕСТИРОВАНИЕ LinkedListTabulatedFunction:");
        testLinkedListImplementation();

        System.out.println("\n" + "=".repeat(60) + "\n");

        System.out.println("3. СРАВНИТЕЛЬНОЕ ТЕСТИРОВАНИЕ:");
        comparativeTesting();

        System.out.println("\n" + "=".repeat(60) + "\n");

        System.out.println("4. ТЕСТИРОВАНИЕ ИСКЛЮЧЕНИЙ:");
        testExceptions();
    }

    // =================== ТЕСТИРОВАНИЕ ArrayTabulatedFunction ===================

    private static void testArrayImplementation() {
        try {
            // Объявляем через интерфейс, создаем через класс
            TabulatedFunction arrayFunc = new ArrayTabulatedFunction(0, 4, 5);
            System.out.println("Создан ArrayTabulatedFunction [0, 4] с 5 точками");

            // y = x^2
            for (int i = 0; i < arrayFunc.getPointsCount(); i++) {
                double x = arrayFunc.getPointX(i);
                arrayFunc.setPointY(i, x * x);
            }

            System.out.println("Установлены значения y = x^2");

            // Демонстрация работы
            System.out.println("\nДемонстрация интерполяции:");
            double[] testX = {-0.5, 0.0, 0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5};
            for (double x : testX) {
                double y = arrayFunc.getFunctionValue(x);
                System.out.printf("f(%.1f) = %s\n", x,
                        Double.isNaN(y) ? "не определена" : String.format("%.4f", y));
            }

            // Тест операций с точками
            System.out.println("\nТест операций с точками:");
            System.out.println(" Исходные точки:");
            printPoints(arrayFunc);

            // Добавляем точку
            System.out.println(" Добавляем точку (1.5, 2.25):");
            arrayFunc.addPoint(new FunctionPoint(1.5, 2.25));
            printPoints(arrayFunc);

            // Изменяем точку
            System.out.println(" Изменяем точку с индексом 2 на (2.2, 4.84):");
            arrayFunc.setPoint(2, new FunctionPoint(2.2, 4.84));
            printPoints(arrayFunc);

            // Удаляем точку
            System.out.println(" Удаляем точку с индексом 1:");
            arrayFunc.deletePoint(1);
            printPoints(arrayFunc);

            System.out.println("Все операции выполнены успешно");

        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    // =================== ТЕСТИРОВАНИЕ LinkedListTabulatedFunction ===================

    private static void testLinkedListImplementation() {
        try {
            TabulatedFunction listFunc = new LinkedListTabulatedFunction(0, 4, 5);
            System.out.println("Создан LinkedListTabulatedFunction [0, 4] с 5 точками");

            // y = sin(x)
            for (int i = 0; i < listFunc.getPointsCount(); i++) {
                double x = listFunc.getPointX(i);
                listFunc.setPointY(i, Math.sin(x));
            }

            System.out.println("Установлены значения y = sin(x)");

            // Демонстрация работы
            System.out.println("\nДемонстрация интерполяции:");
            double[] testX = {-0.5, 0.0, 0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5};
            for (double x : testX) {
                double y = listFunc.getFunctionValue(x);
                System.out.printf("f(%.1f) = %s\n", x,
                        Double.isNaN(y) ? "не определена" : String.format("%.4f", y));
            }

            // Тест операций с точками
            System.out.println("\nТест операций с точками:");
            System.out.println(" Исходные точки:");
            printPoints(listFunc);

            // Добавляем точку
            System.out.println(" Добавляем точку (1.5, 0.997):");
            listFunc.addPoint(new FunctionPoint(1.5, 0.997));
            printPoints(listFunc);

            // Изменяем точку
            System.out.println(" Изменяем точку с индексом 2 на (2.2, 0.808):");
            listFunc.setPoint(2, new FunctionPoint(2.2, 0.808));
            printPoints(listFunc);

            // Удаляем точку
            System.out.println(" Удаляем точку с индексом 1:");
            listFunc.deletePoint(1);
            printPoints(listFunc);

            System.out.println("Все операции выполнены успешно");

        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    // =================== СРАВНИТЕЛЬНОЕ ТЕСТИРОВАНИЕ ===================

    private static void comparativeTesting() {
        System.out.println("Создаем одинаковые функции разными реализациями:");

        try {
            // Создаем одинаковые функции
            TabulatedFunction arrayFunc = new ArrayTabulatedFunction(0, 2, 3);
            TabulatedFunction listFunc = new LinkedListTabulatedFunction(0, 2, 3);

            // Устанавливаем одинаковые значения
            for (int i = 0; i < 3; i++) {
                double x = arrayFunc.getPointX(i);
                double y = Math.exp(x); // y = e^x
                arrayFunc.setPointY(i, y);
                listFunc.setPointY(i, y);
            }

            System.out.println("Функции созданы (y = e^x)");
            System.out.println("\nСравнение значений в точках:");

            // Сравниваем в нескольких точках
            double[] comparePoints = {0.0, 0.3, 0.7, 1.0, 1.3, 1.7, 2.0, 2.5};
            boolean allMatch = true;

            for (double x : comparePoints) {
                double arrayVal = arrayFunc.getFunctionValue(x);
                double listVal = listFunc.getFunctionValue(x);
                boolean match = Math.abs(arrayVal - listVal) < 1e-10 ||
                        (Double.isNaN(arrayVal) && Double.isNaN(listVal));

                System.out.printf("x = %4.1f: Массив = %10.6f, Список = %10.6f %s\n",
                        x, arrayVal, listVal, match ? "✓" : "✗");

                if (!match) allMatch = false;
            }

            if (allMatch) {
                System.out.println("\nРезультаты полностью совпадают!");
            } else {
                System.out.println("\nОбнаружены различия!");
            }

        } catch (Exception e) {
            System.out.println("Ошибка при сравнении: " + e.getMessage());
        }
    }

    // =================== ТЕСТИРОВАНИЕ ИСКЛЮЧЕНИЙ ===================

    private static void testExceptions() {
        System.out.println("Тестируем обработку исключительных ситуаций:\n");

        // Тест 1: Некорректные параметры конструктора
        System.out.println("1. Тест конструкторов (IllegalArgumentException):");
        testConstructorExceptions();

        // Тест 2: Выход за границы массива
        System.out.println("\n2. Тест выхода за границы (FunctionPointIndexOutOfBoundsException):");
        testIndexExceptions();

        // Тест 3: Нарушение порядка точек
        System.out.println("\n3. Тест нарушения порядка точек (InappropriateFunctionPointException):");
        testOrderExceptions();

        // Тест 4: Некорректное удаление
        System.out.println("\n4. Тест некорректного удаления (IllegalStateException):");
        testDeleteExceptions();
    }

    private static void testConstructorExceptions() {
        try {
            System.out.print(" Пытаемся создать функцию с leftX >= rightX... ");
            TabulatedFunction func = new ArrayTabulatedFunction(5, 5, 3);
            System.out.println("Не выброшено исключение!");
        } catch (IllegalArgumentException e) {
            System.out.println("IllegalArgumentException: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Неожиданное исключение: " + e.getClass().getName());
        }

        try {
            System.out.print(" Пытаемся создать функцию с pointsCount < 2... ");
            TabulatedFunction func = new LinkedListTabulatedFunction(0, 5, 1);
            System.out.println("Не выброшено исключение!");
        } catch (IllegalArgumentException e) {
            System.out.println("IllegalArgumentException: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Неожиданное исключение: " + e.getClass().getName());
        }

        try {
            System.out.print(" Пытаемся создать функцию с отрицательным pointsCount... ");
            TabulatedFunction func = new ArrayTabulatedFunction(0, 5, -1);
            System.out.println("Не выброшено исключение!");
        } catch (IllegalArgumentException e) {
            System.out.println("IllegalArgumentException: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Неожиданное исключение: " + e.getClass().getName());
        }
    }

    private static void testIndexExceptions() {
        try {
            TabulatedFunction func = new ArrayTabulatedFunction(0, 4, 3);

            System.out.print(" Пытаемся получить точку с индексом -1... ");
            func.getPoint(-1);
            System.out.println("Не выброшено исключение!");
        } catch (FunctionPointIndexOutOfBoundsException e) {
            System.out.println("FunctionPointIndexOutOfBoundsException");
        } catch (Exception e) {
            System.out.println("Неожиданное исключение: " + e.getClass().getName());
        }

        try {
            TabulatedFunction func = new LinkedListTabulatedFunction(0, 4, 3);

            System.out.print(" Пытаемся получить точку с индексом 10... ");
            func.getPoint(10);
            System.out.println("Не выброшено исключение!");
        } catch (FunctionPointIndexOutOfBoundsException e) {
            System.out.println("FunctionPointIndexOutOfBoundsException");
        } catch (Exception e) {
            System.out.println("Неожиданное исключение: " + e.getClass().getName());
        }

        try {
            TabulatedFunction func = new ArrayTabulatedFunction(0, 4, 3);

            System.out.print(" Пытаемся изменить точку с индексом 5... ");
            func.setPointY(5, 100);
            System.out.println("Не выброшено исключение!");
        } catch (FunctionPointIndexOutOfBoundsException e) {
            System.out.println("FunctionPointIndexOutOfBoundsException");
        } catch (Exception e) {
            System.out.println("Неожиданное исключение: " + e.getClass().getName());
        }
    }

    private static void testOrderExceptions() {
        System.out.println("\n3. Тест нарушения порядка точек (InappropriateFunctionPointException):");

        // Тест 1: Установка точки с нарушением порядка (слишком большой X)
        try {
            TabulatedFunction func = new ArrayTabulatedFunction(0, 4, 3);
            // Точки: (0,0), (2,4), (4,16)
            System.out.print(" Тест 1: Устанавливаем точку (3,y) на позицию 0... ");
            func.setPoint(0, new FunctionPoint(3, 9)); // 3 > 2 (X следующей точки)
            System.out.println("Не выброшено исключение!");
        } catch (InappropriateFunctionPointException e) {
            System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
        }

        // Тест 2: Изменение X точки с ТОЧНЫМ нарушением (равенство)
        try {
            TabulatedFunction func = new ArrayTabulatedFunction(0, 2, 3);
            // Точки: (0,0), (1,1), (2,4)
            System.out.print(" Тест 2: Меняем X точки 1 (было 1) на 0 (равно предыдущей)... ");
            func.setPointX(1, 0); // 0 <= 0 (X предыдущей точки) - ДА!
            System.out.println("Не выброшено исключение!");
        } catch (InappropriateFunctionPointException e) {
            System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
        }

        // Тест 3: Изменение X точки с ТОЧНЫМ нарушением (равенство следующей)
        try {
            TabulatedFunction func = new LinkedListTabulatedFunction(0, 2, 3);
            // Точки: (0,0), (1,1), (2,4)
            System.out.print(" Тест 3: Меняем X точки 1 (было 1) на 2 (равно следующей)... ");
            func.setPointX(1, 2); // 2 >= 2 (X следующей точки) - ДА!
            System.out.println("Не выброшено исключение!");
        } catch (InappropriateFunctionPointException e) {
            System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
        }

        // Тест 4: Добавление точки с существующим X
        try {
            TabulatedFunction func = new ArrayTabulatedFunction(0, 2, 3);
            System.out.print(" Тест 4: Добавляем точку с X=1 (точно существует)... ");
            func.addPoint(new FunctionPoint(1, 100));
            System.out.println("Не выброшено исключение!");
        } catch (InappropriateFunctionPointException e) {
            System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
        }

        // Тест 5: Добавление точки с ОЧЕНЬ близким X (проверка точности)
        try {
            TabulatedFunction func = new LinkedListTabulatedFunction(0, 2, 3);
            System.out.print(" Тест 5: Добавляем точку с X=1.000000000001... ");
            func.addPoint(new FunctionPoint(1.000000000001, 100)); // Разница 1e-12 < 1e-10
            System.out.println("Не выброшено исключение!");
        } catch (InappropriateFunctionPointException e) {
            System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
        }

        // Тест 6: Добавление точки с близким, но допустимым X
        try {
            TabulatedFunction func = new ArrayTabulatedFunction(0, 2, 3);
            System.out.print(" Тест 6: Добавляем точку с X=1.0001 (должно быть OK)... ");
            func.addPoint(new FunctionPoint(1.0001, 100)); // Разница 1e-4 > 1e-10
            System.out.println("Успешно добавлено");
        } catch (InappropriateFunctionPointException e) {
            System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    private static void testDeleteExceptions() {
        try {
            // Создаем функцию с 2 точками
            TabulatedFunction func = new ArrayTabulatedFunction(0, 1, 2);

            System.out.print(" Пытаемся удалить точку из функции с 2 точками... ");
            func.deletePoint(0);
            System.out.println("Не выброшено исключение!");
        } catch (IllegalStateException e) {
            System.out.println("IllegalStateException: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Неожиданное исключение: " + e.getClass().getName());
        }

        try {
            TabulatedFunction func = new LinkedListTabulatedFunction(0, 2, 3);

            System.out.print(" Пытаемся удалить точку с индексом -1... ");
            func.deletePoint(-1);
            System.out.println("Не выброшено исключение!");
        } catch (FunctionPointIndexOutOfBoundsException e) {
            System.out.println("FunctionPointIndexOutOfBoundsException");
        } catch (Exception e) {
            System.out.println("Неожиданное исключение: " + e.getClass().getName());
        }
    }

    // =================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ===================

    private static void printPoints(TabulatedFunction func) {
        System.out.print(" Точки: ");
        for (int i = 0; i < func.getPointsCount(); i++) {
            System.out.printf("[%.2f; %.3f] ", func.getPointX(i), func.getPointY(i));
        }
        System.out.println();
    }

    private static void testBasicOperations(TabulatedFunction func) {
        try {

            // Тест границ
            System.out.printf(" Область определения: [%.2f, %.2f]\n",
                    func.getLeftDomainBorder(), func.getRightDomainBorder());
            System.out.printf(" Количество точек: %d\n", func.getPointsCount());

            // Тест значений за границами
            System.out.println(" Значения за границами:");
            double leftOut = func.getLeftDomainBorder() - 1;
            double rightOut = func.getRightDomainBorder() + 1;
            System.out.printf("f(%.1f) = %s (должен быть NaN)\n",
                    leftOut, Double.isNaN(func.getFunctionValue(leftOut)) ? "NaN ✓" : "число ✗");
            System.out.printf("f(%.1f) = %s (должен быть NaN)\n",
                    rightOut, Double.isNaN(func.getFunctionValue(rightOut)) ? "NaN ✓" : "число ✗");

            // Тест значений в точках
            System.out.println(" Значения в точках функции:");
            for (int i = 0; i < func.getPointsCount(); i++) {
                double x = func.getPointX(i);
                double y = func.getPointY(i);
                double calcY = func.getFunctionValue(x);

                if (Math.abs(y - calcY) < 1e-10) {
                    System.out.printf("f(%.2f) = %.4f = %.4f ✓\n", x, calcY, y);
                } else {
                    System.out.printf("f(%.2f) = %.4f ≠ %.4f ✗\n", x, calcY, y);
                }
            }

        } catch (Exception e) {
            System.out.println(" Ошибка при тестировании: " + e.getMessage());
        }
    }
}