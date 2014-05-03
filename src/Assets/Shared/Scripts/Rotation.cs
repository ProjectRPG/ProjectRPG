using UnityEngine;
using System.Collections;

[RequireComponent(typeof(Rigidbody))]
public class Rotation : MonoBehaviour
{
    private Vector3? prevMousePos = null;

    private bool canMove = false;

    private int rotationCounter = 0;

    private readonly int screenCenter = Screen.width / 2;

    public int CenterVoid = 200;

    public double AccelerationFactor = 0.05;

    public int RotationIncrement = 1;

    // Use this for initialization
    public void Start()
    {
        Debug.Log(AccelerationFactor);
    }

    public void Update()
    {
        if (Input.GetMouseButtonDown(0))
        {
            canMove = true;
            prevMousePos = Input.mousePosition;
        }
        if (Input.GetMouseButtonUp(0))
        {
            canMove = false;
        }
    }

    public void FixedUpdate()
    {
        int currMousePos = (int)Input.mousePosition.x;
        if (canMove)
        {
            if (currMousePos < screenCenter - CenterVoid)
            {
                rotationCounter -= (int)((screenCenter - CenterVoid - currMousePos) * AccelerationFactor) + RotationIncrement;
                Vector3 rotate = new Vector3(0, rotationCounter, 0);
                Quaternion rotationDelta = Quaternion.Euler(rotate);
                rigidbody.MoveRotation(rotationDelta);
            }

            if (currMousePos > screenCenter + CenterVoid)
            {
                rotationCounter += (int)((currMousePos - screenCenter + CenterVoid) * AccelerationFactor) + RotationIncrement;
                Vector3 rotate = new Vector3(0, rotationCounter, 0);
                Quaternion rotationDelta = Quaternion.Euler(rotate);
                rigidbody.MoveRotation(rotationDelta);
            }
        }
    }
}
