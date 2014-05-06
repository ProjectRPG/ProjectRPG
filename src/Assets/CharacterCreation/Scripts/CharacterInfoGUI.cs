using UnityEngine;
using System.Collections;

namespace CharacterCreation
{
    public class CharacterInfoGUI : MonoBehaviour
    {
        static internal string RaceName = string.Empty;

        static internal string RaceDescription = string.Empty;

        static internal string RaceStats = string.Empty;

        private float previousButtonLeft;

        private float nextButtonLeft;

        private float previousButtonTop;

        private float nextButtonTop;

        public float PreviousButtonTopPercent = 25;

        public float PreviousButtonLeftPercent = 25;

        public float NextButtonTopPercent = 25;

        public float NextButtonLeftPercent = 25;

        public float PreviousButtonWidth;

        public float PreviousButtonHeight;

        public float NextButtonWidth;

        public float NextButtonHeight;

        void Start()
        {
            previousButtonTop = Screen.height * (PreviousButtonTopPercent / 100);
            nextButtonTop = Screen.height * (NextButtonTopPercent / 100);
            nextButtonLeft = Screen.height * (NextButtonLeftPercent / 100);
            previousButtonLeft = Screen.height * (PreviousButtonLeftPercent / 100);
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

            if (GUI.Button(new Rect(Screen.width - nextButtonLeft - NextButtonWidth, Screen.height - nextButtonTop, NextButtonWidth, NextButtonHeight), "Next"))
            {
                var thing2 = new Vector3(0f, 90f, 0f);
                Camera.main.transform.Rotate(thing2);
            };

            if (GUI.Button(new Rect(previousButtonLeft, Screen.height - previousButtonTop, PreviousButtonWidth, PreviousButtonHeight), "Previous"))
            {
                var thing2 = new Vector3(0f, -90f, 0f);
                Camera.main.transform.Rotate(thing2);
            };
        }
    }
}