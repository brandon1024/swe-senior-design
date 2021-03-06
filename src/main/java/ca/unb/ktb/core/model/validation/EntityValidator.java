package ca.unb.ktb.core.model.validation;

import org.springframework.lang.Nullable;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

public final class EntityValidator {

    /**
     * Validate an entity.
     *
     * @param <E> Type of the entity.
     * @param entity The entity to be validated.
     * @return True if the entity is valid, false if the entity is null or invalid.
     * */
    public static <E> boolean validateEntity(@Nullable final E entity) {
        if(Objects.isNull(entity)) {
            return false;
        }

        return getBucketConstraintViolations(entity).isEmpty();
    }

    /**
     * Verify that an entity is valid, returning the entity if so, otherwise throw an exception produced by the exception
     * supplying function.
     *
     * @param <E> Type of the entity.
     * @param <T> Type of the exception to be thrown.
     * @param entity The entity to verify.
     * @param exceptionSupplier The supplying function that produces an exception to be thrown.
     * @throws T If the entity is invalid.
     * @return The supplied entity, if valid.
     * */
    public static <E, T extends Throwable> E validateEntity(@Nullable final E entity,
                                                            final Supplier<? extends T> exceptionSupplier) throws T {
        if(validateEntity(entity)) {
            return entity;
        }

        throw exceptionSupplier.get();
    }

    /**
     * Get a set of validation constraint violations for an entity.
     *
     * @param entity The bucket to be validated.
     * @param <E> The type of the entity.
     * @return The set of constraint violations for a given bucket.
     * */
    public static <E> Set<ConstraintViolation<E>> getBucketConstraintViolations(final E entity) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

        return factory.getValidator().validate(entity);
    }
}
