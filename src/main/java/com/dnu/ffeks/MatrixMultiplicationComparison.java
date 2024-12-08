package com.dnu.ffeks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class MatrixMultiplicationComparison {

    private static final int M = 1200;
    private static final int N = 1200;
    private static final int K = 1200;

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        int[][] A = new int[M][N];
        int[][] B = new int[N][K];
        int[][] C1 = new int[M][K];
        int[][] C2 = new int[M][K];

        Random rand = new Random();
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                A[i][j] = rand.nextInt(10);
            }
        }
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < K; j++) {
                B[i][j] = rand.nextInt(10);
            }
        }

        long startTime1 = System.nanoTime();
        multiplyWithThreadPool(A, B, C1);
        long endTime1 = System.nanoTime();
        long duration1 = (endTime1 - startTime1);

        long startTime2 = System.nanoTime();
        multiplyWithVirtualThreads(A, B, C2);
        long endTime2 = System.nanoTime();
        long duration2 = (endTime2 - startTime2);

        System.out.println("Matrix A:");
        printMatrix(A);
        System.out.println("Matrix B:");
        printMatrix(B);

        System.out.println("Matrix C (ThreadPool Result):");
        printMatrix(C1);
        System.out.println("Matrix C (VirtualThreads Result):");
        printMatrix(C2);

        System.out.println("Time taken with ThreadPool: " + duration1 + " nanoseconds");
        System.out.println("Time taken with VirtualThreads: " + duration2 + " nanoseconds");
    }

    private static void multiplyWithThreadPool(int[][] A, int[][] B, int[][] C) throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        List<Future<Void>> futures = new ArrayList<>();

        for (int i = 0; i < M; i++) {
            for (int j = 0; j < K; j++) {
                final int row = i;
                final int col = j;
                Future<Void> future = executorService.submit(() -> {
                    C[row][col] = 0;
                    for (int t = 0; t < N; t++) {
                        C[row][col] += A[row][t] * B[t][col];
                    }
                    return null;
                });
                futures.add(future);
            }
        }

        for (Future<Void> future : futures) {
            future.get();
        }

        executorService.shutdown();
    }

    private static void multiplyWithVirtualThreads(int[][] A, int[][] B, int[][] C) throws InterruptedException {
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < K; j++) {
                final int row = i;
                final int col = j;
                Thread thread = Thread.ofVirtual().start(() -> {
                    C[row][col] = 0;
                    for (int t = 0; t < N; t++) {
                        C[row][col] += A[row][t] * B[t][col];
                    }
                });
                threads.add(thread);
            }
        }

        for (Thread thread : threads) {
            thread.join();
        }
    }

    private static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            for (int val : row) {
                System.out.printf("%3d ", val);
            }
            System.out.println();
        }
    }
}