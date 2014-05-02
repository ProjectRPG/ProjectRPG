using UnityEngine;
using System.Collections;

[RequireComponent(typeof(Rigidbody))]
public class Rotation : MonoBehaviour
{
    private Vector3? prevMousePos = null;

    private bool canMove = false;

    private int rotationCounter = 0;

    private string headerText = "Select Your Rebel";

    // Use this for initialization
    public void Start()
    {

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
        if (canMove)
        {
            if (Input.mousePosition.x > Screen.width / 2)
            {
                rotationCounter -= 1;
                Vector3 rotate = new Vector3(0, rotationCounter, 0);
                Quaternion rotationDelta = Quaternion.Euler(rotate);
                rigidbody.MoveRotation(rotationDelta);
            }

            if (Input.mousePosition.x < Screen.width / 2)
            {
                rotationCounter += 1;
                Vector3 rotate = new Vector3(0, rotationCounter, 0);
                Quaternion rotationDelta = Quaternion.Euler(rotate);
                rigidbody.MoveRotation(rotationDelta);
            }
        }
    }
}
