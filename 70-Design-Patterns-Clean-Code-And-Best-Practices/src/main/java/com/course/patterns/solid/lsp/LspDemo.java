package com.course.patterns.solid.lsp;

import com.course.patterns.demo.PatternDemo;
import java.util.List;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/*
 * L - LISKOV SUBSTITUTION PRINCIPLE
 * "If code works with a parent type, it must keep working with ANY child type."
 * A subclass must not surprise the caller (throw, do nothing, need special checks).
 *
 * BAD : Penguin extends Bird but cannot fly -> throws. Code written for Bird breaks.
 * GOOD: only birds that CAN fly implement FlyingBird. The type system now tells the truth.
 *
 * Expected output:
 *   [bad ] Sparrow is flying
 *   [bad ] CRASH: Penguins cannot fly!
 *   [good] Sparrow is flying
 *   [good] Eagle is flying
 *   [good] Penguin is swimming (it is a Bird, but never asked to fly)
 */
@Component
@Order(3)
public class LspDemo implements PatternDemo {

    // ---------------------------------------------------------- BAD
    static class BadBird {
        String fly() {
            return getClass().getSimpleName().replace("Bad", "") + " is flying";
        }
    }

    static class BadSparrow extends BadBird {
    }

    static class BadPenguin extends BadBird {
        @Override
        String fly() {
            throw new UnsupportedOperationException("Penguins cannot fly!"); // surprise!
        }
    }

    // ---------------------------------------------------------- GOOD
    interface Bird {
        String name();
    }

    interface FlyingBird extends Bird {
        default String fly() {
            return name() + " is flying";
        }
    }

    record Sparrow() implements FlyingBird {
        public String name() { return "Sparrow"; }
    }

    record Eagle() implements FlyingBird {
        public String name() { return "Eagle"; }
    }

    record Penguin() implements Bird {
        public String name() { return "Penguin"; }

        String swim() { return name() + " is swimming (it is a Bird, but never asked to fly)"; }
    }

    @Override
    public String title() {
        return "L - Liskov Substitution Principle";
    }

    @Override
    public void run() {
        for (BadBird bird : List.of(new BadSparrow(), new BadPenguin())) {
            try {
                System.out.println("[bad ] " + bird.fly());
            } catch (UnsupportedOperationException e) {
                System.out.println("[bad ] CRASH: " + e.getMessage());
            }
        }

        // This method can ONLY receive birds that fly - the compiler guarantees it.
        List<FlyingBird> flyers = List.of(new Sparrow(), new Eagle());
        flyers.forEach(b -> System.out.println("[good] " + b.fly()));
        System.out.println("[good] " + new Penguin().swim());
    }
}
