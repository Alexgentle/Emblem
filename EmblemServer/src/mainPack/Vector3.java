/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mainPack;

/**
 *
 * @author Dasty
 */
public class Vector3 
{
    public double x;
    public double y;
    public double z;
    
    

    public Vector3(double tx, double ty, double tz) {
        x = tx;
        y = ty;
        z = tz;
    }
    
    public void mult(double num)
    {
        x*=num;
        y*=num;
        z*=num;
        
    }
    
    public void add(Vector3 o)
    {
        x+=o.x;
        y+=o.y;
        z+=o.z;
    }
    
    @Override
    public boolean equals(Object o)
    {
        Vector3 ov = (Vector3)o;
        //System.out.println(ov+" - "+this);
        if(ov.x == x && ov.y == y && ov.z == z)
            return true;
        else
            return false;
    }
    
    @Override
    public String toString()
    {
        return Math.round(x*1000000)/1000000.0d+","+Math.round(y*1000000)/1000000.0d+","+Math.round(z*1000000)/1000000.0d;
    }
}
