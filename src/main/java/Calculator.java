import java.util.Scanner;

public class Calculator {
    public static String readExpr() {
        Scanner scanner = new Scanner(System.in);
        scanner.useDelimiter("\\n");

        System.out.print("Введите выражение:\n");
        String input = scanner.nextLine();

        return input;
    }
    public static int findElem(String str, String[] arr, int limit) {
        int ind = limit;

        while(! (arr[ind].equals(";") || arr[ind].equals(str)) ) ind++;
        if (arr[ind] == ";") ind = -1;

        return ind;
    }

    public static int findElem(String str, String[] arr) {
        int ind = findElem(str, arr, 0);
        return ind;
    }

    public static int findElemRight(String str, String[] arr, int limit) {
        int ind = limit;

        while(! (ind == -1 || arr[ind].equals(str)) ) ind--;

        return ind;
    }

    public static int findElemRight(String str, String[] arr) {
        int ind = findElemRight(str, arr, arr.length - 1);
        return ind;
    }
    public static void removeElem(int elem, String[] arr) {
        for (int i = elem; i + 1 < arr.length; i++) arr[i] = arr[i + 1];
        arr[arr.length - 1] = "";
    }

    public static String operation(String x, String action, String y) throws CustomException {
        if ("+-*/".indexOf(x) != -1 || "+-*/".indexOf(y) != -1)
            throw new CustomException("Пропущено число (operation)");

        try {
			// строки переводятся в числа
            double a = Double.parseDouble(x);
            double b = Double.parseDouble(y);

            String res;
			// определяется знак и вычисляется операция
            char op = action.charAt(0);
            switch(op) {
                case '+':
                    res = Double.toString(a + b);
                    break;
                case '-':
                    res = Double.toString(a - b);
                    break;
                case '*':
                    res = Double.toString(a * b);
                    break;
                case '/':
                    if (b != 0)
                        res = Double.toString(a / b); /* Вещественные числа не делятся (делятся как целые) */
                    else
                        res = "Деление на ноль (operation)";
                    break;
                default:
                    res = "Неправильный оператор (operation)";
                    break;
            }

            return res;

        } catch (NumberFormatException e) {
			// если возникли ошибки, метод вместо результата вернет сообщение
            return "Неверно введено число (operation)";
        }
    }

    public static String[] partString(String input) throws CustomException {
		// если строка начинается с минуса,в качестве первого символа приписывается ноль
        char firstSym = input.charAt(0);
        if (firstSym == '-')
            input = "0" + input;
		// длина массива, который вернет метод, совпадает с количеством символов в строке
        int inputLen =  input.length();
        String[] res = new String[inputLen + 1];
        int index = 0;

        for (int i = 0; i  <= inputLen; i++)
            res[i] = "";
		// количество левых и правых скобок
        int left = 0, right = 0;

        String number = ""; // строка, с помощью которой будем сохранять числа
        for (int i = 0; i < inputLen; i++) {
			// получаем очередной символ строки
            char c = input.charAt(i);
			// если символ - цифра или точка, этот символ приписывается к числу
            if (Character.isDigit(c) || c == '.')
                number += c;
            else {
				// отбрасываем неизвестные символы
                if ("+-*/()".indexOf(c) == -1) throw new CustomException("Неизвестный символ (partString)");
				
                if (c == '(') left++;
                if (c == ')') right++;

				// если строка с числом не пустая, в массив добавляется новый элемент
				// т.е. числа в массив добавляются после того, как встречаются прочие знаки
                if (!number.equals("")) {
                    res[index] = number;
                    index++;

                    number = "";
                }
				// добавляем в массив символ (не число)
                res[index] = "" + c;
                index++;
            }
        }
		// отбрасываем случай с неправильно расставленными скобками
        if (left != right) throw new CustomException("Неправильно расставлены скобки (partString)");
		// добавим в массив последнее число, если оно осталось
        if (!number.equals("")) {
            res[index] = number;
            index++;
        }
		// с помощью символа ; обозначим конец выражения,
		// чтобы не рассматривать пустые элементы массива,
		// которые могли остаться в конце
        res[index] = ";";

		// чтобы программа не выдавала ошибку, встретив отрицательное число в скобках,
		// минусы перед такими числами объединяются с самим числом в одну строку
		// т.е. строка вида "(-6)" будет преобразована в три строки "(", "-6", ")"
        int prev = 0, cur = 1;
        while(!res[cur].equals(";")) {
            if (res[prev].equals("(") && res[cur].equals("-")) {
                res[cur + 1] = "-" + res[cur + 1];
                removeElem(cur, res);
            }

            prev++; cur++;
        }
		// возвращается массив
        return res;
    }

    public static void replaceStrings(int ind, String str, String[] arr) {

        arr[ind] = str;
        ind++;

        while(arr[ind + 2] != ";") {
            arr[ind] = arr[ind + 2];
            ind++;
        }

        arr[ind] = ";";
        arr[ind + 1] = "";
        arr[ind + 2] = "";
    }
    public static void solveExpression(int start, int end, String[] arr) throws CustomException {
        int i = start++;
        while(i != end) {
			// ищется индекс первого знака умножения или деления
            while(i < end && ! (arr[i].equals("*") || arr[i].equals("/")) ) i++;
            if (i < end) {
				// вычисляется операция "a*b" или "a+b"
                String op = operation(arr[i - 1], arr[i], arr[i + 1]);
				// отбрасываются ошибки (пропущенные числа)
                char first = op.charAt(0);
                if (!Character.isDigit(first) && first != '-')
                    throw new CustomException(op);
				// строка "a*b" заменяется на результат вычислений
                replaceStrings(i - 1, op, arr);
                end -= 2;
            }
        }
		// аналогичным образом обрабатываются плюсы и минусы
        i = start++;
        while(i != end) {
            while(i < end && ! (arr[i].equals("+") || arr[i].equals("-")) ) i++;
            if (i < end) {
                String op = operation(arr[i - 1], arr[i], arr[i + 1]);

                char first = op.charAt(0);
                if (!Character.isDigit(first) && first != '-')
                    throw new CustomException(op);

                replaceStrings(i - 1, op, arr);
                end -= 2;
            }
        }
    }

    public static void openBrackets(int left, int right, String[] arr) throws CustomException {
        solveExpression(left, right, arr);
        replaceStrings(left, arr[left + 1], arr);
    }
    public static String calculate(String input) throws CustomException {
        String[] elements = partString(input);
        int right = findElem(")", elements);
        while(right != -1) {

            int left = findElemRight("(", elements, right);
            if (left == -1 || (right - left) == 1)
                throw new CustomException("Неправильно расставлены скобки (openBrackets)");
            openBrackets(left, right, elements);
            right = findElem(")", elements);
        }

        int end = 0;
        while(!elements[end].equals(";")) end++;
        solveExpression(0, end, elements);

        return elements[0];
    }

    public static void main(String[] args) {
        /* ТЕСТЫ */
        /*String result;
        // ((1+2)-5*(3+4))-6
        String test1 = "((1+2)-5*(3+4))-6";
        String test2 = "((1+2)%-5*(3+4))-6";
        String test3 = "((1+2.)-5*(3+4))-6";
        String test4 = "((1+2.0)-5*(3+4))-6";
        String test5 = "((1+2.0.1)-5*(3+4))-6";
        String test6 = "((1+2)-5*(3+4)))-6";
        String test7 = "(((1+2)-5*(3+4))-6";
        String test8 = ")1+2(-5*(3+4)-6";
        String test9 = "(()-5*(3+4))-6";
        String test10 = "((1++2)-5*(3+4))-6";
        String test11 = "-((1+2)-5*(3+4))-6";
        String test12 = "(((1+2)-5*(3+4))-6)";
        String test13 = "((1+2)-5*(3+4))+(-6)";

        try {
            System.out.print("Обычная формула\n" + test1 + "\n");
            result = calculate(test1);
            System.out.println(result);
        } catch (CustomException e) {
            System.out.println(e);
        }

        try {
            System.out.print("Лишний символ\n" + test2 + "\n");
            result = calculate(test2);
            System.out.println(result);
        } catch (CustomException e) {
            System.out.println(e);
        }

        try {
            System.out.print("Точка\n" + test3 + "\n");
            result = calculate(test3);
            System.out.println(result);
        } catch (CustomException e) {
            System.out.println(e);
        }

        try {
            System.out.print("Вещественное число\n" + test4 + "\n");
            result = calculate(test4);
            System.out.println(result);
        } catch (CustomException e) {
            System.out.println(e);
        }

        try {
            System.out.print("Две точки\n" + test5 + "\n");
            result = calculate(test5);
            System.out.println(result);
        } catch (CustomException e) {
            System.out.println(e);
        }

        try {
            System.out.print("Лишняя правая скобка\n" + test6 + "\n");
            result = calculate(test6);
            System.out.println(result);
        } catch (CustomException e) {
            System.out.println(e);
        }

        try {
            System.out.print("Лишняя левая скобка\n" + test7 + "\n");
            result = calculate(test7);
            System.out.println(result);
        } catch (CustomException e) {
            System.out.println(e);
        }

        try {
            System.out.print("Скобки\n" + test8 + "\n");
            result = calculate(test8);
            System.out.println(result);
        } catch (CustomException e) {
            System.out.println(e);
        }

        try {
            System.out.print("Пустые скобки\n" + test9 + "\n");
            result = calculate(test9);
            System.out.println(result);
        } catch (CustomException e) {
            System.out.println(e);
        }

        try {
            System.out.print("Два оператора подряд\n" + test10 + "\n");
            result = calculate(test10);
            System.out.println(result);
        } catch (CustomException e) {
            System.out.println(e);
        }

        try {
            System.out.print("Минус перед скобками\n" + test11 + "\n");
            result = calculate(test11);
            System.out.println(result);
        } catch (CustomException e) {
            System.out.println(e);
        }

        try {
            System.out.print("Скобки вокруг формулы\n" + test12 + "\n");
            result = calculate(test12);
            System.out.println(result);
        } catch (CustomException e) {
            System.out.println(e);
        }

        try {
            System.out.print("Отрицательное число в скобках\n" + test13 + "\n");
            result = calculate(test13);
            System.out.println(result);
        } catch (CustomException e) {
            System.out.println(e);
        }*/


        String input = readExpr();
        try {
            String res = calculate(input);
            System.out.println(res);
        } catch (CustomException e) {
            System.out.println(e);
        }
    }
}
