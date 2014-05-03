using UnityEngine;
using System.Collections;
using System.Collections.Generic;

namespace CharacterCreation
{
    public class SelectedScript : MonoBehaviour
    {
        private Dictionary<string, Light> selected_lights = new Dictionary<string, Light>();

        private Dictionary<string, ParticleSystem> selected_particles = new Dictionary<string, ParticleSystem>();

        private const string particles1 = "selected_particles1";
        private const string particles2 = "selected_particles2";
        private const string particles3 = "selected_particles3";
        private const string particles4 = "selected_particles4";

        private const string light1 = "selected_light1";
        private const string light2 = "selected_light2";
        private const string light3 = "selected_light3";
        private const string light4 = "selected_light4";

        private string raceName;

        // Use this for initialization
        public void Start()
        {
            CharacterInfoGUI.RaceName = "Select Your Race";

            var lights = GetComponentsInChildren<Light>();
            var particles = GetComponentsInChildren<ParticleSystem>();

            foreach (var particle in particles)
            {
                selected_particles.Add(particle.name, particle);
                particle.enableEmission = false;
            }

            foreach (var light in lights)
            {
                selected_lights.Add(light.name, light);
                light.enabled = false;
            }
        }


        // Update is called once per frame
        public void Update()
        {
            if (Input.GetMouseButtonUp(0))
            {
                var click = Camera.main.ScreenPointToRay(Input.mousePosition);
                var hit = new RaycastHit();
                if (Physics.Raycast(click, out hit, 100))
                {
                    SelectCharacter(hit.collider.name);
                }
            }
        }

        private void SelectCharacter(string colliderName)
        {
            if (colliderName == "collider1")
            {
                selected_lights[light1].enabled = true;
                selected_lights[light2].enabled = false;
                selected_lights[light3].enabled = false;
                selected_lights[light4].enabled = false;

                selected_particles[particles1].enableEmission = true;
                selected_particles[particles2].enableEmission = false;
                selected_particles[particles3].enableEmission = false;
                selected_particles[particles4].enableEmission = false;
                CharacterInfoGUI.RaceName = "Sicarius";
            }
            else if (colliderName == "collider2")
            {
                selected_lights[light1].enabled = false;
                selected_lights[light2].enabled = true;
                selected_lights[light3].enabled = false;
                selected_lights[light4].enabled = false;

                selected_particles[particles1].enableEmission = false;
                selected_particles[particles2].enableEmission = true;
                selected_particles[particles3].enableEmission = false;
                selected_particles[particles4].enableEmission = false;
                CharacterInfoGUI.RaceName = "Opacis";
            }
            else if (colliderName == "collider3")
            {
                selected_lights[light1].enabled = false;
                selected_lights[light2].enabled = false;
                selected_lights[light3].enabled = true;
                selected_lights[light4].enabled = false;

                selected_particles[particles1].enableEmission = false;
                selected_particles[particles2].enableEmission = false;
                selected_particles[particles3].enableEmission = true;
                selected_particles[particles4].enableEmission = false;
                CharacterInfoGUI.RaceName = "Robur";
            }
            else if (colliderName == "collider4")
            {
                selected_lights[light1].enabled = false;
                selected_lights[light2].enabled = false;
                selected_lights[light3].enabled = false;
                selected_lights[light4].enabled = true;

                selected_particles[particles1].enableEmission = false;
                selected_particles[particles2].enableEmission = false;
                selected_particles[particles3].enableEmission = false;
                selected_particles[particles4].enableEmission = true;
                CharacterInfoGUI.RaceName = "Human";
            }
            else
            {
                selected_lights[light1].enabled = false;
                selected_lights[light2].enabled = false;
                selected_lights[light3].enabled = false;
                selected_lights[light4].enabled = false;

                selected_particles[particles1].enableEmission = false;
                selected_particles[particles2].enableEmission = false;
                selected_particles[particles3].enableEmission = false;
                selected_particles[particles4].enableEmission = false;
                CharacterInfoGUI.RaceName = string.Empty;
            }
        }
    }
}