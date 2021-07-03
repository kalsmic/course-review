package com.kalsmic.learn.model;

public class Course
{
    private int id;
    private String name;
    private String url;

    public Course( String name, String url )
    {
        this.name = name;
        this.url = url;
    }

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl( String url )
    {
        this.url = url;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof Course ) )
        {
            return false;
        }

        Course course = (Course) o;

        if ( getId() != course.getId() )
        {
            return false;
        }
        if ( !getName().equals( course.getName() ) )
        {
            return false;
        }
        return getUrl().equals( course.getUrl() );
    }

    @Override
    public int hashCode()
    {
        int result = getId();
        result = 31 * result + getName().hashCode();
        result = 31 * result + getUrl().hashCode();
        return result;
    }

}
