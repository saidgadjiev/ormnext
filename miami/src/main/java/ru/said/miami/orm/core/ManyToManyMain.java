package ru.said.miami.orm.core;

import org.sqlite.SQLiteDataSource;
import ru.said.miami.orm.core.dao.Dao;
import ru.said.miami.orm.core.dao.DaoManager;

import javax.sql.DataSource;
import java.util.List;

/**
 * Main sample routine to show how to do many-to-many type relationships. It also demonstrates how we user inner queries
 * as well foreign objects.
 * <p>
 * <p>
 * <b>NOTE:</b> We use asserts in a couple of places to verify the results but if this were actual production code, we
 * would have proper error handling.
 * </p>
 */
public class ManyToManyMain {

    private Dao<User, Integer> userDao;
    private Dao<Post, Integer> postDao;
    private Dao<UserPost, Integer> userPostDao;

    public static void main(String[] args) throws Exception {
        // turn our static method into an instance of Main
        new ManyToManyMain().doMain();
    }

    private void doMain() throws Exception {
        SQLiteDataSource dataSource = new SQLiteDataSource();

        // create our data-source for the database
        dataSource.setUrl("jdbc:sqlite:C:/test.sqlite");
        // setup our database and DAOs
        setupDatabase(dataSource);
        // read and write some data
        readWriteData();
        System.out.println("\n\nIt seems to have worked\n\n");
    }

    /**
     * Setup our database and DAOs
     */
    private void setupDatabase(DataSource connectionSource) throws Exception {

        /**
         * Create our DAOs. One for each class and associated table.
         */
        userDao = DaoManager.createDAO(connectionSource, User.class);
        postDao = DaoManager.createDAO(connectionSource, Post.class);
        userPostDao = DaoManager.createDAO(connectionSource, UserPost.class);

        /**
         * Create the tables for our example. This would not be necessary if the tables already existed.
         */
        userDao.createTable();
        postDao.createTable();
        userPostDao.createTable();
    }

    /**
     * Read and write some example data.
     */
    private void readWriteData() throws Exception {

        // create our 1st user
        User user1 = new User("Jim Coakley");

        // persist the user object to the database
        userDao.create(user1);

        // have user1 post something
        Post post1 = new Post("Wow is it cold outside!!");
        // save the post to the post table
        postDao.create(post1);

        // link the user and the post together in the join table
        UserPost user1Post1 = new UserPost(user1, post1);
        userPostDao.create(user1Post1);

        // have user1 post a second post
        Post post2 = new Post("Now its a bit warmer thank goodness.");
        postDao.create(post2);
        UserPost user1Post2 = new UserPost(user1, post2);
        userPostDao.create(user1Post2);

        // create another user
        User user2 = new User("Rose Gray");
        userDao.create(user2);

        // have the 2nd user also say the 2nd post
        UserPost user2Post1 = new UserPost(user2, post2);
        userPostDao.create(user2Post1);

		List<UserPost> userPosts = userPostDao.query(userPostDao.queryBuilder().prepare());

        System.out.println("userPosts = " + userPosts);
    }
}
