using UnityEngine;
using System.Collections;

[RequireComponent(typeof(Rigidbody))]
public class Planets : MonoBehaviour
{
    public float Spin = 50f;

    public void FixedUpdate()
    {
        var rotateMe = GetComponent<Rigidbody>();
        rotateMe.AddTorque(Vector3.up * Spin);
    }
}
