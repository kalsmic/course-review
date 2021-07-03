package com.kalsmic.learn.dao;

import com.kalsmic.learn.exc.DaoException;
import com.kalsmic.learn.model.Course;

import java.util.List;

public interface CourseDao
{
    void add(Course course) throws DaoException;

    List<Course> findAll();

    Course findById( int id );
}
