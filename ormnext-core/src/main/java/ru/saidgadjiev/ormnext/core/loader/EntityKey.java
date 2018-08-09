package ru.saidgadjiev.ormnext.core.loader;

/**
 * Created by said on 09.08.2018.
 */
public class EntityKey {

    private final Class<?> entityType;

    private final Object id;

    public EntityKey(Class<?> entityType, Object id) {
        this.entityType = entityType;
        this.id = id;
    }

    public Class<?> getEntityType() {
        return entityType;
    }

    public Object getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EntityKey entityKey = (EntityKey) o;

        if (entityType != null ? !entityType.equals(entityKey.entityType) : entityKey.entityType != null) return false;
        return id != null ? id.equals(entityKey.id) : entityKey.id == null;
    }

    @Override
    public int hashCode() {
        int result = entityType != null ? entityType.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }
}
