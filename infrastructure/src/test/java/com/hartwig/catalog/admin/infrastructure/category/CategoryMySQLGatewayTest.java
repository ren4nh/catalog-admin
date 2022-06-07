package com.hartwig.catalog.admin.infrastructure.category;

import com.hartwig.catalog.admin.domain.category.Category;
import com.hartwig.catalog.admin.domain.category.CategoryID;
import com.hartwig.catalog.admin.MySQLGatewayTest;
import com.hartwig.catalog.admin.domain.pagination.SearchQuery;
import com.hartwig.catalog.admin.infrastructure.category.persistence.CategoryJpaEntity;
import com.hartwig.catalog.admin.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@MySQLGatewayTest
public class CategoryMySQLGatewayTest {

    @Autowired
    private CategoryMySQLGateway categoryGateway;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void givenAValidCategory_whenCallsCreate_shouldReturnANewCategory() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        assertEquals(0, categoryRepository.count());

        final var actualCategory = categoryGateway.create(aCategory);

        assertEquals(1, categoryRepository.count());

        assertAll(
                () -> assertEquals(aCategory.getId(), actualCategory.getId()),
                () -> assertEquals(expectedName, actualCategory.getName()),
                () -> assertEquals(expectedDescription, actualCategory.getDescription()),
                () -> assertEquals(expectedIsActive, actualCategory.isActive()),
                () -> assertEquals(aCategory.getCreatedAt(), actualCategory.getCreatedAt()),
                () -> assertEquals(aCategory.getUpdatedAt(), actualCategory.getUpdatedAt()),
                () -> assertEquals(aCategory.getDeletedAt(), actualCategory.getDeletedAt()),
                () -> assertNull(actualCategory.getDeletedAt())
        );

        final var actualEntity = categoryRepository.findById(aCategory.getId().getValue()).get();

        assertAll(
                () -> assertEquals(aCategory.getId().getValue(), actualEntity.getId()),
                () -> assertEquals(expectedName, actualEntity.getName()),
                () -> assertEquals(expectedDescription, actualEntity.getDescription()),
                () -> assertEquals(expectedIsActive, actualEntity.isActive()),
                () -> assertEquals(aCategory.getCreatedAt(), actualEntity.getCreatedAt()),
                () -> assertEquals(aCategory.getUpdatedAt(), actualEntity.getUpdatedAt()),
                () -> assertEquals(aCategory.getDeletedAt(), actualEntity.getDeletedAt()),
                () -> assertNull(actualEntity.getDeletedAt())
        );

    }

    @Test
    public void givenAValidCategory_whenCallsUpdate_shouldReturnCategoryUpdated() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aCategory = Category.newCategory("Film", null, expectedIsActive);

        assertEquals(0, categoryRepository.count());

        categoryRepository.saveAndFlush(CategoryJpaEntity.from(aCategory));

        assertEquals(1, categoryRepository.count());

        final var actualInvalidEntity = categoryRepository.findById(aCategory.getId().getValue()).get();

        assertAll(
                () -> assertEquals("Film", actualInvalidEntity.getName()),
                () -> assertNull(actualInvalidEntity.getDescription()),
                () -> assertEquals(expectedIsActive, actualInvalidEntity.isActive())
        );

        final var aUpdatedCategory = aCategory.clone().update(expectedName, expectedDescription, expectedIsActive);

        final var actualCategory = categoryGateway.update(aUpdatedCategory);

        assertEquals(1, categoryRepository.count());

        assertAll(
                () -> assertEquals(aCategory.getId(), actualCategory.getId()),
                () -> assertEquals(expectedName, actualCategory.getName()),
                () -> assertEquals(expectedDescription, actualCategory.getDescription()),
                () -> assertEquals(expectedIsActive, actualCategory.isActive()),
                () -> assertEquals(aCategory.getCreatedAt(), actualCategory.getCreatedAt()),
                () -> assertTrue(aCategory.getUpdatedAt().isBefore(actualCategory.getUpdatedAt())),
                () -> assertEquals(aCategory.getDeletedAt(), actualCategory.getDeletedAt()),
                () -> assertNull(actualCategory.getDeletedAt())
        );

        final var actualEntity = categoryRepository.findById(aCategory.getId().getValue()).get();

        assertAll(
                () -> assertEquals(aCategory.getId().getValue(), actualEntity.getId()),
                () -> assertEquals(expectedName, actualEntity.getName()),
                () -> assertEquals(expectedDescription, actualEntity.getDescription()),
                () -> assertEquals(expectedIsActive, actualEntity.isActive()),
                () -> assertEquals(aCategory.getCreatedAt(), actualEntity.getCreatedAt()),
                () -> assertTrue(aCategory.getUpdatedAt().isBefore(actualCategory.getUpdatedAt())),
                () -> assertEquals(aCategory.getDeletedAt(), actualEntity.getDeletedAt()),
                () -> assertNull(actualEntity.getDeletedAt())
        );

    }

    @Test
    public void givenAPrePersistedCategoryAndValidCategoryId_whenTryDeleteIt_shouldDeleteCategory() {
        final var aCategory = Category.newCategory("Filmes", null, true);

        assertEquals(0, categoryRepository.count());

        categoryRepository.saveAndFlush(CategoryJpaEntity.from(aCategory));

        assertEquals(1, categoryRepository.count());

        categoryGateway.deleteById(aCategory.getId());

        assertEquals(0, categoryRepository.count());

    }

    @Test
    public void givenAPrePersistedCategoryAndInvalidCategoryId_whenTryDeleteIt_shouldDeleteCategory() {
        assertEquals(0, categoryRepository.count());

        categoryGateway.deleteById(CategoryID.from("invalid"));

        assertEquals(0, categoryRepository.count());
    }

    @Test
    public void givenAPrePersistedCategoryAndValidCategoryId_whenCallsFindById_shouldReturnsCategory() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        assertEquals(0, categoryRepository.count());

        categoryRepository.saveAndFlush(CategoryJpaEntity.from(aCategory));

        assertEquals(1, categoryRepository.count());

        final var actualCategory = categoryGateway.findById(aCategory.getId()).get();

        assertAll(
                () -> assertEquals(aCategory.getId(), actualCategory.getId()),
                () -> assertEquals(expectedName, actualCategory.getName()),
                () -> assertEquals(expectedDescription, actualCategory.getDescription()),
                () -> assertEquals(expectedIsActive, actualCategory.isActive()),
                () -> assertEquals(aCategory.getCreatedAt(), actualCategory.getCreatedAt()),
                () -> assertEquals(aCategory.getUpdatedAt(), actualCategory.getUpdatedAt()),
                () -> assertEquals(aCategory.getDeletedAt(), actualCategory.getDeletedAt()),
                () -> assertNull(actualCategory.getDeletedAt())
        );

    }

    @Test
    public void givenAValidCategoryIdNotStored_whenCallsFindById_shouldReturnsEmpty() {

        assertEquals(0, categoryRepository.count());

        assertTrue(categoryGateway.findById(CategoryID.from("empty")).isEmpty());

    }

    @Test
    public void givenPrePersistedCategories_whenCallsFindAll_shouldReturnPaginated() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 3;

        final var filmes = Category.newCategory("Filmes", null, true);
        final var series = Category.newCategory("Séries", null, true);
        final var documentarios = Category.newCategory("Documentários", null, true);

        assertEquals(0, categoryRepository.count());

        categoryRepository.saveAll(List.of(
                CategoryJpaEntity.from(filmes),
                CategoryJpaEntity.from(series),
                CategoryJpaEntity.from(documentarios)
        ));

        assertEquals(3, categoryRepository.count());

        final var query = new SearchQuery(0,1,"","name","asc");
        final var actualResult = categoryGateway.findAll(query);

        assertAll(
                () -> assertEquals(expectedPage, actualResult.currentPage()),
                () -> assertEquals(expectedPerPage, actualResult.perPage()),
                () -> assertEquals(expectedTotal, actualResult.total()),
                () -> assertEquals(expectedPerPage, actualResult.items().size()),
                () -> assertEquals(documentarios.getId(), actualResult.items().get(0).getId())
        );

    }

    @Test
    public void givenEmptyCategoriesTable_whenCallsFindAll_shouldReturnEmptyPage() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 0;

        assertEquals(0, categoryRepository.count());

        final var query = new SearchQuery(0,1,"","name","asc");
        final var actualResult = categoryGateway.findAll(query);

        assertAll(
                () -> assertEquals(expectedPage, actualResult.currentPage()),
                () -> assertEquals(expectedPerPage, actualResult.perPage()),
                () -> assertEquals(expectedTotal, actualResult.total()),
                () -> assertEquals(0, actualResult.items().size())
        );
    }

    @Test
    public void givenFollowPagination_whenCallsFindAllWithPage1_shouldReturnPaginated() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 3;

        final var filmes = Category.newCategory("Filmes", null, true);
        final var series = Category.newCategory("Séries", null, true);
        final var documentarios = Category.newCategory("Documentários", null, true);

        assertEquals(0, categoryRepository.count());

        categoryRepository.saveAll(List.of(
                CategoryJpaEntity.from(filmes),
                CategoryJpaEntity.from(series),
                CategoryJpaEntity.from(documentarios)
        ));

        assertEquals(3, categoryRepository.count());

        final var page0 = new SearchQuery(0,1,"","name","asc");
        final var page0Result = categoryGateway.findAll(page0);

        assertAll(
                () -> assertEquals(expectedPage, page0Result.currentPage()),
                () -> assertEquals(expectedPerPage, page0Result.perPage()),
                () -> assertEquals(expectedTotal, page0Result.total()),
                () -> assertEquals(expectedPerPage, page0Result.items().size()),
                () -> assertEquals(documentarios.getId(), page0Result.items().get(0).getId())
        );

        final var page1 = new SearchQuery(1,1,"","name","asc");
        final var page1Result = categoryGateway.findAll(page1);
        final var expectedPage1 = 1;

        assertAll(
                () -> assertEquals(expectedPage1, page1Result.currentPage()),
                () -> assertEquals(expectedPerPage, page1Result.perPage()),
                () -> assertEquals(expectedTotal, page1Result.total()),
                () -> assertEquals(expectedPerPage, page1Result.items().size()),
                () -> assertEquals(filmes.getId(), page1Result.items().get(0).getId())
        );

        final var page2 = new SearchQuery(2,1,"","name","asc");
        final var page2Result = categoryGateway.findAll(page2);
        final var expectedPage2 = 2;

        assertAll(
                () -> assertEquals(expectedPage2, page2Result.currentPage()),
                () -> assertEquals(expectedPerPage, page2Result.perPage()),
                () -> assertEquals(expectedTotal, page2Result.total()),
                () -> assertEquals(expectedPerPage, page2Result.items().size()),
                () -> assertEquals(series.getId(), page2Result.items().get(0).getId())
        );
    }

    @Test
    public void givenPrePersistedCategoriesAndDocAsTerms_whenCallsFindAllAndTermsMatchsCategoryName_shouldReturnPaginated() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 1;

        final var filmes = Category.newCategory("Filmes", null, true);
        final var series = Category.newCategory("Séries", null, true);
        final var documentarios = Category.newCategory("Documentários", null, true);

        assertEquals(0, categoryRepository.count());

        categoryRepository.saveAll(List.of(
                CategoryJpaEntity.from(filmes),
                CategoryJpaEntity.from(series),
                CategoryJpaEntity.from(documentarios)
        ));

        assertEquals(3, categoryRepository.count());

        final var query = new SearchQuery(0,1,"doc","name","asc");
        final var actualResult = categoryGateway.findAll(query);

        assertAll(
                () -> assertEquals(expectedPage, actualResult.currentPage()),
                () -> assertEquals(expectedPerPage, actualResult.perPage()),
                () -> assertEquals(expectedTotal, actualResult.total()),
                () -> assertEquals(expectedPerPage, actualResult.items().size()),
                () -> assertEquals(documentarios.getId(), actualResult.items().get(0).getId())
        );

    }

    @Test
    public void givenPrePersistedCategoriesAndMaisAssistidaAsTerms_whenCallsFindAllAndTermsMatchsCategoryDescription_shouldReturnPaginated() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 1;

        final var filmes = Category.newCategory("Filmes", "A categoria mais assistida", true);
        final var series = Category.newCategory("Séries", "Uma categoria assistida", true);
        final var documentarios = Category.newCategory("Documentários", "A categoria menos assistida", true);

        assertEquals(0, categoryRepository.count());

        categoryRepository.saveAll(List.of(
                CategoryJpaEntity.from(filmes),
                CategoryJpaEntity.from(series),
                CategoryJpaEntity.from(documentarios)
        ));

        assertEquals(3, categoryRepository.count());

        final var query = new SearchQuery(0,1,"MAIS assistida","name","asc");
        final var actualResult = categoryGateway.findAll(query);

        assertAll(
                () -> assertEquals(expectedPage, actualResult.currentPage()),
                () -> assertEquals(expectedPerPage, actualResult.perPage()),
                () -> assertEquals(expectedTotal, actualResult.total()),
                () -> assertEquals(expectedPerPage, actualResult.items().size()),
                () -> assertEquals(filmes.getId(), actualResult.items().get(0).getId())
        );

    }

}
