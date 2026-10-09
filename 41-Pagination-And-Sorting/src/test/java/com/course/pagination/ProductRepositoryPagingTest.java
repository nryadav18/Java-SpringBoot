package com.course.pagination;

import com.course.pagination.config.DataSeeder;
import com.course.pagination.entity.Product;
import com.course.pagination.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * Repository-level paging tests. @DataJpaTest does not run the DataSeeder (it's a @Component),
 * so we insert the same 60 sample products ourselves before each test.
 */
@DataJpaTest
class ProductRepositoryPagingTest {

    @Autowired
    ProductRepository repository;

    @BeforeEach
    void insertSampleData() {
        repository.saveAll(DataSeeder.sampleProducts());
    }

    @Test
    void pageContainsContentAndTotals() {
        Page<Product> page = repository.findAll(PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(10);
        assertThat(page.getTotalElements()).isEqualTo(60);
        assertThat(page.getTotalPages()).isEqualTo(6);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    void lastPageMayBeSmaller() {
        Page<Product> page = repository.findAll(PageRequest.of(5, 11));   // 60 rows / 11 = pages 0..5, last has 5

        assertThat(page.getContent()).hasSize(5);
        assertThat(page.isLast()).isTrue();
    }

    @Test
    void sortingByPriceDescending() {
        Page<Product> page = repository.findAll(PageRequest.of(0, 3, Sort.by("price").descending()));

        assertThat(page.getContent().get(0).getName()).isEqualTo("Scarf");
        assertThat(page.getContent().get(0).getPrice()).isEqualByComparingTo("204.99");
    }

    @Test
    void pagingADerivedQuery() {
        Page<Product> books = repository.findByCategoryIgnoreCase("books", PageRequest.of(0, 5, Sort.by("name")));

        assertThat(books.getTotalElements()).isEqualTo(12);
        assertThat(books.getContent()).extracting(Product::getCategory).containsOnly("Books");
        assertThat(books.getContent().get(0).getName()).isEqualTo("Atlas");   // alphabetical
    }

    @Test
    void sliceKnowsOnlyIfThereIsANextSlice() {
        Slice<Product> first = repository.findAllBy(PageRequest.of(0, 50));
        Slice<Product> second = repository.findAllBy(PageRequest.of(1, 50));

        assertThat(first.hasNext()).isTrue();
        assertThat(second.hasNext()).isFalse();
        assertThat(second.getContent()).hasSize(10);
    }
}
