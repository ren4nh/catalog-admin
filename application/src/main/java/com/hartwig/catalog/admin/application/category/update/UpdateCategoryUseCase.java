package com.hartwig.catalog.admin.application.category.update;

import com.hartwig.catalog.admin.application.UseCase;
import com.hartwig.catalog.admin.domain.validation.handler.Notification;
import io.vavr.control.Either;

public abstract class UpdateCategoryUseCase
        extends UseCase<UpdateCategoryCommand, Either<Notification, UpdateCategoryOutput>> {
}
