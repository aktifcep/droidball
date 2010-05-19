package com.bluerender.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

//our vehicle object
public class Vehicle extends RigidBody
{
    class Wheel
    {
        private Vector m_forwardAxis, m_sideAxis;
        private float m_wheelTorque, m_wheelSpeed, m_wheelInertia, m_wheelRadius;
        private Vector m_Position = new Vector();

        public Wheel(Vector position, float radius)
        {
            m_Position = position;
            SetSteeringAngle(0);
            m_wheelSpeed = 0;
            m_wheelRadius = radius;
            m_wheelInertia = radius * radius; //fake value
        }

        public void SetSteeringAngle(float newAngle)
        {
        	Matrix mat = new Matrix();

        	float []vecArray = new float[4];
        	//foward vector
        	vecArray[0] = 0;
        	vecArray[1] = 1;
        	//side vector
        	vecArray[2] = -1;
        	vecArray[3] = 0;

        	mat.postRotate(newAngle / (float)Math.PI * 180.0f);
        	mat.mapVectors(vecArray);

        	m_forwardAxis = new Vector(vecArray[0], vecArray[1]);
        	m_sideAxis = new Vector(vecArray[2], vecArray[3]);
        }

        public void AddTransmissionTorque(float newValue)
        {
            m_wheelTorque += newValue;
        }

        public float GetWheelSpeed()
        {
            return m_wheelSpeed;
        }

        public Vector GetAttachPoint()
        {
            return m_Position;
        }

        public Vector CalculateForce(Vector relativeGroundSpeed, float timeStep)
        {
            //calculate speed of tire patch at ground
            Vector patchSpeed = Vector.scalarMultiply(Vector.scalarMultiply(Vector.negative(m_forwardAxis), m_wheelSpeed), m_wheelRadius);
            

            //get velocity difference between ground and patch
            Vector velDifference = Vector.add(relativeGroundSpeed , patchSpeed);

            //project ground speed onto side axis
            Float forwardMag = new Float(0f);
            Vector sideVel = velDifference.Project(m_sideAxis);
            Vector forwardVel = velDifference.Project(m_forwardAxis, forwardMag);

            //calculate super fake friction forces
            //calculate response force
            Vector responseForce = Vector.scalarMultiply(Vector.negative(sideVel), 2.0f);
            responseForce =  Vector.subtract(responseForce, forwardVel);

            //calculate torque on wheel
            m_wheelTorque += forwardMag * m_wheelRadius;

            //integrate total torque into wheel
            m_wheelSpeed += m_wheelTorque / m_wheelInertia * timeStep;

            //clear our transmission torque accumulator
            m_wheelTorque = 0;

            //return force acting on body
            return responseForce;
        }
    }
    
    private Wheel [] wheels = new Wheel[4];

    public void Setup(Vector halfSize, float mass, int color)
    {
        //front wheels
        wheels[0] = new Wheel(new Vector(halfSize.X, halfSize.Y), 0.5f);
        wheels[1] = new Wheel(new Vector(-halfSize.X, halfSize.Y), 0.5f);

        //rear wheels
        wheels[2] = new Wheel(new Vector(halfSize.X, -halfSize.Y), 0.5f);
        wheels[3] = new Wheel(new Vector(-halfSize.X, -halfSize.Y), 0.5f);

        super.Setup(halfSize, mass, color);
    }

    public void SetSteering(float steering)
    {
        float steeringLock = 0.75f;

        //apply steering angle to front wheels
        wheels[0].SetSteeringAngle(-steering * steeringLock);
        wheels[1].SetSteeringAngle(-steering * steeringLock);
    }

    public void SetThrottle(float throttle, boolean allWheel)
    {
        float torque = 20.0f;

        //apply transmission torque to back wheels
        if (allWheel)
        {
            wheels[0].AddTransmissionTorque(throttle * torque);
            wheels[1].AddTransmissionTorque(throttle * torque);
        }

        wheels[2].AddTransmissionTorque(throttle * torque);
        wheels[3].AddTransmissionTorque(throttle * torque);
    }

    public void SetBrakes(float brakes)
    {
        float brakeTorque = 4.0f;

        //apply brake torque apposing wheel vel
        for (Wheel wheel : wheels)
        {
            float wheelVel = wheel.GetWheelSpeed();
            wheel.AddTransmissionTorque(-wheelVel * brakeTorque * brakes);
        }
    }

    public void Update(float timeStep)
    {
        for (Wheel wheel : wheels)
        {
            //wheel.m_wheelSpeed = 30.0f;
            Vector worldWheelOffset = super.RelativeToWorld(wheel.GetAttachPoint());
            Vector worldGroundVel = super.PointVel(worldWheelOffset);
            Vector relativeGroundSpeed = super.WorldToRelative(worldGroundVel);
            Vector relativeResponseForce = wheel.CalculateForce(relativeGroundSpeed, timeStep);
            Vector worldResponseForce = super.RelativeToWorld(relativeResponseForce);

            super.AddForce(worldResponseForce, worldWheelOffset);
        }

        super.Update(timeStep);
    }
}

//our simulation object
class RigidBody
{
    //linear properties
    private Vector m_position = new Vector();
    private Vector m_velocity = new Vector();
    private Vector m_forces = new Vector();
    private float m_mass;

    //angular properties
    private float m_angle;
    private float m_angularVelocity;
    private float m_torque;
    private float m_inertia;

    //graphical properties
    private Vector m_halfSize = new Vector();
    Rect rect = new Rect();
    private int m_color;

    public RigidBody()
    { 
        //set these defaults so we dont get divide by zeros
        m_mass = 1.0f; 
        m_inertia = 1.0f; 
    }

    //intialize out parameters
    public void Setup(Vector halfSize, float mass, int color)
    {
        //store physical parameters
        m_halfSize = halfSize;
        m_mass = mass;
        m_color = color;
        m_inertia = (1.0f / 12.0f) * (halfSize.X * halfSize.X) * (halfSize.Y * halfSize.Y) * mass;

        //generate our viewable rectangle
        rect.left = (int)-m_halfSize.X;
        rect.top = (int)-m_halfSize.Y;
        
        rect.right = rect.left + (int)(m_halfSize.X * 2.0f);
        rect.bottom = rect.top + (int)(m_halfSize.Y * 2.0f);
    }

    public void SetLocation(Vector position, float angle)
    {
        m_position = position;
        m_angle = angle;
    }

    public Vector GetPosition()
    {
        return m_position;
    }

    public void Update(float timeStep)
    {
        //integrate physics
        //linear
        Vector acceleration = Vector.scalarDivide(m_forces, m_mass);
        m_velocity = Vector.add(m_velocity, Vector.scalarMultiply(acceleration, timeStep));
        m_position = Vector.add(m_position, Vector.scalarMultiply(m_velocity , timeStep));
        m_forces = new Vector(0,0); //clear forces

        //angular
        float angAcc = m_torque / m_inertia;
        m_angularVelocity += angAcc * timeStep;
        m_angle += m_angularVelocity * timeStep;
        m_torque = 0; //clear torque
    }

    public void Draw(Canvas graphics)
    {
        //store transform, (like opengl's glPushMatrix())
        Matrix mat1 = graphics.getMatrix();

        //transform into position
        graphics.translate(m_position.X, m_position.Y);
        graphics.rotate(m_angle/(float)Math.PI * 180.0f);

        try
        {
        	Paint paint = new Paint();
        	paint.setStyle(Paint.Style.FILL);
        	paint.setStrokeWidth(1);
        	paint.setColor(m_color);
            //draw body
            graphics.drawRect(rect, paint);

            //draw line in the "forward direction"
            paint.setColor(Color.YELLOW);
            graphics.drawLine(1, 0, 1, 5, paint);
        }
        catch(Exception exc)
        {
            //physics overflow :(
        }  

        //restore transform
        graphics.setMatrix(mat1);
    }

    //take a relative vector and make it a world vector
    public Vector RelativeToWorld(Vector relative)
    {
        Matrix mat = new Matrix();
        float[] vectors = new float[2];

        vectors[0] = relative.X;
        vectors[1] = relative.Y;

        mat.postRotate(m_angle / (float)Math.PI * 180.0f);
        mat.mapVectors(vectors);

        return new Vector(vectors[0], vectors[1]);
    }

    //take a world vector and make it a relative vector
    public Vector WorldToRelative(Vector world)
    {
        Matrix mat = new Matrix();
        float[] vectors = new float[2];

        vectors[0] = world.X;
        vectors[1] = world.Y;

        mat.postRotate(-m_angle / (float)Math.PI * 180.0f);
        mat.mapVectors(vectors);

        return new Vector(vectors[0], vectors[1]);
    }

    //velocity of a point on body
    public Vector PointVel(Vector worldOffset)
    {
        Vector tangent = new Vector(-worldOffset.Y, worldOffset.X);
        return Vector.add( Vector.scalarMultiply(tangent, m_angularVelocity) , m_velocity);
    }

    public void AddForce(Vector worldForce, Vector worldOffset)
    {
        //add linar force
        m_forces = Vector.add(m_forces ,worldForce);
        //and it's associated torque
        m_torque += Vector.cross(worldOffset, worldForce);
    }
    
    
}
