package com.kalsmic.learn.dao;

import com.kalsmic.learn.exc.DaoException;
import com.kalsmic.learn.model.Course;
import com.kalsmic.learn.model.Review;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance( TestInstance.Lifecycle.PER_CLASS )
class Sql2oReviewDaoTest
{
    private Sql2oReviewDao reviewDao;
    private Connection conn;
    private Course course;
    private Sql2oCourseDao courseDao;

    @BeforeAll
    public void setUp() throws DaoException
    {
        String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/init.sql'";
        Sql2o sql2o = new Sql2o( connectionString, "", "" );
        conn = sql2o.open();
        courseDao = new Sql2oCourseDao( sql2o );
        reviewDao = new Sql2oReviewDao( sql2o );
        course = getNewTestCourse();
        courseDao.add( course );

    }

    @AfterAll
    public void tearDown()
    {
        conn.close();
    }

    @Test
    public void addReviewToCourse() throws DaoException
    {
        Review review = new Review(course.getId(), 5, "Excellent"  );
        int originalId =  review.getId();

        reviewDao.add( review );

        assertNotEquals(originalId, review.getId());
    }

    @Test
    public void multipleReviewsAreFoundWhenTheyExistForACourse() throws DaoException
    {
        reviewDao.add( new Review( course.getId(),5,"Excellent" ) );
        reviewDao.add( new Review( course.getId(),2,"Fake" ) );
        List<Review> reviews = reviewDao.findByCourseId( course.getId() );

        assertEquals(2, reviews.size());
    }


//

    private Course getNewTestCourse()
    {
        return new Course( "Test", "http://test.com" );
    }


}