package com.hartwig.catalog.admin.application.category.retrieve.list;

import com.hartwig.catalog.admin.application.UseCase;
import com.hartwig.catalog.admin.domain.category.CategorySearchQuery;
import com.hartwig.catalog.admin.domain.pagination.Pagination;

public abstract class ListCategoriesUseCase
        extends UseCase<CategorySearchQuery, Pagination<CategoryListOutput>> {
}
