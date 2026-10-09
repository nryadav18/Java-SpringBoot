import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/*
 * Example05_AnnotationValidator.java
 * ------------------------------------------------------------
 * What it shows:
 *   A mini version of "Bean Validation" (which you will use in Spring in Topic 34
 *   with @NotBlank, @Min, @Email ...). We write two annotations:
 *     @NotBlank            - text must not be null/empty/spaces
 *     @Min(value = 18)     - number must be at least 'value'
 *   and a Validator that reads them with reflection and collects error messages.
 *
 * How to run:
 *   java Example05_AnnotationValidator.java
 */
public class Example05_AnnotationValidator {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface NotBlank {
        String message() default "must not be blank";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Min {
        int value();
        String message() default "is too small";
    }

    // A "registration form" object, like a request body in a REST API
    static class RegistrationForm {
        @NotBlank(message = "Name is required")
        private final String name;

        @NotBlank
        private final String email;

        @Min(value = 18, message = "You must be at least 18")
        private final int age;

        RegistrationForm(String name, String email, int age) {
            this.name = name;
            this.email = email;
            this.age = age;
        }
    }

    // The generic validator: works for ANY object, because it uses reflection
    static class SimpleValidator {
        static List<String> validate(Object obj) throws IllegalAccessException {
            List<String> errors = new ArrayList<>();
            for (Field field : obj.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(obj);

                NotBlank notBlank = field.getAnnotation(NotBlank.class);
                if (notBlank != null && (value == null || value.toString().isBlank())) {
                    errors.add(field.getName() + ": " + notBlank.message());
                }

                Min min = field.getAnnotation(Min.class);
                if (min != null && value instanceof Integer number && number < min.value()) {
                    errors.add(field.getName() + ": " + min.message() + " (got " + number + ")");
                }
            }
            return errors;
        }
    }

    public static void main(String[] args) throws IllegalAccessException {
        RegistrationForm good = new RegistrationForm("Ravi", "ravi@example.com", 25);
        RegistrationForm bad = new RegistrationForm("  ", null, 15);

        System.out.println("Good form errors: " + SimpleValidator.validate(good));
        System.out.println("Bad form errors:");
        for (String error : SimpleValidator.validate(bad)) {
            System.out.println("  - " + error);
        }
    }
}

/*
 * Output:
 * Good form errors: []
 * Bad form errors:
 *   - name: Name is required
 *   - email: must not be blank
 *   - age: You must be at least 18 (got 15)
 *
 * (getDeclaredFields() usually returns fields in source order, but the
 *  Java spec does not promise it.)
 */
