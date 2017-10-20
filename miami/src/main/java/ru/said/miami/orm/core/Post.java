package ru.said.miami.orm.core;


import ru.said.miami.orm.core.field.DBField;
import ru.said.miami.orm.core.table.DBTable;

/**
 * Post to some blog with String content.
 */
@DBTable(name = "post")
public class Post {

	// we use this field-name so we can query for posts with a certain id
	public final static String ID_FIELD_NAME = "id";

	// this id is generated by the database and set on the object when it is passed to the create method
	@DBField(id = true, generated = true, columnName = ID_FIELD_NAME)
	int id;

	// contents of the post
	@DBField
	String contents;

	Post() {
		// for ormlite
	}

	public Post(String contents) {
		this.contents = contents;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	@Override
	public String toString() {
		return "Post{" +
				"id=" + id +
				", contents='" + contents + '\'' +
				'}';
	}
}
