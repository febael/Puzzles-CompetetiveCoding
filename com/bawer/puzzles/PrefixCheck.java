package com.bawer.puzzles;

import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Written for cmind.
 *
 * Given a list of phone numbers, determine if it is consistent in the sense that no number is the prefix of another.
 *
 * Let’s say the phone catalogue listed these numbers:
 * Emergency 911
 * Alice 97 625 999
 * Bob 91 12 54 26
 * In this case, it’s not possible to call Bob, because the central would direct your call to the emergency line as
 * soon as you had dialled the first three digits of Bob’s phone number. So this list would not be consistent.
 *
 * Input : The first line of input gives a single integer, 1≤t≤401≤t≤40, the number of test cases. Each test case
 * starts with nn, the number of phone numbers, on a separate line, 1≤n≤100001≤n≤10000. Then follows nn lines with one
 * unique phone number on each line. A phone number is a sequence of at most ten digits. Note that leading zeros in
 * phone numbers are significant, e.g., “0911” is a different phone number than “911”.
 *
 * Output : For each test case, output “YES” if the list is consistent, or “NO” otherwise.
 *
 * @author Ferit Baver Elhüseyni
 * Aug 13, 2015
 */
public class PrefixCheck {

    private final static int MAX_NO_OF_TEST_CASES = 40;
    private final static int MAX_NO_OF_PHONE_NUMBERS = 10000;
    final static int MAX_LENGTH_OF_PHONE_NUMBER = 10;

    public static volatile boolean endThreads = false;
    private static String[][] allTestData;
    private static Trie[] tries;
    private static BlockingQueue<Integer> taskQueue = new LinkedBlockingQueue<Integer>(MAX_NO_OF_TEST_CASES);

    /**
     * main method of the program. What it does by order is:
     *  Step1) Create reader thread and start.
     *  Step2) Wait until allTestData (array of arrays) is created.
     *  Step3) Create enough (1 + (allTestData.length / 4)) worker threads, start them.
     *  Step4) Wait for first workerThread to end.
     *  Step5) Print results.
     *
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        final long startOfProgram = System.currentTimeMillis();
        //STEP1:
        new Thread( () -> {
            final long start = System.currentTimeMillis();
            System.out.println("Begin: readTestData.");
            readTestData();
            System.out.println( String.format("End: readTestData in %d ms.", System.currentTimeMillis() - start) );
        }).start();
        //STEP2:
        do {
            Thread.sleep(1);
        } while (allTestData == null);
        //STEP3:
        final int threadCount = 1 + (allTestData.length / 4);
        Thread[] workerThreads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            final String threadName = String.format("workerThread%d.", i);
            (workerThreads[i] = new Thread( () -> {
                Thread.currentThread().setName(threadName);
                long start = System.currentTimeMillis();
                System.out.println( String.format("Begin: %s", threadName) );
                while (!endThreads) {
                    try {
                        doWork();
                    } catch (Exception e) {
                        break;
                    }
                }
                System.out.println( String.format("End: %s in %d ms.", threadName, System.currentTimeMillis() - start) );
            })).start();
        }
        //STEP4:
        workerThreads[0].join();
        //STEP5:
        for (Trie t : tries) {
            System.out.println(t.isFailed() ? "NO" : "YES");
        }
        System.out.println( String.format("End of the program: It took %d ms.", System.currentTimeMillis() - startOfProgram) );
    }

    /**
     * Reads from standard input
     */
    private static void readTestData() {
        try ( Scanner scanner = new Scanner(System.in) ) {
            int noOfTestCases = scanner.nextInt();
            if (noOfTestCases < 1 || noOfTestCases > MAX_NO_OF_TEST_CASES) {
                throw new RuntimeException( String.format("Invalid no of test cases! %d", noOfTestCases) );
            }
            allTestData = new String[noOfTestCases][];
            tries = new Trie[noOfTestCases];
            int i = 0;
            do {
                int noOfPhoneNumbers = scanner.nextInt();
                if (noOfPhoneNumbers < 1 || noOfPhoneNumbers > MAX_NO_OF_PHONE_NUMBERS) {
                    throw new RuntimeException( String.format("Invalid no of phone numbers in test case %d! %d", i+1, noOfPhoneNumbers) );
                }
                allTestData[i] = new String[noOfPhoneNumbers];
                tries[i] = new Trie();
                for (int j = 0; j < noOfPhoneNumbers; j++) {
                    allTestData[i][j] = scanner.next();
                }
                taskQueue.add(i);
            } while (++i < noOfTestCases);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * work method for worker threads. One wok analyses one test case.
     *
     * @throws InterruptedException
     */
    private static void doWork() throws InterruptedException {
        Integer caseIndex = taskQueue.poll(20, TimeUnit.MILLISECONDS);
        if (caseIndex == null) {
            return;
        }

        String[] testData = allTestData[caseIndex];
        Trie trie = tries[caseIndex];
        boolean failed;
        int phoneIndex = 0;
        int noOfPhones = testData.length;

        System.out.println( String.format("%s on case no %d", Thread.currentThread().getName(), caseIndex) );
        do {
            failed = trie.add( testData[phoneIndex].toCharArray() );
        } while (!failed && ++phoneIndex < noOfPhones);

        if (caseIndex+1 == allTestData.length) {
            endThreads = true;
        }
    }
}

/**
 * Data structure for helping calculation of prefix check.
 *
 * @author Ferit Baver Elhüseyni
 * Aug 13, 2015
 */
class Trie {
    private Node[] topNodes = new Node[10];
    private boolean failed = false;

    private class Node {
        private Node[] subNodes = new Node[10];
        private boolean isPhoneNumber;
        private int depth;

        public Node(int depth) {
            this(depth, false);
        }

        public Node(int depth, boolean isPhoneNumber) {
            this.depth = depth;
            this.isPhoneNumber = isPhoneNumber;
        }

        public boolean add(char[] digits) {
            if (depth == digits.length) {
                return isPhoneNumber == true ? true : !(isPhoneNumber = true);
            }
            if (isPhoneNumber) {
                return true;
            }
            int index = digits[depth] - '0';
            Node subNode = subNodes[index];
            if (subNode == null) {
                subNode = subNodes[index] = new Node(depth+1);
            }
            return subNode.add(digits);
        }
    }

    public boolean add(char[] digits) {
        if (digits.length == 0 || digits.length > PrefixCheck.MAX_LENGTH_OF_PHONE_NUMBER) {
            throw new RuntimeException( String.format("Invalid length of a phoneNumber: %s!", String.valueOf(digits)) );
        }
        int index = digits[0] - '0';
        Node topNode = topNodes[index];
        if (topNode == null) {
            topNode = topNodes[index] = new Node(1);
        }
        if (true == topNode.add(digits)) {
            return failed = true;
        }
        return false;
    }

    public boolean isFailed() {
        return failed;
    }
}