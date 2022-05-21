package com.hartwig.catalog.admin.application.category.create;

import com.hartwig.catalog.admin.application.UseCase;
import com.hartwig.catalog.admin.domain.validation.handler.Notification;
import io.vavr.control.Either;

public abstract class CreateCategoryUseCase
        extends UseCase<CreateCategoryCommand, Either<Notification, CreateCategoryOutput>> {
}
