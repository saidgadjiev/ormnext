package ru.saidgadjiev.orm.next.core.validator.data_persister;

import ru.saidgadjiev.orm.next.core.field.DataPersisterManager;
import ru.saidgadjiev.orm.next.core.field.DataType;
import ru.saidgadjiev.orm.next.core.field.persister.DataPersister;
import ru.saidgadjiev.orm.next.core.utils.ExceptionUtils;

import java.util.Arrays;

/**
 * Created by said on 03.02.2018.
 */
public class GeneratedTypeValidator implements IValidator {

    private boolean generated;

    public GeneratedTypeValidator(boolean generated) {
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

            throw new IllegalArgumentException(ExceptionUtils.message(ExceptionUtils.Exception.WRONG_GENERATED_TYPE, builder.toString()));
        }
    }
}
