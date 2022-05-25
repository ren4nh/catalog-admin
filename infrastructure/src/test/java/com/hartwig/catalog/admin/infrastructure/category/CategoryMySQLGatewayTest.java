package com.hartwig.catalog.admin.infrastructure.category;

import com.hartwig.catalog.admin.domain.category.Category;
import com.hartwig.catalog.admin.domain.category.CategoryID;
import com.hartwig.catalog.admin.infrastructure.MySQLGatewayTest;
import com.hartwig.catalog.admin.infrastructure.category.persistence.CategoryJpaEntity;
import com.hartwig.catalog.admin.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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

}
