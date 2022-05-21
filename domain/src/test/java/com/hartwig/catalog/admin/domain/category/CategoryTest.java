package com.hartwig.catalog.admin.domain.category;

import com.hartwig.catalog.admin.domain.exceptions.DomainException;
import com.hartwig.catalog.admin.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryTest {

    @Test
    public void givenAValidParams_whenCallNewCategory_thenInstantiateACategory() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var actualCategory =
                Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        assertAll(
                () -> assertNotNull(actualCategory),
                () -> assertNotNull(actualCategory.getId()),
                () -> assertEquals(expectedName, actualCategory.getName()),
                () -> assertEquals(expectedDescription, actualCategory.getDescription()),
                () -> assertEquals(expectedIsActive, actualCategory.isActive()),
                () -> assertNotNull(actualCategory.getCreatedAt()),
                () -> assertNotNull(actualCategory.getUpdatedAt()),
                () -> assertNull(actualCategory.getDeletedAt())
        );

    }

    @Test
    public void givenAnInvalidNullName_whenCallNewCategoryAndValidate_thenShouldReceiveError() {
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var actualCategory =
                Category.newCategory(null, expectedDescription, expectedIsActive);

        final var actualException =
                assertThrows(DomainException.class, () -> actualCategory.validate(new ThrowsValidationHandler()));

        assertAll(
                () -> assertEquals(expectedErrorCount, actualException.getErrors().size()),
                () -> assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message())
        );
    }

    @Test
    public void givenAnInvalidEmptyName_whenCallNewCategoryAndValidate_thenShouldReceiveError() {
        final var expectedName = "  ";
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be empty";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var actualCategory =
                Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        final var actualException =
                assertThrows(DomainException.class, () -> actualCategory.validate(new ThrowsValidationHandler()));

        assertAll(
                () -> assertEquals(expectedErrorCount, actualException.getErrors().size()),
                () -> assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message())
        );
    }

    @Test
    public void givenAnInvalidNameLengthLessThan3_whenCallNewCategoryAndValidate_thenShouldReceiveError() {
        final var expectedName = "Fi ";
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' must be between 3 and 255 characters";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var actualCategory =
                Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        final var actualException =
                assertThrows(DomainException.class, () -> actualCategory.validate(new ThrowsValidationHandler()));

        assertAll(
                () -> assertEquals(expectedErrorCount, actualException.getErrors().size()),
                () -> assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message())
        );
    }

    @Test
    public void givenAnInvalidNameLengthMoreThan255_whenCallNewCategoryAndValidate_thenShouldReceiveError() {
        final var expectedName = """
                Gostaria de enfatizar que o consenso sobre a necessidade de qualificação auxilia a preparação e a
                composição das posturas dos órgãos dirigentes com relação às suas atribuições.
                Do mesmo modo, a estrutura atual da organização apresenta tendências no sentido de aprovar a
                manutenção das novas proposições.
                """;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' must be between 3 and 255 characters";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var actualCategory =
                Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        final var actualException =
                assertThrows(DomainException.class, () -> actualCategory.validate(new ThrowsValidationHandler()));

        assertAll(
                () -> assertEquals(expectedErrorCount, actualException.getErrors().size()),
                () -> assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message())
        );
    }

    @Test
    public void givenAValidEmptyDescription_whenCallNewCategoryAndValidate_thenShouldReceiveOK() {
        final var expectedName = "Filmes";
        final var expectedDescription = "  ";
        final var expectedIsActive = true;

        final var actualCategory =
                Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        assertDoesNotThrow(() -> actualCategory.validate(new ThrowsValidationHandler()));

        assertAll(
                () -> assertNotNull(actualCategory),
                () -> assertNotNull(actualCategory.getId()),
                () -> assertEquals(expectedName, actualCategory.getName()),
                () -> assertEquals(expectedDescription, actualCategory.getDescription()),
                () -> assertEquals(expectedIsActive, actualCategory.isActive()),
                () -> assertNotNull(actualCategory.getCreatedAt()),
                () -> assertNotNull(actualCategory.getUpdatedAt()),
                () -> assertNull(actualCategory.getDeletedAt())
        );

    }

    @Test
    public void givenAValidFalseIsActive_whenCallNewCategoryAndValidate_thenShouldReceiveOK() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;

        final var actualCategory =
                Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        assertAll(
                () -> assertDoesNotThrow(() -> actualCategory.validate(new ThrowsValidationHandler())),
                () -> assertNotNull(actualCategory),
                () -> assertNotNull(actualCategory.getId()),
                () -> assertEquals(expectedName, actualCategory.getName()),
                () -> assertEquals(expectedDescription, actualCategory.getDescription()),
                () -> assertEquals(expectedIsActive, actualCategory.isActive()),
                () -> assertNotNull(actualCategory.getCreatedAt()),
                () -> assertNotNull(actualCategory.getUpdatedAt()),
                () -> assertNotNull(actualCategory.getDeletedAt())
        );

    }

    @Test
    public void givenAValidActiveCategory_whenCallDeactivate_thenReturnCategoryInactivated() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;

        final var aCategory =
                Category.newCategory(expectedName, expectedDescription, true);

        assertDoesNotThrow(() -> aCategory.validate(new ThrowsValidationHandler()));

        final var createdAt = aCategory.getCreatedAt();
        final var updatedAt = aCategory.getUpdatedAt();


        assertAll(
                () -> assertTrue(aCategory.isActive()),
                () -> assertNull(aCategory.getDeletedAt())
        );

        final var actualCategory = aCategory.deactivate();

        assertAll(
                () -> assertDoesNotThrow(() -> actualCategory.validate(new ThrowsValidationHandler())),
                () -> assertEquals(aCategory.getId(), actualCategory.getId()),
                () -> assertEquals(expectedName, actualCategory.getName()),
                () -> assertEquals(expectedDescription, actualCategory.getDescription()),
                () -> assertEquals(expectedIsActive, actualCategory.isActive()),
                () -> assertEquals(createdAt, actualCategory.getCreatedAt()),
                () -> assertTrue(actualCategory.getUpdatedAt().isAfter(updatedAt)),
                () -> assertNotNull(actualCategory.getDeletedAt())
        );

    }

    @Test
    public void givenAValidInactiveCategory_whenCallActivate_thenReturnCategoryActivated() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aCategory =
                Category.newCategory(expectedName, expectedDescription, false);

        assertDoesNotThrow(() -> aCategory.validate(new ThrowsValidationHandler()));

        final var createdAt = aCategory.getCreatedAt();
        final var updatedAt = aCategory.getUpdatedAt();

        assertFalse(aCategory.isActive());
        assertNotNull(aCategory.getDeletedAt());

        final var actualCategory = aCategory.activate();

        assertAll(
                () -> assertDoesNotThrow(() -> actualCategory.validate(new ThrowsValidationHandler())),
                () -> assertEquals(aCategory.getId(), actualCategory.getId()),
                () -> assertEquals(expectedName, actualCategory.getName()),
                () -> assertEquals(expectedDescription, actualCategory.getDescription()),
                () -> assertEquals(expectedIsActive, actualCategory.isActive()),
                () -> assertEquals(createdAt, actualCategory.getCreatedAt()),
                () -> assertTrue(actualCategory.getUpdatedAt().isAfter(updatedAt)),
                () -> assertNull(actualCategory.getDeletedAt())
        );


    }

    @Test
    public void givenAValidCategory_whenCallUpdate_thenReturnCategoryUpdated() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aCategory =
                Category.newCategory("Film", "A categoria", expectedIsActive);

        assertDoesNotThrow(() -> aCategory.validate(new ThrowsValidationHandler()));

        final var createdAt = aCategory.getCreatedAt();
        final var updatedAt = aCategory.getUpdatedAt();

        final var actualCategory = aCategory.update(expectedName, expectedDescription, expectedIsActive);

        assertAll(
                () -> assertDoesNotThrow(() -> actualCategory.validate(new ThrowsValidationHandler())),
                () -> assertEquals(aCategory.getId(), actualCategory.getId()),
                () -> assertEquals(expectedName, actualCategory.getName()),
                () -> assertEquals(expectedDescription, actualCategory.getDescription()),
                () -> assertEquals(expectedIsActive, actualCategory.isActive()),
                () -> assertEquals(createdAt, actualCategory.getCreatedAt()),
                () -> assertTrue(actualCategory.getUpdatedAt().isAfter(updatedAt)),
                () -> assertNull(actualCategory.getDeletedAt())
        );


    }

    @Test
    public void givenAValidCategory_whenCallUpdateToInactive_thenReturnCategoryUpdated() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;

        final var aCategory =
                Category.newCategory("Film", "A categoria", true);

        assertAll(
                () -> assertDoesNotThrow(() -> aCategory.validate(new ThrowsValidationHandler())),
                () -> assertTrue(aCategory.isActive()),
                () -> assertNull(aCategory.getDeletedAt())
        );


        final var createdAt = aCategory.getCreatedAt();
        final var updatedAt = aCategory.getUpdatedAt();

        final var actualCategory = aCategory.update(expectedName, expectedDescription, expectedIsActive);

        assertDoesNotThrow(() -> actualCategory.validate(new ThrowsValidationHandler()));

        assertAll(
                () -> assertEquals(aCategory.getId(), actualCategory.getId()),
                () -> assertEquals(expectedName, actualCategory.getName()),
                () -> assertEquals(expectedDescription, actualCategory.getDescription()),
                () -> assertFalse(aCategory.isActive()),
                () -> assertEquals(createdAt, actualCategory.getCreatedAt()),
                () -> assertTrue(actualCategory.getUpdatedAt().isAfter(updatedAt)),
                () -> assertNotNull(aCategory.getDeletedAt())
        );


    }

    @Test
    public void givenAValidCategory_whenCallUpdateWithInvalidParams_thenReturnCategoryUpdated() {
        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aCategory =
                Category.newCategory("Filmes", "A categoria", expectedIsActive);

        assertDoesNotThrow(() -> aCategory.validate(new ThrowsValidationHandler()));

        final var createdAt = aCategory.getCreatedAt();
        final var updatedAt = aCategory.getUpdatedAt();

        final var actualCategory = aCategory.update(expectedName, expectedDescription, expectedIsActive);

        assertAll(
                () -> assertEquals(aCategory.getId(), actualCategory.getId()),
                () -> assertEquals(expectedName, actualCategory.getName()),
                () -> assertEquals(expectedDescription, actualCategory.getDescription()),
                () -> assertTrue(aCategory.isActive()),
                () -> assertEquals(createdAt, actualCategory.getCreatedAt()),
                () -> assertTrue(actualCategory.getUpdatedAt().isAfter(updatedAt)),
                () -> assertNull(aCategory.getDeletedAt())
        );

    }
}
