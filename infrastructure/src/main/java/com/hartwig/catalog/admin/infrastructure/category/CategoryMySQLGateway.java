package com.hartwig.catalog.admin.infrastructure.category;

import com.hartwig.catalog.admin.domain.category.Category;
import com.hartwig.catalog.admin.domain.category.CategoryGateway;
import com.hartwig.catalog.admin.domain.category.CategoryID;
import com.hartwig.catalog.admin.domain.category.CategorySearchQuery;
import com.hartwig.catalog.admin.domain.pagination.Pagination;
import com.hartwig.catalog.admin.infrastructure.category.persistence.CategoryJpaEntity;
import com.hartwig.catalog.admin.infrastructure.category.persistence.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryMySQLGateway implements CategoryGateway {

    private final CategoryRepository repository;

    public CategoryMySQLGateway(final CategoryRepository categoryRepository) {
        this.repository = categoryRepository;
    }

    @Override
    public Category create(final Category aCategory) {
        return save(aCategory);
    }

    @Override
    public void deleteById(CategoryID anId) {
        final var anIdValue = anId.getValue();
        if(repository.existsById(anIdValue)) {
            repository.deleteById(anIdValue);
        }
    }

    @Override
    public Optional<Category> findById(CategoryID anId) {
        return repository.findById(anId.getValue()).map(CategoryJpaEntity::toAggregate);
    }

    @Override
    public Category update(final Category aCategory) {
        return save(aCategory);
    }

    @Override
    public Pagination<Category> findAll(CategorySearchQuery aQuery) {
        return null;
    }

    private Category save(Category aCategory) {
        return this.repository.save(CategoryJpaEntity.from(aCategory)).toAggregate();
    }
}
