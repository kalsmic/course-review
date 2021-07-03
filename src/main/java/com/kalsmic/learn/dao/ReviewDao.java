package com.kalsmic.learn.dao;

import com.kalsmic.learn.exc.DaoException;
import com.kalsmic.learn.model.Review;

import java.util.List;

public interface ReviewDao
{
    void add( Review review) throws DaoException;

    List<Review> findAll();

    List<Review> findByCourseId(int courseId);

}
