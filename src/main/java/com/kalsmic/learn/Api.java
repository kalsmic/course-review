package com.kalsmic.learn;

import com.google.gson.Gson;
import com.kalsmic.learn.dao.CourseDao;
import com.kalsmic.learn.dao.ReviewDao;
import com.kalsmic.learn.dao.Sql2oCourseDao;
import com.kalsmic.learn.dao.Sql2oReviewDao;
import com.kalsmic.learn.exc.ApiError;
import com.kalsmic.learn.exc.DaoException;
import com.kalsmic.learn.model.Course;
import com.kalsmic.learn.model.Review;
import org.sql2o.Sql2o;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class Api
{
    public static void main( String[] args )
    {
        String dataSource = "jdbc:h2:~/reviews.db";

        if ( args.length > 0 )
        {
            if ( args.length != 2 )
            {
                System.out.println( "java Api <port> <datasource" );
                System.exit( 0 );

            }
            port( Integer.parseInt( args[0] ) );
            dataSource = args[1];
        }

        Sql2o sql2o = new Sql2o( String.format( "%s;INIT=RUNSCRIPT from 'classpath:db/init.sql'", dataSource ), "",
                "" );

        CourseDao courseDao = new Sql2oCourseDao( sql2o );
        ReviewDao reviewDao = new Sql2oReviewDao( sql2o );

        Gson gson = new Gson();

        path( "/courses", () -> {


            post( "", "application/json", ( req, res ) -> {
                Course course = gson.fromJson( req.body(), Course.class );
                courseDao.add( course );

                res.status( 201 );
                return course;
            }, gson::toJson );

            get( "", "application/json", ( req, res ) -> courseDao.findAll(), gson::toJson );


            path( "/:courseId", () -> {


                get( "", "application/json", ( req, res ) -> {
                    int id;
                    try
                    {
                        id = Integer.parseInt( req.params( "courseId" ) );
                    }
                    catch ( NumberFormatException ex )
                    {
                        throw new ApiError( 400, "Please provide a valid id" );
                    }


                    Course course = courseDao.findById( id );

                    if ( course == null )
                    {
                        throw new ApiError( 404, "Could not find course id " + id );
                    }
                    return course;
                }, gson::toJson );

                post( "/reviews", "application/json", ( req, res ) -> {

                    int id;
                    Review review;
                    try
                    {
                        id = Integer.parseInt( req.params( "courseId" ) );
                        review = gson.fromJson( req.body(), Review.class );
                        review.setCourseId( id );
                        reviewDao.add( review );

                    }
                    catch ( NumberFormatException ex )
                    {
                        throw new ApiError( 400, "Please provide a valid id" );
                    }

                    catch ( DaoException ex )
                    {
                        throw new ApiError( 500, ex.getMessage() );
                    }
                    res.status( 201 );
                    return review;
                }, gson::toJson );

                get( "/reviews", "application/json", ( req, res ) -> {
                    int id;
                    try
                    {
                        id = Integer.parseInt( req.params( "courseId" ) );
                    }
                    catch ( NumberFormatException ex )
                    {
                        throw new ApiError( 400, "Please provide a valid id" );
                    }
                    return reviewDao.findByCourseId( id );
                }, gson::toJson );

            } );


        } );


        exception( ApiError.class, ( exc, req, res ) -> {
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put( "status", exc.getStatus() );
            jsonMap.put( "errorMessage", exc.getMessage() );
            res.type( "application/json" );
            res.status( exc.getStatus() );
            res.body( gson.toJson( jsonMap ) );
        } );
        after( ( req, res ) -> res.type( "application/json" ) );

    }

}
