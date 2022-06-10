package com.hartwig.catalog.admin.e2e.category;

import com.hartwig.catalog.admin.E2ETest;
import com.hartwig.catalog.admin.domain.category.CategoryID;
import com.hartwig.catalog.admin.infrastructure.category.models.CategoryResponse;
import com.hartwig.catalog.admin.infrastructure.category.models.CreateCategoryRequest;
import com.hartwig.catalog.admin.infrastructure.category.models.UpdateCategoryRequest;
import com.hartwig.catalog.admin.infrastructure.category.persistence.CategoryRepository;
import com.hartwig.catalog.admin.infrastructure.configuration.json.Json;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@E2ETest
@Testcontainers
public class CategoryE2ETest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Container
    private static final MySQLContainer MY_SQL_CONTAINER = new MySQLContainer<>("mysql:latest").withPassword("123456").withUsername("root").withDatabaseName("catalog_admin");

    @DynamicPropertySource
    public static void setDatasourceProperties(final DynamicPropertyRegistry registry) {
        registry.add("mysql.port", () -> MY_SQL_CONTAINER.getMappedPort(3306));
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToCreateANewCategoryWithValidValues() throws Exception {
        assertEquals(0, categoryRepository.count());
        assertTrue(MY_SQL_CONTAINER.isRunning());

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;

        final var actualId = givenACategory(expectedName, expectedDescription, expectedIsActive);

        final var actualCategory = categoryRepository.findById(actualId.getValue()).get();

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
    public void asACatalogAdminIShouldBeAbleToNavigateAllCategories() throws Exception {
        assertEquals(0, categoryRepository.count());
        assertTrue(MY_SQL_CONTAINER.isRunning());

        givenACategory("Filmes", null, true);
        givenACategory("Documentários", null, true);
        givenACategory("Séries", null, true);

        listCategories(0,1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(0)))
                .andExpect(jsonPath("$.per_page", equalTo(1)))
                .andExpect(jsonPath("$.total", equalTo(3)))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].name", equalTo("Documentários")));

        listCategories(1,1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(1)))
                .andExpect(jsonPath("$.per_page", equalTo(1)))
                .andExpect(jsonPath("$.total", equalTo(3)))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].name", equalTo("Filmes")));

        listCategories(2,1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(2)))
                .andExpect(jsonPath("$.per_page", equalTo(1)))
                .andExpect(jsonPath("$.total", equalTo(3)))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].name", equalTo("Séries")));

        listCategories(3,1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(3)))
                .andExpect(jsonPath("$.per_page", equalTo(1)))
                .andExpect(jsonPath("$.total", equalTo(3)))
                .andExpect(jsonPath("$.items", hasSize(0)));
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToSearchBetweenAllCategories() throws Exception {
        assertEquals(0, categoryRepository.count());
        assertTrue(MY_SQL_CONTAINER.isRunning());

        givenACategory("Filmes", null, true);
        givenACategory("Documentários", null, true);
        givenACategory("Séries", null, true);

        listCategories(0,1, "fil")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(0)))
                .andExpect(jsonPath("$.per_page", equalTo(1)))
                .andExpect(jsonPath("$.total", equalTo(1)))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].name", equalTo("Filmes")));
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToSortAllCategoriesOrderedByDescriptionDesc() throws Exception {
        assertEquals(0, categoryRepository.count());
        assertTrue(MY_SQL_CONTAINER.isRunning());

        givenACategory("Filmes", "C", true);
        givenACategory("Documentários", "Z", true);
        givenACategory("Séries", "A", true);

        listCategories(0,3, "", "description", "desc")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(0)))
                .andExpect(jsonPath("$.per_page", equalTo(3)))
                .andExpect(jsonPath("$.total", equalTo(3)))
                .andExpect(jsonPath("$.items", hasSize(3)))
                .andExpect(jsonPath("$.items[0].name", equalTo("Documentários")))
                .andExpect(jsonPath("$.items[1].name", equalTo("Filmes")))
                .andExpect(jsonPath("$.items[2].name", equalTo("Séries")));
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToGetACategoryByItsIdentifier() throws Exception {
        assertEquals(0, categoryRepository.count());
        assertTrue(MY_SQL_CONTAINER.isRunning());

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var actualId = givenACategory(expectedName, expectedDescription, expectedIsActive);

        final var actualCategory = retrieveACategory(actualId.getValue());

        assertAll(
                () -> assertEquals(expectedName, actualCategory.name()),
                () -> assertEquals(expectedDescription, actualCategory.description()),
                () -> assertEquals(expectedIsActive, actualCategory.active()),
                () -> assertNotNull(actualCategory.createdAt()),
                () -> assertNotNull(actualCategory.updatedAt()),
                () -> assertNull(actualCategory.deletedAt())
        );
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToSeeATreatedErrorByGettingANotFoundCategory() throws Exception {
        assertEquals(0, categoryRepository.count());
        assertTrue(MY_SQL_CONTAINER.isRunning());

        final var aRequest = get("/categories/123");

        this.mvc.perform(aRequest)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", equalTo("Category with ID 123 was not found")));
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToUpdateACategoryByItsIdentifier() throws Exception {
        assertEquals(0, categoryRepository.count());
        assertTrue(MY_SQL_CONTAINER.isRunning());

        final var actualId = givenACategory("Movies", null, true);

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aRequestBody = new UpdateCategoryRequest(expectedName, expectedDescription, expectedIsActive);

        final var aRequest = put("/categories/" + actualId.getValue())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(aRequestBody));

        this.mvc.perform(aRequest)
                .andExpect(status().isOk());

        final var actualCategory = categoryRepository.findById(actualId.getValue()).get();

        assertAll(
                () -> assertEquals(expectedName, actualCategory.getName()),
                () -> assertEquals(expectedDescription, actualCategory.getDescription()),
                () -> assertEquals(expectedIsActive, actualCategory.isActive()),
                () -> assertNotNull(actualCategory.getCreatedAt()),
                () -> assertNotNull(actualCategory.getUpdatedAt()),
                () -> assertNull(actualCategory.getDeletedAt())
        );
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToInactivateACategoryByItsIdentifier() throws Exception {
        assertEquals(0, categoryRepository.count());
        assertTrue(MY_SQL_CONTAINER.isRunning());

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;

        final var actualId = givenACategory(expectedName, expectedDescription, true);

        final var aRequestBody = new UpdateCategoryRequest(expectedName, expectedDescription, expectedIsActive);

        final var aRequest = put("/categories/" + actualId.getValue())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(aRequestBody));

        this.mvc.perform(aRequest)
                .andExpect(status().isOk());

        final var actualCategory = categoryRepository.findById(actualId.getValue()).get();

        assertAll(
                () -> assertEquals(expectedName, actualCategory.getName()),
                () -> assertEquals(expectedDescription, actualCategory.getDescription()),
                () -> assertEquals(expectedIsActive, actualCategory.isActive()),
                () -> assertNotNull(actualCategory.getCreatedAt()),
                () -> assertNotNull(actualCategory.getUpdatedAt()),
                () -> assertNotNull(actualCategory.getDeletedAt())
        );
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToActivateACategoryByItsIdentifier() throws Exception {
        assertEquals(0, categoryRepository.count());
        assertTrue(MY_SQL_CONTAINER.isRunning());

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var actualId = givenACategory(expectedName, expectedDescription, false);

        final var aRequestBody = new UpdateCategoryRequest(expectedName, expectedDescription, expectedIsActive);

        final var aRequest = put("/categories/" + actualId.getValue())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(aRequestBody));

        this.mvc.perform(aRequest)
                .andExpect(status().isOk());

        final var actualCategory = categoryRepository.findById(actualId.getValue()).get();

        assertAll(
                () -> assertEquals(expectedName, actualCategory.getName()),
                () -> assertEquals(expectedDescription, actualCategory.getDescription()),
                () -> assertEquals(expectedIsActive, actualCategory.isActive()),
                () -> assertNotNull(actualCategory.getCreatedAt()),
                () -> assertNotNull(actualCategory.getUpdatedAt()),
                () -> assertNull(actualCategory.getDeletedAt())
        );
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToDeleteACategoryByItsIdentifier() throws Exception {
        assertEquals(0, categoryRepository.count());
        assertTrue(MY_SQL_CONTAINER.isRunning());

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var actualId = givenACategory(expectedName, expectedDescription, expectedIsActive);

        this.mvc.perform(delete("/categories/" + actualId.getValue()))
                .andExpect(status().isNoContent());

        assertFalse(this.categoryRepository.existsById(actualId.getValue()));

    }


    private ResultActions listCategories(final int page, final int perPage, final String search) throws Exception {
        return listCategories(page, perPage, search,"","");
    }

    private ResultActions listCategories(final int page, final int perPage) throws Exception {
        return listCategories(page, perPage, "","","");
    }

    private ResultActions listCategories(final int page, final int perPage, final String search, final String sort, final String dir) throws Exception {
        final var aRequest = get("/categories")
                .queryParam("page", String.valueOf(page))
                .queryParam("perPage", String.valueOf(perPage))
                .queryParam("search", search)
                .queryParam("sort", sort)
                .queryParam("dir", dir);

        return this.mvc.perform(aRequest);
    }

    private CategoryID givenACategory(final String aName, final String aDescription, final boolean isActive) throws Exception {
        final var request = new CreateCategoryRequest(aName, aDescription, isActive);

        final var aRequest = post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(request));

        final var actualId = this.mvc.perform(aRequest)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse().getHeader("Location")
                .replace("/categories/", "");

        return CategoryID.from(actualId);
    }

    private CategoryResponse retrieveACategory(final String anId) throws Exception {
        final var aRequest = get("/categories/" + anId);

        final var json = this.mvc.perform(aRequest)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        return Json.readValue(json, CategoryResponse.class);
    }
}
