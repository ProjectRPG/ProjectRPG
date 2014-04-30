using UnityEngine;
using System.Collections;

namespace CharacterCreation
{
    public class CharacterInfoGUI : MonoBehaviour
    {
	static internal string RaceName = string.Empty;

        static internal string RaceDescription = string.Empty;

        static internal string RaceStats = string.Empty;

        // Use this for initialization
        void Start()
        {

        }

        // Update is called once per frame
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

            GUI.Box(infoBox, RaceName);
        }
    }
}