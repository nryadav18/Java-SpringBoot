package com.course.jpabasics;

import com.course.jpabasics.entity.Product;
import com.course.jpabasics.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * ProductRepositoryTest - proves that the repository and the entity mapping really work.
 * -------------------------------------------------------------------------------------
 * @DataJpaTest starts ONLY the JPA part of Spring (entities, repositories, H2, EntityManager),
 * not the web layer - so it is fast. Every test runs in a transaction that is ROLLED BACK
 * at the end, so tests never see each other's data.
 *
 * Run:  mvn test
 */
@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    ProductRepository repository;

    @Autowired
    EntityManager entityManager;   // the low-level JPA API that JpaRepository uses internally

    private Product newMouse() {
        return new Product("Mouse", "Test mouse", "T-MOU", new BigDecimal("9.99"), 5);
    }

    @Test
    @DisplayName("save() inserts a row, generates an id and fills createdAt")
    void saveGeneratesIdAndTimestamp() {
        Product saved = repository.save(newMouse());

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("findById() returns Optional - empty when the id does not exist")
    void findByIdReturnsOptional() {
        Product saved = repository.save(newMouse());

        assertThat(repository.findById(saved.getId())).isPresent();
        assertThat(repository.findById(999L)).isEmpty();
        assertThat(repository.existsById(saved.getId())).isTrue();
    }

    @Test
    @DisplayName("derived query findBySku() works without writing any SQL")
    void derivedQueryBySku() {
        repository.save(newMouse());

        assertThat(repository.findBySku("T-MOU")).get()
                .extracting(Product::getName).isEqualTo("Mouse");
        assertThat(repository.existsBySku("NOPE")).isFalse();
    }

    @Test
    @DisplayName("deleteById() removes the row")
    void deleteRemovesRow() {
        Product saved = repository.save(newMouse());

        repository.deleteById(saved.getId());

        assertThat(repository.existsById(saved.getId())).isFalse();
    }

    @Test
    @DisplayName("Entity lifecycle: transient -> managed -> detached -> managed copy -> removed")
    void entityLifecycleStates() {
        Product product = newMouse();                    // TRANSIENT: plain Java object
        assertThat(entityManager.contains(product)).isFalse();

        entityManager.persist(product);                  // MANAGED: tracked by the persistence context
        assertThat(entityManager.contains(product)).isTrue();

        entityManager.flush();                           // push pending INSERT to the database
        entityManager.detach(product);                   // DETACHED: still has an id, but no longer tracked
        assertThat(entityManager.contains(product)).isFalse();

        product.setPrice(new BigDecimal("12.50"));       // change is NOT saved automatically (detached!)
        Product managedCopy = entityManager.merge(product); // merge copies the state into a MANAGED instance
        assertThat(entityManager.contains(managedCopy)).isTrue();

        entityManager.remove(managedCopy);               // REMOVED: will be deleted at flush/commit
        entityManager.flush();
        assertThat(repository.findById(product.getId())).isEmpty();
    }

    @Test
    @DisplayName("Dirty checking: changing a managed entity is saved without calling save()")
    void dirtyCheckingUpdatesAutomatically() {
        Product saved = repository.save(newMouse());
        entityManager.flush();

        saved.setStock(99);                              // just a setter - no save() call!
        entityManager.flush();                           // Hibernate notices the change and runs UPDATE
        entityManager.clear();                           // forget everything, force a fresh SELECT

        Product reloaded = repository.findById(saved.getId()).orElseThrow();
        assertThat(reloaded.getStock()).isEqualTo(99);
    }
}
