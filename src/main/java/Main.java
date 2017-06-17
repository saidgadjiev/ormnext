import dao.DaoImpl;
import utils.TableUtils;
import utils.test.Test;

/**
 * Created by said on 10.06.17.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        DaoImpl<Test> dao = new DaoImpl<>(Test.class);

        TableUtils.createTable(dao.getDataSource(), Test.class);
    }
}
