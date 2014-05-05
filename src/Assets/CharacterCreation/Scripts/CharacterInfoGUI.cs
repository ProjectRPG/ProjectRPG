using UnityEngine;
using System.Collections;

namespace CharacterCreation
{
    public class CharacterInfoGUI : MonoBehaviour
    {
        static internal string RaceName = string.Empty;

        static internal string RaceDescription = string.Empty;

        static internal string RaceStats = string.Empty;

        void Start()
        {

        }

        void Update()
        {

        }

        public void OnGUI()
        {
            var infoBox = new Rect();
            infoBox.x = Screen.width - 500;
            infoBox.y = 20;
            infoBox.width = 400;
            infoBox.height = 100;

            GUI.backgroundColor = Color.red;

            GUI.Box(infoBox, RaceName);

            if (GUI.Button(new Rect(Screen.width - 150, Screen.height - 100, 100, 50), "Next"))
            {
                var thing2 = new Vector3(0f, 90f, 0f);
                Camera.main.transform.Rotate(thing2);
            };

            if (GUI.Button(new Rect(150, Screen.height - 100, 100, 50), "Previous"))
            {
                var thing2 = new Vector3(0f, -90f, 0f);
                Camera.main.transform.Rotate(thing2);
            };
        }
    }
}