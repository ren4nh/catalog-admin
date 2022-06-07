package com.hartwig.catalog.admin.application.category.create;

import com.hartwig.catalog.admin.domain.category.Category;

public record CreateCategoryOutput(
        String id
) {

    public static CreateCategoryOutput from(final Category aCategory) {
        return new CreateCategoryOutput(aCategory.getId().getValue());
    }

    public static CreateCategoryOutput from(final String anId) {
        return new CreateCategoryOutput(anId);
    }
}
