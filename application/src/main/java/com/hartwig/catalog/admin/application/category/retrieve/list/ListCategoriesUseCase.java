package com.hartwig.catalog.admin.application.category.retrieve.list;

import com.hartwig.catalog.admin.application.UseCase;
import com.hartwig.catalog.admin.domain.pagination.Pagination;
import com.hartwig.catalog.admin.domain.pagination.SearchQuery;

public abstract class ListCategoriesUseCase
        extends UseCase<SearchQuery, Pagination<CategoryListOutput>> {
}
