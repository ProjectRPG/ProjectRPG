using UnityEngine;
using System.Collections;

[RequireComponent(typeof(Rigidbody))]
public class Rotation : MonoBehaviour
{
    private bool canMove = false;

    private int rotationCounter = 0;

    private readonly int screenCenter = Screen.width / 2;

    public float CenterVoidPercent = 25;

    private float centerVoid;

    public float OuterVoidPercent;

    private float outerVoid;

    public double AccelerationFactor = 0.05;

    public int RotationIncrement = 1;

    public bool HasArrow = false;

    public Texture2D ArrowLeftTexture = null;

    public Texture2D ArrowRightTexture = null;

    public float ArrowTopPercent = 0;

    public float ArrowLeftPercent = 0;

    private float arrowTop;

    private float arrowLeft;

    public int ArrowWidth = 10;

    public int ArrowHeigh = 10;

    public void Start()
    {
        centerVoid = Screen.width * (CenterVoidPercent / 100f);
        outerVoid = Screen.width * (OuterVoidPercent / 100f);

        arrowLeft = Screen.width * (ArrowLeftPercent / 100f);
        arrowTop = Screen.height * (ArrowTopPercent / 100f);
    }

    public void OnGUI()
    {
        if (HasArrow)
        {
            GUI.DrawTexture(new Rect(arrowLeft, arrowTop, ArrowWidth, ArrowHeigh), ArrowLeftTexture);
            GUI.DrawTexture(new Rect(Screen.width - arrowLeft - ArrowWidth, arrowTop, ArrowWidth, ArrowHeigh), ArrowRightTexture);
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
            if (currentMousePosition < screenCenter - centerVoid && currentMousePosition > outerVoid)
            {
                rotationCounter -= (int)((screenCenter - centerVoid - currentMousePosition) * AccelerationFactor) + RotationIncrement;
                Vector3 rotate = new Vector3(0, rotationCounter, 0);
                Quaternion rotationDelta = Quaternion.Euler(rotate);
                rigidbody.MoveRotation(rotationDelta);
            }

            if (currentMousePosition > screenCenter + centerVoid && currentMousePosition < Screen.width - outerVoid)
            {
                rotationCounter += (int)(Mathf.Abs((screenCenter + centerVoid - currentMousePosition)) * AccelerationFactor) + RotationIncrement;
                Vector3 rotate = new Vector3(0, rotationCounter, 0);
                Quaternion rotationDelta = Quaternion.Euler(rotate);
                rigidbody.MoveRotation(rotationDelta);
            }
        }
    }
}
