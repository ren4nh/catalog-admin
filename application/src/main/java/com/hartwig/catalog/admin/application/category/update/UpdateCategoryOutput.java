package com.hartwig.catalog.admin.application.category.update;

import com.hartwig.catalog.admin.domain.category.Category;

public record UpdateCategoryOutput(
        String id
) {

    public static UpdateCategoryOutput from(final Category aCategory) {
        return new UpdateCategoryOutput(aCategory.getId().getValue());
    }

    public static UpdateCategoryOutput from(final String anId) {
        return new UpdateCategoryOutput(anId);
    }
}
