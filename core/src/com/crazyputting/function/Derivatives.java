package com.crazyputting.function;

import com.badlogic.gdx.math.Vector3;

import java.io.Serializable;
import java.util.Stack;

public class Derivatives implements Function, Serializable {

    private Node root;
    private Node xDeriv;
    private Node yDeriv;

    private String infix;
    private String[] postfix;

    public Derivatives(String inFix) {
        this(convert(inFix.split("\\s+")));
        infix = inFix;
    }

    public Derivatives(String[] postFix) {
        this.postfix = postFix;
        Stack<Node> nodeStack = new Stack();
        Node tempRoot, tempLeft, tempRight;

        for (String fix : postFix) {
            tempRoot = new Node(fix);

            if (!isOperator(fix)) {
                nodeStack.push(tempRoot);
            } else {
                tempRight = nodeStack.pop();
                tempRoot.right = tempRight;

                if (!fix.equals("ln") && !fix.equals("sin") && !fix.equals("cos")) {
                    tempLeft = nodeStack.pop();
                    tempRoot.left = tempLeft;
                }

                nodeStack.push(tempRoot);
            }
        }
        root = nodeStack.peek();

        xDeriv = xDerive(root);
        yDeriv = yDerive(root);

        boolean x = false;
        boolean y = false;
        for (String s : postFix) {
            if (s.equals("x")) x = true;
            if (s.equals("y")) y = true;
        }
        if (!x) xDeriv = new Node("0");
        if (!y) yDeriv = new Node("0");
    }

    private static boolean isOperator(String s) {
        return s.equals("(") || s.equals(")") || s.equals("+") || s.equals("-") || s.equals("*") || s.equals("/") ||
                s.equals("^") || s.equals("ln") || s.equals("sin") || s.equals("cos");
    }

    private static int getPriority(String op) {
        int priority = 0;

        if (op.equals("*") || op.equals("/")) {
            priority = 1;
        }
        if (op.equals("^")) {
            priority = 2;
        }
        if (op.equals("ln") || op.equals("sin") || op.equals("cos")) {
            priority = 3;
        }
        if (op.equals("(") || op.equals(")")) {
            priority = 4;
        }

        return priority;
    }

    private static String[] convert(String[] infix) {
        Stack<String> operatorStack = new Stack();
        Stack<String> postfixStack = new Stack();

        for (String currentSymbol : infix) {
            if (!isOperator(currentSymbol)) {
                postfixStack.push(currentSymbol);
            } else {
                while (!operatorStack.isEmpty() && (getPriority(currentSymbol) < getPriority(operatorStack.peek())) &&
                        !operatorStack.peek().equals("(")) {
                    postfixStack.push(operatorStack.pop());
                }
                operatorStack.push(currentSymbol);
            }

            if (!operatorStack.isEmpty() && operatorStack.peek().equals(")")) {
                operatorStack.pop();

                while (!operatorStack.peek().equals("(")) {
                    postfixStack.push(operatorStack.pop());
                }

                operatorStack.pop();
            }
        }

        while (!operatorStack.isEmpty()) {
            postfixStack.push(operatorStack.pop());
        }

        String[] postfix = new String[postfixStack.size()];

        for (int i = postfix.length - 1; i >= 0; i--) {
            postfix[i] = postfixStack.pop();
        }

        return postfix;
    }

    public float evaluateF(float x, float y) {
        return evaluate(root, x, y);
    }

    public float calcXDeriv(float x, float y) {
        return evaluate(xDeriv, x, y);
    }

    public float calcYDeriv(float x, float y) {
        return evaluate(yDeriv, x, y);
    }

    @Override
    public double evaluate(Vector3 pos) {
        return 0;
    }

    private float evaluate(Node root, float xValue, float yValue) {
        if (!isOperator(root.value)) {
            if (root.value.equals("x")) {
                return xValue;
            } else if (root.value.equals("y")) {
                return yValue;
            }
            return Float.parseFloat(root.value);
        }

        Float leftValue = null;
        Float rightValue = null;

        if (root.left != null)
            leftValue = evaluate(root.left, xValue, yValue);
        if (root.right != null)
            rightValue = evaluate(root.right, xValue, yValue);

        if (root.value.equals("+"))
            return leftValue + rightValue;

        if (root.value.equals("-"))
            return leftValue - rightValue;

        if (root.value.equals("*"))
            return leftValue * rightValue;

        if (root.value.equals("/")) {
            if (rightValue == 0) {
                return 0f;
            }
            return leftValue / rightValue;
        }

        if (root.value.equals("^"))
            return (float) Math.pow(leftValue, rightValue);

        if (root.value.equals("ln")) {
            if (leftValue == null) {
                if (rightValue <= 0) {
                    return 0f;
                }
                return (float) Math.log(rightValue);
            } else {
                if (leftValue <= 0) {
                    return 0f;
                }
                return (float) Math.log(leftValue);
            }
        }

        if (root.value.equals("sin"))
            if (leftValue == null) {
                return (float) Math.sin(rightValue);
            } else {
                return (float) Math.sin(leftValue);
            }

        if (leftValue == null) {
            return (float) Math.cos(rightValue);
        } else {
            return (float) Math.cos(leftValue);
        }
    }

    private Node xDerive(Node root) {
        if (!isOperator(root.value)) {
            if (root.value.equals("x")) {
                return new Node("1");
            }
            return new Node("0");
        }

        if (root.value.equals("+")) {
            Node tempNode = new Node("+");
            tempNode.left = xDerive(root.left);
            tempNode.right = xDerive(root.right);
            return tempNode;
        }

        if (root.value.equals("-")) {
            Node tempNode = new Node("-");
            tempNode.left = xDerive(root.left);
            tempNode.right = xDerive(root.right);
            return tempNode;
        }

        if (root.value.equals("*")) {
            Node tempNode = new Node("+");
            tempNode.left = new Node("*");
            tempNode.right = new Node("*");

            tempNode.left.left = root.left;
            tempNode.left.right = xDerive(root.right);
            tempNode.right.left = xDerive(root.left);
            tempNode.right.right = root.right;

            return tempNode;
        }

        if (root.value.equals("/")) {
            Node tempNode = new Node("/");
            tempNode.left = new Node("-");
            tempNode.right = new Node("^");
            tempNode.left.left = new Node("*");
            tempNode.left.right = new Node("*");
            tempNode.right.right = new Node("2");

            tempNode.left.left.left = root.right;
            tempNode.left.left.right = xDerive(root.left);
            tempNode.left.right.left = root.left;
            tempNode.left.right.right = xDerive(root.right);
            tempNode.right.left = root.right;

            return tempNode;
        }

        if (root.value.equals("^")) {
            Node tempNode = new Node("*");
            tempNode.left = new Node("^");
            tempNode.right = new Node("+");
            tempNode.right.left = new Node("/");
            tempNode.right.right = new Node("*");
            tempNode.right.left.left = new Node("*");
            tempNode.right.right.right = new Node("ln");

            tempNode.left.left = root.left;
            tempNode.left.right = root.right;
            tempNode.right.left.left.left = xDerive(root.left);
            tempNode.right.left.left.right = root.right;
            tempNode.right.left.right = root.left;
            tempNode.right.right.left = xDerive(root.right);
            tempNode.right.right.right.right = root.left;

            return tempNode;
        }

        if (root.value.equals("ln")) {
            Node tempNode = new Node("/");

            if (root.left == null) {
                tempNode.left = xDerive(root.right);
                tempNode.right = root.right;
            } else {
                tempNode.left = xDerive(root.left);
                tempNode.right = root.left;
            }

            return tempNode;
        }

        if (root.value.equals("sin")) {
            Node tempNode = new Node("*");
            tempNode.right = new Node("cos");

            if (root.left == null) {
                tempNode.left = xDerive(root.right);
                tempNode.right.right = root.right;
            } else {
                tempNode.left = xDerive(root.left);
                tempNode.right.right = root.left;
            }

            return tempNode;
        }

        Node tempNode = new Node("*");
        tempNode.right = new Node("*");
        tempNode.right.left = new Node("-1");
        tempNode.right.right = new Node("sin");

        if (root.left == null) {
            tempNode.left = xDerive(root.right);
            tempNode.right.right.right = root.right;
        } else {
            tempNode.left = xDerive(root.left);
            tempNode.right.right.right = root.left;
        }

        return tempNode;
    }

    private Node yDerive(Node root) {
        if (!isOperator(root.value)) {
            if (root.value.equals("y")) {
                return new Node("1");
            }
            return new Node("0");
        }

        if (root.value.equals("+")) {
            Node tempNode = new Node("+");
            tempNode.left = yDerive(root.left);
            tempNode.right = yDerive(root.right);
            return tempNode;
        }

        if (root.value.equals("-")) {
            Node tempNode = new Node("-");
            tempNode.left = yDerive(root.left);
            tempNode.right = yDerive(root.right);
            return tempNode;
        }

        if (root.value.equals("*")) {
            Node tempNode = new Node("+");
            tempNode.left = new Node("*");
            tempNode.right = new Node("*");

            tempNode.left.left = root.left;
            tempNode.left.right = yDerive(root.right);
            tempNode.right.left = yDerive(root.left);
            tempNode.right.right = root.right;

            return tempNode;
        }

        if (root.value.equals("/")) {
            Node tempNode = new Node("/");
            tempNode.left = new Node("-");
            tempNode.right = new Node("^");
            tempNode.left.left = new Node("*");
            tempNode.left.right = new Node("*");
            tempNode.right.right = new Node("2");

            tempNode.left.left.left = root.right;
            tempNode.left.left.right = yDerive(root.left);
            tempNode.left.right.left = root.left;
            tempNode.left.right.right = yDerive(root.right);
            tempNode.right.left = root.right;

            return tempNode;
        }

        if (root.value.equals("^")) {
            Node tempNode = new Node("*");
            tempNode.left = new Node("^");
            tempNode.right = new Node("+");
            tempNode.right.left = new Node("/");
            tempNode.right.right = new Node("*");
            tempNode.right.left.left = new Node("*");
            tempNode.right.right.right = new Node("ln");

            tempNode.left.left = root.left;
            tempNode.left.right = root.right;
            tempNode.right.left.left.left = yDerive(root.left);
            tempNode.right.left.left.right = root.right;
            tempNode.right.left.right = root.left;
            tempNode.right.right.left = yDerive(root.right);
            tempNode.right.right.right.right = root.left;

            return tempNode;
        }

        if (root.value.equals("ln")) {
            Node tempNode = new Node("/");

            if (root.left == null) {
                tempNode.left = yDerive(root.right);
                tempNode.right = root.right;
            } else {
                tempNode.left = yDerive(root.left);
                tempNode.right = root.left;
            }

            return tempNode;
        }

        if (root.value.equals("sin")) {
            Node tempNode = new Node("*");
            tempNode.right = new Node("cos");

            if (root.left == null) {
                tempNode.left = yDerive(root.right);
                tempNode.right.right = root.right;
            } else {
                tempNode.left = yDerive(root.left);
                tempNode.right.right = root.left;
            }

            return tempNode;
        }

        Node tempNode = new Node("*");
        tempNode.right = new Node("*");
        tempNode.right.left = new Node("-1");
        tempNode.right.right = new Node("sin");

        if (root.left == null) {
            tempNode.left = yDerive(root.right);
            tempNode.right.right.right = root.right;
        } else {
            tempNode.left = yDerive(root.left);
            tempNode.right.right.right = root.left;
        }

        return tempNode;
    }

    @Override
    public String toString() {
        StringBuilder post = new StringBuilder();
        for (String s : postfix) {
            post.append(s);
        }
        return infix == null ? post.toString() : infix;
    }

    private class Node implements Serializable {
        String value; //Each node has a value (either a number or an operator)
        Node left, right; //Each node has 2 children

        Node(String value) {
            this.value = value;
            left = right = null;
        }
    }
}
