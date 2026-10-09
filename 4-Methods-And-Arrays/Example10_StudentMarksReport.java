/*
 * ============================================================
 *  Topic 4 - Example 10 : Mini-program - Student Marks Report
 * ============================================================
 *  What this file demonstrates (methods + arrays working together):
 *    - Parallel arrays: names[i] belongs to marks[i][...].
 *    - A 2D array of marks (students x subjects).
 *    - Small, single-purpose methods: total, average, grade, topper, subject average.
 *    - Passing arrays to methods and returning values.
 *    - A formatted report with printf.
 *
 *  How to run:
 *    java Example10_StudentMarksReport.java
 * ============================================================
 */
public class Example10_StudentMarksReport {

    static final String[] SUBJECTS = {"Maths", "Science", "English", "Java"};
    static final int PASS_MARK = 40;

    // Total of one student's marks (one row of the 2D array).
    static int total(int[] studentMarks) {
        int sum = 0;
        for (int m : studentMarks) {
            sum += m;
        }
        return sum;
    }

    static double average(int[] studentMarks) {
        return (double) total(studentMarks) / studentMarks.length;   // reuse total()
    }

    // A student passes only if EVERY subject is >= PASS_MARK.
    static boolean hasPassedAll(int[] studentMarks) {
        for (int m : studentMarks) {
            if (m < PASS_MARK) {
                return false;          // one failure is enough -> stop early
            }
        }
        return true;
    }

    // Grade from average, using a switch-free if/else ladder (Topic 3).
    static char grade(double average, boolean passedAll) {
        if (!passedAll) {
            return 'F';                // guard clause
        }
        if (average >= 90) {
            return 'A';
        } else if (average >= 75) {
            return 'B';
        } else if (average >= 60) {
            return 'C';
        }
        return 'D';
    }

    // Index of the student with the highest total.
    static int topperIndex(int[][] marks) {
        int best = 0;
        for (int i = 1; i < marks.length; i++) {
            if (total(marks[i]) > total(marks[best])) {
                best = i;
            }
        }
        return best;
    }

    // Average of one subject = one COLUMN of the 2D array.
    static double subjectAverage(int[][] marks, int subjectIndex) {
        int sum = 0;
        for (int[] studentMarks : marks) {
            sum += studentMarks[subjectIndex];
        }
        return (double) sum / marks.length;
    }

    static void printLine(int length) {
        for (int i = 0; i < length; i++) {
            System.out.print("-");
        }
        System.out.println();
    }

    static void printReport(String[] names, int[][] marks) {
        System.out.println("STUDENT MARKS REPORT");
        printLine(68);
        System.out.printf("%-8s", "Name");
        for (String subject : SUBJECTS) {
            System.out.printf("%8s", subject);
        }
        System.out.printf("%7s%8s%7s%n", "Total", "Avg", "Grade");
        printLine(68);

        for (int i = 0; i < names.length; i++) {
            System.out.printf("%-8s", names[i]);
            for (int m : marks[i]) {
                System.out.printf("%8d", m);
            }
            double avg = average(marks[i]);
            System.out.printf("%7d%8.2f%7c%n", total(marks[i]), avg, grade(avg, hasPassedAll(marks[i])));
        }
        printLine(68);

        System.out.printf("%-8s", "SubjAvg");
        for (int s = 0; s < SUBJECTS.length; s++) {
            System.out.printf("%8.1f", subjectAverage(marks, s));
        }
        System.out.println();
        printLine(68);

        int top = topperIndex(marks);
        System.out.println("Topper: " + names[top] + " with " + total(marks[top]) + " marks");

        int passCount = 0;
        for (int[] studentMarks : marks) {
            if (hasPassedAll(studentMarks)) {
                passCount++;
            }
        }
        System.out.println("Passed: " + passCount + " of " + names.length + " students");
    }

    public static void main(String[] args) {
        String[] names = {"Asha", "Ravi", "Meera", "John", "Farhan"};
        int[][] marks = {
                {95, 88, 92, 99},
                {72, 65, 80, 70},
                {58, 35, 66, 74},     // failed Science
                {81, 77, 69, 85},
                {45, 52, 48, 61}
        };
        printReport(names, marks);
    }
}

// Output:
// STUDENT MARKS REPORT
// --------------------------------------------------------------------
// Name       Maths Science English    Java  Total     Avg  Grade
// --------------------------------------------------------------------
// Asha          95      88      92      99    374   93.50      A
// Ravi          72      65      80      70    287   71.75      C
// Meera         58      35      66      74    233   58.25      F
// John          81      77      69      85    312   78.00      B
// Farhan        45      52      48      61    206   51.50      D
// --------------------------------------------------------------------
// SubjAvg     70.2    63.4    71.0    77.8
// --------------------------------------------------------------------
// Topper: Asha with 374 marks
// Passed: 4 of 5 students
