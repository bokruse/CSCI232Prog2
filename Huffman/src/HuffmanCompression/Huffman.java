package HuffmanCompression;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Scanner;
import java.util.PriorityQueue;
/**
 *
 * @author Bowen Kruse
 *
 *  5/21/2019
 *
 *  CSCI 232 Programming Assignment 2
 *
 *  Provides program for compression and expanding a sentence
 *  from input.txt and test of expanded message to output.txt
 *  Simplified version of Huffman Tree example
 *  from "Algorithms, 4th Edition by Robert
 *  Sedgewick and Kevin Wayne" pages 822 - 838
 *
 */
public class Huffman {
    private static Map<Character, String> charHashMap = new HashMap<>();
    static HuffNode root;

    public static void main(String args[]) throws IOException {
        Scanner scan1 = new Scanner(new File("src/input.txt"));
        String test = scan1.nextLine();
        System.out.println("Original message: " + test + "\n");
        String s = compress(test);
        expand(s);

    }

    //Construction of Huffman Tree, queue empties into tree until last node becomes root
    private static HuffNode buildTree(Map<Character, Integer> freq) {
        PriorityQueue<HuffNode> priorityQueue = new PriorityQueue<>();
        Set<Character> keys = freq.keySet();
        for (Character c: keys) {
            HuffNode huffmanNode = new HuffNode();
                huffmanNode.data = c;
                huffmanNode.freq = freq.get(c);
                huffmanNode.left = null;
                huffmanNode.right = null;
                priorityQueue.offer(huffmanNode);
        }
        assert priorityQueue.size() > 0;
        while (priorityQueue.size() > 1) {

            HuffNode x = priorityQueue.peek();
            priorityQueue.poll();

            HuffNode z = priorityQueue.peek();
            priorityQueue.poll();

            HuffNode sum = new HuffNode();

            sum.freq = x.freq + z.freq;

            sum.data = '-';

            sum.left = x;

            sum.right = z;
            root = sum;

            priorityQueue.offer(sum);
        }
        return priorityQueue.poll();
    }

    private static void setCodes(HuffNode node, StringBuilder Prefix) {
        if (node != null) {
            if (node.left == null && node.right == null) {
                charHashMap.put(node.data, Prefix.toString());
            }
            else {
                //Left node add zero recursively
                Prefix.append('0');
                setCodes(node.left, Prefix);
                Prefix.deleteCharAt(Prefix.length() - 1);

                //Right node add 1 recursively
                Prefix.append('1');
                setCodes(node.right, Prefix);
                Prefix.deleteCharAt(Prefix.length() - 1);
            }
        }
    }

    private static String compress(String test) {
        Map<Character, Integer> frequency = new HashMap<>();
        for (int i = 0; i < test.length(); i++) {
            if (!frequency.containsKey(test.charAt(i))) {
                frequency.put(test.charAt(i), 0);
            }
            frequency.put(test.charAt(i), frequency.get(test.charAt(i)) + 1);
        }

        System.out.println("Frequency Map: " + frequency);
        //Creation of Huffman Tree
        root = buildTree(frequency);

        //Create and print Code Table
        setCodes(root, new StringBuilder());
        System.out.println("\nCode table: " +charHashMap);
        StringBuilder s = new StringBuilder();

        for (int i = 0; i < test.length(); i++) {
            char y = test.charAt(i);
            s.append(charHashMap.get(y));
        }

        return s.toString();
    }

    private static void expand(String b) {
        StringBuilder stringBuilder = new StringBuilder();
        HuffNode temp = root;

        System.out.println("\nCompressed message: " + b);

        //Convert compressed method back to integer
        for (int i = 0 ; i < b.length(); i++) {
            int j = Integer.parseInt(String.valueOf(b.charAt(i)));

            //Left nodes
            if (j == 0) {
                temp = temp.left;
                if (temp.left == null && temp.right == null) {
                    stringBuilder.append(temp.data);
                    temp = root;
                }
            }

            //Right nodes
            if (j == 1) {
                temp = temp.right;
                if (temp.left == null && temp.right == null) {
                    stringBuilder.append(temp.data);
                    temp = root;
                }
            }
        }
        try {
            PrintWriter diffLines = new PrintWriter(new FileWriter("output.txt"));
            diffLines.println("Expanded message: " + stringBuilder.toString());
            diffLines.close();
        } catch (IOException e){
            e.printStackTrace();
        }


    }
}

//Huffman Tree Node
class HuffNode implements Comparable<HuffNode> {
    int freq;
    char data;
    HuffNode left, right;

    //Compare based on frequency
    public int compareTo(HuffNode node) {
        return freq - node.freq;
    }
}