import java.util.*;
/**
 * 612 LBE06 Dynamic Programming Example
 * Prof Kang
 * This program computes Fibonacci numbers using Iterative, Recursive & 
 * Dynamic Programming methods
 */

public class FibonacciA1 {

   public FibonacciA1() {
   
   }
   
/**
 * Compute a Fibonacci number using Iterative method
 */
   public int iterativeFib(int n) {
             if(n==0) return 0;
             if(n<=2) return 1;
      int previousValue  = 1;
      int curr = 1;
      int newValue =1;
      for(int i=3;i<=n;i++){
         newValue = previousValue +curr;
         previousValue = curr;
         curr = newValue;

      }return newValue;  
     // return 0;
   }
   
/**
 * Compute a Fibonacci number using Recursive method
 */
   public int recursiveFib(int n) {
   //#3
         if(n<=1) return n;
         return recursiveFib(n-1) + recursiveFib(n-2);
      //return 0;
   }
   
/**
 * Compute a Fibonacci number using Dynamic Programming method
 */
   public int dynamicFib(int n) {
   
      return 0;
   }

   public static void main(String[] args) {
      Scanner in = new Scanner(System.in);
      System.out.print("Enter n: ");
      int n = in.nextInt();
      
      FibonacciA1 f = new FibonacciA1();
      for(int i=0;i<=n;i++) {
         //int fib = f.iterativeFib(i);
           int fib = f.recursiveFib(i);

         System.out.println("fib(" + i + ")= " + fib);
      }
   }
}