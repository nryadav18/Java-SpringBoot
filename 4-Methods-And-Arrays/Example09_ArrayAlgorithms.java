/*
 * ============================================================
 *  Topic 4 - Example 09 : Common array algorithms (written by hand)
 * ============================================================
 *  What this file demonstrates - each algorithm in its own small method:
 *    - Find max and min
 *    - Sum and average
 *    - Reverse an array in place (two-pointer swap)
 *    - Linear search
 *    - Binary search (on a sorted array)
 *    - Bubble sort (with the "no swaps -> stop early" optimisation)
 *    - Second largest element
 *    - Count elements matching a condition
 *
 *  Why write them by hand if Arrays.sort exists? Because interviews ask for them, and because
 *  understanding them teaches you how to think in loops and indexes.
 *
 *  How to run:
 *    java Example09_ArrayAlgorithms.java
 * ============================================================
 */
import java.util.Arrays;

public class Example09_ArrayAlgorithms {

    // ---------- max ----------
    // Start with the FIRST element as the "best so far", then compare with the rest.
    // (Starting with 0 would be wrong for an array of negative numbers!)
    static int max(int[] numbers) {
        int best = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            if (numbers[i] > best) {
                best = numbers[i];
            }
        }
        return best;
    }

    static int min(int[] numbers) {
        int smallest = numbers[0];
        for (int n : numbers) {
            if (n < smallest) {
                smallest = n;
            }
        }
        return smallest;
    }

    static double average(int[] numbers) {
        int sum = 0;
        for (int n : numbers) {
            sum += n;
        }
        return (double) sum / numbers.length;   // cast to avoid integer division
    }

    // ---------- reverse in place ----------
    // Two pointers: one at the start, one at the end. Swap, move inwards, stop in the middle.
    static void reverse(int[] numbers) {
        int left = 0;
        int right = numbers.length - 1;
        while (left < right) {
            int temp = numbers[left];      // classic 3-step swap using a temporary variable
            numbers[left] = numbers[right];
            numbers[right] = temp;
            left++;
            right--;
        }
    }

    // ---------- linear search ----------
    // Check each element one by one. Works on ANY array (sorted or not). Returns -1 if not found.
    static int linearSearch(int[] numbers, int target) {
        for (int i = 0; i < numbers.length; i++) {
            if (numbers[i] == target) {
                return i;                  // found: return immediately
            }
        }
        return -1;                         // looked everywhere, not found
    }

    // ---------- binary search ----------
    // Only for SORTED arrays. Look at the middle; throw away the half that can't contain the target.
    // Like searching a word in a dictionary: open in the middle, go left or right.
    static int binarySearch(int[] sorted, int target) {
        int low = 0;
        int high = sorted.length - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;   // same as (low+high)/2 but cannot overflow
            if (sorted[mid] == target) {
                return mid;
            } else if (sorted[mid] < target) {
                low = mid + 1;                  // target is in the right half
            } else {
                high = mid - 1;                 // target is in the left half
            }
        }
        return -1;
    }

    // ---------- bubble sort ----------
    // Repeatedly walk through the array, swapping neighbours that are in the wrong order.
    // After each pass, the largest remaining element has "bubbled up" to the end.
    static void bubbleSort(int[] numbers) {
        int n = numbers.length;
        for (int pass = 0; pass < n - 1; pass++) {
            boolean swapped = false;
            for (int i = 0; i < n - 1 - pass; i++) {     // the last 'pass' elements are already in place
                if (numbers[i] > numbers[i + 1]) {
                    int temp = numbers[i];
                    numbers[i] = numbers[i + 1];
                    numbers[i + 1] = temp;
                    swapped = true;
                }
            }
            System.out.println("  after pass " + (pass + 1) + ": " + Arrays.toString(numbers));
            if (!swapped) {
                break;                                   // no swaps -> already sorted -> stop early
            }
        }
    }

    // ---------- second largest (single pass) ----------
    static int secondLargest(int[] numbers) {
        int largest = Integer.MIN_VALUE;
        int second = Integer.MIN_VALUE;
        for (int n : numbers) {
            if (n > largest) {
                second = largest;          // old largest becomes second
                largest = n;
            } else if (n > second && n != largest) {
                second = n;
            }
        }
        return second;
    }

    // ---------- count matching ----------
    static int countAbove(int[] numbers, int threshold) {
        int count = 0;
        for (int n : numbers) {
            if (n > threshold) {
                count++;
            }
        }
        return count;
    }

    public static void main(String[] args) {
        int[] sales = {420, 150, 980, 300, 760, 980, 55};
        System.out.println("Sales          : " + Arrays.toString(sales));
        System.out.println("Max            : " + max(sales));
        System.out.println("Min            : " + min(sales));
        System.out.printf("Average        : %.2f%n", average(sales));
        System.out.println("Second largest : " + secondLargest(sales));
        System.out.println("Days above 400 : " + countAbove(sales, 400));
        System.out.println("Index of 300   : " + linearSearch(sales, 300));
        System.out.println("Index of 999   : " + linearSearch(sales, 999));

        int[] digits = {1, 2, 3, 4, 5};
        reverse(digits);
        System.out.println("Reversed       : " + Arrays.toString(digits));

        int[] toSort = {64, 25, 12, 22, 11};
        System.out.println("Bubble sort of " + Arrays.toString(toSort) + ":");
        bubbleSort(toSort);

        System.out.println("Binary search 22 in sorted array -> index " + binarySearch(toSort, 22));
        System.out.println("Binary search 99 in sorted array -> " + binarySearch(toSort, 99));
    }
}

// Output:
// Sales          : [420, 150, 980, 300, 760, 980, 55]
// Max            : 980
// Min            : 55
// Average        : 520.71
// Second largest : 760
// Days above 400 : 4
// Index of 300   : 3
// Index of 999   : -1
// Reversed       : [5, 4, 3, 2, 1]
// Bubble sort of [64, 25, 12, 22, 11]:
//   after pass 1: [25, 12, 22, 11, 64]
//   after pass 2: [12, 22, 11, 25, 64]
//   after pass 3: [12, 11, 22, 25, 64]
//   after pass 4: [11, 12, 22, 25, 64]
// Binary search 22 in sorted array -> index 2
// Binary search 99 in sorted array -> -1
