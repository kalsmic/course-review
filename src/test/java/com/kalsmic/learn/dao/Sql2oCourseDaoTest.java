package com.kalsmic.learn.dao;

import com.kalsmic.learn.exc.DaoException;
import com.kalsmic.learn.model.Course;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@TestInstance( TestInstance.Lifecycle.PER_CLASS )
class Sql2oCourseDaoTest
{

    private Sql2oCourseDao dao;
    private Connection conn;

    @BeforeAll
    void setUp()
    {
        String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/init.sql'";
        Sql2o sql2o = new Sql2o( connectionString, "", "" );
        dao = new Sql2oCourseDao( sql2o );
        // keep connection open through entire test so it is not wiped out
        conn = sql2o.open();
    }
    @AfterAll
    void tearDown() {
        conn.close();
    }

    @Test
    void addingCourseSetsId() throws DaoException
    {
        Course course = new Course( "Test", "http://test.com" );
        int originalCourseId = course.getId();
        dao.add( course );
        assertNotEquals(originalCourseId,course.getId());


    }

    @Test
    void allAddedCoursesAreReturned() throws DaoException
    {
     Course course = new Course( "Test2", "http://test2.com" );
     dao.add( course );

     assertEquals( 1, dao.findAll().size() );
    }

    @Test
    void noCoursesReturnsEmpltyList() throws Exception {
        assertEquals( 0, dao.findAll().size() );
    }
}