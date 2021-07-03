package com.kalsmic.learn;

import com.google.gson.Gson;
import com.kalsmic.learn.dao.Sql2oCourseDao;
import com.kalsmic.learn.model.Course;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import spark.Spark;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance( TestInstance.Lifecycle.PER_CLASS )
class ApiTest
{
    public static final String PORT = "4568";
    public static final String TEST_DATASOURCE = "jdbc:h2:mem:testing";
    private Connection connection;
    private ApiClient client;
    private Gson gson;
    private Sql2oCourseDao courseDao;

    @BeforeAll
    public static void startServer(){
        String[] args = {PORT, TEST_DATASOURCE};
        Api.main( args );
    }

    @AfterAll
    public static void stopServer(){
        Spark.stop();
    }

    @BeforeEach
    public void setUp()
    {
        Sql2o sql2o = new Sql2o( TEST_DATASOURCE+ ";INIT=RUNSCRIPT from 'classpath:db/init.sql'" ,"","");
        courseDao = new Sql2oCourseDao( sql2o );
        // keep connection open through entire test so it is not wiped out
        connection = sql2o.open();
        client = new ApiClient( "http://localhost:" + PORT );
        gson = new Gson();

    }

    @AfterEach
    public void tearDown()
    {
        connection.close();
    }

    @Test
    public void addingCoursesReturnsCreatedStatus()
    {
        Map<String,String> values = new HashMap<>();
        values.put( "name", "Test" );
        values.put( "url", "http://test.com/test" );

        ApiResponse res = client.request( "POST", "/courses",  gson.toJson( values ) );

        assertEquals(201, res.getStatus());

    }

    @Test
    public void coursesCanBeAccessedById() throws Exception
    {
        Course course = getNewTestCourse();
        courseDao.add(course);
        ApiResponse res = client.request( "GET", "/courses/"+course.getId() );
        Course  retrieved = gson.fromJson( res.getBody(), Course.class );
        assertEquals(course, retrieved);
    }

    @Test
    public void missingCoursesReturnNotFoundStatus()
    {
        ApiResponse res = client.request( "GET", "/courses/45" );
        Course  retrieved = gson.fromJson( res.getBody(), Course.class );
        assertEquals(404, res.getStatus());
        assertTrue(res.getBody().contains( "Could not find course id 45" ));
    }

    @Test
    public void invalidCourseIdReturnBadRequestStatus()
    {
        ApiResponse res = client.request( "GET", "/courses/45e" );
        Course  retrieved = gson.fromJson( res.getBody(), Course.class );
        assertEquals(400, res.getStatus());
        assertTrue(res.getBody().contains( "Please provide a valid id" ));
    }

    private Course getNewTestCourse()
    {
        return new Course( "Test", "http://test.com" );
    }

}