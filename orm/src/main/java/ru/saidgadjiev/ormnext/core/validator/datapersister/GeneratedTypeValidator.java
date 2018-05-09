package ru.saidgadjiev.ormnext.core.validator.datapersister;

import ru.saidgadjiev.ormnext.core.exception.WrongGeneratedTypeException;
import ru.saidgadjiev.ormnext.core.field.DataPersisterManager;
import ru.saidgadjiev.ormnext.core.field.DataType;
import ru.saidgadjiev.ormnext.core.field.persister.DataPersister;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * Created by said on 03.02.2018.
 */
public class GeneratedTypeValidator implements IValidator {

    private Field field;

    private boolean generated;

    public GeneratedTypeValidator(Field field, boolean generated) {
        this.field = field;
        this.generated = generated;
    }

    @Override
    public void validate(DataPersister<?> dataPersister) {
        if (generated && !dataPersister.isValidForGenerated()) {
            StringBuilder builder = new StringBuilder();

            for (int dataType: DataType.types()) {
                DataPersister<?> persister = DataPersisterManager.lookup(dataType);

                if (persister != null && persister.isValidForGenerated()) {
                    builder.append(Arrays.toString(persister.getAssociatedClasses()));
                }
            }

            throw new WrongGeneratedTypeException(field, builder.toString());
        }
    }
}
