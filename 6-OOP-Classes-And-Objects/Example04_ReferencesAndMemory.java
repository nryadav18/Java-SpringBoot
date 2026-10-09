/*
 * Example04_ReferencesAndMemory.java
 * ------------------------------------------------------------
 * What this file shows:
 *   1. Object variables hold REFERENCES (addresses), not the object itself.
 *   2. Copying a reference (p2 = p1) does NOT copy the object -> two names, ONE object.
 *   3. Passing an object to a method: the method can change the object's fields,
 *      but cannot make the caller's variable point somewhere else (Java is pass-by-value).
 *   4. null = "points to nothing". Using it -> NullPointerException.
 *   5. Where things live: local variables on the STACK, objects on the HEAP.
 *
 * How to run (from this folder):
 *   java Example04_ReferencesAndMemory.java
 */
public class Example04_ReferencesAndMemory {

    public static void main(String[] args) {

        // ---------- 1 & 2: two variables, one object ----------
        Person p1 = new Person("Kiran", 25);  // p1 (on the stack) -> Person object (on the heap)
        Person p2 = p1;                       // copies the ADDRESS, not the object

        p2.age = 30;                          // change through p2 ...
        System.out.println("p1.age = " + p1.age);        // ... and p1 sees it: 30
        System.out.println("p1 == p2 ? " + (p1 == p2));   // true: same object

        Person p3 = new Person("Kiran", 30);  // same data, but a DIFFERENT object
        System.out.println("p1 == p3 ? " + (p1 == p3));   // false

        // ---------- 3: passing objects to methods ----------
        int number = 10;
        addTen(number);
        System.out.println("number after addTen: " + number);   // 10 (primitive: a copy was changed)

        haveBirthday(p1);
        System.out.println("p1.age after haveBirthday: " + p1.age);  // 31 (the SAME object was changed)

        tryToReplace(p1);
        System.out.println("p1.name after tryToReplace: " + p1.name); // Kiran (caller's variable unchanged)

        // ---------- 4: null ----------
        Person nobody = null;                  // the variable exists, but points to no object
        System.out.println("nobody is null? " + (nobody == null));
        try {
            System.out.println(nobody.name);   // using a null reference ...
        } catch (NullPointerException e) {     // ... crashes. (try/catch is covered in Topic 10)
            System.out.println("NullPointerException caught: can't use a null reference!");
        }

        // ---------- 5: objects inside objects ----------
        Person withPhone = new Person("Asha", 28);
        withPhone.phone = new Phone("Samsung", "98480-00000");   // a reference to another heap object
        System.out.println(withPhone.name + " uses a " + withPhone.phone.model);
    }

    static void addTen(int value) {
        value = value + 10;              // changes the local copy only
    }

    static void haveBirthday(Person person) {
        person.age++;                    // 'person' holds a copy of the SAME address -> same object
    }

    static void tryToReplace(Person person) {
        person = new Person("Someone Else", 99);   // only the local copy now points elsewhere
    }
}

class Person {
    String name;
    int age;
    Phone phone;         // a field can hold a reference to another object (default: null)

    Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
}

class Phone {
    String model;
    String number;

    Phone(String model, String number) {
        this.model = model;
        this.number = number;
    }
}

/*
Output:
p1.age = 30
p1 == p2 ? true
p1 == p3 ? false
number after addTen: 10
p1.age after haveBirthday: 31
p1.name after tryToReplace: Kiran
nobody is null? true
NullPointerException caught: can't use a null reference!
Asha uses a Samsung
*/
