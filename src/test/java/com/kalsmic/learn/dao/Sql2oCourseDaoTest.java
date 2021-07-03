package com.kalsmic.learn.dao;

import com.kalsmic.learn.exc.DaoException;
import com.kalsmic.learn.model.Course;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class Sql2oCourseDaoTest
{

    private Sql2oCourseDao dao;
    private Connection conn;

    @BeforeEach
    public void setUp()
    {
        String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/init.sql'";
        Sql2o sql2o = new Sql2o( connectionString, "", "" );
        dao = new Sql2oCourseDao( sql2o );
        // keep connection open through entire test so it is not wiped out
        conn = sql2o.open();
    }
    @AfterEach
    public void tearDown() {
        conn.close();
    }

    @Test
    public void addingCourseSetsId() throws DaoException
    {
        Course course = getNewTestCourse();
        int originalCourseId = course.getId();
        dao.add( course );
        assertNotEquals(originalCourseId,course.getId());


    }

    @Test
    public void allAddedCoursesAreReturned() throws DaoException
    {
     Course course = getNewTestCourse();
     dao.add( course );

     assertEquals( 1, dao.findAll().size() );
    }

    @Test
    public void noCoursesReturnsEmptyList() {
        assertEquals( 0, dao.findAll().size() );
    }

    @Test
    public void existingCoursesCanBeFoundById() throws DaoException
    {
        Course course = getNewTestCourse();
        dao.add( course );
        Course foundCourse = dao.findById( course.getId() );


        assertEquals( course, foundCourse );
    }

    private Course getNewTestCourse()
    {
        return new Course( "Test", "http://test.com" );
    }
}