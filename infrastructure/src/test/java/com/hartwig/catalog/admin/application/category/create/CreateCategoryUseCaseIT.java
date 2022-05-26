package com.hartwig.catalog.admin.application.category.create;

import com.hartwig.catalog.admin.IntegrationTest;
import com.hartwig.catalog.admin.domain.category.CategoryGateway;
import com.hartwig.catalog.admin.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@IntegrationTest
public class CreateCategoryUseCaseIT {

    @Autowired
    private CreateCategoryUseCase useCase;

    @Autowired
    private CategoryRepository categoryRepository;

    @SpyBean
    private CategoryGateway categoryGateway;

    @Test
    public void givenAValidCommand_whenCallsCreateCategory_shouldReturnCategoryId() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        assertEquals(0, categoryRepository.count());

        final var aCommand =
                CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);


        final var actualOutput = useCase.execute(aCommand).get();

        assertEquals(1, categoryRepository.count());

        assertNotNull(actualOutput);
        assertNotNull(actualOutput.id());

        final var actualCategory = categoryRepository.findById(actualOutput.id().getValue()).get();

        assertAll(
                () -> assertEquals(expectedName, actualCategory.getName()),
                () -> assertEquals(expectedDescription, actualCategory.getDescription()),
                () -> assertEquals(expectedIsActive, actualCategory.isActive()),
                () -> assertNotNull(actualCategory.getCreatedAt()),
                () -> assertNotNull( actualCategory.getUpdatedAt()),
                () -> assertNull(actualCategory.getDeletedAt())
        );
    }

    @Test
    public void givenAInvalidName_whenCallsCreateCategory_thenShouldReturnDomainException() {
        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be null";
        final var expectedErrorCount = 1;

        final var aCommand =
                CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);

        assertEquals(0, categoryRepository.count());
        final var notification = useCase.execute(aCommand).getLeft();

        assertAll(
                () -> assertEquals(expectedErrorCount, notification.getErrors().size()),
                () -> assertEquals(expectedErrorMessage, notification.firstError().message())
        );

        assertEquals(0, categoryRepository.count());

        Mockito.verify(categoryGateway, times(0)).create(any());
    }

    @Test
    public void givenAValidCommandWithInactiveCategory_whenCallsCreateCategory_shouldReturnInactiveCategoryId() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;

        final var aCommand =
                CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);

        assertEquals(0, categoryRepository.count());

        final var actualOutput = useCase.execute(aCommand).get();

        assertAll(
                () -> assertNotNull(actualOutput),
                () -> assertNotNull(actualOutput.id())
        );

        assertEquals(1, categoryRepository.count());

        final var actualCategory = categoryRepository.findById(actualOutput.id().getValue()).get();

        assertAll(
                () -> assertEquals(expectedName, actualCategory.getName()),
                () -> assertEquals(expectedDescription, actualCategory.getDescription()),
                () -> assertEquals(expectedIsActive, actualCategory.isActive()),
                () -> assertNotNull(actualCategory.getCreatedAt()),
                () -> assertNotNull( actualCategory.getUpdatedAt()),
                () -> assertNotNull(actualCategory.getDeletedAt())
        );
    }

    @Test
    public void givenAValidCommand_whenGatewayThrowsRandomException_shouldReturnAException() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "Gateway error";

        final var aCommand =
                CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);

        doThrow(new IllegalStateException(expectedErrorMessage))
                        .when(categoryGateway).create(any());

        final var notification = useCase.execute(aCommand).getLeft();

        assertAll(
                () -> assertEquals(expectedErrorCount, notification.getErrors().size()),
                () -> assertEquals(expectedErrorMessage, notification.firstError().message())
        );

    }
}
