package com.bluerender.game;

//mini 2d vector :)
public class Vector
{
    public float X, Y;

    public Vector(){X = 0; Y = 0;}
    public Vector(float x, float y){X = x; Y = y;}

    //length property        
    public float getLength()
    {
        return (float)Math.sqrt((double)(X * X + Y * Y ));
    }

    //addition
    public static Vector add(Vector L, Vector R)
    {
        return new Vector(L.X + R.X, L.Y + R.Y);
    }

    //subtraction
    public static Vector subtract(Vector L, Vector R)
    {
        return new Vector(L.X - R.X, L.Y - R.Y);
    }

    //negative
    public static Vector negative(Vector R)
    {
        Vector temp = new Vector(-R.X, -R.Y);
        return temp;
    }

    //scalar multiply
    public static Vector scalarMultiply(Vector L, float R)
    {
        return new Vector(L.X * R, L.Y * R);
    }

    //divide multiply
    public static Vector scalarDivide(Vector L, float R)
    {
        return new Vector(L.X / R, L.Y / R);
    }

    //dot product
    public static float dot(Vector L, Vector R)
    {
        return (L.X * R.X + L.Y * R.Y);
    }

    //cross product, in 2d this is a scalar since we know it points in the Z direction
    public static float cross(Vector L, Vector R)
    {
        return (L.X*R.Y - L.Y*R.X);
    }

    //normalize the vector
    public void normalize()
    {
        float mag = getLength();

        X /= mag;
        Y /= mag;
    }

    //project this vector on to v
    public Vector Project(Vector v)
    {
        //projected vector = (this dot v) * v;
        float thisDotV = dot(this, v);
        return scalarMultiply(v, thisDotV);
    }

    //project this vector on to v, return signed magnatude
    public Vector Project(Vector v, Float mag)
    {
        //projected vector = (this dot v) * v;
        float thisDotV = dot(this, v);
        mag = thisDotV;
        return scalarMultiply(v, thisDotV);
    }
    
    public void setVector(float x, float y)
    {
    	this.X = x;
    	this.Y = y;
    }
    public void offset(float dx, float dy)
    {
    	this.X += dx;
    	this.Y += dy;
    }
    public void offset(Vector dv)
    {
    	this.X += dv.X;
    	this.Y += dv.Y;
    }
    public String toString()
    {
    	return this.X + ":" + this.Y;    
    }
}
