using UnityEngine;
using System.Collections;

[RequireComponent(typeof(Rigidbody))]
public class Rotation : MonoBehaviour
{
    private bool canMove = false;

    private int rotationCounter = 0;

    private readonly int screenCenter = Screen.width / 2;

    public int CenterVoid = 200;

    public int OuterVoid = 400;

    public double AccelerationFactor = 0.05;

    public int RotationIncrement = 1;

    public bool HasArrow = false;

    public Texture2D ArrowLeftTexture = null;

    public Texture2D ArrowRightTexture = null;

    public int ArrowTop = 0;

    public int ArrowLeft = 0;

    public int ArrowWidth = 10;

    public int ArrowHeigh = 10;

    public void Start()
    {
    }

    public void OnGUI()
    {
        if (HasArrow)
        {
            GUI.DrawTexture(new Rect(ArrowLeft, ArrowTop, ArrowWidth, ArrowHeigh), ArrowLeftTexture);
            GUI.DrawTexture(new Rect(Screen.width - ArrowLeft - ArrowWidth, ArrowTop, ArrowWidth, ArrowHeigh), ArrowRightTexture);
        }
    }

    public void Update()
    {
        if (Input.GetMouseButtonDown(0))
        {
            canMove = true;
        }

        if (Input.GetMouseButtonUp(0))
        {
            canMove = false;
        }
    }

    public void FixedUpdate()
    {
        int currentMousePosition = (int)Input.mousePosition.x;

        if (canMove)
        {
            if (currentMousePosition < screenCenter - CenterVoid && currentMousePosition > OuterVoid)
            {
                rotationCounter -= (int)((screenCenter - CenterVoid - currentMousePosition) * AccelerationFactor) + RotationIncrement;
                Vector3 rotate = new Vector3(0, rotationCounter, 0);
                Quaternion rotationDelta = Quaternion.Euler(rotate);
                rigidbody.MoveRotation(rotationDelta);
            }

            if (currentMousePosition > screenCenter + CenterVoid && currentMousePosition < Screen.width - OuterVoid)
            {
                rotationCounter += (int)(Mathf.Abs((screenCenter + CenterVoid - currentMousePosition)) * AccelerationFactor) + RotationIncrement;
                Vector3 rotate = new Vector3(0, rotationCounter, 0);
                Quaternion rotationDelta = Quaternion.Euler(rotate);
                rigidbody.MoveRotation(rotationDelta);
            }
        }
    }
}
