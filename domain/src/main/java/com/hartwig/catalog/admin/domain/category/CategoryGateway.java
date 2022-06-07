package com.hartwig.catalog.admin.domain.category;

import com.hartwig.catalog.admin.domain.pagination.Pagination;
import com.hartwig.catalog.admin.domain.pagination.SearchQuery;

import java.util.Optional;

public interface CategoryGateway {

    Category create(Category aCategory);

    void deleteById(CategoryID anId);

    Optional<Category> findById(CategoryID anId);

    Category update(Category aCategory);

    Pagination<Category> findAll(SearchQuery aQuery);
}
