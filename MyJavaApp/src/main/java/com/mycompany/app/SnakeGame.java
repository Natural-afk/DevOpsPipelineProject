package com.mycompany.app;

import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

class SnakeGame {
    static final int WIDTH = 20;
    static final int HEIGHT = 10;
    static char[][] board;
    static LinkedList<int[]> snake;
    static int[] food;
    static char direction;
    static boolean gameOver;

    public static void main(String[] args) {
        initGame();
        Scanner scanner = new Scanner(System.in);

        while (!gameOver) {
            drawBoard();
            System.out.println("Enter direction (W/A/S/D): ");
            char input = scanner.nextLine().toUpperCase().charAt(0);
            if (input == 'W' || input == 'A' || input == 'S' || input == 'D') {
                direction = input;
            }
            moveSnake();
            if (checkCollision()) {
                gameOver = true;
            }
            if (checkFood()) {
                growSnake();
                placeFood();
            }
        }
        drawBoard();
        System.out.println("Game Over! Your snake length was: " + snake.size());
        scanner.close();
    }

    static void initGame() {
        board = new char[HEIGHT][WIDTH];
        snake = new LinkedList<>();
        snake.add(new int[]{HEIGHT / 2, WIDTH / 2});
        direction = 'W'; // Start moving upwards
        gameOver = false;
        placeFood();
    }

    static void placeFood() {
        Random random = new Random();
        int x, y;
        do {
            x = random.nextInt(HEIGHT);
            y = random.nextInt(WIDTH);
        } while (isSnakePosition(x, y));
        food = new int[]{x, y};
    }

    static boolean isSnakePosition(int x, int y) {
        for (int[] part : snake) {
            if (part[0] == x && part[1] == y) {
                return true;
            }
        }
        return false;
    }

    static void drawBoard() {
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if (isSnakePosition(i, j)) {
                    System.out.print('O'); // Snake body
                } else if (food[0] == i && food[1] == j) {
                    System.out.print('X'); // Food
                } else {
                    System.out.print('.'); // Empty space
                }
            }
            System.out.println();
        }
    }

    static void moveSnake() {
        int[] head = snake.getFirst().clone();

        switch (direction) {
            case 'W':
                head[0]--;
                break;
            case 'S':
                head[0]++;
                break;
            case 'A':
                head[1]--;
                break;
            case 'D':
                head[1]++;
                break;
        }

        if (head[0] < 0) head[0] = HEIGHT - 1;
        else if (head[0] >= HEIGHT) head[0] = 0;
        if (head[1] < 0) head[1] = WIDTH - 1;
        else if (head[1] >= WIDTH) head[1] = 0;

        snake.addFirst(head);
        snake.removeLast();
    }

    static boolean checkCollision() {
        int[] head = snake.getFirst();
        for (int i = 1; i < snake.size(); i++) {
            if (snake.get(i)[0] == head[0] && snake.get(i)[1] == head[1]) {
                return true;
            }
        }
        return false;
    }

    static boolean checkFood() {
        int[] head = snake.getFirst();
        return head[0] == food[0] && head[1] == food[1];
    }

    static void growSnake() {
        snake.addLast(snake.getLast().clone());
    }
}
