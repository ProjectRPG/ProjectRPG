using UnityEngine;
using System.Collections;

public class SolarSystem : MonoBehaviour
{
    public float Speed = 50;

    public void Update()
    {
        transform.Rotate(Vector3.up * Time.deltaTime * Speed);
    }
}
